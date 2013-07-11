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

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.utility.SafeText;

/**
 * Filter for marking all self citations within a Document object.  Self
 * citations are identified by matching citation author keys against
 * keys generated from the authors of the citing Document.  If there is
 * a match, the matching citation is flagged as a self citation.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class SelfCitationFilter {

    /**
     * Mark all self citations within a Document object.
     * @param doc
     */
    public static void filterCitations(Document doc) {
        
        HashSet<String> authorKeys = new HashSet<String>();
        for (Author author : doc.getAuthors()) {
            String key = buildNameKey(author.getDatum(Author.NAME_KEY));
            if (key != null) {
                authorKeys.add(key);
            }
        }
        for (Citation citation : doc.getCitations()) {
            citation.setSelf(false);
            for (String name : citation.getAuthorNames()) {
                if (authorKeys.contains(buildNameKey(name))) {
                    citation.setSelf(true);
                    break;
                }
            }
        }
        
    }  //- filterCitations
    
    
    private static String buildNameKey(String name) {

        name = name.trim();
        String[] tokens = name.split(" +");
        String key = null;
        if (tokens.length == 1) {
            key = tokens[0];
        } else if (tokens.length > 1) {
            key = tokens[0].charAt(0) + "_" + tokens[tokens.length-1];
        }
        if (key != null) {
            key = SafeText.normalizeText(key);
            key = key.toLowerCase();
        }
        return key;
        
    }  //- buildNameKey
    
}  //- class SelfCitationFilter
