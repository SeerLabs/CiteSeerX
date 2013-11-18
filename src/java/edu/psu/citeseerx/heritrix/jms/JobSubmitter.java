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

import org.archive.crawler.admin.*;
import java.util.regex.*;

/**
 * Bridge into the Heritrix job submission system.  Creates CrawlJob
 * instances based on SubmissionData objects and specified profile
 * information, then passes them into the Heritrix CrawlJobHandler for
 * regular processing.
 * 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 *
 */
public class JobSubmitter {

    private final CrawlJobHandler handler;
    private final String submissionProfile;
    
    public JobSubmitter(CrawlJobHandler handler, String submissionProfile) {
        this.handler = handler;
        this.submissionProfile = submissionProfile;
        
    }  //- JobSubmitter
    
    
    /**
     * Creates a valid CrawlJob based on a pre-configured profile.
     * @param data
     * @throws Exception - if profile does not exist or if the job name
     * is not valid by Heritrix's standards.
     */
    public CrawlJob submit(SubmissionData data) throws Exception {
        
        CrawlJob profile = handler.getJob(submissionProfile);
        if (profile == null) {
            throw new Exception("JMS JobSubmitter: invalid profile: "+
                    submissionProfile);
        }
        
        CrawlJob job = null;
        
        String metaName = data.getMetaName();
        String jobDescription = data.getJobDescription();
        String seeds = data.getSeeds();
        String recovery = "";
        
        Pattern p = Pattern.compile("[a-zA-Z_\\-0-9\\.,]*");
        if (p.matcher(metaName).matches()==false) {
            // Illegal name!
            throw new Exception(
                    "JMS JobSubmitter: "
                    +"Name can only contain letters, digits, and dash, "
                    +"underscore, period, or comma ( - _ . , ). "
                    +"No spaces are allowed");
        }
        
        CrawlJob test = handler.getJob(metaName);
        if (test == null) {
            // Unique name = good
            job = handler.newJob(
                    profile,
                    recovery,
                    metaName,
                    jobDescription,
                    seeds,
                    CrawlJob.PRIORITY_HIGH);
        } else {
            throw new Exception(
                    "JMS JobSumitter: job metaName must be unique!");
        }
        
        CrawlJobHandler.ensureNewJobWritten(job, metaName, jobDescription);
        return job;
        
    }  //- submit
    
}  //- class JobSubmitter
