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

import java.util.List;

import edu.psu.citeseerx.myciteseer.domain.SubmissionNotificationItem;
import edu.psu.citeseerx.myciteseer.domain.UrlSubmission;

/**
 * Provides transparent access to the submission system persistence storage. 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface SubmissionDAO {

	/**
	 * Inserts a new URL pointing to a PDF, PS document or a HUB page 
	 * containing pointers to many PDF, PS documents.
	 * @param submission The url
	 */
    public void insertUrlSubmission(UrlSubmission submission);
    
    /**
     * @param username
     * @return All the URLs the given user has submitted
     */
    public List<UrlSubmission> getUrlSubmissions(String username);
    
    /**
     * @param jobID
     * @return The URL identified by the given jobID or null if that
     * jobID doesn't exist
     */
    public UrlSubmission getUrlSubmission(String jobID);
    
    /**
     * Informs if the given URL has been already submitted by the given user
     * @param url url to validate
     * @param username
     * @return true if user has already submitted the given URL, false 
     * otherwise 
     */
    public boolean isUrlAlreadySubmitted(String url, String username);
    
    /**
     * Inserts components of a URL submission. For instance, links to the
     * PDF documents a HUB page may contain.
     * @param note Submisssion component information
     */
    public void insertSubmissionComponent(SubmissionNotificationItem note);
    
    /**
     * Returns all the components for the submission identified by the
     * given jobID
     * @param jobID Submission identifier
     * @return  A list of SubmissionNotificationItems
     */
    public List<SubmissionNotificationItem> 
    getSubmissionComponents(String jobID);
    
    /**
     * Updated the status of the given submission.
     * @param jobID Job to be updated
     * @param status The new job status
     */
    public void updateJobStatus(String jobID, int status);
    
} //- Interface SubmissionDAO
