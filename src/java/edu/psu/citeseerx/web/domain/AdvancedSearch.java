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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.net.URLEncoder;

/**
 * Advance search data carrier.
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class AdvancedSearch implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 697197717360379474L;
    
    private String textQuery;
    private String titleQuery;
    private String authorQuery;
    private String affilQuery;
    private String venueQuery;
    private String keywordQuery;
    private String abstractQuery;

    private String year;
    private String fromYear;
    private String toYear;
    private String minCitations;
    private String includeCites;
    
    private String sortCriteria;
    
    private int highCites = 15000;
    
    public void setHighCites(int highCites) {
        this.highCites = highCites;
    } //- setHighCites
    
    
    public String getQuery() {
        
        StringBuilder builder = new StringBuilder();
        builder.append("?q=");
        
        boolean inQuery = false;
        try {
            inQuery = buildFieldQuery("text", textQuery, builder, inQuery);
            inQuery = buildFieldQuery("title", titleQuery, builder, inQuery);
            inQuery = buildFieldQuery("abstract", abstractQuery,
                    builder, inQuery);
            inQuery = buildFieldQuery("author", authorQuery, builder, inQuery);
            inQuery = buildFieldQuery("affil", affilQuery, builder, inQuery);
            inQuery = buildFieldQuery("venue", venueQuery, builder, inQuery);
            inQuery = buildFieldQuery("keyword", keywordQuery,
                    builder, inQuery);

            if (minCitations != null && minCitations.length()>0) {
                inQuery = buildFieldQuery("ncites",
                        "["+minCitations+" TO "+highCites+"]",builder, inQuery);
            }
            
            if (year != null && year.length()>0) {
                buildFieldQuery("year", year, builder, inQuery);
            } else if ((fromYear != null && fromYear.length()>0)
                    || (toYear != null && toYear.length()>0)) {
                String start = (fromYear != null && fromYear.length()>0) ?
                        fromYear : "1900";
                
                String end = (toYear != null && toYear.length()>0) ?
                        toYear : Integer.toString(Calendar.getInstance()
                                .get(Calendar.YEAR)+1);
                
                buildFieldQuery("year", "["+start+" TO "+end+"]",
                        builder, inQuery);
            }
            if (sortCriteria != null && inQuery) {
                builder.append("&sort=");
                builder.append(URLEncoder.encode(sortCriteria, "UTF-8"));
            }
            if (includeCites != null && includeCites.equals("1")) {
                builder.append("&ic=1");
            }
            // Till now advance search only works on the general search
            builder.append("&t=doc");
        } catch (UnsupportedEncodingException e) { }
        return builder.toString();
        
    }  //- getQuery
    
    
    private boolean buildFieldQuery(String fieldName, String query,
            StringBuilder builder, boolean inQuery)
    throws UnsupportedEncodingException {
        
        if (query != null && query.length() > 0) {
            if (inQuery) builder.append("+AND+");
            builder.append(fieldName);
            builder.append("%3A");
            if (query.indexOf(' ') != -1) {
                query = groupQuery(fieldName, query);
            }
            builder.append(URLEncoder.encode(query, "UTF-8"));
            return true;
        }
        return inQuery;
        
    }  //- buildFieldQuery
    
    
    private static final Set<String> nogrouping = new HashSet<String>();
    static {
        nogrouping.add("year");
        nogrouping.add("ncites");
    }
    
    private String groupQuery(String fieldName, String query) {
        if (nogrouping.contains(fieldName)) return query;
        return "("+query+")";
    }  //- groupQuery
    
    
    public String getAbstractQuery() {
        return abstractQuery;
    } //- getAbstractQuery
    public void setAbstractQuery(String abstractQuery) {
        this.abstractQuery = abstractQuery;
    } //- setAbstractQuery
    public String getAffilQuery() {
        return affilQuery;
    } //- getAffilQuery
    public void setAffilQuery(String affilQuery) {
        this.affilQuery = affilQuery;
    } //- setAffilQuery
    public String getAuthorQuery() {
        return authorQuery;
    } //- getAuthorQuery
    public void setAuthorQuery(String authorQuery) {
        this.authorQuery = authorQuery;
    } //- setAuthorQuery
    public String getIncludeCites() {
        return includeCites;
    } //- getIncludeCites
    public void setIncludeCites(String includeCites) {
        this.includeCites = includeCites;
    } //- setIncludeCites
    public String getKeywordQuery() {
        return keywordQuery;
    } //- getKeywordQuery
    public void setKeywordQuery(String keywordQuery) {
        this.keywordQuery = keywordQuery;
    } //- setKeywordQuery
    public String getSortCriteria() {
        return sortCriteria;
    } //- getSortCriteria
    public void setSortCriteria(String sortCriteria) {
        this.sortCriteria = sortCriteria;
    } //- setSortCriteria
    public String getTextQuery() {
        return textQuery;
    } //- getTextQuery
    public void setTextQuery(String textQuery) {
        this.textQuery = textQuery;
    } //- setTextQuery
    public String getTitleQuery() {
        return titleQuery;
    } //- getTitleQuery
    public void setTitleQuery(String titleQuery) {
        this.titleQuery = titleQuery;
    } //- setTitleQuery
    public String getVenueQuery() {
        return venueQuery;
    } //- getVenueQuery
    public void setVenueQuery(String venueQuery) {
        this.venueQuery = venueQuery;
    } //- setVenueQuery

    public String getYearFrom() {
        return fromYear;
    } //- getYearFrom

    public void setYearFrom(String fromYear) {
        this.fromYear = fromYear;
    } //- setYearFrom

    public String getMinCitations() {
        return minCitations;
    } //- setYearFrom

    public void setMinCitations(String minCitations) {
        this.minCitations = minCitations;
    } //- setMinCitations

    public String getYearTo() {
        return toYear;
    } //- getYearTo

    public void setYearTo(String toYear) {
        this.toYear = toYear;
    } //- setYearTo

    public String getYear() {
        return year;
    } //- getYear

    public void setYear(String year) {
        this.year = year;
    } //- setYear
    
} //- class AdvancedSearch
