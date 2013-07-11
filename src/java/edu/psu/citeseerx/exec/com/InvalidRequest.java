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
package edu.psu.citeseerx.exec.com;

import java.io.Serializable;

/**
 * ServiceCommand objects use this class to inform callers when
 * the ServiceCommand is called to operate on an expected object type.
 * 
 * @author Isaac Councill
 *
 */
public class InvalidRequest implements Serializable {
    
    final static long serialVersionUID = 72342309;
    
    final Class<?> expected;
    
    /**
     * Sets the class that the caller was expecting.
     */
    public InvalidRequest(Class<?> c) {
        expected = c;
    }
    
    /**
     * @return string that states the class expectation.
     */
    public String getMessage() {
        return "Expected class of type " + expected.toString();
    }
    
}  //- InvalidRequest
