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
package edu.psu.citeseerx.myciteseer.web.admin;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.LinkType;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Processes request for external links deletions. Renders the success view 
 * in case of a valid submission or the error view otherwise 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class DeleteExternalLinkController implements Controller {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        Account account = MCSUtils.getLoginAccount();
        if (!account.isAdmin()) {
            return new ModelAndView("adminRequired", 
                    new HashMap<String, Object>());
        }
        String label = request.getParameter("id");
        
        if (null != label) {
            LinkType link = csxdao.getLinkType(label);
            csxdao.deleteLinkType(link);
        }
        return new ModelAndView(new RedirectView("editExternalLinks"));
    } //- handleRequest

} //- class DeleteExternalLinkController
