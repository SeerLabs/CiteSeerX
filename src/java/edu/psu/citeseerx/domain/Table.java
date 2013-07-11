package edu.psu.citeseerx.domain;
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


import java.io.Serializable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Light-weight bean container for table metadata.
 * 
 * @author Shuyi Zheng, Pradeep Teregowda
 */
public class Table implements Serializable, XMLSerializable
{
	/* Attributes of a table */
	private long id;
	private String tableID;
	private String proxyID;
	private String doi;
	private int inPage;
	private String content;
	private String reference;
	private String footNote;
	private String caption;
	/* The following are added so that it is easier to index
	 * the table.
	*/
	private int nCites;
	private int paperYear;
	private String authors;
	private String title;
	private String docURL;
	
	public static final long serialVersionUID = 2L;

	/* Fields for XML access */
	public static final String TABLE_ROOT = "table";
	public static final String TABLE_ID = "id";
	public static final String DOI_KEY = "doi";
	public static final String INPAGE = "pageNumInDoc";
	public static final String TABLE_CONTENT = "content";
	public static final String REFERENCES = "referenceText";
	public static final String FOOTNOTE = "footnote";
	public static final String CAPTION = "caption";

	protected static String[] fieldArray = {
		TABLE_ROOT, TABLE_ID, DOI_KEY, INPAGE, TABLE_CONTENT,
		REFERENCES, FOOTNOTE, CAPTION
	};
	
	public Table() {
		id = 0;
		tableID = "";
		doi = "";
		inPage = 0;
		content = "";
		reference ="";
		footNote = "";
		caption = "";
	/*
	 * Following are added so that tables play well with others
	 */
		nCites = 0;
		paperYear = 0;
		authors = "";
		title = "";
		docURL = "";
	}
   
	/*
	 * Getters and Setters for the Attributes
	 * 
	 */
	
	public long getID() {
		return id;
	}
	public void setID(long in) {
		id = in;
	}
	
    public String getTableID() {
    	return tableID;
    }
    public void setTableID(String id) {
    	tableID = id;
    }

    public String getProxyKey() {
    	return proxyID;
    }
    public void setProxyKey(String pid) {
    	proxyID = pid;
    }
    
    public String getPaperIDForTable() {
    	return doi;
    }
    public void setPaperIDForTable(String paperDOI) {
    	doi = paperDOI;
    }

    public int getTableOccursInPage() {
    	return inPage;
    }
    public void setTableOccursInPage(int page) {
    	inPage = page;
    }

    public String getContent() {
    	return content;
    }
    public void setContent(String tableContents) {
    	content = tableContents;
    }
    public String getLimitedContent() {
    	int maxint = content.length();
    	if(maxint > 65535) { maxint = 65535; }
    	return content.substring(0, maxint);	
    }

    public String getTableReference() {
    	return reference;
    }
    public void setTableReference(String references) {
    	reference = references;
    }

    public String getFootNote() {
    	return footNote;
    }
    public void setFootNote(String note) {
    	footNote = note;
    }

    public void setCaption(String captionInfo) {
    	caption = captionInfo;
    }
    public String getCaption() {
    	return caption;
    }
    
    
    public void setYear(int pYear) {
    	paperYear = pYear;
    }
    public int getYear() {
    	return paperYear;
    }
    
    public void setCitedBy(int count) {
    	nCites = count;
    }
    public int getCitedBy() {
    	return nCites;
    }
    
    public String getPaperAuthors() {
    	return authors;
    }
    public void setPaperAuthors(String commaSep) {
    	authors = commaSep;
    }
    
    public void setPaperTitle(String ttl) {
    	title = ttl;
    }
    public String getPaperTitle() {
    	return title;
    }
    public void setDocURL(String url) {
    	docURL = url;
    }
    public String getDocURL(String url) {
    	return docURL;
    }
    /*
     * (non-Javadoc)
     * Make the object attributes into a string
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	
    	StringBuffer buf = new StringBuffer();
    	buf.append("ID: "+tableID+"\n");
    	buf.append("DOI: "+doi+"\n");
		buf.append("Page: "+Integer.toString(inPage)+"\n");
		buf.append("Content: "+content+"\n");
		buf.append("References: "+reference+"\n");
		buf.append("Footnotes: "+footNote+"\n");
		buf.append("Caption: "+caption+"\n");

		return buf.toString();
    }

    /*
     * (non-Javadoc)
     * Use build XML to return the string
     * @see edu.psu.citeseerx.domain.XMLSerializable#toXML(boolean)
     */
    public String toXML(boolean s) {
	StringBuffer xml = new StringBuffer();
        buildXML(xml, s);
        return xml.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.psu.citeseerx.domain.XMLSerializable#toXML(java.io.OutputStream, boolean)
     */
    
    public void toXML(OutputStream out, boolean sysData) throws IOException {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        out.write(xml.toString().getBytes("utf-8"));
    }

    public void fromXML(InputStream xmlin) throws IOException {
    	SAXBuilder builder = new SAXBuilder();
        try {
            org.jdom.Document doc = builder.build(xmlin);
            Element root = doc.getRootElement();
            fromXML(root);
            
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        
    }


    public void fromXML(Element root) throws JDOMException {
	if(!root.getName().equals(TABLE_ROOT)) {
		throw new JDOMException("Root is "+root.getName()+" expected "
		        +TABLE_ROOT);
	}
	else {
		tableID = root.getAttributeValue(TABLE_ID);
		List<Element> rootChildren = root.getChildren();
        	for (Iterator<Element> it = rootChildren.iterator(); 
        	it.hasNext(); ) {
            Element child = (Element)it.next();
	     	if (child.getName().equals(INPAGE)) {
				inPage = Integer.parseInt(child.getValue());
			}
			else if (child.getName().equals(CAPTION)) {
				caption = child.getValue();
			}
			else if (child.getName().equals(TABLE_CONTENT)) {
				XMLOutputter cout = new XMLOutputter();
				content = cout.outputString(child);
			}
			else if (child.getName().equals(FOOTNOTE)) {
				footNote = child.getValue();
			}
			else if (child.getName().equals(REFERENCES)) {
				reference = child.getValue(); 
			}
        }
	}
	
    }

    public void buildXML(StringBuffer xml, boolean sysData) {
    	Element tabRec = buildXML(); 
		XMLOutputter bXML = new XMLOutputter();
		String tout = bXML.outputString(tabRec);
		xml.append(tout);
    }
    
    public Element buildXML() {
    	Element tabRec = new Element(TABLE_ROOT);
    	tabRec.setAttribute(TABLE_ID,tableID);
    	Element inpageXML = new Element(INPAGE);
    	tabRec.addContent(inpageXML);
    	inpageXML.addContent(Integer.toString(inPage));
    	Element capXML = new Element(CAPTION);
    	capXML.addContent(caption);
    	tabRec.addContent(capXML);
    	Element cXML = new Element(TABLE_CONTENT);
    	cXML.addContent(content);
    	tabRec.addContent(cXML);
    	Element footE = new Element(FOOTNOTE);
    	footE.addContent(footNote);
		tabRec.addContent(footE);
		Element referXML = new Element(REFERENCES);
		tabRec.addContent(referXML);
		return tabRec;
    }
    
}
