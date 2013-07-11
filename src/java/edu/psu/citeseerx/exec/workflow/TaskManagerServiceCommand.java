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

import edu.psu.citeseerx.exec.com.ServiceCommand;
import edu.psu.citeseerx.exec.protocol.Protocol;
import edu.psu.citeseerx.exec.protocol.ProtocolContainer;
import edu.psu.citeseerx.exec.protocol.TaskError;
import edu.psu.citeseerx.exec.protocol.TaskScript;
import java.util.concurrent.*;

public class TaskManagerServiceCommand implements ServiceCommand {

    protected final ServiceRegistry serviceRegistry;
    protected final ScriptRegistry scriptRegistry;
    protected final ThreadPoolExecutor auxThreadPool;
    
    public TaskManagerServiceCommand(ServiceRegistry serviceRegistry,
            ScriptRegistry scriptRegistry, ThreadPoolExecutor auxPool) {
        this.serviceRegistry = serviceRegistry;
        this.scriptRegistry = scriptRegistry;
        this.auxThreadPool = auxPool;
    }
    
    public Protocol execute(Object obj) {
        Protocol protocol = null;
        
        if (obj instanceof String) {
            String query = (String)obj;
            int boundaryIndex = query.indexOf("/");
            String scriptRoute = query.substring(0, boundaryIndex);
            
            System.out.println("User query: " + query + " Route: " + scriptRoute);
            
            RemoteTaskScript script = scriptRegistry.getScript(scriptRoute);
            if (script == null) {
                protocol = new Protocol();
                protocol.addError(new TaskError("input",
                        new Exception("Unknown Script")));
            } else {
                protocol = createNewUserProtocol(query);
                script.setProtocol(protocol);
                script.execute(auxThreadPool);
                //auxThreadPool.submit(script);
                protocol = script.getProtocol();
                script.setProtocol(null);  // detach protocol from script
                script.reset();
                scriptRegistry.returnScript(script);
            }
            
        } else if (obj instanceof ProtocolContainer) {
            ProtocolContainer pc = (ProtocolContainer)obj;
            protocol = pc.getProtocol();
            TaskScript script = scriptRegistry.getScript(pc.routingSlip);
            if (script == null)
                protocol.addError(new TaskError("input",
                        new Exception("Unknown Script")));
            else {
                script.setProtocol(protocol);
                script.execute(auxThreadPool);
                protocol = script.getProtocol();
            }
            
        } else {
            protocol = new Protocol();
            protocol.addError(new TaskError("input",
                    new Exception("Invalid Request Format")));
        }
        return protocol;
    }
    
    public TaskManagerServiceCommand newCommand() {
        return new TaskManagerServiceCommand(serviceRegistry,
                scriptRegistry, auxThreadPool);
    }
    
    protected static Protocol createNewUserProtocol(String query) {
        Protocol protocol = new Protocol();
        protocol.set("QUERY_STRING", query);
        return protocol;
    }
}
