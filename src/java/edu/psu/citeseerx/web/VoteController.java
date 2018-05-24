package edu.psu.citeseerx.web;


import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.myciteseer.domain.Account;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.RuntimeException;
import java.io.IOException;
import java.util.ArrayList;

public class VoteController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest request,
		HttpServletResponse response)
			throws ServletException, IOException {

				return new ModelAndView("vote");
			}


}
