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
package edu.psu.citeseerx.domain;

import java.util.Date;

/**
 * Utility class to transport some DOI related information.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DOIInfo {
	private String doi;
	private Date modifiedDate;
	
	/**
	 * @return the string identifier
	 */
	public String getDoi() {
		return doi;
	} //- getDoi
	
	/**
	 * @param doi the identifier
	 */
	public void setDoi(String doi) {
		this.doi = doi;
	} //- setDoi
	
	/**
	 * @return the date the object represented by this DOI was
	 * created/modified
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	} //- getModifiedDate
	
	/**
	 * @param modifiedDate the date the object represented by this DOI was
	 * created/modified
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	} //- setModifiedDate

} //- class DOIInfo
