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
package edu.psu.citeseerx.messaging;

import javax.jms.*;

/**
 * Provides implementation for generic JMS message producers, either by
 * the Point-to-Point (Queue) or Publish-Subscribe (Topic) protocols.
 * 
 * Once created, the JMSProducer must be initialized by one of the
 * initialize* methods before being used.  Only one initialize* method
 * may be called, and subsequent calls will be ignored.
 * 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 *
 */
public class JMSSender implements JMSResource {
    
    private String name;
    
    /**
     * Creates a new empty producer resource.
     * @param name channel identifier.
     */
    public JMSSender(String name) {
        this.name = name;
    } //- JMSSender
    
    private Session session;
    private MessageProducer producer;
    
    /**
     * Initializes a point-to-point message channel for sending messages.
     * @param provider
     * @param ackMode message acknowledgement mode as defined by the 
     * JMS Session API.
     */
    public void initializeQueue(JMSProvider provider, int ackMode)
            throws JMSException {
        if (session != null) {
            throw new JMSException("JMSSender already initialized");
        }
        QueueSession qs = provider.createQueueSession(ackMode);
        session = qs;
        Queue queue = qs.createQueue(name);
        producer = qs.createSender(queue);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        
    }  //- initializeQueue
    
    
    /**
     * Initializes a non-durable publish-subscribe message channel for
     * sending messages.
     * @param provider
     * @param ackMode message acknowledgement mode as defined by the 
     * JMS Session API.
     */
    public void initializeTopic(JMSProvider provider, int ackMode)
            throws JMSException {
        if (session != null) {
            throw new JMSException("JMSSender already initialized");
        }
        TopicSession ts = provider.createTopicSession(ackMode);
        session = ts;
        Topic topic = ts.createTopic(name);
        producer = ts.createPublisher(topic);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        
    }  //- initializeTopic
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.messaging.JMSResource#close()
     */
    public void close() {
        try {
            producer.close();
            session.close();
        } catch (JMSException e) {}
        
    }  //- close
    
    
    /**
     * Generates a new MapMessage from the underlying JMS session.
     * @return A MapMessage
     * @throws JMSException
     */
    public MapMessage createMapMessage() throws JMSException {
        return session.createMapMessage();
    } //- createMapMessage
    
    
    /**
     * Sends a JMS message through the underlying producer implementation.
     * @param msg
     * @throws JMSException
     */
    public void postMessage(Message msg) throws JMSException {
        producer.send(msg);
    } //- postMessage
    
}  //- class JMSSender
