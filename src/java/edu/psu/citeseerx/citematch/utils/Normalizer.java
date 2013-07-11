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
package edu.psu.citeseerx.citematch.utils;

import edu.psu.citeseerx.utility.SafeText;

/**
 * Normalization utilities for citation text.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class Normalizer {

    private static final String ASCII =
        "AaEeIiOoUu" +    // grave
        "AaEeIiOoUuYy" +  // acute
        "AaEeIiOoUuYy" +  // circumflex
        "AaEeIiOoUuYy" +  // tilde
        "AaEeIiOoUuYy" +  // umlaut
        "Aa" +            // ring
        "Cc"              // cedilla
        ;

    private static final String UNICODE =
        "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9" +
        "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA" + 
        "\u00DD\u00FD\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4" + 
        "\u00DB\u00FB\u0176\u0177\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE" +
        "\u00D4\u00F4\u00DB\u00FB\u0176\u0177\u00C4\u00E4\u00CB\u00EB" +
        "\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF\u00C5\u00E5" +
        "\u00C7\u00E7"                                                             
        ;

    
    /**
     * Replaces accent chars in string with ascii equivalent.
     */
    public static String replaceAccents(String s) {
          
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            int pos = UNICODE.indexOf(c);
            if (pos > -1){
                sb.append(ASCII.charAt(pos));
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
         
    }  //- replaceAccents
      
    
    /**
     * Normalizes a title based on a specified analyzer.
     * @param s
     * @param sa
     * @param stem whether to stem tokens.
     * @return The normalized version of s using the given analyzer
     */
    public static String normalizeTitle(String s, Analyzer sa, boolean stem) {

        s = SafeText.normalizeText(s);
        //s = replaceAccents(s);
        s = s.toLowerCase();
        s = sa.analyze(s);
        if (stem) {
            s = Stemmer.stemString(s);
        }
          
        return s.trim();
          
    }  //- normalizeTitle
      
    
    /**
     * Normalizes an string of comma-separated author names based on a
     * specified analyzer. 
     * @param s
     * @param na
     * @return A string containing a normalized comma-separated author names. 
     * The names are normalized using the given analyzer   
     */
    public static String normalizeAuthors(String s, Analyzer na) {
        String[] auths = s.split(",");
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<auths.length; i++) {
            auths[i] = SafeText.normalizeText(auths[i]);
            auths[i] = auths[i].toLowerCase();
            auths[i] = na.analyze(auths[i]);
            auths[i] = auths[i].trim();
            builder.append(auths[i]);
            if (i<auths.length-1) {
                builder.append(",");
            }
        }
        return builder.toString();
          
    }  //- normalizeAuthor
      
}  //- class Normalizer
