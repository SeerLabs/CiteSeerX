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
package edu.psu.citeseerx.updates.external.metadata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.dao2.logic.CSXExternalMetadataFacade;
import edu.psu.citeseerx.domain.CiteULike;

/**
 * Stores all mappings from CiteULike (provided in a linkouts file: 
 * http://www.citeulike.org/faq/data.adp) into the external metadata
 * storage. This data is used by other components in different ways. For example,
 * to obtain information to generate links from summary pages.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class CiteulikeMetadataUpdater {
    
    private static String CSX_LINKOUT_TYPE = "CITESX";
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    private CSXExternalMetadataFacade csxemetadata;

    public void setCSXEMETADATA(CSXExternalMetadataFacade csxemetadata) {
        this.csxemetadata = csxemetadata;
    } //- setCSXEMETADATA
    
    private String linkOutFile;
    
    /**
     * @param CiteULike linkout file location (full path)
     * Expected format:
     * 1. Unix Text file ('\n' is the line ending)
     * 2. One record per line
     * 3. Fields are '|' separated
     * 4. Columns: Article id, link out type, ikey_1, ckey_1, ikey_2, ckey_2
     */
    public void setLinkOutFile(String linkOutFile) {
        this.linkOutFile = linkOutFile;
    } //- setDBLPDataFile
    
    /**
     * Updates CiteULike mappings using a link out file.
     * setLinkOutFile should be called before calling this method.
     * Expected format:
     *      Article id|Linkout Type|ikey_1|ckey_1|ikey_2|ckey_2
     *      
     * Sample:
     *      48|PMID  |11497662|||
     *      50|NATUR |406|406188a0|6792|nature
     *      2998399|CITESX||10.1.1.103.1502||
     *      
     * From this file we are interested in records which have CITESX as link out
     * type.
     * @throws FileNotFoundException If the file doesn't exists or no file
     * was set up
     */
    public void updateCiteulike() {
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(linkOutFile)));
            String line = null;
            
            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains(CSX_LINKOUT_TYPE)) {
                    String[] tokens = line.split("\\|");
                    CiteULike record = new CiteULike();
                    record.setCiteulikeID(tokens[0]);
                    record.setCiteSeerXID(tokens[3]);
                    i++;
                    sendRecord(record);
                }
                if (i != 0 && i%1000 == 0) {
                    logger.info(i + " records has been processed");
                }
            }
            if (i%1000 != 0) {
                logger.info(i + " records has been processed");
            }
            logger.info("CiteULike metadata has been updated: " + i + " has " +
            		"been processed");
        }catch (FileNotFoundException e) {
            logger.fatal("The linkout file was not found", e);
        }catch (IOException e) {
            logger.fatal("A problem reading " + linkOutFile + " was found", e);
        }
    } //- updateCiteulike
    
    private void sendRecord(CiteULike record) {
        logger.info("Storing: " + record.getCiteulikeID());
        try {
            csxemetadata.addCiteULikeRecord(record);
        }catch (DataAccessException e) {
            logger.error("Error storing: " + record.getCiteulikeID(), e);
        }
    } //-sendRecord
} //- Class CiteulikeMetadataUpdater
