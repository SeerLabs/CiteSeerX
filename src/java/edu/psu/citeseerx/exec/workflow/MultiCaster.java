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
package edu.psu.citeseerx.exec.workflow;

import edu.psu.citeseerx.utility.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class MultiCaster extends Thread implements RegistryListener {

    private final AccessKey key = new AccessKey();
    private InetAddress multicastGroup;
    private MulticastSocket mcSocket;
    private int port;
    private long multicastInterval;
    private ServiceRegistry registry;
    private static final int TTL = 1;  // Restrict multicast to local network.
    private final int warnBufferSize;
    
    /**
     * Reads in configuration, sets up the multicast socket and initial
     * datagram, and registers as a listener of the specified ServiceRegistry.
     * @param cm a configuration manager with access to this class's
     * configuration data
     * @param registry a registry of services, used to construct a reference
     * of service endpoints in the datagram
     * @throws IOException if there is a problem creating the multicast
     * server
     * @throws Exception if configuration data is not available.
     */
    public MultiCaster(final ConfigurationManager cm,
            final ServiceRegistry registry) throws IOException,
            Exception {
        
        this.setName("MultiCaster");
        this.setDaemon(true);  // This thread won't keep the JVM alive.
        
        port = cm.getInt("MultiCaster.port", key);
        multicastInterval = cm.getLong("MultiCaster.multicastInterval", key);
        String group = cm.getString("MultiCaster.multicastGroup", key);
        warnBufferSize = cm.getInt("MultiCaster.maxBufferSize", key);
    
        this.registry = registry;
        this.registry.addListener(this);
        
        multicastGroup = InetAddress.getByName(group);  // better way?
        mcSocket = new MulticastSocket(port);
        mcSocket.setTimeToLive(TTL);
        rebuildPacket();
        
    }  //- MultiCaster
    

    private DatagramPacket packet;
    
    /**
     * Rebuilds the datagram packet based on the current registry environment.
     * If the datagram buffer exceeds warnBufferSize a warning message
     * will be generated.
     */
    private void rebuildPacket() {

        int registryPort = registry.getListenPort();
        Iterator<InetSocketAddress> endpoints = 
            registry.getRegisteredEndpoints();

        String msg = Integer.toString(registryPort);
        while(endpoints.hasNext()) {
            InetSocketAddress endpoint = endpoints.next();
            msg += " " + endpoint.getAddress().getHostAddress() + ":";
            msg += endpoint.getPort();
        }
        
        byte[] buffer = msg.getBytes();
        
        if (buffer.length > warnBufferSize) {
            System.err.println("WARNING (MultiCaster): " +
                    "datagram buffer exceeds " +
                    warnBufferSize + " bytes.");
        }
        
        DatagramPacket newPacket = new DatagramPacket(buffer, buffer.length,
                multicastGroup, port);
        
        if (packet == null) {
            packet = newPacket;
        } else {
            synchronized(packet) {
                packet = newPacket;
            }
        }
        
    }  //- rebuildPacket
    
    
    private boolean shutdown = false;
    
    /**
     * Simply sends the datagram out to the configured multicast group
     * with a periodicity specified by multicastInterval.
     */
    public void run() {
        while(!shutdown) {
            try {
                synchronized(packet) {
                    mcSocket.send(packet);
                }
                sleep(multicastInterval);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                /* ignore */
            }
        }
        
    }  //- run
    
    
    /**
     * Causes the MultiCaster to stop sending messages, terminating the
     * broadcast thread.  The MultiCaster should be discarded after shutdown()
     * is called.
     */
    public void shutdown() {
        registry.removeListener(this);
        shutdown = true;
    }
    
    
    /**
     * Rebuilds the datagram packet to reflect changes in registration.
     */
    public void registrationChanged() {
        rebuildPacket();
    }
    
}  //- class MultiCaster
