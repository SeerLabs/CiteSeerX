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
package edu.psu.citeseerx.updates;

import java.io.IOException;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.utility.CSXConstants;
import edu.psu.citeseerx.dao2.logic.CSXDAO;

/**
 * This class decides when new versions should be triggered and keeps the
 * database and filesystem versioning info up-to-date.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class VersionManager {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    /**
     * Decides whether a document is ready for a version change and updates
     * the database and filesystem backends to reflect any changes made.
     * @param doc
     * @param oldVersionName
     * @return whether a new version was created.
     * @throws IOException
     */
    public boolean handleUpdate(Document doc, String oldVersionName)
    throws IOException {

        String newVersionName = doc.getVersionName();
        if (newVersionName == null) {
            newVersionName = "";
        }
        if (oldVersionName == null) {
            oldVersionName = "";
        }

        if (newVersionName.equals(CSXConstants.USER_VERSION)) {
            // Always write new user versions.
            csxdao.insertVersion(doc);
            return true;
        }

        if (oldVersionName.equals(CSXConstants.USER_VERSION)) {
            if (!newVersionName.equals("")) {
                // Named versions will be written in this case, but
                // will not change the current version of the document.
                csxdao.insertVersion(doc);
            }
            // Versions submitted with no name when
            // there is already a user correction
            // will be ignored!
            return false;
        }
        
        csxdao.insertVersion(doc);
        return true;
    
    }  //- handleUpdate
        
}  //- class VersionManager
