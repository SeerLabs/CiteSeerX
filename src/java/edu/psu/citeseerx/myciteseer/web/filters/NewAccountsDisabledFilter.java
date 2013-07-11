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
package edu.psu.citeseerx.myciteseer.web.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Filter to redirect invited users to register into the system.  
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class NewAccountsDisabledFilter implements Filter {

    private FilterConfig config;
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        this.config = config;        
    } //- init
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() { }
    
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        try {
            MyCiteSeerFacade myciteseer =
                MCSUtils.getMyCiteSeer(config.getServletContext());
            MCSConfiguration configuration = myciteseer.getConfiguration();
            
            String ticket = request.getParameter("ticket");
            boolean ok = false;
            if (ticket != null && myciteseer.isValidInvitationTicket(ticket)) {
                ok = true;
            }
            if (!ok && !configuration.getNewAccountsEnabled()) {
                String context = ((HttpServletRequest)request).getContextPath();
                HttpServletResponse hres = (HttpServletResponse)response;
                hres.sendRedirect(context+
                        config.getInitParameter("redirectURL"));
                return;
            } 
        } catch (SQLException e) {
            throw new ServletException(e.getMessage());
        }
        chain.doFilter(request, response);
        
    }  //- doFilter
    
}  //- class IncompleteAccountFilter
