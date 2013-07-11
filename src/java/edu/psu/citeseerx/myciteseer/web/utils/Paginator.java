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

import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author Juan Pablo Fernandez Ramirez
 * Utility class that retrieves items to be shown in a page from an
 * ordered collection. If a set of one or more comparators are provided the
 * collection items will be ordered as stated on 
 * <b>org.apache.commons.collections.comparators.ComparatorChain</b>
 * If not the collection will be ordered using the items compareTo method. 
 * @version $rev$ $Date$ 
 */
public class Paginator {
	public static final int DEFAULT_PAGE_SIZE = 20;
	
	private int pageSize;
	private ComparatorChain comparator;
	
	public Paginator() {
		pageSize = DEFAULT_PAGE_SIZE;
		comparator = null;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = (pageSize < 0) ? DEFAULT_PAGE_SIZE : pageSize;
	}

	/**
	 * @param comparator the comparator to set
	 */
	public void setComparator(ComparatorChain comparator) {
		this.comparator = comparator;
	}
	
	/**
	 * Retrieve the page pageNumber from the collection of items. If a comparator
	 * was set up the collection is sorted using it before the page is
	 * generated
	 * @param pageNumber	Page to be generated
	 * @param items			
	 * @return the pageNumber page with its items.
	 */
	public Page fetchPage(int pageNumber, List items) {
		if (comparator != null) {
			// sort the collection using the multisort comparator
			Collections.sort(items, comparator);
		}else{
			Collections.sort(items);
		}
		
		// calculate the number of pages based on pageSize
		int numElements = items.size();
		int totalPages = numElements/pageSize;
		if (numElements > pageSize*totalPages) {
			// One more page is needed
			totalPages++;
		}
		
		// Page creation
		Page page = new Page();
		page.setPageNumber(pageNumber);
		page.setTotalPages(totalPages);
		
		// Obtain the elements to be included in the page.
		int first = (pageNumber-1)*pageSize;
		int last = (first+pageSize) > numElements ? numElements :
			first+pageSize;
		page.getPageContent().addAll(items.subList(first, last));
		
		return page;
	} //- fetchPage
} //- class Paginator
