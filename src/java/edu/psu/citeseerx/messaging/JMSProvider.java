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
import org.apache.activemq.*;

/**
 * Container for resources pointing to a single JMS provider.  Two connection
 * types are possible - one for queue channels and one for topic channels.
 * These are initialized lazily when they are required to service specific
 * channels for either of the protocols, through the build*Connection methods.
 * 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 *
 */
public class JMSProvider {

    private final String url;
    
    /**
     * Creates a pointer to a JMS provider located at a specific URL.  No
     * connections are established at this point.
     * @param url
     */
    public JMSProvider(String url) {
        this.url = url;
    } //- JMSProvider
    
    private String queueID;
    private String topicID;
    
    private QueueConnection queueConnection;
    
    /**
     * Builds a connection for a Point-to-Point message channel, setting
     * the exceptionListener and queueID if these attributes are present.
     * @throws JMSException
     */
    private void buildQueueConnection() throws JMSException {
        ActiveMQConnectionFactory connectionFactory =
            new ActiveMQConnectionFactory(url);
        queueConnection = connectionFactory.createQueueConnection();
        if (exceptionListener != null) {
            queueConnection.setExceptionListener(exceptionListener);
        }
        if (queueID != null) {
            queueConnection.setClientID(queueID);
        }

    }  //- buildQueueConnection
    
    
    private TopicConnection topicConnection;
    
    /**
     * Builds a connection for a Publish-Subscribe message channel, setting
     * the exceptionListener and queueID if these attributes are present.
     * @throws JMSException
     */
    private void buildTopicConnection() throws JMSException {
        ActiveMQConnectionFactory connectionFactory =
            new ActiveMQConnectionFactory(url);
        topicConnection = connectionFactory.createTopicConnection();
        if (exceptionListener != null) {
            topicConnection.setExceptionListener(exceptionListener);
        }
        if (topicID != null) {
            topicConnection.setClientID(topicID);
        }
        
    }  //- buildTopicConnection
    
    
    private ExceptionListener exceptionListener;
    
    /**
     * Sets an object that will listen for and handle any exceptions that
     * occur on either of the message channels to this provider.
     * @param listener
     * @throws JMSException
     */
    public void setExceptionListener(ExceptionListener listener)
            throws JMSException {
        
        this.exceptionListener = listener;
        if (queueConnection != null) {
            queueConnection.setExceptionListener(exceptionListener);
        }
        if (topicConnection != null) {
            topicConnection.setExceptionListener(exceptionListener);
        }
        
    }  //- setExceptionListener
    

    /**
     * Creates a new Point-to-Point session with the specified message
     * acknowledgement mode, creating a new Point-to-Point connection with
     * the provider if necessary.
     * @param acknowledgeMode
     * @return the Point-to-point session that was created
     * @throws JMSException
     */
    public QueueSession createQueueSession(int acknowledgeMode)
            throws JMSException {
        if (queueConnection == null) {
            buildQueueConnection();
        }
        return queueConnection.createQueueSession(false, acknowledgeMode);
        
    }  //- createQueueSession
    
    
    /**
     * Creates a new Publish-Subscribe session with the specified message
     * acknowledgement mode, creating a new Publish-Subscribe connection with
     * the provider if necessary.
     * @param acknowledgeMode
     * @return the Publish-Subscribe session that was created
     * @throws JMSException
     */
    public TopicSession createTopicSession(int acknowledgeMode)
            throws JMSException {
        if (topicConnection == null) {
            buildTopicConnection();
        }
        return topicConnection.createTopicSession(false, acknowledgeMode);
        
    }  //- createTopicSession
    
    
    /**
     * Sets the client ID that will be broadcast to the JMS provider when
     * new connections are made.  This is necessary if durable publish-subscribe
     * sessions will be used, and is recommended in all cases.
     * @param id
     * @throws JMSException
     */
    public void setClientID(String id) throws JMSException {
        this.queueID = id+"-queue";
        this.topicID = id+"-topic";
        if (queueConnection != null) {
            queueConnection.setClientID(queueID);
        }
        if (topicConnection != null) {
            topicConnection.setClientID(topicID);
        }
        
    }  //- setClientID
    
    
    /**
     * Gracefully shuts down underlying connections.
     */
    public void close() {
        if (queueConnection != null) {
            try {
                queueConnection.close();
            } catch (JMSException e) { /* ignore */ }
        }
        if (topicConnection != null) {
            try {
                topicConnection.close();
            } catch (JMSException e) { /* ignore */ }
        }
        
    }  //- close

    
    /**
     * Starts the connections, if any have been specified.  This is necessary
     * before any use of the message channels.
     * @throws JMSException
     */
    public void start() throws JMSException {
        if (queueConnection != null) {
            queueConnection.start();
        }
        if (topicConnection != null) {
            topicConnection.start();
        }
        
    }  //- start

    
    /**
     * Pauses the underlying message channels, such that no messages can be
     * sent or received.  A subsequent call to start() will make the
     * channels operational again.
     * @throws JMSException
     */
    public void stop() throws JMSException {
        if (queueConnection != null) {
            queueConnection.stop();
        }
        if (topicConnection != null) {
            topicConnection.stop();
        }
        
    }  //- stop

}  //- class JMSProvider
