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
package edu.psu.citeseerx.myciteseer.web;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Friend;
import edu.psu.citeseerx.myciteseer.domain.UserMessage;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

public class MyNetController implements Controller {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Account account = MCSUtils.getLoginAccount();
        
        // Retrieves my friends list
        List<Friend> friendsList = myciteseer.getFriends(account);

        // Retrieves new messages if any
        List<UserMessage> newMessages = myciteseer.getMessages(account);

        // Add to data model that will be presented on the view
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("friends", friendsList);
        dataMap.put("numberNewMsg", String.valueOf(newMessages.size()));

        return new ModelAndView("mynet", "model", dataMap);
    }
    
}
