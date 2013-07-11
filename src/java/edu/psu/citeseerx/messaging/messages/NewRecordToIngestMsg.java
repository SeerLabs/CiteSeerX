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
 * Wrapper for messages from the crawler to the ingestion system indicating
 * that a new resource is ready for ingestion.
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class NewRecordToIngestMsg extends JMSMessage {

    /**
     * Initializes a NewRecordToIngestMsg with a raw MapMessage.
     * @param msg
     */
    public NewRecordToIngestMsg(MapMessage msg) {
        super(msg);
    } //- NewRecordToIngestMsg
     
    /**
     * Initializes a NewRecordToIngestMsg with a utility for sending the message.
     * The underlying MapMessage is generated from the sender.
     * @param sender
     * @throws JMSException
     */
    public NewRecordToIngestMsg(JMSSender sender) throws JMSException {
        super(sender);
    } //- NewRecordToIngestMsg
    
    /**
     * Initializes a NewRecordToIngestMsg with a sender utility and type
     * specifier as well as all content fields.
     * @param sender
     * @param jobID
     * @param repositoryID
     * @param filePath
     * @param metaPath
     * @param type
     * @throws JMSException
     */
    public NewRecordToIngestMsg(JMSSender sender, String jobID,
            String repositoryID, String filePath,
            String metaPath, String type) throws JMSException {
        super(sender);
        setJobID(jobID);
        setRepositoryID(repositoryID);
        setFilePath(filePath);
        setMetaPath(metaPath);
        setResourceType(type);
        
    }  //- NewRecordToIngestMsg
    
    
    protected final static String jobIDLabel = "JID";
    
    public String getJobID() throws JMSException {
        return msg.getString(jobIDLabel);
    } //- getJobID
    
    public void setJobID(String id) throws JMSException {
        msg.setString(jobIDLabel, id);
    } //- setJobID
    

    protected final static String reposIDLabel = "REPID";

    public String getRepositoryID() throws JMSException {
        return msg.getString(reposIDLabel);
    } //- getRepositoryID

    public void setRepositoryID(String id) throws JMSException {
        msg.setString(reposIDLabel, id);
    } //- setRepositoryID
    
    
    protected final static String filePathLabel = "FILEPATH";
    
    public String getFilePath() throws JMSException {
        return msg.getString(filePathLabel);
    } //- getFilePath
    
    public void setFilePath(String path) throws JMSException {
        msg.setString(filePathLabel, path);
    } //- setFilePath
    
    
    protected final static String metaPathLabel = "METAPATH";
    
    public String getMetaPath() throws JMSException {
        return msg.getString(metaPathLabel);
    } //- getMetaPath
    
    public void setMetaPath(String path) throws JMSException {
        msg.setString(metaPathLabel, path);
    } //- setMetaPath
    
    
    protected final static String typeLabel = "RESOURCETYPE";
    
    public String getResourceType() throws JMSException {
        return msg.getString(typeLabel);
    } //- getResourceType
    
    public void setResourceType(String type) throws JMSException {
        msg.setString(typeLabel, type);
    } //- setResourceType
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.messaging.messages.JMSMessage#validate()
     */
    public boolean validate() throws JMSException {
        return (msg.itemExists(jobIDLabel) &&
                msg.itemExists(filePathLabel) &&
                msg.itemExists(reposIDLabel) &&
                msg.itemExists(metaPathLabel) &&
                msg.itemExists(typeLabel));
    } //- validate
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String str = "NewRecordToIngestMsg: unitialized content";
        try {
            if (validate()) {
                str = "NewRecordToIngestMsg:\n"
                    +"JobID: "+getJobID()+"\n"
                    +"Repository: "+getRepositoryID()+"\n"
                    +"FilePath: "+getFilePath()+"\n"
                    +"MetaPath: "+getMetaPath()+"\n"
                    +"ResourceType: "+getResourceType()+"\n";
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return str;
                
    }  //- toString
    
}  //- class NewRecordToIngestMsg
