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

import java.net.*;
import java.io.*;

/**
 * A container for socket connections that allows an implementation-specific
 * initialization routine whose call can be delayed until an appropriate
 * time.  This allows an ObjectServer to thread off an incoming connection
 * without first transforming the client socket into an
 * ObjectTransferConnection, which performs a blocking read upon 
 * construction until the first object is transferred.
 * 
 * @author Isaac Councill
 *
 */
public abstract class SocketInitializer {

    protected final Socket socket;
    protected final boolean useCompression;
    protected final int compressedBlockSize;
    
    /**
     * Stores the specified socket for later initialization.
     * @param socket
     */
    public SocketInitializer(Socket socket, boolean compress,
            int compressedBlockSize){
        this.socket = socket;
        this.useCompression = compress;
        this.compressedBlockSize = compressedBlockSize;
    }
    
    /**
     * Override to initialize the socket into a useful communication channel.
     * @return inialized comm channel.
     */
    public abstract ObjectTransferConnection initialize() throws IOException;
    
    /**
     * Close the socket that is initialized by this connection.
     * @throws IOException
     */
    public void closeSocket() throws IOException {
        socket.close();
    }
    
}  //- SocketInitializer
