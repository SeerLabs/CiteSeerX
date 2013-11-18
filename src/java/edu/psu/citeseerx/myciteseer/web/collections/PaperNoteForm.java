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

import edu.psu.citeseerx.myciteseer.domain.PaperNote;

import java.io.Serializable;

/**
 * Command object to manipulate/obtain user input to be used by
 * PaperNoteFormController
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class PaperNoteForm implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 2079578357447454902L;
    private PaperNote paperNote;
	private String paperTitle;
	private String collectionName;
	private boolean newPaperNote;
	
	public PaperNoteForm() {
		paperNote = new PaperNote();
		newPaperNote = true;
	} //- PaperNoteForm
	public PaperNoteForm(PaperNote paperNote) {
		this.paperNote = paperNote;
		newPaperNote = false;
	} //- PaperNoteForm
	public PaperNote getPaperNote() {
		return paperNote;
	} //- getPaperNote
	public void setPaperNote(PaperNote paperNote) {
		this.paperNote = paperNote;
	} //- setPaperNote
	public String getPaperTitle() {
		return paperTitle;
	}
	public void setPaperTitle(String paperTitle) {
		this.paperTitle = paperTitle;
	} //- setPaperTitle
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	} //- setCollectionName

	public boolean isNewPaperNote() {
		return newPaperNote;
	} //- isNewPaperNote
	
} //- class PaperNoteForm
