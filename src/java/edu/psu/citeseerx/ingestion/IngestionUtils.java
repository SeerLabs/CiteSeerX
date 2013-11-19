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

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.text.DateFormat;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.ingestion.datamappers.MappingException;

/**
 * Container class for standard ingestion utilities.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class IngestionUtils {

    /**
     * Reads crawl metadata from teh specified metaPath parameter and
     * maps the data into the given Document object.
     * @param doc
     * @param metaPath - path to a .met crawl metadata file.
     * @throws IOException
     */
    public static void insertCrawlMeta(Document doc, String metaPath)
    throws IOException {
        
        System.out.println("metaPath: " + metaPath);
        SAXBuilder builder = new SAXBuilder();
        FileReader reader = null;
        
        DocumentFileInfo finfo = doc.getFileInfo();
        String rootName = "CrawlData";
        
        try {
            reader = new FileReader(metaPath);
            org.jdom.Document xmldoc = builder.build(reader);
            System.out.println(xmldoc.toString());
            Element root = xmldoc.getRootElement();
            if (!root.getName().equalsIgnoreCase(rootName)) {
                throw new MappingException("Root name attribute is not what " +
                        "was expected: found " + root.getName() +
                        ", expected " + rootName);
            }
            List<Element> children = root.getChildren();
            for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
                Element elt = (Element)it.next();
                if (elt.getName().equalsIgnoreCase("crawlDate")) {
                    Date date = new Date(System.currentTimeMillis());
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    finfo.setDatum(DocumentFileInfo.CRAWL_DATE_KEY,
                            dateFormat.format(date));
                }
                if (elt.getName().equalsIgnoreCase("url")) {
                    finfo.addUrl(elt.getValue());
                }
                if (elt.getName().equalsIgnoreCase("parentUrl")) {
                    Hub hub = new Hub();
                    hub.setUrl(elt.getValue());
                    finfo.addHub(hub);
                }
                if (elt.getName().equalsIgnoreCase("SHA1")) {
                    CheckSum cs = new CheckSum();
                    cs.setSha1(elt.getValue());
                    finfo.addCheckSum(cs);
                }
            }
        } catch (JDOMException e) {
            IOException exc = new IOException(e.getMessage());
            exc.setStackTrace(e.getStackTrace());
            throw exc;
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) {}
            }
        }
            
    }  //- insertCrawlMeta
    
}  //- class IngestionUtils
