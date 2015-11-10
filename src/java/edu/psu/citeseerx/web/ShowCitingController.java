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

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.BiblioTransformer;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.ExternalLink;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.utility.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
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
import java.util.Set;

/**
 * Handle citations views
 * @author Isaac Councill 
 * Version: $Rev$ $Date$
 */
public class ShowCitingController implements Controller {

    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    

    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- citedao


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
    

    private int nrows = 10;
    
    public void setNrows(int nrows) {
        this.nrows = nrows;
    } //- setNrows
    
    
    private int feedSize = 100;
    
    public void setFeedSize(int feedSize) {
        this.feedSize = feedSize;
    } //- setFeedSize
    
    private String systemBaseURL;
    
    /**
	 * @param systemBaseURL Base URL for the system
	 */
	public void setSystemBaseURL(String systemBaseURL) {
		this.systemBaseURL = systemBaseURL;
	} //- setSystemBaseURL

    
    private static final String SORT_CITE = "cite";
    private static final String SORT_DATE = "date";
    private static final String SORT_ASCDATE = "ascdate";
    private static final String SORT_TIME = "recent";

    private static final Map<String,String> sortTypes =
        new HashMap<String,String>();

    static {
        sortTypes.put(SORT_CITE, "sort=ncites+desc");
        sortTypes.put(SORT_DATE, "sort=year+desc");
        sortTypes.put(SORT_ASCDATE, "sort=year+asc");
        sortTypes.put(SORT_TIME, "sort=vtime+desc");
    }
    
    
    private static final String RSS = "rss";
    private static final String ATOM = "atom";
    
    private static final Set<String> feedTypes = new HashSet<String>();
    
    static {
        feedTypes.add(RSS);
        feedTypes.add(ATOM);
    }

    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException, JSONException {

        String doi = request.getParameter("doi");
        Long cluster = null;
        Document doc = null;
        String errorTitle = "Document may have moved";
        if (doi != null) {
            doc = csxdao.getDocumentFromDB(doi, false, false);
            if (doc != null) cluster = doc.getClusterID();
        }

        HashMap<String, Object> model = new HashMap<String, Object>();
        if (cluster == null) {
            String cid = request.getParameter("cid");

            try {
                cluster = Long.parseLong(cid);
            } catch (Exception e) {
        	model.put("pagetitle", errorTitle);
        	return new ModelAndView("viewDocError", model);
            }
        }
        
        
        String feed = request.getParameter("feed");
        if (!feedTypes.contains(feed)) {
            feed = null;
        }

        
        String sort = request.getParameter("sort");
        if (sort == null || !sortTypes.containsKey(sort)) {
            sort = SORT_CITE;
        }
        
        int start = 0;
        String startStr = request.getParameter("start");
        try {
            start = Integer.parseInt(startStr);
        } catch (Exception e) { }
        
                
        String title = null;
        String authors = new String();
        String venue = null;
        Integer year = null;
        Integer ncites = null;
        Boolean inCollection = false;
        
        if (doc != null) {
            title = doc.getDatum(Document.TITLE_KEY);
            for (Iterator<Author> it = doc.getAuthors().iterator(); 
            it.hasNext(); ) {
                authors += it.next().getDatum(Author.NAME_KEY);
                if (it.hasNext()) {
                    authors += ", ";
                }
                venue = doc.getDatum(Document.VENUE_KEY);
                try {
                    year = new Integer(doc.getDatum(Document.YEAR_KEY));
                } catch (Exception e) { }
                ncites = doc.getNcites();
                inCollection = true;
            }
        } else {
            ThinDoc thinDoc = citedao.getThinDoc(cluster);
            if (thinDoc == null) {
        		model.put("pagetitle", errorTitle);
        		return new ModelAndView("viewDocError", model);
            }
            title = thinDoc.getTitle();
            authors = thinDoc.getAuthors();
            if (authors != null) {
                authors = authors.replace(",", ", ");
            }
            venue = thinDoc.getVenue();
            try {
                year = new Integer(thinDoc.getYear());
            } catch (Exception e) { }
            try {
                ncites = new Integer(thinDoc.getNcites());
            } catch (Exception e) { }
            inCollection = thinDoc.getInCollection();
        }
        
        boolean error = false;
        String errMsg = null;

        List<ThinDoc> citings = new ArrayList<ThinDoc>();
        List<String>  coinsCit = null;
        List<String> citationContexts = new ArrayList<String>();
        Integer numFound = new Integer(0);
        StringBuffer urlBuffer = new StringBuffer();
        try {
            urlBuffer.append("?q=");
            urlBuffer.append(URLEncoder.encode("cites:", "UTF-8"));
            urlBuffer.append(cluster);
            urlBuffer.append("&");
            urlBuffer.append(sortTypes.get(sort));
            urlBuffer.append("&start=");
            urlBuffer.append(start);
            
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
                "http", solrSelectUrl , null);
           
	    System.err.println("[II] Query: "+ uri.toURL().toString() + urlBuffer.toString());
            JSONObject output =
                SolrSelectUtils.doJSONQuery(uri.toURL().toString() + 
                        urlBuffer.toString());

            JSONObject responseObj = output.getJSONObject("response");
            //JSONObject respHeaderObj = output.getJSONObject("responseHeader");
            
            numFound = responseObj.getInt("numFound");

            citings = SolrSelectUtils.buildHitListJSON(output);
            
            // Obtains the COins info for each element in citings.
            String url = request.getRequestURL().toString();
            url = url.replace("showciting", "viewdoc/summary");
            coinsCit = BiblioTransformer.toCOinS(citings, url);
            
            // Get citation contexts
            for (ThinDoc citing: citings){
                String context = citedao.getContext(citing.getCluster(), cluster);
                if (context != null){
                    citationContexts.add(context);
                } else{
                    citationContexts.add("");
                }
            }

        } catch (SolrException e) {
            error = true;
            int code = e.getStatusCode();
            if (code == 400) {
                errMsg = "Invalid query type.  " +
                        "Please check your syntax.";
            } else {
                errMsg = "<p><span class=\"char_emphasized\">" +
                        "Error processing query.</span></p><br>" +
                        "<p>The most likely cause of this condition " +
                        "is a malformed query. Please check your query  " +
                        "syntax and, if the problem persists, " +
                        "contact an admin for assistance.</p>";
            }

            e.printStackTrace();
        }catch (Exception e) {
            /*
             *  A problem retrieving the results happen.
             */
            errMsg = "<p><span class=\"char_emphasized\">" +
                "Error obtaining the query results.</span></p>" +
                "Try your query again and, if the problem persists, " +
                "contact an admin for assistance.</p>";
            System.err.println("Query: " + urlBuffer.toString()); 
            System.err.println(e.getMessage());
        }

        StringBuilder nextPageParams = new StringBuilder();
        if (doi != null) {
            nextPageParams.append("doi="+URLEncoder.encode(doi, "UTF-8"));
        } else {
            nextPageParams.append("cid="+cluster);            
        }
        
        String citeQuery, dateQuery, ascDateQuery, timeQuery;
        String rssUrl, atomUrl;
        
        citeQuery = dateQuery = ascDateQuery = timeQuery =
            nextPageParams.toString();
        citeQuery += "&amp;sort="+SORT_CITE;
        dateQuery += "&amp;sort="+SORT_DATE;
        ascDateQuery += "&amp;sort="+SORT_ASCDATE;
        timeQuery += "&amp;sort="+SORT_TIME;
        
        if (sort != null) {
            nextPageParams.append("&amp;sort=");
            nextPageParams.append(URLEncoder.encode(sort, "UTF-8"));
        }

        rssUrl = atomUrl = nextPageParams.toString();
        rssUrl += "&amp;feed="+RSS;
        atomUrl += "&amp;feed="+ATOM;

        nextPageParams.append("&amp;start="+(start+nrows));

        // build title, description, and keywords meta tags content
        String pageTitle, pageDescription, pageKeywords;
        
        pageTitle = "Citation Query " + 
        	((title != null && title.length() > 0) ? title : "Unknown Title");
        pageKeywords = "citations, " + 
        	((title != null && title.length() > 0) ? title : "") + ", " +
        	((authors != null && authors.length() > 0) ? authors : "");
        pageDescription = "Scientific documents that cite the following " + 
        	"paper: " + title;
        	
        List<ExternalLink> eLinks = csxdao.getExternalLinks(doi);
        
        model.put("pagetitle", pageTitle);
        model.put("pagekeywords", pageKeywords);
        model.put("pagedescription", pageDescription);
        model.put("cid", cluster);
        model.put("title", title);
        model.put("authors", authors);
        model.put("venue", venue);
        model.put("year", year);
        model.put("ncites", numFound);
        model.put("sorttype", sort);
        model.put("citeq", citeQuery);
        model.put("dateq", dateQuery);
        model.put("ascdateq", ascDateQuery);
        model.put("timeq", timeQuery);
        model.put("rss", rssUrl);
        model.put("atom", atomUrl);
        model.put("inCollection", inCollection);
        model.put("start", new Integer(start));
        model.put("nrows", new Integer(nrows));
        if (start+nrows < numFound && !error) {
            model.put("nextpageparams", nextPageParams.toString());
        }
        model.put("hits", citings);
        model.put("coins", coinsCit);
        model.put("citationContexts", citationContexts);
        model.put("elinks", eLinks);
        model.put("error", error);
        model.put("errorMsg", errMsg);
        
        if (feed != null) {
            if (feed.equals(RSS)) {
                return rssView(request, model);
            }
            if (feed.equals(ATOM)) {
                return atomView(request, model);
            }
        }

        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }
        
        return new ModelAndView("showCiting", model);
        
    }  //- ModelAndView
    
    
    private ModelAndView rssView(HttpServletRequest request, Map<String, Object> model) {
        String feedTitle = "Citation Search: "
            + (String)model.get("title");
        String feedLink = (String)model.get("rss");
        String feedDate =
            DateUtils.formatRFC822(new Date(System.currentTimeMillis()));
        String feedDesc = "Documents that cite the paper: "
            + (String)model.get("title");
        
        feedLink = systemBaseURL + "/showciting?" + feedLink;
        
        model.put("feedTitle", feedTitle);
        model.put("feedLink", feedLink);
        model.put("feedDate", feedDate);
        model.put("feedDesc", feedDesc);
        model.put("baseUrl", systemBaseURL);
        
        return new ModelAndView("feeds/rss", model);
    } //- rssView
    
    
    private ModelAndView atomView(HttpServletRequest request, Map<String, Object> model) {

        String feedTitle = "Citation Search: "
            + (String)model.get("title");
        String feedLink = (String)model.get("atom");
        String feedDate =
            DateUtils.formatRFC3339(new Date(System.currentTimeMillis()));

        feedLink = systemBaseURL + "/showciting?" + feedLink;

        model.put("feedTitle", feedTitle);
        model.put("feedLink", feedLink);
        model.put("feedDate", feedDate);
        model.put("baseUrl", systemBaseURL);

        return new ModelAndView("feeds/atom", model);
    } //- atomView
    
    
}  //- class showCitingController
