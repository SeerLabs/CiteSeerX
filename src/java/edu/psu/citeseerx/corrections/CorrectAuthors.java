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
package edu.psu.citeseerx.corrections;

import java.util.Date;
import java.util.List;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.updates.UpdateManager;
import edu.psu.citeseerx.utility.SeerSoftTFIDF;

/**
 * Delete duplicate authors from the same paper if any duplicate is found.
 * This job search for duplicates authors in a paper and deletes the one
 * with less information.
 * 
 * An author is duplicated if there is more than one author record that shared
 * the same name (distance metrics is used to determine if two names are equal)
 * within the same document.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CorrectAuthors {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    private static final String CORRECTION_SOURCE = "user correction - " +
    		"Author Dedup";
    private static final int MAX_RECORDS = 1000;
    
    private UpdateManager updateManager;
    
    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    } //- setUpdateManager
    
    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
    
    private String modelFile;

    public void setModelFile (String modelFile) {
        this.modelFile = modelFile;
    } //- setModelFile
    
    private double similarityFactor = 0.8;
    
    public void setSimilarityFactor(double similarityFactor) {
        this.similarityFactor = similarityFactor;
    } //- setSimilarityFactor
    
    private SeerSoftTFIDF distanceMetric = null;
    
    /*
     * Informs if two authors have the same name
     */
    private boolean compareAuthors(Author a1, Author a2) {
        
        boolean result = false;
        double similarity = distanceMetric.score(a1.getDatum(Author.NAME_KEY),
                a2.getDatum(Author.NAME_KEY));
        
        if (similarity >= similarityFactor) {
            // They are equal or pretty similar then they are the same
            result = true;
        }
        
        return result;
    } //- compareAuthors

    private String authorKeys[] = {Author.NAME_KEY, Author.AFFIL_KEY, 
            Author.ADDR_KEY, Author.EMAIL_KEY};
    
    /*
     * Returns the number of fields with a meaningful value. The value is 
     * neither null nor an empty string. 
     * 
     * Order is not included in the checked fields since it will have always a
     * value, and is not meaningful for the purpose of this Fixer. 
     */
    private int howManyFilledFields(Author author) {
        int filled = 0;
        
        String value;
        for (int j = 0; j < authorKeys.length; ++j) {
            value = author.getDatum(authorKeys[j]);
            if (null != value && value.trim().length() > 0) {
                filled++;
            }
        }
        return filled;
    } //- getNotNullFields
    
    /*
     * Sets the correction source for the author
     */
    private void setCorrectionSource(Author author) {
        for (int j = 0; j < authorKeys.length; ++j) {
            author.setSource(authorKeys[j], CORRECTION_SOURCE);
        }
    } //- setCorrectionSource
    
    /*
     * Authors a1 and a2 have the same name but probably are not equal from the
     * document stand point (a different order, for instance).
     * 
     * Returns:
     *  -1 if author1 has more data than author2
     *   0 if both author have the same amount of data
     *   1 if author2 has more data than author1
     */
    private int bestAuthor(Author a1, Author a2) {
        int result = 0;
        int filledFieldsA1 = howManyFilledFields(a1);
        int filledFieldsA2 = howManyFilledFields(a2);
        if (filledFieldsA2 > filledFieldsA1) {
            result = 1;
        }else if (filledFieldsA1 > filledFieldsA2) {
            result = -1;
        }
        return result;
         
    } //- bestAuthor

    /*
     * Eliminates duplicate authors from this document if any.
     * The author list is ordered by order of appearance. This means that 
     * duplicates could be in any place in the author list.
     */
    private boolean eliminateDuplicateAuthors(Document doc) {
        List<Author> authors = doc.getAuthors();
        
        if (null == authors || authors.size() <= 1) {
            // Nothing to do.
            return false;
        }
        
        boolean corrected = false;
        for (int i = authors.size() - 1; i >= 1; --i) {
            Author lastAuthor = authors.get(i);
            for (int j = i - 1; j >= 0; --j) {
                Author currentAuthor = authors.get(j);
                if (compareAuthors(lastAuthor, currentAuthor)) {
                    logger.info("Duplicate authors have been found: " + 
                            lastAuthor.getDatum(Author.NAME_KEY) + " and " + 
                            currentAuthor.getDatum(Author.NAME_KEY));
                    
                    // They are the same. Find out which one has more data.
                    if (-1 == bestAuthor(lastAuthor, currentAuthor)) {
                        authors.set(j, lastAuthor);
                    }

                    // No matter what. We always delete the last one.
                    authors.remove(i);
                    j = -1;
                    corrected = true;
                }
            }
        }
        if (corrected) {
            for (int i = 0; i < authors.size(); i++) {
                authors.get(i).setDatum(Author.ORD_KEY, Integer.toString(i+1));
                authors.get(i).setSource(Author.ORD_KEY, CORRECTION_SOURCE);
                
                // update source of correction for the rest of the fields
                setCorrectionSource(authors.get(i));
            }
            
            // Send the correction to the database.
            logger.info("Sending correction of: " + 
                    doc.getDatum(Document.DOI_KEY));
            try {
                updateManager.doCorrection(doc, "SystemCorrections");
            }catch (IOException e) {
                logger.error("An error ocurred while saving correction for: " +
                        doc.getDatum(Document.DOI_KEY), e);
            }catch (JSONException e) {
                logger.error("An error ocurred while saving correction for: " +
                        doc.getDatum(Document.DOI_KEY), e);
            }
        }
        return corrected;
    } //- eliminateDuplicateAuthors
    
    /**
     * Search for new documents within the corpus, and delete duplicates authors
     * If a user correction is found, nothing is done.
     * @return The number of corrected documents
     */
    public int process() {

        int numDocuments = 0;
        int numUpdated = 0;
        String lastDOI = "0.0.0.0.0";
        distanceMetric = new SeerSoftTFIDF(similarityFactor);
        
        try  {
            distanceMetric.loadModel(modelFile);
            Date start = citedao.getLastAuthorDedupTime();
            Date end = new Date(System.currentTimeMillis()); 

            List<String> dois = csxdao.getCrawledDOIs(start, end, lastDOI, 
                    MAX_RECORDS);
            boolean finished = dois.isEmpty();
            boolean corrected;
            while (!finished) {
                numDocuments++;
                for (String doi : dois) {
                    // get the document.
                    Document doc = csxdao.getDocumentFromDB(doi);
                    logger.info("Processing document: " + doi);
                    if (doc.getVersionName() != "USER") {
                        corrected = eliminateDuplicateAuthors(doc);
                    }else{
                        corrected = false;
                    }
                    logger.info("Document: " + doi + " has been processed");
                    lastDOI = doi;
                    if (corrected) {
                        numUpdated++;
                    }
                }
                if ( (numDocuments%MAX_RECORDS) == 0 ) {
                    logger.info("Processed: " + numDocuments);
                }
                dois = csxdao.getCrawledDOIs(start, end, lastDOI, 
                        MAX_RECORDS);
                finished = dois.isEmpty();
                
            }
            logger.info("Processed: " + numDocuments);
            citedao.setLastAuthorDedupTime(end);
        }catch (IOException e) {
            logger.error("An error ocurred while loading the model file", e);
        }
        logger.info(numUpdated + " documents were updated");
        return numUpdated;
    } //- process

} //- class CorrectAuthors
