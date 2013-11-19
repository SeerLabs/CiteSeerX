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
package edu.psu.citeseerx.myciteseer.web.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.psu.citeseerx.myciteseer.domain.PeopleAdvancedSearch;
import edu.psu.citeseerx.webutils.RedirectUtils;

/**
 * Form controller to handle advance search options for People Search.
 * @see org.springframework.web.servlet.mvc.SimpleFormController
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class PeopleAdvancedSearchFormController extends SimpleFormController {
	private String peopleSearchURL = "/myciteseer/search/peoplesearch";

	/**
	 * @param peopleSearchURL URL to PeopleSearch
	 */
	public void setPeopleSearchURL(String peopleSearchURL) {
		this.peopleSearchURL = peopleSearchURL;
	} //- setPeopleSearchURL
	
	public PeopleAdvancedSearchFormController() {
		setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("peopleAdvancedSearch");
        setFormView("MCSAdvancedSearch");
	} //- PeopleAdvancedSearchFormController

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return new PeopleAdvancedSearch();
	} //- formBackingObject

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected void onBindAndValidate(HttpServletRequest request,
			Object command, BindException errors) throws Exception {
		//PeopleAdvancedSearch peopleAdvancedSearch = (PeopleAdvancedSearch)command;
		//getValidator().validate(peopleAdvSearch, errors);
	} //- onBindAndValidate

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		onBindAndValidate(request, command, errors);
        if (errors.hasErrors()) {
            return showForm(request, response, errors);
        }
        PeopleAdvancedSearch peopleAdvancedSearch = 
        	(PeopleAdvancedSearch)command;
        String path = peopleSearchURL + peopleAdvancedSearch.getQuery();
        System.out.println("Select path: " + path);
        RedirectUtils.sendRedirect(request, response, path);
        return null;
	} //- onSubmit
	
	
} //- class PeopleAdvancedSearchFormController
