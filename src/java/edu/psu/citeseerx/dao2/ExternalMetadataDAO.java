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

import java.util.List;

import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.domain.ACM;
import edu.psu.citeseerx.domain.CiteULike;
import edu.psu.citeseerx.domain.DBLP;

/**
 * Provides transparent access to DBLP records persistence storage 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public interface ExternalMetadataDAO {
    /**
     * Adds a new DBLP record into the database
     * @param record
     * @throws DataAccessException
     */
    public void addDBLPRecord(DBLP record) throws DataAccessException;
    
    /**
     * Deletes all the records
     * @throws DataAccessException
     */
    public void deleteDBLP() throws DataAccessException;
    
    /**
     * @param title
     * @return Returns all the records that match the given title
     * @throws DataAccessException
     */
    public List<DBLP> getDBLPRecordsByTitle(String title) 
    throws  DataAccessException;
    
    /**
     * Adds a new CiteULike record into the database 
     * @param record
     * @throws DataAccessException
     */
    public void addCiteULikeRecord(CiteULike record) throws DataAccessException;
    
    /**
     * @param doi
     * @return A CiteULike record associated to a given CiteSeerX doi or null if
     * no one is fuound
     * @throws DataAccessException
     */
    public CiteULike getCiteULikeRecordByDOI(String doi) 
    throws DataAccessException;
    
    /**
     * Adds a new ACM record into the database
     * @param record
     * @throws DataAccessException
     */
    public void addACMRecord(ACM record) throws DataAccessException;

    /**
     * @param title
     * @return Returns all the records that match the given title
     * @throws DataAccessException
     */
    public List<ACM> getACMRecordsByTitle(String title)
    throws  DataAccessException;
} //- interface ExternalMetadataDAO
