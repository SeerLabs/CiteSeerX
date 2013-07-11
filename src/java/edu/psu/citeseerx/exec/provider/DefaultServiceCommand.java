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

import edu.psu.citeseerx.exec.com.ServiceCommand;
import edu.psu.citeseerx.exec.protocol.Protocol;
import edu.psu.citeseerx.exec.protocol.ProtocolContainer;
import edu.psu.citeseerx.exec.protocol.TaskError;
import edu.psu.citeseerx.exec.protocol.TaskImpl;

public class DefaultServiceCommand implements ServiceCommand {

    private final LocalTaskRegistry taskRegistry;
    
    public DefaultServiceCommand(LocalTaskRegistry registry) {
        this.taskRegistry = registry;
    }
    
    public Protocol execute(Object obj) {
        ProtocolContainer pc = (ProtocolContainer)obj;
        Protocol protocol = pc.getProtocol();
        TaskImpl task = taskRegistry.getTask(pc.routingSlip);
        if (task == null) {
            /*protocol.addError(new TaskError(task.getName(),
                    new NullPointerException("undefined task resource")));*/
            protocol.addError(new TaskError("Null Task",
            new NullPointerException("undefined task resource")));
        }
        task.setProtocol(protocol);
        
        task.execute();
        
        taskRegistry.returnTask(task);
        return protocol;
    }
    
    public DefaultServiceCommand newCommand() {
        return new DefaultServiceCommand(taskRegistry);
    }
}
