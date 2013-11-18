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
package edu.psu.citeseerx.myciteseer.web.utils;

import java.io.*;
import java.util.*;

/**
 * Determines if a string is within the defined set of foul words.
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class FoulWordFilter {

    
    public void setFoulWordList(String foulWordFile) throws IOException {
        readList(foulWordFile);
    } //- setFoulWordList

    
    private HashSet<String> foulWords = new HashSet<String>();
    
    protected void readList(String foulWordFile) throws IOException {
        
        BufferedReader reader =
            new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(foulWordFile)));
        String line = "";
        while((line = reader.readLine()) != null) {
            if (line.matches("^\\s*$") || line.charAt(0) == '#') {
                continue;
            }
            line = line.toLowerCase();
            foulWords.add(line);
        }
        
    }  //- readList
    
    
    public String findFoulWord(String str) {
        if (str == null) return null;
        StringTokenizer st = new StringTokenizer(str);
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if (foulWords.contains(token.toLowerCase())) {
                return token;
            }
        }
        return null;
        
    }  //- findFoulWord
    
}  //- class FoulWordFilter
