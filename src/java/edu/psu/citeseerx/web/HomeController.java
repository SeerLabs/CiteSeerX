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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.domain.HomeStat;

/**
 * Provides model objects to home view.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class HomeController implements Controller {

    private String statDir = "/WEB-INF/stats";
    
    /**
     * @param statDir Path to folder containing statistics
     */
    public void setStatDir(String statDir) {
        this.statDir = statDir;
    } //- setStatDir
    
    private String fileBase = "home";
	
	/**
	 * @param fileBase Base name of the file
	 */
	public void setFileBase(String fileBase) {
		this.fileBase = fileBase;
	} //- setFileBase

	private String fullStatDir;

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		buildStatDir(request);
		String fpath =
            fullStatDir+System.getProperty("file.separator")+fileBase;
		HomeStat stats = getStats(fpath);

		Map<String, Object> model = new HashMap<String, Object>();

		model.put("articles", stats.getDocuments());
		model.put("citations", stats.getCitations());
		model.put("tables", stats.getTables());
		model.put("articlesInc", 
		        stats.getDocuments()-stats.getPreviousDocuments());
		model.put("citationsInc", 
		        stats.getCitations()-stats.getPreviousCitations());
        model.put("tablesInc", 
                stats.getTables()-stats.getPreviousTables());
		return new ModelAndView("index", model);
	} //- handleRequest

	/*
	 * Builds the full path to stats dir
	 */
	private void buildStatDir(HttpServletRequest request) {
        if (fullStatDir == null) {
            fullStatDir =
                request.getSession().getServletContext().getRealPath(statDir);
        }
    } //- buildStatDir
	
	private HomeStat getStats(String fpath) {
		HomeStat stats = new HomeStat();
		try {
			FileReader fr = new FileReader(fpath);
            BufferedReader reader = new BufferedReader(fr);
            String line = null;
            
            // First Line: Actual reading
            line = reader.readLine();
            
            String[] tokens = line.split("\t");
            if (tokens.length>=3) {
                stats.setDocuments(Long.parseLong(tokens[0]));
                stats.setCitations(Long.parseLong(tokens[1]));
                stats.setTables(Long.parseLong(tokens[2]));
            }
            
            // Second Line: Previous Reading
            line = reader.readLine();
            
            tokens = line.split("\t");
            if (tokens.length>=3) {
                stats.setPreviousDocuments(Long.parseLong(tokens[0]));
                stats.setPreviousCitations(Long.parseLong(tokens[1]));
                stats.setPreviousTables(Long.parseLong(tokens[2]));
            }
            reader.close();
		}catch (Exception e) {
			stats.setDocuments(0);
        	stats.setCitations(0);
        	stats.setTables(0);
        	stats.setPreviousDocuments(0);
            stats.setPreviousCitations(0);
            stats.setPreviousTables(0);
		}
		return stats;
	} //- getStats
	
} //- class HomeController
