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

import edu.psu.citeseerx.utility.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * This class maintains a multi-threaded server architecture for
 * efficiently handling ObjectTransferConnections.  A fixed-size
 * thread pool is set up and command objects that will be used
 * to handle incoming messages are pre-cached to minimize object
 * creation overhead.  All threads in the thread pool are prestarted.
 *
 * @author Isaac Councill
 *
 */
public abstract class ObjectServer extends Thread {

    /**
     * Class for controlling the execution of command objects that
     * handle incoming messages, and hiding communication details
     * from out-of-package users.
     */
    protected class ServiceWorker implements Runnable {
        
        final ServiceCommand command;
        SocketInitializer initializer;
        ObjectTransferConnection connection;
        
        ServiceWorker(ServiceCommand command) {
            this.command = command;
        }
        
        /**
         * Sets the initialization method for the communication channel.
         * MUST be called before run(). 
         */
        public void setInitializer(SocketInitializer initializer) {
            this.initializer = initializer;
        }
        
        /**
         * Reads objects from the ObjectTransferConnections, passes them
         * to the command object for execution, and writes the result
         * back to the client.  The loop is maintained until the
         * ObjectServer shuts down or an exception is thrown.  Before this
         * method terminates, the connection is nullified and this
         * ServiceWorker object is returned to the ObjectServer worker
         * cache.
         */
        public void run() {
            while (!shutdown) {
                try {
                    if (connection == null)
                        connection = initializer.initialize();
                    Object result = command.execute(connection.readObject());
                    connection.writeObject(result);
                } catch (EOFException e) {
                    // Connection closed gracefully by client.
                    break;
                } catch (SocketException e) {
                    // Connection Reset is common when a validation request
                    // times out and breaks protocol.
                    break;
                } catch (SocketTimeoutException e) {
                    // SocketTimeoutExceptions are also expected.                    
                    break;
                } catch (IOException e) {
                    // SERIOUS - unexpected socket I/O problem.
                    e.printStackTrace();  //TODO
                    break;
                } catch (ClassNotFoundException e) {
                    // SERIOUS - Object sent is not in local classpath.
                    e.printStackTrace();  //TODO
                    break;
                } catch (Exception e) {
                    // Other unexpected error - take these seriously.
                    e.printStackTrace();
                    break;
                }
            }
            
            try {
                connection.terminate();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    // close underlying socket - connection may not have
                    // been set up yet.
                    initializer.closeSocket();
                } catch (IOException ioe){/*ignore*/}
            }
            connection = null;
            
            synchronized(workerCache) {
                workerCache.addLast(this);
            }
            
        }  //- run
        
    }  //- class ServiceWorker
    
    
    protected final ServerSocket server;
    protected final ThreadPoolExecutor threadPool;
    
    protected final LinkedBlockingQueue<Runnable> workQueue
        = new LinkedBlockingQueue<Runnable>();
    protected final LinkedList<ServiceWorker> workerCache
        = new LinkedList<ServiceWorker>();
    
    protected final ServiceCommand commandFactory;
    protected final ServerConfiguration config;
    
    /**
     * Open a base ServerSocket at the specified port, initialize
     * thread pool, pre-cache a pool of serviceCommand clones, and then
     * start listening for connections.
     * @param command command object for handling client messages
     * @param configurationManager object for reading configuration.
     * @throws Exception
     */
    public ObjectServer(ServiceCommand command,
            ConfigurationManager configurationManager) throws Exception {

        config = new ServerConfiguration(configurationManager);
        try {
            server = new ServerSocket(config.getServerPort());
            commandFactory = command;

            // threadPool is fixed size and keepalive
            // is set to never time out threads.
            threadPool =
                new ThreadPoolExecutor(config.getPoolSize(),  // core threads
                                       config.getPoolSize(),  // max threads
                                       Long.MAX_VALUE,        // keepalive time
                                       TimeUnit.NANOSECONDS,  // time unit
                                       workQueue);            // job queue
            threadPool.setRejectedExecutionHandler(
                    new ThreadPoolExecutor.AbortPolicy());
            threadPool.prestartAllCoreThreads();  // No lazy initialization.
        
            for (int i=0; i<config.getPoolSize(); i++){
                workerCache.addLast(
                    new ServiceWorker(commandFactory.newCommand()));
            }
            
            setName("ObjectServer main");
            start();
            
        } catch (Exception e){
            throw(e);
        }
                
    }  //- ObjectServer
    
    
    /**
     * Factory method for generating the appropriate type of ObjectServer
     * based only upon existing on-disk configuration.
     * @param command
     * @param configurationManager
     * @return An ObjectServer created from on-disk configuration
     * @throws Exception
     */
    public static ObjectServer createFromConfiguration(ServiceCommand command,
            ConfigurationManager configurationManager) throws Exception {
        ServerConfiguration tmpConfig =
            new ServerConfiguration(configurationManager);
        if (tmpConfig.getType() == ServerConfiguration.TYPE.BYTE_STREAM) {
            return new ByteStreamObjectServer(command, configurationManager);
        }
        if (tmpConfig.getType() == ServerConfiguration.TYPE.XSTREAM) {
            return new XStreamObjectServer(command, configurationManager);
        }
        throw new Exception ("Configuration specifies unknown "+
                "ObjectTransferConnection type.");
        
    }  //- createFromConfiguration
    
    
    /**
     * Returns the configuration for this ObjectServer.
     * @return the configuration for this ObjectServer
     */
    public ServerConfiguration getConfiguration() {
        return config;
    }
    
    
    /* Flag for stopping the acceptance of new connections or messages. */
    private boolean shutdown = false;
    /* Socket timeout, in ms. */
    protected final int soTimeout = 30000;

    /* Precache protocol objects.  Not implementing this at this point.
    private final OKProtocol okProtocol = new OKProtocol();
    private final MaxClientsReachedProtocol maxClientsProtocol =
        new MaxClientsReachedProtocol();
    private final ServerShutdownProtocol shutdownProtocol =
        new ServerShutdownProtocol();
    */
    
    /**
     * Readies cached ServiceWorkers and submits them to the thread pool
     * to handle incoming requests.  Requests are first wrapped as
     * ObjectTransferConnections before the root ObjectServer thread
     * relinquishes control.
     */
    public void run() {
        try {
            ServiceWorker worker;
            while(!shutdown) {
                // Pull an available worker from the cache, or create
                // a new one if none is available.
                synchronized(workerCache) {
                    if (workerCache.size() > 0) {
                        worker = workerCache.removeLast();
                    } else {
                        worker = new ServiceWorker(
                            commandFactory.newCommand());
                    }
                }
                Socket socket = server.accept();
                socket.setSoTimeout(soTimeout);
                try {
                    startWorker(worker, socket, 
                            config.getCompressedBlockSize());
                } catch (RejectedExecutionException e) {
                    socket.close();
                    System.err.println("ObjectServer: Max clients reached.");
                }
            }
        
        } catch (SocketException e) {
            /* Ignore - most likely cause is shutdown. */
        } catch (Exception e) {
            /* Should never happen. */
            e.printStackTrace();
            
        } finally {
            shutdown();  // Does nothing if shutdown was already called.
        }
        
    }  //- run
    
    
    /**
     * Subclasses must override to provide the correct initializer 
     * for raw sockets accepted from client connections.
     * @param worker a ServiceWorker that will use the specified socket  
     * @param socket raw socket accepted from server
     */
    protected abstract void startWorker(
            ServiceWorker worker, Socket socket, int compressedBlockSize);

    /**
     * Sets the shutdown flag so no new clients will be accepted or threads
     * started, then shuts down the threadPool gracefully and closes the
     * ServerSocket.
     */
    public void shutdown() {
        if (shutdown)
            return;
        try {
            shutdown = true;
            server.close();
            threadPool.shutdown();
            System.out.println("ObjectServer shut down cleanly.");
        
        } catch (IOException e) {/*ignore*/}
         
    }  //- shutdown
    
    /**
     * @return whether this server is in a shutdown state.
     */
    public boolean isShutdown() {
        return shutdown;
    }
    
}  //- ObjectServer
