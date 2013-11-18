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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.psu.citeseerx.dao2.logic.CSXExternalMetadataFacade;
import edu.psu.citeseerx.domain.DBLP;
import edu.psu.citeseerx.domain.Document;

/**
 * External Link Updater for DBLP
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DBLPExternalLinkUpdater extends AbstractExternalLinkUpdater {

    protected final Log logger = LogFactory.getLog(getClass());
    
    private CSXExternalMetadataFacade csxemetadata;

    public void setCSXEMETADATA(CSXExternalMetadataFacade csxemetadata) {
        this.csxemetadata = csxemetadata;
    } //- setCSXEMETADATA
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.updates.externallinks.AbstractExternalLinkUpdater#getUrlForPaper(edu.psu.citeseerx.domain.Document)
     */
    public String getUrlForPaper(Document doc) {
        
        String resp = null;
        String title = doc.getDatum(Document.TITLE_KEY);
        if (title != null) {
            List<DBLP> records = csxemetadata.getDBLPRecordsByTitle(title);
            // For now, If more than one record is returned we'll use just the
            // first one. The DBLP records has all the information so we can try
            // to match using more information.
            if (records.size() != 0) {
                resp = records.get(0).getUrl();
            }
        }
        return resp;
    } //- getUrlForPaper
    
    
} //- class DBLPExternalLinkUpdater
