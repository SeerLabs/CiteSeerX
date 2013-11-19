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

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Deletes the current user from a group.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class LeaveGroupController implements Controller {
	
	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		long groupID;
		boolean error = false;
		String errMsg = "";
		
		groupID = 
			ServletRequestUtils.getLongParameter(request, "gid", -1);
		String tab =
			ServletRequestUtils.getStringParameter(request, "tab", "Member");
		if (groupID == -1) {
			error = true;
			errMsg = "Bad group ID : \"" + groupID + "\"";
		}
		try {
			Account account = MCSUtils.getLoginAccount();
			Group group = myciteseer.getGroup(groupID);
			if (group.getOwner().compareToIgnoreCase(account.getUsername()) == 0) {
				error = true;
				errMsg = "Group owner can't leave the group.";
			}else{
				myciteseer.leaveGroup(group, account.getUsername());
			}
		}catch (DataAccessException e) {
			e.printStackTrace();
			error = true;
			errMsg = "An error occurred during the processing of your " +
            "request. Please try again later.";
		}
		HashMap<String, String> model = new HashMap<String, String>();
		if (error) {
            model.put("errMsg", errMsg);
            return new ModelAndView("parameterError", model);
		}
		else {
			model.put("tab", tab);
			return new ModelAndView(new RedirectView("viewGroups"), model);
		}
	} //- handleRequest

} //- class LeaveGroupController
