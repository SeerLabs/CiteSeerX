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

import edu.psu.citeseerx.domain.VenueStat;
import edu.psu.citeseerx.webutils.StatisticsTimestampReader;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;

/**
 * Provides model objects to venue stats view
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class VenueStatController implements Controller {

    private String statDir = "/WEB-INF/stats";
    
    public void setStatDir(String statDir) {
        this.statDir = statDir;
    } //- setStatDir
    
    
    private String fileBase = "venues";
    
    public void setFileBase(String fileBase) {
        this.fileBase = fileBase;
    } //- setFileBase
    
    
    private String dblpUrl = "http://www.informatik.uni-trier.de/~ley/db/";
    
    public void setDblpUrl(String dblpUrl) {
        this.dblpUrl = dblpUrl;
    } //- setDblpUrl
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {
        
        String yearStr = request.getParameter("y");
        Integer year = null;
        try { if (yearStr != null) year = Integer.parseInt(yearStr); }
        catch (Exception e) {}

        buildStatDir(request);
        List<Integer> years = findYearLinks(request, fileBase);
        if (year == null) year = years.get(years.size()-1);
        String fpath =
            fullStatDir+System.getProperty("file.separator")+fileBase+"_"+year;
        List<VenueStat> venues = buildVenueList(request, fpath);
        String timestamp =
            StatisticsTimestampReader.getReadableTimestamp(fpath);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("pagetitle", "Statistics - Venue Impact Factors");
        model.put("pageDescription", "Impact factor calculations based on " +
        	"articles and citations in the collection");
        model.put("pageKeywords", "impact factor, statistics");
        model.put("years", years);
        model.put("gendate", timestamp);
        model.put("venues", venues);
        model.put("currentlink", year);
        return new ModelAndView("venues", model);
        
    }  //- handleRequest
    
    
    private String fullStatDir;
    
    
    private List<VenueStat> buildVenueList(HttpServletRequest request,
            String fpath) {
        
        List<VenueStat> venues = new ArrayList<VenueStat>();
        
        try {
            FileReader fr = new FileReader(fpath);
            BufferedReader reader = new BufferedReader(fr);
            String line = null;
            reader.readLine();  // first line should be timestamp
            while((line = reader.readLine()) != null) {
                venues.add(parseLine(line));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Collections.sort(venues, new DescendingVenueComparator());
        return venues;
        
    }  //- buildVenueList
    
    
    class DescendingVenueComparator implements Comparator<VenueStat> {
        public int compare(VenueStat v1, VenueStat v2) {
            return -1*v1.compareTo(v2);
        } //- compare
    } //- class DescendingVenueComparator
    
    
    private VenueStat parseLine(String line) {
        VenueStat venue = new VenueStat();
        String[] tokens = line.split("\t");
        if (tokens.length>=5) {
            try {
                float factor = (float)Double.parseDouble(tokens[1]);
                factor = (float)(Math.round(factor*100.0) / 100.0);
                venue.setName(tokens[4]);
                venue.setUrl(dblpUrl+tokens[2]);
                venue.setImpact(factor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return venue;
        
    }  //- parseLine
    
    
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
} //- class VenueStatController
