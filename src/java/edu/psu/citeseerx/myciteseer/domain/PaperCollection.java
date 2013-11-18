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
 * Data transfer object with PaperCollection information.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class PaperCollection implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -4348083929718531215L;
    
    private long collectionID;
	private String paperID;
	private String UID;
	public long getCollectionID() {
		return collectionID;
	}
	public void setCollectionID(long collectionID) {
		this.collectionID = collectionID;
	}
	public String getPaperID() {
		return paperID;
	}
	public void setPaperID(String paperID) {
		this.paperID = paperID;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uid) {
		UID = uid;
	}
} //- class PaperCollection
