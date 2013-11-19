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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.CheckSum;

/**
 * Maps file information from the fileInfo segment of document XML.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class FileInfoMapper {

    /**
     * Adds conversion trace information to a Document.
     * @param doc
     * @param convTrace
     */
    public static void map(edu.psu.citeseerx.domain.Document doc,
            String convTrace) {

        DocumentFileInfo finfo = new DocumentFileInfo();
        finfo.setDatum(DocumentFileInfo.CONV_TRACE_KEY, convTrace);
        
        doc.setFileInfo(finfo);
        
    }  //- map
    
    /**
     * Maps file information into a Document from the root element of
     * a preparsed DOM tree.
     * @param doc
     * @param root
     * @throws MappingException
     */
    public static void map(edu.psu.citeseerx.domain.Document doc, Element root)
    throws MappingException {
        if (!root.getName().equals("fileInfo")) {
            throw new MappingException("Expected 'fileInfo' element, found " +
                    root.getName());
        }
        
        DocumentFileInfo finfo = new DocumentFileInfo();
        
        List<Element> children = root.getChildren();
        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equals("repository")) {
                finfo.setDatum(DocumentFileInfo.REP_ID_KEY, child.getValue());
            }
            if (child.getName().equals("conversionTrace")) {
                finfo.setDatum(DocumentFileInfo.CONV_TRACE_KEY,
                        child.getValue());
            }
            if (child.getName().equals("checkSums")) {
                Set<String> sums = new HashSet<String>();
                List<Element> checkSumElts = child.getChildren();
                for (Iterator<Element> cit = checkSumElts.iterator(); 
                cit.hasNext(); ) {
                    Element checkSumElt = (Element)cit.next();
                    CheckSum checkSum = new CheckSum();
                    List<Element> cfields = checkSumElt.getChildren();
                    for (Iterator<Element> cfieldit = cfields.iterator();
                    cfieldit.hasNext(); ) {
                        Element cfield = (Element)cfieldit.next();
                        if (cfield.getName().equals("fileType")) {
                            checkSum.setFileType(cfield.getValue());
                        }
                        if (cfield.getName().equals("sha1")) {
                            checkSum.setSha1(cfield.getValue());
                        }
                    }
                    if (!sums.contains(checkSum.getSha1())) {
                        sums.add(checkSum.getSha1());
                        finfo.addCheckSum(checkSum);
                    }
                }
            }
        }
        
        doc.setFileInfo(finfo);
        
    }  //- map
    
    
}  //- class FileInfoMapper
