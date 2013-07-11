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

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Show collection statistics
 * @author Pradeep
 * @version $Rev: 89 $ $Date: 2011-03-15 15:12:07 -0400 (Tue, 15 Mar 2011) $
 */

public class ShowStatisticsController implements Controller {
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        Account adminAccount = MCSUtils.getLoginAccount();
        if (!adminAccount.isAdmin()) {
            return new ModelAndView("admin/adminRequired", null);
        }
        
        HashMap<String, Object> model = new HashMap<String, Object>();
	try {
		model.put("totaldocuments",csxdao.getDocumentsInCollection());
		model.put("totalcitations",csxdao.getCitationsInCollection());
		model.put("publicdocuments", csxdao.getPublicDocumentsInCollection());
		model.put("totalauthors", csxdao.getAuthorsInCollection());
		model.put("uniqueauthors", csxdao.getUniqueAuthorsInCollection());
		model.put("disambiguatedauthors", csxdao.getDisambiguatedAuthorsInCollection());
		model.put("uniquerecords", csxdao.getUniqueEntitiesInCollection());
		model.put("uniquepublicrecords", csxdao.getNumberofUniquePublicDocuments());
	} 		
	catch(Exception e) {
		e.printStackTrace();
	}

        return new ModelAndView("admin/showStatistics", model);
    } //- handleRequest
} //- class ShowStatisticsController 
