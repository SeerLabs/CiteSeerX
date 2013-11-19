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
import java.io.Serializable;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Light-weight bean container for algorithm metadata. 
 *
 * @author Sumit Bathia
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class Algorithm implements Serializable, XMLSerializable {

    private static final long serialVersionUID = 1L;
    
    private long id;

    /**
     * @return the id
     */
    public long getID() {
        return id;
    } //- getId

    /**
     * @param id the id to set
     */
    public void setID(long id) {
        this.id = id;
    } //- setId
    
    private String caption;

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    } //- getCaption

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    } //- setCaption
    
    private String reference;

    /**
     * @return the reference
     */
    public String getAlgorithmReference() {
        return reference;
    } //- getAlgorithmReference

    /**
     * @param reference the reference to set
     */
    public void setAlgorithmReference(String reference) {
        this.reference = reference;
    } //- setAlgorithmReference
    
    private int inPage;
    
    /**
     * @return the page
     */
    public int getAlgorithmOccursInPage() {
        return inPage;
    } //- getAlgorithmOccursInPage
    
    /**
     * @param page the page to set
     */
    public void setAlgorithmOccursInPage(int page) {
        inPage = page;
    } //- setAlgorithmOccursInPage
    
    private String doi;

    /**
     * @return the doi
     */
    public String getPaperIDForAlgorithm() {
        return doi;
    } //- getPaperIDForAlgorithm

    /**
     * @param doi the doi to set
     */
    public void setPaperIDForAlgorithm(String doi) {
        this.doi = doi;
    } //- setPaperIDForAlgorithm
    
    private String synopsis;

    /**
     * @return the synopsis
     */
    public String getSynopsis() {
        return synopsis;
    } //- getSynopsis

    /**
     * @param synopsis the synopsis to set
     */
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    } //- setSynopsis
    
    private String algorithmID;

    /**
     * @return the algorithmID
     */
    public String getAlgorithmID() {
        return algorithmID;
    } //- getAlgorithmID

    /**
     * @param algorithmID the algorithmID to set
     */
    public void setAlgorithmID(String algorithmID) {
        this.algorithmID = algorithmID;
    } //- setAlgorithmID

    private String proxyID;
    
    /**
     * @return the proxyID
     */
    public String getProxyKey() {
        return proxyID;
    } //- getProxyKey

    /**
     * @param proxyID the proxyID to set
     */
    public void setProxyKey(String proxyID) {
        this.proxyID = proxyID;
    } //- setProxyKey
    
    /* The following are added so that it is easier to index
     * the table.
     */
    private int ncites;
    
    /**
     * @return the ncites
     */
    public int getNcites() {
        return ncites;
    } //- getNcites

    /**
     * @param cites the nCites to set
     */
    public void setNcites(int cites) {
        ncites = cites;
    } //- setNcites
    
    private int paperYear;

    /**
     * @return the paperYear
     */
    public int getPaperYear() {
        return paperYear;
    } //- getPaperYear

    /**
     * @param paperYear the paperYear to set
     */
    public void setPaperYear(int paperYear) {
        this.paperYear = paperYear;
    } //- setPaperYear
    
    private String authors;

    /**
     * @return the authors
     */
    public String getAuthors() {
        return authors;
    } //- getAuthors

    /**
     * @param authors the authors to set
     */
    public void setAuthors(String authors) {
        this.authors = authors;
    } //- setAuthors

    private String title;
    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    } //- getTitle

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    } //- setTitle
    
    private String docURL;
    
    /**
     * @return the docURL
     */
    public String getDocURL() {
        return docURL;
    } //- getDocURL

    /**
     * @param docURL the docURL to set
     */
    public void setDocURL(String docURL) {
        this.docURL = docURL;
    } //- setDocURL
    
    /* Fields for XML access */
    public static final String ALGORITHM_ROOT = "algorithm";
    public static final String ALGORITHM_ID = "id";
    public static final String INPAGE = "pagenum";
    public static final String CAPTION = "caption";
    public static final String ALGORITHM_SYNOPSIS = "synopsis";
    public static final String REFTEXT = "reftext";

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("ID: "+algorithmID+"\n");
        buf.append("DOI: "+doi+"\n");
        buf.append("Page: "+Integer.toString(inPage)+"\n");
        buf.append("Caption: "+caption+"\n");
        buf.append("References: "+reference+"\n");
        buf.append("Synopsis: "+synopsis+"\n");
        return buf.toString();
    } //- toString
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#buildXML(java.lang.StringBuffer, boolean)
     */
    public void buildXML(StringBuffer buffer, boolean sysData) {
        Element algRec = buildXML();
        XMLOutputter bXML = new XMLOutputter();
        String aout = bXML.outputString(algRec);
        buffer.append(aout);

    } //- buildXML
    
    public Element buildXML() {
        Element algRec = new Element(ALGORITHM_ROOT);
        algRec.setAttribute(ALGORITHM_ID,algorithmID);
        Element inpageXML = new Element(INPAGE);
        algRec.addContent(inpageXML);
        inpageXML.addContent(Integer.toString(inPage));
        Element capXML = new Element(CAPTION);
        capXML.addContent(caption);
        algRec.addContent(capXML);
        Element synop = new Element(ALGORITHM_SYNOPSIS);
        synop.addContent(synopsis);
        algRec.addContent(synop);
        Element referXML = new Element(REFTEXT);
        algRec.addContent(referXML);
        return algRec;
    } //- buildXML

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#fromXML(java.io.InputStream)
     */
    public void fromXML(InputStream in) throws IOException {
        SAXBuilder builder = new SAXBuilder();
        try {
            org.jdom.Document doc = builder.build(in);
            Element root = doc.getRootElement();
            fromXML(root);

        } catch (JDOMException e) {
            e.printStackTrace();
        }

    } //- fromXML

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#fromXML(org.jdom.Element)
     */
    public void fromXML(Element root) throws JDOMException {
        
        if(!root.getName().equals(ALGORITHM_ROOT)) {
            throw new JDOMException("Root is "+root.getName()+" expected "+ 
                    ALGORITHM_ROOT);
        }
        else {
            algorithmID = root.getAttributeValue(ALGORITHM_ID);
            List<Element> rootChildren = root.getChildren();
            for (Element child : rootChildren ) {
                if (child.getName().equals(INPAGE)) {
                    inPage = Integer.parseInt(child.getValue());
                } else if (child.getName().equals(CAPTION)) {
                    caption = child.getValue();
                } else if (child.getName().equals(ALGORITHM_SYNOPSIS)) {
                    synopsis = child.getValue();
                }else if (child.getName().equals(REFTEXT)){
                    reference = child.getValue();
                }
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

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.domain.XMLSerializable#toXML(java.io.OutputStream, boolean)
     */
    public void toXML(OutputStream out, boolean sysData) throws IOException {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        out.write(xml.toString().getBytes("utf-8"));

    } //- toXML

} //- Class Algorithm
