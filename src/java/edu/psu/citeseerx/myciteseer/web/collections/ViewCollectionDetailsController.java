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
package edu.psu.citeseerx.myciteseer.web.collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.BiblioTransformer;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DomainTransformer;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.CollectionNote;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.domain.Paper;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSConstants;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.myciteseer.web.utils.Page;
import edu.psu.citeseerx.myciteseer.web.utils.Paginator;
import edu.psu.citeseerx.myciteseer.web.utils.PaperUtils;

/**
 * Handle presentation of collection items
 * @author Juan Pablo Fernandez Ramirez
 * @version $Revision$ $Date$
 */
public class ViewCollectionDetailsController implements Controller {
	
	// MyCiteSeer data access
	private MyCiteSeerFacade myciteseer;
    
	// CiteSeer data access
	private CSXDAO csxdao;
	
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		long collectionID;
		String errMsg = null;
		boolean error = false;
		Account account = MCSUtils.getLoginAccount();

		collectionID = ServletRequestUtils.getLongParameter(request, "cid", -1);
		if (collectionID == -1) {
			error = true;
			errMsg = "Bad collection ID : \"" + collectionID + "\" collection";
		}

		// Obtain collection info.
		Collection collection = null;
		try {
			collection = 
				myciteseer.getCollection(collectionID, account);
			if (collection == null) {
				error = true;
				errMsg = "Bad collection ID : \"" + collectionID + 
					"\" collection";
			}
		}catch (DataAccessException e) {
			errMsg = "An error ocurred while trying to get the collection data.";
			error = true;
		}

		if (!error) {
			List<PaperCollection> colPapers = null;
			List<CollectionNote> collectionNotes;
			try {
				// Obtain paper's id associated with collectionID and owned by
				// the connected user
				colPapers = 
					myciteseer.getUserCollectionPapers(collectionID, account);
				
				// Obtain collection notes.
				collectionNotes = myciteseer.getCollectionNotes(collectionID);
			
				// Obtain paper info for each paper within the collection
				List<Paper> papers = getPapers(colPapers, request);
				
				int papersPageNumber = ServletRequestUtils.getIntParameter(
				        request, "ppn", 1);
				String sort = ServletRequestUtils.getStringParameter(request, 
				        "psort", "title");
				String spType = ServletRequestUtils.getStringParameter(request,
				        "sptype", "asc");
				Paginator paperPaginator = new Paginator();
				paperPaginator.setComparator(
						PaperUtils.getPaperComparator(sort, spType));
				paperPaginator.setPageSize(MCSConstants.MAX_RECORDS_PER_PAGE);
				Page page = paperPaginator.fetchPage(papersPageNumber, papers);
	
				String dateQuery, citeQuery, titleQuery, pageParam;
				
				// Create parameters for the page
				pageParam = "?cid="+collection.getCollectionID();
				
				dateQuery = citeQuery = titleQuery = pageParam;
				
				dateQuery += "&amp;ppn=" + papersPageNumber + "&amp;psort=" + 
					PaperUtils.SORT_DATE + "&amp;sptype=";
				citeQuery += "&amp;ppn=" + papersPageNumber + "&amp;psort="+
					PaperUtils.SORT_CITE + "&amp;sptype=";
				titleQuery += "&amp;ppn=" + papersPageNumber + "&amp;psort="+
				PaperUtils.SORT_TIT + "&amp;sptype=";
				if (sort.equalsIgnoreCase(PaperUtils.SORT_DATE)) {
					dateQuery += (spType.equalsIgnoreCase("asc")) ? "desc" : 
					    "asc";
					citeQuery += "asc";
					titleQuery += "asc";
				}else if (sort.equalsIgnoreCase(PaperUtils.SORT_CITE)) {
					citeQuery += (spType.equalsIgnoreCase("asc")) ? "desc" : 
					    "asc";
					dateQuery += "asc";
					titleQuery += "asc";
				}else{
					// By default SORT_TIT
					titleQuery += (spType.equalsIgnoreCase("asc")) ? "desc" : 
					    "asc";
					dateQuery += "asc";
					citeQuery += "asc";
				}

				pageParam += "&amp;psort="+sort+"&amp;sptype="+spType;

				String nextPageParams = null;
				if (page.getPageNumber() < page.getTotalPages()) {
					nextPageParams = pageParam + "&amp;ppn="+(
					        page.getPageNumber()+1);
				}
					
				String previousPageParams = null;
				if (page.getPageNumber() > 1) {
					previousPageParams = pageParam + "&amp;ppn="+
					(page.getPageNumber()-1);
				}
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("papers", page.getPageContent());
				model.put("npresults", papers.size());
				model.put("tppn", page.getTotalPages());
				model.put("ppn", page.getPageNumber());
				model.put("nextpageparams", nextPageParams);
				model.put("previouspageparams", previousPageParams);
				model.put("dateq", dateQuery);
				model.put("citeq", citeQuery);
				model.put("titleq", titleQuery);
				model.put("collection", collection);
				model.put("collectionNotes", collectionNotes);
				model.put("sptype", spType);
				model.put("psort", sort);
				return new ModelAndView("viewCollectionDetails", model);
			}catch (DataAccessException e) {
				errMsg = "An error ocurred while trying to get the collection " +
						"data.";
				return MCSUtils.errorPage(errMsg);
			}
		}
		else {
			return MCSUtils.errorPage(errMsg);
		}
	} //- handleRequest

	
	private List<Paper> getPapers(List<PaperCollection> colPapers, 
			HttpServletRequest request) {
		List<Paper> papers = new ArrayList<Paper>(); 
		
		if (colPapers != null) {
			Iterator<PaperCollection> it = colPapers.iterator();
			while(it.hasNext()) {
				PaperCollection cp = it.next();
				Document doc = null;
		        try {
		            doc = csxdao.getDocumentFromDB(cp.getPaperID());
		            if (doc != null && doc.isPublic()) {
    		            Paper paper = new Paper();
    			        paper.setDoc(DomainTransformer.toThinDoc(doc));
    			        String url = 
    			        	request.getRequestURL().toString().replace(
    			        			"myciteseer/action/viewCollectionDetails", 
    			        			"viewdoc/summary");
    			        paper.setCoins(BiblioTransformer.toCOinS(paper.getDoc(),
    			        		url));
    		            
    			        // Obtain paper notes
    			        paper.setNotes(myciteseer.getPaperNotes(cp.getPaperID(), 
    			        		cp.getCollectionID()));
    			        
    			        // Add the paper to the papers collection
    			        papers.add(paper);
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}
		return papers;
	} //- getPapers
	
} //- Class ViewCollectionDetailssController
