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
package edu.psu.citeseerx.updates.external.links;

import java.util.List;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.updates.UpdateListener;

/**
 * Defines the way how CiteSeerX documents are linked to external sources.
 * Examples of External Sources are DBLP, ACM, CiteULike.
 * An External Links updater should implement this interface 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public interface ExternalLinkUpdater extends UpdateListener {
    
    /**
     * Obtains a list of papers (only the paper identifier) which don't have
     * external link for the label configured in the Updater. <b>Note:</b> The
     * label must correspond to the one configured in the Database. 
     * @param amount
     * @param lastID
     * @return A List containing at most amount number of papers identifiers
     * which doesn't have an external link for the giving label starting from
     * lastID (not included) 
     */
    public List<String> getPapersNoELink(Long amount, String lastID);

    /**
     * Updates external links for Papers in CiteSeerX corpus which doesn't have
     * the external link for the label in this ExternalLinkUpdater.
     */
    public void updateExternalLinks();
    
    /**
     * Tries to update the external link for the given paper for label 
     * configured for this updater. This method is intended to be use
     * in case the caller wants to updated <b>ONE</b> document; for instance, 
     * when integrating the updater in a pipe line.
     * @param doc
     */
    public void updateELinkForPaper(Document doc);
    
    /**
     * Tries to update the external link for the paper represented by doi for 
     * the label configured in the Updater. This method is intended to be use
     * in case the caller wants to updated <b>ONE</b> document; for instance, 
     * when integrating the updater in a pipe line.
     * @param doi
     * @param label
     */
    public void updateELinkForPaper(String doi);
    
    /**
     * Obtains the portion of the URL to be attached to the baseURL for the 
     * external link
     * @param doc The document to match. All information in this object can be
     * used to find the map
     * @return The portion of the URL to be appended to the baseURL for a given
     * CiteSeerX document in order to point to the external source. If no match
     * is found null is returned
     * <b>Note:</b> The baseURL is configured in the persistent storage in a 
     * tuple: <label, baseURL>
     */
    public String getUrlForPaper(Document doc);
} //- interface ExternalLinkUpdater
