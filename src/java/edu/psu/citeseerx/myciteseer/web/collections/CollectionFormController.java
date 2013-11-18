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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Processes request for collection edit/create. Renders the success view in
 * case of a valid submission or resubmits the form view in case of errors
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CollectionFormController extends SimpleFormController {
	private MyCiteSeerFacade myciteseer;

	public CollectionFormController() {
		setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("collectionForm");
        setFormView("CollectionPage");
        
	} //- CollectionFormController

	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
		this.myciteseer = myciteseer;
	} //- setMyciteseer

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request) 
		throws Exception {
		CollectionForm form;
		long collectionID;
		
		collectionID = ServletRequestUtils.getLongParameter(request, "cid", -1);

		if (collectionID != -1) {
			// The user wants to edit a collection
			Account account = MCSUtils.getLoginAccount();
			Collection collection = 
				myciteseer.getCollection(collectionID, account);
			form = new CollectionForm(collection);
		}
		else {
			form = new CollectionForm();
			String DOI = request.getParameter("doi");
			
			if (DOI != null && DOI.trim().length() > 0) {
				// The user wants to create a collection and then add a paper.
				form.setAddPaper(true);
				form.setPaperID(DOI);
			}
		}
		return form;
	} //- formBackingObject
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {

		CollectionForm collectionForm = (CollectionForm)command;
		Collection collection = collectionForm.getCollection();
		errors.setNestedPath("collection");
        getValidator().validate(collection, errors);
        errors.setNestedPath("");
	} //- onBindAndValidate
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	protected Map<String, Object> referenceData(HttpServletRequest request)
	throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        
        long collectionID;
		
		collectionID = 
			ServletRequestUtils.getLongParameter(request, "cid", -1);
		if (collectionID != -1) {
			// The user wants to edit a collection
			Account account = MCSUtils.getLoginAccount();
			Collection collection = 
				myciteseer.getCollection(collectionID, account);
			if (collection == null) {
				model.put("error", new Boolean(true));
				model.put("errorMsg", "Invalid Collection Id");
			}
		}

        return model;
        
    }  //- referenceData
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
		
		CollectionForm collectionForm = (CollectionForm)command;
		Account account =  MCSUtils.getLoginAccount();
		
		try {
			if (collectionForm.isNewCollection()) {
				this.myciteseer.addCollection(collectionForm.getCollection());
			}
			else {
				this.myciteseer.updateCollection(collectionForm.getCollection(),
				        account);
			}
		}
		catch (DataAccessException ex) {
			ex.printStackTrace();
			errors.rejectValue("collection.name", "UNKNOWN_ERROR",
                    "An error occurred during the processing of your " +
			        "request. Please try again later.");
            return showForm(request, response, errors);
		}

		if (collectionForm.isAddPaper()) {
			/* The user wants to add a paper. Redirect to the add paper to
			 * collection page
			 */
			return new ModelAndView(new RedirectView("addPaperCollection"), 
			        "doi", collectionForm.getPaperID());
		}
		
		return new ModelAndView(new RedirectView(getSuccessView()));
		
	} //- onSubmit
} //- class CollectionFormController
