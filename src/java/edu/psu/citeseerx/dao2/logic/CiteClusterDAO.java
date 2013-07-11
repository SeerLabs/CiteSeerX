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
import java.util.List;
import java.util.Date;

import edu.psu.citeseerx.domain.*;
import org.json.JSONException;

public interface CiteClusterDAO {

    /**
     * Cluster the given document into the group it belongs to using the
     * given keys. If no group is found, a new one is created. 
     * 
     * Use this method when the document is being clustered for the first time
     * otherwise use @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#reclusterDocument
     * @param key List of strings used to find the cluster the given document
     *            belongs to.
     * @param doc The document being clustered
     * @return The cluster id where the document is put
     * @throws DataAccessException
     * @throws JSONException
     */
    public Long clusterDocument(List<String> key, Document doc)
    throws DataAccessException, JSONException;
    
    /**
     * Changes the cluster of the given document based on the provided keys.
     * Use this method to cluster documents that are already part of the
     * corpus otherwise use @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#clusterDocument
     * @param keys List of strings used to find the cluster the given document
     *            belongs to.
     * @param doc The document being reclustered
     * @return The cluster id where the document is put
     * @throws DataAccessException
     * @throws JSONException
     */
    public Long reclusterDocument(List<String> keys, Document doc)
    throws DataAccessException, JSONException;
    
    /**
     * Obtains amount documents beginning in start which cites documents in 
     * the given cluster.
     * @param clusterid The cited cluster
     * @param start     Starting point
     * @param amount    How many documents are returned from start
     * @return A list, of size amount or less, which contains ThinDoc that
     *         cites by the given cluster cluster identifier.
     * @throws DataAccessException
     */
    public List<ThinDoc> getCitingDocuments(Long clusterid, int start, 
            int amount) throws DataAccessException;
    
    /**
     * Obtains amount documents beginning in start which are cited by documents  
     * that belong to the given cluster.
     * @param clusterid The citing cluster
     * @param start     Starting point
     * @param amount    How many documents are returned from start
     * @return A list, of size amount or less, which contains ThinDoc that
     *         are cited by documents that belongs to the given cluster.
     * @throws DataAccessException
     */
    public List<ThinDoc> getCitedDocuments(Long clusterid, int start, 
            int amount) throws DataAccessException;
    
    /**
     * Gets all the clusters that have documents which cites the given cluster.
     * @param clusterid The cited cluster
     * @return A List of cluster identifiers which have documents that cites
     *         the given cluster.
     * @throws DataAccessException
     */
    public List<Long> getCitingClusters(Long clusterid) 
    throws DataAccessException;

    /**
     * Gets all the clusters which are cited by documents in the given cluster
     * @param clusterid The citing cluster
     * @return A List of cluster identifiers which are cited by documents that
     *         belongs to the given cluster.
     * @throws DataAccessException
     */
    public List<Long> getCitedClusters(Long clusterid)
    throws DataAccessException;

    /**
     * Gets the top amount of cluster which have documents that cites the 
     * given cluster.
     * @param clusterid The cited cluster
     * @param amount    The maximum number of citing clusters
     * @return A List which contains the identifiers of the top amount of
     *         clusters which cites the given cluster. The size of the list
     *         might be less than amount. 
     * @throws DataAccessException
     */
    public List<Long> getCitingClusters(Long clusterid, int amount)
    throws DataAccessException;

    /**
     * Gets the top amount of cluster which are cited by documents that belongs
     * to the given cluster.
     * @param clusterid The citing cluster
     * @param amount    The maximum number of cited clusters
     * @return A List which contains the identifiers of the top amount of
     *         cited clusters. The size of the list might be less than amount. 
     * @throws DataAccessException
     */
    public List<Long> getCitedClusters(Long clusterid, int amount)
    throws DataAccessException;

    /**
     * Removes the given document from the cluster it belongs to.
     * Use with caution. This method usually is part of a bigger operation. For
     * instance, re-clustering a document which implies first delete the 
     * document from the actual cluster and then cluster it again
     * @param doc   The document being remove from the cluster.
     * @throws DataAccessException
     */
    public void deleteDocument(Document doc) throws DataAccessException;
    
    /**
     * In some cases, usually due to document re-clustering, clusters end up 
     * being empty. In such cases, the cluster identifier is stored in the 
     * deleted persistent storage so changes can be propagated to other modules.
     * After those changes are propagated the cluster is removed forever.
     * @param clusterid
     * @throws DataAccessException
     */
    public void removeDeletion(Long clusterid) throws DataAccessException;
    
    /**
     * Remove all the clusters, from the deleted persistent storage, that were
     * deleted before the giving date.
     * @see CiteClusterDAO#removeDeletion(Long)
     * @param date
     */
    public void removeDeletions(Date date);
    
    /**
     * Returns all the cluster identifiers that have been deleted before the
     * given date.
     * @see CiteClusterDAO#removeDeletion(Long)
     * @param date
     * @return A List of cluster identifiers deleted before the given date.
     */
    public List<Long> getDeletions(Date date);
    
    /**
     * Gets amount clusters beginning from start for those clusters which have
     * the full paper in it.
     * @param start
     * @param amount
     * @return 
     * @throws DataAccessException
     */
    public List<ThinDoc> getClustersInCollection(Long start, int amount)
    throws DataAccessException;
    
    /**
     * @param clusterid
     * @return A list of paper identifiers that belong to the given cluster
     * @throws DataAccessException
     */
    public List<String> getPaperIDs(Long clusterid) throws DataAccessException;
    
    /**
     * 
     * @param clusterid
     * @return A list of citation identifiers which belong to the same cluster.
     * @throws DataAccessException
     */
    public List<Long> getCitationIDs(Long clusterid) throws DataAccessException;
    
    /**
     * Gets the canonical representation of the Paper / Citation for the given
     * cluster.
     * @param clusterid
     * @return the canonical representation of the paper / citation grouped in 
     * the given cluster.
     * @throws DataAccessException
     */
    public ThinDoc getThinDoc(Long clusterid) throws DataAccessException;
    
    /**
     * Gets amount clusters beginning at id that have been updated after the
     * giving date.
     * @param date
     * @param id
     * @param amount
     * @return The canonical representation of the clusters beginning at id. 
     *         The size of the returned list could be less than amount
     * @throws DataAccessException
     */
    public List<ThinDoc> getClustersSinceTime(Date date, Long id, int amount)
    throws DataAccessException;
    
    /**
     * Updates the cluster
     * @param cluster
     * @param changed
     * @throws DataAccessException
     */
    public void updateCluster(ThinDoc cluster, boolean changed)
    throws DataAccessException;
    
    public String getContext(Long citing, Long cited)
    throws DataAccessException;
    
    public Long getMinClusterID() throws DataAccessException;
    
    public Long getMaxClusterID() throws DataAccessException;
    
    public Date checkInfUpdateRequired(Long cid) throws DataAccessException;
    
    public void insertInfUpdateTime(Long cid, Date time)
    throws DataAccessException;
    
    public void setLastIndexTime(Date time);
    
    public Date getLastIndexTime();
    
    /**
     * Sets the last time the author deduplication job was run 
     * @param time
     */
    public void setLastAuthorDedupTime(Date time);
    
    /**
     * 
     * @return the last time the author deduplication job was run
     */
    public Date getLastAuthorDedupTime();
    
} //- Interface CiteClusterDAO
