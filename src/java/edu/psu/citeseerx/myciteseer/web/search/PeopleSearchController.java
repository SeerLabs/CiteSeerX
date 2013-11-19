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
package edu.psu.citeseerx.myciteseer.web.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.search.SolrSelectUtils.SolrException;

/**
 * Handles requests which involves searches about people in MyCiteSeerX application
 * @author Juan Pablo Fernandez Ramirez
 * Based on code initially written by ....
 * @version $$Rev$$ $$Date$$
 */
public class PeopleSearchController implements Controller {

	// Url to send search request to the Sorl instance 
	private String solrSelectPeopleUrl;
	
	/**
	 * @param solrSelectPeopleUrl URL to the people Solr instance
	 */
	public void setSolrSelectPeopleUrl(String solrSelectPeopleUrl) 
	throws MalformedURLException {
		new URL(solrSelectPeopleUrl);
		this.solrSelectPeopleUrl = solrSelectPeopleUrl;
	} //- setSolrSelectPeopleUrl

	// Number of rows to be shown
	private int nrows = 10;

	public void setNrows(int nrows) {
        this.nrows = nrows;
    } //- setNrows
	
	// Maximum amount of records to be retrieve and returned.
	private int maxresults = 500;
    
    public void setMaxResults(int maxresults) {
        this.maxresults = maxresults;
    }

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) 
	throws ServletException, IOException, JSONException {
		
		String qi = request.getParameter("query");
		if (qi == null || qi.equals("")) {
            RedirectUtils.sendRedirect(request, response, "/");
            return null;
        }
		
		// Get the starting value
		Integer start = 0;
		try
        {
            start = new Integer(request.getParameter("start"));
        }
        catch(Exception e) {start = 0; }
        
        // Build the complete request to solr.
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(solrSelectPeopleUrl);
        urlBuffer.append("?");
        urlBuffer.append("q=");
        urlBuffer.append(URLEncoder.encode(qi, "UTF-8"));
        // add number of records to be returned (For pagination purposes).
        urlBuffer.append("&");
        urlBuffer.append("rows=");
        urlBuffer.append(nrows);
        // add starting point for returning hits.
        urlBuffer.append("&");
        urlBuffer.append("start=");
        urlBuffer.append(start);
        // add the query handler.
        urlBuffer.append("&");
        urlBuffer.append("qt=");
        if (qi.indexOf(':') != -1) {
            urlBuffer.append("standard");
        } else {
            urlBuffer.append("dismax");
        }
        // We want highlights and the answer in JSON format
        urlBuffer.append("&hl=true");
        urlBuffer.append("&wt=json");
        
        boolean error = false;
        String errMsg = null;
        
        if (start >= maxresults) {
            error = true;
            errMsg = "Only the top " + maxresults + " hits are available.  " 
            + "Please try a more specific query.";
        }
		
        Integer numFound = 0;
        List<Account> hits = new ArrayList<Account>();
        try {
        	if (!error) {
        		// Send the query
        		JSONObject output = 
        			SolrSelectUtils.doJSONQuery(urlBuffer.toString());
        		// Obtain the results.
        		JSONObject responseObj = output.getJSONObject("response");
        		numFound = responseObj.getInt("numFound");
        		// Transform response from JSON to Domain objects.
        		hits = SolrSelectUtils.buildHitAccountListJSON(output);
        	}
        }catch (SolrException e) {
            error = true;
            int code = e.getStatusCode();
            if (code == 400) {
                errMsg = "Invalid query type.  " +
                        "Please check your syntax.";
            } else {
                errMsg = "<p><span class=\"char_emphasized\">" +
                        "Error processing query.</span></p><br>" +
                        "<p>The most likely cause of this condition " +
                        "is a malformed query. Please check your query  " +
                        "syntax and, if the problem persists, " +
                        "contact an admin for assistance.</p>";
            }
        }catch (Exception e) {
        	e.printStackTrace();
        }
        
        // Create page parameters
        StringBuffer nextPageParams = new StringBuffer();
        nextPageParams.append("query=");
        nextPageParams.append(URLEncoder.encode(qi, "UTF-8"));
   		nextPageParams.append("&amp;start=");
        nextPageParams.append(start+nrows);
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("mcsquery", qi);
        model.put("error", new Boolean(error));
        model.put("errorMsg", errMsg);
        model.put("nfound", numFound);
        model.put("start", start);
        model.put("nrows", nrows);
        if (start+nrows < numFound && !error) {
            model.put("nextpageparams", nextPageParams.toString());
        }
        model.put("hits", (!error) ? hits : new ArrayList<Account>());
        
        return new ModelAndView("peopleSearch", model);
	} //- handleRequest

} //- Class PeopleSearchController
