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

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.CollectionNote;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.domain.PaperNote;

/**
 * Provides transparent access to user collections persistence storage.
 * This interface would be refactored to use ACL rights in order to allow users to
 * share their collections. The functionality will be the same but some methods
 * signatures would change.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public interface CollectionDAO {
	/**
	 * Add a new collection to the user collections set.
	 * @param collection Collection information
	 * @throws DataAccessException
	 */
	void addCollection(Collection collection) throws DataAccessException;
	
	/**
	 * Updates collection information
	 * @param collection collection to be updated
	 * @param account User trying to do the update
	 * @throws DataAccessException
	 */
	void updateCollection(Collection collection, Account account) throws DataAccessException;
	
	/**
	 * Returns a list of collections owned by the given user
	 * @param username User who owns the collections
	 * @return the user collections
	 * @throws DataAccessException
	 */
	List<Collection> getCollections(String username) throws DataAccessException;
	
	/**
	 * Returns a specific user collection
	 * @param collectionID the collection to be returned
	 * @param account User who is trying to obtain the collection
	 * @return the collection if any is found null otherwise
	 * @throws DataAccessException
	 */
	Collection getCollection(long collectionID, Account account) 
	throws DataAccessException;
	
	/**
	 * Informs if the given collection is already a collection owned by the 
	 * given user
	 * @param collection Collection to validate
	 * @param account 
	 * @return True if a collection with the same name is owned by the given 
	 * user, false otherwise
	 * @throws DataAccessException
	 */
	boolean getCollectionAlreadyExists(Collection collection, Account account)
	throws DataAccessException;
	
	/**
	 * Returns info about the papers in a particular user collection
	 * @param collectionID The collection to obtain the papers from
	 * @param account User trying to obtain the papers
	 * @return The list of papers if the user is the one who owns the
	 * collection, an empty list otherwise.
	 * @throws DataAccessException
	 */
	List<PaperCollection> getUserCollectionPapers(long collectionID, 
			Account account) 
	throws DataAccessException;
	
	/**
	 * Informs if the given collection is owned by the given user
	 * @param collectionID Collection to validate
	 * @param account User to validater
	 * @return true if the user owns the collection, false otherwise
	 * @throws DataAccessException
	 */
	boolean isUserCollection(long collectionID, Account account) 
	throws DataAccessException;
	
	/**
	 * Informs if the given paper is part of the given collection.
	 * @param paperCollection Paper and collection information to be validated
	 * @param account
	 * @return true if the paper is part of the given collection and that 
	 * collection is owned by the given user; false otherwise
	 * @throws DataAccessException
	 */
	boolean isPaperInCollection(PaperCollection paperCollection, Account account) 
	throws DataAccessException;

	/**
	 * Adds a paper to a collection
	 * @param paperCollection Carries the information about the paper to be included
	 * as well the collection where that paper should be included
	 * @throws DataAccessException
	 */
	void addPaperToCollection(PaperCollection paperCollection) 
	throws DataAccessException;
	
	/**
	 * Includes a new note to a collection
	 * @param noteCollection Carries the information about the note to be included
	 * as well the collection where that note should be included
	 * @throws DataAccessException
	 */
	void addNoteToCollection(CollectionNote noteCollection) 
	throws DataAccessException;
	
	/**
	 * Updates a note belonging to the given collection
	 * @param collectionNote Note, and collection data 
	 * @param account User trying to do the update
	 * @throws DataAccessException
	 */
	void updateCollectionNote(CollectionNote collectionNote, Account account)
	throws DataAccessException;
	
	/**
	 * Returns all the notes added to the given collection
	 * @param collectionID Collection from which the notes will be obtained
	 * @return A list of notes, could be an empty list.
	 * @throws DataAccessException
	 */
	List<CollectionNote> getCollectionNotes(long collectionID) throws DataAccessException;
	
	/**
	 * Includes a new note to a paper
	 * @param paperNote Carries the information about the note to be included
	 * as well the paper where that note should be included
	 * @throws DataAccessException
	 */
	void addNoteToPaper(PaperNote paperNote) throws DataAccessException;
	
	/**
	 * Updates a note belonging to the given paper
	 * @param paperNote Note, and paper data
	 * @throws DataAccessException
	 */
	void updatePaperNote(PaperNote paperNote) throws DataAccessException;
	
	/**
	 * Returns all the notes for the given paper in the given collection
	 * @param paperID Paper from which the notes will be obtained
	 * @param collectionID Collection that includes the given paper
	 * @return A list of PaperNotes. The list could be empty if there is no notes 
	 * for that paper in that collection or the paper does not belong to the given
	 * collection or the user is not the owner of that collection
	 * @throws DataAccessException
	 */
	List<PaperNote> getPaperNotes(String paperID, long collectionID)
	throws DataAccessException;
	
	/**
	 * Obtains a specific collection note
	 * @param collectionNoteID 
	 * @param collectionID
	 * @param account
	 * @return The note if it exists. Null otherwise.
	 * @throws DataAccessException
	 */
	CollectionNote getCollectionNote(long collectionNoteID, long collectionID, 
			Account account) throws DataAccessException;
	
	/**
	 * Obtains a specific paper note
	 * @param paperNoteID
	 * @param collectionID
	 * @param paperID
	 * @param account
	 * @return A PaperNote for the given parameters
	 * @throws DataAccessException
	 */
	PaperNote getPaperNote(long paperNoteID, long collectionID, String paperID,
			Account account) throws DataAccessException;
	
	/**
	 * Deletes an user collection and all its content
	 * @param collection Collection to be deleted
	 * @param account User executing the action
	 * @throws DataAccessException
	 */
	public void deleteCollection(Collection collection, Account account) 
	throws DataAccessException;;
	
	/**
	 * Deletes a paper from a collection
	 * @param paperCollection Paper and collection information
	 * @param account User executing the operation
	 * @throws DataAccessException
	 */
	public void deletePaperFromCollection(PaperCollection paperCollection, Account account)  
	throws DataAccessException;
	
	/**
	 * Deletes a paper note from a paper within a given collection
	 * @param paperNoteID Note to be deleted
	 * @param paperID Paper to delete the note from
	 * @param collectionID Collection
	 * @param account User executing the action
	 * @throws DataAccessException
	 */
	public void deletePaperNote(long paperNoteID, String paperID, 
			long collectionID, Account account) throws DataAccessException;
	
	/**
	 * Deletes a note from a collection
	 * @param collectionID collection to delete the note from
	 * @param collectionNoteID Note to be deleted
	 * @param account User performing the action
	 * @throws DataAccessException
	 */
	public void deleteCollectionNote(long collectionID, long collectionNoteID,
			Account account) throws DataAccessException;
} //- interface CollectionDAO
