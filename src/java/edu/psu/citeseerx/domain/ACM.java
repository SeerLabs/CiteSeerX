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
 * ACM data carrier.
 *
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ACM implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -4579126666241545546L;
    
    private long id;
    private String authors;
    private String title;
    private int year;
    private String venue;
    private String url;
    private String pages;
    private String publication;
    
    public long getId() {
        return id;
    } //- getId
    
    public void setId(long id) {
        this.id = id;
    } //- setId
    
    public String getAuthors() {
        return authors;
    } //- getAuthors
    
    public void setAuthors(String authors) {
        this.authors = authors;
    } //- setAuthors
    
    public String getTitle() {
        return title;
    } //- getTitle
    
    public void setTitle(String title) {
        this.title = title;
    } //- setTitle
    
    public int getYear() {
        return year;
    } //- getYear
    
    public void setYear(int year) {
        this.year = year;
    } //- setYear
    
    public String getVenue() {
        return venue;
    } //-
    
    public void setVenue(String venue) {
        this.venue = venue;
    } //- setVenue
    
    public String getUrl() {
        return url;
    } //- getUrl
    
    public void setUrl(String url) {
        this.url = url;
    } //- setUrl
    
    public String getPages() {
        return pages;
    } //- getPages
    
    public void setPages(String pages) {
        this.pages = pages;
    } //- setPages
    
    public String getPublication() {
        return publication;
    } //- getPublication
    
    public void setPublication(String publication) {
        this.publication = publication;
    } //- setPublication
    
} //- class ACM
