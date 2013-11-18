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

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;

/**
 * Controller used to edit user information
 * @see org.springframework.web.servlet.mvc.Controller
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class EditUserController implements Controller {

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

        Account editaccount = null;

        String uid = request.getParameter("uid");
        if (uid != null) {
            editaccount = myciteseer.getAccountOrNull(uid);
        } else {
            return new ModelAndView("admin/searchUser", model);
        }
                
        if (editaccount == null) {
            model.put("error", new Boolean(true));
            model.put("errMsg", "No user matching id \""+uid+"\"");
            return new ModelAndView("admin/searchUser", model);
        }
        
        String type = request.getParameter("type");
        if (type != null && type.equals("update")) {
            String admin = request.getParameter("setadmin");
            String enabled = request.getParameter("setenabled");
            try {
                boolean setAdmin = false;
                boolean setEnabled = false;
                if (admin != null && admin.equals("on")) {
                    setAdmin = true;
                    System.out.println("yes admin");
                } else {
                    System.out.println("no admin");                    
                }
                if (enabled != null && enabled.equals("on")) {
                    setEnabled = true;
                    System.out.println("yes enabled");
                } else {
                    System.out.println("no enabled");
                }
                editaccount.setAdmin(setAdmin);
                editaccount.setEnabled(setEnabled);
                myciteseer.updateAccount(editaccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Boolean admin = editaccount.isAdmin();
        Boolean enabled = editaccount.isEnabled();

        model.put("editaccount", editaccount);
        model.put("admin", admin);
        model.put("enabled", enabled);
        
        return new ModelAndView("admin/editUser", model);
        
    }  //- handleRequest
    
}  //- EditUserController
