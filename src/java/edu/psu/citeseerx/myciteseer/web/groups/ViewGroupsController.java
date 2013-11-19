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
import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSConstants;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.myciteseer.web.utils.Page;
import edu.psu.citeseerx.myciteseer.web.utils.Paginator;

/**
 * Controls the presentation of user groups
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class ViewGroupsController implements Controller {

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

		MCSConfiguration config = myciteseer.getConfiguration();
		if (!config.getGroupsEnabled()) {
			return new ModelAndView("groupsDisabled");
		}
		
		Account account = MCSUtils.getLoginAccount();
		try {
			// Get the groups the user owns or it is a member
			List<Group> groups = myciteseer.getGroups(account.getUsername());
			
			List<Group> myGroups = new ArrayList<Group>();
	        List<Group> memberOf = new ArrayList<Group>();
			
	        for (Iterator<Group> it = groups.iterator(); it.hasNext();) {
	        	Group group = (Group)it.next();
	        	if (group.getOwner().compareToIgnoreCase(account.getUsername()) 
	        	        == 0) {
	        		myGroups.add(group);
	        	}else{
	        		memberOf.add(group);
	        	}
	        }
	        
	        // Obtain the owned groups to be shown
	        // Page number to show (Groups owned by the user)
	        int myGroupsPageNumber =  ServletRequestUtils.getIntParameter(request, "mgpn", 1);
	        String mgSort = 
	        	ServletRequestUtils.getStringParameter(request, "mgsort", 
	        			SORT_NAME);
			String smgType = 
				ServletRequestUtils.getStringParameter(request, "smgtype", 
						"asc");
			String tab = 
				ServletRequestUtils.getStringParameter(request, "tab", 
						"MyGroups");
			Paginator myGroupsPaginator = new Paginator();
			myGroupsPaginator.setComparator(
					getGroupComparator(mgSort, smgType));
			myGroupsPaginator.setPageSize(MCSConstants.MAX_RECORDS_PER_PAGE);
			Page myGroupsPage = myGroupsPaginator.fetchPage(myGroupsPageNumber, 
					myGroups);
			
			// Obtain the groups the user is member of to be shown
			int memberOfPageNumber = 
				ServletRequestUtils.getIntParameter(request, "mogpn", 1);
			String mogSort = 
				ServletRequestUtils.getStringParameter(request, "mogsort", 
						SORT_NAME);
			String smogType = 
				ServletRequestUtils.getStringParameter(request, "smogtype",
						"asc");
			Paginator memberOfPaginator = new Paginator();
			memberOfPaginator.setComparator(
					getGroupComparator(mogSort, smogType));
			memberOfPaginator.setPageSize(MCSConstants.MAX_RECORDS_PER_PAGE);
			Page memberOfPage = memberOfPaginator.fetchPage(memberOfPageNumber,
					memberOf);

			// Generate page parameters
			String pageParam = "?mgsort=" + mgSort + "&amp;smgtype=" + 
				smgType + "&amp;mogsort=" + mogSort + "&amp;smogtype=" + 
				smogType;
			
			String nameQueryMG = "?mgpn=" + myGroupsPageNumber + 
				"&amp;gmopn=" + memberOfPageNumber +"&amp;mgsort=" + mgSort + 
				"&amp;mogsort=" + mogSort;
			nameQueryMG += "&amp;smgtype=" + 
				(smgType.equalsIgnoreCase("asc") ? "desc" : "asc") +
				"&amp;smogtype=" + 
				(smogType.equalsIgnoreCase("asc") ? "desc" : "asc");
			String nameQueryMOG = nameQueryMG;
			nameQueryMG += "&amp;tab=MyGroups";
			nameQueryMOG += "&amp;tab=Member";
			
			// Next page parameters
			String nextPageParamsMG = null;
			if (myGroupsPage.getPageNumber() < myGroupsPage.getTotalPages()) {
				nextPageParamsMG = pageParam + "&amp;tab=MyGroups" + 
					"&amp;mgpn=" + (myGroupsPage.getPageNumber()+1) +
					"&amp;mogpn=" + memberOfPageNumber;
			}
			String nextPageParamsMOG = null;
			if (memberOfPage.getPageNumber() < memberOfPage.getTotalPages()) {
				nextPageParamsMOG = pageParam + "&amp;tab=Member" + 
					"&amp;mogpn=" + (memberOfPage.getPageNumber()+1) +
					"&amp;mgpn=" + myGroupsPageNumber;
			}
			// Previous page parameters	
			String previousPageParamsMG = null;
			if (myGroupsPage.getPageNumber() > 1) {
				previousPageParamsMG = pageParam + "&amp;tab=MyGroups" + 
					"&amp;mgpn="+(myGroupsPage.getPageNumber()-1);
			}
			String previousPageParamsMOG = null;
			if (memberOfPage.getPageNumber() > 1) {
				previousPageParamsMOG = pageParam + "&amp;tab=Member" + 
					"&amp;mogpn="+(memberOfPage.getPageNumber()-1);
			}
			Map<String, Object> model = new HashMap<String, Object>();
	        model.put("mygroups", myGroupsPage.getPageContent());
	        model.put("mgsize", myGroups.size());
	        model.put("tpmg", myGroupsPage.getTotalPages());
			model.put("mgpn", myGroupsPage.getPageNumber());
			model.put("memberof", memberOfPage.getPageContent());
			model.put("mosize", memberOf.size());
			model.put("tpmog", memberOfPage.getTotalPages());
			model.put("mogpn", memberOfPage.getPageNumber());
			model.put("nextpageparamsmg", nextPageParamsMG);
			model.put("previouspageparamsmg", previousPageParamsMG);
			model.put("nextpageparamsmog", nextPageParamsMOG);
			model.put("previouspageparamsmog", previousPageParamsMOG);
			model.put("nameqmg", nameQueryMG);
			model.put("nameqmog", nameQueryMOG);
			model.put("smgtype", smgType);
			model.put("mgsort", mgSort);
			model.put("smogtype", smogType);
			model.put("mogsort", mogSort);
			model.put("tab", tab);
	        return new ModelAndView("viewGroups", model);
		}catch (DataAccessException e) {
			String errMsg = "An error ocurred while trying to get user groups.";
			return MCSUtils.errorPage(errMsg);
		}
	} //- handleRequest

	/*
	 * Creates the chain comparator to sort the results
	 */
	private ComparatorChain getGroupComparator(String sort, String oType) {
		
		// Determine the order (will apply to the first comparator).
		boolean mainOrder = false; // true = asc; false = desc
		if (oType == null || "asc".compareToIgnoreCase(oType) != 0) {
			mainOrder = true;
		}
		
		// Order By?
		ComparatorChain comparator = new ComparatorChain();
		comparator.addComparator(Group.nameComparator, mainOrder);
		return comparator;
	} //- getGroupComparator
} //- Class ViewGroupsController
