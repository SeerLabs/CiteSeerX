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
package edu.psu.citeseerx.ingestion.datamappers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.Hub;
import edu.psu.citeseerx.utility.CSXConstants;

/**
 * Maps crawl metadata from crawler-generated .met files into Document
 * objects.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CrawlMetaMapper {

    /**
     * Maps in crawl metadata from an InputStream to a .met file.
     * @param doc
     * @param in
     * @throws IOException
     */
    public static void map(Document doc, InputStream in) throws IOException {
        
        SAXBuilder builder = new SAXBuilder();
        InputStreamReader reader = new InputStreamReader(in);
        
        try {
            org.jdom.Document xmldoc = builder.build(reader);
            Element root = xmldoc.getRootElement();
            map(doc, root);
            
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        
    }  //- map
    
    
    /**
     * Maps in crawl metadata from the root element of a preparsed DOM tree.
     * @param doc
     * @param root
     */
    public static void map(Document doc, Element root) {
        
        DocumentFileInfo finfo = doc.getFileInfo();
        String rootName = "CrawlData";
        
        if (!root.getName().equalsIgnoreCase(rootName)) {
            throw new MappingException("Root name attribute is not what " +
                    "was expected: found " + root.getName() +
                    ", expected " + rootName);
        }
        List<Element> children = root.getChildren();
        Hub hub = new Hub();
            
        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element elt = (Element)it.next();
            if (elt.getName().equalsIgnoreCase("crawlDate")) {
                Date date = new Date(System.currentTimeMillis());
                hub.setLastCrawled(date);
                DateFormat dateFormat = DateFormat.getDateInstance();
                finfo.setDatum(DocumentFileInfo.CRAWL_DATE_KEY,
                        dateFormat.format(date));
            }
            if (elt.getName().equalsIgnoreCase("url")) {
                String val = elt.getValue();
                if (val.length() > CSXConstants.MAX_URL) {
                    val = val.substring(0, CSXConstants.MAX_URL);
                }
                finfo.addUrl(val);
            }
            if (elt.getName().equalsIgnoreCase("parentUrl")) {
                String val = elt.getValue();
                if (val.length() > CSXConstants.MAX_URL) {
                    val = val.substring(0, CSXConstants.MAX_URL);
                }
                hub.setUrl(val);
            }
        }
        if (hub.getUrl() != null && !hub.getUrl().equals("")) {
            finfo.addHub(hub);
        }
            
    }  //- map
    
}  //- class CrawlMetaMapper
