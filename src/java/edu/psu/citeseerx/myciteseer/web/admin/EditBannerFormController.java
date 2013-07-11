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
package edu.psu.citeseerx.myciteseer.web.admin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.dao2.logic.CSXDAO;


/**
 * Processes request for Banner edit/create. Renders the success view in
 * case of a valid submission or resubmits the form view in case of errors
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class EditBannerFormController extends SimpleFormController {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    public EditBannerFormController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("editBannerForm");
        setFormView("admin/editBanner");
        
    } //- EditBannerFormController


    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    protected Object formBackingObject(HttpServletRequest request) 
        throws Exception {

        EditBannerForm form = new EditBannerForm();
        form.setBanner(csxdao.getBanner());
        return form;
        
    } //- formBackingObject
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
     */
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {

    } //- onBindAndValidate
    

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
     */
    protected Map<String, Object> referenceData(HttpServletRequest request)
    throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        return model;
        
    }  //- referenceData
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        
        EditBannerForm editBannerForm = (EditBannerForm)command;
        Account account = MCSUtils.getLoginAccount();
        if (!account.isAdmin()) {
            return new ModelAndView("adminRequired", 
                    new HashMap<String, Object>());
        }
        csxdao.setBanner(editBannerForm.getBanner());
        return new ModelAndView(new RedirectView(getSuccessView()));
        
    } //- onSubmit
    
} //- class EditBannerFormController
