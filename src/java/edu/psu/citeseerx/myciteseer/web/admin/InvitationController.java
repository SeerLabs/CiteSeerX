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
package edu.psu.citeseerx.myciteseer.web.admin;

import java.io.IOException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.mail.MailException;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.myciteseer.web.mail.MailManager;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.RandomStringGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Controller used to send invitations to join the system
 * @see org.springframework.web.servlet.mvc.Controller
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class InvitationController implements Controller {

    private MailManager mailManager;
    
    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    } //- setMailManager
    
    
    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
    
        Account adminAccount = MCSUtils.getLoginAccount();
        if (!adminAccount.isAdmin()) {
            return new ModelAndView("admin/adminRequired", null);
        }
        
        HashMap<String, Object> model = new HashMap<String, Object>();

        Boolean error = false;
        String errMsg = null;
        String msg = null;
        
        String emailStr = request.getParameter("invite");
        List<String> addresses = new ArrayList<String>();
        if (emailStr != null) {
            StringTokenizer st = new StringTokenizer(emailStr);
            while(st.hasMoreTokens()) {
                String address = st.nextToken();
                if (MailManager.isValidEmailAddress(address)) {
                    addresses.add(address);
                } else {
                    error = true;
                    errMsg = "Invalid address: "+address+
                        ".  No messages were sent";
                }
            }
        }

        String ccListStr = request.getParameter("alsocc");
        List<String> ccList = new ArrayList<String>();
        if (ccListStr != null) {
            StringTokenizer st = new StringTokenizer(ccListStr);
            while(st.hasMoreTokens()) {
                String address = st.nextToken();
                if (MailManager.isValidEmailAddress(address)) {
                    ccList.add(address);
                } else {
                    error = true;
                    errMsg = "Invalid CC address: "+address+
                        ".  No messages were sent";
                }
            }
        }

        String message = request.getParameter("message");
        
        String ccStr = request.getParameter("cc");
        String ccAddr = MCSUtils.getLoginAccount().getEmail();
        if (ccStr != null && ccStr.equals("on")) {
            if (!ccList.contains(ccAddr)) {
                ccList.add(ccAddr);
            }
        }
        
        if (!error && !addresses.isEmpty()) {
            for (String address : addresses) {
                try {
                    String url = buildInvitationUrl(request);
                    mailManager.sendInvitationMessage(address, ccList,
                            message, url);
                } catch (MailException e) {
                    e.printStackTrace();
                    error = true;
                    errMsg += " Address "+address+" generated an exception: "+
                    e.getMessage();
                }
            }
        }
        
        if (!error && !addresses.isEmpty()) {
            msg = "Invitations were sent.";
        }

        model.put("error", error);
        model.put("errMsg", errMsg);
        model.put("msg", msg);
        return new ModelAndView("admin/invite", model);
        
    }
    
    
    private String buildInvitationUrl(HttpServletRequest request) {
        String ticket = RandomStringGenerator.randomString(15, 20);
        while(myciteseer.isValidInvitationTicket(ticket)) {
            ticket = RandomStringGenerator.randomString(15, 20);
        }
        myciteseer.storeInvitationTicket(ticket);
        
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append("https://");
        urlBuffer.append(request.getServerName());

        String context = request.getContextPath();
        if (context != null && context.length() > 0) {
            urlBuffer.append(request.getContextPath());
        }
        urlBuffer.append("/mcsutils/newAccount?ticket=");
        urlBuffer.append(ticket);
        
        return urlBuffer.toString();
        
    }  //- buildInvitationUrl

} //- InvitationController
