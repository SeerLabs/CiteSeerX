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
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.citeseerx.dao2.InferenceBuilder;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Citation;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DomainTransformer;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.utility.CSXConstants;

/**
 * Updates canonical metadata of cluster records based on metadata from
 * citations.  This will generally only update records which have been
 * modified since the last update time.  If a change occurs, the 
 * UpdateManager is called to handle any changes that should occur to
 * version and document metadata.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class InferenceUpdater {

    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
    
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    private UpdateManager updateManager;
    
    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    } //- setUpdateManager
    
    
    /**
     * Updates all clusters that have changed since the last update time.
     * @throws JSONException
     * @throws IOException
     */
    public void updateAll() throws JSONException, IOException {
        Long min = citedao.getMinClusterID();
        Long max = citedao.getMaxClusterID();
        if (min==null || max==null) {
            System.err.println("Null value for min or max at updateAll");
        }
        long counter = 0;
        
        for (Long i=min; i<=max; i++) {
            counter++;
            Date updateTime = citedao.checkInfUpdateRequired(i);
            if (updateTime != null) {
                ThinDoc doc = updateCluster(i);
                citedao.insertInfUpdateTime(i, updateTime);
                if (doc != null
                        && doc.getInCollection()
                        && doc.getNcites() > 0) {
                    updateInference(doc);
                }
            }
            if ((counter%1000)==0) {
                System.out.println("Processed "+counter);
            }
        }
        
    }  //- updateAll
    
    
    protected ThinDoc updateCluster(Long clusterid) throws JSONException {
        
        ThinDoc cluster = citedao.getThinDoc(clusterid);
        if (cluster == null || cluster.getNcites() <= 0) return cluster;

        JSONObject json = new JSONObject();
        
        List<Citation> citations = csxdao.getCitationsForCluster(clusterid);
        if (citations.size() == 0) {
            return cluster;
        }
        for (Citation citation : citations) {
            ThinDoc thinDoc = DomainTransformer.toThinDoc(citation);
            InferenceBuilder.addObservation(thinDoc, json);
        }

        cluster.setObservations(json.toString());
        
        ThinDoc bundle = InferenceBuilder.toThinDoc(json);
        bundle.setCluster(clusterid);
        bundle.setNcites(cluster.getNcites());
        bundle.setSelfCites(cluster.getSelfCites());
        bundle.setInCollection(cluster.getInCollection());
        bundle.setObservations(cluster.getObservations());
        citedao.updateCluster(bundle, true);
        
        return bundle;
        
    }  //- updateCluster
    
    
    public void updateInference(ThinDoc doc) throws IOException {

        List<String> dois =
            citedao.getPaperIDs(new Long(doc.getCluster()));
        for (String doi : dois) {
            updateDocument(doc, doi);
        }
        
    }  //- updateInference
    
    
    /**
     * Builds a Document object based on metadata in the supplied ThinDoc
     * and sends the Document to the UpdateManager to make any necessary
     * changes to the database and file system representation of the
     * corresponding document record.
     * @param doc
     * @param doi
     * @throws IOException
     */
    public void updateDocument(ThinDoc doc, String doi) throws IOException {
        
        String src = CSXConstants.INFERENCE_VERSION;

        if (doc.getNcites() == 0) {
            return;
        }
        
        String title = doc.getTitle();
        String venue = doc.getVenue();
        String vt    = doc.getVentype();
        String pages = doc.getPages();
        String tech  = doc.getTech();
        String publ  = doc.getPublisher();
        int year     = doc.getYear();
        int vol      = doc.getVol();
        int num      = doc.getNum();
        
        if (title == null && venue == null && vt == null && year < 0) {
            return;
        }
        
        Document update = new Document();
        
        update.setVersionName(CSXConstants.INFERENCE_VERSION);
        update.setNcites(doc.getNcites());
        update.setClusterID(doc.getCluster());
        
        update.setDatum(Document.DOI_KEY, doi);
        if (title != null) {
            update.setDatum(Document.TITLE_KEY, title);
            update.setSource(Document.TITLE_KEY, src);
        }
        if (venue != null) {
            update.setDatum(Document.VENUE_KEY, venue);
            update.setSource(Document.VENUE_KEY, src);
        }
        if (vt != null) {
            update.setDatum(Document.VEN_TYPE_KEY, vt);
            update.setSource(Document.VEN_TYPE_KEY, src);
        }
        if (pages != null) {
            update.setDatum(Document.PAGES_KEY, pages);
            update.setSource(Document.PAGES_KEY, src);
        }
        if (tech != null) {
            update.setDatum(Document.TECH_KEY, tech);
            update.setSource(Document.TECH_KEY, src);
        }
        if (publ != null) {
            update.setDatum(Document.PUBLISHER_KEY, publ);
            update.setSource(Document.PUBLISHER_KEY, src);
        }
        if (year > 0) {
            update.setDatum(Document.YEAR_KEY,
                    Integer.toString(year));
            update.setSource(Document.YEAR_KEY, src);
        }
        if (vol > 0) {
            update.setDatum(Document.VOL_KEY,
                    Integer.toString(vol));
            update.setSource(Document.VOL_KEY, src);
        }
        if (num > 0) {
            update.setDatum(Document.NUM_KEY,
                    Integer.toString(num));
            update.setSource(Document.NUM_KEY, src);
        }
        updateManager.updateDocument(update, true);            

    }  //- updateDocument
        
}  //- class InferenceUpdater
