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
 * Utilities for translating Document DOIs to relative file paths within
 * a repository.  Includes methods to build version file paths.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class FileNamingUtils {

    /**
     * @param doi
     * @return the relative directory name representing the location of 
     * document files within a specific repository.
     */
    public static String getDirectoryFromDOI(String doi) {
        String[] parts = doi.split("\\.");
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<parts.length; i++) {
            buf.append(parts[i]);
            buf.append(System.getProperty("file.separator"));
        }
        return buf.toString();
        
    }  //- getDirectoryFromDOI
    
    
    /**
     * @param doi
     * @return the XML filename based on a DOI.
     */
    public static String buildXMLFileName(String doi) {
        return doi+".xml";
    } //- buildXMLFileName
    
    
    /**
     * @param doi
     * @param version
     * @return the XML filename for a specific metadata version of a document.
     */
    public static String buildVersionFileName(String doi, int version) {
        return doi+"v"+version+".xml";
    } //- buildVersionFileName
     
    
    /**
     * @param doi
     * @return the full relative path for the base XML for a document.
     */
    public static String buildXMLPath(String doi) {
        String dir = getDirectoryFromDOI(doi);
        String file = buildXMLFileName(doi);
        return dir+file;
    } //- buildXMLPath
    
    
    /**
     * @param doi
     * @param version
     * @return the full relative path for a specific version of a document's
     * metadata.
     */
    public static String buildVersionPath(String doi, int version) {
        String dir = getDirectoryFromDOI(doi);
        String file = buildVersionFileName(doi, version);
        return dir+file;
    } //- buildVersionPath
    
}  //- class FileNamingUtils
