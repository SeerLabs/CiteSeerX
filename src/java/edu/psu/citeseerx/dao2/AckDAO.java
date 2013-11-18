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
import edu.psu.citeseerx.domain.Acknowledgment;

/**
 * Provides transparent access to Acknowledgment persistence storage 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public interface AckDAO {

    /**
     * Returns a list of acknowledges associated to the given doi
     * @param doi   doi to get the acknowledges from
     * @param getContexts if true the context of each acknowledges is included
     * @param getSource if true the source is included in the answer
     * @return A list of acknowledges associated to doi
     * @throws DataAccessException
     */
    public List<Acknowledgment> getAcknowledgments(String doi,
            boolean getContexts, boolean getSource)
            throws DataAccessException;

    /**
     * Add a new acknowledge to the doi
     * @param doi
     * @param ack
     * @throws DataAccessException
     */
    public void insertAcknowledgment(String doi, Acknowledgment ack)
    throws DataAccessException;
    
    /**
     * 
     * @param ackID
     * @return The acknowledge text associated to the given acknowledge id
     * @throws DataAccessException
     */
    public List<String> getAckContexts(Long ackID)
    throws DataAccessException;
    
    /**
     * Insert all the contexts where the acknowledge was found
     * @param ackID
     * @param contexts
     * @throws DataAccessException
     */
    public void insertAckContexts(Long ackID, List<String> contexts)
    throws DataAccessException;
    
    public void updateAcknowledgment(Acknowledgment ack)
    throws DataAccessException;
    
    /**
     * Updates the acknowledge
     * @param ack
     * @param clusterID
     * @throws DataAccessException
     */
    public void setAckCluster(Acknowledgment ack, Long clusterID)
    throws DataAccessException;
    
    /**
     * Deletes all the acknowledges associated with doi
     * @param doi
     * @throws DataAccessException
     */
    public void deleteAcknowledgments(String doi) throws DataAccessException;
    
    /**
     * Deletes the acknowledge associated to ackID
     * @param ackID
     * @throws DataAccessException
     */
    public void deleteAcknowledgment(Long ackID) throws DataAccessException;
    
    /**
     * Deletes the acknowledge contexts associated with ackID
     * @param ackID
     * @throws DataAccessException
     */
    public void deleteAckContexts(Long ackID) throws DataAccessException;
    
} //- interface AckDAO
