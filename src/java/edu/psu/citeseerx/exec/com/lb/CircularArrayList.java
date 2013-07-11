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
package edu.psu.citeseerx.exec.com.lb;

import java.util.*;

public class CircularArrayList<E> {
    
    protected int currentNode = 0;
    protected ArrayList<E> nodeList = new ArrayList<E>();
    
    public int getSize() {
        return nodeList.size();
    }
    
    
    /**
     * Returns the object currently indexed in the list and increments
     * the index to point to the next object in sequence. 
     */
    public E nextElement() {
        E obj = null;
        synchronized(nodeList) {
            if (nodeList.size() > 0) {
                obj = nodeList.get(incrementCurrentNode());
            }
        }
        return obj;
        
    }  //- getNext
    
    
    /**
     * Inserts the specified object into the list.  A single object
     * may be referenced only once in the list, so if the object is
     * already contained, nothing further is done.
     * @param obj
     */
    public void put(E obj) {
        synchronized(nodeList) {
            if (nodeList.contains(obj)) {
                return;
            } else {
                nodeList.add(obj);
            }
        }
        
    }  //- put
    
    
    /**
     * Removes the specified object from the list and adjusts the list
     * state accordingly.  Does nothing if the object is not contained
     * in the list.
     * @param obj
     */
    public void remove(E obj) {
        synchronized(nodeList) {
            nodeList.remove(obj);
            if (currentNode >= nodeList.size()) {
                currentNode = 0;
            }
        }
        
    }  //- remove
    
    
    /**
     * Safely increments the current node index.
     * @return the old node index
     */
    private int incrementCurrentNode() {
        int priorIndex = currentNode;
        if (currentNode >= (nodeList.size() - 1)) {
            currentNode = 0;
        } else {
            currentNode++;
        }
        return priorIndex;
        
    }  //- incrementCurrentNode
    
}  //- class CircularArrayList
