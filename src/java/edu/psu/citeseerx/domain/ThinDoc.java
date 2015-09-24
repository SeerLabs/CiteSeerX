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
import java.util.Date;
import edu.psu.citeseerx.utility.DateUtils;
import org.apache.solr.client.solrj.beans.Field;
/**
 * Light-weight bean container for document, citation, or cluster metadata.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class ThinDoc implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8919839426733776073L;

    @Field
    private String doi;
    private Long cluster;
    private String authors;
    @Field
    private String title;
    @Field("abstract")
    private String abs;
    @Field
    private String venue;
    @Field("ventype")
    private String ventype;
    private int year = -1;
    @Field
    private String pages;
    private int vol = -1;
    private int num = -1;
    @Field
    private String publisher;
    @Field
    private String tech;
    private int ncites = 0;
    private int selfCites = 0;
    private String snippet;
    @Field
    private String url;
    @Field("incol")
    private boolean inCollection; 
    private String observations;
    private Date updateTime;

    private String id;
    
    public String getAuthors() {
        return authors;
    } //- getAuthors
    public void setAuthors(String authors) {
        this.authors = authors;
    } //- setAuthors
    public Long getCluster() {
        return cluster;
    } //- getCluster
    public void setCluster(Long cluster) {
        this.cluster = cluster;
    } //- setCluster
    public String getDoi() {
        return doi;
    } //- getDoi
    public void setDoi(String doi) {
        this.doi = doi;
    } //- setDoi
    public int getNcites() {
        return ncites;
    } //- getNcites
    public void setNcites(int ncites) {
        this.ncites = ncites;
    } //- setNcites
    public int getSelfCites() {
        return selfCites;
    } //- getSelfCites
    public void setSelfCites(int selfCites) {
        this.selfCites = selfCites;
    } //- setSelfCites
    public String getTitle() {
        return title;
    } //- getTitle
    public void setTitle(String title) {
        this.title = title;
    } //- setTitle
    public String getVentype() {
        return ventype;
    } //- getVentype
    public void setVentype(String ventype) {
        this.ventype = ventype;
    } //- setVentype
    public String getVenue() {
        return venue;
    } //- getVenue
    public void setVenue(String venue) {
        this.venue = venue;
    } //- setVenue
    public int getYear() {
        return year;
    } //- getYear
    public void setYear(int year) {
        this.year = year;
    } //- setYear
    public String getAbstract() {
        return abs;
    } //- getAbstract
    public void setAbstract(String abs) {
        this.abs = abs;
    } //- setAbstract
    public String getSnippet() {
        return snippet;
    } //- getSnippet
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    } //- setSnippet
    public String getUrl() {
        return url;
    } //- getUrl
    public void setUrl(String url) {
        this.url = url;
    } //- setUrl
    public boolean getInCollection() {
        return inCollection;
    } //- getInCollection
    public void setInCollection(boolean inCollection) {
        this.inCollection = inCollection;
    } //- setInCollection
    public String getAbs() {
        return abs;
    } //- getAbs
    public void setAbs(String abs) {
        this.abs = abs;
    } //- setAbs
    public int getNum() {
        return num;
    } //- getNum
    public void setNum(int num) {
        this.num = num;
    } //- setNum
    public String getPages() {
        return pages;
    } //- getPages
    public void setPages(String pages) {
        this.pages = pages;
    } //- setPages
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
    public int getVol() {
        return vol;
    } //- getVol
    public void setVol(int vol) {
        this.vol = vol;
    } //- setVol
    public String getObservations() {
        return observations;
    } //- getObservations
    public void setObservations(String observations) {
        this.observations = observations;
    } //- setObservations
    public Date getUpdateTime() {
        return updateTime;
    } //- getUpdateTime
    public void setId(String id) {
        this.id = id;
    } //- setId
    public String getId() {
        return id;
    } //- getId

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    } //- setUpdateTime
    public String getRfc822Time() {
        return DateUtils.formatRFC822(updateTime);
    } //- getRfc822Time

    public String getRfc3339Time() {
        return DateUtils.formatRFC3339(updateTime);
    } //- getRfc3339Time

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        
        buf.append("THINDOC\n");
        buf.append("DOI: "+doi+"\n");
        buf.append("CLUST: "+cluster+"\n");
        buf.append("AUTH: "+authors+"\n");
        buf.append("TITL: "+title+"\n");
        buf.append("ABS: "+abs+"\n");
        buf.append("VEN: "+venue+"\n");
        buf.append("VT: "+ventype+"\n");
        buf.append("YEAR: "+year+"\n");
        buf.append("PAGE: "+pages+"\n");
        buf.append("VOL: "+vol+"\n");
        buf.append("NUM: "+num+"\n");
        buf.append("PUBL: "+publisher+"\n");
        buf.append("TECH: "+tech+"\n");
        buf.append("NCITE: "+ncites+"\n");
        buf.append("SELFCITE: "+selfCites+"\n");
        buf.append("SNIP: "+snippet+"\n");
        buf.append("URL: "+url+"\n");
        buf.append("INCOL: "+inCollection+"\n"); 

        return buf.toString();
    } //- toString
    
    
}  //- class ThinDoc
