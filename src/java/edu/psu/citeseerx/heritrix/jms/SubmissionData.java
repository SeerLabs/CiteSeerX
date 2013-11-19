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

/**
 * Basic data holder for job information, including job name, description,
 * and seeds.  The seeds string may contain multiple URLs (as indicated by
 * the plural form), as long as they are separated by newline characters.
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class SubmissionData {

    private String metaName = "";
    private String jobDescription = "";
    private String seeds = "";
    
    public String getMetaName() {
        return metaName;
    }
    
    public void setMetaName(String metaName) {
        this.metaName = metaName;
    }
    
    public String getJobDescription() {
        return jobDescription;
    }
    
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
    
    public String getSeeds() {
        return seeds;
    }
    
    public void setSeeds(String seeds) {
        this.seeds = seeds;
    }
    
}  //- class SubmissionData
