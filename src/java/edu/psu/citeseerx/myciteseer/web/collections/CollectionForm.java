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
package edu.psu.citeseerx.myciteseer.web.collections;

import java.util.List;

import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import java.io.Serializable;

/**
 * Command object to manipulate/obtain user input to be used by
 * CollectionFormController
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class CollectionForm implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 7473761433213982293L;
    private Collection collection;
	private List<Collection> collections;
	private boolean hasCollections = false;
	private boolean addPaper = false;
	private String paperID;
	private boolean newCollection;
	
	public CollectionForm() {
		collection = new Collection();
		collection.setUsername(MCSUtils.getLoginAccount().getUsername());
		newCollection = true;
		// It's only false for System defined collections.
		collection.setDeleteAllowed(true);
	} //- CollectionForm
	
	public CollectionForm(Collection collection) {
		this.collection = collection;
		newCollection = false;
	} //- CollectionForm
	
	public List<Collection> getPreviousCollections() {
		return collections;
	} //- getPreviousCollections
	
	public void setPreviousCollections(List<Collection> collections) {
		this.collections = collections;
	} //- setPreviousCollections
	
	public Collection getCollection() {
		return collection;
	} //- getCollection
	
	public boolean isAddPaper() {
		return addPaper;
	} //- isAddPaper

	public void setAddPaper(boolean addPaper) {
		this.addPaper = addPaper;
	} //- setAddPaper

	public String getPaperID() {
		return paperID;
	} //- getPaperID

	public void setPaperID(String paperID) {
		this.paperID = paperID;
	} //- setPaperID

	public void setCollection(Collection collection) {
		this.collection = collection;
	} //- setCollection

	public boolean getHasCollections() {
		return hasCollections;
	} //- getHasCollections

	public void setHasCollections(boolean hasCollections) {
		this.hasCollections = hasCollections;
	} //- setHasCollections

	public boolean isNewCollection() {
		return newCollection;
	} //- isNewCollection
} //- class CollectionForm
