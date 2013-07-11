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
package edu.psu.citeseerx.ingestion;

import java.util.*;
import java.sql.SQLException;

import edu.psu.citeseerx.dao2.logic.*;
import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.utility.CSXConstants;
import edu.psu.citeseerx.ingestion.ws.DOIClient;
import edu.psu.citeseerx.citematch.CitationClusterer;
import edu.psu.citeseerx.updates.InferenceUpdater;

/**
 * This is the main entry point for importing documents into the CiteSeerX
 * corpus.  All imports should go through this class!  The DocumentEntryPoint
 * ties together various functions for maintaining backend persistence,
 * versioning, citation matching, and citation inference.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
class DocumentEntryPoint {
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
    
    
    private CitationClusterer clusterer;
    
    public void setCitationClusterer(CitationClusterer clusterer) {
        this.clusterer = clusterer;
    } //- setCitationClusterer
    
    
    private FileIngester fileIngester;
    
    public void setFileIngester(FileIngester ingester) {
        this.fileIngester = ingester;
    } //- setFileIngester
    
    
    private DOIClient doiClient;
    
    public void setDOIClient(DOIClient doiClient) {
        this.doiClient = doiClient;
    } //- setDOIClient
    
    
    private InferenceUpdater inferenceUpdater;
    
    public void setInferenceUpdater(InferenceUpdater inferenceUpdater) {
        this.inferenceUpdater = inferenceUpdater;
    } //- setInferenceUpdater
    
    //String doibase = "10.1.1.3.";
    //int counter = 1;
    
    /**
     * Imports a document by obtaining a new DOI, importing file resources
     * to the filesystem repository, mapping all data to persistent storage,
     * clustering the document and all it's citations, and updating version
     * info if any citations are found to the document from which to draw
     * inference.  Before any of this is done, a check for duplicate documents
     * is conducted based on SHA1 checksum.
     * @return a list of CheckSum objects that is empty unless duplicate
     * documents were found.  If there were duplicates, this list can be used
     * to report on the DOIs of all duplicates.
     */
    public List<CheckSum> importDocument(Document doc, String fileBase)
    throws Exception {

        List<CheckSum> duplicateEntries = findDuplicates(doc);
        if (duplicateEntries.isEmpty()) {

            String doi = doiClient.getDOI(CSXConstants.ARTICLE_SUB_ID);
            //String doi = doibase+counter++;
            doc.setDatum(Document.DOI_KEY, doi);
            
            fileIngester.importFileData(doc, fileBase);
            
            csxdao.insertDocumentEntry(doc);
            csxdao.importDocument(doc);
            
            clusterer.clusterDocument(doc);
            
            ThinDoc cluster = citedao.getThinDoc(doc.getClusterID());
            inferenceUpdater.updateDocument(cluster, doi);
            
        }
        for (CheckSum dup : duplicateEntries) {
            updateHubMapping(doc, dup.getDOI());
        }
            
        return duplicateEntries;
            
    }  //- importDocument
    
    
    protected List<CheckSum> findDuplicates(Document doc)
    throws SQLException {

        ArrayList<CheckSum> duplicateEntries = new ArrayList<CheckSum>();
        List<CheckSum> docSums = doc.getFileInfo().getCheckSums();

        for (CheckSum sum : docSums) {
            List<CheckSum> duplicates = csxdao.getChecksums(sum.getSha1());
            duplicateEntries.addAll(duplicates);
        }
        return duplicateEntries;

    }  //- findDuplicates
    
    
    protected void updateHubMapping(Document doc, String doi)
    throws SQLException {

        List<String> oldUrls = csxdao.getUrls(doi);
        HashSet<String> urls = new HashSet<String>();
        for (String url : oldUrls) {
            urls.add(url);
        }
        
        List<String> newUrls = doc.getFileInfo().getUrls();
        for (String url : newUrls) {
            if (urls.contains(url)) {
                List<Hub> oldHubs = csxdao.getHubsForUrl(url);
                HashSet<String> hubUrls = new HashSet<String>();
                for (Hub hub : oldHubs) {
                    hubUrls.add(hub.getUrl());
                }
                for (Hub hub : doc.getFileInfo().getHubs()) {
                    if (!hubUrls.contains(hub.getUrl())) {
                        csxdao.addHubMapping(hub, url, doi);
                    }
                }
            } else {
                csxdao.insertUrl(doi, url);
                for (Hub hub : doc.getFileInfo().getHubs()) {
                    csxdao.addHubMapping(hub, url, doi);
                }
            }
        }
        
    }  //- updateHubMapping
    
}  //- class DocumentEntryPoint
