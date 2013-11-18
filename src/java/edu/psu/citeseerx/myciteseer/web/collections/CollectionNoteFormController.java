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
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.CollectionNote;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Processes request for collection notes edit/create. Renders the success view 
 * in case of a valid submission or resubmits the form view in case of errors
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CollectionNoteFormController extends SimpleFormController {

	// MyCiteSeer data access
	private MyCiteSeerFacade myciteseer;
	
	public CollectionNoteFormController() {
		setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("collectionNoteForm");
        setFormView("notePage");
	} //- CollectionNoteFormController
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
		this.myciteseer = myciteseer;
	} //- setMyciteseer
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request) 
	throws Exception {
		
		Account account = MCSUtils.getLoginAccount();
		CollectionNoteForm collectionNoteForm = null;
		
		long collectionID = ServletRequestUtils.getLongParameter(request, "cid",
		        -1);
		long collectionNoteID = 
			ServletRequestUtils.getLongParameter(request, "nid", -1);

		if (collectionID != -1) {
			// Obtain the collection.
			Collection collection = null;
			try {
				collection = myciteseer.getCollection(collectionID, account);
			}
			catch (DataAccessException e) {
				e.printStackTrace();
			}
			if (collection != null) {
				CollectionNote collectionNote;
				
				if (collectionNoteID == -1) {
					/* The collection exists, it's owned by the user and
					 * he/she wants to add a new note.
					 */
					collectionNote = new CollectionNote();
					collectionNote.setCollectionID(collection.getCollectionID());
					collectionNote.setUID(account.getUsername());
					collectionNoteForm = new CollectionNoteForm();
					collectionNoteForm.setCollectionNote(collectionNote);
				}
				else {
					/*
					 * The collection exists, it's owned by the user and
					 * he/she want to update the note.
					 */
					try {
						collectionNote = 
							myciteseer.getCollectionNote(collectionNoteID, 
									collection.getCollectionID(), account);
						if (collectionNote != null) {
							// Note not found or it's not a note related to 
							collectionNoteForm = new CollectionNoteForm(collectionNote);
						}
						else {
							collectionNote = new CollectionNote();
							collectionNote.setUID(account.getUsername());
							collectionNoteForm = new CollectionNoteForm();
							collectionNoteForm.setCollectionNote(collectionNote);
						}
					}
					catch (DataAccessException e) {
						collectionNote = new CollectionNote();
						collectionNote.setUID(account.getUsername());
						collectionNoteForm = new CollectionNoteForm();
						collectionNoteForm.setCollectionNote(collectionNote);
						e.printStackTrace();
					}
				}
				collectionNoteForm.setCollectionName(collection.getName());
			}
			else {
				/* There is no collection. The user change the collection ID 
				 * in the URL but either the collection doesn't exists or it's
				 * not owned by the user.
				 */ 
				collectionNoteForm = new CollectionNoteForm();
			}
		}
		return collectionNoteForm;
	} //- formBackingObject
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {

		CollectionNoteForm collectionNoteForm = (CollectionNoteForm)command;
		CollectionNote collectionNote = collectionNoteForm.getCollectionNote();
		errors.setNestedPath("collectionNote");
        getValidator().validate(collectionNote, errors);
        errors.setNestedPath("");
	} //- onBindAndValidate
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
		
		Account account = MCSUtils.getLoginAccount();
		CollectionNoteForm collectionNoteForm = (CollectionNoteForm)command;
		try {
			if (collectionNoteForm.isNewCollectionNote()) {
				myciteseer.addNoteToCollection(
				        collectionNoteForm.getCollectionNote());
			}
			else {
				myciteseer.updateCollectionNote(
						collectionNoteForm.getCollectionNote(),
						account);
			}
		}
		catch (DataAccessException ex) {
			ex.printStackTrace();
			errors.rejectValue("collectionNote.collectionID", "COLLECTION_ERROR",
                    "There was an error adding this note to the collection.");
            return showForm(request, response, errors);
		}
		return new ModelAndView(new RedirectView(getSuccessView()), "cid", 
				collectionNoteForm.getCollectionNote().getCollectionID());
	} //- onSubmit
} //- CollectionNoteFormController
