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
package edu.psu.citeseerx.myciteseer.web;

import edu.psu.citeseerx.myciteseer.domain.SubmissionNotificationItem;
import edu.psu.citeseerx.myciteseer.domain.UrlSubmission;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSConstants;
import edu.psu.citeseerx.myciteseer.web.utils.Page;
import edu.psu.citeseerx.myciteseer.web.utils.Paginator;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes request for submission details rendering an adequate view in case
 * of success or failure  
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ViewSubmissionDetailsController implements Controller {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
    throws ServletException, IOException {
        String jid = request.getParameter("subid");
        String tab = 
        	ServletRequestUtils.getStringParameter(request, "tab", 
        			"Documents");
        List<SubmissionNotificationItem> components = 
            myciteseer.getSubmissionComponents(jid);

        List<SubmissionNotificationItem> successes = 
        	new ArrayList<SubmissionNotificationItem>();
        List<SubmissionNotificationItem> failures = 
        	new ArrayList<SubmissionNotificationItem>();
        for (SubmissionNotificationItem item : components) {
            if (item.isSuccess()) {
                successes.add(item);
            } else {
                failures.add(item);
            }
        }
        
        // Pagination
        int successPageNumber = 
        	ServletRequestUtils.getIntParameter(request, "spn", 1);
        Paginator successPaginator = new Paginator();
        successPaginator.setPageSize(MCSConstants.MAX_RECORDS_PER_PAGE);
        Page successPage = 
        	successPaginator.fetchPage(successPageNumber, successes);
        int failurePageNumber =
        	ServletRequestUtils.getIntParameter(request, "fpn", 1);
        Paginator failurePaginator = new Paginator();
        failurePaginator.setPageSize(MCSConstants.MAX_RECORDS_PER_PAGE);
        Page failurePage = 
        	failurePaginator.fetchPage(failurePageNumber, failures);
        
        // Parameters creation
        String pageParam = "?subid=" + jid;
        // Next page parameters
        String nextPageParamsSuc = null;
		if (successPage.getPageNumber() < successPage.getTotalPages()) {
			nextPageParamsSuc = pageParam + "&amp;tab=Documents&amp;spn=" + 
				(successPage.getPageNumber()+1);
		}
		String nextPageParamsFail = null;
		if (failurePage.getPageNumber() < failurePage.getTotalPages()) {
			nextPageParamsFail = pageParam + "&amp;tab=Errors&amp;fpn=" + 
				(failurePage.getPageNumber()+1);
		}
		// Previous page parameters	
		String previousPageParamsSuc = null;
		if (successPage.getPageNumber() > 1) {
			previousPageParamsSuc = pageParam + "&amp;tab=Documents" +
				"&amp;spn=" + (successPage.getPageNumber()-1);
		}
		String previousPageParamsFail = null;
		if (failurePage.getPageNumber() > 1) {
			previousPageParamsFail = pageParam + "&amp;tab=Errors&amp;fpn=" + 
				(failurePage.getPageNumber()-1);
		}
		
        
        UrlSubmission submission = myciteseer.getUrlSubmission(jid);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("submission", submission);
        model.put("successes", successPage.getPageContent());
        model.put("ssize", successes.size());
        model.put("tps", successPage.getTotalPages());
		model.put("spn", successPage.getPageNumber());
        model.put("failures", failurePage.getPageContent());
        model.put("fsize", failures.size());
        model.put("tpf", failurePage.getTotalPages());
		model.put("fpn", failurePage.getPageNumber());
        model.put("nextpageparamssuc", nextPageParamsSuc);
		model.put("previouspageparamssuc", previousPageParamsSuc);
		model.put("nextpageparamsfail", nextPageParamsFail);
		model.put("previouspageparamsfail", previousPageParamsFail);
		model.put("tab", tab);
        return new ModelAndView("viewSubmissionDetails", model);
    } //- handleRequest
    
} //- Class ViewSubmissionDetailsController
