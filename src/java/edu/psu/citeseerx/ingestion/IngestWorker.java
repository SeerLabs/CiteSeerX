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

import edu.psu.citeseerx.ingestion.ws.BpelClient;
import edu.psu.citeseerx.messaging.messages.NewRecordToIngestMsg;
/* Changed this, the rest of the code doesn't make any sense . so leaving it at that
 * to fix - need to identify the paths, how a DOI is assigned and finally ingestion
 * 
 * */
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.utility.*;

import java.io.*;


/**
 * Runnable job implementation for submitting new records to the ingestion
 * pipeline and storing results upon success.
 * 
 * @author Isaac Councill
 *
 */
public class IngestWorker implements Runnable {
    
    private final NewRecordToIngestMsg msg;
    private final BpelClient bpelClient;
    private final CSXDAO csxdao;
    private final IngestionManager manager;
    
    /**
     * Sets up pointers to needed resources.
     * @param msg
     * @param bpelClient
     * @param csxdao
     * @param manager
     */
    public IngestWorker(NewRecordToIngestMsg msg, BpelClient bpelClient,
            CSXDAO csxdao, IngestionManager manager) {
        
        this.msg = msg;
        this.bpelClient = bpelClient;
        this.csxdao = csxdao;
        this.manager = manager;
        
    }  //- IngestWorker

    
    private String sep = System.getProperty("file.separator");


    public void run() {
        /*
        try {
            String crawlerRepository = msg.getRepositoryID();
            String relCrawlerPath = msg.getFilePath();
            String jobID = msg.getJobID();
            String metaPath = msg.getMetaPath();
            String resourceType = msg.getResourceType();
            String repID = manager.getRepositoryID();
            String crawlerPath =
                manager.getCrawlerRepositoryPath(crawlerRepository)
                + sep + relCrawlerPath;
            String tmpPath =
                manager.getRepositoryPath()+sep+manager.getTmpDir()+sep+jobID;

            if (resourceType.equals(CSXConstants.ARTICLE_TYPE)) {

                String relTmpPath = copyToTmp(crawlerPath, tmpPath);
                Document doc = bpelClient.callService(relTmpPath, repID);

                String fullMetaPath =
                    manager.getCrawlerRepositoryPath(crawlerRepository) + 
                    sep + metaPath;
                IngestionUtils.insertCrawlMeta(doc, fullMetaPath);
                
                copyToRepository(doc);
                csxdao.loadDocument(doc);
                
                msg.acknowledge();
                
                manager.notifyUser(jobID, UrlStatusMappings.CSX_OK,
                        doc.getFileInfo().getUrls().get(0),
                        doc.getDatum(Document.DOI_KEY, Document.UNENCODED));
                
            }
            if (resourceType.equals(CSXConstants.HUB_TYPE)) {

                msg.acknowledge();
            }
                        
        } catch (IngestionException ie) {
            System.out.println("Msg: " + ie.getMessage());
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
  */      
    }  //- run
    
    
    protected String copyToTmp(String crawlerPath, String tmpPath)
    throws IOException {
        
        File fromFile = new File(crawlerPath);

        tmpPath = tmpPath + sep + fromFile.getName();
        File toFile = new File(tmpPath);
        
        FileUtils.copy(fromFile, toFile);
        
        return FileUtils.makeRelative(tmpPath, manager.getRepositoryPath());
        
    }  //- copyToTmp
    
    
    protected void copyToRepository(edu.psu.citeseerx.domain.Document doc)
    throws IOException {
        
        String repPath = manager.getRepositoryPath();

        String doi = doc.getDatum(Document.DOI_KEY, Document.UNENCODED);
        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String repID = manager.getRepositoryID();
        String fullDestDir = repPath + sep + dir;

        DocumentFileInfo finfo = doc.getFileInfo();

        String srcPath = "fixme";
        String fullSrcPath = repPath + sep + srcPath;
        
        String[] extensions = {
                FileUtils.getExtension(fullSrcPath), ".txt", ".body", ".cite"
        };
        
        
        for (String ext : extensions) {
            String src = FileUtils.changeExtension(fullSrcPath, ext);
            String dest = fullDestDir + doi + ext;
            File srcFile = new File(src);
            File destFile = new File(dest);
            FileUtils.copy(srcFile, destFile);
            if (ext.equals(".txt")) {
                // do nothing
            }
            /*
              else if (ext.equals(".body")) {
                finfo.setDatum(DocumentFileInfo.BODY_PATH_KEY,
                        FileUtils.makeRelative(dest, repPath));                
            } else if (ext.equals(".cite")) {
                finfo.setDatum(DocumentFileInfo.CITE_PATH_KEY,
                        FileUtils.makeRelative(dest, repPath));
            } else {
                finfo.setDatum(DocumentFileInfo.FILE_PATH_KEY,
                        FileUtils.makeRelative(dest, repPath));
            }
            */
        }
        
        finfo.setDatum(DocumentFileInfo.REP_ID_KEY, repID);

    }  //- copyToRepository
    
    
}  //- class IngestWorker
