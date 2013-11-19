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
package edu.psu.citeseerx.myciteseer.web.admin;

import java.io.Serializable;
import java.util.List;

import edu.psu.citeseerx.domain.LinkType;

/**
 * Command object to manipulate/obtain user input to be used by
 * ExternalLinkFormFormController
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class EditExternalLinkForm implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -9103690355479458858L;
    private boolean newLinkType = false;
    private LinkType link;

    public LinkType getLink() {
        return link;
    } //- getELink

    public void setLink(LinkType link) {
        this.link = link;
    } //- setELink

    public EditExternalLinkForm(LinkType link) {
        this.link = link;
        newLinkType = false;
    } //- EditExternalLinkForm
    
    public EditExternalLinkForm() {
        link = new LinkType();
        newLinkType = true;
    } //- EditExternalLinkForm
    
    private List<LinkType> links;

    public List<LinkType> getLinks() {
        return links;
    } //- getLinks

    public void setLinks(List<LinkType> links) {
        this.links = links;
    } //- setLinks
    
    public boolean isNewLinkType() {
        return newLinkType;
    } //- isNewLinktype
    
    private String oldLabel = null;

    public String getOldLabel() {
        return oldLabel;
    } //- getOldLabel

    public void setOldLabel(String oldLabel) {
        this.oldLabel = oldLabel;
    } //- setOldLabel
        
} //- class EditExternalLinkForm
