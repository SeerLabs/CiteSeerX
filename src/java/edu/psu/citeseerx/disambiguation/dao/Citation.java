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
package edu.psu.citeseerx.disambiguation.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import edu.psu.citeseerx.utility.StringUtil;

/**
 * Citation
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class Citation {
	
    protected String id;
    protected String title;
	protected List<Author> authors = new ArrayList<Author>();

	public Document getDocument() {
		Document doc = new Document();

		doc.add(new Field("ID", id, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new 
		        Field("Title", title, Field.Store.YES, Field.Index.TOKENIZED));

		return doc;
	}

	public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }

	public String getId() { return this.id; }    
    public String getTitle() { return this.title; }

	public void addAuthor(Author author) { authors.add(author); }
    public int getNumAuthors() { return authors.size(); }
    
	// get author with the same first initial, ignore middle name...
	// also ignore accent ex. "Chadimová" and "Chadimova" are equals
	public Author getAuthor(String lastName, String initials) {
		char init = Character.toUpperCase(initials.charAt(0));

        for (int i = 0; i < authors.size(); i++) {
			Author auth = authors.get(i);
			if ((auth.lastName != null) && 
			        StringUtil.relaxStrEquals(lastName, auth.lastName) &&
			        (auth.firstName != null) && 
			        (auth.firstName.charAt(0) == init)) {
			    //auth.setOrder(i+1);
			    return auth;
			}
        }
        return null;
	}

	public Author getAuthorById(String id) { 
		for (int i = 0; i < authors.size(); i++) {
			Author auth = authors.get(i);
			if (auth.getId().equals(id))
				return auth;
		}
		return null;
	}

	public Author getAuthor(String lastName) {
        for (int i = 0; i < authors.size(); i++) {
			Author auth = authors.get(i);
			if ((auth.lastName != null) && 
			        StringUtil.relaxStrEquals(lastName, auth.lastName)) {
			    auth.setOrder(i+1);
			    return auth;
			}
        }
        return null;
	}
	public int getAuthOrder(String lastName, String initials) {
		char init = Character.toUpperCase(initials.charAt(0));
		
		for (int i = 0; i < authors.size(); i++) {
		    Author auth = authors.get(i);
			if ((auth.lastName != null) && 
			        StringUtil.relaxStrEquals(lastName, auth.lastName) &&
			        (auth.firstName != null) && 
			        (auth.firstName.charAt(0) == init)) {
				return i+1;
			}
		}
		return -1;
	}
	public String getLastNames() {
		String authorsStr = "";
		if (0 == authors.size())
			return "";
		
		authorsStr = (authors.get(0)).lastName;
		for (int i = 1; i < authors.size(); i++) {
			authorsStr += ", " + authors.get(i).lastName;
		}
		return authorsStr;
	}
	public String getAuthorStr() {
		String authorsStr = "";
		if (0 == authors.size())
			return "";
		
		authorsStr = (authors.get(0)).getName();
		for (int i = 1; i < authors.size(); i++) {
			authorsStr += ", " + authors.get(i).getName();
		}
		return authorsStr;
	}
    // use lastname & first initial as critiria...
	public List<Author> intersectAuthors(Citation cite) {
		List<Author> intersect = new ArrayList<Author>();
		
		Set<String> set = new HashSet<String>();
		for (Author author: cite.authors) {
			set.add(author.lastAndFirstInit());	
		}
		for (Author author: this.authors) {
			if (set.contains(author.lastAndFirstInit())) {
				intersect.add(author);
			}
		}
		return intersect;
	}
	
	public List<Author> intersectCoAuthors(Citation cite, String lastName) {
	    List<Author> intersect = new ArrayList<Author>();

		Author auth1 = this.getAuthor(lastName);
		Author auth2 = cite.getAuthor(lastName);

		Set<String> set = new HashSet<String>();
		for (Author coauth2: cite.authors) {
			if (coauth2 != auth2)
				set.add(coauth2.lastAndFirstInit());	
		}
		
		for (Author coauth1: this.authors) {
			if (coauth1 != auth1) {
				if (set.contains(coauth1.lastAndFirstInit())) {
					intersect.add(coauth1);
				}
			}
		}
		return intersect;
	}	
	// intersect CoAuthors...
    // 
    // use lastname & first initial as critiria...
	public List<Author> intersectCoAuthors(Citation cite, String lastName, 
	        String initials) {
		List<Author> intersect = new ArrayList<Author>();

		Author auth1 = this.getAuthor(lastName, initials);
		Author auth2 = cite.getAuthor(lastName, initials);

		Set<String> set = new HashSet<String>();
		for (Author coauth2: cite.authors) {
			if (coauth2 != auth2)
				set.add(coauth2.lastAndFirstInit());	
		}
		
		for (Author coauth1: this.authors) {
			if (coauth1 != auth1) {
				if (set.contains(coauth1.lastAndFirstInit())) {
					intersect.add(coauth1);
				}
			}
		}
		return intersect;
	}
	// intersect CoAuthors...
    // 
    // use lastname & first initial as critiria...
	public List<Author> intersectCoAuthors(Citation cite, int skip1, 
	        int skip2) {
		ArrayList<Author> intersect = new ArrayList<Author>();

		Set<String> set = new HashSet<String>();
		for (int i = 0; i < cite.authors.size(); i++) {
		    Author coauth2 = cite.authors.get(i);
            if (i != skip2)
				set.add(coauth2.lastAndFirstInit());
		}
		
        for (int i = 0; i < this.authors.size(); i++) {
            Author coauth1 = this.authors.get(i);
            if (i != skip1) {
				if (set.contains(coauth1.lastAndFirstInit())) {
					intersect.add(coauth1);
				}
            }
        }
		return intersect;
	}
	
	public String toString() {
		String ret = "";
		
		ret += "<Doc id=" + id + ">\n";
		ret += "\t<Title>" + title + "</Title>\n";
		for (Author auth: authors) {
			ret += auth;
		}
		ret += "</Doc>";
		return ret;		
	}
}