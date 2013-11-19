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
package edu.psu.citeseerx.messaging.messages;

/**
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class SubmissionStatusCodes {

    // Success code
    public static final int OK = 0;

    // Errors codes
    public static final int TIMEOUT = 1;        // Connection is timeout.
    public static final int FORBIDDENROBOT = 2; // Url is in the forbidden list of the robots.txt.
    public static final int URLDUP = 3;         // Url is duplicate.
    public static final int TOOBIG = 4;         // Resource is too big.
    public static final int TOODEEP = 5;        // Resource is too deep (i.e. out of the range of depth).
    public static final int OTHER = 6;          // Other errors.

} //- class SubmissionStatusCodes
