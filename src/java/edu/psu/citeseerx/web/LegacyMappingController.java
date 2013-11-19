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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.webutils.RedirectUtils;

/**
 * Controller used to map citeseer documents to CiteSeerX documents, and 
 * redirect to the document view  or a mapping error view
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class LegacyMappingController implements Controller {
    
    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    private String successView = "/viewdoc/summary";
    
    public void setSuccessView(String successView) {
        this.successView = successView;
    } //- setSuccessView
    
    
    private String errorView = "legacyMapError";
    
    public void setErrorView(String errorView) {
        this.errorView = errorView;
    } //- setErrorView
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        Map<String, Object> model = new HashMap<String, Object>();
        String errMsg = "";
        String citeseerOldURL = "http://citeseer.ist.psu.edu/old/";
        String didstr = request.getParameter("did");
        Integer did = null;
        try {
            did = Integer.parseInt(didstr);
        } catch (NumberFormatException e) {
            errMsg = "Invalid did parameter - should be an integer";
        }

        if (did != null) {
            String doi = csxdao.getNewID(did);
            if (doi != null) {
                String path = successView+"?doi="+doi;
                RedirectUtils.sendPermanentRedirect(request, response, path);
                return null;
            } else if(did != null) {
            	String path = citeseerOldURL+did+".html";
            	RedirectUtils.sendPermanentRedirect(request, response, path);
            }
        }else {
            errMsg = "No matching doi for specified legacy ID: " +
            "either the specified ID never existed or the legacy " +
            "document could not be ingested into CiteSeerX.";
        }
        model.put("did", didstr);
        model.put("errMsg", errMsg);
        return new ModelAndView(errorView, model);

    }  //- handleRequest

}
