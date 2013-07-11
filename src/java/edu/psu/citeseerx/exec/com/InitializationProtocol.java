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
 * Protocol used to check the initialization status of new connections.
 * These classes should be sent by an ObjectServer to new clients to
 * indicate the state of a new connection.  This is not yet implemented,
 * and may not be implemented.  For now, it's here just as a reminder that
 * this strategy was considered.
 * 
 * @author Isaac Councill
 *
 */
public abstract class InitializationProtocol implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7209713245851602316L;

    /**
     * @return the message component of the protocol object
     */
    public abstract String message();
        
}  //- class InitializationProtocol


class OKProtocol extends InitializationProtocol {
    static final long serialVersionUID = 732489762;
    public String message() {
        return "OK";
    }
}


class MaxClientsReachedProtocol extends InitializationProtocol {
    static final long serialVersionUID = 2340987;
    public String message() {
        return "Max Clients Reached";
    }
}


class ServerShutdownProtocol extends InitializationProtocol {
    static final long serialVersionUID = 823490823;
    public String message() {
        return "Server is Shutting Down";
    }
}
