/**
 * 
 */
package edu.psu.citeseerx.myciteseer.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Controls how to show user information 
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Revision$$ $$Date$$
 */
public class ViewUserController implements Controller {

	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		Boolean error = false;
		String errMsg = null;
		Account userAccount = null;
		
		String username = ServletRequestUtils.getStringParameter(request, 
				"userid", "");
		if (username.trim().length() == 0) {
			error = true;
			errMsg = "Bad userid : \"" + username;
		}else{
			// Obtain userinfo.
			 userAccount = myciteseer.getAccount(username); 
			if (userAccount == null) {
				error = true;
				errMsg = "Profile for: \"" + username + " not found";
			}
		}

		if (!error) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("profile", userAccount);
			return new ModelAndView("viewUserProfile", model);
		}else{
			return MCSUtils.errorPage(errMsg);
		}
	} //- handleRequest
} //- class ViewUserController
