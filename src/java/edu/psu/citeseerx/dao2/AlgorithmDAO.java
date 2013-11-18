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

import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.domain.Algorithm;

/**
 * Provides transparent access to Algorithms from persistent storage
 * @author jSumit Bhatia
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public interface AlgorithmDAO {
    
    /**
     *Returns a list of document-elements (Algorithms) associated with the given doi
     * 
     * @param id
     *            id of the document element
     * @return A Algorithm object
     * @throws DataAccessException
     */
    public Algorithm getAlgorithm(long id) throws DataAccessException;
    
    /**
     * Returns total number of document elements
     * 
     * @return algorithm number
     * @throws DataAccessException
     */
    public Integer countAlgorithm() throws DataAccessException;
    
    /*
     * Inserts an Algorithm into the database either by proxy or
     * doi id.
     * @param oneAlgorithm 
     *          The Algorithm object to be inserted
     * @param 
     * 
     */
    public void insertAlgorithm(Algorithm oneAlgorithm) 
    throws DataAccessException;
    
    /*
     * Select and Update The index Times;
     * @returns java.sql.Date 
     * @throws DataAccessException 
     */
    public Date lastAlgorithmIndexTime() throws DataAccessException;

    /* 
     * Returns all Algorithms updated since the given date
     * @param date from when 
     * @returns list of Algorithm elements
     * @throws DataAccessException
    */
    public List<Algorithm> getUpdatedAlgorithms(Date dt) 
    throws DataAccessException;

    /*
     * Update the last time the index was updated (now)
     * @throws DataAccessException
     */
    public void updateAlgorithmIndexTime() throws DataAccessException;

} //- interface AlgorithmDAO
