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

import edu.psu.citeseerx.utility.*;

/**
 * Container class for holding ObjectServer configuration and performing
 * configuration I/O using a ConfigurationManager.
 *
 * @author Isaac Councill
 *
 */
public class ServerConfiguration {

    public static enum TYPE { BYTE_STREAM, XSTREAM };
    
    private int serverPort;
    private int poolSize;
    private int readTimeout;
    private boolean useCompression;
    private int compressedBlockSize;
    private String returnAddress;
    private TYPE type;
    
    private final ConfigurationManager manager;
    private final AccessKey accessKey = new AccessKey();

    /**
     * Reads in configuration from the specified ConfigurationManager
     * and throws an exception if any expected configuration element is 
     * missing.
     * @param cm
     * @throws Exception
     */
    public ServerConfiguration(ConfigurationManager cm) throws Exception {
        manager = cm;
        serverPort = manager.getInt("ObjectServer.serverPort", accessKey);
        poolSize = manager.getInt("ObjectServer.poolSize", accessKey);
        readTimeout = manager.getInt("ObjectServer.readTimeout", accessKey);
        useCompression =
            manager.getBoolean("ObjectServer.useCompression", accessKey);
        compressedBlockSize =
            manager.getInt("ObjectServer.compressedBlockSize", accessKey);
        returnAddress = 
            manager.getString("ObjectServer.returnAddress", accessKey);

        String transferType =
            manager.getString("ObjectServer.objectTransferType", accessKey);
        if (transferType.equals("BYTE_STREAM")) {
            type = TYPE.BYTE_STREAM;
        } else if (transferType.equals("XSTREAM")) {
            type = TYPE.XSTREAM;
        } else {
            throw new Exception ("objectTransferType not specified");
        }
        
    }  //- ServerConfiguration
    

    private final class AccessKey extends ConfigurationKey {}

    public int getCompressedBlockSize() {
        return compressedBlockSize;
    }

    public void setCompressedBlockSize(int compressedBlockSize) {
        this.compressedBlockSize = compressedBlockSize;
    }
    
    public String getReturnAddress() {
        return returnAddress;
    }
    
    public void setReturnAddress(String address) {
        returnAddress = address;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isUseCompression() {
        return useCompression;
    }

    public void setUseCompression(boolean useCompression) {
        this.useCompression = useCompression;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }  
}


