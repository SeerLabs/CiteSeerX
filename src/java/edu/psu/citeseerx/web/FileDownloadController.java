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
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.RepositoryService;
import edu.psu.citeseerx.repository.DocumentUnavailableException;
import edu.psu.citeseerx.webutils.RedirectUtils;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Process a request to download a file, sending the file to the user. If for 
 * some reason the file is not found and Internal error is generated.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class FileDownloadController implements Controller {
    
    private CSXDAO csxdao;

    private RepositoryService repositoryService;

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    // if URI domain name contains any of them, redirect download link to summary page. possible false positives
    public static final Set<String> redirectDomainSet = new HashSet<String>();

    public boolean checkURIReferer(String referer) throws URISyntaxException {
        URI uri = new URI(referer);
        String domain = uri.getHost().toLowerCase();
        // loop over hash set to see if an element is contained in the domain
        for (String rds : redirectDomainSet) {
            if (domain.contains(rds)) {
                return true;
            }
        }
        return false;
    }
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, URISyntaxException {
        
        String doi = request.getParameter("doi");
        String rep = request.getParameter("rep");
        String type = request.getParameter("type");
        String urlIndex = request.getParameter("i");
        String referer = request.getHeader("referer");
       
        // if the referer comes from google/yahoo/bing, redirect to summary page
        if (referer != null) {
            // parse url and get the domain
            boolean urlRefererSearchEngine = checkURIReferer(referer);
            if (urlRefererSearchEngine) {
                RedirectUtils.sendRedirect(request, response, "/viewdoc/summary?doi="+doi);
                return null;
            }
        }


        Map<String, Object> model = new HashMap<String, Object>();
        if (doi == null || type == null) {
            String errorTitle = "Document Not Found";
            model.put("doi", doi);
            model.put("pagetitle", errorTitle);
            return new ModelAndView("baddoi", model);
        }
        
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        
        try {
            Document doc = null;
            try {
                doc = csxdao.getDocumentFromDB(doi);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (doc.isDMCA() == true) {
                String dmcaTitle = "DMCA Notice";
                model.put("doi", doi);
                model.put("pagetitle", dmcaTitle);
                return new ModelAndView("dmcaPage", model);
            }

            if (doc.isRemoved() == true) {
               response.setStatus(404);
               return new ModelAndView("null",model); 
            }

            if (doc == null || doc.isPublic() == false) {
                String errorTitle = "Document Not Found";
                model.put("doi", doi);
                model.put("pagetitle", errorTitle);
                response.setStatus(404);
                return new ModelAndView("baddoi", model);
            }
            
            if (type.equalsIgnoreCase("url")) {

                DocumentFileInfo finfo = doc.getFileInfo();
                int index = 0;
                try {
                    index = Integer.parseInt(urlIndex);
                }
                catch (NumberFormatException e) {
                    index = 0;
                }
                String url;
                if (index >= finfo.getUrls().size() || index < 0) {
                    url = finfo.getUrls().get(0);
                }
                else {
                    url = finfo.getUrls().get(index);
                }
                RedirectUtils.externalRedirect(response, url);

            }else{

                response.reset();
                if (type.equalsIgnoreCase("pdf")) {
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition",
                            "inline; filename=\""+doi+".pdf\"");
                }else if(type.equalsIgnoreCase("ps")) {
                    response.setContentType("application/ps");
                    response.setHeader("Content-Disposition",
                            "attachment; filename=\""+doi+".ps\"");
                }
                else {
                    String errorTitle = "Unknown file type";
                    model.put("doi", doi);
                    model.put("pagetitle", errorTitle);
                    return new ModelAndView("baddoi", model);
                }

                HashMap<String,String> p = new HashMap<String,String>();
                p.put(Document.DOI_KEY, doi);
                p.put(RepositoryService.REPOSITORYID, rep);
                p.put(RepositoryService.FILETYPE, type);
                try {
                    InputStream in = repositoryService.getDocument(p);
                    input = new BufferedInputStream(in);
                    output = new BufferedOutputStream(response.getOutputStream());
                    byte[] buffer = new byte[8192];
                    int got = 0;
                    while((got = input.read(buffer)) != -1) {
                        output.write(buffer, 0, got);
                    }
                    output.flush();
                } catch(DocumentUnavailableException e) {
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            RedirectUtils.sendRedirect(request, response, 
                    "/viewdoc/summary?doi="+doi);
            return null;
        } finally {
            try { input.close(); } catch (Exception exc) {}
            try { output.close(); } catch (Exception exc) {}
        }
        return null;
        
    }  //- handleRequest

}  //- class FileDownloadController
