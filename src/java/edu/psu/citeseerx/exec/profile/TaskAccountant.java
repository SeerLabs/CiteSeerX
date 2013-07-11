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

import java.io.Serializable;

public class TaskAccountant implements Serializable {
    
    public static final long serialVersionUID = 1238972;
    
    private final String taskName;
    private long executionCount = 0;
    private long errorCount = 0;
    private float meanExecutionTime = -1;
    private long startTime;
    
    public TaskAccountant(String taskName) {
        this.taskName = taskName;
        startTime = System.currentTimeMillis();
    }
    
    public synchronized void registerExecution(long time) {
        float tmp = (executionCount*meanExecutionTime)+time;
        meanExecutionTime = tmp/++executionCount;
    }
    
    public synchronized void registerError() {
        errorCount++;
    }
    
    public synchronized void reset() {
        executionCount = 0;
        errorCount = 0;
        meanExecutionTime = -1;
        startTime = System.currentTimeMillis();
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public synchronized long getExecutionCount() {
        return executionCount;
    }
    
    public synchronized long getErrorCount() {
        return errorCount;
    }
    
    public synchronized float getMeanExecutionTime() {
        return meanExecutionTime;
    }
    
    public synchronized long getStartTime() {
        return startTime;
    }
    
}
