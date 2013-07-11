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

import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Tag;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Process requests to list tags. Renders the success view in case of
 * a valid submission otherwise shows the error view.
 * @author Isaac Council
 * @version $Rev$ $Date$
 */
public class ViewTagsController implements Controller {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest arg0,
            HttpServletResponse arg1) throws ServletException {
        
        Account account = MCSUtils.getLoginAccount();
        List<String> rawTags = myciteseer.getTags(account);
        
        List<Tag> tags = processTags(rawTags);
        
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("tags", tags);
        
        return new ModelAndView("viewTags", model);
    }
    
    
    private List<Tag> processTags(List<String> rawTags) {
        HashMap<String,Tag> tagMap = new HashMap<String,Tag>();
        for (Object rawTag : rawTags) {
            String raw = (String)rawTag;
            if (tagMap.containsKey(raw)) {
                Tag tag = tagMap.get(raw);
                tag.incrCount();
            } else {
                Tag tag = new Tag();
                tag.setTag(raw);
                tagMap.put(raw, tag);
            }
        }
        Collection<Tag> tags = tagMap.values();
        List<Tag> tagList = new ArrayList<Tag>();
        for (Tag tag : tags) {
            tagList.add(tag);
        }
        Collections.sort(tagList);
        return tagList;
        
    }  //- processTags
    
}  //- class ViewTagsController
