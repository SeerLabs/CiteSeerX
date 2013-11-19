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

/*

	Hacked in by: Pradeep Teregowda 08-18-2008 
*/

package edu.psu.citeseerx.web;

import java.io.IOException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Provides model objects to simple document submission and process the
 * data submission.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class SimpleDocSubmit implements Controller {
    
    
    /** Value to be supplied with the "rt" URL parameter.  Value is "view" */
    public static final String VIEW_TYPE = "view";

    /** Value to be supplied with the "rt" URL parameter.  Value is "send" */
    public static final String SEND_TYPE = "send";

    private String fileName = "";
    private static Set<String> reqTypes = new HashSet<String>();
    private static Logger logger;
    private static FileHandler fh;
	
    static {
        reqTypes.add(VIEW_TYPE);
        reqTypes.add(SEND_TYPE);        
    }
    
    private String defaultEmail = "you@domain.org";
    private String defaultURL = "http://yourhomepage.org";
    
    public void setDefaultEmail(String demail) {
        this.defaultEmail = demail;
    }
    
    public void setDefaultURL(String durl) {
        this.defaultURL = durl;
    }
   
    public void setLogFile(String logFile) {
        this.fileName = logFile;
    }
 
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        String addr    = request.getParameter("email");
        String submitURL    = request.getParameter("url");
        String reqType = request.getParameter("rt");
        Account account = MCSUtils.getLoginAccount();
        
        if (addr == null && account != null) {
        	addr = account.getEmail();
        }
        else if (addr == null && account == null) {
        	addr = defaultEmail;
        }

        if (submitURL == null) submitURL = defaultURL;
  
      	if (reqType == null || !reqTypes.contains(reqType)) reqType = VIEW_TYPE;
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("email", addr);
        model.put("url", submitURL);

        Boolean error = false;
        String errMsg = "";
        
    	if(submitURL == defaultURL) {
    		error = true;
    		errMsg = "Please enter a valid document link";
    	}


        if (!isValidEmailAddress(addr)) {
            error = true;
            errMsg = "Please enter a valid e-mail address";
        }

    	if(!isURLValid(submitURL)) {
    	    error = true;
    	    errMsg = "Please enter a valid web page link";
    	}

        model.put("error", error);
        model.put("errMsg", errMsg);


        if (reqType != null && reqType.equals(SEND_TYPE)) {
            storeSubmissioninLog(model);
            error = (Boolean)model.get("error");
        }

        if (!error && reqType.equals(SEND_TYPE)) {
            return new ModelAndView("submitSent", null);
        } else {
            return new ModelAndView("submit", model);
        }
        
    }  //- handleRequest
   

    private static boolean isValidEmailAddress(String emailAddress) {
	/* Using regular expressions and extending ideas */

	String emailRegex = "^[^\\.@][_\\w\\.\\-\\+&]+@[\\w\\.-]+\\.[a-z]{2,6}$";
	Matcher me;
	me = Pattern.compile(emailRegex).matcher(emailAddress);
	if(me.matches()) {
		return true;
	}
	return false;
    }
	
    private static boolean isURLValid(String inURL) {
	 /* String regexes for the most common observed invalid submissions */

	 String wordRegex = "(xanax|VIAGRA|CIALIS|phentermine|LEVITRA|VALIUM|MERIDIA|tramadol|ATIVAN|ULTRAM|AMBIEN|ALPRAZOLAM)[-_0-9HT\b]";
         String domainRegex = "(ieeexplore|acm|tetongravity|keyhole|extjs|swsoft|softimage|forum|discussion|freespeech|scam|ezproxy)\\.";
         String urlString = "(viewtopic\\.php|\\/boards\\/)";
	 Matcher mw, md, mu;
	 mw = Pattern.compile(wordRegex,Pattern.CASE_INSENSITIVE).matcher(inURL);
	 md = Pattern.compile(domainRegex).matcher(inURL);
	 mu = Pattern.compile(urlString).matcher(inURL);
	 if(mw.find() || md.find() || mu.find()) {
		return false;
	 }
	 try {
		new URL(inURL);
	 }
	 catch (MalformedURLException m) {
		return false;
	 }
	 return true;
    }
 
    protected void storeSubmissioninLog(Map<String, Object> model) {
	/* 
		Log the message ... we will look at it later
	*/
	logger = Logger.getLogger("edu.psu.citeseerx.web.SimpleDocSubmit");
	try {
		fh = new FileHandler(fileName, true);
		logger.addHandler(fh);
		logger.info((String)model.get("email")+":"+(String)model.get("url"));
		fh.close();
	}
	catch (Exception exc) {
		model.put("error", true);
		model.put("errMsg", "Submissions is not currently active, please try again later");
        	// model.put("errMsg", "Store Error"+exc.getMessage());
	};
       	 
    }  //- storeSubmissioninLog

}  //- class SimpleDocSubmit
