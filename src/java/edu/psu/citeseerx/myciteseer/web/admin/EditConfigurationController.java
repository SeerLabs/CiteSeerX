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

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Controller used to manipulate System configuration
 * @see org.springframework.web.servlet.mvc.Controller
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class EditConfigurationController implements Controller {

	private MyCiteSeerFacade myciteseer;
    
    /**
     * @param myciteseer
     */
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Account adminAccount = MCSUtils.getLoginAccount();
		// Only administrators can perform this action 
        if (!adminAccount.isAdmin()) {
            return new ModelAndView("admin/adminRequired", null);
        }
        
        // Obtain actual system configuration
        MCSConfiguration editConfig = myciteseer.getConfiguration();
        
        String type = request.getParameter("type");
        if (type != null && type.equals("update")) {
        	// User has change the configuration
        	
        	boolean newAccountsEnabled = 
        		ServletRequestUtils.getBooleanParameter(request, "setaccounts",
        				false);
        	boolean urlSubmissionEnabled = 
        		ServletRequestUtils.getBooleanParameter(request, 
        				"seturlsubmission", false);
        	boolean correctionsEnabled = 
        		ServletRequestUtils.getBooleanParameter(request, 
        				"setcorrections", false);
        	boolean groupsEnabled = 
        		ServletRequestUtils.getBooleanParameter(request, "setgroups",
        				false);
        	boolean peopleSearchEnabled = 
        		ServletRequestUtils.getBooleanParameter(request, 
        				"setpeoplesearch", false);
        	boolean personalPortalEnabled =
        		ServletRequestUtils.getBooleanParameter(request, 
        				"setpersonalportal", false);
        	editConfig.setNewAccountsEnabled(newAccountsEnabled);
        	editConfig.setUrlSubmissionsEnabled(urlSubmissionEnabled);
        	editConfig.setCorrectionsEnabled(correctionsEnabled);
        	editConfig.setGroupsEnabled(groupsEnabled);
        	editConfig.setPeopleSearchEnabled(peopleSearchEnabled);
        	editConfig.setPersonalPortalEnabled(personalPortalEnabled);
        	myciteseer.saveConfiguration(editConfig);
        }
        
        HashMap<String, Boolean> model = new HashMap<String, Boolean>();
    	model.put("setaccounts", 
    			editConfig.getNewAccountsEnabled());
    	model.put("seturlsubmission", 
    			editConfig.getUrlSubmissionsEnabled());
    	model.put("setcorrections", editConfig.getCorrectionsEnabled());
    	model.put("setgroups", editConfig.getGroupsEnabled());
    	model.put("setpeoplesearch", editConfig.getPeopleSearchEnabled());
    	model.put("setpersonalportal", editConfig.getPersonalPortalEnabled());
    	return new ModelAndView("admin/editConfiguration", model);
	} //- handleRequest    
    
} //- EditConfigurationController
