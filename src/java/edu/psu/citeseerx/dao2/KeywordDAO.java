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

import java.util.List;
import edu.psu.citeseerx.domain.Keyword;
import org.springframework.dao.DataAccessException;

/**
 * Provides transparent access to Keywords persistence storage 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public interface KeywordDAO {

    /**
     * 
     * @param doi
     * @param getSource
     * @return A list of keywords associated to the given doi
     * @throws DataAccessException
     */
    public List<Keyword> getKeywords(String doi, boolean getSource)
    throws DataAccessException;
    
    /**
     * Associates a keyword with the given doi
     * @param doi
     * @param keyword
     * @throws DataAccessException
     */
    public void insertKeyword(String doi, Keyword keyword)
    throws DataAccessException;
    
    /**
     * Updates a keyword associated to the given doi
     * @param doi
     * @param keyword
     * @throws DataAccessException
     */
    public void updateKeyword(String doi, Keyword keyword)
    throws DataAccessException;
    
    /**
     * Deletes the keyword from the keyword set associted to the given doi 
     * @param doi
     * @param keyword
     * @throws DataAccessException
     */
    public void deleteKeyword(String doi, Keyword keyword)
    throws DataAccessException;
    
    /**
     * Deletes all the keywords associated to doi
     * @param doi
     * @throws DataAccessException
     */
    public void deleteKeywords(String doi) throws DataAccessException;
    
} //- interface KeywordDAO
