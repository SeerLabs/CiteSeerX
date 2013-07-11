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
package edu.psu.citeseerx.myciteseer.dao;

import org.springframework.dao.DataAccessException;
import edu.psu.citeseerx.myciteseer.domain.Account;
import java.util.List;

/**
 * Provides transparent access to Tags persistence storage 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface TagDAO {

    /**
     * Add a new tag to the given paper
     * @param account User tagging the paper
     * @param doi Paper being tagged
     * @param tag 
     * @throws DataAccessException
     */
    public void addTag(Account account, String doi, String tag)
    throws DataAccessException;
    
    /**
     * Deletes a tag from a paper
     * @param account User deleting the tag
     * @param doi Paper id from which the tag is deleted
     * @param tag tag to be deleted
     * @throws DataAccessException
     */
    public void deleteTag(Account account, String doi, String tag)
    throws DataAccessException;
    
    /**
     * @param account
     * @return The list of tags created by the the given user. An empty list
     * if the user has not tagged anything yet
     * @throws DataAccessException
     */
    public List<String> getTags(Account account) throws DataAccessException;
    
    /**
     * 
     * @param account User account
     * @param tag 
     * @return A list of paper ids that the user have tagged with the given tag
     * @throws DataAccessException
     */
    public List<String> getDoisForTag(Account account, String tag)
    throws DataAccessException;
    
} //- Interface TagDAO
