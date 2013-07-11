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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import edu.psu.citeseerx.exec.com.ServiceCommand;
import edu.psu.citeseerx.exec.respmod.Config.Configuration;

/**
 * This is the service command extension to handle the transfer of the
 * object from the  task handler to the response modifiers and back to 
 * the task handler.
 * 
 * @author Levent Bolelli
 *
 */

public class ResponseHandlerServiceCommand implements ServiceCommand{
	private ThreadPoolExecutor clientThreadPool = null;
	private Collection<Callable<Object>> modifierTaskCollection = 
										new ArrayList<Callable<Object>>();
	
	public ResponseHandlerServiceCommand newCommand() {
		return new ResponseHandlerServiceCommand();
	}
	
	public void setThreadPoolExecutor(ThreadPoolExecutor tp) {
		clientThreadPool = tp;
	}
	
	public Object execute(Object obj){
		// Pass the object that is received from the task handler to the
		// response modifiers
		for (Iterator<Callable<Object>> it = modifierTaskCollection.iterator(); 
		it.hasNext();) {
			ModifierTask task = (ModifierTask)it.next();
			task.setRequestObject(obj);
		}
		
		List<Future<Object>> results = null;
		try {
			if(Configuration.responseHandlerTimeout < 0) {
				results = clientThreadPool.invokeAll(modifierTaskCollection);
			}
			else {
				results = clientThreadPool.invokeAll(
									modifierTaskCollection,
									Configuration.responseHandlerTimeout,
									Configuration.responseHandlerTimeoutUnit);
			}
		} catch (final InterruptedException e) {
			for(Future<Object> modifierTask : results){
				try {
					obj = modifierTask.get();
					//TODO:: Collect all objects from all the response modifiers
					// and merge them before sending back to the task handler
					// the format of the object that will be passed around is not
					// determined yet!
				} catch (final InterruptedException ie) {
					// No response
				} catch (final ExecutionException ie) {
					//TODO:: What do we do with this modifier?
				}
			}
		}
		return obj; //TODO:: Collect and merge response objects from the response modifiers
	}
}
