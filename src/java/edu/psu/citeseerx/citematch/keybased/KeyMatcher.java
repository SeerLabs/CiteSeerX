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
package edu.psu.citeseerx.citematch.keybased;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONException;

import edu.psu.citeseerx.citematch.CitationClusterer;
import edu.psu.citeseerx.citematch.utils.Analyzer;
import edu.psu.citeseerx.citematch.utils.DefaultStopwordAnalyzer;
import edu.psu.citeseerx.citematch.utils.NameAnalyzer;
import edu.psu.citeseerx.citematch.utils.SelfCitationFilter;
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Citation;
import edu.psu.citeseerx.domain.Document;

/**
 * This CitationClusterer implementation uses a hash key approach to
 * cluster citations and documents in an online manner.  A KeyGenerator
 * is used to create the hash keys and mappings are managed within a
 * persistent storage backend.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class KeyMatcher implements CitationClusterer {

    private final KeyGenerator keyGenerator;
    
    public KeyMatcher() {
        Analyzer stopWordAnalyzer = new DefaultStopwordAnalyzer();
        Analyzer nameAnalyzer = new NameAnalyzer();
        keyGenerator = new KeyGenerator(stopWordAnalyzer, nameAnalyzer);
    } //- KeyMatcher

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
    
/*    
    public void matchCitations() throws SQLException {
        List<Citation> citations = csxdao.getCitations(28023, 1);
        for (Citation citation : citations) {
            clusterCitation(citation, null);
        }
    }
*/    
    
    /**
     * Creates keys for a given Citation and stores the keys within the
     * Citation object.
     */
    public void processCitation(Citation citation) {

        String title = citation.getDatum(Citation.TITLE_KEY);
        List<String> authors = citation.getAuthorNames();

        StringBuffer authBuf = new StringBuffer();
        for (Iterator<String> it = authors.iterator(); it.hasNext(); ) {
            authBuf.append(it.next());
            if (it.hasNext()) {
                authBuf.append(",");
            }
        }
        String authStr = authBuf.toString();

        if (title == null || authors == null) {
            return;
        }
        List<String> keys = keyGenerator.getKeys(title, authStr);
        citation.setKeys(keys);

    }  //- processCitation
    
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.citematch.CitationClusterer#clusterDocument(edu.psu.citeseerx.domain.Document)
     */
    public void clusterDocument(Document doc) throws JSONException {

        SelfCitationFilter.filterCitations(doc);
        List<String> keys = processDocument(doc);
        citedao.clusterDocument(keys, doc);
        
    }  //- clusterDocument
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.citematch.CitationClusterer#reclusterDocument(edu.psu.citeseerx.domain.Document)
     */
    public void reclusterDocument(Document doc) throws JSONException {

        SelfCitationFilter.filterCitations(doc);
        List<String> keys = processDocument(doc);
        citedao.reclusterDocument(keys, doc);
        
    } //- reclusterDocument
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.citematch.CitationClusterer#reclusterDocument(edu.psu.citeseerx.domain.Document, edu.psu.citeseerx.domain.Document)
     */
    public void reclusterDocument(Document newDoc, Document oldDoc)
    throws JSONException {
        
        SelfCitationFilter.filterCitations(newDoc);
        
        List<String> newKeys = processDocument(newDoc);
        List<String> oldKeys = processDocument(oldDoc);
        
        Set<String> s1 = new HashSet<String>(newKeys);
        Set<String> s2 = new HashSet<String>(oldKeys);
        
        if (!s1.equals(s2)) {
            citedao.reclusterDocument(newKeys, newDoc);
        }
    } //- reclusterDocument
    

    /**
     * Generates keys for all citations within the specified Document
     * and returns a list of keys for the Document itself. 
     * @param doc
     * @return the list of keys for the Document itself
     */
    protected List<String> processDocument(Document doc) {
        StringBuffer authBuf = new StringBuffer();
        for (Iterator<Author> it = doc.getAuthors().iterator();
        it.hasNext(); ) {
            String name = it.next().getDatum(Author.NAME_KEY);
            authBuf.append(name);
            if (it.hasNext()) {
                authBuf.append(",");
            }
        }
        
        for (Citation citation : doc.getCitations()) {
            processCitation(citation);
        }

        String authStr = authBuf.toString();
        String title = doc.getDatum(Document.TITLE_KEY);
        
        List<String> keys = keyGenerator.getKeys(title, authStr);
                
        return keys;
        
    }  //- processDocument
    
    
    /**
     * Clusters all Documents and Citations in the corpus from scratch.  It
     * is highly recommended to make sure the citation graph database is
     * empty before calling this method.
     * @param start
     * @throws JSONException
     */
    public void buildAll(String start) throws JSONException {

        int amount = 1000;
        int counter = 0;

        while(true) {
            List<String> dois = csxdao.getDOIs(start, amount);
            if (dois.isEmpty()) {
                break;
            }
            for (String doi : dois) {
                System.out.println(doi);
                Document doc = csxdao.getDocumentFromDB(doi, true, false);
                clusterDocument(doc);
                start = doi;
                counter++;
            }
            System.out.println("finished "+counter);
        }
            //if ((current % 10) == 0) {
            //    System.out.println("finished "+current);
            //}
    } //- buildAll
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.citematch.CitationClusterer#deleteDocumentFromCluster(edu.psu.citeseerx.domain.Document)
     */
    public void deleteDocumentFromCluster(Document doc) {
        citedao.deleteDocument(doc);
    } //- deleteDocumentFromCluster
    
    /*
    public static void main(String[] args) throws Exception {

        long t0 = System.currentTimeMillis();
        
        CSXDAO csxdao = new CSXDAO();
        csxdao.setDataSource(DBCPFactory.createDataSource("citeseerx"));

        CiteClusterDAOImpl clusterdao = new CiteClusterDAOImpl();
        DataSource dataSource = DBCPFactory.createDataSource("citegraph");
        clusterdao.setDataSource(dataSource);
        
        KeyMatcher matcher = new KeyMatcher();
        matcher.setCSXDAO(csxdao);
        matcher.setCiteClusterDAO(clusterdao);
        matcher.buildAll();
        
        long elapsed = System.currentTimeMillis() - t0;
        System.out.println("Elapsed seconds: "+(elapsed/1000));
        
    }
    */
    
}  //- KeyMatcher
