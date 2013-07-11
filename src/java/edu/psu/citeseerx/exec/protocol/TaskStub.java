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
import edu.psu.citeseerx.exec.com.lb.BalanceSetException;
import edu.psu.citeseerx.exec.com.lb.LoadBalancer;
import edu.psu.citeseerx.exec.com.lb.LoadBalancerMonitor;

public class TaskStub extends Task implements LoadBalancerMonitor {
    
    String codebase;
    
    public String getCodebase() {
        return codebase;
    }
    
    public void setCodebase(String codebase) {
        this.codebase = codebase;
    }
    
    LoadBalancer balancer;
    
    public TaskStub newInstance() {
        TaskStub newStub = new TaskStub(name);
        if (codebase != null) {
            newStub.setCodebase(codebase);
        }
        newStub.setBalancer(balancer);
        return newStub;
    }
    
    public TaskStub(String name) {
        super(name);
    }
    
    
    public TaskStub(String name, String codebase) {
        super(name);
        this.codebase = codebase;
    }
    
    
    public TaskStub(String name, String codebase, LoadBalancer balancer) {
        super(name);
        this.codebase = codebase;
        this.balancer = balancer;
        balancer.registerMonitor(this);
    }
    
    public void setBalancer(LoadBalancer balancer) {
        this.balancer = balancer;
        balancer.registerMonitor(this);
    }
    
    public boolean execute() {
        try {
            System.out.println("STUB EXECUTING: "+name);
            ProtocolContainer pc = new ProtocolContainer(protocol, name);
            Protocol response = (Protocol)balancer.query(pc);
            protocol.merge(response);
            for (Enumeration<String> e = protocol.getDataKeys();
                    e.hasMoreElements(); ) {
                String key = e.nextElement();
                Object val = protocol.get(key);
                System.out.println("RESPONSE: "+key+":"+val);
            }
            return true;
        } catch (BalanceSetException e){
            System.err.println("TASK FAILURE: "+name);
            e.printStackTrace();
            return false;
        }
    }
    
    final ArrayList<TaskIODescriptor> requiredInputs = 
        new ArrayList<TaskIODescriptor>();
    final ArrayList<TaskIODescriptor> optionalInputs =
        new ArrayList<TaskIODescriptor>();
    final ArrayList<TaskIODescriptor> outputs =
        new ArrayList<TaskIODescriptor>();
    
    public void addRequiredInput(TaskIODescriptor type) {
        requiredInputs.add(type);
    }
    public void addOptionalInput(TaskIODescriptor type) {
        optionalInputs.add(type);
    }
    public void addOutput(TaskIODescriptor type) {
        outputs.add(type);
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("TaskStub " + name + " codebase=" + codebase);
        buffer.append("\nRequiredInputs:");
        for (Iterator<TaskIODescriptor> it = requiredInputs.iterator();
                it.hasNext(); ) {
            buffer.append(" "+it.next().toString());
        }
        buffer.append("\nOptionalInputs:");
        for (Iterator<TaskIODescriptor> it = optionalInputs.iterator();
                it.hasNext(); ) {
            buffer.append(" "+it.next().toString());
        }
        buffer.append("\nOutputs:");
        for (Iterator<TaskIODescriptor> it = outputs.iterator();
                it.hasNext(); ) {
            buffer.append(" "+it.next().toString());
        }
        return buffer.toString();
    }
    
    private boolean available = false;
    
    public boolean isAvailable() {
        return available;
    }
    
    public void notifyServiceAvailable(boolean available) {
        this.available = available;
        notifyMonitors();
    }
    
    protected final ArrayList<TaskStubMonitor> monitors =
        new ArrayList<TaskStubMonitor>();
    
    public void registerMonitor(TaskStubMonitor monitor) {
        if (!monitors.contains(monitor)) {
            monitors.add(monitor);
        }
    }
    
    protected void notifyMonitors() {
        synchronized(monitors) {
            for (Iterator<TaskStubMonitor> it = monitors.iterator();
                    it.hasNext(); ) {
                it.next().notifyTaskAvailable(this, available);
            }
        }
    }
    
}  //- class TaskStub
