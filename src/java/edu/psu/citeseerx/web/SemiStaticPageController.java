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
package edu.psu.citeseerx.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Provides model objects to semi-static pages. Like help, about, etc.
 * The purpose of this controller is to allow configurable title, description, 
 * and keywords tags in the header of each page. The controller will pass all this
 * information to the header view (Same for all those pages). 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class SemiStaticPageController extends ParameterizableViewController {

	private String title;

	/**
	 * @param title for the page
	 */
	public void setTitle(String title) {
		this.title = title;
	} //- setTitle

	private String keywords;
	
	/**
	 * @param keywords to be passed to the view
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	} //- setKeywords

	private String description;
	
	/**
	 * @param description Description of the page
	 */
	public void setDescription(String description) {
		this.description = description;
	} //- setDescription
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, String> model = new HashMap<String, String>();
		model.put("pagetitle", title);
		model.put("pagedescription", description);
		model.put("pagekeywords", keywords);
		return new ModelAndView(getViewName(), model);
	} //-  handleRequestInternal

} //- class SemiStaticPageController
