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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.ExternalLink;

/**
 * Abstract implementation of ExternalLinkUpdater implementing all the common 
 * methods to each ExternalLinkUpdater
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public abstract class AbstractExternalLinkUpdater 
implements ExternalLinkUpdater {

    protected final Log logger = LogFactory.getLog(getClass());
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private String label;
    
    /**
     * Sets the label that identifies this external source within the database
     * @param label A label to identify the external source. <b>Note:</b> This
     * value must match a value in the links_types table within the database.
     */
    public void setLabel(String label) {
        this.label = label;
    } //- setLabel
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.updates.ExternalLinkUpdater#getPapersNoELink(java.lang.Long, java.lang.String)
     */
    public List<String> getPapersNoELink(Long amount, String lastID) {
        return csxdao.getPapersNoELink(label, lastID, amount);
    } //- getPapersNoELink

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.updates.ExternalLinkUpdater#updateELinkForPaper(java.lang.String)
     */
    public void updateELinkForPaper(String doi) {

        try {
            Document doc = csxdao.getDocumentFromDB(doi);
            if (doc != null) {
                updateELinkForPaper(doc);
            }else{
                logger.info("No document was found for doi: " + doi);
            }
        }catch (DataAccessException e) {
            logger.error("An error occured updating external links for " +
            		"document: " + doi, e);
        }      
    } //- updateELinkForPaper


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.updates.ExternalLinkUpdater#updateELinkForPaper(edu.psu.citeseerx.domain.Document)
     */
    public void updateELinkForPaper(Document doc) {
        try {
            String urlPortion = getUrlForPaper(doc);
            String doi = doc.getDatum(Document.DOI_KEY);
                
            if (urlPortion != null) {
                ExternalLink eLink = new ExternalLink();
                eLink.setLabel(label);
                eLink.setPaperID(doi);
                eLink.setUrl(urlPortion);
                csxdao.updateExternalLink(eLink);
            }else{
                if (csxdao.getExternalLinkExist(label, doi)) {
                    // The document has an elink for label but it's a wrong one
                    csxdao.deleteExternalLink(doi, label);
                }
            }
        }catch (DataAccessException e) {
            logger.error("An error occured updating external links for " +
                    "document: " + doc.getDatum(Document.DOI_KEY), e);
        }
        
    } //- updateELinkForPaper

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.updates.ExternalLinkUpdater#updateExternalLinks()
     */
    public void updateExternalLinks() {
        String lastID = "";
        boolean finished = false;
        
        Long amount = new Long(1000);
        List<String> docIDs = new ArrayList<String>();
        logger.info("Starting updates for label: " + label);
        do {
            docIDs = getPapersNoELink(amount, lastID);
            if (docIDs.isEmpty()) {
                finished = true;
            }else{
                for (String doi : docIDs) {
                    logger.info("Updating External Link for document: " + doi + 
                            " using " + label + " label");
                    updateELinkForPaper(doi);
                    logger.info("External Link for document: " + doi + 
                            " using " + label + " label has been updated");
                }
                lastID = docIDs.get(docIDs.size()-1);
            }
        }while(!finished);
        logger.info("Updates for label: " + label + " has finished");
        
    } //- updateExternalLinks
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.updates.UpdateListener#handleUpdate(edu.psu.citeseerx.domain.Document)
     */
    public void handleUpdate(Document doc) {
        logger.trace("Handling update for: " + doc.getDatum(Document.DOI_KEY));
        updateELinkForPaper(doc);
        logger.trace("Update for: " + doc.getDatum(Document.DOI_KEY) + 
                " finished");
    } //- handleUpdate

    /**
     * Child classes must override this method.
     * @see edu.psu.citeseerx.updates.external.links.ExternalLinkUpdater#getUrlForPaper(edu.psu.citeseerx.domain.Document)
     */
    public String getUrlForPaper(Document doc) {
        return null;
    } //- getUrlForPaper
} //- class ExternalLinkAbstractUpdater
