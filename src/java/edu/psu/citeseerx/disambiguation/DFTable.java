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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * CsxDisambiguationUpdate
 * 
 * @author Puck Treeratpituk
 * @version $Rev$ $Date$
 */
public class DFTable {
    
	// to save space, allocate the small numbers only once in the 
    // documentFrequency map
	private static final Integer ONE    = new Integer(1);
	private static final Integer TWO    = new Integer(2);
	private static final Integer THREE  = new Integer(3);

	// maps tokens to document frequency
	protected Map<String,Integer> documentFrequency = 
	    new HashMap<String,Integer>(); 
	// count number of documents
	protected int collectionSize = 0;
	    
    public DFTable(String df_file) throws IOException {
		BufferedReader bufReader = new BufferedReader(
		        new InputStreamReader(new FileInputStream(new File(df_file))));
					
		String line;
		line = bufReader.readLine();
		collectionSize = Integer.parseInt(line.substring(1));
		
		// NOTE: assume case insensitive....
		while (null != (line = bufReader.readLine())) {
			String[] terms = line.split("\t");
			int df 		= Integer.parseInt(terms[0]);
			String tok  = terms[1].toLowerCase();
			
			setDocumentFrequency(tok,  df);
		}
    }
    public void setDocumentFrequency(String tok, int df) {		
		if (df == 1) documentFrequency.put(tok,ONE); 
		else if (df == 2) documentFrequency.put(tok,TWO);
		else if (df == 3) documentFrequency.put(tok,THREE);
		else documentFrequency.put(tok, new Integer(df));
    }
    public double getDocumentFrequency(String tok) {
		Integer freqInteger = (Integer)documentFrequency.get(tok.toLowerCase());
		if (freqInteger==null) return 0;
		else return freqInteger.intValue();
    }
    public double getIDFScore(String token) {
        double df  = getDocumentFrequency(token);
        if (df == 0) df = 1;        
        double idf = Math.log( collectionSize/df );        
        return idf;
    }
    public double getIDFScore(String[] tokens) {
        double ret = 0;
        for (String tok: tokens) {
            ret += getIDFScore(tok);
        }
        return ret;
    }    
	public double getIDFScore(ArrayList<String> tokens) {
		double ret = 0;
		for (String tok: tokens) {
			ret += getIDFScore(tok);
		}
		return ret;
	}
}