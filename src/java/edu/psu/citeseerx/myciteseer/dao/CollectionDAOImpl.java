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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.CollectionNote;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.domain.PaperNote;

/**
 * CollectionDao implementation using MYSQL as a persistent storage.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CollectionDAOImpl extends JdbcDaoSupport implements CollectionDAO {

	protected InsertCollection insertCollection;
	protected UpdateCollection updateCollection;
	protected GetCollectionsMapping getCollectionsMapping;
	protected CollectionExistsMapping collectionExistsMapping;
	protected GetUserCollectionPapersMapping getUserCollectionPapersMapping;
	protected GetCollectionMapping getCollectionMapping;
	protected PaperInCollectionMapping paperInCollectionMapping;
	protected InsertPaPerInCollection insertPaPerInCollection;
	protected InsertNoteToCollection insertNoteToCollection;
	protected UpdateCollectionNote updateCollectionNote;
	protected GetCollectionNotesMapping getCollectionNotesMapping; 
	protected InsertNoteToPaper insertNoteToPaper;
	protected GetPaperNotesMapping getPaperNotesMapping;
	protected GetCollectionNoteMapping getCollectionNoteMapping;
	protected GetPaperNoteMapping getPaperNoteMapping;
	protected UpdatePaperNote updatePaperNote;
	protected DeleteCollection deleteCollection;
	protected DeletePaperCollection deletePaperCollection;
	protected DeletePaperNote deletePaperNote;
	protected DeleteCollectionNote deleteCollectionNote;
	
	/* (non-Javadoc)
	 * @see org.springframework.dao.support.DaoSupport#initDao()
	 */
	protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
	
	protected void initMappingSqlQueries() {
        this.insertCollection = new InsertCollection(getDataSource());
        this.updateCollection = new UpdateCollection(getDataSource());
        this.getCollectionsMapping = 
        	new GetCollectionsMapping(getDataSource());
        this.collectionExistsMapping =
        	new CollectionExistsMapping(getDataSource());
        this.getUserCollectionPapersMapping = 
        	new GetUserCollectionPapersMapping(getDataSource());
        this.getCollectionMapping =
        	new GetCollectionMapping(getDataSource());
        this.paperInCollectionMapping = 
        	new PaperInCollectionMapping(getDataSource());
        this.insertPaPerInCollection = 
        	new InsertPaPerInCollection(getDataSource());
        this.insertNoteToCollection =
        	new InsertNoteToCollection(getDataSource());
        this.updateCollectionNote = new UpdateCollectionNote(getDataSource());
        this.getCollectionNotesMapping =
        	new GetCollectionNotesMapping(getDataSource());
        this.insertNoteToPaper = new InsertNoteToPaper(getDataSource());
        this.getPaperNotesMapping = new GetPaperNotesMapping(getDataSource());
        this.getCollectionNoteMapping =
        	new GetCollectionNoteMapping(getDataSource());
        this.getPaperNoteMapping = new GetPaperNoteMapping(getDataSource());
        this.updatePaperNote = new UpdatePaperNote(getDataSource());
        this.deleteCollection = new DeleteCollection(getDataSource());
        this.deletePaperCollection = new DeletePaperCollection(getDataSource());
        this.deletePaperNote = new DeletePaperNote(getDataSource());
        this.deleteCollectionNote = 
        	new DeleteCollectionNote(getDataSource());
        
    } //- initMappingSqlQueries
	
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addCollection(edu.psu.citeseerx.myciteseer.domain.Collection)
	 */
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addCollection(edu.psu.citeseerx.myciteseer.domain.Collection)
	 */
	public void addCollection(Collection collection) 
	throws DataAccessException {
		insertCollection.run(collection);
	} //- addCollection

	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#updateCollection(edu.psu.citeseerx.myciteseer.domain.Collection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void updateCollection(Collection collection, Account account)
			throws DataAccessException {
		updateCollection.run(collection, account);
		
	}


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollections(java.lang.String)
	 */
	public List<Collection> getCollections(String username) 
	throws DataAccessException {
		return getCollectionsMapping.execute(username);
	} //- getCollections

	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollectionAlreadyExists(edu.psu.citeseerx.myciteseer.domain.Collection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean getCollectionAlreadyExists(Collection collection, 
			Account account) throws DataAccessException {
		
		boolean exists = false;

		List<Collection> collections = 
			collectionExistsMapping.run(collection.getName(), account);
		if (collections.size() > 0 &&  
				((Collection)collections.get(0)).getCollectionID() != 
					collection.getCollectionID()) {
			exists = true;
        }
		return exists;
	} //- collectionAlreadyExists
	
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getUserCollectionPapers(long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public List<PaperCollection> getUserCollectionPapers(long collectionID, 
			Account account) throws DataAccessException {
		return getUserCollectionPapersMapping.run(collectionID, account);
	} //- getUserCollectionPapers
	
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#isUserCollection(long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean isUserCollection(long collectionID, Account account)
			throws DataAccessException {
		boolean isUserCol = false;
		List<Collection> collections = getCollectionMapping.run(collectionID, 
				account);
		if (collections.size() > 0) {
			isUserCol = true;
		}
		
		return isUserCol;
	} //- isUserCollection

	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#isPaperInCollection(edu.psu.citeseerx.myciteseer.domain.PaperCollection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean isPaperInCollection(PaperCollection paperCollection, 
			Account account) throws DataAccessException {
		boolean isInCollection = false;
		List<PaperCollection> collections = 
			paperInCollectionMapping.run(paperCollection, account);
		if (collections.size() > 0) {
			isInCollection = true;
		}
		return isInCollection;
	} //- isPaperInCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addPaperToCollection(edu.psu.citeseerx.myciteseer.domain.PaperCollection)
	 */
	public void addPaperToCollection(PaperCollection paperCollection)
			throws DataAccessException {
		insertPaPerInCollection.run(paperCollection);
		
	} //- addPaperToCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollection(long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public Collection getCollection(long collectionID, Account account)
			throws DataAccessException {
		Collection theCollection = null;
		List<Collection> collections = getCollectionMapping.run(collectionID, 
				account);
		if (collections.size() > 0) {
			theCollection = (Collection)collections.get(0);
		}
		return theCollection;
	} //- getCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addNoteToCollection(edu.psu.citeseerx.myciteseer.domain.CollectionNote)
	 */
	public void addNoteToCollection(CollectionNote collectionNote)
			throws DataAccessException {
		insertNoteToCollection.run(collectionNote);
	} //- addNoteToCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#updateCollectionNote(edu.psu.citeseerx.myciteseer.domain.CollectionNote, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void updateCollectionNote(CollectionNote collectionNote, 
			Account account) throws DataAccessException {
		updateCollectionNote.run(collectionNote, account);
		
	} //- UpdateCollectionNote


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollectionNotes(long)
	 */
	public List<CollectionNote> getCollectionNotes(long collectionID)
			throws DataAccessException {
		return getCollectionNotesMapping.execute(collectionID);
	} //- getCollectionNotes


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#addNoteToPaper(edu.psu.citeseerx.myciteseer.domain.PaperNote)
	 */
	public void addNoteToPaper(PaperNote paperNote) throws DataAccessException {
		insertNoteToPaper.run(paperNote);
	} //- addNoteToPaper


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getPaperNotes(java.lang.String, long)
	 */
	public List<PaperNote> getPaperNotes(String paperID, long collectionID) {
		return getPaperNotesMapping.run(collectionID, paperID);
	} //- getPaperNotes


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getCollectionNote(long, long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public CollectionNote getCollectionNote(long collectionNoteID, 
			long collectionID, Account account) throws DataAccessException {
		CollectionNote theNote = null;
		List<CollectionNote> notes = 
			getCollectionNoteMapping.run(collectionNoteID, collectionID, 
					account);
		if (notes.size() > 0) {
			theNote = (CollectionNote)notes.get(0);
		}
		return theNote;
	} //- getCollectionNote


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#getPaperNote(long, long, java.lang.String, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public PaperNote getPaperNote(long paperNoteID, long collectionID,
			String paperID, Account account) throws DataAccessException {
		PaperNote theNote = null;
		List<PaperNote> notes = getPaperNoteMapping.run(paperNoteID, 
				collectionID, paperID, account);
		if (notes.size() > 0) {
			theNote = (PaperNote)notes.get(0);
		}
		return theNote;
	} //- getPaperNote


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#updatePaperNote(edu.psu.citeseerx.myciteseer.domain.PaperNote)
	 */
	public void updatePaperNote(PaperNote paperNote) 
	throws DataAccessException {
		updatePaperNote.run(paperNote);
	} //- updatePaperNote


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#deleteCollection(edu.psu.citeseerx.myciteseer.domain.Collection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void deleteCollection(Collection collection, Account account) {
		deleteCollection.run(collection, account);
		
	} //- deleteCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#deletePaperFromCollection(edu.psu.citeseerx.myciteseer.domain.PaperCollection, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void deletePaperFromCollection(PaperCollection paperCollection, 
			Account account) throws DataAccessException {
		deletePaperCollection.run(paperCollection, account);
	} //- deletePaperFromCollection


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#deletePaperNote(long, java.lang.String, long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void deletePaperNote(long paperNoteID, String paperID, 
			long collectionID, Account account) throws DataAccessException {
		deletePaperNote.run(paperNoteID, paperID, collectionID, account);
	} //- deletePaperNote

	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.CollectionDAO#deleteCollectionNote(long, long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public void deleteCollectionNote(long collectionID, long collectionNoteID,
			Account account) throws DataAccessException {
		deleteCollectionNote.run(collectionID, collectionNoteID, account);
	} //- deleteCollectionNote


	public static final String DEF_INSERT_COLLECTION_STATEMENT =
		"insert into collections values(NULL, ?, ?, ?, ?)";

	protected class InsertCollection  extends SqlUpdate {
		public InsertCollection(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_INSERT_COLLECTION_STATEMENT);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.TINYINT));
			compile();
		} //- InsertCollection
		
		public int run(Collection collection) {
			Object[] params = new Object[] {
					collection.getName(),
					collection.getDescription(),
					collection.getUsername(),
					collection.isDeleteAllowed()
			};
			return update(params);
		} //- run
	} //-InsertCollection
	
	public static final String DEF_UPDATE_COLLECTION_STATEMENT =
		"update collections set name = ?, description = ? where id = ? and " +
		"UID = ?";
	
	protected class UpdateCollection extends SqlUpdate {
		public UpdateCollection(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_UPDATE_COLLECTION_STATEMENT);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		} //- UpdateCollection
		
		int run(Collection collection, Account account) {
			Object[] params = new Object[] {
					collection.getName(),
					collection.getDescription(),
					collection.getCollectionID(),
					account.getUsername()
			};
			return update(params);
		} //- run
	} //- UpdateCollection
	
	public static final String DEF_GET_COLLECTIONS_QUERY =
		"select id, name, description, UID, deleteAllowed from collections " +
		"where UID = ?";
	
	protected class GetCollectionsMapping extends MappingSqlQuery {

		public GetCollectionsMapping(DataSource ds) {
			super(ds, DEF_GET_COLLECTIONS_QUERY);
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetCollectionsMapping
		
		protected Collection mapRow(ResultSet rs, int rownum) 
		throws SQLException {
			Collection collection = new Collection();
			collection.setCollectionID(rs.getLong("id"));
			collection.setName(rs.getString("name"));
			collection.setDescription(rs.getString("description"));
			collection.setUsername(rs.getString("UID"));
			collection.setDeleteAllowed(rs.getBoolean("deleteAllowed"));
			return collection;
		} //- mapRow
		
		public List<Collection> run(String username) {
            Object[] params = new Object[] { username };
            return execute(params);
        } //- run
	} //- GetCollectionsMapping
	
	public static final String DEF_COL_ALREADY_EXISTS_QUERY =
		"select id, name, description, UID, deleteAllowed from collections " +
		"where UID = ? and name = ?";
	
	protected class CollectionExistsMapping extends MappingSqlQuery {
		public CollectionExistsMapping(DataSource ds) {
			super(ds, DEF_COL_ALREADY_EXISTS_QUERY);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetCollectionsMapping
		
		protected Collection mapRow(ResultSet rs, int rownum) 
		throws SQLException {
			Collection collection = new Collection();
			collection.setCollectionID(rs.getLong("id"));
			collection.setName(rs.getString("name"));
			collection.setDescription(rs.getString("description"));
			collection.setUsername(rs.getString("UID"));
			collection.setDeleteAllowed(rs.getBoolean("deleteAllowed"));
			return collection;
		} //- mapRow
		
		public List<Collection> run(String name, Account account) {
            Object[] params = new Object[] { account.getUsername(), name };
            return execute(params);
        } //- run
	} //- CollectionExistsMapping
	
	public static final String DEF_GET_USER_COLLECTION_PAPERS_QUERY =
		"select CID, PID from papers_in_collection where CID = ? and " +
		"UID = ?";
	
	protected class GetUserCollectionPapersMapping extends MappingSqlQuery {

		public GetUserCollectionPapersMapping(DataSource ds) {
			super(ds, DEF_GET_USER_COLLECTION_PAPERS_QUERY);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetUserCollectionPapersMapping
		
		protected PaperCollection mapRow(ResultSet rs, int rownum)
		throws SQLException {
			PaperCollection collectionPaper = new PaperCollection();
			collectionPaper.setCollectionID(rs.getLong("CID"));
			collectionPaper.setPaperID(rs.getString("PID"));
			return collectionPaper;
		} //- mapRow
		
		public List<PaperCollection> run(long collectionID, Account account) {
            Object[] params = new Object[] { collectionID, 
                    account.getUsername() };
            return execute(params);
        } //- run
	} //- GetUserCollectionPapersMapping

	public static final String DEF_GET_USER_COLLECTION =
		"select id, name, description, UID, deleteAllowed from collections " +
		"where id = ? and UID = ?";
	
	protected class GetCollectionMapping extends MappingSqlQuery {

		public GetCollectionMapping(DataSource ds) {
			super(ds, DEF_GET_USER_COLLECTION);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetCollectionMapping
		
		protected Collection mapRow(ResultSet rs, int rownum) 
		throws SQLException {
			Collection collection = new Collection();
			collection.setCollectionID(rs.getLong("id"));
			collection.setName(rs.getString("name"));
			collection.setDescription(rs.getString("description"));
			collection.setUsername(rs.getString("UID"));
			collection.setDeleteAllowed(rs.getBoolean("deleteAllowed"));
			return collection;
		} //- mapRow
		
		public List<Collection> run(long collectionID, Account account) {
            Object[] params = new Object[] {collectionID,
                    account.getUsername() };
            return execute(params);
        } //- run
	} //- GetCollectionMapping
	
	public static final String DEF_PAPER_IN_COLLECTION =
		"select CID, PID, UID from papers_in_collection where CID = ? " +
		"and PID = ? and UID = ?";
	
	protected class PaperInCollectionMapping extends MappingSqlQuery {

		public PaperInCollectionMapping(DataSource ds) {
			super(ds, DEF_PAPER_IN_COLLECTION);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetCollectionMapping
		
		protected PaperCollection mapRow(ResultSet rs, int rownum)
		throws SQLException {
			PaperCollection collectionPaper = new PaperCollection();
			collectionPaper.setCollectionID(rs.getLong("CID"));
			collectionPaper.setPaperID(rs.getString("PID"));
			collectionPaper.setUID(rs.getString("UID"));
			return collectionPaper;
		} //- mapRow
		
		public List<PaperCollection> run(PaperCollection paperCollection, 
		        Account account) {
            Object[] params = new Object[] {
            		paperCollection.getCollectionID(),
            		paperCollection.getPaperID(),
            		account.getUsername() 
            };
            return execute(params);
        } //- run
	} //- PaperInCollectionMapping
	
	public static final String DEF_GET_COLLECTION_NOTES = 
		"select id, CID, UID, note from collection_notes where CID = ?";
	
	protected class GetCollectionNotesMapping extends MappingSqlQuery {
		public GetCollectionNotesMapping(DataSource ds) {
			super(ds, DEF_GET_COLLECTION_NOTES);
			declareParameter(new SqlParameter(Types.BIGINT));
		} //- GetCollectionNotesMapping
		
		protected CollectionNote mapRow(ResultSet rs, int rownum) 
		throws SQLException {
			CollectionNote collectionNote = new CollectionNote();
			collectionNote.setNoteID(rs.getLong("id"));
			collectionNote.setCollectionID(rs.getLong("CID"));
			collectionNote.setNote(rs.getString("note"));
			return collectionNote;
		} //- mapRow
	} //- GetCollectionNotesMapping
	
	public static final String DEF_GET_PAPER_NOTES =
		"select id, CID, PID, UID, note from paper_notes where CID = ? " +
		"and PID =?";
	
	protected class GetPaperNotesMapping extends MappingSqlQuery {
		public GetPaperNotesMapping(DataSource ds) {
			super(ds, DEF_GET_PAPER_NOTES);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetCollectionNotesMapping
		
		protected PaperNote mapRow(ResultSet rs, int rownum) throws SQLException {
			PaperNote paperNote = new PaperNote();
			paperNote.setNoteID(rs.getLong("id"));
			paperNote.setCID(rs.getLong("CID"));
			paperNote.setPID(rs.getString("PID"));
			paperNote.setNote(rs.getString("note"));
			return paperNote;
		} //- mapRow
		
		public List<PaperNote> run(long collectionID, String paperID) {
            Object[] params = new Object[] {collectionID, paperID };
            return execute(params);
        } //- run
	} //- GetCollectionNotesMapping
	
	public static final String DEF_INSERT_PAPER_IN_COLLECTION =
		"insert into papers_in_collection values (?, ?, ?)";
	
	protected class InsertPaPerInCollection  extends SqlUpdate {
		public InsertPaPerInCollection(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_INSERT_PAPER_IN_COLLECTION);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		} //- InsertPaPerInCollection
		
		public int run(PaperCollection paperCollection) {
			Object[] params = new Object[] {
					paperCollection.getCollectionID(),
					paperCollection.getPaperID(),
					paperCollection.getUID()
			};
			return update(params);
		} //- run
	} //- InsertPaPerInCollection
	
	public static final String DEF_INSERT_COLLECTION_NOTE =
		"insert into collection_notes values(NULL, ?, ?, ?)";
	
	protected class InsertNoteToCollection extends SqlUpdate {
		public InsertNoteToCollection(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_INSERT_COLLECTION_NOTE);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		} //- InsertNoteToCollection
		
		public int run(CollectionNote collectionNote) {
			Object[] params = new Object[] {
					collectionNote.getCollectionID(),
					collectionNote.getUID(),
					collectionNote.getNote()
			};
			return update(params);
		} //- run
	} //- InsertNoteToCollection
	
	public static final String DEF_UPDATE_COLLECTION_NOTE_STATEMENT =
		"update collection_notes set note = ? where id = ? and UID = ?";
	
	protected class UpdateCollectionNote extends SqlUpdate {
		public UpdateCollectionNote(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_UPDATE_COLLECTION_NOTE_STATEMENT);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		} //- UpdateCollectionNote
		
		public int run (CollectionNote collectionNote, Account account) {
			Object[] params = new Object[] {
					collectionNote.getNote(),
					collectionNote.getNoteID(),
					account.getUsername()
			};
			return update(params);
		} //- run
	} // - UpdateCollectionNote
	
	public static final String DEF_INSERT_PAPER_NOTE =
		"insert into paper_notes values (NULL, ?, ?, ?, ?)";
	
	protected class InsertNoteToPaper extends SqlUpdate {
		public InsertNoteToPaper(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_INSERT_PAPER_NOTE);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		} //- InsertNoteToPaper
		
		public int run(PaperNote paperNote) {
			Object[] params = new Object[] {
					paperNote.getCID(),
					paperNote.getPID(),
					paperNote.getUID(),
					paperNote.getNote()
			};
			return update(params);
		} //- run
	} //- InsertNoteToPaper
	
	public static final String DEF_GET_COLLECTION_NOTE = 
		"select id, CID, UID, note from collection_notes where id = ? " +
			" and CID = ? and UID = ?";
	
	protected class GetCollectionNoteMapping extends MappingSqlQuery {
		public GetCollectionNoteMapping(DataSource ds) {
			super(ds, DEF_GET_COLLECTION_NOTE);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetCollectionNotesMapping
		
		protected CollectionNote mapRow(ResultSet rs, int rownum)
		throws SQLException {
			CollectionNote collectionNote = new CollectionNote();
			collectionNote.setNoteID(rs.getLong("id"));
			collectionNote.setCollectionID(rs.getLong("CID"));
			collectionNote.setUID(rs.getString("CID"));
			collectionNote.setUID(rs.getString("UID"));
			collectionNote.setNote(rs.getString("note"));
			return collectionNote;
		} //- mapRow
		
		public List<CollectionNote> run(long collectionNoteID, 
		        long collectionID, Account account) {
            Object[] params = new Object[] {
            		collectionNoteID,
            		collectionID,
            		account.getUsername()
            };
            return execute(params);
        } //- run
	} //- GetCollectionNoteMapping
	
	public static final String DEF_GET_PAPER_NOTE = 
		"select id, CID, PID, UID, note from paper_notes where id = ? " +
		"and CID = ? and PID = ? and UID = ?";
	
	protected class GetPaperNoteMapping extends MappingSqlQuery {
		public GetPaperNoteMapping(DataSource ds) {
			super(ds, DEF_GET_PAPER_NOTE);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetCollectionNotesMapping
		
		protected PaperNote mapRow(ResultSet rs, int rownum)
		throws SQLException {
			PaperNote paperNote = new PaperNote();
			paperNote.setNoteID(rs.getLong("id"));
			paperNote.setCID(rs.getLong("CID"));
			paperNote.setPID(rs.getString("PID"));
			paperNote.setUID(rs.getString("UID"));
			paperNote.setNote(rs.getString("note"));
			return paperNote;
		} //- mapRow
		
		public List<PaperNote> run(long paperNoteID, long collectionID,
		        String paperID,
				Account account) {
            Object[] params = new Object[] {
            		paperNoteID,
            		collectionID,
            		paperID,
            		account.getUsername()
            };
            return execute(params);
        } //- run
	} //- GetPaperNoteMapping
	
	public static final String DEF_UPDATE_PAPER_NOTE_STATEMENT =
		"update paper_notes set note = ? where id = ? and UID = ?";
	
	protected class UpdatePaperNote extends SqlUpdate {
		public UpdatePaperNote(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_UPDATE_PAPER_NOTE_STATEMENT);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		} //- UpdatePaperNote
		
		public int run (PaperNote paperNote) {
			Object[] params = new Object[] {
					paperNote.getNote(),
					paperNote.getNoteID(),
					paperNote.getUID()
			};
			return update(params);
		} //- run
	} // - UpdatePaperNote
	
	private static final String DEF_DEL_COLLECTION_STMT =
		"delete from collections where id =? and UID = ?";
	
	protected class DeleteCollection extends SqlUpdate {
        public DeleteCollection(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_COLLECTION_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteCollection
        
        public int run(Collection collection, Account account) {
            Object[] params = new Object[] {
                    collection.getCollectionID(),
                    account.getUsername() };
            return update(params);
        } //- run
        
    }  //- class DeleteCollection
	
	private static final String DEF_DEL_PAPER_COLLECTION_STMT =
		"delete from papers_in_collection where CID = ? AND PID = ? and UID = ?";
	
	protected class DeletePaperCollection extends SqlUpdate {
        public DeletePaperCollection(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_PAPER_COLLECTION_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeletePaperCollection
        
        public int run(PaperCollection paperCollection, Account account) {
            Object[] params = new Object[] {
            		paperCollection.getCollectionID(),
            		paperCollection.getPaperID(),
            		account.getUsername()};
            return update(params);
        } //- run
        
    }  //- class DeletePaperCollection
	
	private static final String DEF_DEL_PAPER_NOTE_STMT =
		"delete from paper_notes where id = ? and PID = ? and " +
		"CID = ? and UID = ?";
	
	protected class DeletePaperNote extends SqlUpdate {
        public DeletePaperNote(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_PAPER_NOTE_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(long paperNoteID, String paperID, long collectionID, 
        		Account account) {
            Object[] params = new Object[] {
            		paperNoteID,
            		paperID,
            		collectionID,
            		account.getUsername()};
            return update(params);
        } //- run

    }  //- class DeletePaperNote
	
	private static final String DEF_DEL_COLLECTION_NOTE_STMT =
		"delete from collection_notes where id = ? and CID = ? and " +
		"UID = ?";
	
	protected class DeleteCollectionNote extends SqlUpdate {
        public DeleteCollectionNote(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_COLLECTION_NOTE_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteCollectionNote
        
        public int run(long collectionID, long collectionNoteID,
        		Account account) {
            Object[] params = new Object[] {
            		collectionNoteID,
            		collectionID,
            		account.getUsername()};
            return update(params);
        } //- run

    }  //- class DeleteCollectionNote
	
} //- class CollectionDAOImpl
