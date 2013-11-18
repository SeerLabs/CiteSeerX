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
package edu.psu.citeseerx.myciteseer.domain;

import java.io.Serializable;
import java.util.Date;

import edu.psu.citeseerx.utility.UrlStatusMappings;

/**
 * Data transfer object with Submission information.
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class UrlSubmission implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5771594370863421737L;
    
    private String jobID;
    private String username;
    private String url;
    private int depth;
    private Date time;
    private int status;
    private Date statusTime;
    private int numFiles;
    
    public int getDepth() {
        return depth;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }
    public String getJobID() {
        return jobID;
    }
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public Date getStatusTime() {
        return statusTime;
    }
    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public int getNumFiles() {
        return numFiles;
    }
    public void setNumFiles(int numFiles){
        this.numFiles = numFiles;
    }
    
    public String getStatusDesc() {
        return UrlStatusMappings.getDescription(status);
    }
    
    
}  //- class UrlSubmission
