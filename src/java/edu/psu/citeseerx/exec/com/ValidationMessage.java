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

import java.io.*;

/**
 * Container for validation messages, used by ObjectTransferConnections
 * to make sure that the connection is working properly.
 * 
 * @author Isaac Councill
 *
 */
public abstract class ValidationMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5884216738770833496L;
    public static final String REQUEST = "REQUEST";
    public static final String RESPONSE = "RESPONSE";
    public static long REQID = 0;
    
    public static synchronized long newID() {
        return REQID++;
    }
    
    protected long id;
    
    public long getID() {
        return id;
    }
    
    public abstract String type();
    
    /**
     * Factory method to generate a ValidationRequest object.
     */
    public static ValidationRequest createRequest() {
        return new ValidationRequest();
    }
    
    /**
     * Factory method to generate a ValidationResponse object.
     */
    public static ValidationResponse createResponse(int id) {
        return new ValidationResponse(id);
    }
    
}  //- class ValidationMessage


class ValidationRequest extends ValidationMessage {
    static final long serialVersionUID = 970981235;
    public ValidationRequest() {
        id = ValidationMessage.newID();
    }
    public String type() {
        return REQUEST;
    }
    
}  //- class Request


class ValidationResponse extends ValidationMessage {
    static final long serialVersionUID = 82347234;
    public ValidationResponse(long id) {
        this.id = id;
    }
    public String type() {
        return RESPONSE;
    }
    
}  //- class Response

