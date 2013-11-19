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
import java.util.Comparator;

/**
 * Data transfer object with a group member information.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class GroupMember implements Comparable<GroupMember>, Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -2459970486455929214L;
    
    private long groupId;
	private boolean validating;
	private Account member;
	
	/**
	 * @return the groupId
	 */
	public long getGroupId() {
		return groupId;
	} //- getGroupId
	
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	} //- setGroupId
	
	/**
	 * @return the validating
	 */
	public boolean getValidating() {
		return validating;
	} //- getValidating
	
	/**
	 * @param validating the validating to set
	 */
	public void setValidating(boolean validating) {
		this.validating = validating;
	} //- setValidating
	
	/**
	 * @return the member
	 */
	public Account getMember() {
		return member;
	} //- getMember
	
	/**
	 * @param member the member to set
	 */
	public void setMember(Account member) {
		this.member = member;
	} //- setMember

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(GroupMember anotherGroupMember) {
		String memberName = member.getFirstName() + " " + 
			member.getMiddleName() + " " + member.getLastName();
		
		Account anotherMember = anotherGroupMember.getMember();
		String anotherName = anotherMember.getFirstName() + " " + 
			anotherMember.getMiddleName() + " " + anotherMember.getLastName();
		
		return memberName.toLowerCase().compareTo(anotherName.toLowerCase());
	} //- compareTo
	
	public static Comparator<GroupMember> memberComparator = 
		new Comparator<GroupMember>() {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(GroupMember groupMember, 
				GroupMember anotherGroupMember) {
			return groupMember.compareTo(anotherGroupMember);
		} //- compare
	}; //- comparator
	
} //- class GroupMember
