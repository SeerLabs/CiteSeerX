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
package edu.psu.citeseerx.dao2;

import org.springframework.dao.DataAccessException;

/**
 * Provides transparent access to Legacy data persistence storage.
 * This is a map from CiteSeer identifiers to CiteSeerX ones 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface LegacyIDDAO {

    /**
     * 
     * @param legacyID
     * @return Returns a CiteSeerX DOI to be related with legacyID
     * @throws DataAccessException
     */
    public String getNewID(int legacyID) throws DataAccessException;

    /**
     * Stores the association between a CiteSeerX identifier and a 
     * CiteSeer one
     * @param csxID
     * @param legacyID
     * @throws DataAccessException
     */
    public void insertLegacyIDMapping(String csxID, int legacyID)
    throws DataAccessException;
    
}
