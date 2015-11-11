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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.webutils.RedirectUtils;

/**
 * Controller used to redirect user to desired bookmark place.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class BookmarkController implements Controller {

    private final static String BIBSONOMY = 
        "http://www.bibsonomy.org/BibtexHandler?requTask=upload&url=";
    private final static String REDDIT = "http://www.reddit.com/submit?url=";
    private final static String FACEBOOK = 
        "http://www.facebook.com/sharer.php?u=";
    private final static String MENDELEY = 
        "http://www.mendeley.com/import/?url=";
    private final static String LINKEDIN = 
        "http://www.linkedin.com/shareArticle?mini=true&source=CiteSeerX&url=";
    private final static String TWITTER = 
        "http://twitter.com/home?status=";
    private final static String BUZZ = "http://www.google.com/buzz/post?url=";
        
    private String summaryPage;
    
    /**
     * Sets the URL to the document summary page
     * @param summaryPage
     */
    public void setSummaryPage(String summaryPage) {
        this.summaryPage = summaryPage;
    } //- setSummaryPage

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
       
        String doi = null;
        String bookmarkSite = null;
        String title = null;
    	
        try {
            doi = request.getParameter("doi");
            bookmarkSite = request.getParameter("site");
            title = request.getParameter("title");
        }
        catch (Exception e) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("pagetitle",new String("Unknown Resource"));
                    return new ModelAndView("viewDocError", model);
        }
    
        title = (null!=title) ? title : "";
        
        String url = "";
        if (bookmarkSite == null) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("doi", doi);
            return new ModelAndView(new RedirectView("summary"), "doi", doi);
        }
        
        String csxURL = URLEncoder.encode(summaryPage + "?doi=" + doi, "UTF-8");
        title = URLEncoder.encode(title, "UTF-8");
        if ("bibsonomy".equalsIgnoreCase(bookmarkSite)) {
            url = BIBSONOMY + csxURL + "&description=" + title;
        }else if ("reddit".equalsIgnoreCase(bookmarkSite)) {
            url = REDDIT + csxURL + "&title=" + title;
        }else if ("facebook".equalsIgnoreCase(bookmarkSite)) {
            url = FACEBOOK + csxURL + "&t=" + title;
        }else if ("mendeley".equalsIgnoreCase(bookmarkSite)) {
            url = MENDELEY + csxURL;
        }else if ("linkedin".equalsIgnoreCase(bookmarkSite)) {
            url = LINKEDIN + csxURL + "&title=" + title;
        }else if ("twitter".equalsIgnoreCase(bookmarkSite)) {
            url = TWITTER + title + " " + csxURL + " " + 
                URLEncoder.encode("#CiteSeerX", "UTF-8");
        }else if ("buzz".equalsIgnoreCase(bookmarkSite)) {
            url = BUZZ + csxURL;
        }else{
            return new ModelAndView(new RedirectView("summary"), 
                    "doi", doi);
        }
        RedirectUtils.externalRedirect(response, url);    
                
            
        return null;
    } //- handleRequest

} //- class BoookmarkController
