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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Account creation/editing form validation utility.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class AccountValidator implements Validator {

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
        return Account.class.isAssignableFrom(clazz);
    } //- supports


    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {

        Account account = (Account)obj;

        validateFirstName(account.getFirstName(), errors);
        validateMiddleName(account.getMiddleName(), errors);
        validateLastName(account.getLastName(), errors);
        validateEmail(account.getEmail(), errors);
        validateAffiliation1(account.getAffiliation1(), errors);
        validateWebPage(account.getWebPage(), errors);
        validateCountry(account.getCountry(), errors);

    }  //- validate

    /**
     * Validates the username 
     * @param username Username to be validated
     * @param errors Contains any error that might occur 
     */
    public void validateUsername(String username, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username",
                "USERNAME_REQUIRED",
        "Username is required.");
        if (errors.getAllErrors().size() == 0) {
            try {
                Account existingAccount = myciteseer.getAccount(username);
                if (existingAccount != null) {
                    errors.rejectValue("username", "USERNAME_UNIQUE_REQUIRED",
                    "This user ID is already taken.");
                }
            } catch (Exception e) { /* ignore */ }
        }

    }  //- validateUsername

    /**
     * Email validation
     * @param email email to be validated
     * @param errors Contains any error that might occur
     */
    public void validateEmail(String email, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
                "EMAIL_REQUIRED", "Email address is required.");
        if (errors.getAllErrors().size() > 0) {
            return;
        }        
        try {
            new InternetAddress(email);
            if (!hasNameAndDomain(email)) {
                errors.rejectValue("email", "VALID_EMAIL_REQUIRED",
                "Invalid email address");
            }
        } catch (AddressException e) {
            errors.rejectValue("email", "VALID_EMAIL_REQUIRED",
            "Invalid email address");            
        }
        if (errors.getAllErrors().size() > 0) {
            return;
        }
        try {
            Account loginAccount = MCSUtils.getLoginAccount();
            Account existingAccount = myciteseer.getAccountByEmail(email);
            if (existingAccount != null) {
                if ((loginAccount == null) ||
                        !loginAccount.getUsername().equals(
                                existingAccount.getUsername())) {
                    errors.rejectValue("email", "EMAIL_UNIQUE_REQUIRED",
                    "This email address is already in use.");                    
                }
            }
        } catch (Exception e) { /* ignore */ }
        
    }  //- validateEmail
    
    
    private static boolean hasNameAndDomain(String emailAddress) {
        String[] tokens = emailAddress.split("@");
        return (tokens.length == 2 &&
                tokens[0].length() > 0 &&
                tokens[1].length() > 0);
        
    }  //- hasNameAndDomain


    /**
     * Produces and error if first name is empty
     * @param firstName
     * @param errors
     */
    public void validateFirstName(String firstName, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "firstName",
                "FIRST_NAME_REQUIRED",
                "First name is required.");
    } //- validateFirstName


    public void validateMiddleName(String firstName, Errors errors) {}

    /**
     * Produces and error if the last name is empty 
     * @param lastName
     * @param errors
     */
    public void validateLastName(String lastName, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "lastName",
                "LAST_NAME_REQUIRED",
                "Last name is required.");
    } //- validateLastName

    /**
     * Produces and error if affiliation1 is empty
     * @param affiliation1
     * @param errors
     */
    public void validateAffiliation1(String affiliation1, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "affiliation1",
                "AFFILIATION_REQUIRED", "Affiliation is required.");
    } //- validateAffiliation1
    
    /**
     * Validates that webPage is a valid URL
     * @param webPage
     * @param errors
     */
    public void validateWebPage(String webPage, Errors errors) {
        if (webPage == null || webPage.length() <= 0) {
            return;
        }
        try {
            new URL(webPage);
        } catch (MalformedURLException e) {
            errors.rejectValue("webPage", "INVALID_URL",
                    "Invalid url.  Did you include the protocol " +
                    "(e.g., http://)?");
        }
        
    }  //- validateWebPage
    
    /**
     * Produces and error if country is empty
     * @param country
     * @param errors
     */
    public void validateCountry(String country, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "country", "COUNTRY_REQUIRED", "Country is required.");
    } //- validateCountry


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
                formBackingObject.getClass().getMethod(accountMethodName,
                        setterArgs);
            accountMethod.invoke(formBackingObject,
                    new Object[] { formInputValue });

            String validationMethodName = "validate" + capitalizedFormInputId;
            Class validationArgs[] = new Class[] { String.class, Errors.class };
            Method validationMethod = getClass().getMethod(validationMethodName,
                    validationArgs);
            validationMethod.invoke(this,
                    new Object[] { formInputValue, errors });

            validationMessage = getValidationMessage(errors, formInputId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return validationMessage;

    }  //- getInputFieldValidationMessage


    /**
     * @param errors
     * @param fieldName
     * @return The error from the message source for fieldName if any
     */
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

}  //- class AccountValidator
