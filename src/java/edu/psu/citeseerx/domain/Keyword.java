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

import org.jdom.*;

import edu.psu.citeseerx.utility.SafeText;

/**
 * SourceableDataObject container for document keyword data.
 * Supported data keys:
 * <ul>
 * <li>KEYWORD_KEY - the keyword string</li>
 * </ul>
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class Keyword extends SourceableDataObject
implements XMLSerializable, XMLTagAttrConstants {

    public static final String KEYWORD_ROOT  = "keyword";
    
    public static final String DOI_KEY       = "doi";
    public static final String KEYWORD_KEY   = "keyword";
    
    protected final static String[] fieldArray = {KEYWORD_KEY};
    
    public Keyword() {
        super();
        for (int i=0; i<privateFieldData.length; i++) {
            addPrivateField(privateFieldData[i]);
        }
    } //- Keyword
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#toXML(boolean)
     */
    public String toXML(boolean sysData) {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        return xml.toString();
        
    }  //- toXML
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#toXML(java.io.OutputStream, boolean)
     */
    public void toXML(OutputStream out, boolean sysData) throws IOException {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        out.write(xml.toString().getBytes("utf-8"));
        
    }  //- toXML(OutputStream)
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#fromXML(java.io.InputStream)
     */
    public void fromXML(InputStream xmlin) throws IOException {
        throw new IOException("Method not implemented");
        
    }  //- fromXML
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#fromXML(org.jdom.Element)
     */
    public void fromXML(Element root) throws JDOMException {
        if (!root.getName().equals(KEYWORD_ROOT)) {
            throw new JDOMException("Invalid root \'"+root.getName()+
                    "\', expected \'"+KEYWORD_ROOT+"\'");
        }
        String id = root.getAttributeValue(ID_ATTR);
        String src = root.getAttributeValue(SRC_ATTR);
        String val = SafeText.decodeHTMLSpecialChars(root.getValue());
        if (id != null) {
            setDatum(DOI_KEY, id);
        }
        if (src != null) {
            setSource(KEYWORD_KEY, src);
        }
        setDatum(KEYWORD_KEY, val);
        
    }  //- fromXML(Element)
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#buildXML(java.lang.StringBuffer, boolean)
     */
    public void buildXML(StringBuffer xml, boolean sysData) {
        if (hasSourceData(KEYWORD_KEY)) {
            xml.append("<"+KEYWORD_ROOT+" "+ID_ATTR+"=\""+
                    getDatum(DOI_KEY, UNENCODED)+"\">");
        } else {
            xml.append("<"+KEYWORD_ROOT+" "+ID_ATTR+"=\""+
                    getDatum(DOI_KEY, UNENCODED)+"\" "+
                    SRC_ATTR+"=\""+getSource(KEYWORD_KEY)+"\">");
        }
        xml.append(getDatum(KEYWORD_KEY, ENCODED));
        xml.append("</"+KEYWORD_ROOT+">");

    }  //- buildXML
    
}  //- class Keyword
