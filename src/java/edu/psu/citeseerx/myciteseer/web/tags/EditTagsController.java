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
package edu.psu.citeseerx.myciteseer.web.tags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.*;

import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;

import java.sql.SQLException;

/**
 * Process requests to Edit/Delete tags. Renders the success view in case of
 * a valid submission otherwise shows the error view.
 * @author Isaac Council
 * @version $Rev$ $Date$
 */
public class EditTagsController implements Controller {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private FoulWordFilter foulWordFilter;
    
    public void setFoulWordFilter(FoulWordFilter foulWordFilter) {
        this.foulWordFilter = foulWordFilter;
    } //- setFoulWordFilter
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        Account account = MCSUtils.getLoginAccount();
        
        String doi = request.getParameter("doi");
        String type = request.getParameter("type");
        String tag = request.getParameter("tag");
        
        if (type != null && type.equals("del") && tag != null && doi != null) {
            deleteTag(account, doi, tag);
            RedirectUtils.sendRedirect(request, response,
                "/myciteseer/action/viewTags");
            return null;
        }
        
        Document doc = null;
        if (doi != null) {
            doc = csxdao.getDocumentFromDB(doi,
                    false,false,false,false,false,false);
        }
        
        Boolean error = false;
        String errMsg = null;
        
        if (doc == null) {
            error = true;
            errMsg = "Invalid DOI \""+doi+"\"";
        }
        
        if (tag == null) {
            error = true;
            errMsg = "No tag was specified";
        } else {
            String foul = foulWordFilter.findFoulWord(tag);
            if (foul != null) {
                error = true;
                errMsg = "We would appreciate it if you didn't post words " +
                        "like \""+foul+"\" on this site.  " +
                        "Thanks for your cooperation.";
            }
        }
        
        if (error) {
            return MCSUtils.errorPage(errMsg);
        } else {
            addTag(account, doi, tag);
            RedirectUtils.sendRedirect(request, response,
                    "/viewdoc/summary?doi="+doi);
            return null;
        }

    } //- handleRequest
    
    
    private void addTag(Account account, String doi, String tag)
    throws SQLException {
        csxdao.addTag(doi, tag);
        myciteseer.addTag(account, doi, tag);
    } //- addTag
    
    
    private void deleteTag(Account account, String doi, String tag)
    throws SQLException {
        csxdao.deleteTag(doi, tag);
        myciteseer.deleteTag(account, doi, tag);
    } //- deleteTag

} //- class EditTagsController
