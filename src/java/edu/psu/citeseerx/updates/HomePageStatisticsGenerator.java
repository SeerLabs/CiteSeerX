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
package edu.psu.citeseerx.updates;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.psu.citeseerx.dao2.logic.CSXDAO;

/**
 * Utilities for generating statistics that are shown in the home page
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class HomePageStatisticsGenerator {
	
    private final Log logger = LogFactory.getLog(getClass());
    
	private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String FILE_SEP = System.getProperty("file.separator");
	
	private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private String outputDir = "stats";
    
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    } //- setOutputDir
    
    /**
     * Generate all statistics.
     */
    public void genStats() {
        try {
            makeOutputDir();
            Integer prevNumDocs, prevNumCites, prevNumTables;
            prevNumDocs = prevNumCites = prevNumTables = 0;
            getPreviousValues(prevNumDocs, prevNumCites, prevNumTables);
            genHomePageStats(prevNumDocs, prevNumCites, prevNumTables);
        }catch (IOException e) {
            logger.error("An error ocurred while calculating home page " +
            		"statistics", e);
        }
    } //- genStats
    
    /**
     * Generate all statistics and output to the given directory.
     * @param outputDir
     */
    public void genStats(String outputDir) {
        setOutputDir(outputDir);
        genStats();
    } //- genStats
    
    private void makeOutputDir() throws IOException {
        File file = new File(outputDir);
        if (!file.isDirectory()) {
            file.mkdir();
        }
    } //- makeOutputDir
    
    /*
     * Obtain the values, and store then in a file.
     */
    private void genHomePageStats(Integer prevNumDocs, Integer prevNumCites, 
            Integer prevNumTables) throws IOException {
        logger.info("Generating Home page statistics");
    	String fileName = outputDir + FILE_SEP + "home";
    	logger.info("Obtaining data");
    	Integer numDocs = csxdao.getNumberOfDocumentRecords();
    	Integer numCitations = csxdao.getNumberOfCitationsRecords();
    	Integer numTables = csxdao.countTable();
    	
    	logger.info("Writing data");
    	FileWriter writer = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(writer);
        
        numDocs = (numDocs == null) ? new Integer(0) : numDocs;
        numCitations = (numCitations == null) ? new Integer(0) : numCitations;
        numTables = (numTables == null) ? new Integer(0) : numTables;
        
        // First, the actual ones.
        out.write(numDocs.toString());
        out.write('\t');
        out.write(numCitations.toString());
        out.write('\t');
        out.write(numTables.toString());
        out.write(NEW_LINE);
        
        // Second the previous data.
        out.write(prevNumDocs.toString());
        out.write('\t');
        out.write(prevNumCites.toString());
        out.write('\t');
        out.write(prevNumTables.toString());
        out.write(NEW_LINE);
        out.close();
        logger.info("Home page statistics: generated");
    } //- genHomePageStats
    
    private void getPreviousValues(Integer numDocs, Integer numCites, 
            Integer numTables) {
        logger.info("Obtaining previous values");
        
        String fileName = outputDir + FILE_SEP + "home";
        
        BufferedReader reader = null;
        try {
            FileReader fr = new FileReader(fileName);
            reader = new BufferedReader(fr);
            String line = null;
            
            /* 
             * Two lines file:
             *   1. Actual values
             *   2. Previous values
             */
            line = reader.readLine(); // 
            
            String[] tokens = line.split("\t");
            if (tokens.length>=3) {
                numDocs = Integer.parseInt(tokens[0]);
                numCites = Integer.parseInt(tokens[1]);
                numTables = Integer.parseInt(tokens[2]);
            }
        }catch (Exception e) {
            numDocs = numCites = numTables = 0;
        }finally{
            if (reader != null) {
                try {
                    reader.close();
                }catch (Exception e) {
                    // Nothing can be done
                }
            }
        }
    } //- getPreviousValues
} //- Class HomePageStatisticsGenerator
