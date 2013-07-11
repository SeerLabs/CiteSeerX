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
import edu.psu.citeseerx.exec.com.ConnectionPoolMonitor;
import edu.psu.citeseerx.exec.com.ObjectTransferConnection;

public interface LoadBalancer extends ConnectionPoolMonitor {
    
    public void addPool(ConnectionPool pool);
    public void removePool(ConnectionPool pool);
    public ObjectTransferConnection getConnection(int time) throws Exception;
    public void registerMonitor(LoadBalancerMonitor monitor);
    public void removeMonitor(LoadBalancerMonitor monitor);
    public Object query(Object obj);
    
    /**
     * This method should be used to notify all monitors when the size of
     * the balance set increases from 0 to 1 (available=true) or from >0
     * to 0 (available=false). 
     * @param available
     */
    abstract void notifyServiceAvailable(boolean available);
    
}  //- interface LoadBalancer
