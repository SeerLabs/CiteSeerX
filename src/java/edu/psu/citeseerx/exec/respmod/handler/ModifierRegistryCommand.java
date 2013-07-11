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
package edu.psu.citeseerx.exec.respmod.handler;

import edu.psu.citeseerx.exec.com.InvalidRequest;
import edu.psu.citeseerx.exec.com.ServiceCommand;
import edu.psu.citeseerx.exec.respmod.Config.Configuration;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This class is the implementation of the service command, that handles the 
 * registration of the response modifiers. Once a request is sent by a 
 * modifier, this class is responsible for validating the request and 
 * generating the connection to the modifier.
 * 
 * @author Levent Bolelli
 *
 */
public class ModifierRegistryCommand implements ServiceCommand{
	
	private List<ModifierConnection> clientConnections;
	private ThreadPoolExecutor threadPool = null;
	
	public ModifierRegistryCommand newCommand() {
		return new ModifierRegistryCommand();
	}
	
	/**
	 * 
	 * @param connections The list of known ModifierConnections that is 
	 * maintained by the response modification handler.
	 */
	public void setClientConnectionsObject(
			         List<ModifierConnection> connections) {
		clientConnections = connections;
	}
	
	/**
	 *
	 * @param tp the threadpool that is maintained by the response modification
	 * handler. These threads are <u>different</u> than the threads in the
	 * client connection pools.
	 */
	public void setThreadPoolObject(ThreadPoolExecutor tp) {
		threadPool = tp;
	}
	
	/**
	 * 
	 * @param newProperty the ModifierProperty of the new request
	 * @return 
	 */
	private boolean isAlreadyRegistered(final ModifierProperty newProperty) {
		final InetAddress newClientHost = newProperty.getModifierHost();
		final int newClientPort = newProperty.getModifierPort();
		
		for(Iterator it = clientConnections.iterator(); it.hasNext(); ) {
			ModifierProperty property = (ModifierProperty)it.next();
			if(newClientHost.equals(property.getModifierHost()) &&
			   newClientPort == property.getModifierPort()){
				return true;
			}
		}
		return false;
	}
	
	// At this point, this method only registers the modifiers by accepting ModifierProperty
	// object. We might add new objects types for richer communication.
	public Object execute(Object obj){
		if(obj == null) {
			return new InvalidRequest(ModifierProperty.class);
		}
		
		ModifierProperty property;
		try {
            property = (ModifierProperty)obj;
            property.setRegisteredFlag(false);
            
            if(clientConnections == null){
            	property.setMessage("ResponseHandler is not Initialized");
            	return property;
            }
            
            if(isAlreadyRegistered(property)) {
            	final String message = property.getModifierHost().toString() + 
            	                       ":" +
            						   property.getModifierPort() + 
            						   " already registered!";
            	property.setMessage(message);
            	return property;
            }
       
            // Should be a new request. Create a new connection and increase the
            // threadpool core thread size.
            ModifierConnection newConnection = new ModifierConnection(
            		property.getModifierName(),
            		property.getModifierHost(),
            		property.getModifierPort());
            
            newConnection.createClientConnectionPool();
            final int newCorePoolSize =
            	     threadPool.getCorePoolSize() + 
            	     Configuration.minNumberOfThreadsPerModifierTask;
            
            if(newCorePoolSize < threadPool.getMaximumPoolSize()){
            	threadPool.setCorePoolSize(newCorePoolSize);
            }
            clientConnections.add(newConnection);
        } catch (ClassCastException e) {
            return new InvalidRequest(ModifierProperty.class); 
        }
        
        final String message = "Registered " + property.getModifierName() + 
                               " at " + property.getModifierHost() + 
                               ":"+property.getModifierPort();
        property.setRegisteredFlag(true);
        property.setMessage(message);
        return property;
	}
}
