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
package edu.psu.citeseerx.exec.protocol;

import java.util.Iterator;
import java.util.Vector;

public class LocalComponentTask extends ComponentTask {

    private TaskImpl baseTask;
    
    public LocalComponentTask(TaskImpl task, TaskScript parent) {
        super(task, parent);
        this.baseTask = task;
    }
    
    private Protocol protocol;
    
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
        baseTask.setProtocol(this.protocol);
    }
    
    private final Vector<ComponentTask> requiredDependencyList =
        new Vector<ComponentTask>();
    
    private final Vector<ComponentTask> optionalDependencyList =
        new Vector<ComponentTask>();
    
    public void addDependency(ComponentTask task, boolean optional) {
        if (optional)
            if (!optionalDependencyList.contains(task))
                optionalDependencyList.add(task);
        else
            if (!requiredDependencyList.contains(task))
                requiredDependencyList.add(task);
            
    }  //- addDependency
    
    
    private final Vector<ComponentTaskListener> listeners =
        new Vector<ComponentTaskListener>();
    
    public void addListener(ComponentTaskListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }
    
    private void notifyCompleted() {
        synchronized(listeners) {
            for (Iterator<ComponentTaskListener> it = listeners.iterator(); 
                    it.hasNext(); ) {
                it.next().taskCompleted(this);
            }
        }
        
    }  //- notifyCompleted
    
    
    private void notifyFailed() {
        synchronized(listeners) {
            for (Iterator<ComponentTaskListener> it = listeners.iterator();
                    it.hasNext(); ) {
                it.next().taskFailed(this);
            }
        }
        
    }  //- notifyFailed
    
    
    private boolean taskComplete = false;
    
    public boolean isComplete() {
        return taskComplete;
    }
    
    private boolean taskError = false;
    
    public boolean completedOK() {
        return taskError;
    }
    
    public void run() {
        try {
            while(dependenciesNotMet())
                this.wait();
            boolean dependenciesOK = true;
            synchronized(requiredDependencyList) {
                for (Iterator<ComponentTask> it =
                        requiredDependencyList.iterator(); it.hasNext(); ) {
                    if (!it.next().completedOK()) {
                        dependenciesOK = false;
                        break;
                    }
                }                
            }
            if (dependenciesOK)
                if (!baseTask.execute())
                    taskError = true;
            else
                taskError = true;

            taskComplete = true;
            notifyCompleted();
        } catch (InterruptedException e) { /* ignore */ }
        
    }  //- run
    
    
    private boolean dependenciesNotMet() {
        synchronized(requiredDependencyList) {
            for (Iterator<ComponentTask> it = requiredDependencyList.iterator();
                    it.hasNext(); ) {
                if (!it.next().isComplete())
                    return true;
            }
        }
        synchronized(optionalDependencyList) {
            for (Iterator<ComponentTask> it = optionalDependencyList.iterator();
                    it.hasNext(); ) {
                if (!it.next().isComplete())
                    return true;
            }
        }
        return false;
        
    }  //- dependenciesNotMet
    
    
    public void taskCompleted(ComponentTask task) {
        //dependencyList.remove(task);
    }
    
    public void taskFailed(ComponentTask task) {
        taskComplete = true;
        notifyFailed();
    }
    
    
    public LocalComponentTask newInstance() {
        return null;
    }
    
}  //- class ComponentTask
