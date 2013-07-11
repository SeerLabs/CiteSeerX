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
package edu.psu.citeseerx.exec.com.tests;

import edu.psu.citeseerx.exec.com.InvalidRequest;
import edu.psu.citeseerx.exec.com.ServiceCommand;

/**
 * Very basic service command object that validates requests and
 * does some read/write operations on the request object.
 */
class Command implements ServiceCommand {
    
    public Command newCommand() {
        return new Command();
    }
    
    public Object execute(Object obj) {
        CommandContainer container;
        try {
            container = (CommandContainer)obj;
        } catch (ClassCastException e) {
            return new InvalidRequest(CommandContainer.class); 
        }
        
        // Simulate long execution.
        try {
            Thread.currentThread();
            Thread.sleep(Configuration.commandTime);
        } catch (InterruptedException e) {}
        
        String req = (String)container.request.get(CommandContainer.requestKey);
        String res;
        if (req.equals(CommandContainer.requestVal)) {
            res = CommandContainer.responseVal;
        } else {
            res = "Unknown request";
        }
        container.response.put(CommandContainer.responseKey, res);
        return obj;
    }
}