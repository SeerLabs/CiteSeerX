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


import java.util.Iterator;
import java.util.List;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * table metadata.
 * 
 * @author Shuyi Zheng, Pradeep Teregowda
 */
public class TableSet implements XMLSerializable
{
    public static final long serialVersionUID = 3L;
    public static final String DOC_ROOT = "tables";
    public static final String PROXY_KEY = "sha1";
    
    private List<Table> tableList = new ArrayList<Table>();
    private String proxy_key = null;
    
    protected static String[] fieldArray = {
    	DOC_ROOT, PROXY_KEY
    };

    public TableSet() {
    	super();
    }
    
    public String getProxyKey() {
    	return proxy_key;
    }
    public void setProxyKey(String key) {
    	proxy_key = key;
    }
    public String toXML() {
    	StringBuffer xml = new StringBuffer();
    	buildXML(xml, false);
    	return xml.toString();
    }
 
    public List<Table> getTables() {
    	return tableList;
    }
    
    public void toXML(OutputStream out, boolean sd) throws IOException {
    	StringBuffer xml = new StringBuffer();
    	buildXML(xml,sd);
    	out.write(xml.toString().getBytes("utf-8"));
    }

    public void fromXML(InputStream xmldoc) throws IOException {
    	SAXBuilder builder = new SAXBuilder();
    	try {
    		org.jdom.Document tbrec = builder.build(xmldoc);
    		Element root = tbrec.getRootElement();
    		fromXML(root);
    	}
	catch (JDOMException e) {
		e.printStackTrace();
		}
		
   }

   public void fromXML(Element root) throws JDOMException {
	if(!root.getName().equals(DOC_ROOT)) {
		throw new JDOMException("Invalid Root "+root.getName()+
			", expected ,"+DOC_ROOT);
	}
	else {
		proxy_key = root.getAttributeValue(PROXY_KEY); // Proxy Key
		List<Element> rootChildren = root.getChildren();
		for (Iterator<Element> it = rootChildren.iterator(); 
		it.hasNext();) {
			Element indivTable = (Element)it.next();
			tableList.add(createTable(indivTable, proxy_key));
		}
	}
   }
	
   public String toXML(boolean s) {
	   StringBuffer xml = new StringBuffer();
       buildXML(xml, s);
       return xml.toString();
   }
   
   public void buildXML(StringBuffer stringbuf, boolean s) {
	   Element tabRec = new Element(DOC_ROOT);
	   if(proxy_key != null) {
		   tabRec.setAttribute(PROXY_KEY,proxy_key);
	   }
	   for(int i =0; i < tableList.size(); i++) {
		   tabRec.addContent(tableList.get(i).buildXML());
	   }
	   XMLOutputter output = new XMLOutputter();
	   stringbuf.append(output.outputString(tabRec));
   }   
   
 
   
   
   public Table createTable(Element tblRoot, String proxyid) {
		Table tblObject = new Table();
		tblObject.setProxyKey(proxyid);
    	if(!tblRoot.getName().equals(Table.TABLE_ROOT)) {
    		return null;
    	}
    	else {
    		tblObject.setTableID(tblRoot.getAttributeValue(Table.TABLE_ID));
    		List<Element> rootChildren = tblRoot.getChildren();
            	for (Iterator<Element> it = rootChildren.iterator(); it.hasNext(); ) {
                Element child = (Element)it.next();
    	     	if (child.getName().equals(Table.INPAGE)) {
    				tblObject.setTableOccursInPage(
    						Integer.parseInt(child.getValue()));
    			}
    			else if (child.getName().equals(Table.CAPTION)) {
    				tblObject.setCaption(child.getValue());
    			}
    			else if (child.getName().equals(Table.TABLE_CONTENT)) {
    				tblObject.setContent(child.getValue());
    			}
    			else if (child.getName().equals(Table.FOOTNOTE)) {
    				tblObject.setFootNote(child.getValue());
    			}
    			else if (child.getName().equals(Table.REFERENCES)) {
    				tblObject.setTableReference(child.getValue());
    			}
            }
    	}
    	return tblObject;
    }
   
}
