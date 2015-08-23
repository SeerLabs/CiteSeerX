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

import java.util.*;
import org.json.*;

import java.util.List;
import java.util.Date;
import java.sql.*;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import edu.psu.citeseerx.dao2.InferenceBuilder;
import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.citeinf.*;

public class CiteClusterDAOImpl extends JdbcDaoSupport
implements CiteClusterDAO {

    private GetGraphMapping getGraphMapping;
    private InsertGraphMapping insertGraphMapping;
    private UpdateGraphContext updateGraphContext;
    private GetCiting getCiting;
    private GetCited getCited;
    private GetCitingClusters getCitingClusters;
    private GetCitedClusters getCitedClusters;
    private GetTopCitingClusters getTopCitingClusters;
    private GetTopCitedClusters getTopCitedClusters;
    private InsertCluster insertCluster;
    private InsertDoc insertDoc;
    private SetInCollection setInCollection;
    private InsertCitation insertCitation;
    private GetClusterID getClusterID;
    private InsertKeyMapping insertKeyMapping;
    private GetClustersInCollection getClustersInCollection;
    private GetPaperIDs getPaperIDs;
    private GetCiteIDs getCiteIDs;
    private GetCluster getCluster;
    private UpdateCluster updateCluster;
    private UpdateObs updateObs;
    private GetClustersSinceTime getClustersSinceTime;
    private GetCiteContext getCiteContext;
    private GetMinClusterID getMinClusterID;
    private GetMaxClusterID getMaxClusterID;
    private GetInfTime getInfTime;
    private UpdateInfTime updateInfTime;
    private InsertInfTime insertInfTime;
    private GetUpdateTime getUpdateTime;
    private InsertIndexTime insertIndexTime;
    private UpdateIndexTime updateIndexTime;
    private GetIndexTime getIndexTime;
    private InsertAuthorDedupTime insertAuthorDedupTime;
    private UpdateAuthorDedupTime updateAuthorDedupTime;
    private GetAuthorDedupTime getAuthorDedupTime;
    
    private DeleteCiting deleteCiting;
    private DeleteCited deleteCited;
    private DeleteCitation deleteCitation;
    private DeletePaper deletePaper;
    private DeleteInfUpdate deleteInfUpdate;
    private DeleteCluster deleteCluster;
    private DeleteDeletion deleteDeletion;
    private DeleteDeletions deleteDeletions;
    private GetDeletions getDeletions;
    private DeleteCitations deleteCitations;
    private DeleteKeyMappings deleteKeyMappings;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getGraphMapping = new GetGraphMapping(getDataSource());
        insertGraphMapping = new InsertGraphMapping(getDataSource());
        updateGraphContext = new UpdateGraphContext(getDataSource());
        getCiting = new GetCiting(getDataSource());
        getCited = new GetCited(getDataSource());
        getCitingClusters = new GetCitingClusters(getDataSource());
        getCitedClusters = new GetCitedClusters(getDataSource());
        getTopCitingClusters = new GetTopCitingClusters(getDataSource());
        getTopCitedClusters = new GetTopCitedClusters(getDataSource());
        insertCluster = new InsertCluster(getDataSource());
        insertDoc = new InsertDoc(getDataSource());
        setInCollection = new SetInCollection(getDataSource());
        insertCitation = new InsertCitation(getDataSource());
        getClusterID = new GetClusterID(getDataSource());
        insertKeyMapping = new InsertKeyMapping(getDataSource());
        getClustersInCollection = new GetClustersInCollection(getDataSource());
        getPaperIDs = new GetPaperIDs(getDataSource());
        getCiteIDs = new GetCiteIDs(getDataSource());
        getCluster = new GetCluster(getDataSource());
        updateCluster = new UpdateCluster(getDataSource());
        updateObs = new UpdateObs(getDataSource());
        getClustersSinceTime = new GetClustersSinceTime(getDataSource());
        getCiteContext = new GetCiteContext(getDataSource());
        getMinClusterID = new GetMinClusterID(getDataSource());
        getMaxClusterID = new GetMaxClusterID(getDataSource());
        getInfTime = new GetInfTime(getDataSource());
        updateInfTime = new UpdateInfTime(getDataSource());
        insertInfTime = new InsertInfTime(getDataSource());
        getUpdateTime = new GetUpdateTime(getDataSource());
        insertIndexTime = new InsertIndexTime(getDataSource());
        updateIndexTime = new UpdateIndexTime(getDataSource());
        getIndexTime = new GetIndexTime(getDataSource());
        insertAuthorDedupTime = new InsertAuthorDedupTime(getDataSource());
        updateAuthorDedupTime = new UpdateAuthorDedupTime(getDataSource());
        getAuthorDedupTime = new GetAuthorDedupTime(getDataSource());
        
        deleteCiting = new DeleteCiting(getDataSource());
        deleteCited = new DeleteCited(getDataSource());
        deleteCitation = new DeleteCitation(getDataSource());
        deletePaper = new DeletePaper(getDataSource());
        deleteInfUpdate = new DeleteInfUpdate(getDataSource());
        deleteCluster = new DeleteCluster(getDataSource());
        deleteDeletion = new DeleteDeletion(getDataSource());
        deleteDeletions = new DeleteDeletions(getDataSource());
        getDeletions = new GetDeletions(getDataSource());
        deleteCitations = new DeleteCitations(getDataSource());
        deleteKeyMappings = new DeleteKeyMappings(getDataSource());
        
    }  //- initMappingSqlQueries
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#clusterDocument(java.util.List, edu.psu.citeseerx.domain.Document)
     */
    public Long clusterDocument(List<String> keys, Document doc)
    throws DataAccessException, JSONException {
        
        String keyFound = null;
        List<Long> cids = new ArrayList<Long>();

        if (!keys.isEmpty()) {
            Long cid = getClusterID.run(keys.get(0));
            if (cid != null) {
                // Only test the first (best) key for documents
                keyFound = keys.get(0);
                cids.add(cid);
            }
        }

        if (cids.size() == 0) {
            Long cid = insertCluster(doc, keys);
            cids.add(cid);
            insertDocument(doc, cid);
        } else {
            Long cid = (Long)cids.get(0);
            for (String key : keys) {
                if (!key.equalsIgnoreCase(keyFound)) {
                    insertKeyMapping.run(key, cid);
                }
            }
            insertDocument(doc, cid);
        }
        
        for (Citation citation : doc.getCitations()) {
            clusterCitation(citation.getKeys(), citation, (Long)cids.get(0));
        }
        
        return (Long)cids.get(0);
        
    }  //- clusterDocument
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#reclusterDocument(java.util.List, edu.psu.citeseerx.domain.Document)
     */
    public Long reclusterDocument(List<String> keys, Document doc)
    throws DataAccessException, JSONException {
        
        deleteDocument(doc);
        return clusterDocument(keys, doc);
        
    } //- reclusterDocument

    /**
     * Cluster the given citation into the group it belongs to using the given
     * keys. If no group is found, a new one is created. Additionally, storages
     * the map  between the citing cluster and the cluster where the citation
     * is put into.
     * @param keys      List of strings used to find the cluster the given
     *                  citation belongs to.
     * @param citation  The citation being clustered
     * @param docCID    The cluster identifier that holds the document which
     *                  contains the given citation.
     * @throws DataAccessException
     * @throws JSONException
     */
    private void clusterCitation(List<String> keys, Citation citation,
            Long docCID) throws DataAccessException, JSONException {
        
        if (keys.isEmpty()) {
            return;
        }
        
        String keyFound = null;
        List<Long> cids = new ArrayList<Long>();

        for (String key : keys) {
            Long cid = getClusterID.run(key);
            if (cid != null) {
                keyFound = key;
                cids.add(cid);
            }
        }
        
        String context = null;
        if (!citation.getContexts().isEmpty()) {
            context = citation.getContexts().get(0);
        }
        
        if (cids.size()==0) {
            Long cid = insertCluster(false, keys);
            insertCitation(citation, cid);
            insertGraphMapping(docCID, cid, context, citation.isSelf());
        } else if (cids.size() > 1) {
            for (String key : keys) {
                Long cid = getClusterID.run(key);
                if (cid != null) {
                    insertCitation(citation, cid);
                    insertGraphMapping(docCID, cid, context,
                            citation.isSelf());
                    break;
                }
            }
        } else if (cids.size() == 1) {
            Long cid = (Long)cids.get(0);
            for (String key : keys) {
                if (!key.equals(keyFound)) {
                    insertKeyMapping.run(key, cid);
                }
            }
            insertCitation(citation, cid);
            insertGraphMapping(docCID, cid, context, citation.isSelf());
        }
        
    }  //- clusterCitation
    
    /**
     * Storages the relationship between a citing cluster represented by docCID
     * and a cited cluster represented by cid. In addition to the relationship
     * the context where the citation appears and a flag indicating if the 
     * citation is a self cite is stored.
     * @param docCID    The citing cluster.
     * @param cid       The cited cluster.
     * @param context   Reference text where the cited cluster is cited in the
     *                  citing cluster.
     * @param isSelf    True if the citation is a self-cite.
     */
    protected void insertGraphMapping(Long docCID, Long cid,
            String context, boolean isSelf) {

        // Don't allow clusters to cite themselves.
        if (docCID.longValue() == cid.longValue()) {
            return;
        }
        
        GraphMapping mapping = getGraphMapping.run(docCID, cid);
        if (mapping == null) {
            insertGraphMapping.run(docCID, cid, context, isSelf);
        } else {
            if (mapping.getFirstContext() == null) {
                updateGraphContext.run(mapping.getId(), context);
            }
        }
        
    }  //- insertGraphMapping
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getCitingDocuments(java.lang.Long, int, int)
     */
    public List<ThinDoc> getCitingDocuments(Long clusterid, int start, 
            int amount) throws DataAccessException {
        int limit = start+amount;
        List<ThinDoc> citing = getCiting.run(clusterid, limit);
        return citing.subList(start, citing.size());

    }  //- getCitingDocuments

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getCitedDocuments(java.lang.Long, int, int)
     */
    public List<ThinDoc> getCitedDocuments(Long clusterid, int start, 
            int amount) throws DataAccessException {
        int limit = start+amount;
        List<ThinDoc> cited = getCited.run(clusterid, limit);
        return cited.subList(start, cited.size());

    }  //- getCitedDocuments
    

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getCitingClusters(java.lang.Long)
     */
    public List<Long> getCitingClusters(Long clusterid) 
    throws DataAccessException {
        return getCitingClusters.run(clusterid);
    }  //- getCitingClusters

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getCitedClusters(java.lang.Long)
     */
    public List<Long> getCitedClusters(Long clusterid) 
    throws DataAccessException {
        return getCitedClusters.run(clusterid);
    }  //- getCitedClusters

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getCitingClusters(java.lang.Long, int)
     */
    public List<Long> getCitingClusters(Long clusterid, int amount)
    throws DataAccessException {
        return getTopCitingClusters.run(clusterid, amount);
    }  //- getCitingClusters

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getCitedClusters(java.lang.Long, int)
     */
    public List<Long> getCitedClusters(Long clusterid, int amount)
    throws DataAccessException {
        return getTopCitedClusters.run(clusterid, amount);
    }  //- getCitedClusters
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#deleteDocument(edu.psu.citeseerx.domain.Document)
     */
    public void deleteDocument(Document doc) throws DataAccessException {
        
        String doi = doc.getDatum(Document.DOI_KEY);
        Long cid = doc.getClusterID();
                
        for (Object o : getCited.run(cid, Integer.MAX_VALUE)) {
            ThinDoc cite = (ThinDoc)o;
            if (cite.getNcites() <= 1 && !cite.getInCollection()) {
                deleteCluster(cite.getCluster());
            }
        }
        
        deleteCiting.update(cid);
        deleteCitations.update(doi);
        
        deletePaper.update(doi);
        
        List<String> papers = getPaperIDs(cid);
        if (papers.size() == 0) {
            setInCollection.run(cid, false);
        }
        if (doc.getNcites() == 0 && papers.size() == 0) {
            deleteCluster(cid);
        }

    }  //- deleteDocument
    
    /**
     * Deletes the cluster represented by the given identifier. Deleting a 
     * cluster means:
     * <ul><ol>Delete all the references to cited clusters</ol>
     * <ol>Delete all the references from citing clusters</ol>
     * <ol>Delete the inference update information related to this cluster</ol>
     * <ol>Delete all the keys for the given cluster</ol>
     * <ol>Finally, delete the cluster</ol>
     * </ul>
     * @param cid The cluster to be deleted.
     */
    protected void deleteCluster(Long cid) {
        
        deleteCited.update(cid);
        deleteCiting.update(cid);
        deleteInfUpdate.update(cid);
        deleteKeyMappings.update(cid);
        deleteCluster.update(cid);
        
    }  //- deleteCluster
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#removeDeletion(java.lang.Long)
     */
    public void removeDeletion(Long cid) throws DataAccessException {
        deleteDeletion.update(cid);
    } //- removeDeletion
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#removeDeletions(java.util.Date)
     */
    public void removeDeletions(Date date) {
        deleteDeletions.run(date);
    } //- removeDeletions
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getDeletions(java.util.Date)
     */
    public List<Long> getDeletions(Date date) {
        return getDeletions.run(date);
    } //- getDeletions
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#checkInfUpdateRequired(java.lang.Long)
     */
    public Date checkInfUpdateRequired(Long cid) throws DataAccessException {
        Date upTime = getUpdateTime.run(cid);
        if (upTime == null) {
            return null;
        }
        Date lastInf = getInfTime.run(cid);
        if (lastInf == null) {
            insertInfTime.run(cid, new Date(0));
            return upTime;
        }
        if (upTime.after(lastInf)) {
            return upTime;
        } else {
            return null;
        }
        
    }  //- checkInfUpdateRequired
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#insertInfUpdateTime(java.lang.Long, java.util.Date)
     */
    public void insertInfUpdateTime(Long cid, Date time)
    throws DataAccessException {
        updateInfTime.run(cid, time);
    } //- insertInfUpdateTime

    /**
     * Creates a new cluster and stores the associated keys.
     * This method is usually used when creating the cluster from a citation.
     * Additional information will be added to the cluster when the inference
     * process is run.
     * @param inCollection  True if the cluster includes the full paper.
     * @param keys          The set of keys which identifies the citation.
     * @return The assigned cluster identifier.
     * @throws DataAccessException
     */
    private Long insertCluster(boolean inCollection, List<String> keys)
    throws DataAccessException {
        Long cid = insertCluster.run(inCollection, null, null);
        for (String key : keys) {
            insertKeyMapping.run(key, cid);
        }                
        return cid;
        
    }  //- insertCluster

    /**
     * Creates a new cluster and stores the associated keys.
     * This method is used when creating the cluster for a paper. In addition, 
     * to the basic information, authors and title information will also be
     * stored as part of the cluster information.
     * @param doc   The document associated to the new cluster.
     * @param keys  The set of keys which identifies the document.
     * @return The assigned cluster identifier.
     * @throws DataAccessException
     */
    private Long insertCluster(Document doc, List<String> keys)
    throws DataAccessException {
        StringBuffer authBuf = new StringBuffer();
        for (Iterator<Author> it = doc.getAuthors().iterator(); it.hasNext();) {
            String name = it.next().getDatum(Author.NAME_KEY, Author.UNENCODED);
            authBuf.append(name);
            if (it.hasNext()) {
                authBuf.append(",");
            }
        }
        String authors = authBuf.toString();
        String title = doc.getDatum(Document.TITLE_KEY, Document.UNENCODED);
        Long cid = insertCluster.run(true, authors, title);
        for (String key : keys) {
            insertKeyMapping.run(key, cid);
        }        
        return cid;

    }  //- insertCluster

    /**
     * Stores all the information about the given document and the cluster it 
     * belongs to.
     * @param doc   The document associated to the cid.
     * @param cid   The cluster where the given document belongs to.
     * @throws DataAccessException
     */
    private void insertDocument(Document doc, Long cid)
    throws DataAccessException {
        insertDoc.run(doc.getDatum(Document.DOI_KEY), cid);
        setInCollection.run(cid, true);
        doc.setClusterID(cid);
                    
    }  //- insertDocument
    
    /**
     * Relates the given citation with the given cluster identifier. Since the
     * given citation is part of the cluster observation are updated.
     * @param citation
     * @param cid
     * @throws DataAccessException
     * @throws JSONException
     */
    private void insertCitation(Citation citation, Long cid)
    throws DataAccessException, JSONException {
        insertCitation.run(citation, cid);
        updateObservations(citation, cid);
        
    }  //- insertCitation

    /**
     * Add the citation metadata to the given cluster as an observation. 
     * Updating the canonical metadata if necessary. All cahnges are stored.
     * @param citation
     * @param cid
     * @throws DataAccessException
     * @throws JSONException
     */
    private void updateObservations(Citation citation, Long cid)
    throws DataAccessException, JSONException {
                
        StringBuffer authorBuf = new StringBuffer();
        for (Iterator<String> it = citation.getAuthorNames().iterator();
        it.hasNext(); ) {
            authorBuf.append(it.next());
            if (it.hasNext()) {
                authorBuf.append(",");
            }
        }
        
        String yearStr = citation.getDatum(Citation.YEAR_KEY);
        String volStr  = citation.getDatum(Citation.VOL_KEY);
        String numStr  = citation.getDatum(Citation.NUMBER_KEY);
        
        ThinDoc thinDoc = new ThinDoc();
        thinDoc.setAuthors(authorBuf.toString());
        thinDoc.setTitle(citation.getDatum(Citation.TITLE_KEY));
        thinDoc.setVenue(citation.getDatum(Citation.VENUE_KEY));
        thinDoc.setVentype(citation.getDatum(Citation.VEN_TYPE_KEY));
        thinDoc.setPages(citation.getDatum(Citation.PAGES_KEY));
        thinDoc.setPublisher(citation.getDatum(Citation.PUBLISHER_KEY));
        thinDoc.setTech(citation.getDatum(Citation.TECH_KEY));
        try {thinDoc.setYear(Integer.parseInt(yearStr));} catch (Exception e) {}
        try {thinDoc.setVol(Integer.parseInt(volStr));}   catch (Exception e) {}
        try {thinDoc.setNum(Integer.parseInt(numStr));}   catch (Exception e) {}
        thinDoc.setCluster(cid);
        
        ThinDoc cluster = getThinDoc(cid);
        JSONObject metadata = new JSONObject();
        if (cluster != null && cluster.getObservations() != null) {
            metadata = new JSONObject(cluster.getObservations());
        }
        boolean updated = InferenceBuilder.addObservation(thinDoc, metadata);

        updateCluster(thinDoc, updated);

    }  //- updateObservations
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getClustersInCollection(java.lang.Long, int)
     */
    public List<ThinDoc> getClustersInCollection(Long start, int amount) 
    throws DataAccessException {
        return getClustersInCollection.run(start, amount);
    }  //- getClustersInCollection    
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getPaperIDs(java.lang.Long)
     */
    public List<String> getPaperIDs(Long clusterid) throws DataAccessException {
        return getPaperIDs.run(clusterid);
    }  //- getPaperIDs

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getCitationIDs(java.lang.Long)
     */
    public List<Long> getCitationIDs(Long clusterid)
    throws DataAccessException {
        return getCiteIDs.run(clusterid);
    }  //- getPaperIDs

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getThinDoc(java.lang.Long)
     */
    public ThinDoc getThinDoc(Long clusterid) throws DataAccessException {
        return getCluster.run(clusterid);
    }  //- getThinDoc

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#updateCluster(edu.psu.citeseerx.domain.ThinDoc, boolean)
     */
    public void updateCluster(ThinDoc cluster, boolean changed)
    throws DataAccessException {
        if (changed) {
            updateCluster.run(cluster, changed);
        }
        updateObs.run(cluster.getObservations(), cluster.getCluster());

    }  //- updateCluster    

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getClustersSinceTime(java.util.Date, java.lang.Long, int)
     */
    public List<ThinDoc> getClustersSinceTime(Date date, Long lastID, 
            int amount) throws DataAccessException {
        return getClustersSinceTime.run(date, lastID, amount);
    } //- getClustersSinceTime
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getContext(java.lang.Long, java.lang.Long)
     */
    public String getContext(Long citing, Long cited)
    throws DataAccessException {
        return getCiteContext.run(citing, cited);
    }  //- getContext
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getMinClusterID()
     */
    public Long getMinClusterID() throws DataAccessException {
        return getMinClusterID.run();
    }  //- getMinClusterID

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getMaxClusterID()
     */
    public Long getMaxClusterID() throws DataAccessException {
        return getMaxClusterID.run();
    }  //- getMaxClusterID
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#setLastIndexTime(java.util.Date)
     */
    public void setLastIndexTime(Date date) {
        Date lastIndex = getIndexTime.run();
        if (lastIndex == null) {
            insertIndexTime.run(date);
        } else {
            updateIndexTime.run(date);
        }
        
    }  //- setLastIndexTime
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getLastIndexTime()
     */
    public Date getLastIndexTime() {
        Date lastIndex = getIndexTime.run();
        if (lastIndex == null) {
            lastIndex = new Date(0);
        }
        return lastIndex;
        
    }  //- getLastIndexTime
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#getLastAuthorDedupTime()
     */
    public Date getLastAuthorDedupTime() {
        Date lastDedup = getAuthorDedupTime.run();
        if (null == lastDedup) {
            lastDedup = new Date(0);
        }
        return lastDedup;
    } //- getLastAuthorDedupTime


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CiteClusterDAO#setLastAuthorDedupTime(java.util.Date)
     */
    public void setLastAuthorDedupTime(Date time) {
        Date lastDedup = getAuthorDedupTime.run();
        if (lastDedup == null) {
            insertAuthorDedupTime.run(time);
        } else {
            updateAuthorDedupTime.run(time);
        }
    } //- setLastAuthorDedupTime



    private static final String DEF_GET_GRAPH_MAPPING_QUERY =
        "select id, firstContext from citegraph where citing=? and cited=?";
    
    private class GetGraphMapping extends MappingSqlQuery {
        
        public GetGraphMapping(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_GRAPH_MAPPING_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public GraphMapping mapRow(ResultSet rs, int rowNum)
        throws SQLException {
            GraphMapping mapping = new GraphMapping();
            mapping.setId(rs.getLong(1));
            mapping.setFirstContext(rs.getString(2));
            return mapping;
        }
        
        public GraphMapping run(Long citing, Long cited) {
            Object[] params = new Object[] { citing, cited };
            List<GraphMapping> list = execute(params);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
        
    }  //- class GetGraphMapping
    
    
    /* ciging, cited, firstContext */
    private static final String DEF_INSERT_GRAPH_MAPPING_STMT =
        "insert into citegraph values (NULL, ?, ?, ?, ?)";
    
    private class InsertGraphMapping extends SqlUpdate {
        
        public InsertGraphMapping(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_GRAPH_MAPPING_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.TINYINT));
            compile();
        }
        
        public int run(Long citing, Long cited, String context, boolean self) {
            Object[] params = new Object[] { citing, cited, context, self };
            return update(params);
        }
        
    }  //- class InsertGraphMapping
        
    
    private static final String DEF_UPDATE_GRAPH_CONTEXT_STMT =
        "update citegraph set firstContext=? where id=?";
    
    private class UpdateGraphContext extends SqlUpdate {
        
        public UpdateGraphContext(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_GRAPH_CONTEXT_STMT);
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(Long id, String context) {
            Object[] params = new Object[] { context, id };
            return update(params);
        }
        
    }  //- class UpdateGraphContext
        
    
    private static final String DEF_GET_CITING_QUERY =
        "select clusters.id, cauth, ctitle, cvenue, cyear, cpages, " +
        "cpublisher, cvol, cnum, ctech, incollection, " +
        "size, firstContext, selfCites, updated from clusters, citegraph " +
        "where citing=clusters.id and cited=? order by size desc limit ?";
    
    private class GetCiting extends MappingSqlQuery {
        
        public GetCiting(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITING_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            ThinDoc doc = new ThinDoc();
            doc.setCluster(rs.getLong(1));
            doc.setAuthors(rs.getString("cauth"));
            doc.setTitle(rs.getString("ctitle"));
            doc.setVenue(rs.getString("cvenue"));
            try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
            try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
            try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
            doc.setPages(rs.getString("cpages"));
            doc.setPublisher(rs.getString("cpublisher"));
            doc.setTech(rs.getString("ctech"));
            doc.setInCollection(rs.getBoolean("incollection"));
            doc.setNcites(rs.getInt("size"));
            doc.setSelfCites(rs.getInt("selfCites"));
            doc.setSnippet(rs.getString("firstContext"));
            doc.setUpdateTime(new Date(rs.getTimestamp("updated").getTime()));
            return doc;
        }
        
        public List<ThinDoc> run(Long clusterid, int amount) {
            Object[] params = new Object[] { clusterid, new Integer(amount) };
            return execute(params);
        }
        
    }  //- class GetCiting
    
    
    private static final String DEF_GET_CITED_QUERY =
        "select clusters.id, cauth, ctitle, cvenue, cyear, " +
        "cpages, cpublisher, cvol, cnum, ctech, incollection, " +
        "size, firstContext, selfCites, updated from clusters, citegraph where " +
        "cited=clusters.id and citing=? limit ?";

    private class GetCited extends MappingSqlQuery {
        
        public GetCited(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITED_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            ThinDoc doc = new ThinDoc();
            doc.setCluster(rs.getLong(1));
            doc.setAuthors(rs.getString("cauth"));
            doc.setTitle(rs.getString("ctitle"));
            doc.setVenue(rs.getString("cvenue"));
            try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
            try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
            try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
            doc.setPages(rs.getString("cpages"));
            doc.setPublisher(rs.getString("cpublisher"));
            doc.setTech(rs.getString("ctech"));
            doc.setInCollection(rs.getBoolean("incollection"));
            doc.setNcites(rs.getInt("size"));
            doc.setSelfCites(rs.getInt("selfCites"));
            doc.setSnippet(rs.getString("firstContext"));
            doc.setUpdateTime(new Date(rs.getTimestamp("updated").getTime()));
            return doc;
        }
        
        public List<ThinDoc> run(Long clusterid, int amount) {
            Object[] params = new Object[] { clusterid, new Integer(amount) };
            return execute(params);
        }
        
    }  //- class GetCited
    
    
    private static final String DEF_GET_CITING_CLUST_QUERY =
        "select citing from citegraph where cited=?";
    
    private class GetCitingClusters extends MappingSqlQuery {
        
        public GetCitingClusters(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITING_CLUST_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public List<Long> run(Long clusterid) {
            return execute(clusterid);
        }
        
    }  //- class GetCitingClusters
    
    
    private static final String DEF_GET_CITED_CLUST_QUERY =
        "select cited from citegraph where citing=?";
    
    private class GetCitedClusters extends MappingSqlQuery {
        
        public GetCitedClusters(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITED_CLUST_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public List<Long> run(Long clusterid) {
            return execute(clusterid);
        }
        
    }  //- class GetCitedClusters
    
    private static final String DEF_GET_TOP_CITING_CLUST_QUERY =
        "select citing from citegraph, clusters where cited=? " +
        "and citing=clusters.id order by size desc limit ?";
    
    private class GetTopCitingClusters extends MappingSqlQuery {
        
        public GetTopCitingClusters(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_TOP_CITING_CLUST_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public List<Long> run(Long clusterid, int amount) {
            Object[] params = new Object[] { clusterid, new Integer(amount) };
            return execute(params);
        }
        
    }  //- class GetTopCitingClusters
    
    
    private static final String DEF_GET_TOP_CITED_CLUST_QUERY =
        "select cited from citegraph, clusters where citing=? " +
        "and cited=clusters.id order by size desc limit ?";
    
    private class GetTopCitedClusters extends MappingSqlQuery {
        
        public GetTopCitedClusters(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_TOP_CITED_CLUST_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public List<Long> run(Long clusterid, int amount) {
            Object[] params = new Object[] { clusterid, new Integer(amount) };
            return execute(params);
        }
        
    }  //- class GetTopCitedClusters
    
    
    /* incollection, cauth, ctitle */
    private static final String DEF_INSERT_CLUSTER_STMT =
        "insert into clusters values (NULL, 0, ?, ?, ?, NULL, NULL, " +
        "NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, CURRENT_TIMESTAMP)";

    private class InsertCluster extends SqlUpdate {
        
        public InsertCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_CLUSTER_STMT);
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.VARCHAR));
            setReturnGeneratedKeys(true);
            compile();
        }
        
        public Long run(boolean inCollection, String auth, String title) {
            Object[] params = new Object[] {
                    new Boolean(inCollection), auth, title
            };
            KeyHolder holder = new GeneratedKeyHolder();
            update(params, holder);
            return new Long(holder.getKey().longValue());
        }
        
    }  //- class InsertCluster
        
    
    /* doi, cluster */
    private static final String DEF_INSERT_DOC_STMT =
        "insert into papers values (?, ?)";
    
    private class InsertDoc extends SqlUpdate {
        
        public InsertDoc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_DOC_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(String doi, Long clusterid) {
            Object[] params = new Object[] { doi, clusterid };
            return update(params);
        }
        
    }  //- class InsertDoc
        
    
    private static final String DEF_SET_INCOLLECTION_STMT =
        "update clusters set incollection=? where id=?";
    
    private class SetInCollection extends SqlUpdate {
        
        public SetInCollection(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_INCOLLECTION_STMT);
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Long cid, boolean inCollection) {
            return update( new Object[] { new Boolean(inCollection), cid } );
        }
        
    }  //- class SetInCollection
        
    
    /* citeid, clusterid, paperid */
    private static final String DEF_INSERT_CITATION_STMT =
        "insert into citations values (?, ?, ?)";
    
    private class InsertCitation extends SqlUpdate {
        
        public InsertCitation(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_CITATION_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Citation citation, Long cid) {
            Object[] params = new Object[] {
                    Long.parseLong(citation.getDatum(Citation.DOI_KEY)),
                    cid, citation.getDatum(Citation.PAPERID_KEY)
            };
            return update(params);
        }
        
    }  //- class InsertCitation
        
        
    private static final String DEF_GET_CLUSTERID_QUERY = 
        "select cid from keymap where ckey=?";
    
    private class GetClusterID extends MappingSqlQuery {
        
        public GetClusterID(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CLUSTERID_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public Long run(String key) {
            List<Long> list = execute(key);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
        
    }  //- class GetClusterID
    
    
    /* key, cluster */
    private static final String DEF_INSERT_KEYMAP_STMT =
        "insert into keymap values (NULL, ?, ?)";
    
    private class InsertKeyMapping extends SqlUpdate {
        
        public InsertKeyMapping(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_KEYMAP_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(String key, Long cid) {
            Object[] params = new Object[] { key, cid };
            return update(params);
        }
        
    }  //- class InsertKeyMapping
    
    
    private static final String DEF_GET_CLUSTERS_IN_COLL_QUERY =
        "select id, size, cauth, ctitle, cvenue, cventype, cyear, " +
        "cpages, cpublisher, cvol, cnum, ctech, selfCites, updated " +
        "from clusters where id>? and incollection=1 limit ?";
    
    private class GetClustersInCollection extends MappingSqlQuery {
        
        public GetClustersInCollection(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CLUSTERS_IN_COLL_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long cid     = rs.getLong("id");
            int size     = rs.getInt("size");
            String auths = rs.getString("cauth");
            String title = rs.getString("ctitle");
            String venue = rs.getString("cvenue");
            String vt    = rs.getString("cventype");
            Integer year = rs.getInt("cyear");
            String pages = rs.getString("cpages");
            String publ  = rs.getString("cpublisher");
            Integer vol  = rs.getInt("cvol");
            Integer num  = rs.getInt("cnum");
            String tech  = rs.getString("ctech");
            int self     = rs.getInt("selfCites");
            long updated = rs.getTimestamp("updated").getTime();
            
            ThinDoc doc = new ThinDoc();
            
            doc.setCluster(cid);
            doc.setNcites(size);
            doc.setAuthors(auths);
            doc.setTitle(title);
            doc.setVenue(venue);
            doc.setVentype(vt);
            if (year != null) {
                doc.setYear(year.intValue());
            }
            doc.setPages(pages);
            doc.setPublisher(publ);
            if (vol != null) {
                doc.setVol(vol.intValue());
            }
            if (num != null) {
                doc.setNum(num.intValue());
            }
            doc.setTech(tech);
            doc.setSelfCites(self);
            doc.setUpdateTime(new Date(updated));
            
            return doc;
        }
        
        public List<ThinDoc> run(Long start, int amount) {
            Object[] params = new Object[] { start, new Integer(amount) };
            return execute(params);
        }
        
    }  //- class GetClustersInCollection
        
    
    private static final String DEF_GET_PAPERIDS_BY_CLUSTER_QUERY =
        "select id from papers where cluster=?";
    
    private class GetPaperIDs extends MappingSqlQuery {
        
        public GetPaperIDs(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_PAPERIDS_BY_CLUSTER_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
        
        public List<String> run(Long clusterid) {
            return execute(clusterid);
        }
        
    }  //- class GetPaperIDs
        
    
    private static final String DEF_GET_CITEIDS_BY_CLUSTER_QUERY =
        "select id from citations where cluster=?";
    
    private class GetCiteIDs extends MappingSqlQuery {
        
        public GetCiteIDs(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITEIDS_BY_CLUSTER_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public List<Long> run(Long clusterid) {
            return execute(clusterid);
        }
        
    }  //- class GetCiteIDs
    
        
    private static final String DEF_GET_CLUSTER_QUERY =
        "select size, incollection, cauth, ctitle, cvenue, cventype, cyear, " +
        "cpages, cpublisher, cvol, cnum, ctech, observations, selfCites, " +
        "updated from clusters where id=?";
    
    private class GetCluster extends MappingSqlQuery {
        
        public GetCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CLUSTER_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            ThinDoc doc = new ThinDoc();
            doc.setTitle(rs.getString("ctitle"));
            doc.setAuthors(rs.getString("cauth"));
            doc.setVenue(rs.getString("cvenue"));
            doc.setVentype(rs.getString("cventype"));
            try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
            doc.setPages(rs.getString("cpages"));
            doc.setPublisher(rs.getString("cpublisher"));
            try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
            try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
            doc.setTech(rs.getString("ctech"));
            doc.setObservations(rs.getString("observations"));
            doc.setNcites(rs.getInt("size"));
            doc.setSelfCites(rs.getInt("selfCites"));
            doc.setInCollection(rs.getBoolean("incollection"));
            doc.setUpdateTime(new Date(rs.getTimestamp("updated").getTime()));
            return doc;
        }
        
        public ThinDoc run(Long clusterid) {
            List<ThinDoc> list = execute(clusterid);
            if (list.isEmpty()) {
                return null;
            } else {
                ThinDoc doc = (ThinDoc)list.get(0);
                doc.setCluster(clusterid);
                return doc;
            }
        }
        
    }  //- class GetCluster
        
    
    private static final String DEF_UPDATE_CLUSTER_STMT =
        "update clusters set cauth=?, ctitle=?, " +
        "cvenue=?, cventype=?, cyear=?, cpages=?, cpublisher=?, cvol=?, " +
        "cnum=?, ctech=?, observations=?, updated=current_timestamp where id=?";
    
    private class UpdateCluster extends SqlUpdate {
        
        public UpdateCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_CLUSTER_STMT);
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(ThinDoc cluster, boolean changed) {
            Integer year = null;
            if (cluster.getYear() > 0) {
                year = cluster.getYear();
            }
            Integer vol = null;
            if (cluster.getVol() > 0) {
                vol = cluster.getVol();
            }
            Integer num = null;
            if (cluster.getNum() > 0) {
                num = cluster.getNum();
            }
            Object[] params = new Object[] {
                    cluster.getAuthors(), cluster.getTitle(),
                    cluster.getVenue(), cluster.getVentype(), year,
                    cluster.getPages(), cluster.getPublisher(), num, vol,
                    cluster.getTech(), cluster.getObservations(),
                    cluster.getCluster()
            };
            return update(params);
        }
        
    }  //- class UpdateCluster
    
    
    private static final String DEF_UPDATE_OBS_STMT =
        "update clusters set observations=? where id=?";
    
    private class UpdateObs extends SqlUpdate {
        
        public UpdateObs(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_OBS_STMT);
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(String obs, Long clusterid) {
            Object[] params = new Object[] { obs, clusterid };
            return update(params);
        }
        
    }  //- class UpdateObs
            
    
    private static final String DEF_GET_CLUSTERS_SINCE_TIME_QUERY =
        "select id, size, incollection, cauth, ctitle, cvenue, cventype, " +
        "cyear, cpages, cpublisher, cvol, cnum, ctech, selfCites, updated " +
        "from clusters where updated>=? and id>? order by id asc limit ?";
    
    private class GetClustersSinceTime extends MappingSqlQuery {
        
        public GetClustersSinceTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CLUSTERS_SINCE_TIME_QUERY);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            ThinDoc doc = new ThinDoc();
            doc.setCluster(rs.getLong("id"));
            doc.setNcites(rs.getInt("size"));
            doc.setInCollection(rs.getBoolean("incollection"));
            doc.setAuthors(rs.getString("cauth"));
            doc.setTitle(rs.getString("ctitle"));
            doc.setVenue(rs.getString("cvenue"));
            doc.setVentype(rs.getString("cventype"));
            try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
            doc.setPages(rs.getString("cpages"));
            doc.setPublisher(rs.getString("cpublisher"));
            try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
            try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
            doc.setTech(rs.getString("ctech"));
            doc.setSelfCites(rs.getInt("selfCites"));
            doc.setUpdateTime(new Date(rs.getTimestamp("updated").getTime()));
            return doc;
        }
        
        public List<ThinDoc> run(Date time, Long lastID, int amount) {
            Object[] params = {
                    new Timestamp(time.getTime()), lastID,
                    new Integer(amount)
            };
            return execute(params);
        }
        
    }  //- class GetClustersSinceTime
    
    
    private static final String DEF_GET_CONTEXT_QUERY =
        "select firstContext from citegraph where citing=? and cited=?";
    
    private class GetCiteContext extends MappingSqlQuery {
        
        public GetCiteContext(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CONTEXT_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
        
        public String run(Long citing, Long cited) {
            Object[] params = new Object[] { citing, cited };
            List<String> list = execute(params);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
        
    }  //- class GetCiteContext
    
    
    private static final String DEF_GET_MIN_CID_QUERY =
        "select min(id) from clusters";
    
    private class GetMinClusterID extends MappingSqlQuery {
        
        public GetMinClusterID(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_MIN_CID_QUERY);
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public Long run() {
            List<Long> list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
        
    }  //- class GetMinClusterID
    
        
    private static final String DEF_GET_MAX_CID_QUERY =
        "select max(id) from clusters";
    
    private class GetMaxClusterID extends MappingSqlQuery {
        
        public GetMaxClusterID(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_MAX_CID_QUERY);
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public Long run() {
            List<Long> list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
        
    }  //- class GetMaxClusterID
    
    
    private static final String DEF_GET_INFUPDATE_QUERY =
        "select lastupdate from infupdates where id=?";
    
    private class GetInfTime extends MappingSqlQuery {
        
        public GetInfTime(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_GET_INFUPDATE_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public Date mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp ts = rs.getTimestamp(1);
            if (ts != null) {
                return new Date(rs.getTimestamp(1).getTime());
            } else {
                return null;
            }
        }
        
        public Date run(Long cid) {
            List<Date> list = execute(cid);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
        
    }  //- class GetInfTime
    
    
    private static final String DEF_INS_INFUPDATE_STMT =
        "insert into infupdates values (?, ?)";
    
    private class InsertInfTime extends SqlUpdate {
        
        public InsertInfTime(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INS_INFUPDATE_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        }
        
        public int run(Long cid, Date time) {
            return update(new Object[] { cid, new Timestamp(time.getTime()) });
        }
        
    }  //- class InsertInfTime
    
    
    private static final String DEF_UPD_INFUPDATE_STMT =
        "update infupdates set lastupdate=? where id=?";
    
    private class UpdateInfTime extends SqlUpdate {
        
        public UpdateInfTime(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_UPD_INFUPDATE_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(Long cid, Date time) {
            return update(new Object[] { new Timestamp(time.getTime()), cid });
        }
        
    }  //- class UpdateInfTime
    
    
    private static final String DEF_GET_UPDATE_TIME_QUERY =
        "select updated from clusters where id=?";
    
    private class GetUpdateTime extends MappingSqlQuery {
        
        public GetUpdateTime(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_GET_UPDATE_TIME_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public Date mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Date(rs.getTimestamp(1).getTime());
        }
        
        public Date run(Long cid) {
            List<Date> list = execute(cid);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
        
    }  //- class GetUpdateTime
    
    
    private static final String DEF_DEL_CITING_STMT =
        "delete from citegraph where citing=?";
    
    protected class DeleteCiting extends SqlUpdate {
        
        public DeleteCiting(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CITING_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
    }  //- class DeleteCiting


    private static final String DEF_DEL_CITED_STMT =
        "delete from citegraph where cited=?";
    
    protected class DeleteCited extends SqlUpdate {
        
        public DeleteCited(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CITED_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
    }  //- class DeleteCited

    
    private static final String DEF_DEL_CITE_STMT =
        "delete from citations where id=?";
    
    protected class DeleteCitation extends SqlUpdate {
        
        public DeleteCitation(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CITE_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
    }  //- class DeleteCite


    private static final String DEF_DEL_PAPER_STMT =
        "delete from papers where id=?";
    
    protected class DeletePaper extends SqlUpdate {
        
        public DeletePaper(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_PAPER_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
    }  //- class DeletePaper


    private static final String DEF_DEL_INFUPDATE_STMT =
        "delete from infupdates where id=?";
    
    protected class DeleteInfUpdate extends SqlUpdate {
        
        public DeleteInfUpdate(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_INFUPDATE_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
    }  //- class DeleteInfUpdate
    
    
    private static final String DEF_DEL_CLUSTER_STMT =
        "delete from clusters where id=?";
    
    protected class DeleteCluster extends SqlUpdate {
        
        public DeleteCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CLUSTER_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
    }  //- class DeleteCluster
    
    
    private static final String DEF_DEL_DELETION_STMT =
        "delete from deletions where id=?";
    
    protected class DeleteDeletion extends SqlUpdate {
        
        public DeleteDeletion(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_DELETION_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
    }  //- class DeleteDeletion
    
    
    private static final String DEF_DEL_DELETIONS_STMT =
        "delete from deletions where deleted<=?";
    
    protected class DeleteDeletions extends SqlUpdate {
        
        public DeleteDeletions(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_DELETIONS_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        }
        
        public int run(Date date) {
            return update(new Object[] { new Timestamp(date.getTime()) });
        }
        
    }  //- class DeleteDeletions
    
    
    private static final String DEF_GET_DELETIONS_STMT =
        "select id from deletions where deleted<=?";
    
    protected class GetDeletions extends MappingSqlQuery {
        
        public GetDeletions(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_DELETIONS_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public List<Long> run(Date date) {
            return execute(new Object[] { new Timestamp(date.getTime()) });
        }
        
    }  //- class GetDeletions
    
    
    private static final String DEF_DEL_CITATIONS_STMT =
        "delete from citations where paperid=?";
    
    protected class DeleteCitations extends SqlUpdate {

        public DeleteCitations(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CITATIONS_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
    }  //- class DeleteCitations
    
    
    private static final String DEF_DEL_KEYMAP_STMT =
        "delete from keymap where cid=?";
    
    protected class DeleteKeyMappings extends SqlUpdate {
        
        public DeleteKeyMappings(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_KEYMAP_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
    }  //- class DeleteKeyMappings
    
    
    private static final String DEF_INSERT_INDEX_TIME_STMT =
        "insert into indexTime values (\"indexTime\", ?)";
    
    protected class InsertIndexTime extends SqlUpdate {
        
        public InsertIndexTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_INDEX_TIME_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        }
        
        public int run(Date date) {
            return update(new Object[] { new Timestamp(date.getTime()) });
        }
        
    }  //- class InsertIndexTime
    
    
    private static final String DEF_UPDATE_INDEX_TIME_STMT =
        "update indexTime set lastupdate=? where param=\"indexTime\"";
    
    protected class UpdateIndexTime extends SqlUpdate {
        
        public UpdateIndexTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_INDEX_TIME_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        }
        
        public int run(Date date) {
            return update(new Object[] { new Timestamp(date.getTime()) });
        }
        
    }  //- class UpdateIndexTime
    
    
    private static final String DEF_GET_INDEX_TIME_QUERY =
        "select lastupdate from indexTime where param=\"indexTime\"";
    
    protected class GetIndexTime extends MappingSqlQuery {
        
        public GetIndexTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_INDEX_TIME_QUERY);
            compile();
        }
        
        public Date mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Date(rs.getTimestamp(1).getTime());
        }
        
        public Date run() {
            List<Date> list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
        
    }  //- class GetIndexTime
    
    private static final String DEF_INSERT_AUTHOR_DEDUP_TIME_STMT =
        "insert into indexTime values (\"authorDedupTime\", ?)";
    
    protected class InsertAuthorDedupTime extends SqlUpdate {
        
        public InsertAuthorDedupTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_AUTHOR_DEDUP_TIME_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        } //- InsertAuthorDedupTime.InsertAuthorDedupTime
        
        public int run(Date date) {
            return update(new Object[] { new Timestamp(date.getTime()) });
        } //- InsertAuthorDedupTime.run
        
    }  //- class InsertAuthorDedupTime
    
    private static final String DEF_UPDATE_AUTHOR_DEDUP_TIME_STMT =
        "update indexTime set lastupdate=? where param=\"authorDedupTime\"";
    
    protected class UpdateAuthorDedupTime extends SqlUpdate {
        
        public UpdateAuthorDedupTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_AUTHOR_DEDUP_TIME_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        } //- UpdateAuthorDedupTime.UpdateAuthorDedupTime
        
        public int run(Date date) {
            return update(new Object[] { new Timestamp(date.getTime()) });
        } //- UpdateAuthorDedupTime.run
        
    }  //- class UpdateAuthorDedupTime
    
    private static final String DEF_GET_AUTHOR_DEDUP_TIME_QUERY =
        "select lastupdate from indexTime where param=\"authorDedupTime\"";
    
    protected class GetAuthorDedupTime extends MappingSqlQuery {
        
        public GetAuthorDedupTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_AUTHOR_DEDUP_TIME_QUERY);
            compile();
        } //- AuthorDedup.AuthorDedup
        
        public Date mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Date(rs.getTimestamp(1).getTime());
        } //- GetAuthorDedupTime.mapRwo
        
        public Date run() {
            List<Date> list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        } //- GetAuthorDedupTime.run
        
    }  //- class GetAuthorDedupTime
    
}  //- class CiteClusterDAOImpl


class GraphMapping {
    
    private Long id;
    private Long citing;
    private Long cited;
    private String firstContext;
    
    public Long getCited() {
        return cited;
    }
    public void setCited(Long cited) {
        this.cited = cited;
    }
    public Long getCiting() {
        return citing;
    }
    public void setCiting(Long citing) {
        this.citing = citing;
    }
    public String getFirstContext() {
        return firstContext;
    }
    public void setFirstContext(String firstContext) {
        this.firstContext = firstContext;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
}  //- class GraphMapping
