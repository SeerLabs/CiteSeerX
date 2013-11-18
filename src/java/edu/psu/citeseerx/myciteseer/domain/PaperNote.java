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
 * Data transfer object with PaperNote information.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class PaperNote implements Serializable {
    
	/**
     * 
     */
    private static final long serialVersionUID = -6918516442846001643L;
    
    private long noteID;
	private long CID;
	private String PID;
	private String UID;
	private String note;
	public long getNoteID() {
		return noteID;
	}
	public void setNoteID(long noteID) {
		this.noteID = noteID;
	}
	public long getCID() {
		return CID;
	}
	public void setCID(long cid) {
		CID = cid;
	}
	public String getPID() {
		return PID;
	}
	public void setPID(String pid) {
		PID = pid;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uid) {
		UID = uid;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
} //- class PaperNote
