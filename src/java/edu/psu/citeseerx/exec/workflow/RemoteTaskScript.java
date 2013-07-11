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

import org.w3c.dom.*;

import edu.psu.citeseerx.exec.protocol.Task;
import edu.psu.citeseerx.exec.protocol.TaskScript;
import edu.psu.citeseerx.exec.protocol.TaskStub;
import java.util.concurrent.*;


public class RemoteTaskScript extends TaskScript {

    protected final ServiceRegistry serviceRegistry;
    
    public RemoteTaskScript(String name, ThreadPoolExecutor auxThreadPool,
            ServiceRegistry registry) {
        super(name, auxThreadPool);
        this.serviceRegistry = registry;
    }
    
    public RemoteTaskScript(Document doc, ThreadPoolExecutor auxThreadPool,
            ServiceRegistry registry) 
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {        
        this.serviceRegistry = registry;
        this.auxThreadPool = auxThreadPool;
        parseTaskDoc(doc);
    }
    
    protected Task buildTask(Element elt) {
        String name = elt.getAttribute("name");
        TaskStub stub = new TaskStub(name);
        serviceRegistry.registerTask(stub);
        return stub;
    }
    
    public RemoteTaskScript newInstance() {
        RemoteTaskScript newScript =
            new RemoteTaskScript(name, auxThreadPool, serviceRegistry);
        newScript.copyTasks(this);
        return newScript;
    }
    
}
