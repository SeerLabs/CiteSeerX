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
import edu.psu.citeseerx.domain.ACM;
import edu.psu.citeseerx.domain.Document;

/**
 * External Link Updater for ACM. Tries to match corpus documents to the
 * corresponding ACM ones
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ACMExternalLinkUpdater extends AbstractExternalLinkUpdater {
protected final Log logger = LogFactory.getLog(getClass());
    
    private CSXExternalMetadataFacade csxemetadata;

    public void setCSXEMETADATA(CSXExternalMetadataFacade csxemetadata) {
        this.csxemetadata = csxemetadata;
    } //- setCSXEMETADATA

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.updates.external.links.AbstractExternalLinkUpdater#getUrlForPaper(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public String getUrlForPaper(Document doc) {
        String resp = null;
        String title = doc.getDatum(Document.TITLE_KEY);
        if (title != null) {
            List<ACM> records = csxemetadata.getACMRecordsByTitle(title);
            // For now, If more than one record is returned we'll use just the
            // first one. The ACM records has all the information so we can try
            // to match using more information.
            if (records.size() != 0) {
                resp = records.get(0).getUrl();
            }
        }
        return resp;
    } //- getUrlForPaper
    
    
} //- class ACMExternalLinkUpdater
