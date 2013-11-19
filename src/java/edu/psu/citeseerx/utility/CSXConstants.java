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
 * Global constants for CiteSeerX.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class CSXConstants {

    //////////////////////////////////
    // Resource constants
    //////////////////////////////////

    public static final String ARTICLE_TYPE = "article";
    public static final String HUB_TYPE = "hub";
    
    public static final int ARTICLE_SUB_ID = 1;
    public static final int HUB_SUB_ID = 5;
    
    
    //////////////////////////////////
    // Messaging constants
    //////////////////////////////////

    public static final String USER_SUBMISSION_PREFIX = "USUB-";
    

    //////////////////////////////////
    // Version constants
    //////////////////////////////////

    public static final String INFERENCE_VERSION = "INFERENCE";
    public static final String USER_VERSION = "USER";

    //////////////////////////////////
    // Author Cluster constants
    //////////////////////////////////

    public static final int UNPROCESS_AUTHOR = 0;   // - mark yet-to-be disambiguate records
    public static final int ERROR_AUTHOR     = 1;   // - mark records that are marked as likely name parsing error
                                                    // by the CsxAuthorFilter eg. "Student Member", "Technische Universit Dresden"
    public static final int NOISE_AUTHOR     = 2;   // - mark records that are marked as noises by the disambiguation clustering
	                                                // algorithm, e.g those that are singleton clusters
	public static final int USER_REMOVED     = 3;   // mark records that was assigned to an author, but was removed by user correction

    //////////////////////////////////
    // Data size constants
    //////////////////////////////////

    public static final int MAX_DOC_TITLE = 255;
    public static final int MAX_DOC_VENUE = 255;
    public static final int MAX_DOC_VENTYPE = 20;
    public static final int MAX_DOC_PAGES = 20;
    public static final int MAX_DOC_PUBL = 100;
    public static final int MAX_DOC_PUBADDR = 100;
    public static final int MAX_DOC_TECH = 100;

    public static final int MAX_AUTH_NAME = 100;
    public static final int MAX_AUTH_AFFIL = 255;
    public static final int MAX_AUTH_ADDR = 255;
    public static final int MAX_AUTH_EMAIL = 100;

    public static final int MAX_KEYWORD = 100;
    
    public static final int MAX_ACK_NAME = 255;
    public static final int MAX_ACK_ETYPE = 20;
    public static final int MAX_ACK_ATYPE = 20;

    public static final int MAX_URL = 255;
    public static final int MAX_CONV_TRACE = 255;
    
}
