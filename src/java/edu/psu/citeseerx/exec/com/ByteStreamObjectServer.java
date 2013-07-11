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
import edu.psu.citeseerx.utility.*;

/**
 * ObjectServer implementation that wraps incoming connections as
 * ByteStreamTransferConnections.
 *
 * @author Isaac Councill
 *
 */
public class ByteStreamObjectServer extends ObjectServer {

    /**
     * Initialize superclass.
     * @param command command object for handling client messages
     * @param configurationManager for reading configuration
     * @throws Exception
     */
    public ByteStreamObjectServer(ServiceCommand command,
            ConfigurationManager configurationManager) throws Exception {
        super (command, configurationManager);
    }
    
    /**
     * Starts a ServiceWorker by supplying it with a
     * ByteStreamSocketInitializer to wrap the raw client Socket.
     */
    protected void startWorker(ServiceWorker worker, Socket socket,
            int compressedBlockSize) {  
        SocketInitializer initializer =
            new ByteStreamSocketInitializer(
                    socket, config.isUseCompression(), compressedBlockSize);
        worker.setInitializer(initializer);
        threadPool.submit(worker);
        
    }  //- startWorker

}  //- class ByteStreamObjectServer
