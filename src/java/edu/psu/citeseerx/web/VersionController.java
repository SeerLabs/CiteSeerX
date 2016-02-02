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
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.AuthorVersion;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.repository.RepositoryUtilities;
import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.utility.CSXConstants;

/**
 * Provides model objects to version view
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class VersionController implements Controller {

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

    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        String doi = null;
        String cid = request.getParameter("cid");
        String errorTitle = "Document Not Found";
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

        String sysData = request.getParameter("sysData");
        boolean bsysData = false;
        try {
            bsysData = Boolean.parseBoolean(sysData);
        } catch (Exception e) {}

        Document doc = null;
        
        try {
            doc = csxdao.getDocumentFromDB(doi, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (doc == null || doc.isPublic() == false) {
        	model.put("doi", doi);
        	model.put("pagetitle", errorTitle);
            return new ModelAndView("baddoi", model);
        }
        
        int currentVersion = doc.getVersion();
        int version = currentVersion; 
        String versionStr = request.getParameter("version");
        if (versionStr != null) {
            try {
                version = Integer.parseInt(versionStr);
            } catch (Exception e) { }
        }
        
        Document versionDoc = doc;
        Boolean error = false;
        String errMsg = null; 
        if (version != currentVersion) {
            try {
                versionDoc = csxdao.getDocVersion(doi, version);
                if (versionDoc == null) {
                    error = true;
                    errMsg="No version matches the supplied query parameters.";
                }
            } catch (IOException e) {
                error = true;
                errMsg="Version info is currently unavailable.";
            }
        }
        
        if (bxml) {
            response.getWriter().print(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            Account account = MCSUtils.getLoginAccount();
            if (bsysData && account != null && account.isAdmin()) {
                response.getWriter().print(versionDoc.toXML(true));
            } else {
                response.getWriter().print(versionDoc.toXML(false));                
            }
            return null;
            //return new ModelAndView("xml", model);
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

        String userid = null;
        if (doc.getVersionName() != null &&
                doc.getVersionName().equals(CSXConstants.USER_VERSION)) {
            userid = csxdao.getCorrector(doi, version);
        }
        
        DocumentFileInfo finfo = doc.getFileInfo();
        String rep = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);
        List<String> urls = getClusterURLs(doc.getClusterID());

        Long clusterID = doc.getClusterID();

        List<ExternalLink> eLinks = csxdao.getExternalLinks(doi);
        
        // Obtain the hubUrls that points to this document.
        List<Hub> hubUrls = csxdao.getHubs(doi);
        
        model.put("error", error);
        model.put("errMsg", errMsg);
        model.put("pagetype", "versions");
        model.put("pagetitle", "Version "+version+": "+title);
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
        model.put("hubUrls", hubUrls);
        model.put("fileTypes", RepositoryUtilities.getFileTypes(repositoryService, doi, rep));
        model.put("maxversion", new Integer(currentVersion));
        model.put("thisversion", new Integer(version));
        model.put("user", userid);
        if (versionDoc != null) {
            addVersionData(versionDoc, model);
        }
        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }
        
        return new ModelAndView("versions", model);
    }
    
    
    private static void addVersionData(Document doc, 
            Map<String, Object> model) {
        
        String title = doc.getDatum(Document.TITLE_KEY);
        String abs = doc.getDatum(Document.ABSTRACT_KEY);
        String year = doc.getDatum(Document.YEAR_KEY);
        String venue = doc.getDatum(Document.VENUE_KEY);
        String ventype = doc.getDatum(Document.VEN_TYPE_KEY);
        String pages = doc.getDatum(Document.PAGES_KEY);
        String vol = doc.getDatum(Document.VOL_KEY);
        String num = doc.getDatum(Document.NUM_KEY);
        String tech = doc.getDatum(Document.TECH_KEY);

        String title_src = doc.getSource(Document.TITLE_KEY);
        String abs_src = doc.getSource(Document.ABSTRACT_KEY);
        String year_src = doc.getSource(Document.YEAR_KEY);
        String venue_src = doc.getSource(Document.VENUE_KEY);
        String ventype_src = doc.getSource(Document.VEN_TYPE_KEY);
        String pages_src = doc.getSource(Document.PAGES_KEY);
        String vol_src = doc.getSource(Document.VOL_KEY);
        String num_src = doc.getSource(Document.NUM_KEY);
        String tech_src = doc.getSource(Document.TECH_KEY);

        List<Citation> citations = doc.getCitations();
        String citesv = Integer.toString(citations.size()) + " found";
        String cites_src = doc.getSource(Document.CITES_KEY);
        
        ArrayList<AuthorVersion> authors = new ArrayList<AuthorVersion>();
        for (Author auth : doc.getAuthors()) {
            AuthorVersion authv = new AuthorVersion();
            authv.setName(auth.getDatum(Author.NAME_KEY));
            authv.setAddr(auth.getDatum(Author.ADDR_KEY));
            authv.setAffil(auth.getDatum(Author.AFFIL_KEY));
            authv.setNameSrc(auth.getSource(Author.NAME_KEY));
            authv.setAddrSrc(auth.getSource(Author.ADDR_KEY));
            authv.setAffilSrc(auth.getSource(Author.AFFIL_KEY));
            authors.add(authv);
        }
        
        if (title != null) {
            model.put("titlev", title);
            model.put("title_src", title_src);
        }
        if (abs != null) {
            model.put("absv", abs);
            model.put("abs_src", abs_src);
        }
        if (year != null) {
            model.put("yearv", year);
            model.put("year_src", year_src);
        }
        if (venue != null) {
            model.put("venuev", venue);
            model.put("venue_src", venue_src);
        }
        if (ventype != null) {
            model.put("ventypev", ventype);
            model.put("ventype_src", ventype_src);
        }
        if (pages != null) {
            model.put("pagesv", pages);
            model.put("pages_src", pages_src);
        }
        if (vol != null) {
            model.put("volv", vol);
            model.put("vol_src", vol_src);
        }
        if (num != null) {
            model.put("numv", num);
            model.put("num_src", num_src);
        }
        if (tech != null) {
            model.put("techv", tech);
            model.put("tech_src", tech_src);
        }
        model.put("citesv", citesv);
        model.put("cites_src", cites_src);
        model.put("authv", authors);
        
    }  //- addVersionData
    
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
}  //- class VersionController
