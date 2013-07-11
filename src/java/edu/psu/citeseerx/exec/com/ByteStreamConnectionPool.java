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
import java.net.*;

/**
 * Manages a pool of ByteStreamTransferConnections.
 *
 * @author Isaac Councill
 *
 */
public class ByteStreamConnectionPool extends ConnectionPool {

    /**
     * Set up a new connection pool with default configuration.
     * @param host remote server host.
     * @param port remote server port.
     */
    public ByteStreamConnectionPool(InetAddress host, int port) {
        super(host, port);
        
    }  //- ByteStreamConnectionPool
    
    
    /**
     * Set up a new connection pool with the supplied configuration.
     * @param host remote server host.
     * @param port remote server port.
     * @param expirationTime timeout on idle connections in ms.
     * @param useCompression whether to use gzip compression.
     */
    public ByteStreamConnectionPool(InetAddress host, int port,
            long expirationTime, boolean useCompression,
            int compressedBlockSize) {
        super(host, port, expirationTime, useCompression, compressedBlockSize);
        
    }  //- ByteStreamConnectionPool
    
    
    /**
     * Creates a new ByteStreamTransferConnection.
     */
    protected ObjectTransferConnection createConnectionImpl
            (boolean useCompression, int compressedBlockSize)
                throws IOException {
        Socket socket = new Socket(serverHost, serverPort);
        return new ByteStreamTransferConnection(
                socket, useCompression, compressedBlockSize);
        
    }  //- createConnection

}  //- class ByteStreamConnectionPool
