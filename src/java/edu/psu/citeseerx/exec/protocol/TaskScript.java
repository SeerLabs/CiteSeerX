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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class TaskScript implements Runnable {

    protected String name;
    
    public String getName() {
        return name;
    }
    
    protected Protocol protocol;
    
    public Protocol getProtocol() {
        return protocol;
    }
    
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
        for (Iterator<ComponentTask> it = tasks.iterator(); it.hasNext(); ) {
            it.next().setProtocol(protocol);
        }
    }
    
    protected ThreadPoolExecutor auxThreadPool;
    
    public TaskScript(String name, ThreadPoolExecutor auxPool) {
        this.name = name;
        this.auxThreadPool = auxPool;
    }
    
    public TaskScript(String name) {
        this.name = name;
    }
    

    public TaskScript() {}
    
    private final Vector<ComponentTask> scriptDependencyList =
        new Vector<ComponentTask>();
    private ArrayList<Integer> scriptDependencyIndex =
        new ArrayList<Integer>();
    
    protected void parseTaskDoc(Document doc) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        Element root = doc.getDocumentElement();
        name = root.getAttribute("name");
        populateTasks(root);
        
        NodeList children = root.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();
            if (name.equals("Translator")) {
                readTranslator(child);
            }
            if (name.equals("Sequence")) {
                scriptDependencyIndex =
                    handleSequence(child, new ArrayList<Integer>());
            }
            if (name.equals("Parallel")) {
                scriptDependencyIndex =
                    handleParallel(child, new ArrayList<Integer>());
            }
        }
        buildScriptDependencies();

    }
    
    final Object notificationLock = new Object();
    
    protected void buildScriptDependencies() {
        for (Iterator<Integer> it = scriptDependencyIndex.iterator();
                it.hasNext(); ) {
            ComponentTask task = taskIndex.get(it.next());
            System.out.println(name+" dependency: " +task.baseTask.name);
            scriptDependencyList.add(task);
            task.addListener(notificationLock);
        }
                
    }

    
    protected final List<ComponentTask> tasks =
        new ArrayList<ComponentTask>();
    
    protected final Hashtable<Integer,ComponentTask> taskIndex =
        new Hashtable<Integer,ComponentTask>();


    protected void populateTasks(Element elt) throws TaskCollisionException {
        NodeList taskList = elt.getElementsByTagName("Task");
        for (int i=0; i<taskList.getLength(); i++) {
            Element taskElt = (Element)taskList.item(i);
            Integer id = Integer.decode(taskElt.getAttribute("id"));
            if (taskIndex.containsKey(id)) {
                throw new TaskCollisionException("collision: " + id.toString());
            }
            Task task = buildTask(taskElt);
            ComponentTask ctask = new ComponentTask(task, this);
            tasks.add(ctask);
            taskIndex.put(id, ctask);
        }
    }
    
    protected abstract Task buildTask(Element elt);
    
    class TaskCollisionException extends RuntimeException {
        /**
         * 
         */
        private static final long serialVersionUID = 5101749484363533027L;

        public TaskCollisionException(String msg){
            super(msg);
        }
    }
    
    
    protected Translator translator;

    protected void readTranslator(Node node) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        System.out.println("reading translator");
        String type = ((Text)node.getFirstChild()).getData();
        translator = (Translator)Class.forName(type).newInstance();
        if (translator == null)
            System.out.println("null translator: "+type);
    }
        
    protected ArrayList<Integer> handleSequence(Node sequence,
            ArrayList<Integer> dependencySet) {
        NodeList sequentialTasks = sequence.getChildNodes();
        ArrayList<Integer> futureDependencies = dependencySet;
        for (int i=0; i<sequentialTasks.getLength(); i++) {
            if (!(sequentialTasks.item(i) instanceof Element)) {
                continue;
            }
            Element elt = (Element)sequentialTasks.item(i);
            String nodeType = elt.getNodeName();
            if (nodeType.equals("Task")) {
                Integer id = Integer.decode(elt.getAttribute("id"));
                setDependencies(id, futureDependencies);
                futureDependencies.clear();
                futureDependencies.add(id);
            }
            if (nodeType.equals("Sequence")) {
                ArrayList<Integer> futures =
                    handleSequence(elt, futureDependencies);
                futureDependencies = futures;
            }
            if (nodeType.equals("Parallel")) {
                ArrayList<Integer> futures =
                    handleParallel(elt, futureDependencies);
                futureDependencies = futures;
            }
        }
        System.out.println("future dep size: " + futureDependencies.size());
        return futureDependencies;
        
    }
    
    protected ArrayList<Integer> handleParallel(Node parallel,
            ArrayList<Integer> dependencySet) {
        NodeList parallelTasks = parallel.getChildNodes();
        ArrayList<Integer> futureDependencies = new ArrayList<Integer>();
        for (int i=0; i<parallelTasks.getLength(); i++) {
            if (!(parallelTasks.item(i) instanceof Element)) {
                continue;
            }
            Element elt = (Element)parallelTasks.item(i);
            String nodeType = elt.getNodeName();
            if (nodeType.equals("Task")) {
                Integer id = Integer.decode(elt.getAttribute("id"));
                setDependencies(id, dependencySet);
                futureDependencies.add(id);
            }
            if (nodeType.equals("Sequence")) {
                ArrayList<Integer> futures = handleSequence(elt, dependencySet);
                futureDependencies.addAll(futures);
            }
            if (nodeType.equals("Parallel")) {
                ArrayList<Integer> futures = handleParallel(elt, dependencySet);
                futureDependencies.addAll(futures);
            }
        }
        return futureDependencies;
        
    }
    
    
    protected ArrayList<Integer> copyIntArrayList(ArrayList<Integer> array) {
        ArrayList<Integer> copy = new ArrayList<Integer>();
        for (Iterator<Integer> it = array.iterator(); it.hasNext(); ) {
            copy.add(new Integer(it.next().intValue()));
        }
        return copy;
    }
    
    
    protected Hashtable<Integer,ArrayList<Integer>> dependencyIndex =
        new Hashtable<Integer,ArrayList<Integer>>();

    
    protected void setDependencies(Integer taskID,
            ArrayList<Integer> dependencies) {
        ComponentTask task = taskIndex.get(taskID);
        for (Iterator<Integer> it = dependencies.iterator(); it.hasNext(); ) {
            ComponentTask dependency = taskIndex.get(it.next());
            task.addDependency(dependency, false);
        }
        dependencyIndex.put(taskID, copyIntArrayList(dependencies));
    }
    
    
    public void copyTasks(TaskScript otherScript) {
        
        this.translator = otherScript.translator;
        this.auxThreadPool = otherScript.auxThreadPool;
        
        Hashtable<Integer,ComponentTask> otherTaskIndex =
            otherScript.getTaskIndex();
        Enumeration<Integer> e = otherTaskIndex.keys();
        List<Integer> idList = Collections.list(e);
        Collections.sort(idList);
        Enumeration<Integer> sortedE = Collections.enumeration(idList);
        while (sortedE.hasMoreElements()) {
            Integer oldInt = sortedE.nextElement();
            Integer newInt = new Integer(oldInt.intValue());
            ComponentTask task = otherTaskIndex.get(oldInt).newInstance(this);
            taskIndex.put(newInt, task);
            tasks.add(task);
        }
        
        Hashtable<Integer,ArrayList<Integer>> otherDependencyIndex =
            otherScript.getDependencyIndex();
        for (Enumeration<Integer> en = otherDependencyIndex.keys();
                en.hasMoreElements(); ) {
            Integer oldInt = en.nextElement();
            Integer newInt = new Integer(oldInt.intValue());
            ArrayList<Integer> oldArrayList = otherDependencyIndex.get(oldInt);
            ArrayList<Integer> newArrayList = new ArrayList<Integer>();
            for (Iterator<Integer> it = oldArrayList.iterator();
                    it.hasNext(); ) {
                newArrayList.add(new Integer(it.next().intValue()));
            }
            dependencyIndex.put(newInt, newArrayList);
            setDependencies(newInt, newArrayList);
        }
        
        for (Iterator<Integer> it =
                otherScript.scriptDependencyIndex.iterator(); it.hasNext(); ) {
            scriptDependencyIndex.add(new Integer(it.next()).intValue());
        }
        buildScriptDependencies();
        
    }
    
    
    public List<ComponentTask> getTasks() {
        return tasks;
    }
    
    public Hashtable<Integer,ComponentTask> getTaskIndex() {
        return taskIndex;
    }
    
    public Hashtable<Integer,ArrayList<Integer>> getDependencyIndex() {
        return dependencyIndex;
    }
    

    /*protected static Hashtable parameterOverrides = new Hashtable();
    protected static Hashtable auxiliaryTasks = new Hashtable();
    protected List auxTaskCache = new ArrayList();*/
    
    
    public void run() {
        execute(auxThreadPool);
    }
    
    public void execute(ThreadPoolExecutor executor) {
        if (translator != null)
            translator.translate(protocol);
        else {
            System.out.println("null translator (execute)");
        }
        for (Iterator<ComponentTask> it = tasks.iterator();
                it.hasNext(); ) {
            ComponentTask task = it.next();
            System.out.println("Submitting " + task.baseTask.getName());
            executor.submit(task);
        }
        synchronized(notificationLock) {
            while (dependenciesNotMet()) {
                System.out.println(name+": waiting for results");
                try {
                    notificationLock.wait();
                } catch (InterruptedException e) {}
                System.out.println("got results");
            }
        }
        for (Enumeration<String> e = protocol.getDataKeys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            Object val = protocol.get(key);
            System.out.println("SCRIPTEND: " +key+":"+val);
        }
    }
    
    
    private boolean dependenciesNotMet() {
        System.out.println("Dep size: " + scriptDependencyList.size());
        for (Iterator<ComponentTask> it = scriptDependencyList.iterator();
                it.hasNext(); ) {
            if (!it.next().isComplete())
                return true;
        }
        return false;
    }
    
    public abstract TaskScript newInstance();
    
    public void reset() {
        for (Iterator<ComponentTask> it = tasks.iterator(); it.hasNext(); ) {
            it.next().reset();
        }
    }
    
}
