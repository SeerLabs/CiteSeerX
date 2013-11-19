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
package edu.psu.citeseerx.ingestion;

import java.io.*;
import java.util.*;

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.ingestion.datamappers.BatchMapper;
import edu.psu.citeseerx.ingestion.datamappers.CrawlMetaMapper;
import edu.psu.citeseerx.utility.*;

/**
 * This class can be used to import preparsed documents from the filesystem,
 * without going through any web services for additional parsing.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class BatchIngester {

    private DocumentEntryPoint entryPoint;
    
    public void setDocumentEntryPoint(DocumentEntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    } //- setDocumentEntryPoint

    /**
     * Ingest a new document based on information provided by the given XML file 
     * @param xmlFile
     * @throws Exception
     */
    protected void ingest(String xmlFile) throws Exception {
        
        File file = new File(xmlFile);
        if (!file.exists()) {
            throw new IOException("File not found: " + xmlFile);
        }
        
        FileInputStream in = new FileInputStream(file);
        Document doc = BatchMapper.map(in);
        in.close();
        
        String fileBase = FileUtils.stripExtension(xmlFile);
        FileInputStream met = new FileInputStream(fileBase+".met");
        CrawlMetaMapper.map(doc, met);
        met.close();

        //System.out.println(doc.toXML(true));
        List<CheckSum> duplicates =
            entryPoint.importDocument(doc, fileBase);
            
        if (duplicates.isEmpty()) {
        } else {
            for (CheckSum checksum : duplicates) {
                System.err.println(xmlFile+" is duplicate: "+checksum.getDOI());
            }
        }
            
        System.out.println("Imported " + xmlFile);
        
    }  //- ingest
    
    
    /**
     * Imports all preparsed document files in the specified directories.
     * There must be an XML file for each document to ingest, containing all
     * metadata in the appropriate format.  The base name of the XML files
     * should match the base names of the other file resources for
     * specific files; that is, if a file named 1234.xml is found, there should
     * be corresponding files such as 1234.pdf, 1234.txt, etc.
     * @param args an array of directories from which to import files.
     */
    public void ingestDirectories(String[] args) {
        if (args.length <= 0) {
            System.out.println("Please specify one or more directories from " +
                    "which to ingest content");
            System.exit(0);
        }
                
        for (String dir : args) {
           File file = new File(dir);
           if (!file.isDirectory()) {
               System.err.println("Input " + dir +
                       " is not a directory: skipping");
               continue;
           }
           File[] files = file.listFiles(new XMLFileNameFilter());
           for (File source : files) {
               System.out.println("trying "+source.getName());
               if (source.getName().endsWith(".xml")) {
                   try {
                       ingest(source.getAbsolutePath());
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
           }
        }
        
    }  //- ingestDirectories
    
}  //- class BatchIngester
