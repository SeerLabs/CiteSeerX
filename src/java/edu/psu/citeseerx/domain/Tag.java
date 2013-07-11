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

/**
 * Container class for user-supplied tag data.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class Tag {

    private String tag;
    private int count = 0;
    
    public String getTag() {
        return tag;
    } //- getTag
    
    public void setTag(String tag) {
        this.tag = tag;
    } //- setTag
    
    public int getCount() {
        return count;
    } //- getCount
    
    public void setCount(int count) {
        this.count = count;
    } //- setCount
    
} //- class Tag
