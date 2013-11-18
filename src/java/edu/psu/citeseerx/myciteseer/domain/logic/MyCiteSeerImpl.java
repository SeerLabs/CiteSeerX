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
package edu.psu.citeseerx.myciteseer.domain.logic;

import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import edu.psu.citeseerx.myciteseer.dao.AccountDAO;
import edu.psu.citeseerx.myciteseer.dao.CollectionDAO;
import edu.psu.citeseerx.myciteseer.dao.ConfigurationDAO;
import edu.psu.citeseerx.myciteseer.dao.FeedDAO;
import edu.psu.citeseerx.myciteseer.dao.GroupDAO;
import edu.psu.citeseerx.myciteseer.dao.MyNetDAO;
import edu.psu.citeseerx.myciteseer.dao.SubmissionDAO;
import edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO;
import edu.psu.citeseerx.myciteseer.dao.TagDAO;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.CollectionNote;
import edu.psu.citeseerx.myciteseer.domain.Feed;
import edu.psu.citeseerx.myciteseer.domain.Friend;
import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.GroupMember;
import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.domain.PaperNote;
import edu.psu.citeseerx.myciteseer.domain.SubmissionNotificationItem;
import edu.psu.citeseerx.myciteseer.domain.UrlSubmission;
import edu.psu.citeseerx.myciteseer.domain.UserMessage;

import java.util.Date;
import java.util.List;

import java.sql.SQLException;

/**
 * MyCiteSeerFacade implementation. This implementation is independent from the
 * persistent storage implementation. If you want to use a different persistent
 * storage to the one provide with this distribution you just need to 
 * implement a new version of all the DAO interfaces.   
 * @author Isaac Councill
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class MyCiteSeerImpl implements MyCiteSeerFacade {

    private AccountDAO accountDAO;

    public void setAccountDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    } //- setAccountDAO
    
    private SubmissionDAO submissionDAO;

    public void setSubmissionDAO(SubmissionDAO submissionDAO) {
        this.submissionDAO = submissionDAO;
    } //- setSubmissionDAO
    
    private MyNetDAO myNetDAO;
    
    public void setMyNetDAO(MyNetDAO myNetDAO) {
        this.myNetDAO = myNetDAO;
    }
    
    private ConfigurationDAO configurationDAO;
    
    public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
        this.configurationDAO = configurationDAO;
    } //- setConfigurationDAO
    
    private CollectionDAO collectionDAO;
    
    public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	} //- setCollectionDAO
    
    private SubscriptionDAO subscriptionDAO;
    
    public void setSubscriptionDAO(SubscriptionDAO subscriptionDAO) {
        this.subscriptionDAO = subscriptionDAO;
    } //- setSubscriptionDAO
    
    private TagDAO tagDAO;
    
    public void setTagDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }
    
    private FeedDAO feedDAO;
    
    public void setFeedDAO(FeedDAO feedDAO) {
        this.feedDAO = feedDAO;
    }
    
    private GroupDAO groupDAO;
    
    public void setGroupDAO(GroupDAO groupDAO) {
    	this.groupDAO = groupDAO;
    } //- setGroupDAO
   

    // AccountDAO
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccount(java.lang.String)
	 */
	public Account getAccount(String username) {
        return accountDAO.getAccount(username);
    } //- getAccount

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccountOrNull(java.lang.String)
     */
    public Account getAccountOrNull(String username) {
        return accountDAO.getAccountOrNull(username);
    } //- getAccountOrNull

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccount(java.lang.String, java.lang.String)
     */
    public Account getAccount(String username, String password) {
        return accountDAO.getAccount(username, password);
    } //- getAccount
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccountByEmail(java.lang.String)
     */
    public Account getAccountByEmail(String emailAddress) {
        return accountDAO.getAccountByEmail(emailAddress);
    } //- getAccountByEmail

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#insertAccount(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void insertAccount(Account account) {
        accountDAO.insertAccount(account);
    } //- insertAccount

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#updateAccount(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void updateAccount(Account account) {
        accountDAO.updateAccount(account);
    } //- updateAccount
    
    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    public UserDetails loadUserByUsername(String name) {
    	try {
    		setGroupsEnable(
    				configurationDAO.getConfiguration().getGroupsEnabled());
    	}catch (SQLException e) {
			setGroupsEnable(false);
		}
        return accountDAO.loadUserByUsername(name);
    } //- loadUserByUsername
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#changePassword(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void changePassword(Account account) {
        accountDAO.changePassword(account);
    } //- changePassword
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#storeActivationCode(java.lang.String, java.lang.String)
     */
    public void storeActivationCode(String username, String code) {
        accountDAO.storeActivationCode(username, code);
    } //- storeActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#deleteActivationCode(java.lang.String)
     */
    public void deleteActivationCode(String username) {
        accountDAO.deleteActivationCode(username);
    } //- deleteActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#isValidActivationCode(java.lang.String, java.lang.String)
     */
    public boolean isValidActivationCode(String username, String code) {
        return accountDAO.isValidActivationCode(username, code);
    } //- isValidActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#storeInvitationTicket(java.lang.String)
     */
    public void storeInvitationTicket(String ticket)
    throws DataAccessException {
        accountDAO.storeInvitationTicket(ticket);
    } //- storeInvitationTicket
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#deleteInvitationTicket(java.lang.String)
     */
    public void deleteInvitationTicket(String ticket)
    throws DataAccessException {
        accountDAO.deleteInvitationTicket(ticket);
    } //- deleteInvitationTicket
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#isValidInvitationTicket(java.lang.String)
     */
    public boolean isValidInvitationTicket(String ticket)
    throws DataAccessException {
        return accountDAO.isValidInvitationTicket(ticket);
    } //- isValidInvitationTicket
    
    public void setGroupsEnable(boolean isGroupEnable) {
    	accountDAO.setGroupsEnable(isGroupEnable);
    } //- setGroupsEnable
    
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUsers(java.lang.Long, int)
	 */
	public List<Account> getUsers(Long start, int amount) 
	throws DataAccessException {
		return accountDAO.getUsers(start, amount);
	} //- getAllUsers
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUsersSinceTime(java.util.Date, java.lang.Long, int)
	 */
	public List<Account> getUsersSinceTime(Date time, Long start, int amount) 
    throws DataAccessException {
		return accountDAO.getUsersSinceTime(time, start, amount);
	} //- getUsersSinceTime
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUserLastIndexTime()
	 */
	public Date getUserLastIndexTime() throws DataAccessException {
		return accountDAO.getUserLastIndexTime();
	} //- getUserLastIndexTime
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#setUsersLastIndexTime(java.util.Date)
	 */
	public void setUsersLastIndexTime(Date time) throws DataAccessException {
		accountDAO.setUsersLastIndexTime(time);
	} //- setUsersLastIndexTime
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getDisabled(java.util.Date)
	 */
	public List<Long> getDisabled(Date date) throws DataAccessException {
		return accountDAO.getDisabled(date);
	} //- getDisabled
    
	/* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#changeAppid(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void changeAppid(Account account) throws DataAccessException {
        accountDAO.changeAppid(account);
    } //- 	changeAppid
	
    // SubmissionDAO


    /* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.SubmissionDAO#isUrlAlreadySubmitted(java.lang.String, java.lang.String)
	 */
	public boolean isUrlAlreadySubmitted(String url, String username) {
        return submissionDAO.isUrlAlreadySubmitted(url, username);
    } //- isUrlAlreadySubmitted
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubmissionDAO#insertUrlSubmission(edu.psu.citeseerx.myciteseer.domain.UrlSubmission)
     */
    public void insertUrlSubmission(UrlSubmission submission) {
        submissionDAO.insertUrlSubmission(submission);
    } //- insertUrlSubmission
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubmissionDAO#getUrlSubmissions(java.lang.String)
     */
    public List<UrlSubmission> getUrlSubmissions(String username) {
        return submissionDAO.getUrlSubmissions(username);
    } //- getUrlSubmissions
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubmissionDAO#insertSubmissionComponent(edu.psu.citeseerx.myciteseer.domain.SubmissionNotificationItem)
     */
    public void insertSubmissionComponent(SubmissionNotificationItem item) {
        submissionDAO.insertSubmissionComponent(item);
    } //- insertSubmissionComponent
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubmissionDAO#getSubmissionComponents(java.lang.String)
     */
    public List<SubmissionNotificationItem> getSubmissionComponents(
            String JID) {
        return submissionDAO.getSubmissionComponents(JID);
    } //- getSubmissionComponents
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubmissionDAO#getUrlSubmission(java.lang.String)
     */
    public UrlSubmission getUrlSubmission(String JID) {
        return submissionDAO.getUrlSubmission(JID);
    } //- getUrlSubmission
    
    public void updateJobStatus(String JID, int status) {
        submissionDAO.updateJobStatus(JID, status);
    }
    
    
    // MyNetDAO
    
    public void sendMsg(Account sender, String msgTo, String msgBody) {
        myNetDAO.sendMsg(sender, msgTo, msgBody);
    }
    
    public List<UserMessage> getMessages(Account account) {
        return myNetDAO.getMessages(account);
    }
    
    public void addFriend(Account account, String friendId) {
        myNetDAO.addFriend(account, friendId);
    }
    
    public List<Friend> getFriends(Account account) {
        return myNetDAO.getFriends(account);
    }
    
    public List<Friend> getFriendsOfFriend(Account account, String friendId) {
        return myNetDAO.getFriendsOfFriend(account, friendId);
    }
    
    
    // ConfigurationDAO
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.ConfigurationDAO#getConfiguration()
     */
    public MCSConfiguration getConfiguration() throws SQLException {
        return configurationDAO.getConfiguration();
    } //- getConfiguration
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.ConfigurationDAO#saveConfiguration(edu.psu.citeseerx.myciteseer.domain.MCSConfiguration)
     */
    public void saveConfiguration (MCSConfiguration configuration)
    throws SQLException {
        configurationDAO.saveConfiguration(configuration);
    } //- saveConfiguration


    // CollectionDAO
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addCollection(edu.psu.citeseerx.myciteseer.domain.Collection)
     */
    public void addCollection(Collection collection) {
		collectionDAO.addCollection(collection);
	} //- addCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollections(java.lang.String)
	 */
	public List<Collection> getCollections(String username) {
		return collectionDAO.getCollections(username);
	} //- getCollections


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollectionAlreadyExists(edu.psu.citeseerx.myciteseer.domain.Collection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean getCollectionAlreadyExists(Collection collection,
	        Account account) {
		return collectionDAO.getCollectionAlreadyExists(collection, account);
	} //- getCollectionAlreadyExists

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getUserCollectionPapers(long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public List<PaperCollection> getUserCollectionPapers(long collectionID, 
	        Account account) {
		return collectionDAO.getUserCollectionPapers(collectionID, account);
	} //- getUserCollectionPapers


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#isUserCollection(long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean isUserCollection(long collectionID, Account account)
			throws DataAccessException {
		return collectionDAO.isUserCollection(collectionID, account);
	} //- isUserCollections


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#isPaperInCollection(edu.psu.citeseerx.myciteseer.domain.PaperCollection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean isPaperInCollection(PaperCollection paperCollection, Account account)
			throws DataAccessException {
		return collectionDAO.isPaperInCollection(paperCollection, account);
	} //- isPaperInCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addPaperToCollection(edu.psu.citeseerx.myciteseer.domain.PaperCollection)
	 */
	public void addPaperToCollection(PaperCollection paperCollection)
			throws DataAccessException {
		collectionDAO.addPaperToCollection(paperCollection);
	} //- addPaperToCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollection(long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public Collection getCollection(long collectionID, Account account)
			throws DataAccessException {
		return collectionDAO.getCollection(collectionID, account);
	} //- getCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addNoteToCollection(edu.psu.citeseerx.myciteseer.domain.CollectionNote)
	 */
	public void addNoteToCollection(CollectionNote noteCollection)
			throws DataAccessException {
		collectionDAO.addNoteToCollection(noteCollection);
	} //- addNoteToCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollectionNotes(long)
	 */
	public List<CollectionNote> getCollectionNotes(long collectionID)
			throws DataAccessException {
		return collectionDAO.getCollectionNotes(collectionID);
	} //- getCollectionNotes


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addNoteToPaper(edu.psu.citeseerx.myciteseer.domain.PaperNote)
	 */
	public void addNoteToPaper(PaperNote paperNote) throws DataAccessException {
		collectionDAO.addNoteToPaper(paperNote);
	} //- addNoteToPaper


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getPaperNotes(java.lang.String, long)
	 */
	public List<PaperNote> getPaperNotes(String paperID, long collectionID)
    throws DataAccessException {
		return collectionDAO.getPaperNotes(paperID, collectionID);
	} //- getPaperNotes


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#updateCollection(edu.psu.citeseerx.myciteseer.domain.Collection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void updateCollection(Collection collection, Account account)
			throws DataAccessException {
		collectionDAO.updateCollection(collection, account);
	} //- updateCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#updateCollectionNote(edu.psu.citeseerx.myciteseer.domain.CollectionNote, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void updateCollectionNote(CollectionNote collectionNote, 
	        Account account) throws DataAccessException {
		collectionDAO.updateCollectionNote(collectionNote, account);
	} //- updateCollectionNote


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollectionNote(long, long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public CollectionNote getCollectionNote(long collectionNoteID, 
	        long collectionID, Account account) throws DataAccessException {
		return collectionDAO.getCollectionNote(collectionNoteID, collectionID, 
		        account);
	} //- getCollectionNote


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getPaperNote(long, long, java.lang.String, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public PaperNote getPaperNote(long paperNoteID, long collectionID, 
			String paperID, Account account) throws DataAccessException {
		return collectionDAO.getPaperNote(paperNoteID, collectionID, paperID,
				account);
	} //- getPaperNote
	
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#updatePaperNote(edu.psu.citeseerx.myciteseer.domain.PaperNote)
	 */
	public void updatePaperNote(PaperNote paperNote) 
	throws DataAccessException {
		collectionDAO.updatePaperNote(paperNote);
	} //- updatePaperNote
    

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#deleteCollection(edu.psu.citeseerx.myciteseer.domain.Collection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void deleteCollection(Collection collection, Account account) {
		collectionDAO.deleteCollection(collection, account);
	} //- deleteCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#deletePaperFromCollection(edu.psu.citeseerx.myciteseer.domain.PaperCollection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void deletePaperFromCollection(PaperCollection paperCollection, 
	        Account account) throws DataAccessException {
		collectionDAO.deletePaperFromCollection(paperCollection, account);
	} //- deletePaperFromCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#deletePaperNote(long, java.lang.String, long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void deletePaperNote(long paperNoteID, String paperID, 
	        long collectionID, Account account) throws DataAccessException {
		collectionDAO.deletePaperNote(paperNoteID, paperID, collectionID, 
				account);
	} //- deletePaperNote


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#deleteCollectionNote(long, long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void deleteCollectionNote(long collectionID, long collectionNoteID,
			Account account) throws DataAccessException {
		collectionDAO.deleteCollectionNote(collectionID, collectionNoteID, 
				account);
	} //- deleteCollectionNote
	
	
    // Subscription DAO

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO#addMonitor(edu.psu.citeseerx.myciteseer.domain.Account, java.lang.String)
	 */
	public void addMonitor(Account account, String paperid)
    throws DataAccessException {
        subscriptionDAO.addMonitor(account, paperid);
    } //- addMonitor
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO#deleteMonitor(edu.psu.citeseerx.myciteseer.domain.Account, java.lang.String)
     */
    public void deleteMonitor(Account account, String paperid)
    throws DataAccessException {
        subscriptionDAO.deleteMonitor(account, paperid);
    } //- deleteMonitor
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO#getMonitors(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public List<String> getMonitors(Account account) 
    throws DataAccessException {
        return subscriptionDAO.getMonitors(account);
    } //- getMonitors
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO#getUsersMonitoring(java.lang.String)
     */
    public List<String> getUsersMonitoring(String paperid) {
        return subscriptionDAO.getUsersMonitoring(paperid);
    } //- getUsersMonitoring
    
    
    // Tag DAO
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.TagDAO#addTag(edu.psu.citeseerx.myciteseer.domain.Account, java.lang.String, java.lang.String)
     */
    public void addTag(Account account, String doi, String tag)
    throws DataAccessException {
        tagDAO.addTag(account, doi, tag);
    } //- addTag
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.TagDAO#deleteTag(edu.psu.citeseerx.myciteseer.domain.Account, java.lang.String, java.lang.String)
     */
    public void deleteTag(Account account, String doi, String tag)
    throws DataAccessException {
        tagDAO.deleteTag(account, doi, tag);
    } //- deleteTag
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.TagDAO#getTags(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public List<String> getTags(Account account) throws DataAccessException {
        return tagDAO.getTags(account);
    } //- getTags
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.TagDAO#getDoisForTag(edu.psu.citeseerx.myciteseer.domain.Account, java.lang.String)
     */
    public List<String> getDoisForTag(Account account, String tag)
    throws DataAccessException {
        return tagDAO.getDoisForTag(account, tag);
    } //- getDoisForTag
    
    
    // Feed DAO
    
    public void addFeed(Feed feed) {
        feedDAO.addFeed(feed);
    }
    
    public List<Feed> getFeeds(String userid) {
        return feedDAO.getFeeds(userid);
    }
    
    public Feed getFeed(long id) {
        return feedDAO.getFeed(id);
    }
    
    public void deleteFeed(long id, String userid) {
        feedDAO.deleteFeed(id, userid);
    }

    // Group DAO
    
    
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#addGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public Group addGroup(Group group)
			throws DataAccessException {
		return groupDAO.addGroup(group);
	} //- addGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#addMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String, boolean)
	 */
	public void addMember(Group group, String userid, boolean validating)
			throws DataAccessException {
		groupDAO.addMember(group, userid, validating);
	} //- addMember


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroup(long)
	 */
	public Group getGroup(long groupID)
			throws DataAccessException {
		return groupDAO.getGroup(groupID);
	} //- getGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroups(java.lang.String)
	 */
	public List<Group> getGroups(String username) throws DataAccessException {
		return groupDAO.getGroups(username);
	} //- getGroups

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#isNameRepeated(edu.psu.citeseerx.myciteseer.domain.Group, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean isNameRepeated(Group group, Account account)
			throws DataAccessException {
		return groupDAO.isNameRepeated(group, account);
	} //- isNameRepeated
    

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#deleteGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public void deleteGroup(Group group)
	throws DataAccessException {
		groupDAO.deleteGroup(group);

	} //- deleteGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getMembers(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public List<GroupMember> getMembers(Group group) throws DataAccessException {
		return groupDAO.getMembers(group);
	} //- getMembers
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#updateGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public void updateGroup(Group group)
			throws DataAccessException {
		groupDAO.updateGroup(group);
	} //- updateGroup
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#leaveGroup(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public void leaveGroup(Group group, String userid)
			throws DataAccessException {
		groupDAO.leaveGroup(group, userid);
	} //- leaveGroup
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#removeMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public void removeMember(Group group, String userid)
			throws DataAccessException {
		groupDAO.removeMember(group, userid);
	} //- removeMember


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#validateUser(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public void validateUser(Group group, String userid)
			throws DataAccessException {
		groupDAO.validateUser(group, userid);
	} //- validateUser
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroupAuthorities(java.lang.String)
	 */
	public List<GrantedAuthority> getGroupAuthorities(String username)
			throws DataAccessException {
		return groupDAO.getGroupAuthorities(username);
	} //- getGroupAuthorities
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#isMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public boolean isMember(Group group, String userid)
	throws DataAccessException {
		return groupDAO.isMember(group, userid);
	} //- isMember
	
} //- class MyCiteSeerImpl
