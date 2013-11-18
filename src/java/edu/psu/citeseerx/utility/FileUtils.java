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

import java.io.*;

/**
 * File I/O and naming utilities.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class FileUtils {

    /**
     * Copies a file from one path to another, creating target directory
     * structure when needed.
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copy(File fromFile, File toFile) throws IOException {
    
        if (!fromFile.exists()) {
            throw new IOException("File does not exist: " +
                    fromFile.getPath());
        }
        if (!fromFile.isFile()) {
            throw new IOException("Cannot copy - file is a directory: " +
                    fromFile.getPath());
        }
        if (!fromFile.canRead()) {
            throw new IOException("Source file is unreadable: " +
                    fromFile.getPath());
        }
        
        if (createPath(toFile)) {
            
//            if (!toFile.canWrite()) {
//                throw new IOException("Cannot write to output file: " +
//                        toFile.getPath());
//            }
            
            FileInputStream in = null;
            FileOutputStream out = null;
            
            try {
                in = new FileInputStream(fromFile);
                out = new FileOutputStream(toFile);
            
                byte[] buffer = new byte[4096];
                int bytesRead;
            
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                
            } finally {
                if (in != null) {
                    try { in.close(); } catch (IOException e) {}
                    try { out.close(); } catch (IOException e) {}
                }
            }
            
        } else {
            throw new IOException("Could not make path during copy: " +
                    toFile.getPath());
        }
        
    }  //- copy

    
    /**
     * Creates the path represented by a File object if necessary.
     * @param file
     * @return true if successful and false otherwise.
     */
    public static boolean createPath(File file) {
        
        String parent = file.getParent();
        File parentFile = new File(parent);
        if (parentFile.exists()) {
            return true;
        }
        return parentFile.mkdirs();
        
    }  //- createPath
    
    
    /**
     * Changes the extension of a file name to the new one supplied. 
     * @param filename
     * @param newExt new extension without the preceding ".".
     * @return the given filename with the newExt replacing the old one
     */
    public static String changeExtension(String filename, String newExt) {
        int lastDot = filename.lastIndexOf(".");
        if (lastDot != -1) {
            return filename.substring(0, lastDot) + newExt;
        } else {
            return filename + newExt;
        }
        
    }  //- changeExtension
    
    
    /**
     * @param filename
     * @return the extension of the given filename.
     */
    public static String getExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        if (lastDot != -1) {
            return filename.substring(lastDot);
        } else {
            return "";
        }
        
    }  //- getExtension
    
    
    /**
     * @param filename
     * @return a copy of the supplied String without all characters
     * from the last "." on.
     */
    public static String stripExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        if (lastDot != -1) {
            return filename.substring(0, lastDot);
        } else {
            return filename;
        }
        
    }  //- stripExtension
    
    
    /**
     * Returns a copy of the "path" String that is relative to the supplied
     * "parent" String.  For example, if path="/usr/local/dir/file" and
     * parent="/usr/local", this method will return "dir/file".
     * @param path
     * @param parent
     * @return the new relative path.
     * @throws IOException
     */
    public static String makeRelative(String path, String parent)
    throws IOException {
        if (path.startsWith(parent)) {
            String relPath = path.substring(parent.length());
            if (relPath.startsWith("/")) {
                relPath = relPath.substring(1);
            }
            return relPath;
        } else {
            throw new IOException("Specified path does not " +
                    "overlap with parent");
        }
        
    }  //- makeRelative
    
    
}  //- class FileUtils
