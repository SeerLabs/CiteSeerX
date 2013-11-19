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
package edu.psu.citeseerx.myciteseer.web.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.support.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.context.ApplicationContext;

import javax.servlet.*;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;

/**
 * Utility class
 * @author Isaac Councill
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class MCSUtils {

	/**
	 * @return Returns the connected user details.
	 */
    public static Account getLoginAccount() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authen = context.getAuthentication();
        Object principal = null;
        if (authen != null) {
            principal = authen.getPrincipal();
        }
        if (principal != null && principal instanceof Account) {
            return (Account)principal;
        } else {
            return null;
        }
        
    }  //- getLoginAccount

    /**
     * Returns the facade bean which gives access to the web application
     * operations.
     * @param context Session servlet context
     * @return The citeseerx facade bean
     */
    public static MyCiteSeerFacade getMyCiteSeer(ServletContext context) {
        ApplicationContext appContext =
            WebApplicationContextUtils.getWebApplicationContext(context);
        MyCiteSeerFacade myciteseer =
            (MyCiteSeerFacade)appContext.getBean("myCiteSeer");
        return myciteseer;
        
    }  //- getMyCiteSeer
    
    /**
     * Returns the ModelAndView which handle errors occurred in a controller. 
     * This function should be used by controllers, which don't have bind
     * functionality to show errors back, when errors are produced. 
     * @param errMsg
     * @return parameterError view
     */
    public static ModelAndView errorPage(String errMsg) {
    	Map<String, String> model = new HashMap<String, String>();
    	model.put("errMsg", errMsg);
    	return new ModelAndView("parameterError", model);
    } //- errorPage
    
}  //- class MCSUtils
