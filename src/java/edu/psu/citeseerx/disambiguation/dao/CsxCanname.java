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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.psu.citeseerx.utility.FreqTable;
import edu.psu.citeseerx.utility.StringUtil;

/**
 * CsxCanname
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CsxCanname {

	public static final int TYPE_NOISE     = 1;
	public static final int TYPE_SINGLETON = 2;

	int cid;
	String canname, fname, mname, lname;
	String url, address, email;
	int ndocs, ncites, hindex;

	List<String> affils = new ArrayList<String>();

	/////////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	public CsxCanname(int cid) { this.cid = cid; }
	public CsxCanname(Connection conn, int cid) throws SQLException {
		PreparedStatement selectCID = 
		    conn.prepareStatement("SELECT * FROM cannames WHERE id=?");
		selectCID.setInt(1, cid);

		ResultSet rs = selectCID.executeQuery();
		rs.next();
		init(rs);
		rs.close();
	}
	public CsxCanname(ResultSet rs) throws SQLException {
		init(rs);
	}

	private void init(ResultSet rs) throws SQLException {
		this.cid = rs.getInt("id");
		this.canname = rs.getString("canname");
		this.fname   = rs.getString("fname");
		this.mname   = rs.getString("mname");
		this.lname   = rs.getString("lname");
		this.url     = rs.getString("url");
		this.address = rs.getString("address");
		this.email   = rs.getString("email");
		
		this.ndocs  = rs.getInt("ndocs");
		this.ncites = rs.getInt("ncites");
		this.hindex = rs.getInt("hindex");
		
		String affil = rs.getString("affil");
		if (affil != null) 
			affils.add(affil);
		if (null != (affil = rs.getString("affil2")))
			affils.add(affil);
		if (null != (affil = rs.getString("affil3")))
			affils.add(affil);
	}
	// END Constructors
	/////////////////////////////////////////////////////////////////////////////////////////
	
	public int getCid() { return this.cid; }
	public String getCanname() { return this.canname; }
	public String getMiddleName() { return this.mname; }
	public String getAffil() { 
		if (this.affils.size() > 0)
			return this.affils.get(0); 
		else return null;
	}
	public int getNdocs() { return this.ndocs; }
	
	private static String insertCanname = 
		"INSERT INTO cannames(id, canname, fname, mname, lname, email, url, " +
		"ndocs, ncites, hindex, affil, affil2, affil3)" + 
		" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public PreparedStatement getInsertStatement(Connection conn) 
	throws SQLException {
		PreparedStatement createCID = conn.prepareStatement(insertCanname);
		
		createCID.setInt(1, cid);
		createCID.setString(2, canname); // canname
		createCID.setString(3, fname);
		createCID.setString(4, mname);
		createCID.setString(5, lname);
		createCID.setString(6, email);
		createCID.setString(7, url);
		createCID.setInt(8, ndocs);
		createCID.setInt(9, ncites);
		createCID.setInt(10, hindex);
		for (int i = 0; i < 3; i++) { // 11, 12, 13
			String affil = null;
			if (i < this.affils.size())
				affil = this.affils.get(i);
			createCID.setString(11+i, affil);
		}

		return createCID;
	}
	private static String updateCanname = 
		"UPDATE cannames SET canname=?, fname=?, mname=?, lname=?, " +
		"email=?, url=?, ndocs=?, ncites=?, hindex=?, affil=?, affil2=?, " +
		"affil3=? WHERE id=?";
	public void updateToDB(Connection conn) throws SQLException {
		PreparedStatement updateCID = conn.prepareStatement(updateCanname);
		
		updateCID.setString(1, canname); // canname
		updateCID.setString(2, fname);
		updateCID.setString(3, mname);
		updateCID.setString(4, lname);
		updateCID.setString(5, email);
		updateCID.setString(6, url);
		updateCID.setInt(7, ndocs);
		updateCID.setInt(8, ncites);
		updateCID.setInt(9, hindex);
		for (int i = 0; i < 3; i++) { // 10, 11, 12
			String affil = null;
			if (i < this.affils.size())
				affil = this.affils.get(i);
			updateCID.setString(10+i, affil);
		}
		
		updateCID.setInt(13, cid);
		updateCID.executeUpdate();
	}
	

	/////////////////////////////////////////////////////////////////////////////////////////
	// START Update Functions	
	//        - calculate new information for a given canname (cid)
	//  

	private static String selectAuthorsByCID = "SELECT a.*, p.year as pyear, " +
			"p.cluster as pcluster FROM authors a JOIN papers p ON " +
			"a.paperid=p.id WHERE a.cluster = ? ORDER BY pyear";
	public void updateInfo(Connection conn) throws SQLException {
		PreparedStatement selectAID = conn.prepareStatement(selectAuthorsByCID);
		selectAID.setInt(1, this.cid);
		
		FreqTable cnames = new FreqTable();
		FreqTable fnames = new FreqTable();
		FreqTable mnames = new FreqTable();
		FreqTable lnames = new FreqTable();
		FreqTable emails = new FreqTable();
		FreqTable affils = new FreqTable();
		Set<Integer> docs = new HashSet<Integer>();
		Map<String,Integer> yearOf = new HashMap<String,Integer>();

		ResultSet rs = selectAID.executeQuery();
		while (rs.next()) {
			cnames.addCount(rs.getString("name").trim());
			
			CsxAuthor author = new CsxAuthor(rs);
			fnames.addCount(author.getFirstName());
			mnames.addCount(author.getMiddleName());
			lnames.addCount(author.getLastName());
			emails.addCount(author.getEmail());
			String affilStr = author.getAffil();
			
			docs.add(rs.getInt("pcluster"));
			
			if (affilStr != null) {
				affilStr = affilStr.replaceAll("Department","Dept.");
				affilStr = 
				    StringUtil.cleanUpAffil(affilStr).replaceAll(";",",");

				if (!affilStr.equals("")) {
					affils.addCount(affilStr);
					
					Integer year = rs.getInt("pyear");
					if ((year == null) || (year > 2015) || (year < 1950))
						year = new Integer(1950);
					yearOf.put(affilStr, year);
				}
			}
		}
		rs.close();
		updateInfo(cnames, fnames, mnames, lnames, emails, affils, docs, 
		        yearOf);
		updateNcitesAndHindex(conn);
	}

	public void updateInfo(FreqTable cnames, FreqTable fnames, FreqTable mnames,
	        FreqTable lnames, FreqTable emails, FreqTable affils, 
	        Set<Integer> docs, final Map<String,Integer> yearOf) {
		this.fname = fnames.getMostFreq();
		this.mname = mnames.getMostFreq();
		this.lname = lnames.getMostFreq();
		this.email = emails.getMostFreq();

		this.canname = cnames.getMostFreq();
		if ((mname != null) && (mnames.getCount(mname) <= 1))
			mname = null;

		this.ndocs = docs.size();

		updateAffils(affils, yearOf);
	}

	private static String selectCitations = 
		"SELECT p.ncites FROM cannames AS c" + 
		" JOIN authors AS a ON c.id=a.cluster" + 
		" JOIN papers AS p ON a.paperid=p.id" + 
		" WHERE c.id=? group BY p.cluster ORDER BY p.ncites DESC";
	public void updateNcitesAndHindex(Connection conn) throws SQLException {
		PreparedStatement selectCites = conn.prepareStatement(selectCitations);
		selectCites.setInt(1, this.cid);

		this.ncites = 0;
		this.hindex = 0;

		ResultSet rs = selectCites.executeQuery();
		while (rs.next()) {
			int cur_ncites = rs.getInt("ncites");
			if (cur_ncites == 0)
				break;
			this.ncites += cur_ncites;
			if (hindex < cur_ncites) {
				this.hindex++;
			}
		}
		rs.close();
	}

	public void updateAffils(FreqTable affils, 
	        final Map<String,Integer> yearOf) {		
		// select the best 3 affil
		List<String> most_affils = affils.getSortedKey();
		List<String> selected_affils = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			if (i <  most_affils.size()) {
				String key = most_affils.get(i);
				String s1  = key.replaceAll(",", "");
				if ((i != 0) && (affils.getCount(key) < 2))
					break;
				boolean dup = false;
				for (String selected : selected_affils) {
					String s2 = selected.replaceAll(",", "");
					if ((s2.indexOf(s1) >= 0) || (s1.indexOf(s2) >= 0)) {
						dup = true;
						break;
					}
				}
				if (!dup)
					selected_affils.add(key);
			}
		}
		Collections.sort(selected_affils, new Comparator<String>() {
				public int compare(String s1, String s2) {
					Integer v1 = yearOf.get((String)s1);
					Integer v2 = yearOf.get((String)s2);
					return v2.compareTo(v1);
				}
			});
		this.affils.clear();
		for (int i = 0; i < 3; i++) {
			if (i < selected_affils.size())
				this.affils.add(selected_affils.get(i));
		}
	}
	// END Update Functions
	/////////////////////////////////////////////////////////////////////////////////////////


	public String toString() {
		String xml = "<doc>\n";
		xml += "  <field name=\"id\">" + cid + "</field>\n";
		if (canname != null)
			xml += "  <field name=\"canname\">" + StringUtil.formatXML(canname) 
			+ "</field>\n";
		if (email != null)
			xml += "  <field name=\"email\">"   + StringUtil.formatXML(email) 
			+ "</field>\n";
		xml += "  <field name=\"ndocs\">"   + ndocs + "</field>\n";
		xml += "  <field name=\"ncites\">"  + ncites + "</field>\n";
		xml += "  <field name=\"hindex\">"  + hindex + "</field>\n";
		for (String affil : affils) {
			xml += "  <field name=\"affil\">"   + StringUtil.formatXML(affil) +
			"</field>\n";
		}
		/*
		  for (String varname : varnames) {
		    xml += "<field name=\"varname\">" + varname + "</field>\n";
		  }
		*/
		xml += "</doc>\n";

		return xml;
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	//
	// Process...
	//

	// merge Canname with src_cid ==> tar_cid...
	private static String UPDATE_AUTH_CID = 
	    "UPDATE authors SET cluster=? WHERE cluster=?";
	private static String DELETE_CID      = "DELETE FROM cannames WHERE id=?";		

	public static void mergeCannames(Connection conn, int tar_cid, int src_cid)
	throws SQLException {
		CsxCanname src = new CsxCanname(conn, src_cid);
		System.out.println(">> DELETE");
		System.out.println(src);

		// update cluster in authors table from src_cid to tar_cid
		PreparedStatement updateAuthorCid = 
		    conn.prepareStatement(UPDATE_AUTH_CID);
		updateAuthorCid.setInt(1, tar_cid); 
		updateAuthorCid.setInt(2, src_cid);
		updateAuthorCid.executeUpdate();
		
		// delete canname with src_cid
		PreparedStatement deleteCid = conn.prepareStatement(DELETE_CID);
		deleteCid.setInt(1, src_cid);
		deleteCid.executeUpdate();

		// recalculate cid1 ... 
		CsxCanname target = new CsxCanname(tar_cid);
		target.updateInfo(conn);
		target.updateToDB(conn);

		System.out.println(target);
	}

	// set all the authors in a canname cluster to noise....
	public static void deleteCanname(Connection conn, int cid, int noise_type) 
	throws SQLException {
		CsxCanname src = new CsxCanname(conn, cid);
		System.out.println(">> DELETE");
		System.out.println(src);

		PreparedStatement updateAuthorCid = 
		    conn.prepareStatement(UPDATE_AUTH_CID);
		updateAuthorCid.setInt(1, noise_type);
		updateAuthorCid.setInt(2, cid);
		updateAuthorCid.executeUpdate();

		PreparedStatement deleteCid = conn.prepareStatement(DELETE_CID);
		deleteCid.setInt(1, cid);
		deleteCid.executeUpdate();
	}

	public static void updateAllCannames(Connection conn) throws SQLException {		
		int min_cid =  300001;
		int max_cid =  309352; //90000;
	  //int max_cid =  309352;
		for (int cid = min_cid; cid <= max_cid; cid++) {
			CsxCanname cname = new CsxCanname(cid);
			try {
				cname.updateInfo(conn);
				cname.updateToDB(conn);
				if ((cid % 1000) == 0)
					System.out.println(">>> " + cid);
			} catch (Exception e) {
				System.out.println("ERROR: " + cid);
				e.printStackTrace();
			}
		}
	}

	public static void matchUrl(Connection conn, String url_file) 
	throws Exception {
		BufferedReader reader = new BufferedReader(
		        new FileReader(new File(url_file)));
		reader.readLine(); // throw away the first line (header)
		
		PreparedStatement selectCID = 
		    conn.prepareStatement("SELECT * FROM cannames WHERE lname=? AND " +
		    		"fname=?");
		PreparedStatement updateURL = 
		    conn.prepareStatement("UPDATE cannames SET url=? WHERE id=?");
		
		int count       = 0;
		int count_guess = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			String[] cols = line.split("\t");
			// canname affil   email   url     ndocs   hindex
			String name   = cols[0];
			String affil  = cols[1];
			String email  = cols[2];
			if (email != null)
				email = StringUtil.cleanUpEmail(email);
			String url    = cols[3];
			String ndocs  = cols[4];
			String hindex = cols[5];
			// 20670 => single match, 9000+ double matches
			if (!url.equals("NULL")) { // only 51,795 out of 844,950 have URLs
				count ++;
				CsxAuthor auth = new CsxAuthor();
				auth.setName(name);

				selectCID.setString(1, auth.getLastName());
				selectCID.setString(2, auth.getFirstName());

				ResultSet rs = selectCID.executeQuery();
				List<CsxCanname> list = new ArrayList<CsxCanname>();
				while (rs.next()) {
					CsxCanname canname = new CsxCanname(rs);
					list.add(canname);
				}
				rs.close();

				if (list.size() == 0) {
					//System.out.println(">>" + url);
					//System.out.println(auth.getFirstName() + "\t" + auth.getMiddleName() + "\t" + auth.getLastName() + "\t[" + affil + "]\t" + email);
				}
				else if (list.size() == 1) {
					CsxCanname canname = list.get(0);
					updateURL.setString(1, url);
					updateURL.setInt(2, canname.cid);
					updateURL.executeUpdate();
				}
				else if (list.size() > 1) {
					boolean found = false;
					for (CsxCanname canname : list) {
						if ((canname.email != null) && 
						        (canname.email.equals(email))) {
							updateURL.setString(1, url);							
							updateURL.setInt(2, canname.cid);
							updateURL.executeUpdate();
							found = true;
							break;
						}
					}
					// use affil to match
					if (!found && (affil != null) && (affil.length() > 5)) {
						//count_guess += 1;
						// 3984
						affil = affil.replaceAll("Department", "Dept.");
						affil = 
						    StringUtil.cleanUpAffil(affil).replaceAll(";",",");
						affil = affil.replaceAll(",","");
						boolean has_guess = false;
						for (CsxCanname canname : list) {
							for (String s1 : canname.affils) {
								s1 = s1.replaceAll(",", "");
								if ((s1.indexOf(affil) >= 0) || 
								        (affil.indexOf(s1) >= 0)) {
									has_guess = true;
									break;
								}
							}
							if (has_guess) {
								updateURL.setString(1, url);								
								updateURL.setInt(2, canname.cid);
								updateURL.executeUpdate();
								count_guess += 1;
								break;
							}
						}
					}
				}
				
				//System.out.println(">>" + url);
				//System.out.println(auth.getFirstName() + "\t" + auth.getMiddleName() +
				//				   "\t" + auth.getLastName() + "\t[" + affil + "]\t" + email);
			}
		}
		System.out.println("TOTAL:" + count_guess);
		reader.close();
	}

	public static void main(String[] args) throws Exception {
		String cmd = args[0];

		String connectionURL ="jdbc:mysql://localhost:3306/citeseerx";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = 
		    DriverManager.getConnection(connectionURL,"csx-devel","csx-devel");

		if (cmd.equals("update")) {
			if (args.length != 2) {
				System.out.println("Usage: java " +
						"edu.psu.seerlab.dao.csx.CsxCanname update <cid>");
				System.exit(0);
			}
			int cid = Integer.parseInt(args[1]);
			CsxCanname cname = new CsxCanname(cid);
			
			cname.updateInfo(conn);
			cname.updateToDB(conn);
			
			System.out.println(cname);
		}
		else if (cmd.equals("merge")) {
			if (args.length != 3) {
				System.out.println("Usage: java " +
						"edu.psu.seerlab.dao.csx.CsxCanname merge " +
						"<tar_cid> <src_cid>");
				System.exit(0);
			}

			int tar_cid = Integer.parseInt(args[1]);
			int src_cid = Integer.parseInt(args[2]);
			
			mergeCannames(conn, tar_cid, src_cid);
		}
		else if (cmd.equals("delete")) {
			if (args.length != 3) {
				System.out.println("Usage: java " +
						"edu.psu.seerlab.dao.csx.CsxCanname delete <cid> " +
						"<nose_type>");
				System.exit(0);
			} 
			int cid     = Integer.parseInt(args[1]);
			String type = args[2];
			int noise_type = TYPE_NOISE;
			if (type.equals("single"))
				noise_type = TYPE_SINGLETON;
			else if (type.equals("noise"))
				noise_type = TYPE_NOISE;
			deleteCanname(conn, cid, noise_type);
		}
		else if (cmd.equals("update_all")) {
			updateAllCannames(conn);
		}
		else if (cmd.equals("match_url")) {
			if (args.length != 2) {
				System.out.println("Usage: java " +
						"edu.psu.seerlab.dao.csx.CsxCanname match_url " +
						"<url_data.txt>");
				System.exit(0);
			}
			String url_file = args[1];
			matchUrl(conn, url_file);
		}
	}
}