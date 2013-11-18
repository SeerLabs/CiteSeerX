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

import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.web.utils.MCSConstants;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Collection creation/editing form validation utility.
 *
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class CollectionValidator implements Validator {

	private MyCiteSeerFacade myciteseer;
	
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    } //- setMessageSource


    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	
    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
    	return Collection.class.isAssignableFrom(clazz);
	} //- supports

	
    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {
		Collection collection = (Collection)obj;
		Account account = MCSUtils.getLoginAccount();
		validateName(collection, account, errors);
		validateDescription(collection.getDescription(), errors);
	} //- validate

    public void validateName(Collection collection, Account account, Errors errors) {
    	ValidationUtils.rejectIfEmpty(errors, "name",
                "CNAME_REQUIRED", "A collection name is required.");
 
    	if (myciteseer.getCollectionAlreadyExists(collection, account)) {
    		errors.rejectValue("name", "NEW_CNAME_REQUIRED",
    				"You already have a collection with this name.");
    	}
    	
    	if (collection.getName().length() > MCSConstants.MAX_CNAME_LENGTH) {
    		errors.rejectValue("name", "BAD_COLLECTION_NAME",
                    "Supplied name is too long.");
    	}
    } //- validateName
    
    public void validateDescription(String description, Errors errors) {
    	if (description.length() > MCSConstants.MAX_VARCHAR_LENGTH) {
    		errors.rejectValue("description", "BAD_COLLECTION_DESCRIPTION",	
    				"Supplied description is too long.");
    	}
    	
    } //- validateDescription
    
    public String getInputFieldValidationMessage(String formInputId,
            String formInputValue) {
        String validationMessage = "";

        try {
            Object formBackingObject = new Collection();
            Errors errors = new BindException(formBackingObject, "command");

            formInputId = formInputId.split("\\.")[1];
            String capitalizedFormInputId = StringUtils.capitalize(formInputId);

            String accountMethodName = "set" + capitalizedFormInputId;
            Class setterArgs[] = new Class[] { String.class };
            Method accountMethod =
                formBackingObject.getClass().getMethod(accountMethodName, setterArgs);
            accountMethod.invoke(formBackingObject, new Object[] { formInputValue });

            String validationMethodName = "validate" + capitalizedFormInputId;
            Class validationArgs[] = new Class[] { String.class, Errors.class };
            Method validationMethod = getClass().getMethod(validationMethodName,
                    validationArgs);
            validationMethod.invoke(this, new Object[] { formInputValue, errors });

            validationMessage = getValidationMessage(errors, formInputId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return validationMessage;

    }  //- getInputFieldValidationMessage
    
    
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
} //- CollectionValidator
