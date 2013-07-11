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

import edu.psu.citeseerx.utility.*;
import edu.psu.citeseerx.exec.com.ObjectServer;
import edu.psu.citeseerx.exec.com.ServiceCommand;

public class BaseService {

    protected final ObjectServer server;
    protected final LocalTaskRegistry taskRegistry;
    
    public BaseService() throws Exception {
        try {
            
            ConfigurationManager cm = new ConfigurationManager();
            taskRegistry = new LocalTaskRegistry();
            
            ServiceCommand command = new DefaultServiceCommand(taskRegistry);
            server = ObjectServer.createFromConfiguration(command, cm);
            
            RegistrationProvider regProvider =
                new RegistrationProvider(
                        cm,                         // Global configuration
                        server.getConfiguration(),  // Server configuration
                        taskRegistry);              // Available taskIndex
            regProvider.start();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw(e);
        }
    }
    
}

class AccessKey extends ConfigurationKey {}

class Command implements ServiceCommand {
    
    public ServiceCommand newCommand() {
        return new Command();
    }
    
    public Object execute(Object obj) {
        return obj;
    }
}