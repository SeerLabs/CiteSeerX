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
import java.util.*;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Paper;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.*;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.BiblioTransformer;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DomainTransformer;

import java.sql.SQLException;

/**
 * Process requests to list documents with a particular tag. Renders the success 
 * view in case of a valid submission otherwise shows the error view.
 * @author Isaac Council
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ViewTaggedDocsController implements Controller {

private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private int nrows = 10;
    
    public void setNrows(int nrows) {
        this.nrows = nrows;
    } //- setNrows
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        Account account = MCSUtils.getLoginAccount();

        String tag = request.getParameter("tag");
        String startstr = request.getParameter("start");
        Integer start = null;
        if (startstr != null) {
            try { start = new Integer(startstr); } catch (Exception e) { }
        }

        List<Paper> docs = new ArrayList<Paper>();
        
        Integer nresults = 0;
        boolean more = false;
        int stop = nrows;
        
        if (tag != null) {
            List<String> dois = myciteseer.getDoisForTag(account, tag);
            nresults = dois.size();
            
            int index = 0;
            if (start != null) {
                index = start.intValue();
            }
            stop = index+nrows;
            
            while(index < dois.size() && index < stop) {
                Document doc =
                    csxdao.getDocumentFromDB((String)dois.get(index));
                if (doc != null && doc.isPublic()) {
                    Paper paper = new Paper();
                    paper.setDoc(DomainTransformer.toThinDoc(doc));
                    String url = 
			        	request.getRequestURL().toString().replace(
			        			"myciteseer/action/viewTaggedDocs", 
			        			"viewdoc/summary");
			        paper.setCoins(BiblioTransformer.toCOinS(paper.getDoc(),
			        		url));
                    docs.add(paper);
                }
                index++;
            }
            if (dois.size() >= stop) {
                more = true;
            }
        }
        
        String nextPageParams = null;
        if (more) {
            nextPageParams =
                "?tag="+URLEncoder.encode(tag, "UTF-8")+"&start="+stop;
        }
        
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("tag", tag);
        model.put("nresults", nresults);
        model.put("nextpageparams", nextPageParams);
        model.put("papers", docs);
        
        return new ModelAndView("viewTaggedDocs", model);
        
    } //- handleRequest
    
}  //- class ViewTaggedDocsController
