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

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.List;

import edu.psu.citeseerx.dao2.logic.CSXDAO;

import edu.psu.citeseerx.domain.Algorithm;
import edu.psu.citeseerx.domain.AlgorithmSet;
import edu.psu.citeseerx.domain.CheckSum;
import edu.psu.citeseerx.repository.RepositoryMap;
import edu.psu.citeseerx.utility.FileNamingUtils;
import edu.psu.citeseerx.utility.FileUtils;
import edu.psu.citeseerx.utility.XMLFileNameFilter;

/**
 * This class imports Algorithm information extracted from parsed documents 
 *
 * @author Sumit Bathia
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class AlgorithmIngester {
    
    private CSXDAO csxdao;
    
    /**
     * @return the csxdao
     */
    public CSXDAO getCSXDAO() {
        return csxdao;
    } //- getCsxdao
    
    /**
     * @param csxdao the csxdao to set
     */
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCsxdao
    
    private RepositoryMap repositoryMap;
    
    /**
     * @return the repositoryMap
     */
    public RepositoryMap getRepositoryMap() {
        return repositoryMap;
    } //- getRepositoryMap

    /**
     * @param repositoryMap the repositoryMap to set
     */
    public void setRepositoryMap(RepositoryMap repositoryMap) {
        this.repositoryMap = repositoryMap;
    } //- setRepositoryMap
    
    protected String repositoryID;

    private final static String sep = System.getProperty("file.separator");

    /**
     * Sends Algorithm information to persistent storage
     * @param aobj
     * @param doi
     * @return
     * @throws Exception
     */
    public int importAlgorithm(Algorithm aobj, String doi) throws Exception {
        aobj.setPaperIDForAlgorithm(doi);
        csxdao.insertAlgorithm(aobj);
        return 0;
    } //- importAlgorithm
    
    /**
     * Send to persistent storage a set of algorithms 
     * @param fileName
     * @return
     */
    public int importAlgorithmSet(String fileName) {

        AlgorithmSet set = new AlgorithmSet();
        try {
            File algFile = new File(fileName);
            set.fromXML(new FileInputStream(algFile));
            CheckSum matchingDoc = findDocument(set.getProxyKey());
            String doi = matchingDoc.getDOI();
            for (Algorithm indiv: set.getAlgorithms()) {
                    importAlgorithm(indiv, doi);
            }
    
            String dir = FileNamingUtils.getDirectoryFromDOI(doi);
            String fullDestDir =
            repositoryMap.getRepositoryPath(repositoryID) + sep + dir;
            String dest = fullDestDir + sep + doi + ".alg";
            File destFile = new File(dest);
            FileUtils.copy(algFile, destFile);
            // Move the Algorithm file into the repository

            return 0;
        }
        catch(Exception e) {
            System.out.println("Ingestion Failed");
            e.printStackTrace();
            return -1;
        }
    } //- importAlgorithmSet
    
    protected CheckSum findDocument(String sha1) throws SQLException {
        // SHA1 key returned
        List<CheckSum> chksumDoc = csxdao.getChecksums(sha1);
        if(chksumDoc.isEmpty()) {
            return null;
        }
        else {
            return chksumDoc.get(0);
        }
    } //- findDocument
    
    /**
     * Ingest the content of a folder which contains algorithm information 
     * @param args
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
               if (source.getName().endsWith(".alg")) {
                   try {
                       importAlgorithmSet(source.getAbsolutePath());
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
           }
        }
    } //- ingestDirectories
} //- class AlgorithmIngester