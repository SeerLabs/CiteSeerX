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
package edu.psu.citeseerx.exec.protocol;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jdom.Element;
import org.jdom.Namespace;

public abstract class TaskImpl extends Task {    
    
    public TaskImpl(String name){
        super(name);
        reflectAnnotations();
    }
        
    private final Properties inputMapping = new Properties();
    
    protected final Object getInput(String name) {
        return protocol.get(inputMapping.getProperty(name));
    }
    
    public void overrideInputMapping(String name, String newVal) {
        synchronized(inputMapping) {
            inputMapping.setProperty(name, newVal);
        }
    }

    
    private final Properties outputMapping = new Properties();
    
    protected final void setOutput(String name, Serializable item) {
        protocol.set(outputMapping.getProperty(name), item);
    }    
    
    public void overrideOutputMapping(String name, String newVal) {
        synchronized(outputMapping) {
            outputMapping.setProperty(name, newVal);
        }
    }
    
    
    private final Properties parameterMapping = new Properties();

    protected final Object getParameter(String name) {
        return protocol.getTaskParameter(parameterMapping.getProperty(name));
    }
    
    public void overrideParameterMapping(String name, String newVal) {
        synchronized(parameterMapping) {
            parameterMapping.setProperty(name, newVal);
        }
    }
    
    public boolean parameterOverrideExists(String name) {
        return protocol.taskParameterExists(name);
    }
    
    
    public void resetMappings() {
        synchronized(inputMapping) {
            for (Enumeration<Object> en = inputMapping.keys();
                    en.hasMoreElements(); ) {
                String key = (String)en.nextElement();
                inputMapping.setProperty(key, key);
            }
        }
        synchronized(outputMapping) {
            for (Enumeration<Object> en = outputMapping.keys();
                    en.hasMoreElements(); ) {
                String key = (String)en.nextElement();
                outputMapping.setProperty(key, key);
            }
        }
        synchronized(parameterMapping) {
            for (Enumeration<Object> en = parameterMapping.keys();
                    en.hasMoreElements(); ) {
                String key = (String)en.nextElement();
                parameterMapping.setProperty(key, key);
            }
        }
        
    }  //- resetMappings

    
    protected final List<Field> requiredInputs = new ArrayList<Field>();
    protected final List<Field> optionalInputs = new ArrayList<Field>();
    protected final List<Field> outputs = new ArrayList<Field>();
    protected final List<Field> parameters = new ArrayList<Field>();
    
    private void reflectAnnotations() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i=0; i<fields.length; i++) {
            Annotation[] annotations = fields[i].getAnnotations();
            for (int j=0; j<annotations.length; j++) {
                Class<?> type = annotations[j].annotationType();
                if (type == RequiredInput.class) {
                    requiredInputs.add(fields[i]);
                    String name = fields[i].getName();
                    inputMapping.setProperty(name, name);
                }
                if (type == OptionalInput.class) {
                    optionalInputs.add(fields[i]);
                    String name = fields[i].getName();
                    inputMapping.setProperty(name, name);
                }
                if (type == Output.class) {
                    outputs.add(fields[i]);
                    String name = fields[i].getName();
                    outputMapping.setProperty(name, name);
                }
                if (type == Parameter.class) {
                    parameters.add(fields[i]);
                    String name = fields[i].getName();
                    parameterMapping.setProperty(name, name);
                }
            }
        }
        
    }  //- reflectAnnotations
    
    
    public Element toXML(Namespace ns) {
        Element root = new Element("Task", ns);
        root.setAttribute("name", name);
        root.setAttribute("codebase", this.getClass().getName());
        for (Iterator<Field> it = requiredInputs.iterator(); it.hasNext(); ) {
            root.addContent(buildInput(it.next(), "required", ns));
        }
        for (Iterator<Field> it = optionalInputs.iterator(); it.hasNext(); ) {
            root.addContent(buildInput(it.next(), "optional", ns));
        }
        for (Iterator<Field> it = outputs.iterator(); it.hasNext(); ) {
            root.addContent(buildOutput(it.next(), ns));
        }
        return root;
        
    }  //- toXML
    
    
    private Element buildInput(Field field, String use, Namespace ns) {
        Element input = new Element("input", ns);
        input.setAttribute("id", field.getName());
        input.setAttribute("use", use);
        input.setText(field.getType().getName());
        return input;
        
    }  //- buildInput
    
    
    private Element buildOutput(Field field, Namespace ns) {
        Element output = new Element("output", ns);
        output.setAttribute("id", field.getName());
        output.setText(field.getType().getName());
        return output;
        
    }  //- buildOutput
    
    public abstract TaskImpl newInstance();
    
}  //- class TaskImpl
