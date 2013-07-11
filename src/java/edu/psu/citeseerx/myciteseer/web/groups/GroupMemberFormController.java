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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.search.SolrSelectUtils;
import edu.psu.citeseerx.myciteseer.web.search.SolrSelectUtils.SolrException;

/**
 * Form controller for group members management.
 * @see org.springframework.web.servlet.mvc.SimpleFormController
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class GroupMemberFormController extends SimpleFormController {
	
	// Url to send search request to the Sorl instance 
	private String solrSelectPeopleUrl;
	
	/**
	 * @param solrSelectPeopleUrl URL to the people Solr instance
	 */
	public void setSolrSelectPeopleUrl(String solrSelectPeopleUrl) 
	throws MalformedURLException {
		new URL(solrSelectPeopleUrl);
		this.solrSelectPeopleUrl = solrSelectPeopleUrl;
	} //- setSolrSelectPeopleUrl
	
	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
		this.myciteseer = myciteseer;
	} //- setMyciteseer

	public GroupMemberFormController() {
		setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("groupMemberForm");
        setFormView("addGroupMembers");
	} //- GroupFormController
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request)
	throws Exception {
		GroupMemberForm form = null;
		long groupID;
		
		// obtain the group id.
		groupID = ServletRequestUtils.getLongParameter(request, "gid", -1);
		if (groupID != -1) {
			// Retrieve group members 
			Group group = myciteseer.getGroup(groupID);
			if (group == null) {
				throw new Exception("Bad group ID: \"" + groupID + "\"");
			}
			form = new GroupMemberForm(group);
			
		}
		return form;
	} //- formBackingObject

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	protected Map<String, Object> referenceData(
			HttpServletRequest request, Object command, Errors errors) 
			throws Exception {
		
		// If search parameters are provided do the search
		GroupMemberForm groupMemberForm = (GroupMemberForm)command;

		if (groupMemberForm.getFirstName() == null) {
			groupMemberForm.setFirstName(
					ServletRequestUtils.getStringParameter(request, 
							"search_firstname", ""));
		}
		if (groupMemberForm.getMiddleName() == null) {
			groupMemberForm.setMiddleName(
					ServletRequestUtils.getStringParameter(request, 
							"search_middlename", ""));
		}
		if (groupMemberForm.getLastName() == null) {
			groupMemberForm.setLastName(
					ServletRequestUtils.getStringParameter(request, 
							"search_lastname", ""));
		}
		List<Account> users = null;
        boolean error = false;
        String errMsg = null;
		String fullName = groupMemberForm.getFullName();
		
		if (fullName.trim().length() > 0) {
			// Build the complete request to solr.
	        StringBuffer urlBuffer = new StringBuffer();
	        urlBuffer.append(solrSelectPeopleUrl);
	        urlBuffer.append("?");
	        urlBuffer.append("q=");
	        urlBuffer.append(URLEncoder.encode(fullName, "UTF-8"));
	        urlBuffer.append("&qt=");
	        urlBuffer.append("standard");
	        urlBuffer.append("&hl=true");
	        urlBuffer.append("&wt=json");

	        
	        try {
        		// Send the query
        		JSONObject output = 
        			SolrSelectUtils.doJSONQuery(urlBuffer.toString());
        		// Obtain the results.

        		// Transform response from JSON to Domain objects.
        		users = SolrSelectUtils.buildHitAccountListJSON(output);
        		
        		/*
        		 *  Search results may contain actual members: we don't want
        		 *  them in the list.
        		 */
        		users = extractMembers(users, groupMemberForm.getGroup());
	        }catch (SolrException e) {
	            error = true;
	            int code = e.getStatusCode();
	            if (code == 400) {
	                errMsg = "Invalid query type.  " +
	                        "Please check your syntax.";
	            } else {
	                errMsg = "<p><span class=\"char_emphasized\">" +
	                        "Error processing query.</span></p><br>" +
	                        "<p>The most likely cause of this condition " +
	                        "is a malformed query. Please check your query  " +
	                        "syntax and, if the problem persists, " +
	                        "contact an admin for assistance.</p>";
	            }
	        }catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
		Map<String, Object> model = null;
		if (users != null && !users.isEmpty()) {
			model =	new HashMap<String, Object>();
			model.put("error", new Boolean(error));
	        model.put("errorMsg", errMsg);
	        model.put("users", (!error) ? users : new ArrayList<Account>());
		}
		return model;
	} //- referenceData

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		GroupMemberForm groupMemberForm = (GroupMemberForm)command;
		Group group;
		try {
			if (groupMemberForm.getUserIDs() == null) {
				throw new Exception("No users have been selected");
			}
			String[] userIDs = groupMemberForm.getUserIDs();
			group = groupMemberForm.getGroup();
			for(int i = 0; i < userIDs.length; ++i) {
				myciteseer.addMember(group, userIDs[i], false);
			}
		}catch (DataAccessException ex) {
			ex.printStackTrace();
			errors.rejectValue("userIDs", "UNKNOWN_ERROR",
                    "An error occurred during the processing of your " +
                    "request. Please try again later.");
            return showForm(request, response, errors);
		}catch (Exception e) {
				errors.rejectValue("userIDs", "NO_USERS_SELECTED",
	                    e.getMessage());
	            return showForm(request, response, errors);
			}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("gid", group.getId());
		return new ModelAndView(new RedirectView(getSuccessView()), model);
	} //- OnSubmit
	
	/*
	 * Removes from the list users who are members of the group
	 */
	private List<Account> extractMembers(List<Account> users, Group group) {
		List<Account> list = new ArrayList<Account>();
		for (Account user : users) {
			if (!myciteseer.isMember(group, user.getUsername())) {
				list.add(user);
			}
		}
		return list;
	} //- extractMembers
} //- Class GroupMemberFormController
