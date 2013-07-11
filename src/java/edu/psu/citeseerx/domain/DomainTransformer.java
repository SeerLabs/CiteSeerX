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
package edu.psu.citeseerx.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utilities for translating between the various domain object formats
 * in CiteSeerX.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class DomainTransformer {

    /**
     * @param citation
     * @return a ThinDoc representation of the given Citation object.
     */
    public static ThinDoc toThinDoc(Citation citation) {
        
        ThinDoc thinDoc = new ThinDoc();
        
        try {
            thinDoc.setCluster(new Long(citation.getDatum(Citation.CLUST_KEY)));
        } catch (Exception e) {}

        List<String> authors = citation.getAuthorNames();
        StringBuffer authBuf = new StringBuffer();
        for (Iterator<String> it = authors.iterator(); it.hasNext(); ) {
            authBuf.append(it.next());
            if (it.hasNext()) {
                authBuf.append(", ");
            }
        }
        thinDoc.setAuthors(authBuf.toString());

        thinDoc.setTitle(citation.getDatum(Citation.TITLE_KEY));
        thinDoc.setVenue(citation.getDatum(Citation.VENUE_KEY));
        thinDoc.setVentype(citation.getDatum(Citation.VEN_TYPE_KEY));
        try {
            thinDoc.setYear(Integer.parseInt
                    (citation.getDatum(Citation.YEAR_KEY)));
        } catch (Exception e) {}
        thinDoc.setPages(citation.getDatum(Citation.PAGES_KEY));
        thinDoc.setPublisher(citation.getDatum(Citation.PUBLISHER_KEY));
        try {
            thinDoc.setVol(Integer.parseInt(
                    citation.getDatum(Citation.VOL_KEY)));
        } catch (Exception e) {}
        try {
            thinDoc.setNum(Integer.parseInt(
                    citation.getDatum(Citation.NUMBER_KEY)));
        } catch (Exception e) {}
        thinDoc.setTech(citation.getDatum(Citation.TECH_KEY));
        
        return thinDoc;
        
    }  //- toThinDoc
    
    
    /**
     * @param doc
     * @return a ThinDoc representation of the given Document object.
     */
    public static ThinDoc toThinDoc(Document doc) {
        
        ThinDoc thinDoc = new ThinDoc();
        
        try {
            thinDoc.setCluster(new Long(doc.getDatum(Document.CLUST_KEY)));
        } catch (Exception e) {}
        thinDoc.setDoi(doc.getDatum(Document.DOI_KEY));

        List<Author> authors = doc.getAuthors();
        StringBuffer authBuf = new StringBuffer();
        for (Iterator<Author> it = authors.iterator(); it.hasNext(); ) {
            authBuf.append(it.next().getDatum(Author.NAME_KEY));
            if (it.hasNext()) {
                authBuf.append(", ");
            }
        }
        thinDoc.setAuthors(authBuf.toString());
        
        thinDoc.setTitle(doc.getDatum(Document.TITLE_KEY));
        thinDoc.setVenue(doc.getDatum(Document.VENUE_KEY));
        thinDoc.setVentype(doc.getDatum(Document.VEN_TYPE_KEY));
        try {
            thinDoc.setYear(Integer.parseInt
                    (doc.getDatum(Document.YEAR_KEY)));
        } catch (Exception e) {}
        thinDoc.setPages(doc.getDatum(Document.PAGES_KEY));
        thinDoc.setPublisher(doc.getDatum(Document.PUBLISHER_KEY));
        try {
            thinDoc.setVol(Integer.parseInt(
                    doc.getDatum(Document.VOL_KEY)));
        } catch (Exception e) {}
        try {
            thinDoc.setNum(Integer.parseInt(
                    doc.getDatum(Document.NUM_KEY)));
        } catch (Exception e) {}
        thinDoc.setTech(doc.getDatum(Document.TECH_KEY));
        thinDoc.setNcites(doc.getNcites());
        thinDoc.setSelfCites(doc.getSelfCites());
        thinDoc.setUpdateTime(doc.getVersionTime());
        
        return thinDoc;
        
    }  //- toThinDoc
    
    
    /**
     * @param thinDoc
     * @return a Document object representing the given ThinDoc.
     */
    public static Document toDocument(ThinDoc thinDoc) {
        
        Document doc = new Document();
        
        try {
            doc.setClusterID(thinDoc.getCluster());
        } catch (Exception e) {}

        String authStr = thinDoc.getAuthors();
        List<Author> authors = new ArrayList<Author>();
        if (authStr != null) {
            StringTokenizer st = new StringTokenizer(authStr, ",");
            while(st.hasMoreTokens()) {
                Author auth = new Author();
                auth.setDatum(Author.NAME_KEY, st.nextToken().trim());
                authors.add(auth);
            }
        }
        doc.setAuthors(authors);
        
        doc.setDatum(Document.TITLE_KEY, thinDoc.getTitle());
        doc.setDatum(Document.VENUE_KEY, thinDoc.getVenue());
        doc.setDatum(Document.VEN_TYPE_KEY, thinDoc.getVentype());
        if (thinDoc.getYear() > 0) {
            doc.setDatum(Document.YEAR_KEY,Integer.toString(thinDoc.getYear()));
        }
        doc.setDatum(Document.PAGES_KEY, thinDoc.getPages());
        doc.setDatum(Document.PUBLISHER_KEY, thinDoc.getPublisher());
        if (thinDoc.getVol() > 0) {
            doc.setDatum(Document.VOL_KEY, Integer.toString(thinDoc.getVol()));
        }
        if (thinDoc.getNum() > 0) {
            doc.setDatum(Document.NUM_KEY, Integer.toString(thinDoc.getNum()));
        }
        doc.setDatum(Document.TECH_KEY, thinDoc.getTech());
        doc.setNcites(thinDoc.getNcites());
        doc.setSelfCites(thinDoc.getSelfCites());
        doc.setVersionTime(thinDoc.getUpdateTime());
        
        return doc;
        
    }  //- toDocument
    
    
}  //- class DomainTransformer
