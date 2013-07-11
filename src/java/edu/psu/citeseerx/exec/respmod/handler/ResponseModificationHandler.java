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

/**
 * 
 * @author Levent Bolelli
 *
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.psu.citeseerx.exec.com.ByteStreamConnectionPool;
import edu.psu.citeseerx.exec.com.ObjectServer;
import edu.psu.citeseerx.exec.respmod.Config.Configuration;
import edu.psu.citeseerx.utility.ConfigurationManager;

/**
 * Response Modification Handler. Oversees the overall communication
 * framework between the task handler and the response modifiers.
 * 
 * @author Levent Bolelli
 *
 */

public final class ResponseModificationHandler {

	private ObjectServer taskHandlerServer = null;
	private ObjectServer registryServer = null;
	private List<ModifierConnection> clientConnections;
	private ThreadPoolExecutor clientThreadPool = null;
	
	/**
	 * Initializes the servers (server that communicates with the task handler
	 * and the one that manages the response modifier registration) and creates 
	 * the threadpool for calling the response modifiers.
	 * @return true if the server initializes
	 * @throws Exception
	 */
	public boolean initialize() throws Exception {
			
		clientConnections = new ArrayList<ModifierConnection>();
		
		clientThreadPool = 	new ThreadPoolExecutor(
					     Configuration.minInitialThreadCount,
					     Configuration.maxInitialThreadCount,
					     Long.MAX_VALUE,   // keepalive time
					     TimeUnit.NANOSECONDS,  // time unit
					     new LinkedBlockingQueue<Runnable>());  // job queue
		
		clientThreadPool.prestartAllCoreThreads();
		
		// Initialize the server that listens to modifier registration process
		// Each modifier connects to this server, and sends a ModifierProperty
		// Object with the client's details. The Modification Handler, in turn,
		// establishes the client connection pool to send the requests to the
		// response modifier.
		
		ConfigurationManager registryServerConfigurationManager = 
								new ConfigurationManager();
		ModifierRegistryCommand registryCommand = new ModifierRegistryCommand();
		registryCommand.setClientConnectionsObject(clientConnections);
		ObjectServer registryServer = ObjectServer.createFromConfiguration(
                                     registryCommand,
                                     registryServerConfigurationManager);
		if(!registryServer.isAlive()) {
			return false;
		}
		// Intialize the server that communicates with the task handler
        ConfigurationManager cm = new ConfigurationManager();
        ObjectServer taskHandlerServer = ObjectServer.createFromConfiguration(
                new ResponseHandlerServiceCommand(), cm);
        if(!taskHandlerServer.isAlive()) {
			return false;
		}
        return true;
	}
	
	public void shutdown() {
		for(Iterator<ModifierConnection> it = clientConnections.iterator(); 
		it.hasNext();) {
			ByteStreamConnectionPool connectionPool = 
				((ModifierConnection)it.next()).getConnectionPool();
			if(!connectionPool.isShutdown())
				connectionPool.shutdown();
		}
		
		if(!registryServer.isShutdown()) {
			registryServer.shutdown();
		}
		if(!taskHandlerServer.isShutdown()) {
			taskHandlerServer.shutdown();
		}
		if(!clientThreadPool.isShutdown()) {
			clientThreadPool.shutdown();
		}
	}
}