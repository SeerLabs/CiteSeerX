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
import java.io.Serializable;

public class Protocol implements Serializable {

    public static final long serialVersionUID = 1872345;
    
    private final Hashtable<String,Object> data =
        new Hashtable<String,Object>();
    
    public Object get(String name) {
        return data.get(name);
    }
    
    public Enumeration<String> getDataKeys() {
        return data.keys();
    }
    
    public void set(String name, Object item) {
        data.put(name, item);
    }
    
    private Hashtable<String,Object> taskParameters =
        new Hashtable<String,Object>();
    
    public Object getTaskParameter(String name) {
        return taskParameters.get(name); 
    }
    
    public void setTaskParameter(String name, Object value) {
        taskParameters.put(name, value);
    }
    
    public boolean taskParameterExists(String name) {
        return taskParameters.containsKey(name);
    }
    
    private ArrayList<String> dataItemsToKeep = new ArrayList<String>(); 
    
    public void setKeeper(String label) {
        dataItemsToKeep.add(label);
    }
    
    public void merge(Protocol protocol) {
        Enumeration<String> keys = protocol.getDataKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            data.put(key, protocol.get(key));
        }
    }
    
    
    public void deleteExtraneousData() {
        synchronized(data) {
            for (Enumeration<String> keys = data.keys();
                    keys.hasMoreElements(); ) {
                String key = keys.nextElement();
                if (!dataItemsToKeep.contains(key)) {
                    data.remove(key);
                }
            }
        }
        
    }  //- deleteExtraneousData
    
    private final ArrayList<TaskError> errors = new ArrayList<TaskError>();
    
    public void addError(TaskError error) {
        errors.add(error);
    }
    
    public int errorCount() {
        return errors.size();
    }
    
    public Iterator<TaskError> getErrors() {
        return errors.iterator();
    }
    
}  //- class Protocol
