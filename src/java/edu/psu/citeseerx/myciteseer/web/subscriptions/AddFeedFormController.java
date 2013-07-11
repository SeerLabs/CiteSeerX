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
package edu.psu.citeseerx.myciteseer.web.subscriptions;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Feed;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;


public class AddFeedFormController extends SimpleFormController {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }
    
    
    public AddFeedFormController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("feed");
        setFormView("addFeed");   
    }


    protected Object formBackingObject(HttpServletRequest request) {

        String params = "";

        String type = request.getParameter("type");
        if (type.equals(Feed.DOC_TYPE)) {
            params = "q="+request.getParameter("q");
        }
        if (type.equals(Feed.CIT_TYPE)) {
            params = "cid="+request.getParameter("cid");
        }
        String sort = request.getParameter("sort");
        if (sort != null && sort.length() > 0) {
            if (params.length() > 0) { params = params + "&"; }
            params = params + "sort=" + sort;
        }
        
        Feed feed = new Feed();
        feed.setParams(params);
        feed.setType(request.getParameter("type"));
        return feed;
        
    } //- formBackingObject
    
    
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {

        Feed feed = (Feed)command;
        if (!Feed.isValidType(feed.getType())) {
            errors.reject("type", "Invalid feed type");
        }
        if (feed.getParams() == null || feed.getParams().length() <= 0) {
            errors.reject("params", "Invalid feed parameters");
        }
        
    } //- onBindAndValidate
    
    
    protected Map<String, Object> referenceData(HttpServletRequest request)
    throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        return model;
        
    }  //- referenceData
    
    
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
    
        onBindAndValidate(request, command, errors);
        if (errors.hasErrors()) {
            return showForm(request, response, errors);
        }
        
        Feed feed = (Feed)command;
        feed.setUserid(MCSUtils.getLoginAccount().getUsername());
        myciteseer.addFeed(feed);
        
        return new ModelAndView(new RedirectView(getSuccessView()));
        
    }  //- onSubmit
    
}  //- class AddFeedFormController
