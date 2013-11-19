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

import java.util.*;

/**
 * Analyzer for normalizing author names.  This class is intended for
 * use during citation key generation.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class NameAnalyzer implements Analyzer {

    private static final String[] joinWords = {
            "van", "von", "der", "den", "di", "de", "le"
    };

    private HashMap<String,Object> joinTable = new HashMap<String,Object>();
    
    public NameAnalyzer() {
        for (String joinWord : joinWords) {
            joinTable.put(joinWord, null);
        }
    } //- NameAnalyzer
    
    /**
     * Joins multi-word names into unspaced strings, leaving spaces between
     * all other tokens.
     */
    public String analyze(String s) {
        StringBuffer buffer = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            buffer.append(token);
            if (st.hasMoreTokens() && !joinTable.containsKey(token)) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    } //- analyze
    
}  //- class NameAnalyzer
