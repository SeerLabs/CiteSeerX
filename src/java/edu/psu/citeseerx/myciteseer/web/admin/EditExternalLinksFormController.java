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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.LinkType;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Processes requests for External Link Types edit/create. Renders the success 
 * view in case of a valid submission or resubmits the form view in case of 
 * errors
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class EditExternalLinksFormController extends SimpleFormController {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO

    public EditExternalLinksFormController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("editLinkType");
        setFormView("admin/editExternalLinks");
    } //- EditExternalLinksFormController
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    protected Object formBackingObject(HttpServletRequest request) 
        throws Exception {

        String label = request.getParameter("id");
        EditExternalLinkForm form = null;
        if (null == label) {
            form = new EditExternalLinkForm();
        }else{
            // Get the External link the user wants.
           LinkType link = csxdao.getLinkType(label);
           form = new EditExternalLinkForm(link);
           form.setOldLabel(link.getLabel());
        }
        form.setLinks(csxdao.getLinkTypes());
        return form;
        
    } //- formBackingObject

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
     */
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {

        EditExternalLinkForm form = (EditExternalLinkForm)command;
        LinkType link = form.getLink();
        errors.setNestedPath("link");
        getValidator().validate(link, errors);
        errors.setNestedPath("");
    } //- onBindAndValidate
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        
        EditExternalLinkForm form = (EditExternalLinkForm)command;
        Account account = MCSUtils.getLoginAccount();
        if (!account.isAdmin()) {
            return new ModelAndView("adminRequired", 
                    new HashMap<String, Object>());
        }
        try {
            if (form.isNewLinkType()) {
                csxdao.addLinkType(form.getLink());
            }else{
                csxdao.updateLinkType(form.getLink(), form.getOldLabel());
            }
        }catch (DataAccessException ex) {
            ex.printStackTrace();
            errors.rejectValue("linkType.label", "UNKNOWN_ERROR",
                    "An error occurred during the processing of your " +
                    "request. Please try again later.");
            return showForm(request, response, errors);
        }
        return new ModelAndView(new RedirectView(getSuccessView()));
    } //- onSubmit
    
} //- class EditExternalLinksFormController
