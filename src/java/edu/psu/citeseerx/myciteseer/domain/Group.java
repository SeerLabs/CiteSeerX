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
 * Data transfer object with group information.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class Group implements Comparable<Group>, Serializable {
	
	/**
     * 
     */
    private static final long serialVersionUID = -5537469221793120979L;
    
    private long id;
	private String name;
	private String description;
	private String owner;
	private String authority;

	/**
	 * @return returns the storage id for this group
	 */
	public long getId() {
		return id;
	} //- getId

	/**
	 * @param id sets the storage id for this group
	 */
	public void setId(long id) {
		this.id = id;
	} //- setId

	/**
	 * @return groups name
	 */
	public String getName() {
		return name;
	} //- getName

	/**
	 * @param name name of this group 
	 */
	public void setName(String name) {
		this.name = name;
	} //- setName

	/**
	 * @return group's description
	 */
	public String getDescription() {
		return description;
	} //- getDescription

	/**
	 * @param description Description about the group
	 */
	public void setDescription(String description) {
		this.description = description;
	} //- setDescription

	/**
	 * @return the owner of this group
	 */
	public String getOwner() {
		return owner;
	} //- getOwner

	/**
	 * @param owner Sets the owner of the group
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	} //- setOwner

	/**
	 * @return the spring's authority string for this group
	 */
	public String getAuthority() {
		return authority;
	} //- getAuthority

	/**
	 * @param authority String representing this group as an authority in spring
	 */
	public void setAuthority(String authority) {
		this.authority = authority;
	} //- setAuthority

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Group anotherGroup)  throws ClassCastException {
		if (!(anotherGroup instanceof Group)) {
			throw new ClassCastException("Group object expected.");
		}
		String anotherName = anotherGroup.getName().toUpperCase();
		return this.name.toUpperCase().compareTo(anotherName);
	} //- compareTo

	public static Comparator<Group> nameComparator = new Comparator<Group>() {
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Group group, Group anotherGroup) {
			String name = group.getName().toLowerCase();
			String otherName = anotherGroup.getName().toLowerCase();
			return name.compareTo(otherName);
		} //- compare
	}; //- class Comparator (Group)
	
} //- Class Group
