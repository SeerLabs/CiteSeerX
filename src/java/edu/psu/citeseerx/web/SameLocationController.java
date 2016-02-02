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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

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

/**
 * Provides model objects to documents from same location view
 * @author JuanPablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class SameLocationController implements Controller {
    
    private CSXDAO csxdao;
    private RepositoryService repositoryService;
    
    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
    
    private int nrows = 10;
    
    public void setNrows(int nrows) {
        this.nrows = nrows;
    } //- setNrows

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        
        String doi = null;
        String hUrl = null;
        hUrl = request.getParameter("hurl");
        String cid = request.getParameter("cid");
        
        String errorTitle = "Document not found";
        Map<String, Object> model = new HashMap<String, Object>();
        
        if (cid != null) {
            Long cluster;
            try {
                cluster = Long.parseLong(cid);
            }catch (NumberFormatException e) {
                e.printStackTrace();
                model.put("pagetitle", errorTitle);
                return new ModelAndView("viewDocError", model);
            }
            List<String> dois = citedao.getPaperIDs(cluster);
            if(!dois.isEmpty()) {
                doi = dois.get(0);
                RedirectUtils.sendDocumentCIDRedirect(request, response, doi);
                return null;
            }
            else {
                model.put("pagetitle", errorTitle);
                return new ModelAndView("viewDocError", model);
            }
        }
        
        if (doi == null) {
            doi = request.getParameter("doi");
        }
        
        if (doi == null) {
            model.put("pagetitle", errorTitle);
            return new ModelAndView("viewDocError", model);
        }
        
        errorTitle = "Incorrect Parameter";
        if (hUrl == null) {
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

        /*String sysData = request.getParameter("sysData");
        boolean bsysData = false;
        try {
            bsysData = Boolean.parseBoolean(sysData);
        } catch (Exception e) {}*/

        Document doc = null;
        try {
            doc = csxdao.getDocumentFromDB(doi, false, bsrc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (doc == null) {
            model.put("doi", doi);
            model.put("pagetitle", errorTitle);
            return new ModelAndView("baddoi", model);
        }

        ArrayList<UniqueAuthor> uauthors = new ArrayList<UniqueAuthor>();
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
        
        // Obtain the paper ids associated to the hubURL.
        Integer start = 0;
        try {
            start = new Integer(request.getParameter("start"));
        } catch (Exception e) { }
        
        List<String> paperIDs = csxdao.getPaperIdsFromHubUrl(hUrl);
        List<ThinDoc> hits = null;
        StringBuffer nextPageParams = new StringBuffer();
        if (paperIDs != null) {
            // Obtain the hits to show.
            hits = getHits(paperIDs, start, doi);
      
            // Calculate the next page param.
            if (start+nrows < paperIDs.size() ) {
                nextPageParams.append("hurl=");
                nextPageParams.append(URLEncoder.encode(hUrl, "UTF-8"));
                nextPageParams.append("&doi=");
                nextPageParams.append(doi);
                nextPageParams.append("&start=");
                nextPageParams.append(start+nrows);
                model.put("nextpageparams", nextPageParams.toString());
            }
        }
        
        model.put("pagetype", "similar");
        model.put("pagetitle", "Documents from same URL: "+title);
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
        model.put("hurl", hUrl);
        model.put("hits", hits);

        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }
        return new ModelAndView("sameLocation", model);
    } //- handleRequest

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
    
    private List<ThinDoc> getHits(List<String> paperIDs, Integer start, 
            String toExclude) {
        List<ThinDoc> hits = new ArrayList<ThinDoc>();
        int skipCounter = 1;
        int toInclude = 0;
  
        // We need to skip start documents which are public.
        for (String paperID : paperIDs) {
            // We don't want the paper the user is viewing in the result
            if (toExclude.equals(paperID)) {continue;}
            Document doc = csxdao.getDocumentFromDB(paperID);
            if (doc.isPublic()) {
                if (skipCounter > start) {
                    if (toInclude < nrows) {
                        ThinDoc hit = DomainTransformer.toThinDoc(doc);
                        SolrSelectUtils.prepCitation(hit);
                        hits.add(hit);
                        toInclude++;
                    }else{
                        // There is no more items to include. Exit the loop!
                        break;
                    }
                }else{
                    skipCounter++;
                }
            }
        }
        return hits;
    } //- getHits
    
} //- class SameLocationController
