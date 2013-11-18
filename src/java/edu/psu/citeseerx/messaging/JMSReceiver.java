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
 * Provides implementation for generic JMS message consumers, either by
 * the Point-to-Point (Queue) or Publish-Subscribe (Topic) protocols.
 * 
 * Once created, the JMSReceiver must be initialized by one of the
 * initialize* methods before being used.  Only one initialize* method
 * may be called, and subsequent calls will be ignored.
 * 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class JMSReceiver implements JMSResource {
    
    private String name;
    
    /**
     * Creates a new empty consumer resource.
     * @param name channel identifier.
     */
    public JMSReceiver(String name) {
        this.name = name;
    } //- JMSReceiver
    
    private Session session;
    private MessageConsumer consumer;
    
    /**
     * Initializes a point-to-point message channel for receiving messages.
     * @param provider
     * @param ackMode message acknowledgement mode as defined by the 
     * JMS Session API.
     */
    public void initializeQueue(JMSProvider provider, int ackMode)
            throws JMSException {
        if (session != null) {
            throw new JMSException("JMSReceiver already initialized");
        }
        QueueSession qs = provider.createQueueSession(ackMode);
        session = qs;
        Queue queue = qs.createQueue(name);
        consumer = qs.createReceiver(queue);

    }  //- initializeQueue
    
    
    /**
     * Initializes a non-durable publish-subscribe message channel for
     * receiving messages.
     * @param provider
     * @param ackMode message acknowledgement mode as defined by the 
     * JMS Session API.
     */
    public void initializeTopic(JMSProvider provider, int ackMode)
            throws JMSException {
        if (session != null) {
            throw new JMSException("JMSReceiver already initialized");
        }
        TopicSession ts = provider.createTopicSession(ackMode);
        session = ts;
        Topic topic = ts.createTopic(name);
        consumer = ts.createSubscriber(topic);
        
    }  //- initializeTopic
    
    
    /**
     * Initializes a durable publish-subscribe message channel.
     * @param provider
     * @param ackMode message acknowledgement mode as defined by the 
     * JMS Session API.
     * @param durableID ID for this durable subscription
     * @throws JMSException
     */
    public void initializeDurableTopic(JMSProvider provider, int ackMode,
            String durableID) throws JMSException {
        if (session != null) {
            throw new JMSException("JMSReceiver already initialized");
        }
        TopicSession ts = provider.createTopicSession(ackMode);
        session = ts;
        Topic topic = ts.createTopic(name);
        consumer = ts.createDurableSubscriber(topic, durableID);
        
    }  //- initializeDurableTopic
    
    
    /**
     * Returns the message consumer for this receiver.  This is useful if
     * a client would like to use the blocking receive() method of the
     * message consumer rather than registering an asynchronous handler
     * through the setMessageListener() method.
     * @return the message consumer for this receiver
     */
    public MessageConsumer getConsumer() {
        return consumer;
    } //- getConsumer
    
    
    /**
     * Sets the listener for this consumer resource.  This is necessary
     * to register implementation for handling incoming messages.
     * @param listener
     * @throws JMSException
     */
    public void setMessageListener(MessageListener listener)
            throws JMSException {
        consumer.setMessageListener(listener);
    } //- setMessageListener
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.messaging.JMSResource#close()
     */
    public void close() {
        try {
            consumer.close();
            session.close();
        } catch (JMSException e) {}
        
    }  //- close
    
}  //- class JMSReceiver
