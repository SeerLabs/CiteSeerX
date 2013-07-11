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
 * Data object transporting Venue statistics
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class VenueStat implements Comparable<VenueStat> {

    private String name;
    private float impact;
    private String url;
    
    public String getName() {
        return name;
    } //- getName

    public void setName(String name) {
        this.name = name;
    } //- setName
    
    public float getImpact() {
        return impact;
    } //- getImpact
    
    public void setImpact(float impact) {
        this.impact = impact;
    } //- setImpact
    
    public String getUrl() {
        return url;
    } //- getUrl
    
    public void setUrl(String url) {
        this.url = url;
    } //- setUrl
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(VenueStat otherStat) {
        if (this.getImpact() > otherStat.getImpact()) return 1;
        if (this.getImpact() < otherStat.getImpact()) return -1;
        return 0;
    } //- compareTo
    
} //- class VenueStat
