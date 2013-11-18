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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import javax.sql.DataSource;

import edu.psu.citeseerx.disambiguation.dbscan.BlockingDatabase;
import edu.psu.citeseerx.disambiguation.dao.CiteSeerDoc;
import edu.psu.citeseerx.disambiguation.dao.CsxAuthor;
import edu.psu.citeseerx.disambiguation.DisambiguationService;

/**
 * CsxAuthorBlock
 * 
 * Database structure for efficient and abstraction retrieval of author records
 * to be used by DBSCAN (or any other clustering methods)
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CsxAuthorBlock extends BlockingDatabase {
    
	Set<Integer> aids = new HashSet<Integer>();
	Map<Integer,CsxAuthorPoint> points = new HashMap<Integer,CsxAuthorPoint>();
	Map<String, List<CsxAuthorPoint>> regions = 
	    new HashMap<String, List<CsxAuthorPoint>>();
	Map<String,Double> distCache = new HashMap<String,Double>();

	DataSource dataSource;
	ProfileDistance profDistance;
	DisambiguationService distService;
	String tmpFile;

	public CsxAuthorBlock() { }
	
	public CsxAuthorBlock(DataSource dataSource, 
	        DisambiguationService distService, String aids_file) 
	throws Exception {
		setDataSource(dataSource);
		setDistService(distService);
		loadAuthors(aids_file);
	} //- CsxAuthorBlock

	public CsxAuthorBlock(DataSource dataSource, 
	        DisambiguationService distService, ArrayList<Integer> aids)
	throws Exception {
		setDataSource(dataSource);
		setDistService(distService);
		loadAuthors(aids);
	} //- CsxAuthorBlock

	public void loadAuthors(String aids_file) throws Exception {
		List<Integer> aids = new ArrayList<Integer>();
		String aidStr;
        BufferedReader reader = new BufferedReader(new FileReader(
                new File(aids_file)));
		while ((aidStr = reader.readLine()) != null) {
			Integer aid = Integer.parseInt(aidStr);
			aids.add(aid);
		}
		loadAuthors(aids);
	} //- loadAuthors

	public void loadAuthors(List<Integer> aids) throws Exception {
		this.aids      = new HashSet<Integer>();
		this.points    = new HashMap<Integer,CsxAuthorPoint>();
		this.regions   = new HashMap<String, List<CsxAuthorPoint>>();
		this.distCache = new HashMap<String,Double>();

		Connection conn = this.dataSource.getConnection();
		for (Integer aid : aids) {
			CiteSeerDoc doc = new CiteSeerDoc(conn, aid);
			CsxAuthor auth  = (CsxAuthor)doc.getAuthorById(aid.toString());
			
			CsxAuthorPoint point = new CsxAuthorPoint(auth, doc);
			
			this.points.put(aid, point);
			this.aids.add(aid);
			
			String namekey = this.getNameKey(auth);
			List<CsxAuthorPoint> list;
			if (!regions.containsKey(namekey)) {
				list = new ArrayList<CsxAuthorPoint>();
				regions.put(namekey, list);
			}
			else 
				list = regions.get(namekey);
			list.add(point);			
		}
	} //- loadAuthors

	// ------------------------------------------------------------
	// Set & Get Methods
	// ------------------------------------------------------------
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	} //- setDataSource
	
	public void setProfDistance(ProfileDistance profDistance) {
		this.profDistance = profDistance;
	} //- setProfDistance
	
	public void setDistService(DisambiguationService distService) {
		this.distService = distService;
	} //- setDistService
	
	public void setTmpFile(String tmpFile) {
		this.tmpFile = tmpFile;
	} //- setTmpFile
	
	public String getTmpFile() {
		return this.tmpFile;
	} //- getTmpFile
	// END Set & Get Methods
	// ------------------------------------------------------------

	protected String getNameKey(CsxAuthor author) {
		String namekey = "";

		String fname = author.getFirstName();
		if (fname.length() < 2)
			namekey = fname.substring(0,1);
		else 
			namekey = fname; //fname.substring(0,2);
		return namekey;
	} //- getNameKey

	// ------------------------------------------------------------
	// BlockingDatabase Interface
	// ------------------------------------------------------------
	public Set<Integer> getAllPoints() {
		return this.aids;
	} //- getAllPoints
	
	public String getDistKey(Integer aid1, Integer aid2) {
		if (aid1 <= aid2) 
			return aid1 + ":" + aid2;
		else return aid2 + ":" + aid1;
	} //- getDistKey

	public Set<Integer> getNeighbors(CsxAuthorPoint p1, double eps) {
		Set<Integer> neighbors = new HashSet<Integer>();

		String namekey = this.getNameKey(p1.author); // 2 chars key

		List<CsxAuthorPoint> no_caches = new ArrayList<CsxAuthorPoint>();

		List<CsxAuthorPoint> region0 = regions.get(namekey);
		getNeighborsByCache(p1, region0, eps, neighbors, no_caches);

		if (namekey.length() > 1) {
			// default region so "Prasenjit Mitra" get compared with "P Mitra"
			List<CsxAuthorPoint> region1 = 
			    regions.get(namekey.substring(0,1));
			if (region1 != null)
				getNeighborsByCache(p1, region1, eps, neighbors, no_caches);			
		}
		
		if (no_caches.size() > 0)
			getNeighborsByService(p1, no_caches, eps, neighbors);
		else {
			//System.out.println(">>> GREAT!!!!");
		}
		return neighbors;
	} //- getNeighbors

	// pid = point ID
	public Set<Integer> getNeighbors(Integer aid, double eps) {
		CsxAuthorPoint p1 = points.get(aid);
		return getNeighbors(p1, eps);
	} //-getNeighbors
	
	public boolean passConstrains(CsxAuthor a1, CsxAuthor a2) {
		return a1.isCompatible(a2);
	} //- passConstrains
	
	public void getNeighborsByCache(CsxAuthorPoint p1, 
	        List<CsxAuthorPoint> region, double eps, Set<Integer> neighbors,
	        List<CsxAuthorPoint> no_caches) {		
		for (CsxAuthorPoint p2: region) {
			if ((p1.id == p2.id) || (p1.doc.getId().equals(p2.doc.getId())))
				continue;
			if (!passConstrains(p1.author, p2.author))
				continue;

			String distkey = getDistKey(p1.id, p2.id);
			Double d = distCache.get(distkey);
			if (d != null) {
				if (d < eps) {
					neighbors.add(p2.id);
				}
			}
			else {
				no_caches.add(p2);
			}
		}
	} //- getNeighborsByCache
	
	public void getNeighborsByService(CsxAuthorPoint p1, 
	        List<CsxAuthorPoint> region, double eps, Set<Integer> neighbors) {
		try {
			BufferedWriter out = new BufferedWriter(
			        new FileWriter(getTmpFile()));
			out.write("auth_fst,auth_mid,auth_ord,auth_last_idf,email_jac," +
					"aff_softtfidf,aff_tfidf,aff_jac,coauth_lname_shared," +
					"coauth_lname_idf,coauth_lname_jac,title_shared\n");
			
			for (CsxAuthorPoint p2: region) {
				Map<ProfileDistance.FeatType, Double> feats = 
					profDistance.calcFeatures(p1.doc, p1.author, p2.doc, 
					        p2.author);
				String line = profDistance.getFeatureString(feats, false);
				out.write(line + "\n");
			}
			out.close();
			double[] dists = distService.calcDistances(getTmpFile());
			
			int i = 0;
			for (CsxAuthorPoint p2: region) {
				double d = dists[i];
				String distkey = getDistKey(p1.id, p2.id);
				distCache.put(distkey, new Double(d));
				if (d < eps) {
					
					neighbors.add(p2.id);
				}
				i++;
			}
		} catch (Exception ex) { 
			System.out.println(ex);
			ex.printStackTrace();
			System.exit(0);
		}
	} //- getNeighborsByService
	
	public void getNeighborsByCoAuth(CsxAuthorPoint p1, 
	        List<CsxAuthorPoint> region, double eps, Set<Integer> neighbors) {
		for (CsxAuthorPoint p2: region) {
			Map<ProfileDistance.FeatType, Double> feats = 
			    new EnumMap<ProfileDistance.FeatType, Double>(
			            ProfileDistance.FeatType.class);
			
			profDistance.calcCoAuthFeats(p1.doc, p1.author.getOrder(), 
										 p2.doc, p2.author.getOrder(), feats);
			Double d = feats.get(ProfileDistance.FeatType.COAUTH_LNAME_SHARED);
			if (d != 0.0) {
				neighbors.add(p2.id);
			}
		}
	} //- getNeighborsByCoAuth
	
	public void printPoint(Integer aid) {
		CsxAuthorPoint pt = points.get(aid);
		CsxAuthor author  = pt.author;
		System.out.println("\t" + aid + "\t" + author.getName() + " >> " + 
		        pt.author.getAffil());
	} //- printPoint

	public class CsxAuthorPoint {
		Integer id;
		CsxAuthor author;
		CiteSeerDoc doc;
		
		CsxAuthorPoint(CsxAuthor author, CiteSeerDoc doc) {
			this.id     = new Integer(author.getId());
			this.author = author;
			this.doc 	= doc;
		}
	} //- class CsxAuthorPoint
	
} //- class CsxAuthorBlock