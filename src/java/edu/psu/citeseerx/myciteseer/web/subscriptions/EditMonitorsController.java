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
package edu.psu.citeseerx.myciteseer.web.subscriptions;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;

import java.sql.SQLException;

/**
 * Process requests to Edit/Delete monitors. Renders the success view in case of
 * a valid submission otherwise shows the error view.
 * @author Isaac Council
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class EditMonitorsController implements Controller {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        String doi = request.getParameter("doi");
        String type = request.getParameter("type");
        
        Account account = MCSUtils.getLoginAccount();
        
        if (type != null && type.equals("del") && doi != null) {
            myciteseer.deleteMonitor(account, doi);
            RedirectUtils.sendRedirect(request, response,
                "/myciteseer/action/viewMonitors");
            return null;
        }
        
        Document doc = null;
        if (doi != null) {
            doc = csxdao.getDocumentFromDB(doi, false, false);
        }
        
        Boolean error = false;
        String errMsg = null;
        
        if (doc == null) {
            error = true;
            errMsg = "Invalid DOI \""+doi+"\"";
        }
        
        if (error) {
            HashMap<String, Object> model = new HashMap<String, Object>();
            model.put("errMsg", errMsg);
            return new ModelAndView("parameterError", model);
        } else {
            myciteseer.addMonitor(account, doi);
            RedirectUtils.sendRedirect(request, response,
                "/myciteseer/action/viewMonitors");
            return null;
        }
    } //- handleRequest
    
}  //- class EditMonitorsController
