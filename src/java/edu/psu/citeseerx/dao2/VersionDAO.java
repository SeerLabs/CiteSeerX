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
import edu.psu.citeseerx.domain.Document;
import java.io.IOException;

/**
 * Provides transparent access to Version persistence storage 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface VersionDAO {

    /**
     * Assigns a new version to the supplied doi
     * @param doi
     * @param version
     * @throws DataAccessException
     * @throws IOException
     */
    public void setVersion(String doi, int version)
    throws DataAccessException, IOException;

    /**
     * Inserts a new version of a document
     * @param doc
     * @return version id
     * @throws DataAccessException
     * @throws IOException
     */
    public boolean insertVersion(Document doc)
    throws DataAccessException, IOException;
    
    /**
     * Creates a new version for the document
     * @param doc
     * @throws DataAccessException
     */
    public void createNewVersion(Document doc) throws DataAccessException;
    
    /**
     * Gives a name to the given version of the supplied document
     * @param doi
     * @param version
     * @param name
     * @throws DataAccessException
     */
    public void setVersionName(String doi, int version, String name)
    throws DataAccessException;
    
    /**
     * Sets the given version as deprecated for the supplied document
     * @param doi
     * @param version
     * @throws DataAccessException
     */
    public void deprecateVersion(String doi, int version)
    throws DataAccessException;
    
    /**
     * Deprecate all the version created after the supplied one
     * @param doi
     * @param version
     * @throws DataAccessException
     */
    public void deprecateVersionsAfter(String doi, int version)
    throws DataAccessException;
    
    /**
     * Marks the given version as spam for the supplied document
     * @param doi
     * @param version
     * @param isSpam
     * @throws DataAccessException
     */
    public void setVersionSpam(String doi, int version, boolean isSpam)
    throws DataAccessException;
    
    /**
     * Inserts a correction to the given document, version tuple made by the
     * supplied user
     * @param userid
     * @param paperid
     * @param version
     * @throws DataAccessException
     */
    public void insertCorrection(String userid, String paperid, int version)
    throws DataAccessException;
    
    /**
     * 
     * @param paperid
     * @param version
     * @return Returns the user which did a correction over the supplied paper
     * that created the given version
     * @throws DataAccessException
     */
    public String getCorrector(String paperid, int version)
    throws DataAccessException;
    
} //- interface VersionDAO
