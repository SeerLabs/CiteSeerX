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

import edu.psu.citeseerx.utility.CSXConstants;

import java.util.Hashtable;

import org.archive.crawler.admin.CrawlJob;
import org.archive.crawler.admin.CrawlJob.MBeanCrawlController;
import org.archive.crawler.datamodel.CrawlURI;
import org.archive.crawler.framework.CrawlController;
import org.archive.crawler.framework.Processor;

/**
 * Calls JMSInterface to create JMS messages indicating any problems
 * encountered during the crawl.  Messages will only be sent for 
 * jobs whose names start with CSXConstants.USER_SUBMISSION_PREFIX.
 * <br><br>
 * There are several failure codes that will be ignored, specified
 * in the static final ignoreCodes array within this class.  These codes
 * are -50, 1, 100, 101, 300, 301, 302, 303, 304, 307.  All other will be
 * reported.
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class JMSCrawlStatusProcessor extends Processor {

    /**
     * 
     */
    private static final long serialVersionUID = 7215449026341978444L;

    public static final String description =
        "Posts CrawlURI status updates to the JMSInterface for messaging.";
    
    private Hashtable<Integer,Integer> ignoreTable;
    
    private static final int[] ignoreCodes =
        new int[]{-50, 1, 100, 101, 300, 301, 302, 303, 304, 307};
    
    public JMSCrawlStatusProcessor(String name) {
        super(name, description);
        ignoreTable = new Hashtable<Integer,Integer>();
        for (int i=0; i<ignoreCodes.length; i++) {
            Integer code = new Integer(ignoreCodes[i]);
            ignoreTable.put(code, code);
        }
        
    }  //- JMSCrawlStatusProcessor
    
    
    /**
     * Checks a CrawlURI for failure, checks conditions for message
     * generation, and calls JMSInterface to create a new status message
     * when appropriate.
     */
    protected void innerProcess(CrawlURI curi) {

        String jobName;
        
        CrawlController ctrl = this.getController();
        if (ctrl instanceof MBeanCrawlController) {
            MBeanCrawlController mctrl = (MBeanCrawlController)ctrl;
            CrawlJob job = mctrl.getCrawlJob();
            jobName = job.getJobName();
        } else {
            System.err.println("JMSCrawlStatusProcessor: no job found.");
            return;
        }
        
        if (!matchesNotificationFilter(jobName)) {
            return;
        }
        
        if (!curi.is2XXSuccess()) {

            int status = curi.getFetchStatus();
            if (isIgnorable(status)) {
                return;
            }
            
            String uri = "";
            try {
                uri = curi.getUURI().getURI();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JMSInterface jmsInterface = JMSInterface.getInstance();
            jmsInterface.sendStatusUpdate(jobName, uri, status);
        }
        
    }  //- innerProcess
    
    
    private boolean isIgnorable(int code) {
        Integer icode = new Integer(code);
        return ignoreTable.containsKey(icode);
    }
    
    
    private boolean matchesNotificationFilter(String name) {
        return name.startsWith(CSXConstants.USER_SUBMISSION_PREFIX);
    }
    
}  //- class JMSCrawlStatusProcessor
