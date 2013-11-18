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

import java.util.Hashtable;
import java.util.List;
import java.util.Enumeration;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.psu.citeseerx.utility.ConfigurationKey;
import edu.psu.citeseerx.utility.ConfigurationManager;

/**
 * Implements a configuration-driven JMS environment for sending or
 * receiving messages.  Providers and channel resources are kept separate
 * in order to manage resources independently by name.  Consumer and provider
 * channels are also kept in separate namespaces in order to prevent
 * confusion over how each channel can be used.
 * <br><br>
 * Configuration is based on the ConfigurationManager from the utility
 * package.  The following is an example:
 * <br><br>
 * 
 * <pre>
 * &lt;?xml version="1.0"?>
 * &lt;serviceConfiguration>
 * &lt;edu>&lt;psu>&lt;citeseerx>
 * 
 * &lt;utility>
 *   &lt;ConfigurationManager>
 *     &lt;autoSave>false&lt;/autoSave>
 *   &lt;/ConfigurationManager>
 * &lt;/utility>
 * 
 * &lt;messaging>
 * 
 *   &lt;jmsProvider>
 *     &lt;url>tcp://localhost:61616&lt;/url>
 *     &lt;clientID>consumer1&lt;/clientID>
 * 
 *     &lt;queue>
 *       &lt;name>JMSQUEUETEST&lt;/name>
 *       &lt;role>producer&lt;/role>
 *       &lt;acknowledgeMode>CLIENT_ACKNOWLEDGE&lt;/acknowledgeMode>
 *     &lt;/queue>
 * 
 *     &lt;topic>
 *       &lt;name>JMSTOPICTEST&lt;/name>
 *       &lt;role>consumer&lt;/role>
 *       &lt;durable>true&lt;/durable>
 *       &lt;durableID>consumertest1&lt;/durableID>
 *       &lt;acknowledgeMode>CLIENT_ACKNOWLEDGE&lt;/acknowledgeMode>
 *     &lt;/topic>
 * 
 *   &lt;/jmsProvider>
 * 
 * &lt;/messaging>
 * 
 * &lt;/citeseerx>&lt;/psu>&lt;/edu>
 * &lt;/serviceConfiguration>
 * </pre>
 * <br><br>
 * In this example, a single provider is defined at url tcp://localhost:61616.
 * Clients will identify with the base ID consumer1, and will logically
 * identify as consumer1-queue with the queue connection and consumer1-topic
 * with the topic connection.
 * <br><br>
 * Two channels are defined, a Point-to-Point channel by which the client
 * will send messages to a single consumer, and a Publish-Subscribe channel
 * by which the client will consume messages published to a topic with the
 * label JMSTOPICTEST.  This Publish-Subscribe channel is configured to be
 * durable, such that clients will receive messages over this channel that
 * were sent when the client was not operational, as long as messages do
 * not expire before they are sent.  To maintain this durability, a
 * durableID of consumertest1 is provided to identify this durable client
 * to the channel.  Durability is optional, and so are these configuration
 * directives.
 * <br><br>
 * The client code for this message service will look something like this:
 * <br><br>
 * <pre>
 * // Set up the service.  This will read all configuration and build
 * // the appropriate connections and channels.
 * MsgService msgService = new MsgService();
 * 
 * // Set up code to receive messages over the consumer channel.
 * msgService.setMessageListener("JMSTOPICTEST", aMessageListener);
 * 
 * // Provide an onMessage(Message msg) handler method for aMessageListener.
 * public synchronized void onMessage(Message msg) {
 *     if (msg instanceof MapMessage) {
 *         MapMessage m = (MapMessage)msg;
 *         try {
 *             ... do something with message ...
 *             msg.acknowledge();  // ALWAYS ACKNOWLEDGE *AFTER* USE.
 *         } catch (JMSException e) {
 *             e.printStackTrace();
 *         }
 *     }
 * }
 *
 * // Start the service (should be called after all other setup is completed,
 * // particularly MessageListeners should be set first on consumer channels.
 * msgService.start();
 * 
 * // Send a message over the producer channel.
 * JMSSender sender = msgService.getJMSSender("JMSQUEUETEST");
 * MapMessage msg = sender.createMapMessage();
 * ... Populate msg with data (see JMS API docs) ...
 * sender.send(msg);
 * 
 * </pre>
 * 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 * 
 */
public class MsgService implements ExceptionListener {

    private final ConfigurationKey accessKey = new AccessKey();
    private final ConfigurationManager cm = new ConfigurationManager();


    /**
     * Creates a new service set from configuration, reading in individual
     * JMS provider descriptions and configuring each individually.
     * @throws Exception
     */
    public MsgService() throws Exception {
        List providers = cm.getList("jmsProvider.url", accessKey);
        for (int i=0; i<providers.size(); i++) {
            processProvider(cm.configurationAt("jmsProvider("+i+")",
                    accessKey));
        }
        
    }  //- MsgService
    

    protected final Hashtable<String,JMSProvider> jmsProviders = 
        new Hashtable<String,JMSProvider>();
    protected final Hashtable<String,JMSReceiver> jmsReceivers =
        new Hashtable<String,JMSReceiver>();
    protected final Hashtable<String,JMSSender> jmsSenders =
        new Hashtable<String,JMSSender>();

    public static final int QUEUE = 0;
    public static final int TOPIC = 1;

    /**
     * Configures a JMS provider based on configuration, passing execution
     * to other handlers for specific channel descriptors.
     * @param config
     * @throws Exception
     */
    private void processProvider(HierarchicalConfiguration config)
            throws Exception {
        
        String url = config.getString("url");
        String clientID = config.getString("clientID");
        JMSProvider provider = new JMSProvider(url);
        provider.setClientID(clientID);
        jmsProviders.put(url, provider);
        
        System.out.println("processing provider: "+url);
        
        List queues = config.getList("queue.name");
        for (int i=0; i<queues.size(); i++) {
            processChannel(provider, config.configurationAt("queue("+i+")"),
                    QUEUE);
        }
        List topics = config.getList("topic.name");
        for (int i=0; i<topics.size(); i++) {
            processChannel(provider, config.configurationAt("topic("+i+")"),
                    TOPIC);
        }
        
    }  //- processProvider
    
    
    /**
     * Configures an individual channel to be handled by a specified provider.
     * Channels are created for consumption or production based on their
     * descriptions, and execution is passed to other handlers to build
     * specific channel implementations.
     * @param provider
     * @param config
     * @param type
     * @throws Exception
     */
    protected void processChannel(JMSProvider provider,
            HierarchicalConfiguration config, int type) throws Exception {
        String name = config.getString("name");
        String role = config.getString("role");
        String modeStr = config.getString("acknowledgeMode");
        int mode = getAckMode(modeStr);
        
        if (role.equalsIgnoreCase("consumer")) {
            boolean durable = false;
            if (type == TOPIC) {
                try {
                    durable = config.getBoolean("durable");
                } catch (Exception e) { /* ignore */ } 
            }
            if (durable) {
                String durableID = config.getString("durableID");
                createDurableSubscriber(provider, name, mode, durableID);
            } else {
                createConsumer(provider, name, type, mode);
            }
        } else
        if (role.equalsIgnoreCase("producer")) {
            createProducer(provider, name, type, mode);
        }
        
    }  //- processChannel
    
    
    /**
     * Creates a durable publish-subscribe consumer for a channel resource.
     * @param provider
     * @param name channel name
     * @param mode message acknowledgement mode
     * @param durableID durable subscription id for this channel
     * @throws JMSException
     */
    protected void createDurableSubscriber(JMSProvider provider,
            String name, int mode, String durableID) throws JMSException {
        System.out.println("Configuring durable consumer for topic: " + name);
        JMSReceiver receiver = new JMSReceiver(name);
        receiver.initializeDurableTopic(provider, mode, durableID);
        jmsReceivers.put(name, receiver);
        
    }  //- createDurableSubscriber
    
    
    /**
     * Creates a consumer for a given type of channel, either queue or topic.
     * @param provider
     * @param name channel name
     * @param type consumer or producer
     * @param mode message acknowledgement mode
     * @throws JMSException
     */
    protected void createConsumer(JMSProvider provider, String name,
            int type, int mode) throws JMSException {
                
        JMSReceiver receiver = new JMSReceiver(name);
        switch(type) {
        case QUEUE:
            System.out.println("Configuring consumer for queue: " + name);
            receiver.initializeQueue(provider, mode);
            break;
        case TOPIC:
            System.out.println("Configuring consumer for topic: " + name);
            receiver.initializeTopic(provider, mode);
            break;
        default:
            throw new JMSException("Invalid channel type specified");
        }
        jmsReceivers.put(name, receiver);
        
    }  //- createConsumer
    
    
    /**
     * Creates a producer for a given type of channel, either queue or topic.
     * @param provider
     * @param name channel name
     * @param type consumer or producer
     * @param mode message acknowledgement mode
     * @throws JMSException
     */
    protected void createProducer(JMSProvider provider, String name,
            int type, int mode) throws JMSException {
                
        JMSSender sender = new JMSSender(name);
        switch(type) {
        case QUEUE:
            System.out.println("Configuring producer for queue: " + name);
            sender.initializeQueue(provider, mode);
            break;
        case TOPIC:
            System.out.println("Configuring producer for topic: " + name);
            sender.initializeTopic(provider, mode);
            break;
        default:
            throw new JMSException("Invalid channel type specified");
        }
        jmsSenders.put(name, sender);
        
    }  //- createProducer
        
    
    /**
     * From ExceptionListener interface, providers handler implementation
     * for trapping JMS exceptions on any channel.
     */
    public synchronized void onException(JMSException e) {
        e.printStackTrace();
    } //- onException

    
    /**
     * Gracefully shuts down all underlying JMS resources.
     */
    public void close() {
        for (Enumeration<JMSReceiver> e = jmsReceivers.elements();
                e.hasMoreElements(); ) {
            e.nextElement().close();
        }
        for (Enumeration<JMSSender> e = jmsSenders.elements();
                e.hasMoreElements(); ) {
            e.nextElement().close();
        }
        for (Enumeration<JMSProvider> e = jmsProviders.elements(); 
                e.hasMoreElements(); ) {
            e.nextElement().close();
        }
        
    }  //- close

    
    /**
     * Starts all underlying connections.  This is required before use
     * of any message channel.
     * @throws JMSException
     */
    public void start() throws JMSException {
        for (Enumeration<JMSProvider> e = jmsProviders.elements(); 
                e.hasMoreElements(); ) {
            e.nextElement().start();
        }
        
    }  //- start

    
    /**
     * Stops all underlying connections from sending or receiving messages.
     * Channels may be resumed by calling the start() method.
     * @throws JMSException
     */
    public void stop() throws JMSException {
        for (Enumeration<JMSProvider> e = jmsProviders.elements(); 
                e.hasMoreElements(); ) {
            e.nextElement().stop();
        }
        
    }  //- stop

    
    /**
     * Utility for translating String representations of acknowledgement
     * modes into their int encoding.  Default is CLIENT_ACKNOWLEDGE, so
     * any string that is not understood will result in a CLIENT_ACKNOWLEDGE
     * mode.
     * @param modeStr
     * @return int representation of string acknowledgement
     */
    protected int getAckMode(String modeStr) {
        int mode = Session.CLIENT_ACKNOWLEDGE;
        if (modeStr.equalsIgnoreCase("AUTO_ACKNOWLEDGE")) {
            mode = Session.AUTO_ACKNOWLEDGE;
        }
        if (modeStr.equalsIgnoreCase("DUPS_OK_ACKNOWLEDGE")) {
            mode = Session.DUPS_OK_ACKNOWLEDGE;
        }
        return mode;
        
    }  //- getMode

    /*=========================================================
     * PRODUCER API
     /=======================================================*/
    
    /**
     * Creates a new MapMessage based on the session identified
     * by channelName.
     * @param channelName channel id
     */
    public MapMessage createMapMessage(String channelName)
            throws JMSException {
        return jmsSenders.get(channelName).createMapMessage();
    } //- createMapMessage


    /**
     * Posts a JMS message to the producer channel identified by channelID.
     * @param channelName channel id
     * @param msg
     * @throws JMSException
     */
    public void postMessage(String channelName, Message msg)
            throws JMSException {
        jmsSenders.get(channelName).postMessage(msg);
    } //- postMessage


    /**
     * Returns a sender for the specified producer channel.  This is
     * provided as a convenience for objects that need to produce and
     * send messages repeatedly over a given channel.
     * @param channelName channel id
     * @return a sender for the specified producer channel
     */
    public JMSSender getJMSSender(String channelName) {
        return jmsSenders.get(channelName);
    } //- getJMSSender
    
    /*=========================================================
     * CONSUMER API
     /=======================================================*/

    /**
     * Returns a MessageConsumer from the specified receiver channel,
     * which may be useful for the MessageConsumer's blocking receive()
     * method instead of setting an asynchronous handler through the
     * setMessageListener() method.
     */
    public MessageConsumer getMessageConsumer(String channelName) {
        return jmsReceivers.get(channelName).getConsumer();
    } //- getMessageConsumer
    
    /**
     * Sets a listener for messages that are received on the specified
     * channel.  This is required to provide implementation for handling
     * incoming messages.
     * @param channelName channel id
     * @param listener message handler implementation
     */
    public void setMessageListener(String channelName,
            MessageListener listener) throws JMSException {
        jmsReceivers.get(channelName).setMessageListener(listener);
    } //- setMessageListener
    
    /* ======================================================*/

}  //- class MsgService


class AccessKey extends ConfigurationKey {}
