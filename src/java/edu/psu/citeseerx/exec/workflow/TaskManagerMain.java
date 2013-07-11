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
package edu.psu.citeseerx.exec.workflow;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.psu.citeseerx.exec.com.ObjectServer;
import edu.psu.citeseerx.exec.com.ServiceCommand;
import edu.psu.citeseerx.utility.ConfigurationKey;
import edu.psu.citeseerx.utility.ConfigurationManager;


public class TaskManagerMain {

    public static void main(String args[]) {
        
        try {
            ConfigurationManager cm = new ConfigurationManager();
            
            SynchronousQueue<Runnable> auxQueue =
                new SynchronousQueue<Runnable>();
            ThreadPoolExecutor auxiliaryThreadPool =
                new ThreadPoolExecutor(
                        50,                     // Core Threads
                        Integer.MAX_VALUE,      // Max Threads
                        2000l,                  // KeepAlive time
                        TimeUnit.MILLISECONDS,  // Time unit
                        auxQueue);              // Work queue

            ServiceRegistry serviceRegistry = new ServiceRegistry(cm);
            ScriptRegistry scriptRegistry =
                new ScriptRegistry(serviceRegistry, auxiliaryThreadPool);
            
            MultiCaster multiCaster = new MultiCaster(cm, serviceRegistry);
            multiCaster.start();
            
            ServiceCommand command =
                new TaskManagerServiceCommand(serviceRegistry,
                        scriptRegistry, auxiliaryThreadPool);
            ObjectServer server =
                ObjectServer.createFromConfiguration(command, cm);
                        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }  //- main
    
}  //- class TaskManagerMain

/**
 * Package-level key for accessing configuration.
 */
class AccessKey extends ConfigurationKey {}