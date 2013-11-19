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

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.UniqueAuthor;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle presentation of unique authors
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ViewAuthController implements Controller {

    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private String hpSubmissionLink;
    
    /**
     * @return Link used to redirect user when sending an author's Home page
     * URL.
     */
    public String getHpSubmissionLink() {
        return hpSubmissionLink;
    } //- getHpSubmissionLink

    /**
     * @param hpSubmissionLink Link used to redirect user when sending an
     * author's Home page URL.
     */
    public void setHpSubmissionLink(String hpSubmissionLink) 
    throws MalformedURLException {
        try {
            new URL(hpSubmissionLink);
            this.hpSubmissionLink = hpSubmissionLink;
        } catch (MalformedURLException e) {
            throw(e);
        }
    } //- setHpSubmissionLink
    
    private static final String SORT_CITE = "cite";
    private static final String SORT_DATE = "date";
    
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> model = new HashMap<String, Object>();
        String errorTitle = "Invalid author identifier";
        String aid = request.getParameter("aid");
        if (aid == null) {
            model.put("pagetitle", errorTitle);
            return new ModelAndView("viewDocError", model);
        }
        
        String sort = request.getParameter("sort");
        if (sort == null) {
            sort = "cite";
        }
        
        UniqueAuthor uauth    = csxdao.getAuthor(aid);
        if (uauth == null) {
            errorTitle = "No Author with ID: " + aid + 
                " was found in the repository";
            model.put("pagetitle", errorTitle);
            return new ModelAndView("viewDocError", model);
        }
        uauth.setVarnames(csxdao.getAuthVarnames(aid));
        List<ThinDoc> docs;
    	if (sort.equals("cite")) {
    	    docs = csxdao.getAuthDocsOrdByCites(aid);
    	}
    	else {
    	    docs = csxdao.getAuthDocsOrdByYear(aid);
    	}	
    	
        String homePageURL = null;
        if (uauth.getUrl() == null || (uauth.getUrl().trim().length() == 0)) {
            homePageURL = hpSubmissionLink + "?aid=" + 
                URLEncoder.encode(aid, "UTF-8"); 
        }
        model.put("uauth", uauth);
        model.put("docs", docs);
        model.put("sorttype", sort);
        model.put("hpslink", homePageURL);
	
        String citeQuery, dateQuery;
        
        StringBuffer nextPageParams = new StringBuffer();
        nextPageParams.append("aid=");
        nextPageParams.append(URLEncoder.encode(aid, "UTF-8"));
        citeQuery = dateQuery = nextPageParams.toString();
        citeQuery += "&amp;sort="+SORT_CITE;
        dateQuery += "&amp;sort="+SORT_DATE;
	
        model.put("citeq", citeQuery);
        model.put("dateq", dateQuery);
	
        return new ModelAndView("viewAuth", model);
    } //- handleRequest
    
}  //- class ViewAuthController
