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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;

/**
 * Processes request for collection deletions. Renders the success view 
 * in case of a valid submission or the error view otherwise 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class DeleteCollectionController implements Controller {

	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		long collectionID;
		boolean error = false;
		String errMsg = "";
		
		collectionID = 
			ServletRequestUtils.getLongParameter(request, "cid", -1);
		if (collectionID == -1) {
			error = true;
			errMsg = "Bad collection ID : \"" + collectionID + "\" collection";
		}
		
		if (!error) {
			Collection collection = null;
			Account account = MCSUtils.getLoginAccount();
			try {
				collection = 
					myciteseer.getCollection(collectionID, account);
				if (collection == null) {
					error = true;
					errMsg = "Collection is not a : \"" + 
						account.getUsername() + "\" collection";
				}
				else {
					if (collection.isDeleteAllowed()) {
						myciteseer.deleteCollection(collection, account);
					}
				}
				return new ModelAndView(new RedirectView("viewCollections"));
			}catch (DataAccessException e) {
				errMsg = "An error ocurred while trying to get the collection " +
						"data.";
				return MCSUtils.errorPage(errMsg);
			}
		}else{
			return MCSUtils.errorPage(errMsg);
		}
	} //- handleRequest

} //- Class DeleteCollectionController
