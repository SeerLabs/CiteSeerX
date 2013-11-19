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

import java.util.Comparator;
import java.util.List;
import java.io.Serializable;

import edu.psu.citeseerx.domain.ThinDoc;

/**
 * Data transfer object with paper information.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class Paper implements Comparable<Paper>, Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -8611825003497312347L;
    
    private ThinDoc doc;
	private String  coins;
	
	// Notes given to this paper by a user within a given collection
	private List<PaperNote> notes;
	
	public Paper() {
	} //- Paper
	
	/**
	 * @return The document info
	 */
	public ThinDoc getDoc() {
		return doc;
	} //-getDoc

	/**
	 * @param doc Information about a document
	 */
	public void setDoc(ThinDoc doc) {
		this.doc = doc;
	} //- setDoc

	/**
	 * @return COinS representation of the document.
	 */
	public String getCoins() {
		return coins;
	} //- getCoins

	/**
	 * @param coins COinS representation of this paper.
	 */
	public void setCoins(String coins) {
		this.coins = coins;
	} //- setCoins

	public List<PaperNote> getNotes() {
		return notes;
	} //- getNotes

	public void setNotes(List<PaperNote> notes) {
		this.notes = notes;
	} //- setNotes

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Paper anotherPaper) throws ClassCastException {
		if (!(anotherPaper instanceof Paper)) {
			throw new ClassCastException("Paper object expected.");
		}
		String anotherTitle = anotherPaper.getDoc().getTitle().toLowerCase();
		return this.getDoc().getTitle().toLowerCase().compareTo(anotherTitle);
	}
	
	public static Comparator<Paper> dateComparator = new Comparator<Paper>() {
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Paper paper, Paper anotherPaper) {
			Integer year = paper.getDoc().getYear();
			Integer anotherYear = anotherPaper.getDoc().getYear();
			return year.compareTo(anotherYear);
		}
	};
	
	public static Comparator<Paper> titleComparator = new Comparator<Paper>() {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Paper paper, Paper anotherPaper) {
			String tPaperTitle = paper.getDoc().getTitle().toLowerCase();
			String oPaperTitle = 
				anotherPaper.getDoc().getTitle().toLowerCase();
			return tPaperTitle.compareTo(oPaperTitle);
		}
	};
	
	public static Comparator<Paper> citesComparator = new Comparator<Paper>() {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Paper paper, Paper anotherPaper) {
			Integer pCites = paper.getDoc().getNcites();
			Integer oCites = anotherPaper.getDoc().getNcites();
			return  pCites.compareTo(oCites);
		}
	};
} //- class Paper
