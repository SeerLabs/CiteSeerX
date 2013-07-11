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
package edu.psu.citeseerx.exec.com.tests;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.psu.citeseerx.exec.com.ByteStreamConnectionPool;
import edu.psu.citeseerx.exec.com.ConnectionPool;
import edu.psu.citeseerx.exec.com.ConnectionPoolMonitor;
import edu.psu.citeseerx.exec.com.ObjectTransferConnection;
import edu.psu.citeseerx.exec.com.XStreamConnectionPool;

/**
 * Container for corecom unit tests.
 * 
 * @author Isaac Councill
 *
 */
public class TestClient implements ConnectionPoolMonitor {

    ConnectionPool clientPool;
    
    static int counter = 0;
    static synchronized int getCount() {
        return counter++;
    }
    
    public void notifyFinalized(ConnectionPool pool) {
        System.out.println("ConnectionPool shut down cleanly.");
    }
    
    public void notifyShutdown(ConnectionPool pool) { /* ignore */ }
    
    /**
     * Sets up a connection pool based on test configuration.
     */
    public TestClient() {
        try {
            InetAddress serverHost =
                InetAddress.getByName(Configuration.serverHost);
            switch(Configuration.TYPE) {
            case Configuration.BYTE_STREAM:
                clientPool = new ByteStreamConnectionPool(
                        serverHost,
                        Configuration.serverPort,
                        Configuration.expirationTime,
                        Configuration.compress,
                        Configuration.compressedBlockSize);
                break;
            case Configuration.XSTREAM:
                clientPool = new XStreamConnectionPool(
                        serverHost,
                        Configuration.serverPort,
                        Configuration.expirationTime,
                        Configuration.compress,
                        Configuration.compressedBlockSize);
                break;                
            }
            clientPool.registerMonitor(this);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection from the pool, writes a request and receives
     * a reponse, checks response for validity, then returns the connection.
     * @param attempts number of attempts to make, allowing retry in case of errors.
     * method will return after the first successful completion.
     */
    public long simpleRequest(int attempts) {
        int ID = getCount();
        long now = System.currentTimeMillis();
        for (int i=0; i<attempts; i++) {

            // Set up the data object to be transferred.
            CommandContainer req = new CommandContainer();
            req.request.put(CommandContainer.requestKey,
                    CommandContainer.requestVal);

            // Lease a connection and set timeout.
            ObjectTransferConnection connection;
            try {
                connection =
                    clientPool.leaseConnection(Configuration.leaseTime);
                //connection.setSoTimeout(500);  // Highly recommended to set!
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(ID + " failed to lease connection");
                break;
            }
            
            now = System.currentTimeMillis();
            // Write request, read response, and make sure it looks valid.
            try {
                connection.writeObject(req);
                CommandContainer res =
                    (CommandContainer)connection.readObject();

                String resVal =
                    (String)res.response.get(CommandContainer.responseKey);
                if (resVal.equals(CommandContainer.responseVal)) {
                    //System.out.println(ID+" ok");
                } else {
                    System.out.println(ID+" invalid response: "+resVal);
                }
                return System.currentTimeMillis() - now;
            
            } catch (SocketTimeoutException e) {
                System.out.println(ID+" req timed out");
                clientPool.invalidate(connection);
            } catch (Exception e) {
                System.out.println(ID+" request failed");
                e.printStackTrace();
            } finally {
                // Best to return connections within a finally.
                //req = null;
                //System.gc();
                clientPool.returnConnection(connection);
            }
        }
        System.out.println(ID +" req failed after "+attempts+" tries");
        return System.currentTimeMillis() - now;
    }
        
    public void loadedTestImpl(int numReqs, long delay) {
        LinkedBlockingQueue<Runnable> workQueue
            = new LinkedBlockingQueue<Runnable>();

        ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(Configuration.clientPoolSize, // core threads
                                   Configuration.clientPoolSize, // max threads
                                   Long.MAX_VALUE,        // keepalive time
                                   TimeUnit.NANOSECONDS,  // time unit
                                   workQueue);            // job queue
        threadPool.prestartAllCoreThreads();  // No lazy initialization.

        for (int i=0; i<numReqs; i++) {
            threadPool.submit(
                    new Runnable() {
                        public void run() {
                            long latency = simpleRequest(3);
                            System.out.println(latency);
                        }
                    });
            try {
                Thread.currentThread();
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(3600, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientPool.shutdown();
    }
    
    public void shutdown() {
        System.out.println("Shutting down client.");
        clientPool.shutdown();
    }
    
    
    public static void simpleTest() {
        TestClient testClient = new TestClient();
        testClient.simpleRequest(1);
        testClient.shutdown();
    }
    
    public static void incrementalTest(int iterations) {
        TestClient testClient = new TestClient();
        for (int i=0; i<10; i++) {
            testClient.simpleRequest(1);
        }
        testClient.shutdown();
    }
    
    public void loadedTest() {
        int numReqs = 10000;
        loadedTestImpl(numReqs, 50);
    }
    

    public static void main(String args[]) {
        TestClient client = new TestClient();
        client.loadedTest();
        //incrementalTest(10);
        //simpleTest();
    }

}




