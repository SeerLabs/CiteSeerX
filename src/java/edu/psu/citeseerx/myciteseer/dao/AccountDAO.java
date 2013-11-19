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

import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetailsService;

import edu.psu.citeseerx.myciteseer.domain.Account;

/**
 * Provides transparent access to accounts persistence storage 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface AccountDAO extends UserDetailsService {

	/**
	 * Returns an user account that corresponds to username. If the user
	 * account doesn't exist a UsernameNotFoundException exception is thrown.
	 * @param username
	 * @return The username's account
	 * @throws DataAccessException
	 */
    Account getAccount(String username) throws DataAccessException;

    /**
     * Search for the user account that corresponds to the given username.
     * If no account is found, null is returned 
     * @param username
     * @return The username's account or null if it doesn't exists
     * @throws DataAccessException
     */
    Account getAccountOrNull(String username) throws DataAccessException;
    
    /**
     * Returns the user account which match the provided username and password.
     * If the user account doesn't exist a UsernameNotFoundException exception 
     * is thrown.
     * @param username
     * @param password
     * @return the user's account
     * @throws DataAccessException
     */
    Account getAccount(String username, String password)
        throws DataAccessException;
    
    /**
     * Returns the user account which match the provided e-mail address. If no
     * account ids found null is returned.
     * @param emailAddress
     * @return The user's account or null is not found.
     * @throws DataAccessException
     */
    Account getAccountByEmail(String emailAddress) throws DataAccessException;

    /**
     * Saves the account information in the persistent storage.
     * @param account
     * @throws DataAccessException
     */
    void insertAccount(Account account) throws DataAccessException;

    /**
     * Updates the persistent storage with the account information
     * @param account
     * @throws DataAccessException
     */
    void updateAccount(Account account) throws DataAccessException;
    
    /**
     * Stores the new password in the persistent storages.
     * @param account
     * @throws DataAccessException
     */
    void changePassword(Account account) throws DataAccessException;
    
    /**
     * Stores the activation code to allow the validation of an user.
     * @param username
     * @param code
     * @throws DataAccessException
     */
    void storeActivationCode(String username, String code)
    throws DataAccessException;
    
    /**
     * Deletes an activation code from persistent storage.
     * @param username
     * @throws DataAccessException
     */
    void deleteActivationCode(String username) throws DataAccessException;
    
    /**
     * Determines is a given activation code is valid for the given username.
     * @param username
     * @param code
     * @return true if the activation code is valid for that user, false
     * otherwise.
     * @throws DataAccessException
     */
    boolean isValidActivationCode(String username, String code)
    throws DataAccessException;
    
    /**
     * Stores an invitation ticket in the persistent storage
     * @param ticket
     * @throws DataAccessException
     */
    void storeInvitationTicket(String ticket) throws DataAccessException;

    /**
     * Deletes an invitation ticket from the persistent storage.
     * @param ticket
     * @throws DataAccessException
     */
    void deleteInvitationTicket(String ticket) throws DataAccessException;
    
    /**
     * Determines if the given ticket is valid
     * @param ticket
     * @return true if the ticket is valid, false otherwise
     * @throws DataAccessException
     */
    boolean isValidInvitationTicket(String ticket) throws DataAccessException;
    
    /**
     * Sets if user groups are enable in the system 
     * @param isGroupEnable
     */
    void setGroupsEnable(boolean isGroupEnable);
    
    /**
     * Returns amount number of users which internalid is greater than start.
     * This method is useful when doing batch processing over users. For example,
     * indexing user information. 
     * @param start
     * @param amount
     * @return A list of Accounts
     * @throws DataAccessException
     */
    public List<Account> getUsers(Long start, int amount) 
    throws DataAccessException;
    
    /**
     * Returns amount number of users which has been updated after time and internalid is greater 
     * than start.
     * This method is useful when doing batch processing over users. For example,
     * indexing updated user information. 
     * @param time
     * @param start
     * @param amount
     * @return A list of Accounts
     * @throws DataAccessException
     */
    public List<Account> getUsersSinceTime(Date time, Long start, int amount) 
    throws DataAccessException;
    
    /**
     * Returns the last time users where indexed
     * @return last indexed time
     * @throws DataAccessException
     */
    public Date getUserLastIndexTime() throws DataAccessException;

    /**
     * Stores the last time when users where indexed
     * @param time
     * @throws DataAccessException
     */
    public void setUsersLastIndexTime(Date time) throws DataAccessException;
    
    /**
     * Returns the internal id's of users which has been disabled before the
     * given date. This method is mainly intended for indexing purposes. 
     * @param date
     * @return the internal userid
     * @throws DataAccessException
     */
    public List<Long> getDisabled(Date date) throws DataAccessException;
    
    /**
     * Stores the new appid in the persistent storage.
     * @param account
     * @throws DataAccessException
     */
    void changeAppid(Account account) throws DataAccessException;
}  //- interface AccountDAO;
