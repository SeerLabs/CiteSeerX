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

package edu.psu.citeseerx.updates.external.metadata;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.psu.citeseerx.dao2.logic.CSXExternalMetadataFacade;
import edu.psu.citeseerx.domain.ACM;

/**
 * Stores all metadata from a new acm xml file into the external metadata
 * storage. This data is used by other components in different ways. For example,
 * to obtain information to generate links from summary pages or correct
 * metadata in CiteSeerX corpus.
 * The expected XML format is:
 * <pre>
 * <acm>
 *     <doc>
 *         <authors></authors>
 *         <title></title>
 *         <year></year>
 *         <venue></venue>
 *         <url></url>
 *         <pages></pages>
 *         <pub></pub>
 *     </doc>
 *     ...
 * </acm>
 * </pre>
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ACMMetadataUpdater {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    private static final String[] acmElements = {"doc"};
    
    // Fields that will appear just once per record
    private static final String[] acmFields = {
        "authors", "title", "year", "venue", "url", "pages", "pub"
    };
    
    private Set<String> elements;
    private Set<String> fields;
    private CSXExternalMetadataFacade   csxemetadata;

    public void setCSXEMETADATA(CSXExternalMetadataFacade csxemetadata) {
        this.csxemetadata = csxemetadata;
    } //- setCSXEMETADATA
    
    private String ACMDataFile;
    
    /**
     * @param ACMDataFile ACM XML file location (full path)
     */
    public void setACMDataFile(String ACMDataFile) {
        this.ACMDataFile = ACMDataFile;
    } //- setDBLPDataFile

    // ContentHandlers.
    private ACMHandler acmHandler;
    
    public ACMMetadataUpdater() {
        acmHandler = new ACMHandler();
        
        elements = new HashSet<String>();
        for (int i = 0; i < acmElements.length; ++i) {
            elements.add(acmElements[i]);
        }
        fields = new HashSet<String>();
        for (int i = 0; i < acmFields.length; ++i) {
            fields.add(acmFields[i]);
        }
    } //- ACMMetadataUpdater
    
    public void updateACM() {
        try {
            // Get the SAX factory.
            SAXParserFactory factory = SAXParserFactory.newInstance();
            
            // Neither we want validation nor namespaces.
            factory.setNamespaceAware(false);
            factory.setValidating(true);

            SAXParser parser = factory.newSAXParser();

            /*xmlReader.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd", 
                    false);*/
            
            parser.parse(ACMDataFile, acmHandler);
        }catch (ParserConfigurationException e) {
            logger.error("The underlaying parser doesn't support the " +
                    "requested feature", e);
        }catch(SAXException e) {
            logger.error("Error", e);
        }catch(IOException e) {
            logger.error("A parsing error has occurred: " + ACMDataFile, e);
        }
    } //- updateACM
    
    private void sendRecord(ACM record) {
        logger.info("Storing: " + record.getId());
        try {
            csxemetadata.addACMRecord(record);
        }catch (DataAccessException e) {
            logger.error("Storing: " + record.getId(), e);
        }
    } //- sendRecord
    
    /*
     * Provides the necessary methods to the parser to parse the
     * ACM XML file
     */
    private class ACMHandler extends DefaultHandler {
        ACM record = null;
        StringBuffer elementValue = new StringBuffer();
        boolean inRecord = false;
        String actualField = null;

        /*
         * Set initial values for each record
         */
        private void initializeData() {
            record = new ACM();
        } //- initializeData
        
        /*
         * Adds the field value to the record
         */
        private void processField(String field) {
            String value;
            try {
                byte[] utf8 = elementValue.toString().getBytes("UTF-8");
                value = new String(utf8, "UTF-8").trim().replaceAll(" +", " ");
            }catch (UnsupportedEncodingException e) {
                value = elementValue.toString();
            }
            if (field.equals("authors")) {record.setAuthors(value);}
            else if (field.equals("title")) {record.setTitle(value);}
            else if (field.equals("venue")) {record.setVenue(value);}
            else if (field.equals("pages")) {record.setPages(value);}
            else if (field.equals("pub")) {record.setPublication(value);}
            else if (field.equals("year")) {
                try {
                    int numValue = Integer.parseInt(value);
                    if (field.equals("year")) {record.setYear(numValue);}
                }catch (NumberFormatException e) {
                    // Nothing the field is not set
                }
            }else if (field.equals("url")) {
                int index = value.indexOf('=');
                record.setUrl(value.substring(value.indexOf("citation")));
                record.setId(Long.parseLong(value.substring(index+1)));
            }
        } //- processField
        
        /*
         * Make final arrangements and send the record to storage 
         */
        private void processRecord() {
            // Stores the record
            sendRecord(record);
        } //- processRecord

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            if ("acm".equals(qName)) {
                // Do nothing this is the root element
            }else if (elements.contains(qName)) {
                // This is the beginning of a new record
                initializeData();
                inRecord = true;
            }else{
                // This is the beginning of an attribute of the current record
                elementValue = new StringBuffer();
            }
            actualField = qName;
        } //- startElement

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if ("acm".equals(qName)) {
                // Do nothing this is the root element
            }else if (inRecord && elements.contains(qName)) {
                // This is the end of the current record. We process it!
                processRecord();
                inRecord = false;
            }else{
                // This is a field in the actual record. we process the field
                processField(actualField);
            }
        } //- endElement

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (inRecord && fields.contains(actualField)) {
                elementValue.append(new String(ch, start, length));
            }
        } //- characters
        
    } //- class ACMHandler
    
} //- class ACMMetadataUpdater
