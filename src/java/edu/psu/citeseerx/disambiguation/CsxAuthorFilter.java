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

import java.util.HashSet;
import java.io.*;

/**
 * CsxAuthorFilter
 *
 * a class encompassing heuristic filtering methods for determining whether an author record
 * is a vaild author record, eg. screening out those like "Student Member", or University, Department names
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CsxAuthorFilter {
	HashSet<String> stopnames = new HashSet<String>();

	public CsxAuthorFilter() { }
	
	public void setNameStopwordsFile(String nameStopwordsFile) throws IOException {
		String line;
        BufferedReader reader = new BufferedReader(new FileReader(new File(nameStopwordsFile)));
		while ((line = reader.readLine()) != null) {
			stopnames.add(line);
		}
	}

	public boolean isStopword(String name) {
		if (stopnames.contains(name.trim()))
			return true;
		return false;
	}

	public boolean isInstitute(String name) {
		if (name.matches(".*(Informatik|Universit|Technische).*"))
			return true;
		return false;
	}
	
	public boolean isPosition(String name) {
		if (name.matches(".*(Member|Members|member|members|Chair|chair| Dr| Prof|Ingenieurwissenschaften)"))
			return true;
		return false;
	}

	public static void main(String[] args) {
		try {
			CsxAuthorFilter filter = new CsxAuthorFilter();
			filter.setNameStopwordsFile("data/resources/disambiguation/name_stopwords.txt");
			
			String[] list = {"Hussein Badr","Dr. -ing Dr","Ph. D. (chair","Lehrstuhl Prof"};

			java.util.Date now = new java.util.Date();
			System.out.println(now);

			String s = "/AB/C/D";
			System.out.println(">>" + s);
			System.out.println(s.matches(".*/.*"));

			for (String name: list) {
				if (filter.isPosition(name)) {
					System.out.println(name);
				}
			}
		} catch (Exception ex) { ex.printStackTrace(); }
	}
}