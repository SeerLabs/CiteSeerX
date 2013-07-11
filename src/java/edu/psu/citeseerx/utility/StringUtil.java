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

/**
 * A collection of utility functions for formating a string
 * @author Puck Treeratpituk
 * @version $Rev: 1 $ $Date: 2010-11-19 $
 */
public class StringUtil {

	/**
	 * do basic clean up for extracted email 
	 */
	public static String cleanUpEmail(String email) {
		return email.replaceAll("[{}]","");
	}

	/**
	 * do basic clean up of affiliation string 
	 */
	public static String cleanUpAffil(String affil) {
		affil = affil.replaceAll("Department","Dept.");
 		affil = affil.replaceAll("Univ. ","University ");
		affil = affil.replaceAll(" & ", " and ");
		affil = affil.replaceAll("\\b([a-z0-9],)*[a-z0-9]\\b","");
		affil = affil.replaceAll("( ;)*","").replaceAll("\\B;\\B","");
		affil = affil.replaceAll(" +", " ").trim();
		return affil;
	}
	
	/** 
	 * formating a string to a proper XML format
	 */
	public static String formatXML(String s) {
		s = s.replaceAll("&","&amp;");
		s = s.replaceAll("'","&apos;");
		s = s.replaceAll(">","&lt;");
		s = s.replaceAll("<","&gt;");
		s = s.replaceAll("\"","&quot;");
		return s;
	}
	
	/**
	 * dropping accent from a string (to deal with european langauges)
	 */
    public static String dropAccent(String s) {
        s = s.replaceAll("è|é|ê|ë","e");
        s = s.replaceAll("û|ù|ü","u");
        s = s.replaceAll("ï|î","i");
        s = s.replaceAll("à|á|â", "a");
        s = s.replaceAll("Ô|ö","o");
        s = s.replaceAll("ç","c");
        s = s.replaceAll("È|É|Ê|Ë","E");
        s = s.replaceAll("Û|Ù","U");
        s = s.replaceAll("Ï|Î","I");
        s = s.replaceAll("À|Â|Å|Á","A");
        s = s.replaceAll("Ô|Ö","O");
        s = s.replaceAll("Ç","C");
        return s;
	}
	
	public static boolean relaxStrEquals(String tar, String src) {
		src = dropAccent(src);
		if (tar.equalsIgnoreCase(src))
			return true;
		else return false;
	}
}