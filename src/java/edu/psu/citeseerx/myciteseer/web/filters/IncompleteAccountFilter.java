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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Filter to redirect users with incomplete account registration to complete
 * their registration process before being able to login into the system.  
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class IncompleteAccountFilter implements Filter {

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

        Account account = MCSUtils.getLoginAccount();
        if (account == null || !account.isComplete()) {
            String context = ((HttpServletRequest)request).getContextPath();
            String uri = ((HttpServletRequest)request).getRequestURI();
            if (!uri.matches("^"+context+
                    config.getInitParameter("redirectURL")+"$")) {
                HttpServletResponse hres = (HttpServletResponse)response;
                hres.sendRedirect(context+
                        config.getInitParameter("redirectURL"));
                return;
            }
        }
        chain.doFilter(request, response);
        
    }  //- doFilter
    
}  //- class IncompleteAccountFilter
