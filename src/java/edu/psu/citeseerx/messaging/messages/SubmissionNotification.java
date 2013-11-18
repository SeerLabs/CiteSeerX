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

import edu.psu.citeseerx.messaging.*;
import javax.jms.*;

/**
 * Supertype for submission notification messages.  These messages are
 * used for updating MyCiteSeer regarding the status of resources submitted
 * for crawling/ingestion.
 * 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 *
 */
public abstract class SubmissionNotification extends JMSMessage {

    public static final int JOBSTARTED = 10;
    public static final int JOBCOMPLETION = 11;
    public static final int URLCRAWLSTATUS = 12;
    public static final int INGESTSTATUS = 13;
    
    public final static String typeLabel = "MTYPE";

    /**
     * Initializes a SubmissionNotification with a raw MapMessage and a
     * type specifier, which must be derived from one of the int fields
     * indicating message type.  If this contructor is used to wrap a
     * premade message (e.g. wrap a MapMessage on the receiving end of a
     * JMS channel), the specified type is checked against the message type
     * and a JMSException will be thrown if there is a mismatch.
     * @param type
     * @param msg
     * @throws JMSException
     */
    public SubmissionNotification(int type, MapMessage msg)
            throws JMSException {
        super(msg);
        if (msg.itemExists(typeLabel)) {
            int t = msg.getInt(typeLabel);
            if (t != type) {
                throw new JMSException("Incompatible message type");
            }
        } else {
            this.msg.setInt(typeLabel, type);
        }
        
    }  //- SubmissionNotification
    
    
    /**
     * Initializes a SubmissionNotification with a sender utility and a
     * type specifier, which must be derived from one of the int fields
     * indicating message type.  The underlying message is created from the
     * sender.
     * @param type
     * @param sender
     * @throws JMSException
     */
    public SubmissionNotification(int type, JMSSender sender)
            throws JMSException {
        super(sender);
        this.msg.setInt(typeLabel, type);
    }
    
    
    /**
     * Initializes a SubmissionNotification with a sender utility and type
     * specifier as well as all content fields.
     * @param type
     * @param sender
     * @param jobID
     * @param url
     * @param status
     * @throws JMSException
     */
    public SubmissionNotification(int type, JMSSender sender, String jobID,
            String url, int status) throws JMSException {
        super(sender);
        this.msg.setInt(typeLabel, type);
        setJobID(jobID);
        setUrl(url);
        setStatus(status);
        
    }  //- SubmissionNotification
    
    /**
     * Returns the type identifier for this message, which will be derived
     * from one of the type fields exposed in this class.
     * @return the type identifier for this message
     */
    public int getType() throws JMSException {
        return msg.getInt(typeLabel);
    } //- getType
    
    
    protected final static String jobIDLabel = "JID";
    
    public String getJobID() throws JMSException {
        return msg.getString(jobIDLabel);
    } //- getJobID
    
    public void setJobID(String jobID) throws JMSException {
        msg.setString(jobIDLabel, jobID);
    } //- setJobID
    

    protected final static String urlLabel = "URL";
    
    public String getUrl() throws JMSException {
        return msg.getString(urlLabel);
    } //- getUrl
    
    public void setUrl(String url) throws JMSException {
        msg.setString(urlLabel, url);
    } //- setUrl
    
    
    protected final static String statusLabel = "STATUS";

    public int getStatus() throws JMSException {
        return msg.getInt(statusLabel);
    } //- getStatus
    
    public void setStatus(int status) throws JMSException {
        msg.setInt(statusLabel, status);
    } //- setStatus
    
    /**
     * True if the message in this SubmissionNotification object is valid
     */
    public boolean validate() throws JMSException {
        return (msg.itemExists(typeLabel) &&
                msg.itemExists(jobIDLabel) &&
                msg.itemExists(urlLabel) &&
                msg.itemExists(statusLabel));
    } //- validate
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String str = "SubmissionNotification: unitialized content";
        try {
            if (validate()) {
                str = "SubmissionNotification:\n"
                    +"Type: "+getType()+"\n"
                    +"JobID: "+getJobID()+"\n"
                    +"URL: "+getUrl()+"\n"
                    +"Status: "+getStatus()+"\n";
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return str;
    } //- toString
    
    
}  //- class SubmissionNotification
