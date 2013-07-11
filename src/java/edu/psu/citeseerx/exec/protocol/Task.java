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

public abstract class Task implements Runnable {

    protected final String name;
    
    public Task(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    protected Protocol protocol;

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
    
    public abstract boolean execute();
    
    public abstract Task newInstance();

    public void run() {
        execute();
    }
    
}
