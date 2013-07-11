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
package edu.psu.citeseerx.loaders;

import java.io.*;
import java.util.*;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Utility harness for reading in Spring applicationContext files that
 * defined application structure and configuration.  This class depends
 * on the "csx.boot" and "csx.conf" variables, to be set on the command
 * line as global definitions.  "csx.boot" should point to a bootstrap
 * file that contains a list of files to load from the directory
 * specified by "csx.conf".
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class ContextReader {

    public static ListableBeanFactory loadContext()
    throws IOException {
        String[] contextFiles = getContextFiles();
        return new FileSystemXmlApplicationContext(contextFiles);
    } //- loadContext
    
    
    private static String[] getContextFiles()
    throws IOException {
        
        String bootFile = System.getProperty("csx.boot");
        String confDir = System.getProperty("csx.conf");
        
        FileReader input = new FileReader(bootFile);
        BufferedReader reader = new BufferedReader(input);
        
        ArrayList<String> files = new ArrayList<String>();
        String line;
        
        while((line = reader.readLine()) != null) {
            if (line.length() > 0 && !line.startsWith("#")) {
                files.add(line);
            }
        }
        String[] fileList = new String[files.size()];
        for (int i=0; i<files.size(); i++) {
            fileList[i] = "file:" + confDir
                + System.getProperty("file.separator") + files.get(i);
            System.out.println(fileList[i]);
        }
        return fileList;
        
    }  //- getContextFiles
    
}  //- class ContextReader
