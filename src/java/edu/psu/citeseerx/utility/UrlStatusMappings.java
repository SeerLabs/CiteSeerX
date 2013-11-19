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
package edu.psu.citeseerx.utility;

/**
 * Global status code definitions and utility for translating codes to
 * human-readable descriptions.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class UrlStatusMappings {

    // HTTP codes
    
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;
    public static final int PARTIAL = 203;
    public static final int NO_RESP = 204;

    public static final int BAD_REQ = 400;
    public static final int UNAUTH = 401;
    public static final int PAY_REQ = 402;    
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    
    public static final int MOVED = 301;
    public static final int FOUND = 302;
    public static final int METHOD = 303;
    public static final int NOT_MOD = 304;
    
    public static final int INT_ERR = 500;
    public static final int NOT_IMP = 501;
    public static final int OVERLOAD = 502;
    public static final int UNAVAIL = 503;
    
    
    // Heritrix Codes
    
    public static final int ROBOTS = -9998;
    public static final int NO_FETCH = 0;
    public static final int DNS_FAILED = -1;
    public static final int HTTP_FAILED = -2;
    public static final int HTTP_BROKEN = -3;
    public static final int HTTP_TIMEOUT = -4;
    public static final int RUNTIME_EXC = -5;
    public static final int PREREQ_DOM_FAILED = -6;
    public static final int ILLEGAL_URI = -7;
    public static final int RETRY_FAILURE = -8;
    public static final int WAITING = -50;
    public static final int UNQUEUEABLE = -60;
    public static final int ROBOTS_PREREQ = -61;
    public static final int PREREQ_FAILED = -62;
    public static final int PREREQ_UNSCHED = -63;
    public static final int JAVA_ERR = -3000;
    public static final int CHAFF = -4000;
    public static final int TOO_MANY_HOPS = -4001;
    public static final int TOO_MANY_TRANS_HOPS = -4002;
    public static final int OUT_OF_SCOPE = -5000;
    public static final int USER_BLOCKED = -5001;
    public static final int PROC_BLOCKED = -5002;
    public static final int QUOTA_BLOCKED = -5003;
    public static final int RT_BLOCKED = -5004;
    public static final int DELETED = -6000;
    public static final int PROC_KILLED = -7000;
    
    // CSX Codes
    
    public static final int CSX_OK = 10000;
    public static final int CSX_CRAWL_UNPROCESSED = 10001;
    public static final int CSX_CRAWL_STARTED = 10002;
    public static final int CSX_CRAWL_COMPLETE = 10003;

    
    /**
     * @param code
     * @return a textual description of the specified code.
     */
    public static String getDescription(int code) {
        switch(code) {
        
        // HTTP
        
        case OK:
            return "HTTP OK";
        case CREATED:
            return "HTTP Created";
        case ACCEPTED:
            return "Accepted but not complete";
        case PARTIAL:
            return "Partial information";
        case NO_RESP:
            return "No response";
        case BAD_REQ:
            return "Bad HTTP request";
        case UNAUTH:
            return "Unauthorized";
        case PAY_REQ:
            return "Payment required";
        case FORBIDDEN:
            return "Access is forbidden";
        case NOT_FOUND:
            return "File not found (404)";
        case MOVED:
            return "Permanent redirect";
        case FOUND:
            return "Forwarded";
        case METHOD:
            return "Method (303)";
        case NOT_MOD:
            return "Not modified";
        case INT_ERR:
            return "Internal server error";
        case NOT_IMP:
            return "Not implemented";
        case OVERLOAD:
            return "Service overloaded";
        case UNAVAIL:
            return "Service unavailable";
            
        // CSX
        
        case CSX_OK:
            return "Finished ingestion";
        case CSX_CRAWL_UNPROCESSED:
            return "Job waiting";
        case CSX_CRAWL_STARTED:
            return "Crawl started";
        case CSX_CRAWL_COMPLETE:
            return "Crawl completed";
            
        // HERITRIX

        case ROBOTS:
            return "Forbidden by robots.txt";
        case NO_FETCH:
            return "Fetch never tried";
        case DNS_FAILED:
            return "DNS lookup failed";
        case HTTP_FAILED:
            return "HTTP connect failed";
        case HTTP_BROKEN:
            return "HTTP connect broken";
        case HTTP_TIMEOUT:
            return "HTTP timeout";
        case RUNTIME_EXC:
            return "Crawler runtime exception (contact admin)";
        case PREREQ_DOM_FAILED:
            return "Prerequisite domain lookup failed";
        case ILLEGAL_URI:
            return "Illegal URI";
        case RETRY_FAILURE:
            return "Retries all failed";
        case WAITING:
            return "Waiting in queue";
        case UNQUEUEABLE:
            return "Could not be queued";
        case ROBOTS_PREREQ:
            return "Prerequisite robots.txt lookup failed";
        case PREREQ_FAILED:
            return "Prerequisite failure";
        case PREREQ_UNSCHED:
            return "Prerequisite unschedulable";
        case JAVA_ERR:
            return "Severe runtime error (contact admin)";
        case CHAFF:
            return "Appears to be crawler trap";
        case TOO_MANY_HOPS:
            return "Too many hops from seed";
        case TOO_MANY_TRANS_HOPS:
            return "Too many redirects";
        case OUT_OF_SCOPE:
            return "Out of crawl scope";
        case USER_BLOCKED:
            return "Blocked by configuration";
        case PROC_BLOCKED:
            return "Blocked by configuration";
        case QUOTA_BLOCKED:
            return "Quota exceeded";
        case RT_BLOCKED:
            return "Runtime exceeded";
        case DELETED:
            return "Deleted by admin";
        case PROC_KILLED:
            return "Processing thread was killed";
            
        default:
            return "Unmapped code: "+code;
        }
        
    }  //- getDescription

}  //- class UrlStatusMapping
