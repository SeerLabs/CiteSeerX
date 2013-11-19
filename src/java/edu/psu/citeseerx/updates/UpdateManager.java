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
package edu.psu.citeseerx.updates;

import java.io.IOException;
import java.util.*;
import org.json.JSONException;

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.citematch.*;
import edu.psu.citeseerx.utility.CSXConstants;

/**
 * This class handles metadata changes when they occur, by recording the
 * changes in the database and filesystem storage backends and passing
 * changes to the VersionManager to handle Versioning.  Metadata changes
 * will be immediately reflected in the citation graph and cluster mappings.
 * <br><br>
 * Class that need to be notified of changes should be registered here.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class UpdateManager {

    private VersionManager versionManager;
    
    public void setVersionManager(VersionManager manager) {
        this.versionManager = manager;
    } //- setVersionManager
    
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    private CitationClusterer clusterer;
    
    public void setCitationClusterer(CitationClusterer clusterer) {
        this.clusterer = clusterer;
    } //- setCitationClusterer
    
    
    private static final String[] updatableDocFields = {
        Document.TITLE_KEY, Document.ABSTRACT_KEY, Document.YEAR_KEY,
        Document.VENUE_KEY, Document.VEN_TYPE_KEY, Document.PAGES_KEY,
        Document.VOL_KEY, Document.NUM_KEY, Document.PUBLISHER_KEY,
        Document.PUBADDR_KEY, Document.TECH_KEY
    };
    
    
    private static final String[] updatableInfFields = {
        Document.TITLE_KEY, Document.YEAR_KEY, Document.VENUE_KEY,
        Document.VEN_TYPE_KEY, Document.PAGES_KEY, Document.VOL_KEY,
        Document.PUBLISHER_KEY, Document.TECH_KEY
    };
    
    
    private static final String[] updatableAuthFields = {
        Author.NAME_KEY, Author.ADDR_KEY, Author.AFFIL_KEY, Author.EMAIL_KEY,
        Author.ORD_KEY
    };
    
    
    /**
     * Handles user-supplied document corrections.
     * @param doc
     * @param userid
     * @throws IOException
     * @throws JSONException
     */
    public void doCorrection(Document doc, String userid)
    throws IOException, JSONException {

        doc.setVersionName(CSXConstants.USER_VERSION);
        Document oldDoc =
            csxdao.getDocumentFromDB(doc.getDatum(Document.DOI_KEY));
        
        Document newDoc = updateDocument(doc, false);
        
        String doi = newDoc.getDatum(Document.DOI_KEY);
        int version = newDoc.getVersion();
        
        csxdao.insertCorrection(userid, doi, version);
        clusterer.reclusterDocument(newDoc, oldDoc);
        
    }  //- doCorrection
    
    
    /**
     * Handles generic document data updates.
     * @param doc
     * @param fromCluster whether this update is coming from a cluster inference
     * change.
     * @return the updated document
     * @throws IOException
     */
    public Document updateDocument(Document doc, boolean fromCluster)
    throws IOException {

        String doi = doc.getDatum(Document.DOI_KEY);
        Document priorDoc = csxdao.getDocumentFromDB(doi, false, true);
        String oldVersionName = priorDoc.getVersionName();
        
        priorDoc.setVersionName(doc.getVersionName());
        if (fromCluster) {
            /*
            priorDoc.setClusterID(doc.getClusterID());
            int priorCites = priorDoc.getNcites();
            if (priorCites != doc.getNcites()) {
                priorDoc.setNcites(doc.getNcites());
                csxdao.setDocNcites(priorDoc, priorCites);
            }
            */
        }
        
        boolean changed = false;
        boolean authChanged = false;
        
        //if (doc.getClusterID() != priorDoc.getClusterID()) {
            //csxdao.setDocCluster(priorDoc, doc.getClusterID());
            //priorDoc.setClusterID(doc.getClusterID());
        //}
        //if (doc.getNcites() != priorDoc.getNcites()) {
            //csxdao.setNcites(priorDoc, doc.getNcites());
            //priorDoc.setNcites(doc.getNcites());
        //}
        
        String[] updateFields = updatableDocFields;
        if (doc.getVersionName() != null &&
                doc.getVersionName().equals(CSXConstants.INFERENCE_VERSION)) {
            updateFields = updatableInfFields;
        }
        
        for (String field : updateFields) {
            String datum = doc.getDatum(field);
            String priorDatum = priorDoc.getDatum(field);

            String src = doc.getSource(field);

            if (datum == null && priorDatum == null) {
                continue;
            }
            if (datum == null && priorDatum != null) {
                changed = true;
                priorDoc.setDatum(field, null);
                priorDoc.setSource(field, src);
                continue;
            }
            if (priorDatum != null && datum.equals(priorDatum)) {
                continue;
            }
            changed = true;
            if (datum != null) {
                priorDoc.setDatum(field, datum);
                priorDoc.setSource(field, src);
            }
        }

        boolean overrideOldAuthors = false;
        if (doc.getVersionName().equals(CSXConstants.USER_VERSION)) {
            overrideOldAuthors = true;
        }
        
        ArrayList<Author> authors = new ArrayList<Author>(doc.getAuthors());
        if (!authors.isEmpty() &&
                !doc.sameAuthors(priorDoc) && overrideOldAuthors) {
            authChanged = true;
            priorDoc.setAuthors(authors);
        } else {
            ArrayList<Author> priorAuthors =
                new ArrayList<Author>(priorDoc.getAuthors());
            for (Author author : priorAuthors) {
                String name = author.getDatum(Author.NAME_KEY);
                for (Author newauth : authors) {
                    String newname = newauth.getDatum(Author.NAME_KEY);
                    if (name != null && newname != null
                            && name.equals(newname)) {
                        for (String field : updatableAuthFields) {
                            String datum = author.getDatum(field);
                            String newdatum = newauth.getDatum(field);
                            if (datum == null && newdatum != null) {
                                author.setDatum(field, newdatum);
                                author.setSource(field,
                                        newauth.getSource(field));
                                authChanged = true;
                            }
                            if (datum != null && newdatum != null
                                    && !datum.equals(newdatum)) {
                                author.setDatum(field, newdatum);
                                author.setSource(field,
                                        newauth.getSource(field));
                                authChanged = true;
                            }
                        }
                    }
                }
            }
        }
        
        ArrayList<Keyword> keywords = new ArrayList<Keyword>(doc.getKeywords());
        if (!keywords.isEmpty()) {
            changed = true;
            priorDoc.setKeywords(keywords);
        }
        
        if (!changed && !authChanged) {
            return doc;
        }
        
        if (versionManager.handleUpdate(priorDoc, oldVersionName)) {
            csxdao.updateDocumentData(priorDoc, authChanged,
                    false,  // don't update citations
                    false,  // don't update acks,
                    false); // don't update keywords)
            notifyListeners(priorDoc);
            return priorDoc;
        } else {
            return doc;
        }
        
    }  //- updateDocument
    
    
    private List<UpdateListener> listeners =
        new ArrayList<UpdateListener>();
    
    /**
     * API for registering a class that will be notified upon metadata
     * changes.
     * @param listener
     */
    public void addListener(UpdateListener listener) {
        listeners.add(listener);
    } //- addListener
    
    /**
     * Bean alias for addListener.
     * @param listener
     */
    public void setListener(UpdateListener listener) {
        listeners.add(listener);
    } //- setListener
    
    public void setListeners(List<UpdateListener> listeners) {
        this.listeners = listeners;
    } //- setListeners
    
    private void notifyListeners(Document doc) {
        for (UpdateListener listener : listeners) {
            try {
                listener.handleUpdate(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } //- notifyListeners
    
    
    public void setDocumentDMCA(Document doc)
    throws JSONException
    {
    	doc.setState(DocumentProperties.IS_DMCA);
    	csxdao.setDocState(doc,DocumentProperties.IS_DMCA);
    }
    
    public void deleteDocument(Document doc) 
    throws JSONException {
    	doc.setState(DocumentProperties.LOGICAL_DELETE);
    	csxdao.setDocState(doc,DocumentProperties.LOGICAL_DELETE);
    }
    
    /**
     * Set's a document as public or not, reflecting the change in the citegraph
     * database. If public is false, the document is deleted from the citegraph
     * database and its clusterid set to 0. If the document changes to be 
     * public it's added to the citegraph database and clustered.
     * @param doc
     * @param isPublic
     */
    
    public void setDocumentPublic(Document doc) 
    throws JSONException {
        doc.setState(DocumentProperties.IS_PUBLIC);
        csxdao.setDocState(doc,DocumentProperties.IS_PUBLIC );
    } //- setPublicDocument
    
    
}  //- class UpdateManager
