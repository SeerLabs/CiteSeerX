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
package edu.psu.citeseerx.myciteseer.domain;

import java.io.Serializable;

/**
 * Data transfer object with configuration information.
 * @author Isacc Councill
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class MCSConfiguration implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2496217120735758518L;
    
    private boolean newAccountsEnabled = false;
    
    public boolean getNewAccountsEnabled() {
        return newAccountsEnabled;
    } //- getNewAccountsEnabled
    public void setNewAccountsEnabled(boolean enabled) {
        this.newAccountsEnabled = enabled;
    } //- setNewAccountsEnabled
    
    private boolean urlSubmissionsEnabled = false;

    public boolean getUrlSubmissionsEnabled() {
        return urlSubmissionsEnabled;
    } //- getUrlSubmissionsEnabled
    public void setUrlSubmissionsEnabled(boolean enabled) {
        this.urlSubmissionsEnabled = enabled;
    } //- setUrlSubmissionsEnabled
    
    private boolean correctionsEnabled = false;
    
    public boolean getCorrectionsEnabled() {
        return correctionsEnabled;
    } //- getCorrectionsEnabled
    
    public void setCorrectionsEnabled(boolean enabled) {
        this.correctionsEnabled = enabled;
    } //- setCorrectionsEnabled
    
    private boolean groupsEnabled = false;

	/**
	 * @return the groupsEnabled
	 */
	public boolean getGroupsEnabled() {
		return groupsEnabled;
	} //- getGroupsEnabled
	/**
	 * @param groupsEnabled the groupsEnabled to set
	 */
	public void setGroupsEnabled(boolean groupsEnabled) {
		this.groupsEnabled = groupsEnabled;
	} //- setGroupsEnabled
	
	private boolean peopleSearchEnabled = false;

	/**
	 * @return Informs whether people search is enabled or not within the 
	 * system 
	 */
	public boolean getPeopleSearchEnabled() {
		return peopleSearchEnabled;
	} //- getPeopleSearchEnabled
	
	/**
	 * @param peopleSearchEnabled Indicates if people search is enable or not
	 */
	public void setPeopleSearchEnabled(boolean peopleSearchEnabled) {
		this.peopleSearchEnabled = peopleSearchEnabled;
	} //- setPeopleSearchEnabled
    
	private boolean personalPortalEnabled = true;

	/**
	 * @return Informs is the personal portal is enabled or not
	 */
	public boolean getPersonalPortalEnabled() {
		return personalPortalEnabled;
	}//- getPersonalPortalEnabled
	
	/**
	 * @param personalPortalEnabled Indicates if Personal portal is enabled
	 */
	public void setPersonalPortalEnabled(boolean personalPortalEnabled) {
		this.personalPortalEnabled = personalPortalEnabled;
	} //- setPersonalPortalEnabled
} //- class MCSConfiguration
