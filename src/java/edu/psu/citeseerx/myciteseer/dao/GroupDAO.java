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
package edu.psu.citeseerx.myciteseer.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.GroupMember;

/**
 * Interface that defines the operations to obtain and store group related 
 * information independently of the data source.
 * 
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public interface GroupDAO {
	/**
	 * Stores a new group
	 * @param group		Information to be stored
	 * @throws DataAccessException
	 */
	Group addGroup(Group group) throws DataAccessException;
	
	/**
	 * Adds a new user to the group
	 * @param group	Group to add the user to
	 * @param userid user to be added to the group
	 * @param validating	indicate if the user has to be validated
	 * @throws DataAccessException
	 */
	void addMember(Group group, String userid, boolean validating) 
	throws DataAccessException;
	
	/**
	 * Validates if the group name already exist in the user groups list
	 * @param group		Group which name needs to be validated
	 * @param account	Owner of the group
	 * @return	True if the groups name already exists false otherwise
	 * @throws DataAccessException
	 */
	boolean isNameRepeated(Group group, Account account)
	throws DataAccessException;
	
	/**
	 * Obtains information for group represented by groupID
	 * @param groupID	Group to be retrieved
	 * @return	The group if groupID identifies a group owned by the user
	 * represented by account
	 * @throws DataAccessException
	 */
	Group getGroup(long groupID) throws DataAccessException;
	
	/**
	 * Get the groups the user owns or is a member of
	 * @param username
	 * @return A List of Group objects representing each group the user
	 * owns or is member of
	 * @throws DataAccessException
	 */
	List<Group> getGroups(String username) throws DataAccessException;

	/**
	 * Deletes a user group.
	 * @param group	Group to be deleted
	 * @throws DataAccessException
	 */
	void deleteGroup(Group group) throws DataAccessException;
	
	/**
	 * List the members of a given group
	 * @param group	The group to list its members
	 * @return	A list with the members of the group.
	 * @throws DataAccessException
	 */
	List<GroupMember>getMembers(Group group) throws DataAccessException;
	
	/**
	 * Updates the group with new information
	 * @param group		Information to be updated
	 * @throws DataAccessException
	 */
	void updateGroup(Group group) throws DataAccessException;
	
	/**
	 * Removes a user from a group
	 * @param group		Group to delete the user from
	 * @param userid	User to be deleted from group
	 * @throws DataAccessException
	 */
	void leaveGroup(Group group, String userid)
	throws DataAccessException;
	
	/**
	 * Removes a user from a group. Similar to @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#removeMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 * but requires different user permissions
	 * @param group		Group to delete the user from
	 * @param userid	User to be deleted from group
	 * @throws DataAccessException
	 */
	void removeMember(Group group, String userid)
	throws DataAccessException;
	
	/**
	 * Approves a user to be part of a group
	 * @param group
	 * @param userid
	 * @throws DataAccessException
	 */
	void validateUser(Group group, String userid) throws DataAccessException;
	
	/**
	 * Returns all the authorities a user have as a result of being part of
	 * a group
	 * @param username
	 * @return A list of user GrantedAuthority inherited from groups the user
	 * is member of 
	 * @throws DataAccessException
	 */
	List<GrantedAuthority> getGroupAuthorities(String username) 
	throws DataAccessException;
	
	/**
	 * Validates if the user is part of the group
	 * @param group
	 * @param userid
	 * @return true if userid is part of group, false otherwise.
	 * @throws DataAccessException
	 */
	boolean isMember(Group group, String userid) throws DataAccessException;
	
} //- interface GroupDAO
