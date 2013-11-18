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

import java.io.FileFilter;
import java.io.File;

/**
 * Basic FileFilter for returning only XML files from a file listing.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class XMLFileNameFilter implements FileFilter {
    
    /* (non-Javadoc)
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File pathname) {
        String name = pathname.getName();
        name = name.toLowerCase();
        if (pathname.getName().endsWith(".xml")) {
            return true;
        } else {
            return false;
        }
        
    }  //- accept

}  //- class XMLFileNameFilter
