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
package edu.psu.citeseerx.messaging.messages;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import edu.psu.citeseerx.messaging.JMSSender;

/**
 * Wrapper for messages that indicate that a submission job has completed
 * in its entirety (all sub-urls have been crawled).
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class SubmissionJobCompleteMsg extends SubmissionNotification {

    /**
     * Initializes a SubmissionJobCompleteMsg with a raw MapMessage.
     * @param msg
     * @throws JMSException
     */
    public SubmissionJobCompleteMsg(MapMessage msg) throws JMSException {
        super(JOBCOMPLETION, msg);
    } //- SubmissionJobCompleteMsg
    
    /**
     * Initializes a SubmissionJobCompleteMsg with a utility for sending the 
     * message. The underlying MapMessage is generated from the sender.
     * @param sender
     * @throws JMSException
     */
    public SubmissionJobCompleteMsg(JMSSender sender) throws JMSException {
        super(JOBCOMPLETION, sender);
    } //- SubmissionJobCompleteMsg
    
    /**
     * Initializes a SubmissionJobCompleteMsg with a sender utility and type
     * specifier as well as all content fields.
     * @param sender
     * @param jobID
     * @param url
     * @param status
     * @throws JMSException
     */
    public SubmissionJobCompleteMsg(JMSSender sender, String jobID, String url,
            int status) throws JMSException {
        super(JOBCOMPLETION, sender, jobID, url, status);
    } //- SubmissionJobCompleteMsg
    
}  //- class SubmissionJobCompleteMsg
