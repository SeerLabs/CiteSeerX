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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import edu.psu.citeseerx.exec.com.ByteStreamConnectionPool;
import edu.psu.citeseerx.exec.com.ObjectTransferConnection;

/**
 * A unit of work to forward the request from the task handler to the response modifiers. 
 * Once the response modification handler receives an incoming request, the object is passed
 * to the instances of this class (each response modifier has one instance of this dedicated
 * to it). 
 * @author Levent Bolelli
 *
 */
public class ModifierTask implements Callable<Object> {
	
	private ModifierConnection connection;
	private Object requestObject;
	
	public ModifierTask(final ModifierConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * 
	 * @param obj the object received by the response modification handler which will
	 * be passed on to the response modifier.
	 */
	public void setRequestObject(Object obj) {
		requestObject = obj;
	}
	
	/**
	 * This method is called once all the threads of the threadpool are invoked.
	 * The invocation calls this method in all of the threads. The response object
	 * is the response received from the response modifier.
	 * 
	 */
	public Object call() {
		// Connects to the response modifier and returns the response object
		
		ByteStreamConnectionPool cp = connection.getConnectionPool();
		try{
			ObjectTransferConnection serverStream = 
				cp.leaseConnection(connection.getLeaseTime());
			serverStream.writeObject(requestObject);
			Object modifierResponse = serverStream.readObject();
			return modifierResponse;
		}
		catch(final Exception e) {
			final String message = "Could not communicate with " + 
			                                    connection.getModifierName();
			return new ExecutionException(new Throwable(message));
		}
	}
}
