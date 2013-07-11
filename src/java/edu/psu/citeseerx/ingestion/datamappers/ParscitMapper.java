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

import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.StringReader;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import edu.psu.citeseerx.utility.SafeText;
import edu.psu.citeseerx.utility.CSXConstants;
import edu.psu.citeseerx.domain.Citation;

/**
 * Maps citation parse data into Citation objects, which can be added to
 * a Document object.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class ParscitMapper {

    public static final String ALG_NAME = "ParsCit";
  
    /**
     * Maps citation data into a Document based on an XML string.
     * @param doc
     * @param xml
     */
    public static void map(edu.psu.citeseerx.domain.Document doc, String xml) {

        SAXBuilder builder = new SAXBuilder();
        
        try {
            org.jdom.Document xmldoc = builder.build(new StringReader(xml));
            Element root = xmldoc.getRootElement();
            map(doc, root);
            
        } catch (IOException e) {
            MappingException exc = new MappingException(e.getMessage());
            exc.setStackTrace(e.getStackTrace());
            throw exc;
        } catch (JDOMException e) {
            MappingException exc = new MappingException(e.getMessage());
            exc.setStackTrace(e.getStackTrace());
            throw exc;
        }
                    
    }  //- map
    
    
    /**
     * Maps citation data into a Document based on the root element of
     * a preparsed DOM tree. 
     * @param doc
     * @param root
     * @throws MappingException
     */
    public static void map(edu.psu.citeseerx.domain.Document doc, Element root)
    throws MappingException {

        if (!root.getAttributeValue("name").equals(ALG_NAME)) {
            throw new MappingException("Root name attribute is not what " +
                    "was expected: found "+root.getAttributeValue("name")+
                    ", expected "+ALG_NAME);
        }
            
        String algName = root.getAttributeValue("name");
        String algVers = root.getAttributeValue("version");
        String src = algName + " " + algVers;
            
        doc.setSource(edu.psu.citeseerx.domain.Document.CITES_KEY, src);
            
        Element listRoot = root.getChild("citationList");
        List<Element> children = listRoot.getChildren();
        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equalsIgnoreCase("citation")) {
                String validStr = child.getAttributeValue("valid");
                if (validStr != null) {
                    boolean valid = Boolean.parseBoolean(validStr);
                    if (!valid) {
                        continue;
                    }
                }
                doc.addCitation(mapCitation(child));
            }
        }
            
    }  //- map
    
    
    /**
     * Creates a Citation based on the root node of a preparsed citation XML
     * element. 
     * @param citeElt
     * @return a Citation based on the root node of a preparsed citation XML
     * element. 
     */
    protected static Citation mapCitation(Element citeElt) {
        
        Citation citation = new Citation();
        
        List<Element> children = citeElt.getChildren();
        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            
            if (child.getName().equalsIgnoreCase("authors")) {
                mapAuthors(citation, child);
            }
            if (child.getName().equalsIgnoreCase("contexts")) {
                mapContexts(citation, child);
            }
            if (child.getName().equalsIgnoreCase("title")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_TITLE) {
                    val = val.substring(0, CSXConstants.MAX_DOC_TITLE);
                }
                citation.setDatum(Citation.TITLE_KEY, val);
            }
            if (child.getName().equalsIgnoreCase("date")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                try {
                    int ival = Integer.parseInt(val);
                    citation.setDatum(Citation.YEAR_KEY, val);                    
                } catch (NumberFormatException e) {}
            }
            if (child.getName().equalsIgnoreCase("journal")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_VENUE) {
                    val = val.substring(0, CSXConstants.MAX_DOC_VENUE);
                }
                citation.setDatum(Citation.VENUE_KEY, val);
                citation.setDatum(Citation.VEN_TYPE_KEY, "JOURNAL");
            }
            if (child.getName().equalsIgnoreCase("booktitle")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_VENUE) {
                    val = val.substring(0, CSXConstants.MAX_DOC_VENUE);
                }
                citation.setDatum(Citation.VENUE_KEY, val);
                citation.setDatum(Citation.VEN_TYPE_KEY, "CONFERENCE");
            }
            if (child.getName().equalsIgnoreCase("tech")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_TECH) {
                    val = val.substring(0, CSXConstants.MAX_DOC_TECH);
                }
                citation.setDatum(Citation.TECH_KEY, val);
                citation.setDatum(Citation.VEN_TYPE_KEY, "TECHREPORT");
            }
            if (child.getName().equalsIgnoreCase("volume")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                try {
                    int ival = Integer.parseInt(val);
                    citation.setDatum(Citation.VOL_KEY, val);
                } catch (NumberFormatException e) {}
            }
            if (child.getName().equalsIgnoreCase("number")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                try {
                    int ival = Integer.parseInt(val);
                    citation.setDatum(Citation.NUMBER_KEY, val);
                } catch (NumberFormatException e) {}  
            }
            if (child.getName().equalsIgnoreCase("pages")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_PAGES) {
                    val = val.substring(0, CSXConstants.MAX_DOC_PAGES);
                }
                citation.setDatum(Citation.PAGES_KEY, val);
            }
            if (child.getName().equalsIgnoreCase("editor")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                citation.setDatum(Citation.EDITORS_KEY, val);
            }
            if (child.getName().equalsIgnoreCase("publisher")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_PUBL) {
                    val = val.substring(0, CSXConstants.MAX_DOC_PUBL);
                }
                citation.setDatum(Citation.PUBLISHER_KEY, val);
            }
            if (child.getName().equalsIgnoreCase("institution")) {
                if (citation.getDatum(Citation.VENUE_KEY, Citation.UNENCODED)
                        != null) {
                    String val =
                        SafeText.decodeHTMLSpecialChars(child.getValue());
                    if (val.length() > CSXConstants.MAX_DOC_VENUE) {
                        val = val.substring(0, CSXConstants.MAX_DOC_VENUE);
                    }
                    citation.setDatum(Citation.VENUE_KEY, val);
                }
            }
            if (child.getName().equalsIgnoreCase("location")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_PUBADDR) {
                    val = val.substring(0, CSXConstants.MAX_DOC_PUBADDR);
                }
                citation.setDatum(Citation.PUB_ADDR_KEY, val);
            }
            if (child.getName().equalsIgnoreCase("marker")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                //citation.setDatum(Citation.MARKER_KEY, val);
            }
            if (child.getName().equalsIgnoreCase("rawString")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                citation.setDatum(Citation.RAW_KEY, val);
            }
        }
        
        return citation;
        
    }  //- mapCitation
    
    
    /**
     * Maps author data into a Citation object.
     * @param citation
     * @param authRoot
     */
    protected static void mapAuthors(Citation citation, Element authRoot) {

        List<Element> children = authRoot.getChildren();
        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equalsIgnoreCase("author")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                citation.addAuthorName(val);
            }
        }

    }  //- mapAuthors
    
    
    /**
     * Maps citation context data into a Citation object.
     * @param citation
     * @param contextRoot
     */
    protected static void mapContexts(Citation citation, Element contextRoot) {
        
        List<Element> children = contextRoot.getChildren();
        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equalsIgnoreCase("context")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                citation.addContext(val);
            }
        }
        
    }  //- mapContexts
    
}  //- class ParscitMapper
