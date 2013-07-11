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
import edu.psu.citeseerx.domain.Hub;
import java.util.List;

/**
 * Provides transparent access to HUBs for URLs persistence storage 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface HubDAO {

    /**
     * @param doi
     * @return A list of hub pages which has references to the given DOI.
     * @throws DataAccessException
     */
    public List<Hub> getHubs(String doi) throws DataAccessException;

    /**
     * @param url
     * @return A list of hubs which point to a given URL
     * @throws DataAccessException
     */
    public List<Hub> getHubsForUrl(String url) throws DataAccessException;
    
    /**
     * @param url
     * @return The hub which correspond to the given URL
     * @throws DataAccessException
     */
    public Hub getHub(String url) throws DataAccessException;
    
    /**
     * Insert a new hub record.  If a hub already exists in the database
     * with a url that matches the one supplied, the hub will be updated
     * will the new values.  For this reason, ONLY INSERT HUBS THAT HAVE
     * ALL FIELDS POPULATED using this method.  Otherwise, data can be lost.
     * If you want a non-destructive hub insert, see addHubMapping.
     * @param hub
     * @throws DataAccessException
     */
    public long insertHub(Hub hub) throws DataAccessException;
    
    /**
     * Updates the hub
     * @param hub
     * @throws DataAccessException
     */
    public void updateHub(Hub hub) throws DataAccessException;
    
    /**
     * Stores a mapping between the URL representes by urlID and the given
     * hubID
     * @param urlID
     * @param hubID
     * @throws DataAccessException
     */
    public void insertHubMapping(long urlID, long hubID)
    throws DataAccessException;
    
    /**
     * 
     * @param doi
     * @return All the URLs associated to the given document identifier
     * @throws DataAccessException
     */
    public List<String> getUrls(String doi) throws DataAccessException;
    
    /**
     * Associates an URL to a document
     * @param doi
     * @param url
     * @return the url id assigned by the system
     * @throws DataAccessException
     */
    public long insertUrl(String doi, String url) throws DataAccessException;
    
    /**
     * Returns all the paper ids associated with the given hubUrl
     * @param hubUrl
     * @return A list containing all the paper ids associated with the given 
     * URL. Null if no papers are found.
     * @throws DataAccessException
     */
    public List<String> getPaperIdsFromHubUrl(String hubUrl) 
    throws DataAccessException; 
    
    /**
     * Adds a hub mapping between a document and an URL
     * @param hub
     * @param url
     * @param doi
     * @throws DataAccessException
     */
    public void addHubMapping(Hub hub, String url, String doi)
    throws DataAccessException;
    
} //- interface HubDao
