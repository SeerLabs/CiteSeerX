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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DomainTransformer;
import edu.psu.citeseerx.domain.ThinDoc;

/**
 * List the last document additions to the corpus
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class NewDocController implements Controller {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private int nrows = 10;
    
    public void setNrows(int nrows) {
        this.nrows = nrows;
    } //- setNrows
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String lastDOI = request.getParameter("last");
        lastDOI = (lastDOI == null || lastDOI.trim().length() == 0) ? "Z" :
            lastDOI;
        
        List<String> dois = csxdao.getLastDocuments(lastDOI, nrows);

        List<ThinDoc> hits = new ArrayList<ThinDoc>(); 
        for (String doi : dois) {
            Document doc = csxdao.getDocumentFromDB(doi, false, true);
            if (doc.isPublic()) {
                ThinDoc tDoc = DomainTransformer.toThinDoc(doc);
                // We do know that all documents here are in the corpus
                tDoc.setInCollection(true);
                tDoc.setAbstract(doc.getDatum(Document.ABSTRACT_KEY));
                if ( tDoc.getAbstract() != null &&
                        tDoc.getAbstract().length() > 0) {
                    int limit = Math.min(tDoc.getAbstract().length(), 200);
                    tDoc.setSnippet(
                            doc.getDatum(Document.ABSTRACT_KEY).substring(0, limit));

                }
                hits.add(tDoc);
            }
        }

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("hits", hits);
        
        StringBuffer nextPageParams = null;
        if (hits.size() > 0) {
            nextPageParams = new StringBuffer();
            nextPageParams.append("last=");
            nextPageParams.append(hits.get(hits.size()-1).getDoi());
            model.put("nextPageParams", nextPageParams);
            model.put("nfound", hits.size());
        }

        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }
        
        // Generate title, description and keywords meta tag values.
        String pageTitle, pageDescription, pageKeywords;
        
        pageTitle = pageDescription = pageKeywords = 
            "Latest ingested documents";
        
        model.put("pagetitle", pageTitle);
        model.put("pagedescription", pageDescription);
        model.put("pagekeywords", pageKeywords);
        model.put("nrows", nrows);

        return new ModelAndView("newDocs", model);
    } //- handleRequest

} //- class NewDocController
