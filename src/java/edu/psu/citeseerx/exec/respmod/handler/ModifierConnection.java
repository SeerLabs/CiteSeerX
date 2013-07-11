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
import edu.psu.citeseerx.exec.com.ByteStreamConnectionPool;
import edu.psu.citeseerx.exec.com.ConnectionPool;
import edu.psu.citeseerx.exec.com.ConnectionPoolMonitor;
import edu.psu.citeseerx.exec.respmod.Config.Configuration;

/**
 * The response modification handler creates and maintains an instance of 
 * ModifierConnection for every response modifier. When a response modifier
 * tries to register itself, the handler first performs a couple of checks, and
 * if the modifier passes those checks, the handler creates a ModifierConnection
 * object with the parameters sent by the modifier. This class also contains the
 * client connection pool to the response modifier server.
 * 
 * @author leventbolelli
 *
 */
public class ModifierConnection implements ConnectionPoolMonitor{
	
	private ByteStreamConnectionPool connectionPool = null;
		
	private String modifierName;
	private long leaseTime = -1;
	private long timeout = Long.MAX_VALUE;
	private InetAddress modifierHost;
	private int modifierPort;
	private boolean compress = Configuration.compress;
	private int compressedBlockSize = Configuration.compressedBlockSize;

	public ModifierConnection(final String modifierName,
							  final InetAddress modifierHost,
							  final int modifierPort,
							  final long timeout,
							  final long leaseTime) {
		this(modifierName, modifierHost, modifierPort, timeout);
		this.leaseTime = leaseTime;
	}

	public ModifierConnection(final String modifierName,
							  final InetAddress modifierHost,
							  final int modifierPort,
						      final long timeout) {
		this(modifierName, modifierHost, modifierPort);
		this.timeout = timeout;
	}

	public ModifierConnection(final String modifierName,
			                  final InetAddress modifierHost,
			  				  final int modifierPort) {
		this.modifierName = modifierName;
		this.modifierHost = modifierHost;
		this.modifierPort = modifierPort;

	}

	/**
	 * Creates the connection pool for this response modifier using
	 * the connection settings
	 */
	public void createClientConnectionPool() {
		if(connectionPool != null)
			return;
			
		connectionPool = 
			new ByteStreamConnectionPool(modifierHost,
			                             modifierPort,
                                         timeout,
                                         compress,
                                         compressedBlockSize);
		
		connectionPool.registerMonitor(this);		
	}
	
	public void notifyFinalized(ConnectionPool pool) {
		final String message = "ConnectionPool for "+modifierName+" shutdown";
	    System.out.println(message);
	}
    
    public void notifyShutdown(ConnectionPool pool) {
        //...
    }
	
	public void shutdown() {
		if(connectionPool != null && connectionPool.isShutdown()) {
			connectionPool.shutdown();
			notifyFinalized(connectionPool);
		}
	}
	
	public long getLeaseTime() {
		return leaseTime;
	}

	public long getTimeout() {
		return timeout;
	}

	public int getModifierPort() {
		return modifierPort;
	}
	
	public String getModifierName() {
		return modifierName;
	}
	
	public InetAddress getModifierHost() {
		return modifierHost;
	}
	
	public boolean compress() {
		return compress;
	}
	
	public int getCompressedBlockSize() {
		return compressedBlockSize;
	}
	
	
	public boolean isShutdown() {
		return connectionPool.isShutdown();
	}
	
	public ByteStreamConnectionPool getConnectionPool() {
		return connectionPool;
	}
	
}
