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

import edu.psu.citeseerx.exec.com.ConnectionPool;
import edu.psu.citeseerx.exec.com.ConnectionPoolConfiguration;
import edu.psu.citeseerx.exec.com.lb.LoadBalancer;
import edu.psu.citeseerx.exec.com.lb.RoundRobinBalancer;
import edu.psu.citeseerx.exec.protocol.TaskStub;
import edu.psu.citeseerx.utility.ConfigurationManager;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ServiceRegistry {

    protected final AccessKey key = new AccessKey();
    protected final RegistryServer server;
    
    public ServiceRegistry(ConfigurationManager cm) throws Exception {
        server = new RegistryServer(cm, this);
        server.start();
    }
    
    
    protected final List<InetSocketAddress> registeredEndpoints = 
        new ArrayList<InetSocketAddress>();
    
    public Iterator<InetSocketAddress> getRegisteredEndpoints() {
        return registeredEndpoints.iterator();
    }

    protected final Vector<ConnectionPool> connectionPools =
        new Vector<ConnectionPool>();
    protected final Hashtable<String,TaskStub> registeredServices =
        new Hashtable<String,TaskStub>();
    protected final Hashtable<String,LoadBalancer> connectionResources =
        new Hashtable<String,LoadBalancer>();
    
    
    public synchronized void registerTask(TaskStub stub)
            throws RegistrationException {
        String name = stub.getName();
        LoadBalancer balancer = null;
        if (connectionResources.containsKey(name)) {
            balancer = connectionResources.get(name);
        } else {
            balancer = new RoundRobinBalancer();
            connectionResources.put(name, balancer);
        }
        stub.setBalancer(balancer);
    }
    
    public synchronized void registerService(ConnectionPoolConfiguration config,
            List<TaskStub> taskList) throws RegistrationException {
        ConnectionPool pool = ConnectionPool.createFromConfiguration(config);
        for (Iterator<TaskStub> it = taskList.iterator(); it.hasNext(); ) {
            TaskStub stub = it.next();
            String name = stub.getName();
            
            System.out.println("registering task: " + name);
            
            if (registeredServices.containsKey(name)) {
                if (!registeredServices.get(name).equals(stub))
                    throw new RegistrationException("TASK CONFLICT: " +name);
            } else {
                System.out.println("not found in registeredServices");
                registeredServices.put(name, stub);
            }
            if (connectionResources.containsKey(name)) {
                System.out.println("found in connectionResources");
                stub.setBalancer(connectionResources.get(name));
                connectionResources.get(name).addPool(pool);
            } else {
                LoadBalancer balancer = new RoundRobinBalancer();
                balancer.addPool(pool);
                stub.setBalancer(balancer);
                connectionResources.put(name, balancer);
            }
        }
        if (!connectionPools.contains(pool)) {
            connectionPools.add(pool);
        }
        registeredEndpoints.add(new InetSocketAddress(config.getRemoteHost(),
                config.getPort()));
        notifyChanged();
    }
    
    class RegistrationException extends RuntimeException {
        /**
         * 
         */
        private static final long serialVersionUID = -7861741044747318801L;

        public RegistrationException (String msg) {
            super(msg);
        }
        
    }  //- class RegistrationException
    
    
    public int getListenPort() {
        return server.getListenPort();
    }
    

    private final ArrayList<RegistryListener> listeners =
        new ArrayList<RegistryListener>();
    
    
    private void notifyChanged() {
        for (Iterator<RegistryListener> it = listeners.iterator();
                it.hasNext(); ) {
            it.next().registrationChanged();
        }
    }
    public void addListener(RegistryListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(RegistryListener listener) {
        listeners.remove(listener);
    }
    
}


interface RegistryListener {
    public void registrationChanged();
}
