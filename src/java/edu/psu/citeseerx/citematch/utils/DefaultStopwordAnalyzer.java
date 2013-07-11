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
 * Analyzer for removing default stopwords from strings, as defined
 * in the DefaultStopwordList class.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class DefaultStopwordAnalyzer implements Analyzer {

    private HashMap<String,Object> stopTable = new HashMap<String,Object>();
    
    /**
     * Reads in stopwords from the DefaultStopwordList.
     */
    public DefaultStopwordAnalyzer() {
        String[] stopwords = DefaultStopwordList.getStopwords();
        for (int i=0; i<stopwords.length; i++) {
            stopTable.put(stopwords[i], null);
        }
    } //- DefaultStopwordAnalyzer

    /**
     * Removes stopwords from the specified String.
     */
    public String analyze(String s) {
        StringBuffer buffer = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s);
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!stopTable.containsKey(token)) {
                buffer.append(token);
                if (st.hasMoreTokens()) {
                    buffer.append(" ");
                }
            }
        }
        return buffer.toString();
    } //- analyze
    
}  //- class DefaultStopwordAnalyzer
