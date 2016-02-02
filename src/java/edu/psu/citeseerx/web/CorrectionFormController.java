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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.*;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.ExternalLink;
import edu.psu.citeseerx.domain.Hub;
import edu.psu.citeseerx.domain.RepositoryService;
import edu.psu.citeseerx.domain.UniqueAuthor;
import edu.psu.citeseerx.myciteseer.web.utils.FoulWordFilter;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.repository.RepositoryUtilities;
import edu.psu.citeseerx.updates.UpdateManager;
import edu.psu.citeseerx.web.domain.AuthorContainer;
import edu.psu.citeseerx.web.domain.DocumentContainer;


/**
 * Controller used to handle user corrections to papers
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class CorrectionFormController extends SimpleFormController {

    private CSXDAO csxdao;
    private RepositoryService repositoryService;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    

    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
    
    
    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    
    private UpdateManager updateManager;
    
    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    } //- setUpdateManager
    
    
    private FoulWordFilter foulWordFilter;
    
    public void setFoulWordFilter(FoulWordFilter foulWordFilter) {
        this.foulWordFilter = foulWordFilter;
    } //-setFoulWordFilter
    
    
    public CorrectionFormController() {
        setValidateOnBinding(false);
        setCommandName("correction");
        setFormView("correct");
    } //CorrectionFormController
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    protected Object formBackingObject(HttpServletRequest request)
    throws Exception {

        String doi = request.getParameter("doi");
        String cid = request.getParameter("cid");
        
        if (doi == null && cid != null) {
            try {
                Long cluster = Long.parseLong(cid);
                List<String> dois = citedao.getPaperIDs(cluster);
                doi = dois.get(0);
            } catch (Exception e) { };
        }
        DocumentContainer dc = new DocumentContainer();
        if (doi != null) {
            Document doc = csxdao.getDocumentFromDB(doi);
            if (doc == null || !(doc.isPublic() || doc.isPDFRedirect())) { // Allow Redirected documents to be edited (Policy)
                // the model for this view is set on mapReferenceData
                setFormView("baddoi");
            }else{
                setFormView("correct");
                try {
                    dc = DocumentContainer.fromDocument(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dc;
        
    }  //- formBackingObject

    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
     */
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        
        String foul;
        DocumentContainer dc = (DocumentContainer)command;
        
        if (dc.getPaperID() == null) {
            errors.reject("INVALID_DOI", "No DOI specified");
        } else {
            Document doc = csxdao.getDocumentFromDB(dc.getPaperID());
            if (doc == null) {
                errors.reject("INVALID_DOI", "Invalid DOI specified");
            }
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title",
                "TITLE_REQUIRED", "Title is required.");
        if ((foul = foulWordFilter.findFoulWord(dc.getTitle())) != null) {
            errors.rejectValue("title", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }
        
        List<AuthorContainer> authors = dc.getAuthors();
        if (!dc.hasAuthors()) {
            errors.reject("AUTHOR_REQUIRED", "At least one author is required");
        }
        for (int i=0; i<authors.size(); i++) {
            AuthorContainer ac = (AuthorContainer)authors.get(i);
            if (!ac.getDeleted()) {
                String authPrefix =  "authors["+i+"].";
                ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                        authPrefix+"name", "AUTHOR_NAME_REQUIRED",
                        "Author name is required.");
                if ((foul = foulWordFilter.findFoulWord(ac.getName()))
                        != null) {
                    errors.rejectValue(authPrefix+"name", "FOUL_WORD",
                            "Flagged as innappropriate content.");
                    foul = null;
                }
                if ((foul = foulWordFilter.findFoulWord(ac.getAffil()))
                        != null) {
                    errors.rejectValue(authPrefix+"affil", "FOUL_WORD",
                            "Flagged as innappropriate content.");
                    foul = null;
                }
                if ((foul = foulWordFilter.findFoulWord(ac.getAddress()))
                        != null) {
                    errors.rejectValue(authPrefix+"address", "FOUL_WORD",
                            "Flagged as innappropriate content.");
                    foul = null;
                }
            }
        }
        if ((foul = foulWordFilter.findFoulWord(dc.getAbs()))
                != null) {
            errors.rejectValue("abs", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }        
        if ((foul = foulWordFilter.findFoulWord(dc.getVenue()))
                != null) {
            errors.rejectValue("venue", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }        
        if ((foul = foulWordFilter.findFoulWord(dc.getPublisher()))
                != null) {
            errors.rejectValue("publisher", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }        
        if ((foul = foulWordFilter.findFoulWord(dc.getPubAddr()))
                != null) {
            errors.rejectValue("pubAddr", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }        
        if ((foul = foulWordFilter.findFoulWord(dc.getTech()))
                != null) {
            errors.rejectValue("tech", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }
        try {
            if (dc.getYear() != null && !dc.getYear().equals(""))
                Integer.parseInt(dc.getYear());
        } catch (NumberFormatException e) {
            errors.rejectValue("year", "INT_REQUIRED", "Integer required.");
        }
        try {
            if (dc.getVol() != null && !dc.getVol().equals(""))
                Integer.parseInt(dc.getVol());
        } catch (NumberFormatException e) {
            errors.rejectValue("vol", "INT_REQUIRED", "Integer required.");
        }
        try {
            if (dc.getNum() != null && !dc.getNum().equals(""))
                Integer.parseInt(dc.getNum());
        } catch (NumberFormatException e) {
            errors.rejectValue("num", "INT_REQUIRED", "Integer required.");
        }
        
    }  //- onBindAndValidate
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
     */
    protected Map<String, Object> referenceData(HttpServletRequest request) 
    throws Exception {
        
        String doi = null;
        String cid = request.getParameter("cid");
        
        if (cid != null) {
            try {
                Long cluster = Long.parseLong(cid);
                List<String> dois = citedao.getPaperIDs(cluster);
                doi = dois.get(0);
            } catch (Exception e) { };
        }
        
        if (doi == null) {
            doi = request.getParameter("doi");
        }
        
        if (doi == null) {
            return new HashMap<String, Object>();
        }

        Document doc = null;
        try {
            doc = csxdao.getDocumentFromDB(doi, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        if (doc == null || !doc.isPublic()) {
            // The view has been changed on formBackingObject
            model.put("doi", doi);
            model.put("error", new Boolean(true));
            model.put("errMsg", "Invalid DOI specified");
            return model;
        }
        
        MCSConfiguration config = myciteseer.getConfiguration();
        model.put("correctionsEnabled",
                new Boolean(config.getCorrectionsEnabled()));
        model.put("pagetype", "");

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
        
        List<ExternalLink> eLinks = csxdao.getExternalLinks(doi);
        
        // Obtain the hubUrls that points to this document.
        List<Hub> hubUrls = csxdao.getHubs(doi);

        model.put("pagetitle", "Correct: "+title);
        model.put("pagedescription", "Document Details (Isaac Councill, " +
                "Lee Giles): " + abs);
        model.put("pagekeywords", authors);
        model.put("title", title);            
        model.put("authors", authors);
        model.put("uauthors", uauthors);
        model.put("abstractText", abs);
        model.put("venue", venue);
        model.put("year", year);
        model.put("urls", urls);
        model.put("doi", doi);
        model.put("rep", rep);
        model.put("ncites", doc.getNcites());
        model.put("selfCites", doc.getSelfCites());
        model.put("fileTypes", RepositoryUtilities.getFileTypes(repositoryService, doi, rep));
        model.put("elinks", eLinks);
        model.put("hubUrls", hubUrls);
        
        return model;
        
    }  //- referenceData
    
    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        
        MCSConfiguration config = myciteseer.getConfiguration();
        if (!config.getCorrectionsEnabled()) {
            return super.onSubmit(request, response, command, errors);
        }

        onBindAndValidate(request, command, errors);
        if (errors.hasErrors()) {
            return showForm(request, response, errors);
        }
        
        DocumentContainer container = (DocumentContainer)command;
        Document doc = csxdao.getDocumentFromDB(
                container.getPaperID(),
                false,  // don't get citation contexts
                true);  // retrieve provenance info
                
        if (doc == null) {
            // TODO add error page
            System.out.println("NO DOC!!!");
            return super.onSubmit(request, response, command, errors);            
        }

        String userid = MCSUtils.getLoginAccount().getUsername();
        if (userid == null) {
            // TODO add error page
            System.out.println("NO USERID!!!");
            return super.onSubmit(request, response, command, errors);
        }
        
        container.toDocument(doc, "user correction");
        
        updateManager.doCorrection(doc, userid);

        ModelAndView mav = onSubmit(command, errors);
        Map<String, Object> refData = referenceData(request, command, errors);
        mav.getModel().putAll(refData);
        return mav;
        
    }  //- onSubmit
        
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
}  //- class CorrectionFormController
