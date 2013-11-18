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

import java.io.Serializable;

/**
 * DBLP data carrier.
 *
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DBLP implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4489895816673333431L;
    
    private long id;
    private String Type;
    private String authors;
    private int numAuthors;
    private String editor;
    private String title;
    private String bookTitle;
    private String pages;
    private int year;
    private String address;
    private String journal;
    private int volume;
    private int number;
    private String month;
    private String url;
    private String ee;
    private String cdrom;
    private String cite;
    private String publisher;
    private String note;
    private String crossref;
    private String isbn;
    private String series;
    private String school;
    private String chapter;
    private String dkey;
    private int numCites;
    
    public long getId() {
        return id;
    } //- getId
    public void setId(long id) {
        this.id = id;
    } //- setId
    public String getType() {
        return Type;
    } //- getType
    public void setType(String type) {
        Type = type;
    } //- setType
    public String getAuthors() {
        return authors;
    } //- getAuthors
    public void setAuthors(String authors) {
        this.authors = authors;
    } //- setAuthors
    public int getNumAuthors() {
        return numAuthors;
    } //- getNumAuthors
    public void setNumAuthors(int numAuthors) {
        this.numAuthors = numAuthors;
    } //- setNumAuthors
    public String getEditor() {
        return editor;
    } //- getEditor
    public void setEditor(String editor) {
        this.editor = editor;
    } //- setEditor
    public String getTitle() {
        return title;
    } //- getTitle
    public void setTitle(String title) {
        this.title = title;
    } //- setTitle
    public String getBookTitle() {
        return bookTitle;
    } //- getBookTitle
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    } //- setBookTitle
    public String getPages() {
        return pages;
    } //- getPages
    public void setPages(String pages) {
        this.pages = pages;
    } //- setPages
    public int getYear() {
        return year;
    } //- getYear
    public void setYear(int year) {
        this.year = year;
    } //- setYear
    public String getAddress() {
        return address;
    } //- getAddress
    public void setAddress(String address) {
        this.address = address;
    } //- setAddress
    public String getJournal() {
        return journal;
    } //- getJournal
    public void setJournal(String journal) {
        this.journal = journal;
    } //- setJournal
    public int getVolume() {
        return volume;
    } //- getVolume
    public void setVolume(int volume) {
        this.volume = volume;
    } //- setVolume
    public int getNumber() {
        return number;
    } //- getNumber
    public void setNumber(int number) {
        this.number = number;
    } //- setNumber
    public String getMonth() {
        return month;
    } //- getMonth
    public void setMonth(String month) {
        this.month = month;
    } //- setMonth
    public String getUrl() {
        return url;
    } //- getUrl
    public void setUrl(String url) {
        this.url = url;
    } //- setUrl
    public String getEe() {
        return ee;
    } //- getEe
    public void setEe(String ee) {
        this.ee = ee;
    } //- setEe
    public String getCdrom() {
        return cdrom;
    } //- getCdrom
    public void setCdrom(String cdrom) {
        this.cdrom = cdrom;
    } //- setCdrom
    public String getCite() {
        return cite;
    } //- getCite
    public void setCite(String cite) {
        this.cite = cite;
    } //- setCite
    public String getPublisher() {
        return publisher;
    } //- getPublisher
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    } //- setPublisher
    public String getNote() {
        return note;
    } //- getNote
    public void setNote(String note) {
        this.note = note;
    } //- setNote
    public String getCrossref() {
        return crossref;
    } //- getCrossref
    public void setCrossref(String crossref) {
        this.crossref = crossref;
    } //- setCrossref
    public String getIsbn() {
        return isbn;
    } //- getIsbn
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    } //- setIsbn
    public String getSeries() {
        return series;
    } //- getSeries
    public void setSeries(String series) {
        this.series = series;
    } //- setSeries
    public String getSchool() {
        return school;
    } //- getSchool
    public void setSchool(String school) {
        this.school = school;
    } //- setSchool
    public String getChapter() {
        return chapter;
    } //- getChapter
    public void setChapter(String chapter) {
        this.chapter = chapter;
    } //- setChapter
    public String getDkey() {
        return dkey;
    } //- getDkey
    public void setDkey(String dkey) {
        this.dkey = dkey;
    } //- setDkey
    public int getNumCites() {
        return numCites;
    } //- getNumCites
    public void setNumCites(int numCites) {
        this.numCites = numCites;
    } //- setNumCites
    
} //- class DBLP
