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
package edu.psu.citeseerx.domain;

import java.util.*;

/**
 * Super-class for MappedDataObjects that may contain provenance information,
 * allowing the source of mapped data values to be tracked.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public abstract class SourceableDataObject extends MappedDataObject {

    public SourceableDataObject() {
        super();
    } //- SourceableDataObject
    
    private HashMap<String,String> sourceMap;
    
    /**
     * Retrieves provenance info for the data indicated by tagName.
     * @param tagName
     * @return provenance info for the data indicated by tagName.
     */
    public String getSource(String tagName) {
        if (sourceMap == null) {
            return null;
        }
        return sourceMap.get(tagName);
    } //- getSource
    
    /**
     * Sets provenance info for the given data item. 
     * @param tagName
     * @param data
     */
    public void setSource(String tagName, String data) {
        if (sourceMap == null) {
            sourceMap = new HashMap<String,String>();
        }
        sourceMap.put(tagName, data);
    } //- setSource
    
    /**
     * Returns whether this object has attached provenance info.
     * @return true if the object  has attached provenance info false otherwise.
     */
    public boolean hasSourceData() {
        return (sourceMap != null);
    } //- hasSourceData
    
    /**
     * Returns whether this object has provenance info for the given data item.
     * @param key
     * @return true if the object  has attached provenance info the given data 
     * item, false otherwise.
     */
    public boolean hasSourceData(String key) {
        if (sourceMap == null) {
            return false;
        }
        return sourceMap.containsKey(key);
    } //- hasSourceData
    
    /**
     * Returns a list of keys for which this object has provenance info.
     * @return a list of keys for which this object has provenance info.
     */
    public List<String> getSourceKeys() {
        return new ArrayList<String>(sourceMap.keySet());
    } //- getSourceKeys
    
}  //- class SourceableDataObject
