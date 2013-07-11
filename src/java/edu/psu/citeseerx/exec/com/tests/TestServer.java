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

import edu.psu.citeseerx.exec.com.ObjectServer;
import edu.psu.citeseerx.utility.ConfigurationManager;

public class TestServer {
    
    ObjectServer server = null;
    
    public TestServer() {
        try {
            ConfigurationManager cm = new ConfigurationManager();
            ObjectServer server = ObjectServer.createFromConfiguration(
                    new Command(), cm);
            Thread.sleep(5000);
            server.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) {
        //TestServer testServer = new TestServer();
    }
}
