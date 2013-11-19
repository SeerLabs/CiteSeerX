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
package edu.psu.citeseerx.heritrix.jms;

import java.util.Collection;
import java.util.Iterator;

import org.archive.crawler.datamodel.CandidateURI;
import org.archive.crawler.datamodel.CrawlURI;
import org.archive.crawler.framework.Processor;

/**
 * <p>After links are discovered in a CrawlURI and promoted to
 * CandidateURIs by a LinkScoper, this class is used to augment all
 * child CandidateURIs with the URL of the parent CrawlURI.  This data
 * is placed in the children with the key "parent-url", and is carried
 * through the system as the children pass through their own processing
 * cycles.</p>
 * 
 * <p>This class is quite useful if you want to know where content came from
 * without having to do batch post-processing on the link graph after
 * crawls.</p>
 * 
 * <p>In configuration, this processor must be placed after a LinkScoper
 * and before the FrontierScheduler.</p>
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class ParentURLAnnotationProcessor extends Processor {

    /**
     * 
     */
    private static final long serialVersionUID = -3133074381026350068L;

    public static final String PARENT_URL_KEY = "parent-url";
    
    public static final String description = "Adds parent (inlink) url"
        +" information to CrawlURI links discovered, with the key 'parent-url'";
    
    public ParentURLAnnotationProcessor(String name) {
        super(name, description);
    }
    
    /**
     * Iterates over the child CandidateURIs of the specified CrawlURI,
     * augmenting them with the parent's URL string.
     */
    public void innerProcess(CrawlURI curi) {
        String url = curi.getUURI().toString();
        Collection<CandidateURI> candidates = curi.getOutCandidates();
        for (Iterator<CandidateURI> it =
                candidates.iterator(); it.hasNext(); ) {
            CandidateURI uri = it.next();
            uri.putString(PARENT_URL_KEY, url);
        }
        
    }  //- innerProcess
    
}  //- class ParentURLAnnotationProcessor
