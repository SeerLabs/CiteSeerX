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

import java.io.IOException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.mail.MailException;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.mail.MailManager;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.util.*;

/**
 * Provides model objects to feedback view, and process feedback submission.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class FeedbackController implements Controller {
    
    private MailManager mailManager;
    
    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    } //- setMailManager
    
    
    /** Value to be supplied with the "rt" URL parameter.  Value is "view" */
    public static final String VIEW_TYPE = "view";

    /** Value to be supplied with the "rt" URL parameter.  Value is "verify" */    
    public static final String VERIFY_TYPE = "verify";

    /** Value to be supplied with the "rt" URL parameter.  Value is "send" */
    public static final String SEND_TYPE = "send";

    private static HashSet<String> reqTypes = new HashSet<String>();
    static {
        reqTypes.add(VIEW_TYPE);
        reqTypes.add(VERIFY_TYPE);
        reqTypes.add(SEND_TYPE);        
    }
    
    private String defaultName = "Anonymous";
    private String defaultEmail = "you@your.domain";
    private String defaultSubj = "No subject";
    private String defaultMsg = "Enter your comments";
    
    public void setDefaultName(String name) {
        this.defaultName = name;
    } //- setDefaultName
    
    public void setDefaultEmail(String email) {
        this.defaultEmail = email;
    } //- setDefaultEmail
    
    public void setDefaultSubject(String subject) {
        this.defaultSubj = subject;
    } //- setDefaultSubject
    
    public void setDefaultMsg(String msg) {
        this.defaultMsg = msg;
    } //- setDefaultMsg
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        String name    = request.getParameter("name");
        String addr    = request.getParameter("addr");
        String subj    = request.getParameter("subj");
        String msg     = request.getParameter("msg");
        String reqType = request.getParameter("rt");
        Account account = MCSUtils.getLoginAccount();
        
        if (name == null && account != null) {
        	name = account.getFirstName() + " " + 
        		account.getMiddleName() + " " + account.getLastName();
        }else if (name == null && account == null) {
        	name = defaultName;
        }
        if (addr == null && account != null) {
        	addr = account.getEmail();
        }else if (addr == null && account == null) {
        	addr = defaultEmail;
        }
        if (subj == null) subj = defaultSubj;
        if (msg == null) msg = defaultMsg;
        if (reqType == null || !reqTypes.contains(reqType)) reqType = VIEW_TYPE;
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        model.put("addr", addr);
        model.put("subj", subj);
        model.put("msg", msg);

        Boolean error = false;
        String errMsg = "";
        
        if (!MailManager.isValidEmailAddress(addr)) {
            error = true;
            errMsg = "Supplied email address appears to be invalid";
        }

        if (addr.equals(defaultEmail) && !reqType.equals(VIEW_TYPE)) {
            error = true;
            errMsg = "Please enter your email address";
        }

        model.put("error", error);
        model.put("errMsg", errMsg);

        if (reqType != null && reqType.equals(SEND_TYPE)) {
            sendFeedback(model);
            error = (Boolean)model.get("error");
        }

        if (!error && reqType.equals(VERIFY_TYPE)) {
        	model.put("pagetitle", "Feedback Verification");
            model.put("pagedescription", "Verify feedback message.");
            model.put("pagekeywords", "Feedback, message, verify");
            return new ModelAndView("feedbackVerify", model);
        } else if (!error && reqType.equals(SEND_TYPE)) {
        	model.put("pagetitle", "Feedback Sent");
            model.put("pagedescription", "Acknowledgement that a feedback " + 
            		"message was sent.");
            model.put("pagekeywords", "Feedback, message, sent");
            return new ModelAndView("feedbackSent", model);
        } else {
        	model.put("pagetitle", "Feedback");
            model.put("pagedescription", "Send feedback to the developer team.");
            model.put("pagekeywords", "Feedback");
            return new ModelAndView("feedback", model);
        }
        
    }  //- handleRequest
    
    
    protected void sendFeedback(Map<String, Object> model) {
        try {
            mailManager.sendFeedbackMessage(
                    (String)model.get("name"), (String)model.get("addr"),
                    (String)model.get("subj"), (String)model.get("msg"));
        } catch (MailException e) {
            model.put("error", new Boolean(true));
            model.put("errMsg",
                    "There was an error sending your message: "+e.getMessage());
        }
        
    }  //- sendFeedback

}  //- class FeedbackController
