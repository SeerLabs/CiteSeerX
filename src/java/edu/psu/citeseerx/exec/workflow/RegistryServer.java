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
package edu.psu.citeseerx.exec.workflow;

import edu.psu.citeseerx.exec.com.ConnectionPoolConfiguration;
import edu.psu.citeseerx.exec.protocol.TaskIODescriptor;
import edu.psu.citeseerx.exec.protocol.TaskStub;
import edu.psu.citeseerx.utility.ConfigurationManager;
import edu.psu.citeseerx.utility.DOMValidator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RegistryServer extends Thread {

    protected final AccessKey key = new AccessKey();
    protected final ServerSocket server;
    protected final ThreadPoolExecutor threadPool;
    protected final LinkedList<ServiceWorker> workerCache =
        new LinkedList<ServiceWorker>();
    protected final ServiceRegistry registry;
    
    public RegistryServer(ConfigurationManager cm, ServiceRegistry registry)
            throws IOException, ParserConfigurationException, Exception {
        this.registry = registry;
        this.setDaemon(true);
        this.setName("RegistryServer");
        
        int listenPort = cm.getInt("RegistryServer.listenPort", key);
        int numberOfThreads = cm.getInt("RegistryServer.numberOfThreads", key);
        
        /* Set global property for XML document builder implementation. */
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
            "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

        LinkedBlockingQueue<Runnable> workQueue = 
            new LinkedBlockingQueue<Runnable>();
        threadPool = new ThreadPoolExecutor(
                numberOfThreads,          // Core threads
                numberOfThreads,          // Max threads (ignored with
                                          //   LinkedBlockingQueue)
                1000,                     // Idle timeout (also ignored)
                TimeUnit.MILLISECONDS,
                workQueue);
        threadPool.prestartAllCoreThreads();
        
        for(int i=0; i<numberOfThreads; i++)
            workerCache.add(new ServiceWorker());
        
        server = new ServerSocket(listenPort);
        
    }  //- RegistryServer
    
    
    class ServiceWorker implements Runnable {
        
        private Socket socket;
        final DocumentBuilder builder;
        
        public ServiceWorker() throws ParserConfigurationException {
            builder = initializeBuilder();
        }
        
        public void setSocket(Socket s) {
            socket = s;
        }
        
        private DocumentBuilder initializeBuilder()
                throws ParserConfigurationException {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            
            factory.setAttribute("http://java.sun.com/xml/jaxp/" +
                    "properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");
                        
            factory.setAttribute("http://java.sun.com/xml/jaxp/" +
                    "properties/schemaSource",
                    "file://"+System.getProperty("CSX_HOME")+  // schema file
                    "/schemas/serviceregistration.xsd");
            
            return factory.newDocumentBuilder();

        }  //- ServiceWorker.initializeBuilder
        
        
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                
                StringBuffer buffer = new StringBuffer();
                int c;
                while ((c = reader.read()) >= 0)
                    buffer.append((char)c);
                
                String input = buffer.toString();
                System.out.println(input);
                
                String returnMessage = "OK";
                try {
                    Document doc = parseInput(input);
                    handleRegistrationDocument(doc);

                } catch (SAXException e) {
                    e.printStackTrace();
                    returnMessage = e.getMessage();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    returnMessage = e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    returnMessage = e.getMessage();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    returnMessage = e.getMessage();
                }
                
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                writer.write(returnMessage);
                writer.flush();
                socket.close();
                
            } catch (IOException e) {
                e.printStackTrace();
                
            } finally {
                synchronized(workerCache) {
                    workerCache.addLast(this);
                }
            }
            
        }  //- ServiceWorker.run
        
        
        private Document parseInput(String input)
                throws IOException, SAXException {
            
            InputSource source = new InputSource(
                    new ByteArrayInputStream(input.getBytes()));
            
            /* Use a new error handler for each request. */
            DOMValidator handler = new DOMValidator();
            builder.setErrorHandler(handler);
            
            Document document = builder.parse(source);
            if (handler.validationError)
                throw handler.saxParseException;
            return document;
            
        }  //- ServiceWorker.parseInput
        
        
        private void handleRegistrationDocument(Document doc) 
                throws NullPointerException, UnknownHostException {
            ConnectionPoolConfiguration config = readConnectionConfig(doc);
            List<TaskStub> tasks = readTaskDescriptors(doc);
            for (Iterator<TaskStub> it = tasks.iterator(); it.hasNext(); ) {
                System.out.println(it.next().toString());
            }
            registry.registerService(config, tasks);
            
        }  //- ServiceWorker.handleRegistrationDocument
        
        
        private ConnectionPoolConfiguration readConnectionConfig(Document doc)
                throws NullPointerException, UnknownHostException {
            ConnectionPoolConfiguration config =
                new ConnectionPoolConfiguration();
            String configns = "http://citeseerx.psu.edu/serviceregistration";
            try {
                config.setRemoteHost(InetAddress.getByName(
                        getValue(doc, "ReturnAddress", configns)));
                config.setPort(Integer.parseInt(
                        getValue(doc, "ServerPort", configns)));
                config.setType(getValue(doc, "ServerType", configns));
                config.setUseCompression(Boolean.parseBoolean(
                        getValue(doc, "Compressed", configns)));
                config.setCompressedBlockSize(Integer.parseInt(
                        getValue(doc, "CompressedBlockSize", configns)));
            } catch (NullPointerException e) {
                throw e;
            } catch (UnknownHostException e) {
                throw e;
            }
            return config;
            
        }  //- ServiceWorker.buildConnectionConfig 
        
        
        private ArrayList<TaskStub> readTaskDescriptors(Document doc) {
            String taskRegNS = "http://citeseerx.psu.edu/taskregistry";
            NodeList list = doc.getElementsByTagNameNS(taskRegNS, "Task");
            ArrayList<TaskStub> stubs = new ArrayList<TaskStub>();
            for (int i=0; i<list.getLength(); i++) {
                Node node = list.item(i);
                NamedNodeMap map = node.getAttributes();
                String name = null, codebase = null;
                for (int j=0; j<map.getLength(); j++){
                    Attr attr = (Attr)map.item(j);
                    if (attr.getName().equals("name"))
                        name = attr.getValue();
                    if (attr.getName().equals("codebase"))
                        codebase = attr.getValue();
                }
                TaskStub stub = new TaskStub(name, codebase);
                NodeList children = node.getChildNodes();
                for (int j=0; j<children.getLength(); j++) {
                    Node child = children.item(j);
                    String id = null, use = null, type = null;
                    type = ((Text)child.getFirstChild()).getData();
                    NamedNodeMap childMap = child.getAttributes();
                    for (int k=0; k<childMap.getLength(); k++) {
                        Attr attr = (Attr)childMap.item(k);
                        if (attr.getName().equals("id"))
                            id = attr.getValue();
                        if (attr.getName().equals("use"))
                            use = attr.getValue();
                    }
                    TaskIODescriptor desc = new TaskIODescriptor(id, type);
                    if (child.getNodeName().equals("input")) {
                        if (use.equals("required"))
                            stub.addRequiredInput(desc);
                        if (use.equals("optional"))
                            stub.addOptionalInput(desc);
                    }
                    if (child.getNodeName().equals("output"))
                        stub.addOutput(desc);
                }
                stubs.add(stub);
            }
            return stubs;
            
        }  //- ServiceWorker.readTaskDescriptors
        
        
        private String getValue(Document doc, String name, String ns) {
            NodeList list = doc.getElementsByTagNameNS(ns, name);
            String value = "";
            if (list.getLength() > 0) {
                Node node = list.item(0);
                Text val = (Text)node.getFirstChild();
                value = val.getData();
            }
            System.out.println(name+": "+value);
            return value;
        }
        
    }  //- class ServiceWorker
    
    
    protected boolean shutdown = false;
    
    public void run() {
        while(!shutdown) {
            ServiceWorker worker;
            synchronized(workerCache) {
                worker = workerCache.removeLast();
            }
            try {
                worker.setSocket(server.accept());
                threadPool.submit(worker);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        
    }  //- run
    
    
    public void shutdown() {
        shutdown = true;
    }
    
    public int getListenPort() {
        return server.getLocalPort();
    }
    
}  //- class RegistryServer
