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
package edu.psu.citeseerx.exec.profile;

import java.util.*;
import java.io.Serializable;

public class ServiceProfiler implements Serializable {

    public static final long serialVersionUID = 4698234;
    
    private final Hashtable<String,TaskAccountant> taskAccounts =
        new Hashtable<String,TaskAccountant>();
    
    public void registerExecutionStatus(String taskName,
            boolean success, long executionTime) {
        TaskAccountant accountant = taskAccounts.get(taskName);
        if (accountant == null) {
            accountant = new TaskAccountant(taskName);
        }
        if (success) {
            accountant.registerExecution(executionTime);
        } else {
            accountant.registerError();
        }
    }
    
    public void registerTask(String taskName) {
        TaskAccountant accountant = new TaskAccountant(taskName);
        taskAccounts.put(taskName, accountant);
    }
    
}
