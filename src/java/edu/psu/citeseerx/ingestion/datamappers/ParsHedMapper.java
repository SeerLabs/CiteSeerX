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
package edu.psu.citeseerx.ingestion.datamappers;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.Keyword;
import edu.psu.citeseerx.utility.SafeText;
import edu.psu.citeseerx.utility.CSXConstants;

/**
 * Maps parsed header data into Document objects.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class ParsHedMapper {

    public static final String ALG_NAME = "SVM HeaderParse";
    
    /**
     * Creates a Document based on XML header data.
     * @param xml
     * @return a Document based on XML header data.
     * @throws MappingException
     */
    public static Document map(String xml)
    throws MappingException {

        SAXBuilder builder = new SAXBuilder();

        try {
            org.jdom.Document xmldoc = builder.build(new StringReader(xml));
            Element root = xmldoc.getRootElement();
            edu.psu.citeseerx.domain.Document doc =
                new edu.psu.citeseerx.domain.Document();
            map(doc, root);
            return doc;
            
        } catch (IOException e) {
            MappingException exc = new MappingException(e.getMessage());
            exc.setStackTrace(e.getStackTrace());
            throw exc;
        } catch (JDOMException e) {
            MappingException exc = new MappingException(e.getMessage());
            exc.setStackTrace(e.getStackTrace());
            throw exc;
        }
        
    }  //- map
    
    
    /**
     * Maps header data into a Document based on the root element of a 
     * preparsed DOM tree.
     * @param doc
     * @param root
     * @throws MappingException
     */
    public static void map(Document doc, Element root)
    throws MappingException {
        
        if (!root.getAttributeValue("name").equals(ALG_NAME) || !root.getAttributeValue("name").contains("GROBID")) {
            throw new MappingException("Root name attribute is not what " +
                    "was expected: found "+root.getAttributeValue("name")+
                    ", expected "+ALG_NAME + "or GROBID");
        }
        buildDoc(doc, root);

    }  //- map
    
    
    protected static void buildDoc(Document doc,
            Element root) {
        
        String algName = root.getAttributeValue("name");
        String algVers = root.getAttributeValue("version");
        String src = algName + " " + algVers;
        
        List<Element> rootChildren = root.getChildren();
        for (Iterator<Element> it = rootChildren.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equalsIgnoreCase("authors")) {
                int ord = 1;
                List<Element> authorElts = child.getChildren();
                for (Iterator<Element> authiter = authorElts.iterator();
                authiter.hasNext(); ) {
                    Element authElt = (Element)authiter.next();
                    Author author = mapAuthor(authElt, src);
                    author.setDatum(Author.ORD_KEY, Integer.toString(ord));
                    doc.addAuthor(author);
                    ord++;
                }
                continue;
            }
            if (child.getName().equalsIgnoreCase("keywords")) {
                List<Element> keywordElts = child.getChildren();
                for (Iterator<Element> keyiter = keywordElts.iterator();
                keyiter.hasNext(); ) {
                    Element keyElt = (Element)keyiter.next();
                    Keyword keyword = mapKeyword(keyElt, src);
                    doc.addKeyword(keyword);
                }
                continue;
            }
            if (child.getName().equalsIgnoreCase("title")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_TITLE) {
                    val = val.substring(0, CSXConstants.MAX_DOC_TITLE);
                }
                doc.setDatum(Document.TITLE_KEY, val);
                doc.setSource(Document.TITLE_KEY, src);
            }
            if (child.getName().equalsIgnoreCase("abstract")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                doc.setDatum(Document.ABSTRACT_KEY, val);
                doc.setSource(Document.ABSTRACT_KEY, src);
            }
            if (child.getName().equalsIgnoreCase("date")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                try {
                    Integer.parseInt(val);
                    doc.setDatum(Document.YEAR_KEY, val);
                    doc.setSource(Document.YEAR_KEY, src);
                } catch (NumberFormatException e) {}
            }
            if (child.getName().equalsIgnoreCase("tech")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_DOC_TECH) {
                    val = val.substring(0, CSXConstants.MAX_DOC_TECH);
                }
                doc.setDatum(Document.TECH_KEY, val);
                doc.setSource(Document.TECH_KEY, src);
            }   
        }
        
    }  //- buildDoc
            
    
    
    protected static Author mapAuthor(Element authElt, String src) {
        
        Author auth = new Author();
        List<Element> children = authElt.getChildren();

        for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();

            if (child.getName().equalsIgnoreCase("name")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_AUTH_NAME) {
                    val = val.substring(0, CSXConstants.MAX_AUTH_NAME);
                }
                auth.setDatum(Author.NAME_KEY, val);
                auth.setSource(Author.NAME_KEY, src);
            }
            
            if (child.getName().equalsIgnoreCase("affiliation")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_AUTH_AFFIL) {
                    val = val.substring(0, CSXConstants.MAX_AUTH_AFFIL);
                }
                auth.setDatum(Author.AFFIL_KEY, val);
                auth.setSource(Author.AFFIL_KEY, src);
            }
            
            if (child.getName().equalsIgnoreCase("address")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_AUTH_ADDR) {
                    val = val.substring(0, CSXConstants.MAX_AUTH_ADDR);
                }
                auth.setDatum(Author.ADDR_KEY, val);
                auth.setSource(Author.ADDR_KEY, src);
            }
            
            if (child.getName().equalsIgnoreCase("email")) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                if (val.length() > CSXConstants.MAX_AUTH_EMAIL) {
                    val = val.substring(0, CSXConstants.MAX_AUTH_EMAIL);
                }
                auth.setDatum(Author.EMAIL_KEY, val);
                auth.setSource(Author.EMAIL_KEY, src);
            }
        }

        return auth;
        
    }  //- mapAuthor
    
    
    protected static Keyword mapKeyword(Element keyElt, String src) {
        
        Keyword keyword = new Keyword();
        String val = SafeText.decodeHTMLSpecialChars(keyElt.getValue());
        if (val.length() > CSXConstants.MAX_KEYWORD) {
            val = val.substring(0, CSXConstants.MAX_KEYWORD);
        }
        keyword.setDatum(Keyword.KEYWORD_KEY, val);
        keyword.setSource(Keyword.KEYWORD_KEY, src);
        return keyword;
        
    }  //- mapKeyword
    
    
}  //- class ParsHedMapper
