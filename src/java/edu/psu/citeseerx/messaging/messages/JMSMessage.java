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
 * Wrapper class for JMS MapMessages.  Messages should be wrapped within
 * a subclass of JMSMessage in order to formalize access patterns to
 * message contents, rather than setting and labeling message fields in
 * an ad-hoc manner, which would be a headache to debug if label mismatches
 * started appearing.
 * 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 *
 */
public abstract class JMSMessage {

    protected MapMessage msg;
    
    /**
     * Initializes a JMSMessage with a raw MapMessage.
     * @param msg
     * @throws JMSException
     */
    public JMSMessage(MapMessage msg) {
        this.msg = msg;
    } //- JMSMessage
    
    protected JMSSender sender;
    
    /**
     * Initializes a JMSMessage with a utility for sending the message.
     * The underlying MapMessage is generated from the sender.
     * @param sender
     * @throws JMSException
     */
    public JMSMessage(JMSSender sender) throws JMSException {
        this.sender = sender;
        msg = sender.createMapMessage();
    } //- JMSMessage
    
    /**
     * Returns the raw MapMessage that this class encapsulates.
     * @return the raw MapMessage that this class encapsulates
     */
    public MapMessage getMessage() {
        return msg;
    } //- getMessage
    
    /**
     * Sends the message to a JMS provider.  To use this method, the
     * JMSMessage MUST be initialized with a JMSSender.
     * @throws JMSException
     */
    public void send() throws JMSException {
        if (!validate()) {
            throw new JMSException("Tried to send invalid message");
        }
        sender.postMessage(msg);
    } //- send
    
    /**
     * Subclasses should override to provide validation routines specific
     * to their message format.  This method is called every time a message
     * is sent and generally should be used to make sure all required fields
     * are set.
     * @return true if all data is valid accord the message type, false 
     * otherwise
     */
    public boolean validate() throws JMSException {
        return true;
    } //- validate
    
    /**
     * Acknowledges that the message has been successfully received and
     * processed (don't call until processing of the message is complete).
     * @throws JMSException
     */
    public void acknowledge() throws JMSException {
        msg.acknowledge();
    } //- acknowledge
    
}  //- class JMSMessage
