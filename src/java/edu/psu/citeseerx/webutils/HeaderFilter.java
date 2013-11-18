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
package edu.psu.citeseerx.webutils;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Set the configured response headers. The filter will set headers that
 * are set as initial parameters.
 * An example is:
 *   <param-name>Cache-Control</param-name>
 *   <param-value>max-age=3600</param-value>
 *   
 * This filter is based on the one presented at:
 *   http://www.onjava.com/pub/a/onjava/2004/03/03/filters.html
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class HeaderFilter implements Filter {

	private FilterConfig fc;
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		this.fc = null;
	} //- destroy

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Enumeration<String> parameters;  
		String headerName = null;
		HttpServletResponse res = (HttpServletResponse)response;
		for (parameters = fc.getInitParameterNames();
			parameters.hasMoreElements();) {
			
			headerName = parameters.nextElement();
			res.addHeader(headerName, fc.getInitParameter(headerName));
		}
		chain.doFilter(request, res);
	} //- doFilter

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		Enumeration<String> params;

		this.fc = config;
	} //- init

} //- HeaderFilter
