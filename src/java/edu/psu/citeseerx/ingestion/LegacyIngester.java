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
import edu.psu.citeseerx.ingestion.ws.DOIClient;
import edu.psu.citeseerx.utility.*;
import edu.psu.citeseerx.dao2.logic.*;
import edu.psu.citeseerx.citematch.keybased.*;
import edu.psu.citeseerx.updates.*;

/**
 * Used for ingesting content from the old citeseer into the new, while
 * maintaining a mapping table from old IDs to the the new DOIs.  This code
 * has not been maintained, so may no longer work.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 * @deprecated
 */
public class LegacyIngester {

    private DocumentEntryPoint entryPoint;
    private CSXDAO csxdao;
    private CiteClusterDAO citedao;
    
    public LegacyIngester() throws Exception {

        entryPoint = new DocumentEntryPoint();

        FileIngester fileIngester = new FileSystemIngester();
        entryPoint.setFileIngester(fileIngester);
        /*
        csxdao = new CSXDAO();
        csxdao.setDataSource(DBCPFactory.createDataSource("citeseerx"));            
        entryPoint.setCSXDAO(csxdao);
        
        citedao = new CiteClusterDAOImpl();
        citedao.setDataSource(DBCPFactory.createDataSource("citegraph"));
        entryPoint.setCiteClusterDAO(citedao);
        */
        KeyMatcher keymatcher = new KeyMatcher();
        keymatcher.setCiteClusterDAO(citedao);
        keymatcher.setCSXDAO(csxdao);
        entryPoint.setCitationClusterer(keymatcher);

        VersionManager versionManager = new VersionManager();
        versionManager.setCSXDAO(csxdao);

        UpdateManager updateManager = new UpdateManager();
        updateManager.setCSXDAO(csxdao);
        updateManager.setVersionManager(versionManager);

        InferenceUpdater inferenceUpdater = new InferenceUpdater();
        inferenceUpdater.setCiteClusterDAO(citedao);
        inferenceUpdater.setUpdateManager(updateManager);
        entryPoint.setInferenceUpdater(inferenceUpdater);
        
        DOIClient doiClient = new DOIClient();
        entryPoint.setDOIClient(doiClient);
        
    }  //- LegacyIngester
        

    public void ingest(String xmlFile, int legacyID) throws Exception {
        
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
            csxdao.insertLegacyIDMapping(
                    doc.getDatum(Document.DOI_KEY, Document.UNENCODED),
                    legacyID);
        } else {
            for (CheckSum checksum : duplicates) {
                csxdao.insertLegacyIDMapping(checksum.getDOI(), legacyID);
            }
        }
            
        System.out.println("Imported " + xmlFile);
        
    }  //- ingest
    
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Specify directory, starting ID, and ending ID");
            System.exit(0);
        }
        
        String dir = args[0];
        int start = Integer.parseInt(args[1]);
        int end = Integer.parseInt(args[2]);
        
        LegacyIngester ingester = null;
        
        try {
            ingester = new LegacyIngester();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        String sep = System.getProperty("file.separator");
        
        for (int i=start; i<=end; i++) {
            File file = new File(dir+sep+i+".xml");
            if (file.exists()) {
                try {
                    ingester.ingest(file.getAbsolutePath(), i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
    }  //- main
    
    
}  //- class LegacyIngester

