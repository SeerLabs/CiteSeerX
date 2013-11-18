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
import edu.psu.citeseerx.domain.Citation;

/**
 * Provides transparent access to Citations persistence storage 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public interface CitationDAO {

    /**
     * 
     * @param docID
     * @param getContexts If true, Citation contexts is included.
     * @return A list of Citation objects associated to docID. 
     * @throws DataAccessException
     */
    public List<Citation> getCitations(String docID, boolean getContexts)
    throws DataAccessException;
    
    /**
     * 
     * @param clusterid
     * @return A list of Citation objects associated to the given clusterid
     * @throws DataAccessException
     */
    public List<Citation> getCitationsForCluster(Long clusterid)
    throws DataAccessException;
    
    /**
     * 
     * @param startID
     * @param n
     * @return a List of n citation objects beginning at startID
     * @throws DataAccessException
     */
    public List<Citation> getCitations(long startID, int n)
    throws DataAccessException;
    
    /**
     * 
     * @param id
     * @return The citation associated to the given id
     * @throws DataAccessException
     */
    public Citation getCitation(long id) throws DataAccessException;
    
    /**
     * Inserts a citation associated to the given document
     * @param DOI
     * @param citation
     * @throws DataAccessException
     */
    public void insertCitation(String DOI, Citation citation)
    throws DataAccessException;
    
    /**
     * 
     * @param citationID
     * @return A list of Strings containing all the context where the given
     * citation appears
     * @throws DataAccessException
     */
    public List<String> getCiteContexts(Long citationID) 
    throws DataAccessException;
    
    /**
     * Inserts the given contexts associated to the given citationID
     * @param citationID
     * @param contexts
     * @throws DataAccessException
     */
    public void insertCiteContexts(Long citationID, List<String> contexts)
    throws DataAccessException;
    
    /**
     * Associates the given citation to the given clusterID
     * @param citation
     * @param clusterID
     * @throws DataAccessException
     */
    public void setCiteCluster(Citation citation, Long clusterID)
    throws DataAccessException;
    
    /**
     * Deletes all the citations associated to the given DOI
     * @param DOI
     * @throws DataAccessException
     */
    public void deleteCitations(String DOI) throws DataAccessException;
    
    /**
     * Deletes the citation represented by citationID
     * @param citationID
     * @throws DataAccessException
     */
    public void deleteCitation(Long citationID) throws DataAccessException;

    /**
     * Deletes all the contexts associated to the given citationID 
     * @param citationID
     * @throws DataAccessException
     */
    public void deleteCiteContexts(Long citationID) throws DataAccessException;
    
    /**
     * @return Number of total citations within the system
     * @throws DataAccessException
     */
    public Integer getNumberOfCitationsRecords() throws DataAccessException;
} //- interface CitationDAO
