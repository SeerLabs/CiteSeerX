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
package edu.psu.citeseerx.citematch.keybased;

import java.util.*;
import edu.psu.citeseerx.citematch.utils.*;

/**
 * This class generates metadata keys used for matching document and citation
 * records.  Keys are based on normalizations of the first and second authors
 * as well as normalizations of the title text. 
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class KeyGenerator {

    private static final int MAX_TITLE = 30;
    private static final int MIN_TITLE = 5;
    private static final int MAX_AUTHORS = 2;
    
    private final Analyzer stopWordAnalyzer;
    private final Analyzer nameAnalyzer;
    
    public KeyGenerator(Analyzer stopWordAnalyzer, Analyzer nameAnalyzer) {
        this.stopWordAnalyzer = stopWordAnalyzer;
        this.nameAnalyzer = nameAnalyzer;
    } //- KeyGenerator
    

    /**
     * @param title
     * @param authors
     * @return a list of keys representing the specified title/author combo.
     */
    public List<String> getKeys(String title, String authors) {
        ArrayList<String> keys = new ArrayList<String>(); 

        List<String> titleKeys = getTitleKeys(title);
        List<String> authorKeys = getAuthorKeys(authors);

        if (titleKeys.isEmpty() || authorKeys.isEmpty()) {
            return keys;
        }
        
        for (int i=0; i<authorKeys.size(); i++) {
            for (int j=0; j<titleKeys.size(); j++) {
                keys.add(authorKeys.get(i)+"_"+titleKeys.get(j));
            }
        }
        
        return keys;
        
    }  //- getKeys
    
    
    private List<String> getAuthorKeys(String authText) {

        ArrayList<String> keys = new ArrayList<String>();
        if (authText == null) {
            return keys;
        }
        
        String nauth = Normalizer.normalizeAuthors(authText, nameAnalyzer);
        StringTokenizer st = new StringTokenizer(nauth, ",");

        int counter = 1;
        while(st.hasMoreTokens()) {
            if (counter > MAX_AUTHORS) {
                break;
            }
            String token = st.nextToken().trim();
            StringTokenizer tst = new StringTokenizer(token);
            String lastName = tst.nextToken();
            while(tst.hasMoreTokens()) {
                lastName = tst.nextToken();
            }
            if (lastName != null && !lastName.equals("")) {
                keys.add(lastName);
                counter++;
            }
        }
        return keys;
        
    }  //- getAuthorKeys
    
    
    private List<String> getTitleKeys(String titleText) {

        ArrayList<String> keys = new ArrayList<String>();
        if (titleText == null) {
            return keys;
        }

        String ntitle = Normalizer.normalizeTitle(titleText,
                stopWordAnalyzer, true);
        
        String offsetTitle = null;
        int firstSpace = ntitle.indexOf(' ');
        if (firstSpace != -1) {
            offsetTitle = ntitle.substring(firstSpace+1);
        }
        
        buildTitleKey(keys, ntitle);
        
        if (offsetTitle != null && offsetTitle.length() > 1) {
            buildTitleKey(keys, offsetTitle);
        }
        
        return keys;
        
    }  //- getTitleKey
    
    
    private void buildTitleKey(List<String> keys, String title) {
        title = title.replace(" ", "");
        if (title.length() > MAX_TITLE) {
            title = title.substring(0, MAX_TITLE);
        }
        if (title.length() >= MIN_TITLE) {
            keys.add(title);
        }
        
    }  //- buildTitleKey
    
}  //- class KeyGenerator
