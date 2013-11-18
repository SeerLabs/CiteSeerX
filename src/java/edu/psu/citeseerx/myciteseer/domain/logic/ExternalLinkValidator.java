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

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.LinkType;
import edu.psu.citeseerx.myciteseer.web.utils.MCSConstants;

/**
 * External link types creation/editing form validation utility.
 *
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ExternalLinkValidator implements Validator {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private MessageSource messageSource;
    
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    } //- setMessageSource
    
    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return LinkType.class.isAssignableFrom(clazz);
    } //- supports

    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {
        LinkType link = (LinkType)obj;
        validateBaseURL(link.getBaseURL(), errors);
        validateLabel(link.getLabel(), errors);
    } //- validate

    public void validateBaseURL(String url, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "baseURL",
                "BASE_URL_REQUIRED", "Please specify a Base URL.");
        if (url.length() > MCSConstants.MAX_URL) {
            errors.rejectValue("url", "BAD_ELINK_URL",
                    "Supplied name is too long.");
        }
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            errors.rejectValue("baseURL", "BURL_MALFORMED", "Invalid URL.");
        }
    } //- validateBaseURL
    
    public void validateLabel(String label, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "label",
                "LABEL_REQUIRED", 
                "Please give a name for the External Link type.");
        if (label.length() > MCSConstants.MAX_ELINK_LABEL_LENGTH) {
            errors.rejectValue("label", "BAD_ELINK_NAME",
            "Supplied name is too long.");
        }
        if (csxdao.getLinkType(label) != null) {
            errors.rejectValue("label", "NEW_LABEL_REQUIRED",
                    "A external link with that name already exist.");
        }
    } //- validateLabel
} //- class ExternalLinkValidator
