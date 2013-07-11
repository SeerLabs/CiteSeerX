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

import java.net.InetAddress;

/**
 * Container class for a ConnectionPool configuration.  This can be used
 * in conjunction with the ConnectionPool.createFromConfiguration method
 * in order to create a specific subclass of a connection pool from raw
 * configuration settings, without requiring direct knowledge of the
 * underlying implementation.
 * 
 * @author Isaac Councill
 *
 */
public class ConnectionPoolConfiguration {

    private InetAddress remoteHost;
    private int port;
    private long expirationTime;
    private boolean useCompression;
    private int compressedBlockSize;
    private ServerConfiguration.TYPE type;
    
    public int getCompressedBlockSize() {
        return compressedBlockSize;
    }
    public void setCompressedBlockSize(int compressedBlockSize) {
        this.compressedBlockSize = compressedBlockSize;
    }
    public long getExpirationTime() {
        return expirationTime;
    }
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public InetAddress getRemoteHost() {
        return remoteHost;
    }
    public void setRemoteHost(InetAddress remoteHost) {
        this.remoteHost = remoteHost;
    }
    public ServerConfiguration.TYPE getType() {
        return type;
    }
    public void setType(String type)
            throws IllegalArgumentException, NullPointerException {
        this.type = Enum.valueOf(ServerConfiguration.TYPE.class, type);
    }
    public boolean isUseCompression() {
        return useCompression;
    }
    public void setUseCompression(boolean useCompression) {
        this.useCompression = useCompression;
    }
    
}  //- class ConnectionPoolConfiguration
