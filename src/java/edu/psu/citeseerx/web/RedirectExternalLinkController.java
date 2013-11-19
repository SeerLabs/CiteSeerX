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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.ExternalLink;
import edu.psu.citeseerx.webutils.RedirectUtils;

/**
 * Process a request to redirect to an associated URL for a given paper. If 
 * for some reason the associated URL is not found an error is generated.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class RedirectExternalLinkController implements Controller {

    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        String doi = request.getParameter("doi");
        String label = request.getParameter("label");
        
        Map<String, Object> model = new HashMap<String, Object>();
        if (doi == null || label == null) {
            String errorTitle = "Document Not Found";
            model.put("doi", doi);
            model.put("pagetitle", errorTitle);
            return new ModelAndView("baddoi", model);
        }
        
        // Getting the URL.
        ExternalLink link = null;
        try {
            link = csxdao.getLink(doi, label);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (link == null) {
            String errorTitle = "Document Not Found";
            model.put("doi", doi);
            model.put("pagetitle", errorTitle);
            return new ModelAndView("baddoi", model);
        }else{
            RedirectUtils.externalRedirect(response, link.getUrl());
        }
        return null;
    } //- handleRequest

} //- class RedirectExternalLinkController
