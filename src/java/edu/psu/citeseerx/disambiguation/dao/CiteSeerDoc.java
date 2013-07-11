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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CiteSeerDoc
 * 
 * @author Puck Treeratpituk
 * @version $Rev$ $Date$
 */
public class CiteSeerDoc extends Citation {

	public CiteSeerDoc() {  }
	public CiteSeerDoc(String doi, String title) {
		this.id 	= doi;
		this.title	= title;
	}
	
	public CiteSeerDoc(Connection conn, String doi) throws SQLException {
		Statement st = conn.createStatement();
		String query = "SELECT * FROM papers WHERE id='" + doi + "'";
		ResultSet rs = st.executeQuery(query);
		
		if (rs.next()) {
			this.id    = rs.getString("id");			
			this.title = rs.getString("title");
			
			String auth_query = "SELECT * FROM authors WHERE paperid='" + 
			    this.id + "' ORDER BY ord";
			
			ResultSet auths   = st.executeQuery(auth_query);
			while (auths.next()) {
				CsxAuthor csxauth = new CsxAuthor(auths);
				authors.add(csxauth);
			}
		}
	}

	public CiteSeerDoc(Connection conn, int aid) throws SQLException {
		Statement st = conn.createStatement();
		String query = "SELECT * FROM papers" + 
			" WHERE id=(SELECT paperid FROM authors WHERE id=" + aid + ")";
		ResultSet rs = st.executeQuery(query);
		
		if (rs.next()) {
			this.id    = rs.getString("id");			
			this.title = rs.getString("title");
			
			String auth_query = "SELECT * FROM authors WHERE paperid='" 
			    + this.id + "' ORDER BY ord";
			ResultSet auths   = st.executeQuery(auth_query);
			while (auths.next()) {
				CsxAuthor csxauth = new CsxAuthor(auths);
				authors.add(csxauth);
			}
		}
	}
		

	public static List<CiteSeerDoc> initDocCollection(Connection conn, 
	        String query) throws SQLException {
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		
		List<CiteSeerDoc> collection = new ArrayList<CiteSeerDoc>();
		while (rs.next()) {
			String id 	 = rs.getString("id");
			String title = rs.getString("title");

			CiteSeerDoc doc = new CiteSeerDoc(id, title);
			collection.add(doc);
		}
		
		for (CiteSeerDoc doc: collection) {
			String auth_query = "SELECT * FROM authors WHERE paperid='" 
			    + doc.id + "'";
			ResultSet auths = st.executeQuery(auth_query);
			while (auths.next()) {
				CsxAuthor csxauth = new CsxAuthor(auths);
				doc.authors.add(csxauth);
			}
		}
		
		return collection;
	}
	
	// return affiliation of the 1st author...
	public String getAffiliation() {
	    if (getNumAuthors() == 0)
	        return "";
	    Author auth = this.authors.get(0); // get the first author
	    return auth.getAffil();
	}
	
	public String toString() {
		String ret = "";
		
		ret += "<Doc id='" + id + "'>\n";
		ret += "\t<Title>" + title + "</Title>\n";
		for (Author auth: this.authors) {
			CsxAuthor csxauth = (CsxAuthor)auth;
			ret += csxauth;
		}
		ret += "</Doc>";
		return ret;
	}
	
	public static void main(String[] args) throws Exception {
		String connectionURL ="jdbc:mysql://localhost:3306/citeseerx";

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = 
		    DriverManager.getConnection(connectionURL,"csx-devel","csx-devel");
		
		//CiteSeerDoc doc = new CiteSeerDoc(conn, 3135060);
		CiteSeerDoc doc = new CiteSeerDoc(conn, args[0]);
		
		System.out.println(doc);
		System.out.println("-------------------------");
	}
}