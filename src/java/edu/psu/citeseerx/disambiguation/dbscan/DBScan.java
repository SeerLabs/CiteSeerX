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
package edu.psu.citeseerx.disambiguation.dbscan;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.psu.citeseerx.disambiguation.dbscan.BlockingDatabase;

/**
 * DBSCAN clustering algorithm
 * 
 * @author Puck Treeratpituk
 */
public class DBScan {
	BlockingDatabase db;
	double eps;
	int minPts;
	
	// point ID => cluster ID
	Map<Integer,Integer> pid2cid = new HashMap<Integer,Integer>();
	// set of unvisited point IDs
	Set<Integer> unvisited = new HashSet<Integer>();
	Set<Integer> noises    = new HashSet<Integer>();
		
	public DBScan(BlockingDatabase db, double eps, int minPts) {
		this.db 	= db;
		this.eps 	= eps;
		this.minPts = minPts;
	} //- DBScan
	
	public void run() {
		
		Set<Integer> points = db.getAllPoints();
		//List<Integer> points = db.getAllPoints();
		for (Integer p1: points)
			unvisited.add(p1);

		int curID = 0;
		for (Integer p1: points) {
			if (unvisited.contains(p1)) {
				unvisited.remove(p1); // mark p1 as visited
				//System.out.println("LEFT: " + p1 + " >> " + unvisited.size());

				Set<Integer> n1 = db.getNeighbors(p1, eps);
				if (n1.size() < minPts) {
					noises.add(p1); 			// mark P as Noise 
				}
				else {
				    // C = next Cluster
					Integer clusterID = new Integer(++curID); 
					expandCluster(p1, n1, clusterID, eps, minPts);
				}
			}
		}
	} //- run
	
	public boolean expandCluster(Integer p1, Set<Integer> n1, 
	        Integer clusterID, double eps, int minPts) {
		
		pid2cid.put(p1, clusterID);				// add P to cluster C
		List<Integer> seeds = new ArrayList<Integer>(n1);

		for (int i = 0; i < seeds.size(); i++) {
			Integer p2 = seeds.get(i);
			if (unvisited.contains(p2)) { 		// if p2 not visited
				unvisited.remove(p2); 			// 		mark p2 as visited
				Set<Integer> n2 = db.getNeighbors(p2, eps);
				if (n2.size() >= minPts) {
					// for all point in n2, that is unvisited.. add to seeds
					for (Integer p3: n2) {
						if (unvisited.contains(p3))
							seeds.add(p3);
					}
				}
			}

			if (!pid2cid.containsKey(p2)) {		// if p2 not in any cluster
				pid2cid.put(p2, clusterID);		//		add p2 to cluster C
			}
		}
		return false;
	} //- expandCluster

	public Map<Integer, List<Integer>> getClusters() {
		Map<Integer, List<Integer>> clusters = 
		    new HashMap<Integer, List<Integer>>();

		for (Integer pid: pid2cid.keySet()) {
			Integer cid = pid2cid.get(pid);
			
			List<Integer> list;
			if (!clusters.containsKey(cid)) {
			 	list = new ArrayList<Integer>();
				clusters.put(cid, list);
			}
			else
				list = clusters.get(cid);
			list.add(pid);
		}
		return clusters;
	} //- getClusters

	public void printResults(String outfile) {
		Map<Integer, List<Integer>> clusters = getClusters();
		try {	
			BufferedWriter out = new BufferedWriter(new FileWriter(outfile));	

			for (Integer cid: clusters.keySet()) {
				List<Integer> list = clusters.get(cid);
				out.write("[" + cid + "]\n");
				for (Integer pid: list) {
					out.write(pid + "\n");
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} //- printResults
	
	public void printResults() {
		Map<Integer, List<Integer>> clusters = getClusters();
		
		for (Integer cid: clusters.keySet()) {
			List<Integer> list = clusters.get(cid);
			System.out.println(cid + ">>>>>>");
			for (Integer pid: list) {
				db.printPoint(pid);
			}
		}
		/*HashSet<Integer> noises    = new HashSet<Integer>();
		for (Integer aid: noises) {
			System.out.println(">>> NOISE : " + aid);
		}*/
	} //- printResults
} //- class DBScan