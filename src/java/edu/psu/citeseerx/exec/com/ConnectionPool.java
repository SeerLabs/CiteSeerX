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
package edu.psu.citeseerx.exec.com;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * ConnectionPool manages client connections to a remote server host,
 * where connections are of type ObjectTransferConnection.  As new
 * connections are used, clients must return the connections to the pool
 * where they can be reused by other objects, increasing performance by
 * reducing the number of new connections that must be created.
 * 
 * Objects that lease connections must specify the amount of time they plan
 * to hold the object.  If the connection is not returned in time, it will
 * be destroyed.  Lease times can be extended by calling the renew method.
 * 
 * Idle connections are destroyed after expirationTime ms have passed.
 * 
 * If a call to shutdown() is made, the pool will stop accepting new
 * connections but it will continue to hold active connections until they are
 * returned or they time out.
 * 
 * @author Isaac Councill
 *
 */
public abstract class ConnectionPool {

    /* ConnectionPool manages connections for a single server location. */
    protected final InetAddress serverHost;
    protected final int serverPort;

    /* Time, in ms, that connections can remain idle before expiring. */
    private long expirationTime = 30000;
    
    /* Whether new connections should use gzip compression. */
    private final boolean useCompression;
    
    /* If using compression, this specifies the amount of data to buffer
     * before compressing and flushing. */
    private int compressedBlockSize = 1024;
    
    /* Sets the timeout for status requests over ObjectTransferConnections. */
    private int statusReqTimeout = 0;
    
    /* Storage for connections. */
    private final Hashtable<ObjectTransferConnection, Long> leased =
        new Hashtable<ObjectTransferConnection, Long>();
    private final Hashtable<ObjectTransferConnection, Long> available =
        new Hashtable<ObjectTransferConnection, Long>();
    
    
    /**
     * Set up a new connection pool with default configuration.
     * @param serverHost remote server host.
     * @param serverPort remote server port.
     */
    public ConnectionPool(InetAddress serverHost, int serverPort) {
    	this.serverHost = serverHost;
        this.serverPort = serverPort;
        useCompression = false;
        
    }  //- ConnectionPool
    
    
    /**
     * Set up a new connection pool with the supplied configuration.
     * @param host remote server host.
     * @param port remote server port.
     * @param expirationTime timeout on idle connections in ms.  If <= 0 is
     * specified, default expirationTime is used.
     * @param useCompression whether to use gzip compression.
     * @param compressedBlockSize if using compression, the amount of data
     * to buffer before compressing.  If <= 0 is specified, default
     * compressedBlockSize is used.
     */
    public ConnectionPool(InetAddress host, int port, long expirationTime,
            boolean useCompression, int compressedBlockSize) {
        this.serverHost = host;
        this.serverPort = port;
        this.useCompression = useCompression;

        if (expirationTime > 0)
            this.expirationTime = expirationTime;
        if (compressedBlockSize > 0)
            this.compressedBlockSize = compressedBlockSize;
    
    }  //- ConnectionPool
    
    
    /**
     * Factory method for creating an appropriate ConnectionPool directly
     * from configuration.
     * @param conf
     * @return A connectionPool created from configuration
     * @throws RuntimeException if the specified ConnectionPool type is
     * unknown
     */
    public static ConnectionPool createFromConfiguration(
            ConnectionPoolConfiguration conf) throws RuntimeException {
        switch(conf.getType()) {
        case BYTE_STREAM:
            return new ByteStreamConnectionPool(
                    conf.getRemoteHost(),
                    conf.getPort(),
                    conf.getExpirationTime(),
                    conf.isUseCompression(),
                    conf.getCompressedBlockSize());
        case XSTREAM:
            return new XStreamConnectionPool(
                    conf.getRemoteHost(),
                    conf.getPort(),
                    conf.getExpirationTime(),
                    conf.isUseCompression(),
                    conf.getCompressedBlockSize());
        default:
            throw new RuntimeException("Invalid ConnectionPool type: " +
                    conf.getType());
                
        }

    }  //- createFromConfiguration
    
    
    /**
     * Passes parameters to createConnectionImpl to get an implementation-
     * specific ObjectTransferConnection and sets the statusReqTimeout
     * if a non-default value is supplied.
     * @param useCompression
     * @param compressedBlockSize
     * @return
     * @throws IOException
     */
    private ObjectTransferConnection createConnection(
            boolean useCompression, int compressedBlockSize)
            throws IOException {
        
        ObjectTransferConnection connection = 
            createConnectionImpl(useCompression, compressedBlockSize);
        if (statusReqTimeout > 0)
            connection.setStatusTimeout(statusReqTimeout);
        return connection;
        
    }  //- createConnection
    
    
    /**
     * Subclasses must override to produce some implementation of
     * an ObjectTransferConnection. 
     */
    protected abstract ObjectTransferConnection
        createConnectionImpl(boolean useCompression, int compressedBlockSize)
            throws IOException;
    
    
    /**
     * Validates a given connection.
     */
    protected synchronized boolean validate(ObjectTransferConnection connection) 
            throws IOException, ClassNotFoundException,
            ObjectTransferConnection.InvalidException {
        return connection.validate();
    }
    
    
    /**
     * Shuts down an existing connection. 
     */
    protected synchronized void expire(ObjectTransferConnection connection) {
        connection.terminate();
        
    }  //- expire
    
    
    /**
     * Returns the number of active connections in this ConnectionPool.
     */
    public int countActiveConnections() {
    	return leased.size();
    }
    
    
    /* Flag for setting whether this pool is being,
     * or has been, shut down. */
    private boolean shutdown = false;
    
    
    /**
     * Stops ConnectionPool from leasing any more connections.  Active
     * connections will continue to be valid until they are returned to
     * the pool.  When all connections are returned, a finalization
     * message will be sent to any monitors.  A thread is spawned that
     * will destroy any active connections whose lease expires.
     */
    public synchronized void shutdown() {
        if (shutdown)
            return;
        notifyShutdown();
    	shutdown = true;
        new Thread("ConnectionPool Reaper") {
            public void run() {
                while (leased.size() > 0) {
                    reapActiveConnections();
                    if (leased.size() == 0) {
                        expireAll();
                    }
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {}
                }
                notifyFinalized();
            }
        }.start();
        
    }  //- shutdown
    
    
    /**
     * Destroy all connections.  This should only be called when there
     * are no leased connections and the pool is shutting down.
     */
    private synchronized void expireAll() {
        Enumeration<ObjectTransferConnection> l_enum = leased.keys();
        while (l_enum.hasMoreElements()) {
            expire(l_enum.nextElement());
        }
        Enumeration<ObjectTransferConnection> a_enum = available.keys();
        while (a_enum.hasMoreElements()) {
            expire(a_enum.nextElement());
        }
        
    }  //- expireAll
    
    
    /**
     * Leases an ObjectTransferConnection from the pool.  This method
     * checks the idle time of existing connections and expires connections
     * that are older than expirationTime.  Available connections are
     * checked against the validate method to make sure that they work
     * before being handed off.  If no available connections exist, a new
     * one is created.
     * @return an ObjectTransferConnection from the pool.
     * @param time the time for which the connection will be leased. Specify
     * a negative value to lease forever.
     * @throws Exception
     */
    public synchronized ObjectTransferConnection leaseConnection(long time)
            throws Exception {
        if (shutdown) {
            throw new ShutdownException();
        }
        reapActiveConnections();
        
        long now = System.currentTimeMillis();
        long expirationDate = Long.MAX_VALUE;
        if (time >= 0)
            expirationDate = now+time;
        
        ObjectTransferConnection connection;
        if (available.size() > 0) {
            Enumeration<ObjectTransferConnection> en = available.keys();
            while(en.hasMoreElements()) {
                connection = en.nextElement();
                if (now - available.get(connection).longValue() >
                        expirationTime) {
                    // Connection has expired.
                    available.remove(connection);
                    expire(connection);
                } else {
                    boolean isValid = false;
                    try {
                        isValid = validate(connection);
                    } catch (Exception e) {
                        /* We don't care - invalid is invalid.
                         * However, care should be taken to ensure
                         * that timeouts aren't killing performance. */
                    }
                    if (isValid) {
                        available.remove(connection);
                        leased.put(connection, new Long(expirationDate));
                        return connection;
                    } else {
                        // Connection failed validation.
                        available.remove(connection);
                        expire(connection);
                    }
                }
            }
        }
        // No connections currently available, so create a new one.
        connection = createConnection(useCompression, compressedBlockSize);
        leased.put(connection, new Long(expirationDate));
        return connection;
        
    }  //- leaseConnection
    
    
    /**
     * Tries to add more time to an existing lease.  Locks the leased table
     * during the method call.
     * @param connection connection to be renewed.
     * @param time time (from now) to extend the lease.
     * @throws Exception connection to be renewed is not in the leased table.
     */
    public void renew(ObjectTransferConnection connection, long time) 
            throws Exception {
        synchronized(leased) {
            if (leased.contains(connection)) {
                long now = System.currentTimeMillis();
                leased.put(connection, new Long(now+time));
            } else {
                throw new InvalidLeaseRequest();
            }
        }
        
    }  //- renew
    
    
    /**
     * Used when a client of this class knows that a connection it is leasing
     * is bad.  This call removes the connection from the leased table and
     * expires it.
     * @param connection
     */
    public void invalidate(ObjectTransferConnection connection) {
        synchronized(leased) {
            if (leased.contains(connection)) {
                leased.remove(connection);
                expire(connection);
            }
        }
        
    }  //- invalidate
    
    
    public class ShutdownException extends RuntimeException {
        /**
         * 
         */
        private static final long serialVersionUID = 1940740433581437050L;

        public ShutdownException() {
            super("Attempt to access ConnectionPool that is shut down");
        }
    }
    
    
    public class InvalidLeaseRequest extends RuntimeException {   
        /**
         * 
         */
        private static final long serialVersionUID = -7221592847266333274L;

        public InvalidLeaseRequest() {
            super("Invalid lease request");
        }
    }
    
    
    /**
     * Destroy leased connections whose leases have expired.  Leasing
     * objects were warned!
     */
    private void reapActiveConnections() {
       long now = System.currentTimeMillis();
       synchronized (leased) {
           Enumeration<ObjectTransferConnection> activeConnections
               = leased.keys();
           while (activeConnections.hasMoreElements()) {
               ObjectTransferConnection connection =
                   activeConnections.nextElement();
               long leaseExpiration = leased.get(connection).longValue();
               if (now > leaseExpiration) {
                   leased.remove(connection);
                   expire(connection);
               }
           }
       }
       
    }  //- reapActiveConnections
    
    
    /**
     * Unlocks the specified connection, returning it to the pool of 
     * available connections.
     * @param connection 
     */
    public synchronized void returnConnection
                (ObjectTransferConnection connection) {
        if (leased.contains(connection)) {
            leased.remove(connection);
            available.put(connection, new Long(System.currentTimeMillis()));
        }
        
    }  //- returnConnection
    
    
    /* Objects that will monitor this pool. */
    private final Vector<ConnectionPoolMonitor> monitors
        = new Vector<ConnectionPoolMonitor>();

    
    /**
     *  When all connections are returned to the pool after a shutdown
     *  message, this method is used to notify all listeners that the 
     *  ConnectionPool is finished.
     */
    private void notifyFinalized() {
        synchronized(monitors) {
            for (Iterator<ConnectionPoolMonitor> it = monitors.iterator();
                    it.hasNext(); ) {
                it.next().notifyFinalized(this);
            }
        }
        
    }  //- notifyFinalized
    
    
    /**
     * When a shutdown is initiated, notifies all monitors.
     */
    private void notifyShutdown() {
        synchronized(monitors) {
            for (Iterator<ConnectionPoolMonitor> it = monitors.iterator();
                    it.hasNext(); ) {
                it.next().notifyShutdown(this);
            }
        }

    }  //- notifyShutdown();
    
    
    /**
     * Registers a class that will listen for events in this ConnectionPool.
     * @param cpm
     */
    public synchronized void registerMonitor(ConnectionPoolMonitor cpm) {
        if (!monitors.contains(cpm)) {
            monitors.add(cpm);
        }
        
    }  //- registerMonitor
    
    
    /**
     * Removes a monitor from the list of monitors.  If the monitor is not
     * in the list, nothing is done.
     * @param cpm
     */
    public synchronized void removeMonitor(ConnectionPoolMonitor cpm) {
        if (monitors.contains(cpm)) {
            monitors.remove(cpm);
        }
        
    }  //- removeMonitor

    
    /**
     * Returns the number of milliseconds it takes for idle
     * connections to expire. 
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * Sets the time it takes for idle connections to expire,
     * in milliseconds. 
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Sets the read timeout for status requests for new
     * ObjectTransferConnections.  If <= 0 is specified, the default
     * timeout in the ObjectTransferConnection implementation is used.
     * @param timeout
     */
    public void setStatusReqTimeout(int timeout) {
        statusReqTimeout = timeout;
    }
    
    /**
     * Returns whether this ConnectionPool is compressing it's messages.
     * @return true if the ConnectionPool is compressing it's messages.
     */
    public boolean isUsingCompression() {
        return useCompression;
    }

    /**
     * Returns the URL of the remote server whose connections
     * this object pools.
     */
    public InetAddress getServerHost() {
        return serverHost;
    }

    /**
     * Returns the remote server port whose connections this object pools.
     */
    public int getServerPort() {
        return serverPort;
    }
    
    /**
     * Returns whether this ConnectionPool has shut down or
     * is in the process of shutting down.
     */
    public boolean isShutdown() {
        return shutdown;
    }
    
}  //- class ConnectionPool
