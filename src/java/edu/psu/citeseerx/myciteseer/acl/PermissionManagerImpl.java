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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;

/**
 * PermissionManager Implementation 
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class PermissionManagerImpl implements PermissionManager {

	// Allow us to have access to ACL's for given objects.
	private MutableAclService aclService;
	
	/**
	 * @param aclService An object implementing MutableAcl interface
	 */
	public void setAclService(MutableAclService aclService) {
		this.aclService = aclService;
	} //- setAclService
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#addPermission(java.lang.Object, java.lang.String, org.springframework.security.acls.Permission)
	 */
	public void addPermission(Object domainObject, String recipient,
			Permission permission) {
		
		try {
			// Getting domainObject ID.
			Method idMethod = domainObject.getClass().getMethod("getId", 
					new Class[0]);
			Long objectID = (Long)idMethod.invoke(domainObject, new Object[0]);
			
			// Represent domainObject in ACL system.
			ObjectIdentity oi = new ObjectIdentityImpl(
					domainObject.getClass().getName(), objectID);
			
			// Obtain the object's ACL or create if doesn't exists
			MutableAcl acl;
			try {
				acl = (MutableAcl)aclService.readAclById(oi);
			}catch (NotFoundException nfe) {
	            acl = aclService.createAcl(oi);
	        }
	
			// Obtain recipient representation in ACL system.
			Sid sid = new PrincipalSid(recipient);
			
			// Add permission to the ACL, and store it.
			acl.insertAce(acl.getEntries().length, permission, sid, true);
			aclService.updateAcl(acl);
		}catch (NoSuchMethodException nme) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (InvocationTargetException ite) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (IllegalAccessException iae) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}
	} //- addPermission

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#changePermission(java.lang.Object, java.lang.String, org.springframework.security.acls.Permission)
	 */
	public void changePermission(Object domainObject, String recipient,
			Permission oldPermission, Permission newPermission) {
		try {
			// Getting domainObject ID.
			Method idMethod = domainObject.getClass().getMethod("getId", 
					new Class[0]);
			Long objectID = (Long)idMethod.invoke(domainObject, new Object[0]);
			
			// Represent domainObject in ACL system.
			ObjectIdentity oi = new ObjectIdentityImpl(
					domainObject.getClass().getName(), objectID);
			
			// Obtain the object's ACL or create if doesn't exists
			MutableAcl acl = (MutableAcl) aclService.readAclById(oi);

			// Represent recipient in ACL system.
			Sid sid = new PrincipalSid(recipient);
			
			// Find the permission given to this recipient.
			 AccessControlEntry[] entries = acl.getEntries();
			 boolean found = false;
			 for (int i = 0; i < entries.length && !found; ++i) {
				 if (entries[i].getSid().equals(sid) &&
						entries[i].getPermission().equals(oldPermission)) {
					 acl.updateAce(i, newPermission);
					 found = true;
				 }
			 }
			 if (found) {
				 aclService.updateAcl(acl);
			 }
		}catch (NoSuchMethodException nme) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (InvocationTargetException ite) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (IllegalAccessException iae) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		} //- changePermission

	} //- changePermission

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#deleteACL(java.lang.Object)
	 */
	public void deleteACL(Object domainObject) {
		try {
			// Getting domainObject ID.
			Method idMethod = domainObject.getClass().getMethod("getId", 
					new Class[0]);
			Long objectID = (Long)idMethod.invoke(domainObject, new Object[0]);
			
			// Represent domainObject in ACL system.
			ObjectIdentity oi = new ObjectIdentityImpl(
					domainObject.getClass().getName(), objectID);
			
			// delete the acl.
			aclService.deleteAcl(oi, false);
		}catch (NoSuchMethodException nme) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (InvocationTargetException ite) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (IllegalAccessException iae) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}

	} //- deleteACL

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#deletePermission(java.lang.Object, java.lang.String, org.springframework.security.acls.Permission)
	 */
	public void deletePermission(Object domainObject, String recipient,
			Permission permission) {
		try {
			// Getting domainObject ID.
			Method idMethod = domainObject.getClass().getMethod("getId", 
					new Class[0]);
			Long objectID = (Long)idMethod.invoke(domainObject, new Object[0]);
			
			// Obtain ACL representation of domain object.
			ObjectIdentity oi = new ObjectIdentityImpl(
					domainObject.getClass().getName(), objectID);
			
			// Obtain the object's ACL.
			MutableAcl acl = (MutableAcl) aclService.readAclById(oi);

			// Obtain ACL representation recipient
			Sid sid = new PrincipalSid(recipient);
			
			// Find the permission given to this recipient.
			 AccessControlEntry[] entries = acl.getEntries();
			 boolean found = false;
			 for (int i = 0; i < entries.length && !found; ++i) {
				 if (entries[i].getSid().equals(sid) &&
						entries[i].getPermission().equals(permission)) {
					 acl.deleteAce(i);
					 found = true;
				 }
			 }
			 if (found) {
				 aclService.updateAcl(acl);
			 }
		}catch (NoSuchMethodException nme) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (InvocationTargetException ite) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (IllegalAccessException iae) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}

	} //- deletePermission
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#deletePermissions(java.lang.Object, java.lang.String)
	 */
	public void deletePermissions(Object domainObject, String recipient) {
		try {
			// Getting domainObject ID.
			Method idMethod = domainObject.getClass().getMethod("getId", 
					new Class[0]);
			Long objectID = (Long)idMethod.invoke(domainObject, new Object[0]);
			
			// Obtain ACL representation of domain object.
			ObjectIdentity oi = new ObjectIdentityImpl(
					domainObject.getClass().getName(), objectID);
			
			// Obtain the object's ACL.
			MutableAcl acl = (MutableAcl) aclService.readAclById(oi);

			// Obtain ACL representation recipient.
			Sid sid = new PrincipalSid(recipient);
			
			// Find permissions given to this recipient.
			 AccessControlEntry[] entries = acl.getEntries();
			 boolean found = false;
			 for (int i = 0; i < entries.length; ++i) {
				 if (entries[i].getSid().equals(sid)) {
					 acl.deleteAce(i);
					 found = true;
				 }
			 }
			 if (found) {
				 aclService.updateAcl(acl);
			 }
		}catch (NoSuchMethodException nme) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (InvocationTargetException ite) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}catch (IllegalAccessException iae) {
			throw new MethodNotImplemented("Class " + 
					domainObject.getClass().getName() + 
					"doesn't implement the method Long getId()");
		}
	} //- deletePermissions

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#addAdminPermission(java.lang.Object, java.lang.String)
	 */
	public void addAdminPermission(Object domainObject, String recipient) {
		Permission p = BasePermission.ADMINISTRATION;
		addPermission(domainObject, recipient, p);
		} //- addAdminPermission

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#addDeletePermission(java.lang.Object, java.lang.String)
	 */
	public void addDeletePermission(Object domainObject, String recipient) {
		Permission p = BasePermission.DELETE;
		addPermission(domainObject, recipient, p);
	} //- addDeletePermission

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#addReadPermission(java.lang.Object, java.lang.String)
	 */
	public void addReadPermission(Object domainObject, String recipient) {
		Permission p = BasePermission.READ;
		addPermission(domainObject, recipient, p);
	} //- addReadPermission

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.acl.PermissionManager#addWritePermission(java.lang.Object, java.lang.String)
	 */
	public void addWritePermission(Object domainObject, String recipient) {
		Permission p = BasePermission.WRITE;
		addPermission(domainObject, recipient, p);
	} //- addWritePermission

} //- class PermissionManagerImpl
