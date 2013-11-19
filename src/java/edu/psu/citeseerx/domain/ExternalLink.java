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
 * External Link data carrier.
 *
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ExternalLink {
    private String paperID;
    private String label;
    private  String url;
    
    public String getPaperID() {
        return paperID;
    } //- getPaperID
    
    public void setPaperID(String paperID) {
        this.paperID = paperID;
    } //- setPaperID
    
    public String getLabel() {
        return label;
    } //- getLabel
    
    public void setLabel(String label) {
        this.label = label;
    } //- setLabel
    
    public String getUrl() {
        return url;
    } //- getUrl
    
    public void setUrl(String url) {
        this.url = url;
    } //- setUrl
    
} //- class ExternalLink
