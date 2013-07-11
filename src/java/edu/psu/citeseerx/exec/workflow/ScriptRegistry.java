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

import edu.psu.citeseerx.utility.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class ScriptRegistry {

    //private final ConfigurationKey accessKey = new AccessKey();
    
    private final String defaultScriptDirectory = "scripts";
    
    private int poolSize = 10;
    
    private final Hashtable<String,ObjectPool<RemoteTaskScript>> registry =
        new Hashtable<String,ObjectPool<RemoteTaskScript>>();
    
    private final DocumentBuilder builder;
    
    private final ServiceRegistry serviceRegistry;
    
    private final ThreadPoolExecutor auxThreadPool;
    
    public ScriptRegistry(ServiceRegistry serviceRegistry,
            ThreadPoolExecutor auxThreadPool)
            throws IOException, ParserConfigurationException {
        this.serviceRegistry = serviceRegistry;
        this.auxThreadPool = auxThreadPool;
        builder = initializeBuilder();
        readRegistry();
    }
    
    private DocumentBuilder initializeBuilder()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        
        factory.setAttribute("http://java.sun.com/xml/jaxp/" +
                "properties/schemaLanguage",
                "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute("http://java.sun.com/xml/jaxp/" +
                "properties/schemaSource",
                "file://"+System.getProperty("CSX_HOME")+
                "/schemas/script.xsd");
        
        return factory.newDocumentBuilder();
        
    }  //- initializeBuilder
    
    
    private void readRegistry() throws IOException {
        String scriptDirectory = System.getProperty("CSX_SCRIPTDIR");
        if (scriptDirectory == null)
            scriptDirectory = defaultScriptDirectory;
        String csx_home = System.getProperty("CSX_HOME");
        if (csx_home == null)
            throw new IOException("CSX_HOME undefined");
        File scriptDir = new File(csx_home+"/"+scriptDirectory);
        File[] scriptFiles = scriptDir.listFiles(new XMLFileNameFilter());
        for (int i=0; i<scriptFiles.length; i++) {
            try {
                readScript(scriptFiles[i]);
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
    }  //- readRegistry
    
    
    private void readScript(File scriptFile)
            throws IOException, SAXException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        
        InputSource source = new InputSource(new FileReader(scriptFile));
        DOMValidator handler = new DOMValidator();
        builder.setErrorHandler(handler);
        
        Document doc = builder.parse(source);
        if (handler.validationError)
            throw handler.saxParseException;
        
        RemoteTaskScript script =
            new RemoteTaskScript(doc, auxThreadPool, serviceRegistry);
        ObjectPool<RemoteTaskScript> pool = new ObjectPool<RemoteTaskScript>();
        for (int i=0; i<poolSize; i++) {
            pool.add(script.newInstance());
        }
        System.out.println("Read script: " + script.getName());
        registry.put(script.getName(), pool);
        
    }  //- readScript
    
    
    public RemoteTaskScript getScript(String name) {
        RemoteTaskScript script = null;
        if (registry.containsKey(name)) {
            script = registry.get(name).lease();
        }
        return script;
    }
    
    public void returnScript(RemoteTaskScript script) {
        String name = script.getName();
        if (registry.containsKey(name)) {
            registry.get(name).returnObject(script);
        }
    }
    
}  //- class ScriptRegistry

