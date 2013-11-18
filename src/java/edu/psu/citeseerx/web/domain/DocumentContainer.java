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
package edu.psu.citeseerx.web.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.list.LazyList;
import org.apache.commons.collections.FactoryUtils;

import java.io.Serializable;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.Author;

/**
 * Document data carrier. Used in corrections
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DocumentContainer implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1102595253475103412L;

    public static DocumentContainer fromDocument(Document doc) {
        DocumentContainer dc = new DocumentContainer();

        dc.setPaperID(doc.getDatum(Document.DOI_KEY));
        dc.setTitle(doc.getDatum(Document.TITLE_KEY));
        dc.setVenue(doc.getDatum(Document.VENUE_KEY));
        dc.setVenType(doc.getDatum(Document.VEN_TYPE_KEY));
        dc.setAbs(doc.getDatum(Document.ABSTRACT_KEY));
        dc.setPublisher(doc.getDatum(Document.PUBLISHER_KEY));
        dc.setPubAddr(doc.getDatum(Document.PUBADDR_KEY));
        dc.setTech(doc.getDatum(Document.TECH_KEY));
        dc.setPages(doc.getDatum(Document.PAGES_KEY));

        try {
            dc.setYear(doc.getDatum(Document.YEAR_KEY));
        } catch (Exception e) {}
        try {
            dc.setNum(doc.getDatum(Document.NUM_KEY));
        } catch (Exception e) {}
        try {
            dc.setVol(doc.getDatum(Document.VOL_KEY));
        } catch (Exception e) {}
        
        List<Author> auths = doc.getAuthors();
        List<AuthorContainer> authContainers = dc.getAuthors();
        for (Author author : auths) {
            AuthorContainer ac = new AuthorContainer();
            ac.setName(author.getDatum(Author.NAME_KEY));
            ac.setAffil(author.getDatum(Author.AFFIL_KEY));
            ac.setAddress(author.getDatum(Author.ADDR_KEY));
            ac.setEmail(author.getDatum(Author.EMAIL_KEY));
            ac.setOrder(author.getDatum(Author.ORD_KEY));
            authContainers.add(ac);
        }
        return dc;
        
    }  //- fromDocument
    
    
    public void toDocument(Document doc, String src) {

        updateDocField(doc, Document.TITLE_KEY, getTitle(), src);
        updateDocField(doc, Document.VENUE_KEY, getVenue(), src);
        updateDocField(doc, Document.VEN_TYPE_KEY, getVenType(), src);
        updateDocField(doc, Document.ABSTRACT_KEY, getAbs(), src);
        updateDocField(doc, Document.PUBLISHER_KEY, getPublisher(), src);
        updateDocField(doc, Document.PUBADDR_KEY, getPubAddr(), src);
        updateDocField(doc, Document.TECH_KEY, getTech(), src);
        updateDocField(doc, Document.PAGES_KEY, getPages(), src);

        updateDocField(doc, Document.YEAR_KEY, getYear(), src);
        updateDocField(doc, Document.NUM_KEY, getNum(), src);
        updateDocField(doc, Document.VOL_KEY, getVol(), src);
        
        ArrayList<Author> auths = new ArrayList<Author>();
        for (Object o : getCorrectedAuthors()) {
            AuthorContainer ac = (AuthorContainer)o;
            Author author = new Author();
            author.setDatum(Author.NAME_KEY, ac.getName());
            author.setSource(Author.NAME_KEY, src);
            author.setDatum(Author.AFFIL_KEY, ac.getAffil());
            author.setSource(Author.AFFIL_KEY, src);
            author.setDatum(Author.ADDR_KEY, ac.getAddress());
            author.setSource(Author.ADDR_KEY, src);
            author.setDatum(Author.EMAIL_KEY, ac.getEmail());
            author.setSource(Author.EMAIL_KEY, src);
            author.setDatum(Author.ORD_KEY, ac.getOrder());
            author.setSource(Author.ORD_KEY, src);
            auths.add(author);
        }
        doc.setAuthors(auths);
        
    }  //- toDocument
    
    
    private void updateDocField(Document doc, String key,
            String newVal, String src) {

        if (doc.getDatum(key) == null && newVal == null) {
            return;
        }
        
        boolean update = false;
        if (doc.getDatum(key) == null && newVal != null) {
            update = true;
        } else if (doc.getDatum(key) != null && newVal == null) {
            update = true;
        } else if (!doc.getDatum(key).equals(newVal)) {
            update = true;
        }
        if (update) {
            doc.setDatum(key, newVal);
            doc.setSource(key, src);
        }
        
    }  //- updateDocField
    
    
    private List<AuthorContainer> authors =
        LazyList.decorate(
                new ArrayList(),
                FactoryUtils.instantiateFactory(AuthorContainer.class));
    
    public List<AuthorContainer> getAuthors() {
        return authors;
    }
    
    public List<AuthorContainer> getCorrectedAuthors() {
        List<AuthorContainer> cAuthors = new ArrayList<AuthorContainer>();
        for (AuthorContainer o : authors) {
            if (!(o.getDeleted())) {
                cAuthors.add(o);
            }
        }         
        return cAuthors;
        
    }  //- getCorrectedAuthors
        

    private Integer pagesFrom;
    private Integer pagesTo;

    public String getPages() {
        if (pagesFrom != null && pagesTo != null) {
            return pagesFrom+"--"+pagesTo;
        }
        if (pagesFrom != null) {
            return pagesFrom.toString();
        }
        return null;
        
    }  //- getPages
    
    Pattern p = Pattern.compile("(\\d+)");
    
    public void setPages(String pages) {
        if (pages == null) {
            return;
        }
        Matcher m = p.matcher(pages);
        if (m.find()) {
            pagesFrom = Integer.parseInt(m.group());
        }
        if (m.find()) {
            pagesTo = Integer.parseInt(m.group());
        }
        
    }  //- setPages
    
    private String doi;
    private String title;
    private String abs;
    private String venue;
    private String venType;
    private String year;
    private String vol;
    private String num;
    private String publisher;
    private String pubAddr;
    private String tech;

    public String getPaperID() {
        return doi;
    } //- getPaperID
    
    public void setPaperID(String doi) {
        this.doi = doi;
    } //- setPaperID
    
    public String getAbs() {
        return abs;
    } //- getAbs

    public void setAbs(String abs) {
        this.abs = abs;
    } //- setAbs

    public String getNum() {
        return num;
    } //- getNum

    public void setNum(String num) {
        this.num = num;
    } //- setNum

    public String getPubAddr() {
        return pubAddr;
    } //- getPubAddr

    public void setPubAddr(String pubAddr) {
        this.pubAddr = pubAddr;
    } //- setPubAddr

    public String getPublisher() {
        return publisher;
    } //- getPublisher

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    } //- setPublisher

    public String getTech() {
        return tech;
    } //- getTech

    public void setTech(String tech) {
        this.tech = tech;
    } //- setTech

    public String getTitle() {
        return title;
    } //- getTitle

    public void setTitle(String title) {
        this.title = title;
    } //- setTitle

    public String getVenType() {
        return venType;
    } //- getVenType

    public void setVenType(String venType) {
        this.venType = venType;
    } //- setVenType

    public String getVenue() {
        return venue;
    } //- getVenue

    public void setVenue(String venue) {
        venue = (null != venue) ? venue.toUpperCase() : venue; 
        this.venue = venue;
    } //- setVenue

    public String getVol() {
        return vol;
    } //- getVol

    public void setVol(String vol) {
        this.vol = vol;
    } //- setVol

    public String getYear() {
        return year;
    } //- getYear

    public void setYear(String year) {
        this.year = year;
    } //- setYear
    
    public int getNumberOfAuthors() {
        return authors.size();
    } //- getNumberOfAuthors
    
    public boolean hasAuthors() {
        for (Object o : authors) {
            if (!((AuthorContainer)o).getDeleted()) return true;
        }
        return false;
    } //- hasAuthors
    
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("DOI: ");
        builder.append(getPaperID());
        builder.append("\n");
        
        builder.append("TITLE: ");
        builder.append(getTitle());
        builder.append("\n");

        for (Object o : getAuthors()) {
            AuthorContainer ac = (AuthorContainer)o;
            
            builder.append("AUTHOR:\n");
            builder.append("\tNAME: ");
            builder.append(ac.getName());
            builder.append("\n");

            builder.append("\tAFFIL: ");
            builder.append(ac.getAffil());
            builder.append("\n");
            
            builder.append("\tADDR: ");
            builder.append(ac.getAddress());
            builder.append("\n");

            builder.append("\tEMAIL: ");
            builder.append(ac.getEmail());
            builder.append("\n");

            builder.append("\tORDER: ");
            builder.append(ac.getOrder());
            builder.append("\n");
            
            builder.append("\tDELETED: ");
            builder.append(ac.getDeleted());
            builder.append("\n");

        }
        
        builder.append("ABSTRACT: ");
        builder.append(getAbs());
        builder.append("\n");

        builder.append("VENUE: ");
        builder.append(getVenue());
        builder.append("\n");

        builder.append("VENTYPE: ");
        builder.append(getVenType());
        builder.append("\n");

        builder.append("YEAR: ");
        builder.append(getYear());
        builder.append("\n");

        builder.append("VOL: ");
        builder.append(getVol());
        builder.append("\n");

        builder.append("NUM: ");
        builder.append(getNum());
        builder.append("\n");

        builder.append("PAGES: ");
        builder.append(getPages());
        builder.append("\n");

        builder.append("PUBLISHER: ");
        builder.append(getPublisher());
        builder.append("\n");

        builder.append("PUBADDR: ");
        builder.append(getPubAddr());
        builder.append("\n");

        builder.append("TECH: ");
        builder.append(getTech());
        builder.append("\n");

        return builder.toString();
        
    }  //- toString
    
}  //- class DocumentContainer
