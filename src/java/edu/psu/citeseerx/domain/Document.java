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
package edu.psu.citeseerx.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.psu.citeseerx.utility.SafeText;

/**
 * SourceableDataObject container for document metadata.  Supported data keys
 * include:
 * <ul>
 * <li>DOI_KEY - the ID of this document.</li>
 * <li>TITLE_KEY</li>
 * <li>ABSTRACT_KEY</li>
 * <li>YEAR_KEY</li>
 * <li>VENUE_KEY</li>
 * <li>VEN_TYPE_KEY</li>
 * <li>PAGES_KEY</li>
 * <li>VOL_KEY</li>
 * <li>NUM_KEY</li>
 * <li>PUBLISHER_KEY</li>
 * <li>PUBADDR_KEY</li>
 * <li>TECH_KEY</li>
 * </ul>
 *
 * @author Isaac Councill
 * @version $Rev: 211 $ $Date: 2012-05-22 15:04:32 -0400 (Tue, 22 May 2012) $
 */
public class Document extends SourceableDataObject
implements Clusterable, XMLSerializable, XMLTagAttrConstants, Versionable {

    public static final String DOC_ROOT         = "document";
    
    public static final String DOI_KEY          = "doi";
    public static final String TITLE_KEY        = "title";
    public static final String ABSTRACT_KEY     = "abstract";
    public static final String YEAR_KEY         = "year";
    public static final String VENUE_KEY        = "venue";
    public static final String VEN_TYPE_KEY     = "venType";
    public static final String PAGES_KEY        = "pages";
    public static final String VOL_KEY          = "volume";
    public static final String NUM_KEY          = "number";
    public static final String PUBLISHER_KEY    = "publisher";
    public static final String PUBADDR_KEY      = "pubAddress";
    public static final String TECH_KEY         = "tech";
    public static final String KEYWORDS_KEY     = "keywords";
    public static final String AUTHORS_KEY      = "authors";
    public static final String CITES_KEY        = "citations";
    public static final String ACKS_KEY         = "acknowledgments";
    public static final String FILEINFO_KEY     = "fileInfo";

    protected static final String[] fieldArray =
    {
        CLUST_KEY,TITLE_KEY,ABSTRACT_KEY,YEAR_KEY,VENUE_KEY,VEN_TYPE_KEY,
        PAGES_KEY,VOL_KEY,NUM_KEY,PUBLISHER_KEY,PUBADDR_KEY,TECH_KEY,
        KEYWORDS_KEY,AUTHORS_KEY,CITES_KEY,ACKS_KEY,FILEINFO_KEY
    };
    
    
    protected int version = 0;
    
    private DocumentProperties
    	documentProperties = new DocumentProperties();
    
    public int getVersion() {
        return version;
    } //- getVersion
    
    public void setVersion(int version) {
        this.version = version;
    } //- setVersion
    
    
    protected String versionName;
    
    public String getVersionName() {
        return versionName;
    } //- getVersionName
    
    public void setVersionName(String name) {
        versionName = name;
    } //- setVersionName
    
    
    protected Date versionTime;
    
    public Date getVersionTime() {
        return versionTime;
    } //- getVersionTime
    
    public void setVersionTime(Date versionTime) {
        this.versionTime = versionTime;
    } //- setVersionTime
    
    
    protected String versionRepID;
    
    public String getVersionRepID() {
        return versionRepID;
    } //- getVersionRepID
    
    public void setVersionRepID(String repID) {
        versionRepID = repID;
    } //- setVersionRepID
    
    
    protected String versionPath;
    
    public String getVersionPath() {
        return versionPath;
    } //- getVersionPath
    
    public void setVersionPath(String path) {
        versionPath = path;
    } //- setVersionPath
    
    
    protected boolean versionDeprecated = false;
    
    public boolean isDeprecatedVersion() {
        return versionDeprecated;
    } //- isDeprecatedVersion
    
    public void setVersionDeprecated(boolean isDeprecated) {
        versionDeprecated = isDeprecated;
    } //- setVersionDeprecated
    
    
    protected boolean versionSpam = false;
    
    public boolean isSpamVersion() {
        return versionSpam;
    } //- isSpamVersion
    
    public void setVersionSpam(boolean isSpam) {
        versionSpam = isSpam;
    } //- setVersionSpam
    
    
    protected boolean reindex = true;
    
    public boolean flaggedForIndexing() {
        return reindex;
    } //- flaggedForIndexing
    
    public void setIndexFlag(boolean flag) {
        reindex = flag;
    } //- setIndexFlag
    
    
    public boolean isPublic() {
        return documentProperties.isPublic();
    } //- isPublic
    
    public void setPublic(boolean isPublic) {
        documentProperties.setPublic(isPublic);
    } //- setPublic

    public boolean isDMCA() {
    	return documentProperties.isDMCA();
    } // - isDMCA
    
    public void setDMCA() {
    	documentProperties.setDMCA();
    } // - setDMCA

    public boolean isPDFRedirect() {
	return documentProperties.isPDFRedirect();
    }
    
    public void setState(int toSet) {
    	documentProperties.setState(toSet);
    } // - setState
    
    public int getState() {
    	return documentProperties.getState();
    }
    private int ncites = 0;
    
    public int getNcites() {
        return ncites;
    } //- getNcites
    
    public void setNcites(int ncites) {
        this.ncites = ncites;
    } //-setNcites
    
    
    private int selfCites = 0;
    
    public int getSelfCites() {
        return selfCites;
    } //- getSelfCites
    
    public void setSelfCites(int selfCites) {
        this.selfCites = selfCites;
    } //- setSelfCites
    
    
    public Document() {
        super();
        for (int i=0; i<privateFieldData.length; i++) {
            addPrivateField(privateFieldData[i]);
        }
        documentProperties.setState(DocumentProperties.IS_PUBLIC);
    } //- Document
    
    
    public Long getClusterID() {
        String clustID = data.get(CLUST_KEY);
        if (clustID == null) {
            return new Long(0);
        } else {
            return Long.parseLong(clustID);
        }
        
    }  //- getClusterID

    public void setClusterID(Long id) {
        data.put(CLUST_KEY, id.toString());
    } //- setClusterID
    
    public boolean isClustered() {
        return data.containsKey(CLUST_KEY);
    } //- isClustered
    
    
    protected DocumentFileInfo fileInfo = new DocumentFileInfo();
    
    public DocumentFileInfo getFileInfo() {
        return fileInfo;
    } //- getFileInfo
    
    public void setFileInfo(DocumentFileInfo fileInfo) {
        this.fileInfo = fileInfo;
    } //- setFileInfo
    

    protected List<Author> authors     = new ArrayList<Author>();
    protected List<Keyword> keywords    = new ArrayList<Keyword>();
    protected List<Citation> citations = new ArrayList<Citation>();
    protected List<Acknowledgment> acknowledgments =
        new ArrayList<Acknowledgment>();
    protected List<Tag> tags = new ArrayList<Tag>();
    
    public List<Author> getAuthors() {
        return authors;
    } //- getAuthors
    
    public void addAuthor(Author author) {
        authors.add(author);
    } //- addAuthor
    
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    } //- setAuthors
    
    public List<Keyword> getKeywords() {
        return keywords;
    } //- getKeywords
    
    public void addKeyword(Keyword keyword) {
        keywords.add(keyword);
    } //- addKeyword
    
    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    } //- setKeywords
    
    public List<Citation> getCitations() {
        return citations;
    } //- getCitations
    
    public void addCitation(Citation citation) {
        citations.add(citation);
    } //- addCitation
    
    public List<Acknowledgment> getAcknowledgments() {
        return acknowledgments;
    } //- getAcknowledgments
    
    public void addAcknowledgment(Acknowledgment ack) {
        acknowledgments.add(ack);
    } //- addAcknowledgment
    
    public List<Tag> getTags() {
        return tags;
    } //- getTags
    
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    } //- setTags
    
    
    public String toXML(boolean sysData) {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        return xml.toString();
        
    }  //- toXML
    
    
    public void toXML(OutputStream out, boolean sysData) throws IOException {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        out.write(xml.toString().getBytes("utf-8"));

        
    }  //- toXML(OutputStream)
    
    
    public void fromXML(InputStream xmlin) throws IOException {
        SAXBuilder builder = new SAXBuilder();
        try {
            org.jdom.Document doc = builder.build(xmlin);
            Element root = doc.getRootElement();
            fromXML(root);
            
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        
    }  //- fromXML
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#fromXML(org.jdom.Element)
     */
    public void fromXML(Element root) throws JDOMException {
        if (!root.getName().equals(DOC_ROOT)) {
            throw new JDOMException("Invalid root \'"+root.getName()+
                    "\', expected \'"+DOC_ROOT+"\'");
        }
        setDatum(DOI_KEY, root.getAttributeValue(ID_ATTR));
        List<Element> rootChildren = root.getChildren();
        for (Iterator<Element> it = rootChildren.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equals(AUTHORS_KEY)) {
                List<Element> authorElts = child.getChildren();
                for (Iterator<Element> authiter = authorElts.iterator();
                authiter.hasNext(); ) {
                    Element authElt = (Element)authiter.next();
                    Author author = new Author();
                    author.fromXML(authElt);
                    addAuthor(author);
                }
                continue;
            }
            if (child.getName().equals(CITES_KEY)) {
                List<Element> citeElts = child.getChildren();
                String src = child.getAttributeValue(SRC_ATTR);
                if (src != null) {
                    setSource(CITES_KEY, src);
                }
                for (Iterator<Element> citeiter = citeElts.iterator();
                citeiter.hasNext(); ) {
                    Element citeElt = (Element)citeiter.next();
                    Citation citation = new Citation();
                    citation.fromXML(citeElt);
                    addCitation(citation);
                }
                continue;
            }
            if (child.getName().equals(KEYWORDS_KEY)) {
                List<Element> keywordElts = child.getChildren();
                for (Iterator<Element> keyiter = keywordElts.iterator();
                keyiter.hasNext(); ) {
                    Element keyElt = (Element)keyiter.next();
                    Keyword keyword = new Keyword();
                    keyword.fromXML(keyElt);
                    addKeyword(keyword);
                }
                continue;
            }
            if (child.getName().equals(ACKS_KEY)) {
                List<Element> ackElts = child.getChildren();
                for (Iterator<Element> ackiter = ackElts.iterator();
                ackiter.hasNext(); ) {
                    Element ackElt = (Element)ackiter.next();
                    Acknowledgment ack = new Acknowledgment();
                    ack.fromXML(ackElt);
                    addAcknowledgment(ack);
                }
                continue;
            }
            if (child.getName().equals(FILEINFO_KEY)) {
                DocumentFileInfo fileInfo = new DocumentFileInfo();
                fileInfo.fromXML(child);
                setFileInfo(fileInfo);
                continue;
            }
            String key = child.getName();
            String src = child.getAttributeValue(SRC_ATTR);
            String val = SafeText.decodeHTMLSpecialChars(child.getValue());
            setDatum(key, val);
            if (src != null) {
                setSource(key, src);
            }
        }
        
    }  //- fromXML(Element)
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#buildXML(java.lang.StringBuffer, boolean)
     */
    public void buildXML(StringBuffer xml, boolean sysData) {
        xml.append("<"+DOC_ROOT+" "+ID_ATTR+"=\""+
                getDatum(DOI_KEY, UNENCODED)+"\">");
        for (int i=0; i<fieldArray.length; i++) {
            String field = fieldArray[i];
            if (!sysData && privateFields.containsKey(field)) {
                continue;
            }
            if (field.equals(AUTHORS_KEY)) {
                xml.append("<"+AUTHORS_KEY+">");
                for (Iterator<Author> it = authors.iterator(); it.hasNext(); ) {
                    it.next().buildXML(xml, sysData);
                }
                xml.append("</"+AUTHORS_KEY+">");
                continue;
            }
            if (field.equals(CITES_KEY) && !citations.isEmpty()) {
                if (hasSourceData(CITES_KEY)) {
                    xml.append("<"+CITES_KEY+" "+SRC_ATTR+"=\""+
                            getSource(CITES_KEY)+"\">");
                } else {
                    xml.append("<"+CITES_KEY+">");
                }
                for (Iterator<Citation> it = citations.iterator();
                it.hasNext(); ) {
                    it.next().buildXML(xml, sysData);
                }
                xml.append("</"+CITES_KEY+">");
                continue;
            }
            if (field.equals(ACKS_KEY) && !acknowledgments.isEmpty()) {
                xml.append("<"+ACKS_KEY+">");
                for (Iterator<Acknowledgment> it = acknowledgments.iterator();
                it.hasNext(); ) {
                    it.next().buildXML(xml, sysData);
                }
                xml.append("</"+ACKS_KEY+">");
                continue;
            }
            if (field.equals(KEYWORDS_KEY)) {
                xml.append("<"+KEYWORDS_KEY+">");
                for (Iterator<Keyword> it = keywords.iterator();
                it.hasNext(); ) {
                    it.next().buildXML(xml, sysData);
                }
                xml.append("</"+KEYWORDS_KEY+">");
                continue;
            }
            if (field.equals(FILEINFO_KEY) && fileInfo != null) {
                fileInfo.buildXML(xml, sysData);
                continue;
            }
            if (getDatum(field, ENCODED) == null) {
                continue;
            }
            if (hasSourceData(field)) {
                xml.append("<"+field+" "+SRC_ATTR+"=\""+getSource(field)+"\">");
            } else {
                xml.append("<"+field+">");
            }
            xml.append(getDatum(field, ENCODED));
            xml.append("</"+field+">");
        }
        xml.append("</"+DOC_ROOT+">");

    }  //- buildXML
    
    /**
     * 
     * @param doc
     * @return true if the given document has the same authors
     */
    public boolean sameAuthors(Document doc) {
        if (getAuthors().size() != doc.getAuthors().size()) {
            return false;
        }
        for (int i=0; i<getAuthors().size(); i++) {
            if (!getAuthors().get(i).equals(doc.getAuthors().get(i))) {
                return false;
            }
        }
        return true;
        
    }  //- sameAuthors
    
    
}  //- class Document
