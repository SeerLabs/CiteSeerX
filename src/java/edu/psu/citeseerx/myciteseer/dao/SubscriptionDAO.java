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
 * Provides transparent access to subscriptions persistence storage 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public interface SubscriptionDAO {

	/**
	 * Created a monitor for the given user over the given paper. 
	 * @param account User who wants to monitor the paper
	 * @param paperid The paper being monitored
	 * @throws DataAccessException
	 */
    public void addMonitor(Account account, String paperid)
    throws DataAccessException;
    
    /**
     * Deletes a specific monitor over a paper 
     * @param account User monitoring the paper
     * @param paperid Monitored paper 
     * @throws DataAccessException
     */
    public void deleteMonitor(Account account, String paperid)
    throws DataAccessException;
    
    /**
     * @param account
     * @return A list of paper ids being monitored by the given user
     * @throws DataAccessException
     */
    public List<String> getMonitors(Account account) 
    throws DataAccessException;
    
    /**
     * 
     * @param doi
     * @return A list of users (usernames) monitoring the given paper 
     * @throws DataAccessException
     */
    public List<String> getUsersMonitoring(String doi)
    throws DataAccessException;
    
} //- Interface SubscriptionDAO
