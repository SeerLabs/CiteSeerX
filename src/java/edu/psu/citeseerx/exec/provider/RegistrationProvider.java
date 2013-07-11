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
package edu.psu.citeseerx.exec.provider;

import edu.psu.citeseerx.exec.com.ServerConfiguration;
import edu.psu.citeseerx.utility.ConfigurationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

public class RegistrationProvider extends Thread {

    private final AccessKey key = new AccessKey();
    private final MulticastSocket listenSocket;
    private final int bufferSize;
    private final String serviceID;
    private final ServerConfiguration serverConfiguration;
    private final LocalTaskRegistry taskRegistry;
    
    public RegistrationProvider(ConfigurationManager cm,
            ServerConfiguration sc, LocalTaskRegistry taskRegistry) 
                throws IOException, Exception {
        
        this.serverConfiguration = sc;
        this.taskRegistry = taskRegistry;
        
        int listenPort = cm.getInt("RegistrationProvider.listenPort", key);
        String multicastGroup = cm.getString(
                "RegistrationProvider.multicastGroup", key);
        bufferSize = cm.getInt("RegistrationProvider.bufferSize", key);
        
        String returnAddress = serverConfiguration.getReturnAddress();
        int serverPort = serverConfiguration.getServerPort();
        serviceID = returnAddress + ":" + serverPort;
            
        InetAddress group = InetAddress.getByName(multicastGroup);
        listenSocket = new MulticastSocket(listenPort);
        listenSocket.joinGroup(group);
        
    }  //- RegistrationProvider
    
    
    private boolean registrationInProcess = false;
    private boolean shutdown = false;
    
    public void run() {
        while (!shutdown) {
            byte[] buffer = new byte[bufferSize];
            DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
            try {
                listenSocket.receive(recv);
            } catch (IOException e) {
                e.printStackTrace();
                shutdown();
            }
            if (!registrationInProcess) {
                String content = new String(buffer);
                InetAddress remoteAddr = recv.getAddress();

                //System.out.println("content: " + content);
                //System.out.println("address: " + remoteAddr.getHostAddress());
                
                /* First token is always the remote registration port. */
                StringTokenizer st = new StringTokenizer(content);
                String portString = st.nextToken();
                portString = portString.trim();
                int remotePort = Integer.parseInt(portString);
                
                boolean registered = false;
                while (st.hasMoreTokens()) {
                    if (st.nextToken().trim().equals(serviceID)) {
                        registered = true;
                        break;
                    }
                }
                
                if (!registered) {
                    registrationInProcess = true;
                    RegistrationThread registrar =
                        new RegistrationThread(remoteAddr, remotePort);
                    registrar.start();
                }
            }
        }
        
    }  //- run
    
    
    public void shutdown() {
        shutdown = true;
    }
    
    
    class RegistrationThread extends Thread {
        
        final InetAddress remoteAddr;
        final int remotePort;
        
        public RegistrationThread(InetAddress addr, int port) {
            remoteAddr = addr;
            remotePort = port;
        }
        
        public void run() {
            try {
                Document doc = buildRegistrationDocument();
                Socket socket = new Socket(remoteAddr, remotePort);
                //BufferedWriter writer = new BufferedWriter(
                //        new OutputStreamWriter(socket.getOutputStream()));
                
                XMLOutputter serializer = new XMLOutputter();
                serializer.output(doc, socket.getOutputStream());
                socket.shutdownOutput();  // send EOS
                
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String response = reader.readLine();
                System.out.println("RESPONSE FROM SERVER: " + response);
                socket.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                registrationInProcess = false;
            }
            
        }  //- RegistrationThread.run
        
        
        private Document buildRegistrationDocument() {

            Namespace srns = Namespace.getNamespace(
                "http://citeseerx.psu.edu/serviceregistration"); 

            Namespace trns = Namespace.getNamespace(
                "http://citeseerx.psu.edu/taskregistry"); 

            Element root = new Element("ServiceRegistration", srns);
                    
            Element serviceConfiguration = buildServerConfiguration(srns);
            Element taskReg = taskRegistry.toXML(trns);
            
            root.addContent(serviceConfiguration);
            root.addContent(taskReg);
            
            return new Document(root);
            
        }  //- RegistrationThread.buildRegistrationDocument
        
        
        private Element buildServerConfiguration(Namespace ns) {
            Element serverConfig = new Element("ServerConfiguration", ns);
            
            Element returnAddress = new Element("ReturnAddress", ns);
            returnAddress.setText(serverConfiguration.getReturnAddress());
            serverConfig.addContent(returnAddress);
            
            Element serverPort = new Element("ServerPort", ns);
            serverPort.setText(
                    Integer.toString(serverConfiguration.getServerPort()));
            serverConfig.addContent(serverPort);
            
            Element serverType = new Element("ServerType", ns);
            serverType.setText(serverConfiguration.getType().toString());
            serverConfig.addContent(serverType);
            
            Element compressed = new Element("Compressed", ns);
            compressed.setText(
                    Boolean.toString(serverConfiguration.isUseCompression()));
            serverConfig.addContent(compressed);
            
            Element blockSize = new Element("CompressedBlockSize", ns);
            blockSize.setText(Integer.toString(
                    serverConfiguration.getCompressedBlockSize()));
            serverConfig.addContent(blockSize);
            
            return serverConfig;
            
        }  //- RegistrationThread.buildServiceConfiguration
        
        
        /*private Element buildTaskRegistry(Namespace ns) {
            Element taskList = new Element("TaskRegistry", ns);
            return taskList;
            
        }*/  //- RegistrationThread.buildTaskRegistry
        
    }  //- class RegistrationThread
        
}  //- class RegistrationProvider
