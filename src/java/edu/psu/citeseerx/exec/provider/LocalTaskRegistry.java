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
package edu.psu.citeseerx.exec.provider;

import edu.psu.citeseerx.exec.protocol.TaskImpl;
//import edu.psu.citeseerx.utility.*;
import java.util.*;
import org.jdom.*;

public class LocalTaskRegistry {    
    
    private final Hashtable<String, TaskPool> registeredTasks =
        new Hashtable<String, TaskPool>();
    
    private int taskPoolSize = 10;
    
    public LocalTaskRegistry() {
        
    }  //- LocalTaskRegistry
    
    
    public synchronized void registerTask(TaskImpl task) {
        if (!registeredTasks.containsKey(task.getName())) {
            TaskPool pool = new TaskPool(task, taskPoolSize);
            registeredTasks.put(task.getName(), pool);
        }
    }
    
    
    public TaskImpl getTask(String name) {
        if (registeredTasks.containsKey(name)) {
            TaskPool pool = registeredTasks.get(name);
            return pool.lease();
        }
        return null;
    }
    
    
    public void returnTask(TaskImpl task) {
        if (registeredTasks.containsKey(task.getName())) {
            registeredTasks.get(task.getName()).returnObject(task);
        }
    }
    
    
    public Element toXML(Namespace ns) {
        Element root = new Element("TaskRegistry", ns);
        synchronized(registeredTasks){
            for (Enumeration<TaskPool> e = registeredTasks.elements();
                    e.hasMoreElements(); ) {
                Element elt = e.nextElement().getPrototype().toXML(ns);
                elt.setNamespace(ns);
                root.addContent(elt);
            }
        }
        return root;
        
    }  //- toXML
        
}  //- class LocalTaskRegistry
