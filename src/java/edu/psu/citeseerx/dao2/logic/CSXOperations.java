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
package edu.psu.citeseerx.dao2.logic;

import org.springframework.dao.DataAccessException;
import java.io.IOException;
import edu.psu.citeseerx.domain.Document;

public interface CSXOperations {

    /**
     * Inserts a basic document record into the database.  If importing
     * a full document for the first time, this method must be called
     * before importDocument due to foreign key constraints on the document
     * id field.  This is not necessary on some RDMS, but it is for
     * MySQL/InnoDB, for example.  If you want fully transactional document
     * imports, use a DB that supports deferred key checks and add a method
     * that does everything at once.
     * @param doc
     * @throws DataAccessException
     */
    public void insertDocumentEntry(Document doc) throws DataAccessException;

    /**
     * Imports full document data into the repository.  This method handles
     * not only DB imports but writes base version info to the file system
     * as well.
     * NOTE: insertDocumentEntry MUST be called first!
     * @param doc
     * @throws DataAccessException
     * @throws IOException
     */
    public void importDocument(Document doc)
        throws DataAccessException, IOException;

    /**
     * Main method to retrieve a Document object from the database.  A great
     * deal of flexibility is included in specifying which data to retrieve.
     * This is useful because every "false" parameter saves a joining SQL
     * query from being performed.
     * @param doi
     * @param getCitations
     * @param getContexts
     * @param getSource
     * @param getAcks
     * @param getKeywords
     * @param getTags
     * @return A basic CiteSeerX document augmented with data specified by each
     * parameter set to true 
     * @throws DataAccessException
     */
    public Document getDocumentFromDB(String doi, boolean getCitations,
            boolean getContexts, boolean getSource, boolean getAcks,
            boolean getKeywords, boolean getTags) throws DataAccessException;

    /**
     * Gets a document object with all the core document metadata, with
     * options to retrieve citation contexts and provenance data.
     * @param doi
     * @param getContexts
     * @param getSource
     * @return A CiteSeerX document with context and provenance data if
     * specified in the input
     * @throws DataAccessException
     */
    public Document getDocumentFromDB(String doi, boolean getContexts,
            boolean getSource) throws DataAccessException;

    /**
     * Convenience method for getting a minimal document object with only
     * core metadata, including authors.  This is the equivalent of calling
     * the fully parameterized getDocumentsFromDB method with all false
     * boolean parameters, and requires only 1 primary key lookup on the
     * papers table, and 2 joined lookups on the authors and fileInfo tables.
     * @param doi
     * @return A minimal CiteSeerX document
     * @throws DataAccessException
     */
    public Document getDocumentFromDB(String doi) throws DataAccessException;

    /**
     * Convenience method to retrieve a document object from the XML version
     * of the document which is stored in the file system.
     * @param doi
     * @return A CiteSeerX Document
     * @throws DataAccessException
     * @throws IOException
     */
    public Document getDocumentFromXML(String doi)
        throws DataAccessException, IOException;

    /**
     * Convenience method to update all document metadata. Calling this method 
     * is the same as calling @see {@link CSXOperations#updateDocumentData(Document, boolean, boolean, boolean, boolean)}
     * with all true boolean parameters.
     * 
     * @param doc
     * @throws DataAccessException
     * @throws IOException
     */
    public void updateDocumentData(Document doc)
    throws DataAccessException, IOException;
    
    /**
     * Main method to update document metadata. A great deal of flexibility is
     * included in specifying which data to update.
     * @param doc
     * @param updateAuthors
     * @param updateCitations
     * @param updateAcknowledgements
     * @param updateKeywords
     * @throws DataAccessException
     * @throws IOException
     */
    public void updateDocumentData(Document doc, boolean updateAuthors,
            boolean updateCitations,
            boolean updateAcknowledgements, boolean updateKeywords)
        throws DataAccessException, IOException;
    
} //- interface CSXOperations
