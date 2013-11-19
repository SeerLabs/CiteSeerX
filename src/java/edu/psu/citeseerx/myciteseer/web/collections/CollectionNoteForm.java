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

import edu.psu.citeseerx.myciteseer.domain.CollectionNote;

import java.io.Serializable;

/**
 * Command object to manipulate/obtain user input to be used by
 * CollectionNoteFormController
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class CollectionNoteForm implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 7054188163210195933L;
    private CollectionNote collectionNote;
	private String collectionName;
	private boolean newCollectionNote;

	public CollectionNoteForm() {
		collectionNote = new CollectionNote();
		this.newCollectionNote = true;
	} //- CollectionNoteForm
	
	
	public CollectionNoteForm(CollectionNote collectionNote) {
		this.collectionNote = collectionNote;
		this.newCollectionNote = false;
	} //- CollectionNoteForm
	
	
	public String getCollectionName() {
		return collectionName;
	} //- getCollectionName
	
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	} //- setCollectionName
	
	
	public CollectionNote getCollectionNote() {
		return collectionNote;
	} //- CollectionNote
	
	
	public void setCollectionNote(CollectionNote noteCollection) {
		this.collectionNote = noteCollection;
	} //- setCollectionNote


	public boolean isNewCollectionNote() {
		return newCollectionNote;
	} //- isNewCollectionNote
	
} //- class CollectionNoteForm
