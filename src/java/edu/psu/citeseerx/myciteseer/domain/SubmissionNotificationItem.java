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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import edu.psu.citeseerx.utility.UrlStatusMappings;
import java.io.Serializable;

/**
 * Data transfer object with SubmissionNotificationItem information.
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class SubmissionNotificationItem implements Serializable,
	Comparable<SubmissionNotificationItem> {

    /**
     * 
     */
    private static final long serialVersionUID = 8509525941666057343L;
    private String jobID;
    private String URL;
    private int status;
    private long time;
    private String DID;
    
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
    public String getURL() {
        return URL;
    }
    public void setURL(String url) {
        URL = url;
    }
    public long getTime() {
        return time;
    }
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        sdf.applyPattern("dd MMM yyyy HH:mm:ss z");
        
        return sdf.format(new Date(time));
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getDID() {
        return DID;
    }
    public void setDID(String DID) {
        this.DID = DID;
    }
    public String getStatusDesc() {
        return UrlStatusMappings.getDescription(status);
    }
    public boolean isSuccess() {
        if (DID != null) {
            return true;
        } else {
            return false;
        }
    }
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(SubmissionNotificationItem otherItem) {
		String url = URL.toLowerCase();
		String otherUrl = otherItem.getURL().toLowerCase();
		return url.compareTo(otherUrl);
	}
    
} //- Class SubmissionNotificationItem
