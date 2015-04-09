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
package edu.psu.citeseerx.oai;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.UnsupportedEncodingException;

import org.jdom.Element;
import org.jdom.Namespace;

import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.Keyword;
import edu.psu.citeseerx.utility.DateUtils;
import edu.psu.citeseerx.utility.SafeText;

/**
 * Utility class to build OAI-PMH response document for .
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class OAIUtils {
    
    private static final String OAI_PMH_ROOT = "OAI-PMH";
    private static final String OAI_PMH_NAMESPACE = 
        "http://www.openarchives.org/OAI/2.0/";
    private static final String XML_SCHEMA_INSTANCE = 
        "http://www.w3.org/2001/XMLSchema-instance"; 
    private static final String OAI_SCHEMA_LOCATION = 
        "http://www.openarchives.org/OAI/2.0/ " +
        "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";
    private static final String OAI_DC_NAMESPACE = 
        "http://www.openarchives.org/OAI/2.0/oai_dc/";
    private static final String DC_NAMESPACE = 
        "http://purl.org/dc/elements/1.1/";
    private static final String OAI_DC_SCHEMA_LOCATION =
        "http://www.openarchives.org/OAI/2.0/oai_dc/ " +
        "http://www.openarchives.org/OAI/2.0/oai_dc.xsd";
    private static final String OAI_IDENTIFIER_NAMESPACE =
        "http://www.openarchives.org/OAI/2.0/oai-identifier";
    private static final String OAI_IDENTIFIER_SCHEMA_LOCATION =
        "http://www.openarchives.org/OAI/2.0/oai-identifier " +
        "http://www.openarchives.org/OAI/2.0/oai-identifier.xsd";
    private static final String OAI_EPRINTS_NAMESPACE =
        "http://www.openarchives.org/OAI/1.1/eprints";
    private static final String OAI_EPRINTS_SCHEMA_LOCATION =
        "http://www.openarchives.org/OAI/1.1/eprints " +
        "http://www.openarchives.org/OAI/1.1/eprints.xsd";
        
    public static final int OAI_DC_METADATAFORMAT = 1; 

    /**
     * Returns a date in UTC format that conforms to the repository
     * granularity.
     * @param date
     * @return a date in UTC format that conforms to the repository
     */
    public static String buildDatestamp(Date date, String granularity) {
        String stamp = null;
        if ("YYYY-MM-DDThh:mm:ssZ".equals(granularity)) {
            stamp = DateUtils.formatDateTimeISO8601UTC(date);
        }else if ("YYYY-MM-DD".equals(granularity)) {
            stamp = DateUtils.formatDateISO8601UTC(date);
        }
        return stamp;
    } //- buildDatestamp
    
    /**
     * @param root
     * @return A new JDOM document with the specified element as Root
     */
    public static org.jdom.Document createDocument(Element root) {
        return new org.jdom.Document(root);
    } //- createDocument
    
    /**
     * @return A OAI_PMH document with only the root element
     */
    public static Element createElementRoot() {
        Element root = new Element(OAI_PMH_ROOT, getOAIPMHNamespace());
        root.setAttribute("schemaLocation", OAI_SCHEMA_LOCATION, 
                getXMLINSTANCENamespace());
        return root;
    } //- createRootElement
    
    /**
     * Add a responseDate element to the given element
     * @param toAddTo
     * @param respDate
     * @return The created element
     */
    public static Element addResponseDate(Element toAddTo, Date respDate) {
        String theDate = DateUtils.formatDateTimeISO8601UTC(respDate);
        return addElement(toAddTo, "responseDate", theDate, null, 
                getOAIPMHNamespace());
    } //- addResponseDate
    
    /**
     * Adds a request element to the given element
     * @param toAddTo
     * @param elementValue
     * @param attributes Attributes to be added to the element. If no attributes
     * are to be included, attributes can be null or an empty map.
     * The keys in the map are the attributes name, the values in the map are
     * the attributes value.
     * @return The created element
     */
    public static Element addRequest(Element toAddTo, String elementValue, 
            Map<String, String> attributes) {
        return addElement(toAddTo, "request", elementValue, attributes, 
                getOAIPMHNamespace());
    } //- addRequest
    
    /**
     * Adds an error element to the given element
     * @param toAddTo
     * @param elementValue
     * @param attributes Attributes to be added to the element. If no attributes
     * are to be included, attributes can be null or an empty map.
     * The keys in the map are the attributes name, the values in the map are
     * the attributes value.
     * @return The created element
     */
    public static Element addError(Element toAddTo, String elementValue, 
            Map<String, String> attributes) {
        return addElement(toAddTo, "error", elementValue, attributes, 
                getOAIPMHNamespace());
    } //- addError
    
    /**
     * Adds a resumption token to the given element
     * @param toAddTo
     * @param elementValue
     * @return The created element
     */
    public static Element addResumptionToken(Element toAddTo, 
            String elementValue) {
        return addElement(toAddTo, "resumptionToken", elementValue, null, 
                getOAIPMHNamespace());
    } //- addResumptionToken
    
    /**
     * Adds a GetRecord element with the information of a CiteSeerX document. 
     * @param toAddTo
     * @return The created element
     */
    public static Element addGetRecord(Element toAddTo) {
        Element getRecord = addElement(toAddTo, "GetRecord", "", null, 
                getOAIPMHNamespace());
        return getRecord;
    } //- addGetRecord
    
    /**
     * Adds a ListRecords element with the information of a CiteSeerX document 
     * @param toAddTo
     * @return
     */
    public static Element addListRecords(Element toAddTo) {
        return addElement(toAddTo, "ListRecords", "", null, 
                getOAIPMHNamespace());
    } //- addListRecord
    
    /**
     * Adds a MetadataFormat element with the information of a CiteSeerX document 
     * @param toAddTo
     * @return
     */
    public static Element addListMetadataFormats(Element toAddTo) {
        return addElement(toAddTo, "ListMetadataFormats", "", null, 
                getOAIPMHNamespace());
    } //- addListMetadataFormats
    
    /**
     * Adds a ListRecords element with the information of a CiteSeerX document 
     * @param toAddTo
     * @return
     */
    public static Element addMetadataFormat(Element toAddTo, 
            String prefix, String nameSpace, String schema) {
        Element metadataFormat = addElement(toAddTo, "metadataFormat", "", null, 
                getOAIPMHNamespace());
        addElement(metadataFormat, "metadataPrefix", prefix, null, 
                getOAIPMHNamespace());
        addElement(metadataFormat, "schema", schema, null, 
                getOAIPMHNamespace());
        addElement(metadataFormat, "metadataNamespace", nameSpace, null, 
                getOAIPMHNamespace());
        return metadataFormat;
    } //- addMetadataFormat
    
    /**
     * Adds a ListIdentifiers element with the information of a CiteSeerX document 
     * @param toAddTo
     * @return
     */
    public static Element addListIdentifiers(Element toAddTo) {
        return addElement(toAddTo, "ListIdentifiers", "", null, 
                getOAIPMHNamespace());
    } //- addListIdentifiers
    
    /**
     * Adds a record element with the information of a CiteSeerX document. The
     * metadata added to the record depends on the metadataFormat parameter.
     * @param toAddTo
     * @param viewDocURL
     * @param cited
     * @param citing
     * @param rights
     * @param doc
     * @param contributor
     * @param granularity
     * @param metadataFormat
     * @param oaiIdentifier
     * @param dateStamp
     * @return The created element
     */
    public static Element addRecord(Element toAddTo, String viewDocURL,
            List<String>cited, List<String>citing, String rights, 
            edu.psu.citeseerx.domain.Document doc, String contributor, 
            String granularity, int metadataFormat, String oaiIdentifier, 
            Date dateStamp) {
        Element record = addElement(toAddTo, "record", "", null, 
                getOAIPMHNamespace());
        addRecordHeader(record, oaiIdentifier, dateStamp, granularity);
        addMetadata(record, viewDocURL, cited, citing, rights, doc, 
                contributor, granularity, metadataFormat);
        return record;
    } //- addRecord
    
    /**
     * Adds a header element
     * @param toAddTo
     * @param identifier
     * @param dateStamp
     * @param granularity
     * @return
     */
    public static Element addRecordHeader(Element toAddTo, String identifier,
            Date dateStamp, String granularity) {
        Element header = addElement(toAddTo, "header", "", null,
                getOAIPMHNamespace());
        addElement(header, "identifier", identifier, null, 
                getOAIPMHNamespace());
        String theDate = buildDatestamp(dateStamp, granularity);
        addElement(header, "datestamp", theDate, null, getOAIPMHNamespace());
        return header;
    } //- addRecordHeader

    /**
     * Adds an Identify element
     * @param toAddTo
     * @param repositoryName
     * @param baseURL
     * @param protocolVersion
     * @param adminEmails
     * @param earliestDatestamp
     * @param deletedRecord
     * @param granularity
     * @param compression
     * @param identifierScheme
     * @param repositoryIdentifier
     * @param delimiter
     * @param sampleId
     * @param eprints
     * @param eprintsMetadataPolicy
     * @param eprintsDataPolicy
     * @return
     */
    public static Element addIdentify(Element toAddTo, String repositoryName, 
            String baseURL, String protocolVersion, List<String>adminEmails,
            String earliestDatestamp, String deletedRecord, String granularity,
            String[] compression, String identifierScheme, 
            String repositoryIdentifier, String delimiter, String sampleId,
            String eprints, String eprintsMetadataPolicy, 
            String eprintsDataPolicy) {
        Namespace defaultNS = getOAIPMHNamespace();
        Element identify = addElement(toAddTo, "Identify", "", null, 
                defaultNS);
        addElement(identify, "repositoryName", repositoryName, null, defaultNS);
        addElement(identify, "baseURL", baseURL, null, defaultNS);
        addElement(identify, "protocolVersion", protocolVersion, null, 
                defaultNS);
        for (String email : adminEmails) {
            addElement(identify, "adminEmail", email, null, defaultNS);
        }
        addElement(identify, "earliestDatestamp", earliestDatestamp, null, 
                defaultNS);
        addElement(identify, "deletedRecord", deletedRecord, null, defaultNS);
        addElement(identify, "granularity", granularity, null, defaultNS);
        for (String format : compression) {
            addElement(identify, "compression", format, null, defaultNS);
        }
        addIdentifierDescription(identify, identifierScheme, 
                repositoryIdentifier, delimiter, sampleId);
        addEprintsDescription(identify, eprints, eprintsMetadataPolicy, 
            eprintsDataPolicy);
        return identify;
        
    } //- addIdentity
    
    /*
     * Adds a description element with eprints information
     */
    private static Element addEprintsDescription(Element toAddTo, 
            String eprints, String eprintsMetadataPolicy, 
            String eprintsDataPolicy) {
        
        Namespace eprintsNameSp = getOAIEprintsNamespace();
        Element desc = addElement(toAddTo, "description", "", null,
                getOAIPMHNamespace());
        Element eprintsTag = addElement(desc, "eprints", "", null,
                eprintsNameSp);
        eprintsTag.setAttribute("schemaLocation", OAI_EPRINTS_SCHEMA_LOCATION, 
                getXMLINSTANCENamespace());
        Element el = addElement(eprintsTag, "content", "", null, eprintsNameSp);
        addElement(el, "text", eprints, null, eprintsNameSp);
        el = addElement(eprintsTag, "metadataPolicy", "", null, eprintsNameSp);
        addElement(el, "text", eprintsDataPolicy, null, eprintsNameSp);
        el = addElement(eprintsTag, "dataPolicy", "", null, eprintsNameSp);
        addElement(el, "text", eprintsDataPolicy, null, eprintsNameSp);
        return desc;
    } //- addEprintsDescription
    
    /*
     * Adds a description element with the Identifier information
     */
    private static Element addIdentifierDescription(Element toAddTo, 
            String identifierScheme, String repositoryIdentifier, 
            String delimiter, String sampleId) {
        Namespace oaiIDNameSp = getOAIIdentifierNamespace();
        Element desc = addElement(toAddTo, "description", "", null, 
                getOAIPMHNamespace());
        Element oaiid = addElement(desc, "oai-identifier", "", null, 
                oaiIDNameSp);
        oaiid.setAttribute("schemaLocation", OAI_IDENTIFIER_SCHEMA_LOCATION,
                getXMLINSTANCENamespace());
        addElement(oaiid, "scheme", identifierScheme, null, oaiIDNameSp);
        addElement(oaiid, "repositoryIdentifier", repositoryIdentifier, null, 
                oaiIDNameSp);
        addElement(oaiid, "delimiter", delimiter, null, oaiIDNameSp);
        addElement(oaiid, "sampleIdentifier", sampleId, null, oaiIDNameSp);
        return desc;
    } //- addIdentifierDescription
    
    /*
     * Adds a metadata element. The metadata added depends on the 
     * metadataFormat parameter
     */
    private static Element addMetadata(Element toAddTo, String viewDocURL,
            List<String>cited, List<String>citing, String rights, 
            edu.psu.citeseerx.domain.Document doc, String contributor, 
            String granularity, int metadataFormat) {
        
        Element metadata = null;
        if (metadataFormat == OAI_DC_METADATAFORMAT) {
            /*
             * Since OAI_DC doesn't allow qualified elements, for oai_dc we
             * just put the cited documents and not the citing.
             */
            metadata = createOAIDC(toAddTo, viewDocURL, cited, null, rights,
                    doc, contributor, granularity);
        }
        return metadata;
    } //- addMetadata
    
    /*
     * Utility method to add a new child into an Element with the associated
     * attributes using the default name space
     * @param toAddTo The new element is added to this element
     * @param elementName Name for the new element.
     * @param elementValue A value for the new element. It can be null or an
     * empty string
     * @param attributes Attributes to be added to the element. If no attributes
     * are to be included, attributes can be null or an empty map.
     * The keys in the map are the attributes name, the values in the map are
     * the attributes value.
     * @param namespace Name space associated with the element
     * @return The created element
     */
    private static Element addElement(Element toAddTo, String elementName, 
            String elementValue, Map<String, String> attributes, 
            Namespace namespace) {
        
        Element toBeAdded;
        if (namespace != null) {
            toBeAdded = new Element(elementName, namespace);
        }else{
            toBeAdded = new Element(elementName);
        }
        
        if (null != attributes && !attributes.isEmpty()) {
            // Add attributes to this element.
            Set<String> keys = attributes.keySet();
            for (String key : keys) {
                String attrValue = attributes.get(key);
                toBeAdded.setAttribute(key, attrValue);
            }
        }
        
        if (null != elementValue && elementValue.trim().length() > 0) {
	    String newElementValue;
            try {
                byte[] utf8Bytes = elementValue.getBytes("UTF-8");
                newElementValue = new String(utf8Bytes,"UTF-8");
            } catch(UnsupportedEncodingException e){
                newElementValue = elementValue;
            }
            toBeAdded.setText(SafeText.stripBadChars(newElementValue));
        }

        toAddTo.addContent(toAddTo.getContentSize(), toBeAdded);
        return toBeAdded;
    } //- addElement
    
    /*
     * Returns the OAI_PMH name space.
     */
    private static Namespace getOAIPMHNamespace() {
        return Namespace.getNamespace(OAI_PMH_NAMESPACE);
    } //- getOAIPMHNamespace
    
    /*
     * Returns the XML-Instance name space.
     */
    private static Namespace getXMLINSTANCENamespace() {
        return Namespace.getNamespace("xsi", XML_SCHEMA_INSTANCE);
    } //- getXMLINSTANCENamespace
    
    /*
     * Returns the OAI_DC name space.
     */
    private static Namespace getOAIDCNamespace() {
        return Namespace.getNamespace("oai_dc", OAI_DC_NAMESPACE);
    } //- getOAIDCNamespace
    
    /*
     * Returns the DC name space.
     */
    private static Namespace getDCNamespace() {
        return Namespace.getNamespace("dc", DC_NAMESPACE);
    } //- getDCNamespace
    
    /*
     * Returns the OAI Identifier name space.
     */
    private static Namespace getOAIIdentifierNamespace() {
        return Namespace.getNamespace(OAI_IDENTIFIER_NAMESPACE);
    } //- getOAIIdentifierNamespace

    /*
     * Returns the OAI eprints name space.
     */
    private static Namespace getOAIEprintsNamespace() {
        return Namespace.getNamespace(OAI_EPRINTS_NAMESPACE);
    } //- getOAIIdentifierNamespace
    
    private static Element createOAIDC(Element toAddTo, String viewDocURL,
            List<String>cited, List<String>citing, String rights, 
            edu.psu.citeseerx.domain.Document doc, String contributor,
            String granularity) {
        
        Element metadata = addElement(toAddTo, "metadata", "", null, 
                getOAIPMHNamespace());
        Namespace dcNamespace = getDCNamespace();
        Element oaidc = addElement(metadata, "dc", "", null, 
                getOAIDCNamespace());
        oaidc.addNamespaceDeclaration(dcNamespace);
        oaidc.addNamespaceDeclaration(getXMLINSTANCENamespace());
        oaidc.setAttribute("schemaLocation", OAI_DC_SCHEMA_LOCATION, 
                getXMLINSTANCENamespace());
        addElement(oaidc, "title", doc.getDatum(Document.TITLE_KEY, true), null, 
                dcNamespace);
        for (Author creator : doc.getAuthors()) {
            /*
             * All the authors are creators of the document
             */
            addElement(oaidc, "creator", creator.getDatum(Author.NAME_KEY, true), 
                    null, dcNamespace);
        }
        for (Keyword keyword : doc.getKeywords()) {
            addElement(oaidc, "subject", keyword.getDatum(Keyword.KEYWORD_KEY,
                    Keyword.ENCODED), null, dcNamespace);
        }
        addElement(oaidc, "description", doc.getDatum(Document.ABSTRACT_KEY, 
                Document.ENCODED), null, dcNamespace);
        addElement(oaidc, "contributor", contributor, null, dcNamespace);
        addElement(oaidc, "publisher", doc.getDatum(Document.PUBLISHER_KEY, 
                Document.ENCODED), null, dcNamespace);
        // Last time the document was modified; eg metadata was corrected.
        addElement(oaidc, "date", 
                buildDatestamp(doc.getVersionTime(), granularity), null, 
                dcNamespace);
        
        DocumentFileInfo dFileInfo = doc.getFileInfo();
        String cDate = null;
        try {
            SimpleDateFormat sDF = new SimpleDateFormat("MMM dd, yyyy");
            Date crawlDate = sDF.parse(dFileInfo.getDatum(
                    DocumentFileInfo.CRAWL_DATE_KEY));
            cDate = buildDatestamp(crawlDate, granularity); 
        }catch (ParseException e) {
            e.printStackTrace();
            cDate = null;
        }
        addElement(oaidc, "date", cDate, null, dcNamespace);
        try {
            String year = doc.getDatum(Document.YEAR_KEY);
            if (null != year && year.length() > 0) {
                Integer.parseInt(year);
                addElement(oaidc, "date", year, null, dcNamespace);
            }
        }catch (NumberFormatException e) {
            // ignore. We just don't add the element
        }
        List<String> urls = dFileInfo.getUrls();
        if ((urls != null) && (urls.size() > 0)) {
            
            if (dFileInfo.getUrls().get(0).contains(".pdf")) {
                addElement(oaidc, "format", "application/pdf", null,
                        dcNamespace);
            }else if (dFileInfo.getUrls().get(0).contains(".ps")) {
                addElement(oaidc, "format", "application/postscript", null,
                        dcNamespace);
            }else if (dFileInfo.getUrls().get(0).contains(".gz")) {
                addElement(oaidc, "format", "application/zip", null,
                        dcNamespace);
            }
        }
        addElement(oaidc, "type", "text", null, dcNamespace);
        
        /*
         * In the header we already put the OAI identifier as requested by the 
         * OAI-PMH specification. Here we put the URL to the record within the
         * library. So, someone using the harvested data can retrieve the document
         * either using the OAI-PMH identifier from the header or by going to the
         * library using this URL
         */
        String fullURL = viewDocURL + "?doi=" + doc.getDatum(Document.DOI_KEY);
        addElement(oaidc, "identifier", fullURL, null, dcNamespace);

        List<String> urlList = dFileInfo.getUrls();
        if (!urlList.isEmpty()) {
            addElement(oaidc, "source", urlList.get(0), null, 
                dcNamespace);
        }
        addElement(oaidc, "language", "en", null, dcNamespace);
        
        // Add papers cited by this one.
	if(cited != null) {
        	for (String cite : cited) {
            		addElement(oaidc, "relation", cite, null, dcNamespace);
        	}
	}
        
        // Add papers citing by this one.
	if(citing != null) {
        	for (String cite : citing) {
            		addElement(oaidc, "relation", cite, null, dcNamespace);
        	}
	}
        addElement(oaidc, "rights", rights, null, dcNamespace);
        return metadata;
    } //- createOIADC
    
} //- class OAIUtils
