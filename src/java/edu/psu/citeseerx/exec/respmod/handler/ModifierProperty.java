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

import java.net.InetAddress;

/**
 * ModifierProperty handles the registration of the response modifiers with the
 * response modification handler. The response modification handler has a server
 * running that listens to modifier registrations. When a response modifier starts,
 * it needs to send an instance of ModifierProperty to the response modification
 * handler, with it's name, host and port number. The response will be the same
 * object, with isRegistered flag set. If registration is not successful, the message
 * field will describe the reason. 
 * 
 * @author Levent Bolelli
 *
 */

public class ModifierProperty {
	// Set by the modifier
	private String modifierName;
	private InetAddress modifierHost;
	private int modifierPort;
    
	// Set by the response modification handler. Upon return of this
	// object. The response modifier should check these fields to verify 
	// registration status.
	private boolean isRegistered = false;
	private String message = "";
	
	/**
	 * 
	 * @param modifierName The descriptive and unique name of the response modifier. 
	 * @param modifierHost Host address
	 * @param modifierPort The port that the modifier is listening to
	 */
	public ModifierProperty(final String modifierName,						
							final InetAddress modifierHost,
			                final int modifierPort) {
		this.modifierName = modifierName;
		this.modifierHost = modifierHost;
		this.modifierPort = modifierPort;
	}
		
	public String getModifierName() {
		return modifierName;
	}
	
	public InetAddress getModifierHost() {
		return modifierHost;
	}
	
	public int getModifierPort() {
		return modifierPort;
	}
	
	public void setRegisteredFlag(final boolean flag) {
		isRegistered = flag;
	}
	
	public void setMessage(final String message) {
		this.message = message;
	}
	
	public boolean isRegistered() {
		return isRegistered;
	}
	
	public String getMessage() {
		return message;
	}
}
