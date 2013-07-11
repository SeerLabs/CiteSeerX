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
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;

import edu.psu.citeseerx.utility.SafeText;

/**
 * SourceableDataObject container for author metadata.  Supported data keys
 * include:
 * <ul>
 * <li>DOI_KEY - the author ID.</li>
 * <li>NAME_KEY - the name of the author.</li>
 * <li>AFFIL_KEY - the affiliation of the author.</li>
 * <li>ADDR_KEY - the address of the author.</li>
 * <li>EMAIL_KEY - the email address of the author.</li>
 * <li>ORD_KEY - the order of authorship.</li>
 * </ul>
 *
 * @author Isaac Councill
 */
public class Author extends SourceableDataObject
implements Clusterable, XMLSerializable, XMLTagAttrConstants {

    public static final String AUTH_ROOT   = "author";
    
    public static final String DOI_KEY     = "doi";
    public static final String NAME_KEY    = "name";
    public static final String AFFIL_KEY   = "affil";
    public static final String ADDR_KEY    = "address";
    public static final String EMAIL_KEY   = "email";
    public static final String ORD_KEY     = "order";
    
    protected static final String[] fieldArray =
    {
        CLUST_KEY,NAME_KEY,AFFIL_KEY,ADDR_KEY,EMAIL_KEY,ORD_KEY
    };
    
    protected static final String[] privateFieldData = {EMAIL_KEY};
    
    
    public Author() {
        super();
        for (int i=0; i<privateFieldData.length; i++) {
            addPrivateField(privateFieldData[i]);
        }
    } //- Author
    

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
    
        
    }  //- fromXML
    
    
    public void fromXML(Element root) throws JDOMException {
        if (!root.getName().equals(AUTH_ROOT)) {
            throw new JDOMException("Invalid root \'"+root.getName()+
                    "\', expected \'"+AUTH_ROOT+"\'");
        }
        String id = root.getAttributeValue(ID_ATTR);
        if (id != null) {
            setDatum(DOI_KEY, id);
        }
        List<Element> children = root.getChildren();
        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
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
        xml.append("<"+AUTH_ROOT+" "+ID_ATTR+"=\""+
                getDatum(DOI_KEY, UNENCODED)+"\">");
        for (int i=0; i<fieldArray.length; i++) {
            String field = fieldArray[i];
            if (!sysData && privateFields.containsKey(field)) {
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
        xml.append("</"+AUTH_ROOT+">");

    }  //- buildXML
    
    
    public boolean equals(Author author) {
        for (String field : fieldArray) {
            if (this.getDatum(field) == null &&
                    author.getDatum(field) != null) {
                return false;
            }
            if (this.getDatum(field) != null &&
                    author.getDatum(field) == null) {
                return false;
            }
            if (this.getDatum(field) != null &&
                    author.getDatum(field) != null) {
                if (!this.getDatum(field).equals(author.getDatum(field))) {
                    return false;
                }
            }
        }
        return true;
        
    }  //- equals

}  //- class Author
