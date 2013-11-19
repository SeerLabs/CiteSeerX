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
package edu.psu.citeseerx.doi;

/**
 * Convenience class for easily deploying DOI Server in a mixture
 * of settings, including web service containers.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class WSInterface {

    /**
     * 
     * @param doiType
     * @return the next digital object identifier for the given type
     * @throws Exception
     */
    public String getDOI(int doiType) throws Exception {
        DOIHandler handler = DOIHandler.getInstance();
        return handler.getDOI(doiType);
    } //- getDOI
    
    /**
     * 
     * @return the String representation of the prefix this DOI Server
     * is using.  This is made up of two integers separated by a "." delimeter
     * that will begin each DOI that is created using this server. 
     * @throws Exception
     */
    public String getPrefix() throws Exception {
        DOIHandler handler = DOIHandler.getInstance();
        return handler.getPrefix();
    } //- getPrefix
    
}  //- class WSInterface
