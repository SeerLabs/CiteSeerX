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

/**
 * Processes request for paper notes deletions. Renders the success view 
 * in case of a valid submission or the error view otherwise 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

public class DeletePaperNoteController implements Controller {

	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String errMsg = "";
		boolean error = false;
		long paperNoteID = 
			ServletRequestUtils.getLongParameter(request, "nid", -1);
		String paperID = 
			ServletRequestUtils.getStringParameter(request, "doi", "");
		long collectionID = 
			ServletRequestUtils.getLongParameter(request, "cid", -1);
		
		if (paperNoteID == -1) {
			errMsg = "Invalid note ID \""+paperNoteID+"\"";
			error = true;
		}
		
		if (collectionID == -1) {
			errMsg += "Invalid collection ID \""+collectionID+"\"";
			error = true;
		}
		
		if (paperID.trim().length() == 0) {
			errMsg += "Invalid DOI \""+paperID+"\"";
			error = true;
		}
		
		if (!error) {
			Account account = MCSUtils.getLoginAccount();
			try {
				myciteseer.deletePaperNote(paperNoteID, paperID, collectionID,
						account);
				return new ModelAndView(new 
						RedirectView("viewCollectionDetails"), 
						"cid", collectionID);
			}catch (DataAccessException e) {
				errMsg = "An error ocurred while trying to delete the paper " +
						"note.";
				return MCSUtils.errorPage(errMsg);
			}
		}else{
			return MCSUtils.errorPage(errMsg);
		}
	} //- handleRequest
} //- Class DeletePaperNoteController
