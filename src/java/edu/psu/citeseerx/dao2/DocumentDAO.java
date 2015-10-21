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
package edu.psu.citeseerx.dao2;

import org.springframework.dao.DataAccessException;

import java.util.Date;
import java.util.List;

import edu.psu.citeseerx.domain.DOIInfo;
import edu.psu.citeseerx.domain.Document;

/**
 * Provides transparent access to Document persistence storage 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface DocumentDAO {

    /**
     * @param docID
     * @param getSource If true, add source information from last version
     * of metadata
     * @return Document information associated with docID
     * @throws DataAccessException
     */
    public Document getDocument(String docID, boolean getSource)
    throws DataAccessException;
    
    /**
     * Inserts a document into the system
     * @param doc
     * @throws DataAccessException
     */
    public void insertDocument(Document doc) throws DataAccessException;

    /**
     * Stores the last version of document metadata
     * @param doc
     * @throws DataAccessException
     */
    public void insertDocumentSrc(Document doc) throws DataAccessException;

    /**
     * Updates the document
     * @param doc
     * @throws DataAccessException
     */
    public void updateDocument(Document doc) throws DataAccessException;
    
    /**
     * Set the document as public or not.
     * @param doc
     * @param toState
     * @throws DataAccessException
     */
    public void setDocState(Document doc, int toState)
    throws DataAccessException;
    
    /**
     * Associates the given document to a cluster
     * @param doc
     * @param clusterID
     * @throws DataAccessException
     */
    public void setDocCluster(Document doc, Long clusterID)
    throws DataAccessException;
    
    /**
     * Updates the number of citations for the given document
     * @param doc
     * @param ncites
     * @throws DataAccessException
     */
    public void setDocNcites(Document doc, int ncites)
    throws DataAccessException;
    
    /**
     * 
     * @return Returns the number of documents within the system
     * @throws DataAccessException
     */
    public Integer getNumberOfDocumentRecords() throws DataAccessException;
    
    /**
     * 
     * @param start
     * @param amount
     * @return Returns a list of amount Document identifiers beginning at start
     * @throws DataAccessException
     */
    public List<String> getDOIs(String start, int amount) throws DataAccessException;
    
    /**
     * @param start		Star date
     * @param end		End date
     * @param prev  	last id from previous call
     * @param amount	Number of records to be returned
     * @return Returns all the DOIs between start and end beginning in
     * prev
     * @throws DataAccessException
     */
    public List<DOIInfo> getSetDOIs(Date start, Date end, String prev,  
    		int amount) throws DataAccessException;
    
    /**
     * @param start
     * @param end
     * @param prev
     * @return Returns the number of DOIs between start and end beginning in
     * prev
     * @throws DataAccessException
     */
    public Integer getSetDOICount(Date start, Date end, String prev) 
    throws DataAccessException;
    
    /**
     * 
     * @param start
     * @param end
     * @param lastDOI
     * @param amount
     * @return Returns amount or less DOIs representing documents crawled 
     * between start and end date, and beginning at lastDOI.
     * @throws DataAccessException
     */
    public List<String> getCrawledDOIs(Date start, Date end, String lastDOI,
            int amount) throws DataAccessException; 
    
    /**
     * 
     * @param lastDOI
     * @param amount
     * @return Returns amount or less DOIs beginning at lastDOI. The DOI's
     * returned correspond to the latest crawled documents within the repository
     * Those are like the most recent crawled/ingested documents.
     * @throws DataAccessException
     */
    public List<String> getLastDocuments(String lastDOI, int amount)
    throws DataAccessException;
    /**
     * 
     * @param doi
     * @return Returns a list of keyphrases of the document with doi
     * @throws DataAccessException
     */
    public List<String> getKeyphrase(String doi)
    throws DataAccessException;
} //- interface DocumentDAO
