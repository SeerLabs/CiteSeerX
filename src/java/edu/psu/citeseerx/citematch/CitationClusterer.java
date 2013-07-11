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
package edu.psu.citeseerx.citematch;

import edu.psu.citeseerx.domain.Document;

import org.json.JSONException;

/**
 * Generic specification for citation clustering implementations.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface CitationClusterer {

    /**
     * Filters the citation list for self citations, generates metadata keys,
     * then clusters the Document and Citations within the database backend.
     */
    public void clusterDocument(Document doc) throws JSONException;

    /**
     * Filters the citation list for self citations, generates new keys
     * based on existing metadata, then calls the database API to delete
     * the Document and Citations, then re-cluster them based on the
     * new key and self-citation information.
     */
    public void reclusterDocument(Document doc) throws JSONException;

    /**
     * Does the same thing as the simpler reclusterDocument, but checks to
     * see whether key information has changed before triggering a
     * call to recluster the Document and Citations.
     */
    public void reclusterDocument(Document newDoc, Document oldDoc)
    throws JSONException;
    
    /**
     * Deletes the document from its cluster.
     * @param doc
     */
    public void deleteDocumentFromCluster(Document doc);

} //- interface CitationClusterer
