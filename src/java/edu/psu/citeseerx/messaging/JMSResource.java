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

import javax.jms.JMSException;

/**
 * Interface to bound the implementation of JMS producers and consumers.
 * JMS Resources will be required to implement at least these methods, but
 * may require additional initialization methods.
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public interface JMSResource {
    
    /**
     * Create a default Point-to-Point messaging channel. 
     * @param provider
     * @param ackMode acknowledgement mode for messages, as defined by
     * the JMS Session API.
     * @throws JMSException
     */
    public void initializeQueue(JMSProvider provider, int ackMode)
        throws JMSException;
    
    /**
     * Create a default Publish-Subscribe message channel.
     * @param provider
     * @param ackMode acknowledgement mode for messages, as defined by
     * the JMS Session API.
     * @throws JMSException
     */
    public void initializeTopic(JMSProvider provider, int ackMode)
        throws JMSException;
    
    /**
     * Gracefully shutdown resources.
     */
    public void close();
    
}  //- interface JMSResource
