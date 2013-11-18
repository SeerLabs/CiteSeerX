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
package edu.psu.citeseerx.myciteseer.web.groups;

import java.io.Serializable;

import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * command object associated with Add Member Page 
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class GroupMemberForm implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -5130768526761141440L;
    private Group group;
	private String firstName = null;
	private String middleName = null;
	private String lastName = null;
	private String[] userIDs;
	private String userid = MCSUtils.getLoginAccount().getUsername();

	public GroupMemberForm() {
		group = new Group();
		userIDs = null;
	} //- GroupMemberForm
	
	public GroupMemberForm(Group group) {
		this.group = group;
		userIDs = null;
	} //- GroupMemberForm
	
	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	} //- getGroup

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	} //- setGroup

	/**
	 * @return the userIDs
	 */
	public String[] getUserIDs() {
		return userIDs;
	} //- getUserIDs

	/**
	 * @param userIDs the userIDs to set
	 */
	public void setUserIDs(String[] userIDs) {
		this.userIDs = userIDs;
	} //- setUserIDs

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * returns if the current user is the owner of the group
	 * in this GroupForm
	 * @return true if the current user is the owner of the group
	 */
	public boolean getIsOwner(){
		return 
			(userid.toLowerCase().compareTo(group.getOwner().toLowerCase()) == 0);
	} //- getIsOwner
	
	/**
	 * @return the user full name
	 */
	public String getFullName() {
		return getFirstName() + " " + getMiddleName() + " " + getLastName();
	}
} //- class GroupMemberForm
