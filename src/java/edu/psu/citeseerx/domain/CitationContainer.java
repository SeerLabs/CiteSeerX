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
package edu.psu.citeseerx.domain;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Bean container used to handle citations.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CitationContainer implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6572153362839475214L;
    
    private Citation citation;
    
    public CitationContainer(Citation citation) {
        this.citation = citation;
    } //- CitationContainer
    
    public String getCiteString() {
        StringBuffer buf = new StringBuffer();

        List<String> authors = citation.getAuthorNames();
        boolean hasAuthors = false;
        
        for (Iterator<String> it = authors.iterator(); it.hasNext(); ) {
            String name = it.next();
            if (name == null || name.matches("^\\s*$")) {
                continue;
            }
            buf.append(name);
            if (it.hasNext()) {
                buf.append(", ");
            }
            hasAuthors = true;
        }
        if (!hasAuthors) {
            buf.append("Unknown authors");
        }
        buf.append(". ");
        
        String year = citation.getDatum(Citation.YEAR_KEY);
        buf.append((year != null) ? year : "Unknown date");
        buf.append(". ");
        
        String title = citation.getDatum(Citation.TITLE_KEY);
        buf.append((title != null) ? title : "Unknown title");
        buf.append(". ");
        
        String venue = citation.getDatum(Citation.VENUE_KEY);
        buf.append((venue != null) ? venue : "Unknown venue");
        
        String vol = citation.getDatum(Citation.VOL_KEY);
        if (vol != null) {
            buf.append(", vol. ");
            buf.append(vol);
            String num =
                citation.getDatum(Citation.NUMBER_KEY);
            if (num != null) {
                buf.append("(");
                buf.append(num);
                buf.append(")");
            }
        }
        
        
        String pages = citation.getDatum(Citation.PAGES_KEY);
        if (pages != null) {
            buf.append(", pp. ");
            buf.append(pages);
            buf.append(".");
        } else {
            buf.append(".");
        }
        return buf.toString();
        
    }  //- getCiteString
    
}  //- class CitationContainer
