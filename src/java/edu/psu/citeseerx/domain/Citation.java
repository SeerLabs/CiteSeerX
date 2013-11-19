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
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;

import edu.psu.citeseerx.utility.SafeText;

/**
 * MappedDataObject container for citation metadata.  Supported data keys
 * include:
 * <ul>
 * <li>DOI_KEY - the citation ID.</li>
 * <li>AUTHORS_KEY - a comma-separated list of author names.</li>
 * <li>TITLE_KEY</li>
 * <li>VENUE_KEY</li>
 * <li>VEN_TYPE_KEY</li>
 * <li>YEAR_KEY</li>
 * <li>PAGES_KEY</li>
 * <li>EDITORS_KEY</li>
 * <li>PUBLISHER_KEY</li>
 * <li>PUB_ADDR_KEY</li>
 * <li>VOL_KEY</li>
 * <li>NUMBER_KEY</li>
 * <li>TECH_KEY</li>
 * <li>PAPERID_KEY - the ID of the document from which the citation came.</li>
 * <li>RAW_KEY - the raw, unparsed citation string.</li>
 * </ul>
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class Citation extends MappedDataObject
implements Clusterable, XMLSerializable, XMLTagAttrConstants {

    public static final String CITE_ROOT       = "citation";
    
    public static final String DOI_KEY         = "doi";
    public static final String AUTHORS_KEY     = "authors";
    public static final String TITLE_KEY       = "title";
    public static final String VENUE_KEY       = "venue";
    public static final String VEN_TYPE_KEY    = "venType";
    public static final String YEAR_KEY        = "year";
    public static final String PAGES_KEY       = "pages";
    public static final String EDITORS_KEY     = "editors";
    public static final String PUBLISHER_KEY   = "publisher";
    public static final String PUB_ADDR_KEY    = "pubAddress";
    public static final String VOL_KEY         = "volume";
    public static final String NUMBER_KEY      = "number";
    public static final String TECH_KEY        = "tech";
    public static final String RAW_KEY         = "raw";
    public static final String PAPERID_KEY     = "paperid";
    public static final String CONTEXT_KEY     = "contexts"; 

    protected static final String[] fieldArray =
    {
        CLUST_KEY,AUTHORS_KEY,TITLE_KEY,VENUE_KEY,VEN_TYPE_KEY,YEAR_KEY,
        PAGES_KEY,EDITORS_KEY,PUBLISHER_KEY,PUB_ADDR_KEY,VOL_KEY,
        NUMBER_KEY,TECH_KEY,RAW_KEY,PAPERID_KEY,CONTEXT_KEY
    };
    
    public Citation() {
        super();
        for (int i=0; i<privateFieldData.length; i++) {
            addPrivateField(privateFieldData[i]);
        }
    } //- Citation
    
    private boolean self = false;
    
    public boolean isSelf() {
        return self;
    } //- isSelf
    
    public void setSelf(boolean self) {
        this.self = self;
    } //- setSelf
    
    protected List<String> authorNames = new ArrayList<String>();
    protected List<String> contexts    = new ArrayList<String>();

    public List<String> getAuthorNames() {
        return authorNames;
    } //- getAuthorNames
    
    public void addAuthorName(String name) {
        authorNames.add(name);
    } //- addAuthorName
    
    public List<String> getContexts() {
        return contexts;
    } //- getContexts
    
    public void addContext(String context) {
        contexts.add(context);
    } //- addContext
    
    public Long getClusterID() {
        String clustID = data.get(CLUST_KEY);
        if (clustID == null) {
            return new Long(0);
        } else {
            return Long.parseLong(clustID);
        }
        
    }  //- getClusterID
    
    public void setClusterID(Long clusterID) {
        data.put(CLUST_KEY, clusterID.toString());
    } //- setClusterID
    
    public boolean isClustered() {
        return data.containsKey(CLUST_KEY);
    } //- isClustered
    
    
    private List<String> keys = new ArrayList<String>();
    
    public void setKeys(List<String> keys) {
        this.keys = keys;
    } //- setKeys
    
    public List<String> getKeys() {
        return keys;
    } //- getKeys
    
    
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
        throw new IOException("Method not implemented");
        
    }  //- fromXML
    
    
    public void fromXML(Element root) throws JDOMException {
        if (!root.getName().equals(CITE_ROOT)) {
            throw new JDOMException("Invalid root \'"+root.getName()+
                    "\', expected \'"+CITE_ROOT+"\'");
        }
        String id = root.getAttributeValue(ID_ATTR);
        if (id != null) {
            setDatum(DOI_KEY, id);
        }
        List<Element> children = root.getChildren();
        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equals(CONTEXT_KEY)) {
                List<Element> contextElts = child.getChildren();
                for (Iterator<Element> cit = contextElts.iterator();
                cit.hasNext(); ) {
                    Element cElt = (Element)cit.next();
                    String context =
                        SafeText.decodeHTMLSpecialChars(cElt.getValue());
                    addContext(context);
                }
                continue;
            }
            if (child.getName().equals(AUTHORS_KEY)) {
                String authorStr =
                    SafeText.decodeHTMLSpecialChars(child.getValue());
                String[] authors = authorStr.split("\\,");
                for (int i=0; i<authors.length; i++) {
                    addAuthorName(authors[i]);
                }
                continue;
            }
            String key = child.getName();
            String val = SafeText.decodeHTMLSpecialChars(child.getValue());
            setDatum(key, val);
        }
        
    }  //- fromXML(Element)
    
    
    public void buildXML(StringBuffer xml, boolean sysData) {
        xml.append("<"+CITE_ROOT+" "+ID_ATTR+"=\""+
                getDatum(DOI_KEY, UNENCODED)+"\">");
        for (int i=0; i<fieldArray.length; i++) {
            String field = fieldArray[i];
            if (!sysData && privateFields.containsKey(field)) {
                continue;
            }
            if (field.equals(AUTHORS_KEY)  && !authorNames.isEmpty()) {
                xml.append("<"+AUTHORS_KEY+">");
                for (Iterator<String> it = authorNames.iterator();
                it.hasNext(); ) {
                    xml.append(SafeText.cleanXML(it.next()));
                    if (it.hasNext()) {
                        xml.append(",");
                    }
                }
                xml.append("</"+AUTHORS_KEY+">");
                continue;
            }
            if (field.equals(CONTEXT_KEY) && !contexts.isEmpty()) {
                xml.append("<"+CONTEXT_KEY+">");
                for (Iterator<String> it = contexts.iterator();
                it.hasNext(); ) {
                    xml.append("<"+CONTEXT_TAG+">");
                    xml.append(SafeText.cleanXML(it.next()));
                    xml.append("</"+CONTEXT_TAG+">");
                }
                xml.append("</"+CONTEXT_KEY+">");
                continue;
            }
            if (getDatum(field, ENCODED) == null) {
                continue;
            }
            xml.append("<"+field+">");
            xml.append(getDatum(field, ENCODED));
            xml.append("</"+field+">");
        }
        xml.append("</"+CITE_ROOT+">");

    }  //- buildXML
    
}  //- class Citation
