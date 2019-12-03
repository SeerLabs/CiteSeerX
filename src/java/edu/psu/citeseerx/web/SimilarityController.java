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
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.DomainTransformer;
import edu.psu.citeseerx.domain.ExternalLink;
import edu.psu.citeseerx.domain.Hub;
import edu.psu.citeseerx.domain.RepositoryService;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.UniqueAuthor;
import edu.psu.citeseerx.repository.RepositoryUtilities;
import edu.psu.citeseerx.webutils.RedirectUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides model objects to document similarity view.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class SimilarityController implements Controller {

    private CSXDAO csxdao;

    private RepositoryService repositoryService;

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
     
    
    private String solrSelectUrl;
    
    public void setSolrSelectUrl(String solrSelectUrl) 
    throws MalformedURLException
	{
	try {
            new URI("http",solrSelectUrl, null).toURL();
            this.solrSelectUrl = solrSelectUrl;
        }catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    } //- setSolrSelectUrl
    
    
    private int maxQueryTerms = 20;
    
    public void setMaxQueryTerms(int maxQueryTerms) {
        this.maxQueryTerms = maxQueryTerms;
    } //- setMaxQueryTerms
    
    
    private static final String SIM_AB  = "ab";
    private static final String SIM_CC = "cc";
    private static final String SIM_SC = "sc";

    private static final Set<String> simTypes = new HashSet<String>();

    static {
        simTypes.add(SIM_AB);
        simTypes.add(SIM_CC);
        simTypes.add(SIM_SC);
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            JSONException, SQLException, URISyntaxException {
        
        String doi = null;
        String errorTitle = "Document Not Found";
        String cid = request.getParameter("cid");
        Long cluster;
        Map<String, Object> model = new HashMap<String, Object>();
        
        if (cid != null) {
        	try {
        		cluster = Long.parseLong(cid);
        	}catch (NumberFormatException e) {
        		e.printStackTrace();
        		model.put("pagetitle", errorTitle);
        		return new ModelAndView("viewDocError", model);
			}
            List<String> dois = citedao.getPaperIDs(cluster);
            doi = dois.get(0);
            RedirectUtils.sendDocumentCIDRedirect(request, response, doi);
            return null;
        }
        
        if (doi == null) {
            doi = request.getParameter("doi");
        }
        
        if (doi == null) {
        	model.put("pagetitle", errorTitle);
    		return new ModelAndView("viewDocError", model);
        }
        
        
        String xml = request.getParameter("xml");
        boolean bxml = false;
        try {
            bxml = Boolean.parseBoolean(xml);
        } catch (Exception e) {}

        String src = request.getParameter("src");
        boolean bsrc = false;
        try {
            if (bxml) {
                bsrc = Boolean.parseBoolean(src);
            }
        } catch (Exception e) {}

        String sysData = request.getParameter("sysData");
        boolean bsysData = false;
        try {
            bsysData = Boolean.parseBoolean(sysData);
        } catch (Exception e) {}

        Document doc = null;
        try {
            doc = csxdao.getDocumentFromDB(doi, false, bsrc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (doc == null || doc.isPublic() == false) {
        	model.put("doi", doi);
        	model.put("pagetitle", errorTitle);
            return new ModelAndView("baddoi", model);
        }

        List<UniqueAuthor> uauthors = new ArrayList<UniqueAuthor>();
        String authors = "";
        
        int c = 1;
        for (Author a : doc.getAuthors()) {
            String authorName = a.getDatum(Author.NAME_KEY);
            authors += authorName + ", ";
            
            // convert to unique authors
            UniqueAuthor uauth = new UniqueAuthor();
            uauth.setCanname(authorName);
            if (a.getClusterID() > 0) {                        
                uauth.setAid("");
            }
            uauthors.add(uauth);
            c++;
        }
        if (authors.length() == 0) {
            authors = "Unknown Authors";
        }else{
            // There is always a final comma.
            authors = authors.substring(0, authors.lastIndexOf(","));
        }
        
        String title = doc.getDatum(Document.TITLE_KEY);
        String abs =  doc.getDatum(Document.ABSTRACT_KEY);
        String venue = doc.getDatum(Document.VENUE_KEY);
        String year = doc.getDatum(Document.YEAR_KEY);

        DocumentFileInfo finfo = doc.getFileInfo();
        String rep = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);
        List<String> urls = getClusterURLs(doc.getClusterID());

        Long clusterID = doc.getClusterID();
        
        List<ExternalLink> eLinks = csxdao.getExternalLinks(doi);
        
        // Obtain the hubUrls that points to this document.
        List<Hub> hubUrls = csxdao.getHubs(doi);
        
        model.put("pagetype", "similar");
        model.put("pagetitle", "Similarity Options: "+title);
        model.put("pagekeywords", authors);
        model.put("pagedescription", "Document Details (Isaac Councill, " +
        		"Lee Giles): " + abs);
        model.put("title", title); 
        model.put("authors", authors);
        model.put("uauthors", uauthors);
        model.put("abstractText", abs);
        model.put("venue", venue);
        model.put("year", year);
        model.put("urls", urls);
        model.put("doi", doi);
        model.put("clusterid", clusterID);
        model.put("rep", rep);
        model.put("ncites", doc.getNcites());
        model.put("selfCites", doc.getSelfCites());
        model.put("elinks", eLinks);
        model.put("fileTypes", RepositoryUtilities.getFileTypes(repositoryService, doi, rep));
        model.put("hubUrls", hubUrls);

        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }
        
        String type = request.getParameter("type");
        if (type == null || !simTypes.contains(type)) {
            return new ModelAndView("docSimilarity", model);
            //default page
        }

        List<ThinDoc> citations = new ArrayList<ThinDoc>();
        if (type.equals(SIM_AB)) {
            citations = doABQuery(clusterID);
            model.put("pagetitle",
                    "Active Bibliography: "+title);
            model.put("citations", citations);
            return new ModelAndView("activeBib", model);
        }
        if (type.equals(SIM_CC)) {
            citations = doCCQuery(clusterID);
            model.put("pagetitle",
                    "Related by Co-Citation: "+title);
            model.put("citations", citations);
            return new ModelAndView("coCite", model);
        }
        if (type.equals(SIM_SC)) {
            citations = doSCQuery(clusterID);
            model.put("pagetitle",
                    "Related by cluster: "+title);
            model.put("citations", citations);
            return new ModelAndView("clustered", model);
        }
        return new ModelAndView("viewDoc", model);

    }  //- handleRequest
    
    
    private List<ThinDoc> doABQuery(Long clusterid) throws SQLException,
    SolrException, JSONException, IOException, URISyntaxException {

        List<Long> cited = citedao.getCitedClusters(clusterid, maxQueryTerms);
        if (cited.isEmpty()) return new ArrayList<ThinDoc>();
       
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
            "http", solrSelectUrl, null);

	String updatedSolrSelectUrl = uri.toURL().toString();
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(updatedSolrSelectUrl);
        urlBuffer.append("?qt=citesim&q=cites:(");
        for (Iterator<Long> it = cited.iterator(); it.hasNext(); ) {
            urlBuffer.append(it.next());
            if (it.hasNext()) {
                urlBuffer.append("+OR+");
            }
        }
        urlBuffer.append(")");
        
        JSONObject output =
            SolrSelectUtils.doJSONQuery(urlBuffer.toString());

        //JSONObject responseObj = output.getJSONObject("response");
        //JSONObject respHeaderObj =
        //    output.getJSONObject("responseHeader");

        List<ThinDoc> hits =
            SolrSelectUtils.buildHitListJSON(output, clusterid);
        
        return hits;
        
    }  //- doABQuery

    
    private List<ThinDoc> doCCQuery(Long clusterid) throws SQLException,
    SolrException, JSONException, IOException, URISyntaxException {

        List<Long> citing = citedao.getCitingClusters(clusterid, maxQueryTerms);
        if (citing.isEmpty()) return new ArrayList<ThinDoc>();
      
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
            "http", solrSelectUrl, null);

        String updatedSolrSelectUrl = uri.toURL().toString();

        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(updatedSolrSelectUrl);
        urlBuffer.append("?qt=citesim&q=citedby:(");
        for (Iterator<Long> it = citing.iterator(); it.hasNext(); ) {
            urlBuffer.append(it.next());
            if (it.hasNext()) {
                urlBuffer.append("+OR+");
            }
        }

        urlBuffer.append(")");
        JSONObject output =
            SolrSelectUtils.doJSONQuery(urlBuffer.toString());

        /*JSONObject responseObj = output.getJSONObject("response");
        JSONObject respHeaderObj =
            output.getJSONObject("responseHeader");*/

        List<ThinDoc> hits =
            SolrSelectUtils.buildHitListJSON(output, clusterid);
        
        return hits;

    }  //- doCCQuery
    
    private List<String> getClusterURLs(Long clusterID) {
        List<String> dois = citedao.getPaperIDs(clusterID);
        List<String> urls = new ArrayList<String>();
        if (!dois.isEmpty()) {
            for (String doi : dois) {
                Document doc = csxdao.getDocumentFromDB(doi);
                if (doc.isPublic()) {
                    DocumentFileInfo finfo = doc.getFileInfo();
                    urls.addAll(finfo.getUrls());
                }
            }
        }
        return urls;
    } //- getClusterURLs

    /**
     * @param clusterID
     * @return Returns all the papers within the given cluster.
     */
    private List<ThinDoc> doSCQuery(Long clusterID) {
        
        List<ThinDoc> papers = new ArrayList<ThinDoc>();
        
        List<String> dois = citedao.getPaperIDs(clusterID);
        if (dois.isEmpty()) {return papers;}
        
        for (String doi : dois) {
            Document doc = csxdao.getDocumentFromDB(doi);
            if (null != doc && doc.isPublic()) {
                papers.add(DomainTransformer.toThinDoc(doc));
            }
        }
        
        return papers;
        
    } //- doSCQuery
    
}  //- class SimilarityController
