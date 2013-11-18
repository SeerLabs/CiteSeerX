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
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import java.io.Serializable;

/**
 * Command object to manipulate/obtain user input to be used by
 * PaperCollectionFormController
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class PaperCollectionForm implements Serializable {
	
	/**
     * 
     */
    private static final long serialVersionUID = -1539147479695081053L;
    private PaperCollection paperCollection;
	private List<Collection> collections;
	private String paperTitle;
	
	public PaperCollectionForm() {
		paperCollection = new PaperCollection();
		paperCollection.setUID(MCSUtils.getLoginAccount().getUsername());
	} //- PaperCollectionForm
	
	public PaperCollectionForm(String paperTitle, String DOI) {
		paperCollection = new PaperCollection();
		paperCollection.setPaperID(DOI);
		paperCollection.setUID(MCSUtils.getLoginAccount().getUsername());
		this.paperTitle = paperTitle;
	} //- PaperCollectionForm
	
	public PaperCollection getPaperCollection() {
		return paperCollection;
	} //- PaperCollection

	public void setPaperCollection(PaperCollection paperCollection) {
		this.paperCollection = paperCollection;
	} //- setPaperCollection

	public List<Collection> getCollections() {
		return collections;
	} //- getCollections

	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	} //- setCollections

	public String getPaperTitle() {
		return paperTitle;
	} //- getPaperTitle

	public void setPaperTitle(String paperTitle) {
		this.paperTitle = paperTitle;
	} //- setPaperTitle

} //- class PaperCollectionForm
