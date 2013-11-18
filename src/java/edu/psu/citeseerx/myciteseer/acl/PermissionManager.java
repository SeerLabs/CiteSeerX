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
package edu.psu.citeseerx.myciteseer.acl;

import org.springframework.security.acls.Permission;

/**
 * Defines generic methods for ACL manipulation.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public interface PermissionManager {
	/**
	 * Adds a new permission to recipient over domainObject. If domainObject
	 * and/or recipient are new to the ACL system all the necessary information
	 * will be created.
	 * In order to be generic all domainObjects must implement a <b>getId</b>
	 * method which returns the instance identifier in its hosts system. 
	 * The ID needs to be <b>Long or Long compatible</b>. 
	 * @param domainObject Instance being restricted.
	 * @param recipient	   System user name or GrantedAuthority
	 * @param permission   Permission to give to recipient over domainObject.
	 */
	public void addPermission(Object domainObject, String recipient, 
			Permission permission);
	
	/**
	 * Delete a permission recipient has over domainObject. 
	 * In order to be generic all domainObjects must implement a <b>getId</b>
	 * method which returns the instance identifier in its hosts system. 
	 * The ID needs to be <b>Long or Long compatible</b>. 
	 * @param domainObject Object instance subject of permission.
	 * @param recipient    Username or GranthedAuthority
	 * @param permission   Permission to delete
	 */
	public void deletePermission(Object domainObject, String recipient, 
			Permission permission);
	
	/**
	 * Delete all permissions a recipient has over domainObject. 
	 * In order to be generic all domainObjects must implement a <b>getId</b>
	 * method which returns the instance identifier in its hosts system. 
	 * The ID needs to be <b>Long or Long compatible</b>. 
	 * @param domainObject Object instance subject of permission.
	 * @param recipient    Username or GranthedAuthority
	 */
	public void deletePermissions(Object domainObject, String recipient);
	
	/**
	 * Deletes the ACL information related to domainObject
	 * 
	 * In order to be generic all domainObjects must implement a <b>getId</b>
	 * method which returns the instance identifier in its hosts system. 
	 * The ID needs to be <b>Long or Long compatible</b>. 
	 * @param domainObject
	 */
	public void deleteACL(Object domainObject);
	
	/**
	 * Changes the permission recipient has over domainObject.
	 * @param domainObject  Object instance subject of permission.
	 * @param recipient     System user name or GrantedAuthority
	 * @param oldPermission Permission to be replaced
	 * @param newPermission The new permission
	 */
	public void changePermission(Object domainObject, String recipient, 
			Permission oldPermission, Permission newPermission);
	
	/**
	 * Utility method to add Administrator rights over the domain object so
	 * the caller doesn't need to create the permission. If domainObject
	 * and/or recipient are new to the ACL system all the necessary information
	 * will be created.
	 * In order to be generic all domainObjects must implement a <b>getId</b>
	 * method which returns the instance identifier in its hosts system. 
	 * The ID needs to be <b>Long or Long compatible</b>. 
	 * @param domainObject Instance being restricted.
	 * @param recipient	   System user name or GrantedAuthority
	 */
	public void addAdminPermission(Object domainObject, String recipient);
	
	/**
	 * Utility method to add read rights over the domain object so
	 * the caller doesn't need to create the permission. If domainObject
	 * and/or recipient are new to the ACL system all the necessary information
	 * will be created.
	 * In order to be generic all domainObjects must implement a <b>getId</b>
	 * method which returns the instance identifier in its hosts system. 
	 * The ID needs to be <b>Long or Long compatible</b>. 
	 * @param domainObject Instance being restricted.
	 * @param recipient	   System user name or GrantedAuthority
	 */
	public void addReadPermission(Object domainObject, String recipient);
	
	/**
	 * Utility method to add write rights over the domain object so
	 * the caller doesn't need to create the permission. If domainObject
	 * and/or recipient are new to the ACL system all the necessary information
	 * will be created.
	 * In order to be generic all domainObjects must implement a <b>getId</b>
	 * method which returns the instance identifier in its hosts system. 
	 * The ID needs to be <b>Long or Long compatible</b>. 
	 * @param domainObject Instance being restricted.
	 * @param recipient	   System user name or GrantedAuthority
	 */
	public void addWritePermission(Object domainObject, String recipient);
	
	/**
	 * Utility method to add delete rights over the domain object so
	 * the caller doesn't need to create the permission. If domainObject
	 * and/or recipient are new to the ACL system all the necessary information
	 * will be created.
	 * In order to be generic all domainObjects must implement a <b>getId</b>
	 * method which returns the instance identifier in its hosts system. 
	 * The ID needs to be <b>Long or Long compatible</b>. 
	 * @param domainObject Instance being restricted.
	 * @param recipient	   System user name or GrantedAuthority
	 */
	public void addDeletePermission(Object domainObject, String recipient);
	
} //- interface PermissionManager
