/**
 * 
 */
package edu.psu.citeseerx.myciteseer.web.groups;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;

/**
 * Removes an user from a group
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Revision: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class RemoveMemberController implements Controller {

	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse resonse) throws Exception {
		
		boolean error = false;
		String errMsg = "";
		
		long groupID = 
			ServletRequestUtils.getLongParameter(request, "gid", -1);
		String userid = 
			ServletRequestUtils.getStringParameter(request, "userid", null);
		String tab =
			ServletRequestUtils.getStringParameter(request, "tab", "Member");
		if (groupID == -1) {
			error = true;
			errMsg = "Bad group ID : \"" + groupID + "\"";
		}
		if (userid == null) {
			error = true;
			errMsg = "Bad user ID : \"" + userid + "\"";
		}
		try {
			Group group = myciteseer.getGroup(groupID);
			myciteseer.removeMember(group, userid);
		}catch (DataAccessException e) {
			e.printStackTrace();
			error = true;
			errMsg = "An error occurred during the processing of your " +
	            "request. Please try again later.";
		}
		HashMap<String, Object> model = new HashMap<String, Object>();
		if (error) {
            model.put("errMsg", errMsg);
            return new ModelAndView("parameterError", model);
		}
		else {
			model.put("gid", groupID);
			model.put("tab", tab);
			return new ModelAndView(new RedirectView("viewGroupMembers"), model);
		}
	}

}
