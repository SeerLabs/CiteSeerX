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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.GroupMember;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSConstants;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.myciteseer.web.utils.Page;
import edu.psu.citeseerx.myciteseer.web.utils.Paginator;

/**
 * Shows members of a given group
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Revision$$ $$Date$$
 */
public class ViewGroupMembersController implements Controller {
	
	private static final String SORT_NAME  = "name";
	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String errMsg = null;
		boolean error = false;

		try {
			String tab = 
				ServletRequestUtils.getStringParameter(request, "tab", 
						"Members");
			long groupID = 
				ServletRequestUtils.getLongParameter(request, "gid", -1);
			
			// Do validations
			if (groupID == -1) {
				error = true;
				errMsg = "Bad group ID: \"" + groupID + "\"";
			}
			
			Group group = null;
			Account account = MCSUtils.getLoginAccount();
			if (!error) {
				group = myciteseer.getGroup(groupID);
				if (group == null) {
					error = true;
					errMsg = "Bad group ID: \"" + groupID + "\"";
				}
			}
			
			if (error) {
				// Something wrong happened send the error to the user 
				return MCSUtils.errorPage(errMsg);
			}
			
			List<GroupMember> members = new ArrayList<GroupMember>();
	        List<GroupMember> validating = new ArrayList<GroupMember>();
	        List<GroupMember> allMembers = myciteseer.getMembers(group);
	        
	        for (Iterator<GroupMember> it = allMembers.iterator(); it.hasNext();) {
	        	GroupMember member = (GroupMember)it.next();
	        	if (member.getValidating()) {
	        		validating.add(member);
	        	}else{
	        		members.add(member);
	        	}

	        }
	        
	        // Obtain the members to be shown (because of pagination)
	        int membersPageNumber = 
	        	ServletRequestUtils.getIntParameter(request, "mpn", 1);
	        String mSort = 
	        	ServletRequestUtils.getStringParameter(request, "msort", 
	        	        SORT_NAME);
			String smType = 
				ServletRequestUtils.getStringParameter(request, "smtype", 
				        "asc");
			Paginator membersPaginator = new Paginator();
			membersPaginator.setComparator(
			        getUserGroupComparator(mSort, smType));
			membersPaginator.setPageSize(MCSConstants.MAX_RECORDS_PER_PAGE);
			Page membersPage = 
				membersPaginator.fetchPage(membersPageNumber, members);
			
			// Obtain users to be validate as group members  to be shown 
			// (because of pagination)
			int validatingPageNumber = 
	        	ServletRequestUtils.getIntParameter(request, "vpn", 1);
	        String vSort = 
	        	ServletRequestUtils.getStringParameter(request, "vsort", 
	        			"name");
			String svType = 
				ServletRequestUtils.getStringParameter(request, "svtype", 
						"asc");
			Paginator validatingPaginator = new Paginator();
			validatingPaginator.setComparator(
					getUserGroupComparator(vSort, svType));
			validatingPaginator.setPageSize(MCSConstants.MAX_RECORDS_PER_PAGE);
			Page validatingPage = 
				validatingPaginator.fetchPage(
						validatingPageNumber, validating);
			
			// Generate page parameters.
			// Generate parameters
			String pageParam = "?gid="+group.getId();
			  
			// Add page number and item used to sort for members
			String nameQueryMembers = pageParam + "&amp;mpn=" + 
				membersPageNumber + "&amp;vpn=" + validatingPageNumber;
			
			nameQueryMembers += "&amp;smtype=" + 
				(smType.equalsIgnoreCase("asc") ? "desc" : "asc") + 
				"&amp;msort=" + mSort + "&amp;svtype=" +
				(svType.equalsIgnoreCase("asc") ? "desc" : "asc") + 
				"&amp;vsort=" + vSort;
			String nameQueryVal = nameQueryMembers;
			
			nameQueryMembers +=	"&amp;tab=Members";
			nameQueryVal += "&amp;tab=Validating";
			
			pageParam += "&amp;msort="+mSort+"&amp;smtype="+smType+
				"&amp;vsort="+vSort+"&amp;svtype="+svType;
			
			// Next page parameters
			String nextPageParamsMembers = null;
			if (membersPage.getPageNumber() < membersPage.getTotalPages()) {
				nextPageParamsMembers = pageParam + 
					"&amp;tab=Members&amp;mpn=" + 
					(membersPage.getPageNumber()+1) + "&amp;vpn=" + 
					validatingPageNumber;
			}
			String nextPageParamsValidating = null;
			if (validatingPage.getPageNumber() < 
			        validatingPage.getTotalPages()) {
				nextPageParamsValidating = pageParam + 
					"&amp;tab=Validating&amp;vpn=" + 
					(validatingPage.getPageNumber()+1) + "&amp;mpn=" +
					membersPageNumber;
			}
			// Previous page parameters	
			String previousPageParamsMembers = null;
			if (membersPage.getPageNumber() > 1) {
				previousPageParamsMembers = pageParam + 
					"&amp;tab=Members&amp;mpn=" + 
					(membersPage.getPageNumber()-1) + "&amp;vpn=" + 
					validatingPageNumber;
			}
			String previousPageParamsValidating = null;
			if (validatingPage.getPageNumber() > 1) {
				previousPageParamsValidating = pageParam + 
					"&amp;tab=Validating&amp;gmopn=" + 
					(validatingPage.getPageNumber()-1) + "&amp;mpn=" +
					membersPageNumber;
			}
			Map<String, Object> model = new HashMap<String, Object>();
	        model.put("members", membersPage.getPageContent());
	        model.put("msize", members.size());
	        model.put("tpm", membersPage.getTotalPages());
			model.put("mpn", membersPage.getPageNumber());
			model.put("validating", validatingPage.getPageContent());
			model.put("vsize", validating.size());
			model.put("tpv", validatingPage.getTotalPages());
			model.put("vpn", validatingPage.getPageNumber());
			model.put("nextpageparamsmem", nextPageParamsMembers);
			model.put("previouspageparamsmem", previousPageParamsMembers);
			model.put("nextpageparamsval", nextPageParamsValidating);
			model.put("previouspageparamsval", previousPageParamsValidating);
			model.put("nameqm", nameQueryMembers);
			model.put("nameqv", nameQueryVal);
			model.put("smtype", smType);
			model.put("msort", mSort);
			model.put("svtype", svType);
			model.put("vsort", vSort);
			model.put("group", group);
			model.put("tab", tab);
			boolean isOwner = 
				group.getOwner().compareToIgnoreCase(account.getUsername()) == 0
				? true : false;
			model.put("isowner", isOwner);
	        return new ModelAndView("viewGroupMembers", model);
		}catch (DataAccessException e) {
			e.printStackTrace();
			errMsg = "An error ocurred while trying to get the group users.";
			return MCSUtils.errorPage(errMsg);
		}
	} //- handleRequest
	
	/*
	 * Creates the chain comparator to sort the results
	 */
	private ComparatorChain getUserGroupComparator(String sort, String oType) {
		
		// Determine the order (will apply to the first comparator).
		boolean mainOrder = false; // true = asc; false = desc
		if (oType == null || "asc".compareToIgnoreCase(oType) != 0) {
			mainOrder = true;
		}
		
		// Order By?
		ComparatorChain comparator = new ComparatorChain();
		comparator.addComparator(GroupMember.memberComparator, mainOrder);
		return comparator;
	} //- getUserGroupComparator

} //- Class ViewGroupMembersController
