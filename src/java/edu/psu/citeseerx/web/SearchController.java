/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.web;

import edu.psu.citeseerx.utility.DateUtils;
import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Algorithm;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.BiblioTransformer;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.Table;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.UniqueAuthor;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Process user searches by obtaining search parameters and creating the
 * search URL to search the Solr index. Obtains hits from Solr and converts them
 * into CiteSeerX domain objects to be send to the view. Produces also the feed
 * links (ATOM, and RSS)
 * @author Isaac Councill
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class SearchController implements Controller {

    // URL to the Solr instance handling the Document queries
    private String solrSelectUrl;

    public void setSolrSelectUrl(String solrSelectUrl)
    throws MalformedURLException {
        try {
            new URI("http",solrSelectUrl, null).toURL();
            this.solrSelectUrl = solrSelectUrl;
        }catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    } //- setSolrSelectUrl

    // URL to the Solr instance handling the table queries
    private String solrTableSelectUrl;

    public void setSolrTableSelectUrl(String solrTableSelectUrl)
    throws MalformedURLException {
        try {
            new URI("http", solrTableSelectUrl, null).toURL();
            this.solrTableSelectUrl = solrTableSelectUrl;
        }catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    } //- setSolrTableSelectUrl

    // URL to the Solr instance handling the disambiguated author queries
    private String solrAuthorSelectUrl;
    public void setSolrAuthorSelectUrl(String solrAuthorSelectUrl)
    throws MalformedURLException {
        try {
            new URI("http", solrAuthorSelectUrl, null).toURL();
            this.solrAuthorSelectUrl = solrAuthorSelectUrl;
        }catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    } //- setSolrAuthorSelectUrl

    // URL to the Solr instance handling the algorithm query
    private String solrAlgSelectUrl;

    public void setSolrAlgSelectUrl(String solrAlgSelectUrl)
    throws MalformedURLException {
        try {
            new URI("http", solrAlgSelectUrl, null).toURL();
            this.solrAlgSelectUrl = solrAlgSelectUrl;
        }catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    } //- setSolrAlgSelectUrl

    private CSXDAO csxdao;

    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO


    private int nrows = 10; // Max number of records to be return in a query

    public void setNrows(int nrows) {
        this.nrows = nrows;
    } //- setNrows


    private int feedSize = 100;

    public void setFeedSize(int feedSize) {
        this.feedSize = feedSize;
    } //- setFeedSize

    // Maximum number of records to be retrieved in sequential calls
    private int maxresults = 500;

    public void setMaxResults(int maxresults) {
        this.maxresults = maxresults;
    } //- setMaxResults

    private String systemBaseURL;

    /**
     * @param systemBaseURL Base URL for the system
     */
    public void setSystemBaseURL(String systemBaseURL) {
        this.systemBaseURL = systemBaseURL;
    } //- setSystemBaseURL

    private final String QUERY_PARAMETER = "q";
    private final String QUERY_TYPE = "t";

    // CiteSeerx query types
    private final String DOCUMENT_QUERY = "doc";
    private final String AUTHOR_QUERY = "auth";
    private final String TABLE_QUERY = "table";
    private final String ALGORITHM_QUERY = "algorithm";

    private final String UAUTH = "uauth";
    private final String UAUTHSET = "1";

    private final String FEED = "feed";
    private final String SORT = "sort";
    private final String START = "start";
    private final String INCLUDE_CITATIONS = "ic";

    // Name of the sort options handled by CiteSeerX
    private static final String SORT_RLV  = "rlv";
    private static final String SORT_CITE = "cite";
    private static final String SORT_DATE = "date";
    private static final String SORT_ASCDATE = "ascdate";
    private static final String SORT_TIME = "recent";

    // sort by ndocs, for unique authors search
    private static final String SORT_DOC = "ndocs";

    // Solr query handlers
    private final String STANDARD_QUERY = "standard";
    private final String DISMAX_QUERY = "dismax";

    private static final HashMap<String,String> sortTypes =
        new HashMap<String,String>();

    static {
        /*
         * Note from Solr: If no sort is specified, the default is score desc
         * to return the matches having the highest relevancy.
         * That's why SORT_RLV is "" here.
         */
        sortTypes.put(SORT_RLV, "");
        sortTypes.put(SORT_CITE, "sort=ncites+desc");
        sortTypes.put(SORT_DATE, "sort=year+desc");
        sortTypes.put(SORT_ASCDATE, "sort=year+asc");
        sortTypes.put(SORT_TIME, "sort=vtime+desc");
        sortTypes.put(SORT_DOC, "sort=ndocs+desc");
    }

    private static final String RSS = "rss";
    private static final String ATOM = "atom";

    private static final HashSet<String> feedTypes = new HashSet<String>();

    static {
        feedTypes.add(RSS);
        feedTypes.add(ATOM);
    }

    /*
     * Obtain all the query parameters from the user request and stores then
     * in a common place.
     * All parameters will have a valid value to be used by the other methods.
     */
    private Map<String, Object> collectQueryParam(
            HttpServletRequest request) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();

        String value = null;
        queryParameters.put(QUERY_PARAMETER,
                ServletRequestUtils.getStringParameter(request,
                        QUERY_PARAMETER, null));
        queryParameters.put(QUERY_TYPE,
                ServletRequestUtils.getStringParameter(request, QUERY_TYPE,
                        DOCUMENT_QUERY));
        queryParameters.put(UAUTH,
                ServletRequestUtils.getStringParameter(request, UAUTH, "0"));
        value = ServletRequestUtils.getStringParameter(request, FEED, null);
        if (!feedTypes.contains(value)) {value = null;}
        queryParameters.put(FEED, value);
        value = ServletRequestUtils.getStringParameter(request, SORT, SORT_RLV);
        if (!sortTypes.containsKey(value)) {
            value = SORT_RLV;
        }
        queryParameters.put(SORT, value);

        Integer start = null;
        try {
            start = new Integer(request.getParameter("start"));
        } catch (Exception e) {
            /*
             * For initial search there is no start. If we send it away
             * no results will be ever displayed. If there is no parameter or it
             * cannot be converted to an integer, just assume start = 0.
             */
            start = 0;
        }
        queryParameters.put(START, start);
        queryParameters.put(INCLUDE_CITATIONS,
                ServletRequestUtils.getBooleanParameter(request,
                        INCLUDE_CITATIONS, false));
        return queryParameters;
    } //- collectQueryParam

    /*
     * builds the Solr query based on the parameters send by the user.
     */
    private String buildSolrQuery(String queryType,
            Map<String, Object> queryParameters) {

        String query = normalizeQuery(
                (String)queryParameters.get(QUERY_PARAMETER));

        if (queryType.equals(DOCUMENT_QUERY)) {

            // Searching for authors within documents
            if (queryParameters.get(QUERY_TYPE).equals(AUTHOR_QUERY) &&
                    !queryParameters.get(UAUTH).equals(UAUTHSET)) {
                query = "authorNorms:("+query+")";
            }
        }

        // First, we add all the common parameters
        StringBuffer queryString = new StringBuffer("?q="+query);
        String sort = (String)queryParameters.get(SORT);

        if (!sort.equals(SORT_RLV)) {
            // Relevance is the default for Solr that's why we ignored it
            queryString.append("&");
            queryString.append(sortTypes.get(sort));
        }

        queryString.append("&rows=");
        if (queryParameters.get(FEED) == null) {
            queryString.append(nrows);
        }else{
            queryString.append(feedSize);
        }
        queryString.append("&start="+queryParameters.get(START));
        // Added fix for disambiguated authors
        if (query.indexOf(':') > 0 || queryParameters.get(UAUTH).equals(UAUTHSET)) {
            queryString.append("&qt="+STANDARD_QUERY);
        }else{
            queryString.append("&qt="+DISMAX_QUERY);
        }

        // Standard addons
        queryString.append("&hl=true&wt=json");

        // Now, we add specific parameters to each type of search
        if (queryType.equals(DOCUMENT_QUERY)) {
            if ((Boolean)queryParameters.get(INCLUDE_CITATIONS) == false) {
                queryString.append("&fq=incol:true");
            }
        }
        return queryString.toString();
    } //- buildSolrQuery

    /*
     * Executes a query towards a Solr intance unsing the supplied parameters
     */
    private JSONObject executeSolrQuery(String solrSelectUrl,
            String queryString)
    throws SolrException, URISyntaxException, JSONException,
    MalformedURLException, IOException {
        JSONObject output = null;
        URI uri = null;

        uri = new URI(
                /*
                 *  Need to modify the configuration file for this
                 *  (no need for http: but it needs the //).
                 *  We use the 3 parameter constructor since
                 *  solrSelectUrl already contains: the host, port and
                 *  path. We just need to append the query and pass no
                 *  fragment
                 */
            "http", solrSelectUrl + queryString.toString(), null);
        output =
            SolrSelectUtils.doJSONQuery(uri.toURL().toString());
        return output;
    } //- executeSolrQuery

    /*
     * Fills the model with all the links needed to be shown to the user.
     * NOTE: DO NOT use URLEncoder.encode when the query has ?, or &. There
     * are encoded and that's not what we want for the links that are meant
     * to be follow by the user. Use it only to encode the text before
     * appending it.
     */
    private Map<String, Object> fillModelWithLinks(Map<String, Object>model,
            Map<String, Object>queryParameters, String queryType) {

        String rlvQuery, citeQuery, dateQuery, ascDateQuery, timeQuery;
        String rssUrl, atomUrl;

        StringBuffer nextPageParams = new StringBuffer();
        try {
            nextPageParams.append("q=" + URLEncoder.encode(
                    (String)queryParameters.get(QUERY_PARAMETER), "UTF-8"));
        }catch (UnsupportedEncodingException e) {
            nextPageParams.append("q=" + queryParameters.get(QUERY_PARAMETER));
        }
        if ((Boolean)queryParameters.get(INCLUDE_CITATIONS)) {
            nextPageParams.append("&ic=1");
        }

        nextPageParams.append("&t=" + queryParameters.get(QUERY_TYPE));
        if (queryType.equals(AUTHOR_QUERY)) {
            // It's a disambiguated author query
            nextPageParams.append("&uauth=1");
        }

        // Create Links for different sort options
        rlvQuery = citeQuery = dateQuery = ascDateQuery = timeQuery =
            nextPageParams.toString();
        citeQuery += "&sort="+SORT_CITE;
        dateQuery += "&sort="+SORT_DATE;
        ascDateQuery += "&sort="+SORT_ASCDATE;
        timeQuery += "&sort="+SORT_TIME;
        nextPageParams.append("&sort=" + queryParameters.get(SORT));

        // create links for feeds.
        rssUrl = atomUrl = nextPageParams.toString();
        rssUrl += "&feed=" + RSS;
        atomUrl += "&feed=" + ATOM;

        Integer newStart = (Integer)queryParameters.get(START) + nrows;
        nextPageParams.append("&start=" + newStart);

        model.put("rlvq", rlvQuery);
        model.put("citeq", citeQuery);
        model.put("dateq", dateQuery);
        model.put("ascdateq", ascDateQuery);
        model.put("timeq", timeQuery);
        model.put("rss", rssUrl);
        model.put("atom", atomUrl);
        if (newStart < (Integer)model.get("resultsize") &&
                !(Boolean)model.get("error")) {
            model.put("nextpageparams", nextPageParams.toString());
        }
        return model;
    } //- fillModelWithLinks

    /*
     * Fills the model object with all the common attributes to all query types
     */
    private Map<String, Object> fillModel(Map<String, Object>model,
            Map<String, Object>queryParameters, String queryType) {

        String pageTitle = "";
        String pageDescription = "";
        String q = (String)queryParameters.get(QUERY_PARAMETER);

        if (queryType.equals(DOCUMENT_QUERY)) {
            pageTitle = "Search Results";
            pageDescription = "Scientific articles matching the query: ";
        }else if (queryType.equals(AUTHOR_QUERY)) {
                pageTitle = "Author search results";
                pageDescription = "Authors matching the query: ";
        }
        else if(queryType.equals(ALGORITHM_QUERY)) {
                pageTitle = "Algorithm search results";
                pageDescription = "Algorithms matching the query: ";
        }
        else if(queryType.equals(TABLE_QUERY)) {
                pageTitle = "Table search results";
                pageDescription = "Tables matching the query: ";
        }

        pageTitle += " &mdash; " + q;
        pageDescription += q;

        model.put("pagetitle", pageTitle);
        model.put("pagedescription", pageDescription);
        model.put("pagekeywords", q);
        model.put("query", q);
        model.put("start", queryParameters.get(START));
        model.put("nrows", nrows);
        model.put("sorttype", queryParameters.get(SORT));
        model.put("dblpparams", "author="+q);

        fillModelWithLinks(model, queryParameters, queryType);

        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }

        return model;
    } //- fillModel

    /*
     * Performs a query to Solr when the user is using the document search. It
     * obtains the result from Solr and sends the results back to the user.
     */
    private ModelAndView doGeneralSearch(Map<String, Object> queryParameters) {

        Map<String, Object> model = new HashMap<String, Object>();
        Boolean error = false;
        String errMsg = "";
        Integer numFound = 0;
        List<ThinDoc> hits = new ArrayList<ThinDoc>();
        List<String> coins = new ArrayList<String>();

        Integer start = (Integer)queryParameters.get(START);
        if (start >= maxresults) {
            error = true;
            errMsg = "Only the top " + maxresults + " hits are available.";
            errMsg = errMsg+" Please try a more specific query";
        }else{

            String solrQuery = buildSolrQuery(DOCUMENT_QUERY, queryParameters);

            try {
                JSONObject output = executeSolrQuery(solrSelectUrl, solrQuery);
                JSONObject responseObject = output.getJSONObject("response");
                numFound = responseObject.getInt("numFound");
                hits = SolrSelectUtils.buildHitListJSON(output);

                // Obtain COinS representation for hits.
                String url = (systemBaseURL.endsWith("/") ? systemBaseURL :
                    systemBaseURL + "/") + "viewdoc/summary";
                coins = BiblioTransformer.toCOinS(hits, url);

            } catch (SolrException e) {
                error = true;
                int code = e.getStatusCode();
                if (code == 400) {
                    errMsg = "Invalid query type.  " +
                            "Please check your syntax.";
                } else {
                    errMsg = "<p>Error processing query.</p>" +
                            "<p>The most likely cause of this condition " +
                            "is a malformed query. Please check your query  " +
                            "syntax and, if the problem persists, " +
                            "contact an admin for assistance.</p>";
                }
                System.err.println("Query: " + solrQuery);
                e.printStackTrace();

            }catch (Exception e) {
                /*
                 *  A problem retrieving the results happen.
                 */
                error = true;
                errMsg = "<p>Error obtaining the query results.</p>" +
                    "Try your query again and, if the problem persists, " +
                    "contact an admin for assistance.</p>";
                System.err.println("Query: " + solrQuery);
                e.printStackTrace();
            }
        }

        model.put("error", error);
        model.put("errorMsg", errMsg);
        model.put("resultsize", numFound);

        fillModel(model, queryParameters, DOCUMENT_QUERY);

        model.put("hits", (!error) ? hits : new ArrayList<ThinDoc>());
        model.put("coins", (!error) ? coins : new ArrayList<String>());

        String feed = (String)queryParameters.get(FEED);
        if (feed != null) {
            if (feed.equals(RSS)) {
                return rssView(model);
            }
            if (feed.equals(ATOM)) {
                return atomView(model);
            }
        }

        return new ModelAndView("searchDocs", model);
    } //- doGeneralSearch

    /*
     * Performs a query to Solr when the user is using the table search. It
     * obtains the result from Solr and sends the results back to the user.
     */
    private ModelAndView doTableSearch(Map<String, Object> queryParameters) {

        Boolean error = false;
        String errMsg = "";
        Integer numFound = 0;
        Map<String, Object> model = new HashMap<String, Object>();
        List<Table> hits = new ArrayList<Table>();

        Integer start = (Integer)queryParameters.get(START);
        if (start >= maxresults) {
            error = true;
            errMsg = "Only the top " + maxresults + " hits are available.";
            errMsg = errMsg+" Please try a more specific query";
        }else{
            String solrQuery = buildSolrQuery(TABLE_QUERY, queryParameters);
            try {
                JSONObject output =
                    executeSolrQuery(solrTableSelectUrl, solrQuery);
                JSONObject responseObject = output.getJSONObject("response");
                numFound = responseObject.getInt("numFound");
                hits = SolrSelectUtils.buildTableHitListJSON(output);

                // Obtain content for tables.
                int page = 1;
                Table tAux = null;
                for (Table table : hits) {
                    page = 1;
                    tAux = csxdao.getTable(table.getID());
                    if (tAux != null) {
                        table.setContent(tAux.getContent());
                        page = tAux.getTableOccursInPage();
                    }
                    table.setTableOccursInPage(page);
                    Document doc =
                        csxdao.getDocumentFromDB(table.getPaperIDForTable());
                    if (doc != null && doc.isPublic()) {
                        String authors = "";
                        int c = 1;
                        for (Iterator<Author> it = doc.getAuthors().iterator();
                        it.hasNext(); ) {
                            authors += it.next().getDatum(Author.NAME_KEY);
                            if (it.hasNext()) {
                                authors += ", ";
                            }
                            c++;
                        }

                        table.setPaperAuthors(authors);
                        table.setPaperTitle(doc.getDatum("title"));
                    }else{
                        table.setPaperAuthors("unknown authors");
                        table.setPaperTitle("unknown title");
                    }
                    // Do we really need this?
                    String docUrl = "viewdoc/summary?doi=" + table.getPaperIDForTable();
                    table.setDocURL(docUrl);
                }

            } catch (SolrException e) {
                error = true;
                int code = e.getStatusCode();
                if (code == 400) {
                    errMsg = "Invalid query type.  " +
                            "Please check your syntax.";
                } else {
                    errMsg = "<p>Error processing query.</p>" +
                            "<p>The most likely cause of this condition " +
                            "is a malformed query. Please check your query  " +
                            "syntax and, if the problem persists, " +
                            "contact an admin for assistance.</p>";
                }
                System.err.println("Query: " + solrQuery);
                e.printStackTrace();
            }catch (Exception e) {
                /*
                 *  A problem retrieving the results happen.
                 */
                error = true;
                errMsg = "<p>Error obtaining the query results.</p>" +
                    "Try your query again and, if the problem persists, " +
                    "contact an admin for assistance.</p>";
                System.err.println("Query: " + solrQuery);
                e.printStackTrace();
            }
        }

        model.put("error", error);
        model.put("errorMsg", errMsg);
        model.put("resultsize", numFound);

        fillModel(model, queryParameters, TABLE_QUERY);

        model.put("hits", (!error) ? hits : new ArrayList<Table>());

        return new ModelAndView("searchTables", model);
    } //- doTableSearch

    /*
     * Performs a query to Solr when the user is using the Author disambiguated
     * search. It obtains the result from Solr and sends the results back to
     * the user.
     */
    private ModelAndView doAuthorSearch(Map<String, Object> queryParameters) {

        Boolean error = false;
        String errMsg = "";
        Integer numFound = 0;
        Map<String, Object> model = new HashMap<String, Object>();
        List<UniqueAuthor> hits = new ArrayList<UniqueAuthor>();

        Integer start = (Integer)queryParameters.get(START);
        if (start >= maxresults) {
            error = true;
            errMsg = "Only the top " + maxresults + " hits are available.";
            errMsg = errMsg+" Please try a more specific query";
        }else{
            String solrQuery = buildSolrQuery(AUTHOR_QUERY, queryParameters);
            URI uri = null;
            try {
                //PUCK
                uri = new URI("http", solrAuthorSelectUrl + solrQuery.toString(), null);

                JSONObject output =
                    executeSolrQuery(solrAuthorSelectUrl, solrQuery);
                JSONObject responseObject = output.getJSONObject("response");
                numFound = responseObject.getInt("numFound");
                hits = AuthorSolrSelectUtils.buildHitListJSON(output);

                UniqueAuthor u2 = null;
                for (UniqueAuthor u : hits) {
                    u2 = csxdao.getAuthor(u.getAid());
                    if (u2 != null) {
                        u.setUrl(u2.getUrl());
                    }
                }
            } catch (SolrException e) {
                error = true;
                int code = e.getStatusCode();
                if (code == 400) {
                    errMsg = "Invalid query type.  " +
                            "Please check your syntax.";
                } else {
                    errMsg = "<p>Error processing query.</p>" +
                            "<p>The most likely cause of this condition " +
                            "is a malformed query. Please check your query  " +
                            "syntax and, if the problem persists, " +
                            "contact an admin for assistance.</p>";
                }
                System.err.println("Query: " + solrQuery);
                e.printStackTrace();
            } catch (Exception e) {
                /*
                 *  A problem retrieving the results happen.
                 */
                Writer traces = new StringWriter();
                PrintWriter printWriter = new PrintWriter(traces);
                e.printStackTrace(printWriter);

                error = true;
                errMsg = "<p>AUTHSEARCH: Error obtaining the query results.</p>" +
                    "Try your query again and, if the problem persists, " +
                    "contact an admin for assistance.</p>" +
                    solrQuery + "<p><p><br><br><br>" +
                    "[" + uri + "]<br><br><br>" +
                    traces.toString();
                System.err.println("Query: " + solrQuery);
                e.printStackTrace();
            }
        }

        model.put("error", error);
        model.put("errorMsg", errMsg);
        model.put("resultsize", numFound);

        fillModel(model, queryParameters, AUTHOR_QUERY);

        model.put("uauthors", (!error) ? hits : new ArrayList<UniqueAuthor>());

        return new ModelAndView("searchAuths", model);
    } //- doAuthorSearch

    /*
     * Performs a query to Solr when the user is using the algorithm search.
     * It obtains the result from Solr and sends the results back to
     * the user.
     */
    private ModelAndView doAlgorithmSearch(Map<String, Object> queryParameters) {
        Boolean error = false;
        String errMsg = "";
        Integer numFound = 0;
        Map<String, Object> model = new HashMap<String, Object>();
        List<Algorithm> hits = new ArrayList<Algorithm>();

        Integer start = (Integer)queryParameters.get(START);
        if (start >= maxresults) {
            error = true;
            errMsg = "Only the top " + maxresults + " hits are available.";
            errMsg = errMsg+" Please try a more specific query";
        }else{
            String solrQuery = buildSolrQuery(ALGORITHM_QUERY, queryParameters);
            try {
                JSONObject output =
                    executeSolrQuery(solrAlgSelectUrl, solrQuery);
                JSONObject responseObject = output.getJSONObject("response");
                numFound = responseObject.getInt("numFound");
                hits = SolrSelectUtils.buildAlgorithmHitListJSON(output);

                // Obtain content for tables.
                Algorithm aAux = null;
                for (Algorithm algorithm : hits) {
                    aAux = csxdao.getAlgorithm(algorithm.getID());
                    if (aAux != null) {
                        algorithm.setSynopsis(aAux.getSynopsis());
                        algorithm.setAlgorithmOccursInPage(
                                aAux.getAlgorithmOccursInPage());
                        algorithm.setPaperYear(aAux.getPaperYear());
                        algorithm.setAlgorithmReference(
                                aAux.getAlgorithmReference());
                    }

                    Document doc = csxdao.getDocumentFromDB(
                            algorithm.getPaperIDForAlgorithm());
                    if (doc != null && doc.isPublic()) {
                        String authors = "";
                        int c = 1;

                        for (Iterator<Author> it = doc.getAuthors().iterator();
                        it.hasNext(); ) {
                            authors += it.next().getDatum(Author.NAME_KEY);
                            if (it.hasNext()) {
                                authors += ", ";
                            }
                            c++;
                        }

                        algorithm.setAuthors(authors);
                        algorithm.setTitle(doc.getDatum("title"));
                    }else{
                        algorithm.setAuthors("unknown authors");
                        algorithm.setTitle("unknown title");
                    }

                    // do we really need this? why?
                    String fileUrl = "viewdoc/summary?doi=" +
                        algorithm.getPaperIDForAlgorithm();
                    algorithm.setDocURL(fileUrl);
                }

            } catch (SolrException e) {
                error = true;
                int code = e.getStatusCode();
                if (code == 400) {
                    errMsg = "Invalid query type.  " +
                            "Please check your syntax.";
                } else {
                    errMsg = "<p>Error processing query.</p>" +
                            "<p>The most likely cause of this condition " +
                            "is a malformed query. Please check your query  " +
                            "syntax and, if the problem persists, " +
                            "contact an admin for assistance.</p>";
                }
                System.err.println("Query: " + solrQuery);
                e.printStackTrace();
            }catch (Exception e) {
                /*
                 *  A problem retrieving the results happen.
                 */
                error = true;
                errMsg = "<p>Error obtaining the query results.</p>" +
                    "Try your query again and, if the problem persists, " +
                    "contact an admin for assistance.</p>";
                System.err.println("Query: " + solrQuery);
                e.printStackTrace();
            }
        }

        model.put("error", error);
        model.put("errorMsg", errMsg);
        model.put("resultsize", numFound);

        fillModel(model, queryParameters, ALGORITHM_QUERY);

        model.put("hits", (!error) ? hits : new ArrayList<Table>());

        return new ModelAndView("searchAlgorithms", model);
    } //- doAuthorSearch

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException, JSONException {

        ModelAndView modelView = null;

        // Collect the query parameters with reasonable values.
        Map<String, Object> queryParameters = collectQueryParam(request);

        // Check if the parameter (currently q exists)
        if(queryParameters.get(QUERY_PARAMETER) == null ||
                ((String)queryParameters.get(QUERY_PARAMETER)).isEmpty()) {
            RedirectUtils.sendRedirect(request, response, "/");
            return modelView;
        }

        String t = (String)queryParameters.get(QUERY_TYPE);

        if (!t.equals(DOCUMENT_QUERY)) {
            // It's not the default query type
            if (t.equals(AUTHOR_QUERY)) {
                // User is searching for authors.
                if(queryParameters.get(UAUTH).equals(UAUTHSET)) {
                    // Search using the Unique Authors index
                    modelView = doAuthorSearch(queryParameters);
                }
                //else{
                    // Search for authors within document index
                //    modelView = doGeneralSearch(queryParameters);
                //}
            }
            else if(t.equals(TABLE_QUERY)) {
                // Searching using the table index
                modelView = doTableSearch(queryParameters);
            }
            else if( t.equals(ALGORITHM_QUERY)) {
                // Searching using the algorithms index
                modelView = doAlgorithmSearch(queryParameters);
            }
        }else{
            modelView = doGeneralSearch(queryParameters);
        }

        return modelView;

    }  //- handleRequest

    private static String normalizeQuery(String q) {
        q = q.replaceAll("author\\:", "authorNorms:");
        return q;
    } //- normalizeQuery


    private ModelAndView rssView(Map<String, Object> model) {
        String feedTitle = "Documents: "
            + (String)model.get("query");
        String feedLink = (String)model.get("rss");
        String feedDate =
            DateUtils.formatRFC822(new Date(System.currentTimeMillis()));
        String feedDesc = "document results for the query: "
            + (String)model.get("query");

        feedLink = systemBaseURL + "/search?" + feedLink;

        model.put("feedTitle", feedTitle);
        model.put("feedLink", feedLink);
        model.put("feedDate", feedDate);
        model.put("feedDesc", feedDesc);
        model.put("baseUrl", systemBaseURL);

        return new ModelAndView("feeds/rss", model);
    } //- rssView


    private ModelAndView atomView(Map<String, Object> model) {

        String feedTitle = "Documents: "
            + (String)model.get("query");
        String feedLink = (String)model.get("atom");
        String feedDate =
            DateUtils.formatRFC3339(new Date(System.currentTimeMillis()));

        feedLink = systemBaseURL + "/search?" + feedLink;

        model.put("feedTitle", feedTitle);
        model.put("feedLink", feedLink);
        model.put("feedDate", feedDate);
        model.put("baseUrl", systemBaseURL);

        return new ModelAndView("feeds/atom", model);
    } //- atomView

}  //- class SearchController
