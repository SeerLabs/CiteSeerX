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
package edu.psu.citeseerx.exec.com.lb;

import edu.psu.citeseerx.exec.com.ConnectionPool;
import edu.psu.citeseerx.exec.com.ObjectTransferConnection;

import java.util.*;
import java.io.IOException;

/**
 * 
 * 
 * @author Isaac Councill
 *
 */
public class RoundRobinBalancer implements LoadBalancer {

    protected final CircularArrayList<ConnectionPool> balanceSet = 
        new CircularArrayList<ConnectionPool>();
    
    
    public Object query(Object obj) throws BalanceSetException {
        if (balanceSet.getSize() == 0) {
            throw new BalanceSetException("No resources available");
        }
        for (int i=0; i<balanceSet.getSize(); i++) {
            try {
                ObjectTransferConnection connection = getConnection(-1);
                connection.writeObject(obj);
                return connection.readObject();
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new BalanceSetException("Failure of all resources");
    }
    
    /**
     * Adds a new pool to the balance set.  If the pool is already part of
     * the balance set, it will not be added again.
     */
    public synchronized void addPool(ConnectionPool pool) {
        balanceSet.put(pool);
        System.out.println("adding to pool: " + balanceSet.getSize());
        if (balanceSet.getSize() == 1) {
            notifyServiceAvailable(true);
            System.out.println("notified listeners");
        }
    }
    
    
    /**
     * Removes a connection pool from the balance set.  This should rarely
     * be called directly.  Calling shutdown on the pool will result in
     * it's removal from the set.
     */
    public void removePool(ConnectionPool pool) {
        balanceSet.remove(pool);
        if (balanceSet.getSize() == 0)
            notifyServiceAvailable(false);
    }
    
    
    /**
     * Gets a connection from the next connection pool in the balance set.
     */
    public ObjectTransferConnection getConnection(int time) throws Exception {
        ConnectionPool pool = balanceSet.nextElement();
        if (pool == null) {
            return null;
        }
        ObjectTransferConnection connection = pool.leaseConnection(time);
        return connection;
        
    }  //- getConnection
    
    
    /**
     * Called when a connection pool is shut down.  This method removes the
     * pool from the balance set.
     */
    public void notifyShutdown(ConnectionPool pool) {
        removePool(pool);
    }
    
    
    /**
     * Called when a connection pool is finalized.  Does nothing.
     */
    public void notifyFinalized(ConnectionPool pool) {
        /* Ignore this notification. */
    }
    
    
    protected Vector<LoadBalancerMonitor> monitors =
        new Vector<LoadBalancerMonitor>();
    
    
    /**
     * Adds a monitor that will listen for events in this balancer.
     */
    public void registerMonitor(LoadBalancerMonitor monitor) {
        if (!monitors.contains(monitor))
            monitors.add(monitor);
    }
    
    
    /**
     * Removes a monitor from the monitor list.
     */
    public void removeMonitor(LoadBalancerMonitor monitor) {
        if (monitors.contains(monitor))
            monitors.remove(monitor);
    }
    
    
    /**
     * Notifies all monitors about changes in service availability.
     */
    public void notifyServiceAvailable(boolean available) {
        synchronized(monitors) {
            for (Iterator<LoadBalancerMonitor> it = monitors.iterator();
                    it.hasNext(); ) {
                it.next().notifyServiceAvailable(available);
            }
        }
        
    }  //- notifyServiceAvailable
    
}  //- class RoundRobinBalancer
