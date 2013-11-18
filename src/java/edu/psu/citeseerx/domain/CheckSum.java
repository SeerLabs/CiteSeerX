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
package edu.psu.citeseerx.domain;

/**
 * Bean container for document file checksum information.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CheckSum {

    private String sha1;
    private String doi;
    private String fileType;
    
    public CheckSum() { }
    
    public CheckSum(String sha1, String doi, String fileType) {
        this.sha1 = sha1;
        this.doi = doi;
        this.fileType = fileType;
    } //- CheckSum
    
    public String getFileType() {
        return fileType;
    } //- getFileType
    public void setFileType(String fileType) {
        this.fileType = fileType;
    } //- setFileType
    public String getSha1() {
        return sha1;
    } //- getSha1
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    } //- setSha1
    public String getDOI() {
        return doi;
    } //- getDOI
    public void setDOI(String doi) {
        this.doi = doi;
    } //- setDOI
    
} //- CheckSum
