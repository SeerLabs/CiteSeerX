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

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Friend;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

public class AddFriendFormController extends SimpleFormController {

    public AddFriendFormController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("friend");
    }
    private MyCiteSeerFacade myciteseer;

    public ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
    throws ServletException, Exception {

        Account account = MCSUtils.getLoginAccount();
        Friend friend = (Friend)command;

        myciteseer.addFriend(account, friend.getId());

        return super.onSubmit(request, response, command, errors);
    }

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {

        return new Friend();
        
    /* Saved for future use - add someone directly to "my" network
        String query = request.getQueryString();
    if (query.length() > 0) {
            addFrd.AddToFriendList(query.substring(query.indexOf("=") + 1, query.length()));
    }
    */

     }

    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        
        Friend friend = (Friend)command;
        errors.setNestedPath("friend");
        getValidator().validate(friend, errors);
        errors.setNestedPath("");
                
    }  //- onBindAndValidate
    
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }

}
