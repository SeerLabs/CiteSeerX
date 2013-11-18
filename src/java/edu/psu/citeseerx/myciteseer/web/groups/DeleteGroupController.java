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
package edu.psu.citeseerx.myciteseer.web.groups;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Deletes a group
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class DeleteGroupController implements Controller {

	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		long groupID;
		boolean error = false;
		String errMsg = "";
		
		groupID = ServletRequestUtils.getLongParameter(request, "gid", -1);
		if (groupID == -1) {
			error = true;
			errMsg = "Bad group ID : \"" + groupID + "\"";
		}
		
		try {
			Account account = MCSUtils.getLoginAccount();
			Group group = myciteseer.getGroup(groupID);
			if (group == null) {
				// This user doesn't own the group
				error = true;
				errMsg = "\"" + account.getUsername() + 
					"\" is not the owner of the group";
			}else{
				myciteseer.deleteGroup(group);
			}
		}catch (DataAccessException e) {
			e.printStackTrace();
			error = true;
			errMsg = "An error occurred during the processing of your " +
            "request. Please try again later.";
		}
		HashMap<String, Object> model = new HashMap<String, Object>();
		if (error) {
            model.put("errMsg", errMsg);
            return new ModelAndView("parameterError", model);
		}
		else {
			return new ModelAndView(new RedirectView("viewGroups"));
		}
	} //- handleRequest

} //- class DeleteGroupController
