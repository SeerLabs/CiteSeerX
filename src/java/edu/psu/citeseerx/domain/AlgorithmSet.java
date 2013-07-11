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
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Algorithm metadata
 * @author Sumit Bathia
 * @author Juan Pablo Fernandez
 * @version $Rev$ $Date$
 */
public class AlgorithmSet implements XMLSerializable {

    public static final String DOC_ROOT = "algorithms";
    public static final String PROXY_KEY = "sha1";
    
    private List<Algorithm> algorithmList = new ArrayList<Algorithm>();
    private String proxy_key = null;
    
    protected static String[] fieldArray = {
        DOC_ROOT, PROXY_KEY
    };

    public AlgorithmSet() {
        super();
    } //- AlgorithmSet
    
    public String getProxyKey() {
        return proxy_key;
    } //- getProxyKey
    
    public void setProxyKey(String key) {
        proxy_key = key;
    } //- getProxyKey
    
    /**
     * @return All the Algorithms
     */
    public List<Algorithm> getAlgorithms() {
        return algorithmList;
    } //- getAlgorithms
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#buildXML(java.lang.StringBuffer, boolean)
     */
    public void buildXML(StringBuffer buffer, boolean sysData) {
        Element algRec = new Element(DOC_ROOT);
        if(proxy_key != null) {
                algRec.setAttribute(PROXY_KEY,proxy_key);
        }
        for(int i =0; i < algorithmList.size(); i++) {
                algRec.addContent(algorithmList.get(i).buildXML());
        }
        XMLOutputter output = new XMLOutputter();
        buffer.append(output.outputString(algRec));
    } //- buildXML

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#fromXML(java.io.InputStream)
     */
    public void fromXML(InputStream in) throws IOException {
        SAXBuilder builder = new SAXBuilder();
        try {
            org.jdom.Document algrec = builder.build(in);
            Element root = algrec.getRootElement();
            fromXML(root);
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
    } //- fromXML

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#fromXML(org.jdom.Element)
     */
    public void fromXML(Element root) throws JDOMException {
        if(!root.getName().equals(DOC_ROOT)) {
            throw new JDOMException("Invalid Root "+root.getName()+
                    ", expected ,"+DOC_ROOT);
        }
        else {
            proxy_key = root.getAttributeValue(PROXY_KEY); // Proxy Key
            List<Element> rootChildren = root.getChildren();
            for (Element indivAlgorithm : rootChildren) {
                algorithmList.add(createAlgorithm(indivAlgorithm, proxy_key));
            }
        }

    } //- fromXML

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#toXML(boolean)
     */
    public String toXML(boolean sysData) {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        return xml.toString();
    } //- toXML
    
    public String toXML() {
        return toXML(false);
    } //- toXML

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#toXML(java.io.OutputStream, boolean)
     */
    public void toXML(OutputStream out, boolean sysData) throws IOException {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        out.write(xml.toString().getBytes("utf-8"));

    } //-toXML
    
    public Algorithm createAlgorithm(Element algRoot, String proxyid) {
        Algorithm algObject = new Algorithm();
        algObject.setProxyKey(proxyid);
        if(!algRoot.getName().equals(Algorithm.ALGORITHM_ROOT)) {
            return null;
        }
        else {
            algObject.setAlgorithmID(algRoot.getAttributeValue(
                    Algorithm.ALGORITHM_ID));
            List<Element> rootChildren = algRoot.getChildren();
            for (Element child : rootChildren ) {
                if (child.getName().equals(Algorithm.INPAGE)) {
                    algObject.setAlgorithmOccursInPage(
                            Integer.parseInt(child.getValue()));
                } else if (child.getName().equals(Algorithm.CAPTION)) {
                    algObject.setCaption(child.getValue());
                } else if (child.getName().equals(Algorithm.ALGORITHM_SYNOPSIS)) {
                    algObject.setSynopsis(child.getValue());
                } else if (child.getName().equals(Algorithm.REFTEXT)) {
                    algObject.setAlgorithmReference((child.getValue()));
                }
            }
        }
        return algObject;
    }

} //- class AlgorithmSet
