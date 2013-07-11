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
package edu.psu.citeseerx.exec.com.tests;

public class Configuration {

    public static final int BYTE_STREAM = 0;
    public static final int XSTREAM = 1;
    public static int TYPE = BYTE_STREAM;

    public static String serverHost = "localhost";
    public static int serverPort = 9801;
    
    public static int poolSize = 25;  // for threads in ObjectServer.
    public static int clientPoolSize = 25; // for threads in TestClient.
    
    /* delay before processing command in ms,
     * to simulate complex operations. */ 
    public static int commandTime = 50;
    
    public static boolean compress = false;
    public static int compressedBlockSize = 1024;
    public static long expirationTime = 3000;  // for idle connections in CP.
    public static long leaseTime = -1;  // for active connections in CP
    
}
