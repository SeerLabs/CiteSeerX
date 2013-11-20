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

import javax.servlet.http.*;
import java.io.IOException;

/**
 * Utility class providing several redirect methods
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class RedirectUtils {

    public static void redirectAcegiLogin(HttpServletRequest request,
            HttpServletResponse response,
            String username, String password)
    throws IOException {
        String context = request.getContextPath();
        String captcha = request.getParameter("j_captcha_response");
        response.sendRedirect(context+"/j_acegi_security_check?"+
                "j_username="+username+"&j_password="+password+
                "&j_captcha_response="+captcha);
    } //- redirectAcegiLogin

    public static void redirectAcegiLogout(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        String context = request.getContextPath();
        response.sendRedirect(context+"/j_acegi_logout");
    } //- redirectAcegiLogout

    /**
     * Redirects to the given path
     * @param request
     * @param response
     * @param path
     * @throws IOException
     */
    public static void sendRedirect(HttpServletRequest request,
            HttpServletResponse response, String path) throws IOException {
        String context = request.getContextPath();
        response.sendRedirect(context+path);
    } //- sendRedirect

    /**
     * Issue a redirect stating that the resource have been moved permanently
     * @param request
     * @param response
     * @param path
     * @throws IOException
     */
    public static void sendPermanentRedirect(HttpServletRequest request,
            HttpServletResponse response, String path) throws IOException {
        String context = request.getContextPath();
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.addHeader("Location", context+path);
    } //- sendPermanentRedirect

    /**
     * Redirects the resource to the URL using DOI.
     * @param request
     * @param response
     * @param doi
     */
    public static void sendDocumentCIDRedirect(HttpServletRequest request,
            HttpServletResponse response, String doi) {
        StringBuffer urlBuf = request.getRequestURL();
        String queryStr = request.getQueryString();

        queryStr = queryStr.replaceAll("cid=\\d+", "doi="+doi);
        urlBuf.append("?");
        urlBuf.append(queryStr);

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.addHeader("Location", urlBuf.toString());
    } //- sendDocumentCIDRedirect

    /**
     * Obtain the servers base URL. The use of this method is not recommended
     * since it will return the base URL of the actual server processing the
     * request which could be different of the system URL in many scenarios. For
     * instance load balancing.
     * <b>This method should be deprecated</b>
     * @param request
     * @return returns the base URL.
     */
    public static String getBaseUrl(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getScheme());
        builder.append("://");

        builder.append(request.getServerName());
        int port = request.getServerPort();
        if (port != 80 && port != 443) {
            builder.append(":");
            builder.append(port);
        }
        builder.append(request.getContextPath());
        return builder.toString();
    } //- getBaseUrl

    /**
     * Redirects to an external site.
     * @param response
     * @param path
     */
    public static void externalRedirect(HttpServletResponse response,
            String path) throws IOException {
        response.sendRedirect(path);
    } //- externalRedirect
} //- class RedirectUtils
