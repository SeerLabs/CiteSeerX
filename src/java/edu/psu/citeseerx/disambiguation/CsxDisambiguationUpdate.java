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
package edu.psu.citeseerx.disambiguation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.loaders.ContextReader;
import edu.psu.citeseerx.disambiguation.CsxAuthorBlock;
import edu.psu.citeseerx.disambiguation.dao.CsxAuthor;
import edu.psu.citeseerx.disambiguation.dao.CsxCanname;
import edu.psu.citeseerx.disambiguation.dao.CiteSeerDoc;
import edu.psu.citeseerx.utility.FreqTable;

/**
 * CsxDisambiguationUpdate
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CsxDisambiguationUpdate {
	
	public static final double EPS  = 0.1;
	public static final int MIN_PTS = 2;

	public static void run(String infile) throws Exception {
		ListableBeanFactory factory       = ContextReader.loadContext();
		DataSource dataSource = (DataSource)factory.getBean("csxDataSource");
		Connection conn = dataSource.getConnection();

		CsxAuthor auth  = new CsxAuthor();
		CiteSeerDoc doc = new CiteSeerDoc();

		loadInfoFromFile(infile, auth, doc);
		List<CsxCanname> cannames = loadCannames(conn, auth);

		List<Integer> aids = new ArrayList<Integer>();
		Map<Integer, Integer> cidOf = new HashMap<Integer, Integer>();
		Map<Integer, CsxCanname> cannameOf = new HashMap<Integer, CsxCanname>();

		PreparedStatement selectAuth = 
		    conn.prepareStatement("SELECT id FROM authors WHERE cluster=?");
		System.out.println("\n>>> CANDIDATES");
		System.out.println("NAME       \tNDOCS\tAFFILIATION");
		for (CsxCanname cname : cannames) {
			cannameOf.put(new Integer(cname.getCid()), cname);
			
			System.out.println(cname.getCanname() + "\t" + cname.getNdocs() + 
			        "\t" + cname.getAffil());
			selectAuth.setInt(1, cname.getCid());
			ResultSet rs = selectAuth.executeQuery();
			while (rs.next()) {
				Integer aid = new Integer(rs.getInt("id"));
				aids.add(aid);
				cidOf.put(aid, new Integer(cname.getCid()));
			}
			rs.close();
		}
		
		System.out.println("\n... LOADING MODELS");
		
		CsxAuthorBlock blocks = (CsxAuthorBlock)factory.getBean("csxAuthorBlock");
		blocks.loadAuthors(aids);
		CsxAuthorBlock.CsxAuthorPoint seed = 
		    blocks.new CsxAuthorPoint(auth, doc);

		System.out.println("\n... CALCULATING MATCHES");

		FreqTable freqTable = new FreqTable();
		Set<Integer> neighbors = blocks.getNeighbors(seed, EPS);
		for (Integer aid : neighbors) {
			Integer cid = cidOf.get(aid);
			//CsxCanname cname = cannameOf.get(cid);
			freqTable.addCount(cid.toString());
			//System.out.println(aid + "\t" + cname.getAffil());
		}

		List<String> sortedKeys = freqTable.getSortedKey();
		if (sortedKeys.size() > 0) {
			String best  = sortedKeys.get(0);
			Integer freq = freqTable.getCount(best);
			Integer cid = Integer.parseInt(best);
			CsxCanname cname = cannameOf.get(cid);
			System.out.println(">>> BEST MATCHED:");
			System.out.println("DENSE \tCID\tNAME       \tNDOCS\tAFFILIATION");
			System.out.println(freq + "\t" + cname.getCid() + "\t" + 
			        cname.getCanname() + "\t" + cname.getNdocs() + "\t" + 
			        cname.getAffil());
		}
		else {
			System.out.println("NO MATCH!!");
		}
		System.out.println("");
	}

	public static List<CsxCanname> loadCannames(Connection conn, 
	        CsxAuthor auth) throws SQLException {
		PreparedStatement selectCID;

		if (auth.getFirstName().length() == 1) {
			selectCID = conn.prepareStatement("SELECT * FROM cannames WHERE" +
					" fname like ? AND lname=?");
			selectCID.setString(1, auth.getFirstName() + "%");
			selectCID.setString(2, auth.getLastName());
		}
		else {
			selectCID = conn.prepareStatement("SELECT * FROM cannames WHERE " +
					"fname=? AND lname=?");
			selectCID.setString(1, auth.getFirstName());
			selectCID.setString(2, auth.getLastName());			
		}

		List<CsxCanname> list = new ArrayList<CsxCanname>();

		ResultSet rs = selectCID.executeQuery();
		while (rs.next()) {
			CsxCanname canname = new CsxCanname(rs);			
			if (auth.isMiddleNameCompatible(canname.getMiddleName())) {
				list.add(canname);
			}
		}
		return list;
	}

	public static void loadInfoFromFile(String filename, CsxAuthor auth, 
	        CiteSeerDoc doc) throws Exception {

		doc.setId("0.0.0.0");
		auth.setid("0");
		
		String line;
        BufferedReader reader = new BufferedReader(new FileReader(new 
                File(filename)));
		while ((line = reader.readLine()) != null) {
			System.out.println(">> " + line);
			String[] cols = line.split("=");

			if (cols.length > 1) {
				String key = cols[0];
				String val = cols[1];

				if (key.equals("name")) {
					auth.setName(val);
				} else if (key.equals("affil")) {
					auth.setAffil(val);
				} else if (key.equals("email")) {
					auth.setEmail(val);
				} else if (key.equals("ord")) {
					auth.setOrder(Integer.parseInt(val));
				} else if (key.equals("title")) {
					doc.setTitle(val);
				} else if (key.equals("coauthors")) {
					String[] names = val.split(" *, *");
					for (String name: names) {
						CsxAuthor coauth = new CsxAuthor();
						coauth.setName(name);
						doc.addAuthor(coauth);
					}					
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {

		String infile = args[0];

		run(infile);
	}
}