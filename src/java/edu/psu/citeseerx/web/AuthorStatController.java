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

import edu.psu.citeseerx.domain.AuthorStat;
import edu.psu.citeseerx.webutils.StatisticsTimestampReader;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

/**
 * Provides model objects to author stats view
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class AuthorStatController implements Controller {

    private String statDir = "/WEB-INF/stats";
    
    public void setStatDir(String statDir) {
        this.statDir = statDir;
    } //- setStatDir
    
    
    private int pageSize = 200;
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    } //- setPageSize
    
    
    private int maxResults = 10000;
    
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    } //- setMaxResults
    
    
    private String fileBase = "authors";
    
    public void setFileBase(String fileBase) {
        this.fileBase = fileBase;
    } //- setFileBase
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {
        
        String allStr = request.getParameter("all");
        Integer start = 0;

        if (allStr == null) {
            String startStr = request.getParameter("st");
            try { if (startStr != null) start = Integer.parseInt(startStr); }
            catch (Exception e) {}
        }
        
        buildStatDir(request);
        String fpath =
            fullStatDir+System.getProperty("file.separator")+fileBase;
        
        int nResults = (allStr != null) ? maxResults : pageSize;
        List<AuthorStat> authors =
            buildAuthorList(request, start, fpath, nResults);
        String timestamp =
            StatisticsTimestampReader.getReadableTimestamp(fpath); 
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("authors", authors);
        model.put("gendate", timestamp);
        model.put("pageSize", pageSize);
        model.put("showingAll", new Boolean(allStr != null));
        model.put("start", start);
        if (maxResults > start+pageSize && authors.size()>=pageSize) {
            StringBuilder nextPageParams = new StringBuilder();
            nextPageParams.append("st=");
            nextPageParams.append(start+pageSize);
            model.put("nextPageParams", nextPageParams.toString());
        }
        model.put("pagetitle", "Statistics - Most Cited Authors in " +
        		"Computer Science");
        model.put("pageDescription", "most cited computer science authors");
        model.put("pageKeywords", "most cited authors, statistics");
        return new ModelAndView("authors", model);
        
    }  //- handleRequest
    
    
    private String fullStatDir;
    
    
    private List<AuthorStat> buildAuthorList(HttpServletRequest request,
            Integer start, String fpath, int nResults) {
                
        List<AuthorStat> authors = new ArrayList<AuthorStat>();
        
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
                if (counter>start+nResults || counter>maxResults) {
                    break;
                }
                authors.add(parseLine(line));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return authors;
        
    }  //- buildAuthorList
    
    
    private AuthorStat parseLine(String line) {
        AuthorStat author = new AuthorStat();
        String[] tokens = line.split("\t");
        if (tokens.length>=2) {
            try {
                author.setName(tokens[0]);
                author.setNcites(Integer.parseInt(tokens[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return author;
        
    }  //- parseLine
    
    
    private void buildStatDir(HttpServletRequest request) {
        if (fullStatDir == null) {
            fullStatDir =
                request.getSession().getServletContext().getRealPath(statDir);
        }
    }
    
}  //- class AuthorStatController
