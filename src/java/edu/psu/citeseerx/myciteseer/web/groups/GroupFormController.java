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

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Form controller for group management.
 * @see org.springframework.web.servlet.mvc.SimpleFormController
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class GroupFormController extends SimpleFormController {

	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
		this.myciteseer = myciteseer;
	} //- setMyciteseer

	public GroupFormController() {
		setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("groupForm");
       	setFormView("group");
	} //- GroupFormController
	
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request)
	throws Exception {
		GroupForm form = null;
		long groupID;
		
        if (!isGroupsEnabled()) {
        	setFormView("groupsDisabled");
        	form = new GroupForm();
        }
        else {
			// obtain the group id, if any.
			groupID = ServletRequestUtils.getLongParameter(request, "gid", -1);
			if (groupID != -1) {
				// The user is updating an existing group
				Group group = myciteseer.getGroup(groupID);
				form = new GroupForm(group);
			}else{
				// The user wants to create a new group
				form = new GroupForm();
			}
        }
		return form;
	} //- formBackingObject
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#onBindAndValidate(javax.servlet.http.HttpServletRequest)
	 */
	protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
		GroupForm groupForm = (GroupForm)command;
		Group group = groupForm.getGroup();
		errors.setNestedPath("group");
		getValidator().validate(group, errors);
		errors.setNestedPath("");
	} //- onBindAndValidate
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
		
		
        if (!isGroupsEnabled()) {
            return new ModelAndView("groupsDisabled", null);
        }
		
		GroupForm groupForm = (GroupForm)command;
		Account account =  MCSUtils.getLoginAccount();
		
		try {
			if (groupForm.isNewGroup()) {
				// Add the new group.
				String authority = "GROUP" + "_" + 
					account.getUsername().toUpperCase() + "_" +
					System.currentTimeMillis();
				groupForm.getGroup().setAuthority(authority);
				myciteseer.addGroup(groupForm.getGroup());
			}else{
				myciteseer.updateGroup(groupForm.getGroup());
			}
		}catch (DataAccessException ex) {
			ex.printStackTrace();
			errors.rejectValue("group.name", "UNKNOWN_ERROR",
                    "An error occurred during the processing of your " +
                    "request. Please try again later.");
            return showForm(request, response, errors);
		}
		return new ModelAndView(new RedirectView(getSuccessView()));
	} //- onSubmit
	
	private boolean isGroupsEnabled() throws SQLException {
		MCSConfiguration config = myciteseer.getConfiguration();
		return config.getGroupsEnabled();
	} //- isGroupsEnabled
} //- class GroupFormController
