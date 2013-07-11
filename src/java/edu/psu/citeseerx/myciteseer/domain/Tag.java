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
package edu.psu.citeseerx.myciteseer.domain;

import java.io.Serializable;

/**
 * Data transfer object with Tag information.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class Tag implements Comparable<Tag>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -418724016014261199L;
    
    private String tag;
    private int count = 1;
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void incrCount() {
        count++;
    }
    public int getCount() {
        return count;
    }
    
    public int compareTo(Tag o) throws ClassCastException {
        String ttagstr = this.getTag().toLowerCase();
        String otagstr = o.getTag().toLowerCase();
        return ttagstr.compareTo(otagstr);
    }
    
}  //- class Tag
