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

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Paper;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSConstants;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.myciteseer.web.utils.Page;
import edu.psu.citeseerx.myciteseer.web.utils.Paginator;
import edu.psu.citeseerx.myciteseer.web.utils.PaperUtils;
import edu.psu.citeseerx.domain.*;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.util.*;

/**
 * Process requests to list monitors. Renders the success view in case of
 * a valid submission otherwise shows the error view.
 * @author Isaac Council
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ViewMonitorsController implements Controller {
	
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
        
        Account account = MCSUtils.getLoginAccount();
        List<Paper> papers = getPapers(account, request);
        
        int papersPageNumber = ServletRequestUtils.getIntParameter(request, 
        		"ppn", 1);
		String sort = ServletRequestUtils.getStringParameter(request, "psort",
				"title");
		String spType = ServletRequestUtils.getStringParameter(request, 
				"sptype", "asc");
		Paginator paperPaginator = new Paginator();
		paperPaginator.setComparator(
				PaperUtils.getPaperComparator(sort, spType));
		paperPaginator.setPageSize(MCSConstants.MAX_RECORDS_PER_PAGE);
		Page page = paperPaginator.fetchPage(papersPageNumber, papers);
        
		String dateQuery, citeQuery, titleQuery, pageParam;
		
		// Create parameters for the page
		pageParam = "?mid=0";
		
		dateQuery = citeQuery = titleQuery = pageParam;
		
		dateQuery += "&amp;ppn=" + papersPageNumber + "&amp;psort=" + 
			PaperUtils.SORT_DATE + "&amp;sptype=";
		citeQuery += "&amp;ppn=" + papersPageNumber + "&amp;psort="+
			PaperUtils.SORT_CITE + "&amp;sptype=";
		titleQuery += "&amp;ppn=" + papersPageNumber + "&amp;psort="+
		PaperUtils.SORT_TIT + "&amp;sptype=";
		if (sort.equalsIgnoreCase(PaperUtils.SORT_DATE)) {
			dateQuery += (spType.equalsIgnoreCase("asc")) ? "desc" : "asc";
			citeQuery += "asc";
			titleQuery += "asc";
		}else if (sort.equalsIgnoreCase(PaperUtils.SORT_CITE)) {
			citeQuery += (spType.equalsIgnoreCase("asc")) ? "desc" : "asc";
			dateQuery += "asc";
			titleQuery += "asc";
		}else{
			// By default SORT_TIT
			titleQuery += (spType.equalsIgnoreCase("asc")) ? "desc" : "asc";
			dateQuery += "asc";
			citeQuery += "asc";
		}

		pageParam += "&amp;psort="+sort+"&amp;sptype="+spType;

		String nextPageParams = null;
		if (page.getPageNumber() < page.getTotalPages()) {
			nextPageParams = pageParam + "&amp;ppn="+(page.getPageNumber()+1);
		}
			
		String previousPageParams = null;
		if (page.getPageNumber() > 1) {
			previousPageParams = pageParam + "&amp;ppn="+(page.getPageNumber()-1);
		}
		
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("monitors", page.getPageContent());
		model.put("npresults", papers.size());
		model.put("tppn", page.getTotalPages());
		model.put("ppn", page.getPageNumber());
		model.put("nextpageparams", nextPageParams);
		model.put("previouspageparams", previousPageParams);
		model.put("dateq", dateQuery);
		model.put("citeq", citeQuery);
		model.put("titleq", titleQuery);
		model.put("sptype", spType);
		model.put("psort", sort);
        return new ModelAndView("viewMonitors", model);
        
    }
    
    
    private List<Paper> getPapers(Account account, 
    		HttpServletRequest request) throws SQLException {

        List<String> dois = myciteseer.getMonitors(account);
        List<Paper> papers = new ArrayList<Paper>();
        
        for (Object doi : dois) {
            Document doc = csxdao.getDocumentFromDB((String)doi, false, false);
            if (doc == null || !doc.isPublic()) {
                continue;
            }
            Paper paper = new Paper();
            paper.setDoc(DomainTransformer.toThinDoc(doc));
            String url = 
	        	request.getRequestURL().toString().replace(
	        			"myciteseer/action/viewMonitors", 
	        			"viewdoc/summary");
	        paper.setCoins(BiblioTransformer.toCOinS(paper.getDoc(),
	        		url));
            papers.add(paper);
        }
        return papers;

    }  //- getPapers
    
}  //- class ViewMonitorsController
