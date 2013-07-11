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
package edu.psu.citeseerx.web;

import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.webutils.StatisticsTimestampReader;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

/**
 * Controller used to send objects to document statistics view
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class DocumentStatController implements Controller {
    
    private String statDir = "/WEB-INF/stats";
    
    public void setStatDir(String statDir) {
        this.statDir = statDir;
    } //- setStatDir
    
    
    private int pageSize = 100;
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    } //- setPageSize
    
    
    private int maxResults = 10000;
    
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    } //- setMaxResults
    
    
    private String defaultType = ARTICLE_TYPE;
    
    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    } //- setDefaultType
    
    
    public static final String ARTICLE_TYPE = "articles";
    public static final String CITATION_TYPE = "citations";
    
    private static final HashSet<String> types = new HashSet<String>();
    static {
        types.add(ARTICLE_TYPE);
        types.add(CITATION_TYPE);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {
        
        String type = request.getParameter("t");
        boolean errorFlag = false;
        String yearStr = request.getParameter("y");
        Integer year = null;
        try { if (yearStr != null) year = Integer.parseInt(yearStr); }
        catch (Exception e) { errorFlag = true; }
        
        String startStr = request.getParameter("st");
        Integer start = 0;
        try { if (startStr != null) start = Integer.parseInt(startStr); }
        catch (Exception e) { if(errorFlag == true) { return null; } } 
        
        if (type == null || !types.contains(type)) type = defaultType;
        
        buildStatDir(request);
        String fpath = (year != null) ?
                fullStatDir+System.getProperty("file.separator")+type+"_"+year :
                    fullStatDir+System.getProperty("file.separator")+type;
                
        List<Integer> years = findYearLinks(request, type);
        List<ThinDoc> docs = buildDocList(request, start, fpath);
        String timestamp =
            StatisticsTimestampReader.getReadableTimestamp(fpath);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("years", years);
        model.put("docs", docs);
        model.put("gendate", timestamp);
        model.put("pageSize", pageSize);
        model.put("start", start);
        if (year != null) {
            model.put("currentlink", year.toString());
        }
        
        if (maxResults > start+pageSize && docs.size()>=pageSize) {
            StringBuilder nextPageParams = new StringBuilder();
            nextPageParams.append("t=");
            nextPageParams.append(type);
            if (year != null) {
                nextPageParams.append("&amp;y=");
                nextPageParams.append(year);
            }
            nextPageParams.append("&amp;st=");
            nextPageParams.append(start+pageSize);
            model.put("nextPageParams", nextPageParams.toString());
        }

        // generate header info.
        String pageTitle, pageDescription, pageKeywords;
        pageTitle = pageDescription = pageKeywords = "";
        if (ARTICLE_TYPE.equalsIgnoreCase(type)) {
        	pageTitle = "Statistics - Most Cited Articles in Computer " + 
        		"Science";
        	pageDescription = "most cited computer science articles"; 
        	pageKeywords = "most cited articles, statistics";
        }else if (CITATION_TYPE.equalsIgnoreCase(type)){
        	pageTitle = "Statistics - Most Cited Citations in Computer " + 
    			"Science";
        	pageDescription = "most cited computer science citations"; 
        	pageKeywords = "most cited citations, statistics";
        }
        model.put("pagetitle", pageTitle);
        model.put("pageDescription", pageDescription);
        model.put("pageKeywords", pageKeywords);
        return new ModelAndView(type, model);
        
    }  //- handleRequest
    
    
    private String fullStatDir;
    
    
    private List<ThinDoc> buildDocList(HttpServletRequest request,
            Integer start, String fpath) {
        
        List<ThinDoc> docs = new ArrayList<ThinDoc>();
        
        try {
            FileReader fr = new FileReader(fpath);
            BufferedReader reader = new BufferedReader(fr);
            int counter = 0;
            String line = null;
            reader.readLine(); // first line should be timestamp
            while((line = reader.readLine()) != null) {
                if (counter++<start) {
                    continue;
                }
                if (counter>start+pageSize || counter>maxResults) {
                    break;
                }
                docs.add(parseLine(line));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return docs;
        
    }  //- buildDocList
    
    
    private ThinDoc parseLine(String line) {
        ThinDoc doc = new ThinDoc();
        String[] tokens = line.split("\t");
        if (tokens.length>=7) {
            try {
                doc.setCluster(Long.parseLong(tokens[0]));
                doc.setNcites(Integer.parseInt(tokens[1]));
                doc.setInCollection(Boolean.parseBoolean(tokens[2]));
                doc.setAuthors(
                        (!tokens[3].equals("NULL")) ? tokens[3] :
                            "unknown authors");
                doc.setAuthors(doc.getAuthors().replace(",", ", "));
                doc.setTitle(
                        (!tokens[4].equals("NULL")) ? tokens[4] :
                            "unknown title");
                doc.setVenue(
                        (!tokens[5].equals("NULL")) ? tokens[5]+", " :
                            "" );
                doc.setYear(
                        (!tokens[6].equals("NULL")) ?
                                Integer.parseInt(tokens[6]) : 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return doc;
    } //- parseLine
    
    
    private List<Integer> findYearLinks(HttpServletRequest request,
            String fileBase) {
        
        File dir = new File(fullStatDir);

        List<Integer> years = new ArrayList<Integer>();
        
        if (dir.isDirectory()) {
            String[] files = dir.list();
            for (String file : files) {
                if (file.startsWith(fileBase)) {
                    String[] parts = file.split("_");
                    try {
                        Integer year = new Integer(
                                Integer.parseInt(parts[parts.length-1]));
                        years.add(year);
                    } catch (Exception e) {}
                }
            }
        }
        Collections.sort(years);
        return years;
        
    }  //- findYearLinks
    
    
    private void buildStatDir(HttpServletRequest request) {
        if (fullStatDir == null) {
            fullStatDir =
                request.getSession().getServletContext().getRealPath(statDir);
        }
    } //- buildStatDir
    
} //- class DocumentStatController
