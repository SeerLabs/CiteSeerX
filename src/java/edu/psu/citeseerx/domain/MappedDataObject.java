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

import java.util.HashMap;
import edu.psu.citeseerx.utility.SafeText;

/**
 * Super-class for domain objects that support key-based data access.  Date
 * items can be retrieved by name much like a properties object.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public abstract class MappedDataObject {

    public static final boolean ENCODED = true;
    public static final boolean UNENCODED = false;
    
    protected static final String[] privateFieldData = {};
    protected HashMap<String,Boolean> privateFields =
        new HashMap<String,Boolean>();
    
    protected HashMap<String,String> data = new HashMap<String,String>();
    
    
    /**
     * Retrieve default unencoded version of the specified datum.
     * @param key
     * @return default unencoded version of the specified datum.
     */
    public String getDatum(String key) {
        return data.get(key);
    } //- getDatum
    
    /**
     * Retrieve a specified datum, in HTML-encoded or raw form.
     * @param key
     * @param encoded
     * @return  specified datum, in HTML-encoded or raw form.
     */
    public String getDatum(String key, boolean encoded) {
        if (encoded) {
            return SafeText.encodeHTMLSpecialChars(data.get(key)); 
        } else {
            return data.get(key);
        }
    } //- getDatum
    
    public void setDatum(String key, String val) {
        data.put(key, val);
    } //- setDatum

    protected void addPrivateField(String field) {
        privateFields.put(field, new Boolean(true));
    } //- addPrivateField
    
}  //- class MappedDataObject
