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
import java.util.List;
import edu.psu.citeseerx.domain.Author;

/**
 * Provides transparent access to Author persistence storage 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface AuthorDAO {

    /**
     * 
     * @param docID
     * @param getSource If true, affiliation, name, email, and order are
     * included from the latest version of metadata in the system (Corrected)
     * @return Returns the authors associated to the given document.
     * @throws DataAccessException
     */
    public List<Author> getDocAuthors(String docID, boolean getSource)
    throws DataAccessException;

    /**
     * Associates the given author to the given document
     * @param docID
     * @param auth
     * @throws DataAccessException
     */
    public void insertAuthor(String docID, Author auth)
    throws DataAccessException;
    
    /**
     * Updates information about the author
     * @param auth
     * @throws DataAccessException
     */
    public void updateAuthor(Author auth) throws DataAccessException;
    
    /**
     * Assigns a clusterID to the given author
     * @param auth
     * @param clusterID
     * @throws DataAccessException
     */
    public void setAuthCluster(Author auth, Long clusterID)
    throws DataAccessException;
    
    /**
     * Deletes all the authors associated to the given document ID
     * @param docID
     * @throws DataAccessException
     */
    public void deleteAuthors(String docID) throws DataAccessException;
    
    /**
     * Deletes de author represented by authorID
     * @param authorID
     * @throws DataAccessException
     */
    public void deleteAuthor(Long authorID) throws DataAccessException;
    
} //- interface AuthorDAO
