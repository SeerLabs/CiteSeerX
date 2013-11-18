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
import java.io.StringReader;
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
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.psu.citeseerx.dao2.logic.CSXExternalMetadataFacade;
import edu.psu.citeseerx.domain.DBLP;

/**
 * Stores all metadata from a new dblp.xml file into the external metadata
 * storage. This data is used by other components in different ways. For example,
 * to obtain information to generate links from summary pages or correct
 * metadata in CiteSeerX corpus.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DBLPMetadataUpdater {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    private static final String[] dblPElements = {
        "article", "inproceedings", "proceedings", "book", "incollection",
        "phdthesis", "mastersthesis", "www"};
    
    // Fields that will appear just once per record
    private static final String[] dblpSingleFields = {"title", "booktitle", 
        "pages", "year", "address", "journal", "volume", "number", "month",
        "url", "ee", "cdrom", "publisher", "note", "crossref", "isbn", "series",
        "school", "chapter"
    };
    
    private static final String[] dblpMultiFields = {
        "author", "editor", "cite"
    };
    
    private Set<String> elements;
    private Set<String> singleFields;
    private Set<String> multiFields;
    
    private CSXExternalMetadataFacade csxemetadata;

    public void setCSXEMETADATA(CSXExternalMetadataFacade csxemetadata) {
        this.csxemetadata = csxemetadata;
    } //- setCSXEMETADATA
    
    private String DBLPDataFile;
    
    /**
     * @param DBLPDataFile DBLP XML file location (full path)
     */
    public void setDBLPDataFile(String DBLPDataFile) {
        this.DBLPDataFile = DBLPDataFile;
    } //- setDBLPDataFile

    private String DBLPDTDFile;
    
    /**
     * @param file DBLP DTD file location (full path)
     */
    public void setDBLPDTDFile(String file) {
        DBLPDTDFile = file;
    } //- setDBLPDTDFile
    
    // ContentHandlers.
    private DBLPHandler dblpHandler;
    
    public DBLPMetadataUpdater() {
        dblpHandler = new DBLPHandler();

        elements = new HashSet<String>();
        for (int i = 0; i < dblPElements.length; ++i) {
            elements.add(dblPElements[i]);
        }
        singleFields = new HashSet<String>();
        for (int i = 0; i < dblpSingleFields.length; ++i) {
            singleFields.add(dblpSingleFields[i]);
        }
        multiFields = new HashSet<String>();
        for (int i = 0; i < dblpMultiFields.length; ++i) {
            multiFields.add(dblpMultiFields[i]);
        }
        
    } //- DBLPMetadataUpdater

    // This one will use a SAX parser.
    public void updateDBLP() {
        try {
            // Get the SAX factory.
            SAXParserFactory factory = SAXParserFactory.newInstance();
            
            // Neither we want validation nor namespaces.
            factory.setNamespaceAware(false);
            factory.setValidating(true);

            SAXParser parser = factory.newSAXParser();
            parser.getXMLReader().setEntityResolver(
                    new DBLPEntityResolver(DBLPDTDFile));

            /*xmlReader.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd", 
                    false);*/
            
            parser.parse(DBLPDataFile, dblpHandler);
        }catch (ParserConfigurationException e) {
            logger.error("The underlaying parser doesn't support the " +
            		"requested feature", e);
        }catch(SAXException e) {
            logger.error("Error", e);
        }catch(IOException e) {
            logger.error("A parsing error has occurred: " + DBLPDataFile, e);
        }
        
    } //- updateDBLP
    

    private void sendRecord(DBLP record) {
        logger.info("Storing: " + record.getDkey());
        try {
            csxemetadata.addDBLPRecord(record);
        }catch (DataAccessException e) {
            logger.error("Storing: " + record.getDkey(), e);
        }
    } //- sendRecord
    
    /*
     * This class handles the dblp element
     */
    private class DBLPHandler extends DefaultHandler{
        
        DBLP record = null;
        StringBuffer authors = new StringBuffer();
        StringBuffer cites = new StringBuffer();
        StringBuffer editors = new StringBuffer();
        StringBuffer elementValue = new StringBuffer();
        int numAuthors = 0;
        int numCites = 0;
        boolean inRecord = false;
        String actualField = null;
        
        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String name,
                Attributes attributes) throws SAXException {
            if ("dblp".equals(name)) {
                // Do nothing this is the root element
                
            }else if (elements.contains(name)) {
                // We are going to process a new record
                initializeData();
                record.setType(name);
                record.setDkey(attributes.getValue("", "key"));
                inRecord = true;
                
            }else{
                elementValue = new StringBuffer();
            }
            actualField = name;
            
        } //- startElement

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (inRecord && (singleFields.contains(actualField) || 
                    multiFields.contains(actualField))) {
                elementValue.append(new String(ch, start, length));
            }
        } //- characters

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            
            if ("dblp".equals(name)) {
                // Do nothing this is the root element
                
            }else if (inRecord && elements.contains(name)) {
                // We finished the record
                processRecord();
                inRecord = false;
                
            }else{
                if (singleFields.contains(actualField)) {
                    processSingleField(actualField);
                }else if (multiFields.contains(actualField)) {
                    processMultiField(actualField);
                }
            }
        } //- endElement
        
        private void initializeData() {
            record = new DBLP();
            authors = new StringBuffer();
            cites = new StringBuffer();
            editors = new StringBuffer();
            numAuthors = 0;
            numCites = 0;
        } //- initializeData
        
        /*
         * Adds the field value to the record
         */
        private void processSingleField(String field) {
            String value;
            try {
                byte[] utf8 = elementValue.toString().getBytes("UTF-8");
                value = new String(utf8, "UTF-8").trim().replaceAll(" +", " ");
            }catch (UnsupportedEncodingException e) {
                value = elementValue.toString();
            }
            if (field.equals("title")) {
                /*
                 * DBLP adds a "." and the end of some titles. Since we do an
                 * exact title match in order to build the links and our titles
                 * don't have that dot, I'm deleting it.
                 * it.
                 */
                int lastDotIndex = value.lastIndexOf('.');
                if (lastDotIndex != -1 && lastDotIndex == value.length()-1) {
                    value = value.substring(0, lastDotIndex);
                }
                record.setTitle(value);
            }
            else if (field.equals("booktitle")) {record.setBookTitle(value);}
            else if (field.equals("pages")) {record.setPages(value);}
            else if (field.equals("address")) {record.setAddress(value);}
            else if (field.equals("journal")) {record.setJournal(value);}
            else if (field.equals("month")) {record.setMonth(value);}
            else if (field.equals("url")) {record.setUrl(value);}
            else if (field.equals("ee")) {record.setEe(value);}
            else if (field.equals("cdrom")) {record.setCdrom(value);}
            else if (field.equals("publisher")) {record.setPublisher(value);}
            else if (field.equals("note")) {record.setNote(value);}
            else if (field.equals("crossref")) {record.setCrossref(value);}
            else if (field.equals("isbn")) {record.setIsbn(value);}
            else if (field.equals("series")) {record.setSeries(value);}
            else if (field.equals("school")) {record.setSchool(value);}
            else if (field.equals("chapter")) {record.setChapter(value);}
            else if (field.equals("year") || field.equals("volume") ||
                    field.equals("number")) {
                try {
                    int numValue = Integer.parseInt(value);
                    if (field.equals("year")) {record.setYear(numValue);}
                    else if (field.equals("volume")) {
                        record.setVolume(numValue);
                    }
                    else if (field.equals("number")) {
                        record.setNumber(numValue);
                    }
                }catch (NumberFormatException e) {
                    // Nothing the field is not set
                }
            }
        } //- processSingleField
        
        /*
         * Add the value to the adequate 
         */
        private void processMultiField(String field) {
            String value;
            try {
                byte[] utf8 = elementValue.toString().getBytes("UTF-8");
                value = new String(utf8, "UTF-8").trim().replaceAll(" +", " ");
            }catch (UnsupportedEncodingException e) {
                value = elementValue.toString();
            }

            if (field.equals("author")) {
                if (authors.length() > 0) {
                    authors.append(',');
                }
                authors.append(value);
                numAuthors++;
            }else if (field.equals("cite")) {
                if (cites.length() > 0) {
                    cites.append(',');
                }
                cites.append(value);
                numCites++;
            }else if (field.equals("editor")) {
                if (editors.length() > 0) {
                    editors.append(',');
                }
                editors.append(value);
            }
        } //- processMultiField
        
        private void processRecord() {
            if (authors.length() > 0) {
                record.setAuthors(authors.toString());
                record.setNumAuthors(numAuthors);
            }
            if (cites.length() > 0) {
                record.setCite(cites.toString());
                record.setNumCites(numCites);
            }
            if (editors.length() > 0) {record.setEditor(editors.toString());}
            
            // Send the record to the database
            sendRecord(record);
        } //- processRecord
    } //- class DBLPDocHandler

    public class DBLPEntityResolver implements EntityResolver {

        private String dtdLocation;
        
        /* (non-Javadoc)
         * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
         */
        public DBLPEntityResolver(String dtdLocation) {
            this.dtdLocation = dtdLocation;
        }

        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            if (dtdLocation != null && dtdLocation.length() > 0) {
                return new InputSource(new StringReader(dtdLocation));
            }else{
                return null;
            }
        }
        
    } //- class DBLPEntityResolver

} //- class DBLPMetadataUpdater
