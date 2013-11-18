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

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.PaperNote;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Processes request for paper notes edit/create. Renders the success view 
 * in case of a valid submission or resubmits the form view in case of errors
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class PaperNoteFormController extends SimpleFormController {

	// MyCiteSeer data access
	private MyCiteSeerFacade myciteseer;
	
	// CiteSeer data access
	private CSXDAO csxdao;
	
	public PaperNoteFormController() {
		setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("paperNoteForm");
        setFormView("paperNotePage");
	} //- PaperNoteFormController
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
		this.myciteseer = myciteseer;
	} //- setMyciteseer
	
	public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
		PaperNoteForm paperNoteForm = (PaperNoteForm)command;
		PaperNote paperNote = paperNoteForm.getPaperNote();
		errors.setNestedPath("paperNote");
        getValidator().validate(paperNote, errors);
        errors.setNestedPath("");
	} //- onBindAndValidate
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
		
		PaperNoteForm paperNoteForm = (PaperNoteForm)command;
		try {
			if (!paperNoteForm.isNewPaperNote()) {
				// Editing note
				myciteseer.updatePaperNote(paperNoteForm.getPaperNote());
			}
			else {
				// Adding new note
				myciteseer.addNoteToPaper(paperNoteForm.getPaperNote());
			}
		}
		catch (DataAccessException ex) {
			ex.printStackTrace();
			errors.rejectValue("paperNote.PID", "PAPER_NOTE_ERROR",
                    "There was an error adding/updating this note to the paper.");
            return showForm(request, response, errors);
		}
		return new ModelAndView(new RedirectView(getSuccessView()), "cid", 
				paperNoteForm.getPaperNote().getCID());
	} //- onSubmit
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object formBackingObject(HttpServletRequest request) 
	throws Exception {
		String paperID;
		long collectionID;
		long paperNoteID;
		
		PaperNoteForm paperNoteForm = null;
		Account account = MCSUtils.getLoginAccount();
		collectionID = ServletRequestUtils.getLongParameter(request, "cid", -1);
		paperID = ServletRequestUtils.getStringParameter(request, "pid", "");
		paperNoteID = ServletRequestUtils.getLongParameter(request, "nid", -1);
		PaperNote paperNote = null;
		if (collectionID != -1 && !paperID.equals("")) {
			if (paperNoteID != -1) {
				// The user wants to edit a note
				try {
					paperNote = myciteseer.getPaperNote(paperNoteID, 
					        collectionID, paperID, account);
					if (paperNote != null) {
						paperNoteForm = new PaperNoteForm(paperNote);
					}else{
						paperNoteForm = new PaperNoteForm();
						paperNote = new PaperNote();
						paperNote.setUID(account.getUsername());
					}
				}
				catch (DataAccessException e) {
					paperNoteForm = new PaperNoteForm();
					paperNote = new PaperNote();
					paperNote.setUID(account.getUsername());
					e.printStackTrace();
				}
			}
			else {
				// User wants to add a note.
				paperNoteForm = new PaperNoteForm();
				paperNote = new PaperNote();
				paperNote.setUID(account.getUsername());
			}
			// Obtain collection and paper data.
			// Obtain the collection.
			Collection collection = null;
			try {
				collection = myciteseer.getCollection(collectionID, account);
			}
			catch (DataAccessException e) {
				e.printStackTrace();
			}
			if (collection != null) {
				paperNote.setCID(collection.getCollectionID());
				paperNoteForm.setCollectionName(collection.getName());
			}
			
			// Obtain paper.
			Document doc = null;
			try {
	            doc = csxdao.getDocumentFromDB(paperID, false, false);
	        } catch (DataAccessException e) {
	            e.printStackTrace();
	        }
	        if (doc != null) {
	        	String paperTitle = 
	        		doc.getDatum(Document.TITLE_KEY);
	        	paperTitle = (paperTitle != null && !paperTitle.matches("^\\s*$")) ?
	        			paperTitle : "unknown";
	        	paperNote.setPID(paperID);
	        	paperNoteForm.setPaperTitle(paperTitle);
	        }
	        paperNoteForm.setPaperNote(paperNote);
		}
		return paperNoteForm;
	} //- BackingObject
} //- class PaperNoteFormController
