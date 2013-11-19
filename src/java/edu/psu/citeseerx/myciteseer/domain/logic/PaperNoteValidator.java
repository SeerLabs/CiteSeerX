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
package edu.psu.citeseerx.myciteseer.domain.logic;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.domain.PaperNote;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Paper note creation/editing form validation utility.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class PaperNoteValidator implements Validator {

	// MyCiteSeer data access
	private MyCiteSeerFacade myciteseer;
	
	private MessageSource messageSource;
	
	// CiteSeer data access
	private CSXDAO csxdao;
	
	public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    } //- setMessageSource
	
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {
		return PaperNote.class.isAssignableFrom(clazz);
	} //- supports

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object obj, Errors errors) {
		PaperNote paperNote = (PaperNote)obj;
		validateNote(paperNote.getNote(), errors);
		validatePID(paperNote.getPID(), errors);
		validateCID(paperNote.getCID(), errors);
		
		// Is the paper in that collection?
		try {
			Account account = MCSUtils.getLoginAccount();
			PaperCollection paperCollection = new PaperCollection();
			paperCollection.setCollectionID(paperNote.getCID());
			paperCollection.setPaperID(paperNote.getPID());
			paperCollection.setUID(paperNote.getUID());
			if (!myciteseer.isPaperInCollection(
					paperCollection, account)) {
				errors.rejectValue("PID", "NOT_IN_COLLECTION", 
						"The specified paper is not part of the specified collection a note" +
						" can not be added.");
			}
		}
		catch (DataAccessException ex) {
            ex.printStackTrace();
            errors.rejectValue("PID", "NOT_IN_COLLECTION", 
            		"The specified paper is not part of the specified collection a note" +
					" can not be added.");
        }
	} //- validate

	public void validateCID(long CID, Errors errors) {
		Account account = MCSUtils.getLoginAccount();
		
		ValidationUtils.rejectIfEmpty(errors, "CID",
	            "COLLECTION_REQUIRED", "Please specify a collection.");
		if (!myciteseer.isUserCollection(CID, account)) {
			errors.rejectValue("CID", "INVALID_COLLECTION", 
					"The specified collection doesn't exists.");
		}
	} //- validateCID
	
	public void validatePID(String PID, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "PID",
                "PAPER_REQUIRED", "Please specify a paper.");
		if (PID == null) {
			errors.rejectValue("PID", "PAPER_REQUIRED", 
					"Please specify a paper.");
		}
		else {
			// does the paper exists?
			Document doc = null;
			try {
	            doc = csxdao.getDocumentFromDB(PID, false, false);
	        } catch (Exception e) {
	            e.printStackTrace();
	            errors.rejectValue("PID", "INVALID_DOI", 
					"The specified paper doesn't exists.");
	        }
	        if (doc == null) {
	        	errors.rejectValue("PID", "INVALID_DOI", 
	        			"The specified paper doesn't exists.");
	        }
		}
	} //- validatePID
	
	public void validateNote(String note, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "note", "NOTE_REQUIRED", "You have to provide a note.");
	} //- validateNote
	
	protected String getValidationMessage(Errors errors, String fieldName) {

        String message = "";
        FieldError fieldError = errors.getFieldError(fieldName);

        if (fieldError != null) {
            message = messageSource.getMessage(fieldError.getCode(), null,
                    "This field is invalid",
                    Locale.ENGLISH);
        }
        return message;
    }  //- getValidationMessage
	
}
