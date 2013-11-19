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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.archive.crawler.datamodel.CandidateURI;
import org.archive.crawler.datamodel.CrawlURI;
import org.archive.crawler.deciderules.DecideRule;
import org.archive.crawler.extractor.Link;

import edu.psu.citeseerx.utility.CSXConstants;
import edu.psu.citeseerx.utility.ConfigurationKey;
import edu.psu.citeseerx.utility.ConfigurationManager;

/**
 * Filter to enable processing of "hub" URIs, where a hub is defined as
 * any document containing links to documents of interest (such as PDF, PS,
 * etc.).  CSXHubFilter will respond with ACCEPT if any of the outlinks
 * discovered from the URI being filtered match a regular expression
 * specified in configuration.  The pattern match should be set in
 * CSX ConfigurationManager style,
 * with the path edu.psu.citeseerx.heritrix.jms.hubLinkIndicator.   
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class CSXHubFilter extends DecideRule {

    /**
     * 
     */
    private static final long serialVersionUID = 2246413439673056713L;

    public static final String description = "Matches if a URI contains"
        +" out links that match file type criteria specified in CSX"
        +" configuration.  If the URI is not a hub, this filter will pass.";

    private static ConfigurationManager cm;
    private static final ConfigurationKey accessKey = new AccessKey();
    
    private String regexp;
    private Pattern pattern;
    
    public CSXHubFilter(String name) throws Exception {
        super(name);
        setDescription(description);
        cm = new ConfigurationManager(); 
        regexp = cm.getString("hubLinkIndicator", accessKey);
        pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);

    }  //- CSXHubFilter
    
    
    /**
     * Checks links for hub status, returning ACCEPT if success and PASS
     * if not.  Object must be an instance of CrawlURI.  If ACCEPT, the
     * CrawlURI is augmented with a "resource-type" attributed indicating
     * that this CrawlURI represents a hub.
     */
    public Object decisionFor(Object object) {
        if (! (object instanceof CrawlURI)) {
            return PASS;
        }
        CrawlURI curi = (CrawlURI)object;
        Object decision = checkOutLinks(curi);
        if (decision == ACCEPT) {
            curi.putString("resource-type", CSXConstants.HUB_TYPE);
        }
        return decision;
        
    }  //- decisionFor
    
    
    /**
     * Iterates over the out links of the given CrawlURI and checks against
     * hubLinkIndicator regular expression.
     * @param curi - CrawlURI to be checked for hub status.
     * @return ACCEPT or PASS
     */
    private Object checkOutLinks(CrawlURI curi) {
        int hubLinkCount = 0;

        Collection<Object> links = curi.getOutObjects();
        for (Iterator<Object> it = links.iterator(); it.hasNext(); ) {
            Object link = it.next();
            String uri = null;
            if (link instanceof CandidateURI) {
                uri = ((CandidateURI)link).getUURI().toString();
            } else if (link instanceof Link) {
                uri = (((Link)link).getDestination()).toString();
            }
            if (uri != null) {
                hubLinkCount += checkLink(uri);
            }
        }
        if (hubLinkCount > 0) {
            return ACCEPT;
        } else {
            return PASS;
        }
        
    }  //- checkOutLinks
    
    
    /**
     * Matches a single uri against the hubLinkIndicator pattern.
     * @param uri
     * @return
     */
    private int checkLink(String uri) {
        Matcher m = pattern.matcher(uri);
        if (m.matches()) {
            return 1;
        } else {
            return 0;
        }
        
    }  //- checkLink
    
}  //- class CSXHubFilter
