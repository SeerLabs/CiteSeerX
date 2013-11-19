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
import java.util.List;

import edu.psu.citeseerx.domain.Tag;

/**
 * Provides transparent access to Tags persistence storage 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface TagDAO {

    /**
     * Associates the given tag to the supplied paper id
     * @param paperid
     * @param tag
     * @throws DataAccessException
     */
    public void addTag(String paperid, String tag) throws DataAccessException;
    
    /**
     * deletes the given tag from the paperid set of tags
     * @param paperid
     * @param tag
     * @throws DataAccessException
     */
    public void deleteTag(String paperid, String tag)
    throws DataAccessException;
    
    /**
     * 
     * @param paperid
     * @return A list of tags assigned to the given document
     * @throws DataAccessException
     */
    public List<Tag> getTags(String paperid) throws DataAccessException;
    
} //- interface TagDAO
