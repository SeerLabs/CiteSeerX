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
package edu.psu.citeseerx.myciteseer.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSConstants;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;


/**
 * Processes request for password edition rendering the success view in
 * case of a valid submission or resubmits the form view in case of errors
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ChangePasswordController extends SimpleFormController {
    
    public ChangePasswordController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("changePasswordForm");
        setFormView("ChangePasswordForm");
    } //- ChangePasswordController

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        Account account = MCSUtils.getLoginAccount();
        return new ChangePasswordForm(account);
        
    }  //- formBackingObject
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
     */
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        
        ChangePasswordForm passForm = (ChangePasswordForm) command;
        Account account = passForm.getAccount();
        
        //errors.setNestedPath("account");
        //getValidator().validate(account, errors);
        errors.setNestedPath("");
        
        String encodedPasswordSupplied =
            encodePassword(account, passForm.getSuppliedPassword());
        
        ValidationUtils.rejectIfEmpty(errors,
                "suppliedPassword",
                "PASSWORD_REQUIRED", "Password is required.");
        ValidationUtils.rejectIfEmpty(errors,
                "newPassword",
                "NEW_PASSWORD_REQUIRED", "New password is required.");
        ValidationUtils.rejectIfEmpty(errors,
                "repeatedPassword",
                "REPEATED_PASSWORD_REQUIRED", "Repeated password is required.");
        
        if (passForm.getNewPassword().length() <
                MCSConstants.MIN_PASSWD_LENGTH) {
            errors.rejectValue("newPassword", "BAD_PASSWORD",
                    "New password is too short.");
        } else if (account.getPassword() == null ||
                account.getPassword().length() < 1) {
            errors.reject("INVALID_PASSWORD", "Invalid session password.");
        } else if (!account.getPassword().equals(encodedPasswordSupplied)) {
            errors.rejectValue("suppliedPassword","BAD_PASSWORD",
                    "Supplied password is incorrect.");
        } else if (passForm.getNewPassword() == null ||
                account.getPassword().length() < 1 ||
                !passForm.getNewPassword().equals(
                        passForm.getRepeatedPassword())) {
            errors.rejectValue("repeatedPassword",
                    "PASSWORD_MISMATCH",
                    "New password fields did not match or were not provided.");
        }
        
    }  //- onBindAndValidate
    
    
    protected Map<Object, Object> referenceData(HttpServletRequest request)
    throws Exception {
        Map<Object, Object> model = new HashMap<Object, Object>();
        return model;
        
    }  //- referenceData
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        
        ChangePasswordForm passForm = (ChangePasswordForm)command;
        
        Account account = passForm.getAccount();
        account.setPassword(passForm.getNewPassword());
        encodePassword(account);
        this.myciteseer.changePassword(account);
        return super.onSubmit(request, response, command, errors);
        
    }  //- onSubmit
    
    
    private SaltSource saltSource;
    
    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    } //- setSaltSource
    
    private PasswordEncoder passwordEncoder;
    
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    } //- setPasswordEncoder
    
    private void encodePassword(Account account) {
        Object salt = saltSource.getSalt(account);
        String encodedPassword =
            passwordEncoder.encodePassword(account.getPassword(), salt);
        account.setPassword(encodedPassword);

    }  //- encodePassword
    
    private String encodePassword(Account account, String password) {
        Object salt = saltSource.getSalt(account);
        String encodedPassword =
            passwordEncoder.encodePassword(password, salt);
        return encodedPassword;

    }  //- encodePassword
    
}  //- class ChangePasswordController
