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
 * Interface to be implemented by domain objects that support versioning.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public interface Versionable {

    /**
     * 
     * @return the current version
     */
    public int getVersion();
    
    /**
     * Set the object version
     * @param version
     */
    public void setVersion(int version);
    
    /**
     * 
     * @return The object's version name
     */
    public String getVersionName();
    
    /**
     * Gives a name to the current version
     * @param name
     */
    public void setVersionName(String name);
    
    /**
     * 
     * @return The repository ID of the current version
     */
    public String getVersionRepID();
    
    /**
     * Sets the repository ID where the version is stored
     * @param repID
     */
    public void setVersionRepID(String repID);
    
    /**
     * 
     * @return Path to the version file
     */
    public String getVersionPath();
    
    /**
     * Stores the path to the version file
     * @param path
     */
    public void setVersionPath(String path);
    
    /**
     * 
     * @return true is this is a deprecated version
     */
    public boolean isDeprecatedVersion();
    
    /**
     * If true, sets this version as deprecated
     * @param isDeprecated
     */
    public void setVersionDeprecated(boolean isDeprecated);
    
    /**
     * 
     * @return true if this version is considered as spam
     */
    public boolean isSpamVersion();
    
    /**
     * If true, sets this version as spam
     * @param isSpam
     */
    public void setVersionSpam(boolean isSpam);
    
} //- interface Versionable
