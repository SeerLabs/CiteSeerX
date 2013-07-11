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

import javax.jms.*;
import edu.psu.citeseerx.messaging.*;

/**
 * Wrapper for messages that are sent to the crawler to indicate a new
 * URLs that should be crawled.
 * 
 * @author Isaac Councill
 *
 */
public class UrlSubmissionMsg extends JMSMessage {

    /**
     * Initializes a UrlSubmissionMsg with a raw MapMessage.
     * @param msg
     */
    public UrlSubmissionMsg(MapMessage msg) {
        super(msg);
    } //- UrlSubmissionMsg
    
    /**
     * Initializes a UrlSubmissionMsg with a utility for sending the 
     * message. The underlying MapMessage is generated from the sender.
     * @param sender
     * @throws JMSException
     */
    public UrlSubmissionMsg(JMSSender sender) throws JMSException {
        super(sender);
    } //- UrlSubmissionMsg
    
    /**
     * Initializes a UrlSubmissionMsg with a sender utility and type
     * specifier as well as all content fields.
     * @param sender
     * @param jobID
     * @param url
     * @param description
     * @throws JMSException
     */
    public UrlSubmissionMsg(JMSSender sender, String jobID, String url,
            String description) throws JMSException {
        super(sender);
        setJobID(jobID);
        setUrl(url);
        setDescription(description);
    } //- UrlSubmissionMsg
    
    
    protected final static String jobIDLabel = "JID";
    
    public String getJobID() throws JMSException {
        return msg.getString(jobIDLabel);
    } //- getJobID
    
    public void setJobID(String id) throws JMSException {
        msg.setString(jobIDLabel, id);
    } //- setJobID
    
    
    protected final static String urlLabel = "URL";
    
    public String getUrl() throws JMSException {
        return msg.getString(urlLabel);
    } //- getUrl
    
    public void setUrl(String url) throws JMSException {
        msg.setString(urlLabel, url);
    } //- setUrl
    
    
    protected final static String descLabel = "DESC";
    
    public String getDescription() throws JMSException {
        String desc = msg.getString(descLabel);
        if (desc != null) {
            return desc;
        }
        return "";
    } //- getDescription
    
    public void setDescription(String desc) throws JMSException {
        if (desc == null) {
            desc = "";
        }
        msg.setString(descLabel, desc);
    } //- setDescription
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.messaging.messages.JMSMessage#validate()
     */
    public boolean validate() throws JMSException {
        return (msg.itemExists(jobIDLabel) &&
                msg.itemExists(urlLabel));
    } //- validate
    
}  //- class UrlSubmissionMsg
