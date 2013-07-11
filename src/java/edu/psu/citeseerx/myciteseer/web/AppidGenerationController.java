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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Process requests from user in order to generate an App id key.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class AppidGenerationController implements Controller {

private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    private String view;
    
    public void setView(String view) {
        this.view = view;
    } //- setView
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Account account = MCSUtils.getLoginAccount();
        String errMsg = null;
        boolean error = false;
        
        if (null == account) {
            error = true;
            errMsg = "Imposible to get the account information";
            System.err.println("Imposible to get the account information " +
            		"while trying to generate an application key");
        }
        
        String appKey = null;
        if (!error) {
            // Generate the key
            appKey = generateKey(account.getUsername(), 
                    account.getEmail());
            account.setAppid(appKey);
            try {
                myciteseer.changeAppid(account);
            }catch(Exception e) {
                error = true;
                errMsg = "A problem occurred while saving the application key";
                System.err.println(e.getStackTrace());
            }
        }
        
        if (error) {
            return MCSUtils.errorPage(errMsg);
        }else{
            HashMap<String,String> model = new HashMap<String,String>();
            model.put("name", account.getFirstName());
            model.put("apikey", appKey);
            return new ModelAndView(view, model);
        }
    } //- handleRequest

    /**
     * Generates a SHA key using username and e-mail as data
     * @param username
     * @param email
     * @return the genarated key
     */
    private String generateKey(String username, String email) {
        return DigestUtils.shaHex(username+email);
    } //- generateKey
} //- class AppidGenerationController