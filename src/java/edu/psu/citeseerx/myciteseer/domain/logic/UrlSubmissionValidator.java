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
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.validation.*;

import edu.psu.citeseerx.myciteseer.domain.*;

/**
 * URLsubmission creation/editing form validation utility.
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class UrlSubmissionValidator implements Validator {

    private MyCiteSeerFacade myciteseer;
 
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    private MessageSource messageSource;
    

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    } //- setMessageSource

    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return UrlSubmission.class.isAssignableFrom(clazz);
    } //- supports


    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {
        UrlSubmission submission = (UrlSubmission)obj;
        validateUrl(submission.getUrl(), submission.getUsername(), errors);
    } //- validate


    public void validateUrl(String url, String userid, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "url",
                "URL_REQUIRED", "Please specify a URL.");
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            errors.rejectValue("url", "URL_SUBMISSION_ERROR", "Invalid URL.");
        }
        if (errors.getAllErrors().size() == 0) {
            if (myciteseer.isUrlAlreadySubmitted(url, userid)) {
                errors.rejectValue("url", "URL_NEW_REQUIRED",
                        "You have already submitted this url.");
            }
        }
        
    }  //- validateUrl


    public String getInputFieldValidationMessage(String formInputId,
            String formInputValue) {
        String validationMessage = "";

        try {
            Object formBackingObject = new Account();
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
    
}  //- class UrlSubmissionValidator
