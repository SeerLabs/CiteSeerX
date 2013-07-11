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
package edu.psu.citeseerx.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Simple frequency table for keeping count...
 *
 * NOTE: currently only deal with String key, probably should generalize it to Object later 
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 1 $ $Date: 2010-11-19 $
 */
public class FreqTable {
	HashMap<String,Integer> freqOf = new HashMap<String,Integer>();
	
	public FreqTable() { }
	public void addCount(String key) { 
		if ((key == null) || key.equals(""))
			return;
		if (freqOf.containsKey(key)) {
			Integer count = freqOf.get(key);
			freqOf.put(key, count + 1);
		}
		else
			freqOf.put(key, new Integer(1));
	}

	public Integer getCount(String key) {
		return freqOf.get(key);
	}

	public String getMostFreq() {
		int    maxVal = -1;
		String maxKey = null;
		for (String key : freqOf.keySet()) {
			Integer freq = freqOf.get(key);
			if (maxVal < freq) {
				maxVal = freq;
				maxKey = key;
			}
		}
		return maxKey;
	}
	
	public ArrayList<String> getSortedKey() {
		ArrayList<String> list = new ArrayList<String>(freqOf.keySet());
		Collections.sort(list, new Comparator() {
				public int compare(Object s1, Object s2) {
					Integer v1 = freqOf.get((String)s1);
					Integer v2 = freqOf.get((String)s2);
					return v2.compareTo(v1);
				}
			});
		return list;
	}
}