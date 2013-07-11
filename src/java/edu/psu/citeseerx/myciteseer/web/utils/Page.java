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
package edu.psu.citeseerx.myciteseer.web.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a set of items intented to be shown in a page
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class Page {
	private int pageNumber;
	private int totalPages;
	private List pageContent = new ArrayList();
	
	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}
	/**handleRequest
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	/**
	 * @return the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}
	/**
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	/**
	 * @return the pageContent
	 */
	public List getPageContent() {
		return pageContent;
	}
	/**
	 * @param pageContent the pageContent to set
	 */
	public void setPageContent(List pageContent) {
		this.pageContent = pageContent;
	}
} // class Page
