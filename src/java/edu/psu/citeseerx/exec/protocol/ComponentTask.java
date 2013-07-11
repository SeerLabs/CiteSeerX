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

import java.util.*;

public class ComponentTask implements Runnable {

    protected final Task baseTask;
    protected final TaskScript parent;
    
    public ComponentTask(Task task, TaskScript parent) {
        this.baseTask = task;
        this.parent = parent;
    }
    
    //private Protocol protocol;
    
    public void setProtocol(Protocol protocol) {
        //this.protocol = protocol;
        baseTask.setProtocol(protocol);
    }
    
    private final Vector<ComponentTask> requiredDependencyList =
        new Vector<ComponentTask>();
    
    private final Vector<ComponentTask> optionalDependencyList =
        new Vector<ComponentTask>();
    
    public void addDependency(ComponentTask task, boolean optional) {
        task.addListener(this);
        if (optional) {
            if (!optionalDependencyList.contains(task)) {
                optionalDependencyList.add(task);
            }
        } else {
            System.out.println("adding required");
            if (!requiredDependencyList.contains(task)) {
                requiredDependencyList.add(task);
            }
        }
        System.out.println("added depency for component "+baseTask.name+
                ": "+task.baseTask.name);
        System.out.println(baseTask.name+" DEPSIZE: "+requiredDependencyList.size());
            
    }  //- addDependency
    
    
    private final Vector<Object> listeners =
        new Vector<Object>();
    
    public void addListener(Object listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }
    
    private void notifyCompleted() {
        System.out.println("notify completed");
        synchronized(listeners) {
            for (Iterator<Object> it = listeners.iterator();
                    it.hasNext(); ) {
                System.out.println("notifying");
                Object lock = it.next();
                synchronized(lock) {
                    lock.notifyAll();
                }
            }
        }
        
    }  //- notifyCompleted
    
    /*
    private void notifyFailed() {
        synchronized(listeners) {
            for (Iterator<ComponentTaskListener> it = listeners.iterator();
                    it.hasNext(); ) {
                it.next().taskFailed(this);
            }
        }
        
    }  //- notifyFailed
    */
    
    private boolean taskComplete = false;
    
    public boolean isComplete() {
        return taskComplete;
    }
    
    private boolean taskError = false;
    
    public boolean completedOK() {
        return taskError;
    }
    
    //public final Object notificationLock = new Object();
    
    public void run() {
        System.out.println(baseTask.name+" CP1");
        synchronized(this) {
            System.out.println(baseTask.name+" CP2");
            while(dependenciesNotMet()) {
                System.out.println(baseTask.name+": waiting for results");
                try {
                    wait();
                } catch (InterruptedException e) {}
                System.out.println(baseTask.name+": got results");
            }
        }
        System.out.println(baseTask.name+" CP3");

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
        System.out.println(baseTask.name+" CP4");

        if (dependenciesOK) {
            if (!baseTask.execute()) {
                taskError = false;
            } else {
                taskError = true;
            }
        }

        System.out.println(baseTask.name+" deps ok? " + dependenciesOK);

        taskComplete = true;
        notifyCompleted();
        
    }  //- run
    
    
    private boolean dependenciesNotMet() {
        System.out.println(baseTask.name+" CP1.1");
        System.out.println(baseTask.name+" DEPSIZE: "+requiredDependencyList.size());

        synchronized(requiredDependencyList) {
            for (Iterator<ComponentTask> it = requiredDependencyList.iterator();
                    it.hasNext(); ) {
                ComponentTask task = it.next();
                System.out.println(baseTask.name+" DEPSTATUS: "+task.baseTask.name+" "+task.isComplete());
                if (!task.isComplete()) {
                    return true;
                }
            }
        }
        System.out.println(baseTask.name+" CP1.2");

        synchronized(optionalDependencyList) {
            for (Iterator<ComponentTask> it = optionalDependencyList.iterator();
                    it.hasNext(); ) {
                if (!it.next().isComplete()) {
                    return true;
                }
            }
        }
        System.out.println(baseTask.name+" CP1.3");

        return false;
        
    }  //- dependenciesNotMet
    
    
    public ComponentTask newInstance(TaskScript parent) {
        return new ComponentTask(baseTask.newInstance(), parent);
    }
    
    public void reset() {
        taskComplete = false;
    }
    
    /*
    public void taskCompleted(ComponentTask task) {
        //dependencyList.remove(task);
    }
    
    public void taskFailed(ComponentTask task) {
        taskComplete = true;
        notifyFailed();
    }
    */
    
}  //- class ComponentTask
