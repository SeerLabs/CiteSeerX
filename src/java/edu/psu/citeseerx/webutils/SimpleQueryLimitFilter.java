/*
 * Copyright 2014 Penn State University
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

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Filter to prevent massive queries from the same ip address. 
 * @author Jian Wu
 * @author Isaac Councill
 * @author Kyle Williams
 * @version $Rev$ $Date$
 */
public class SimpleQueryLimitFilter implements Filter {
    
    // the default max number of queries daily
    private int limit = 3;
    private String redirectUrl = "";

    // 3 Seconds.
    private Long timeLimit = new Long(3000);
    
    // "ipQueryLog.txt" contains queries that exceed the limit. These requests
    // are not full-filled
    private HashMap<String,Integer> qlCounts = new HashMap<String,Integer>();
    private String[] allowedUserAgents = new String[0];
    private String[] allowedIPs = new String[0];
    private String ipLogFilter = "ipQueryLog.txt";
    private BufferedWriter ipLogWriter = null;
    
    private HashMap<String, Long>sameQL = new HashMap<String, Long>();
 
    /* Read parameters from Servlet configeration file
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        
        // the Url when the limit is exceeded
        String redirectUrlStr = config.getInitParameter("redirectUrl");
        if (redirectUrlStr != null) {
            redirectUrl = redirectUrlStr;
        } else {
            System.err.println("SimpleQueryLimitFilter: no redirectUrl " +
                    "specified!");
        }
        
        // the max number of Queries daily configured in web.xml
        String limitStr = config.getInitParameter("limit");
        try {
            limit = Integer.parseInt(limitStr);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SimpleQueryLimitFilter: +" +
                    "Invalid limit specified: "+limitStr);
            System.err.println("Using default limit of "+limit);
        }
 
        // the allowed agents that are not limited (optional)
        String allowedua = config.getInitParameter("allowedUserAgents");
        if (allowedua != null) {
            allowedUserAgents = allowedua.split(",");
            for (int i=0; i<allowedUserAgents.length; i++) {
                String str = allowedUserAgents[i];
                str = str.trim();
                str = str.toLowerCase();
                allowedUserAgents[i] = str;
            }
        }

        // the allowed IPs that are not limited (optional)
        String allowedip = config.getInitParameter("allowedIPs");
        if(allowedip != null) {
            allowedIPs = allowedip.split(",");
            for(int i=0; i < allowedIPs.length; i++) {
                String str = allowedIPs[i];
                str = str.trim();
                allowedIPs[i] = str;
            }	    
        }

        // time between consecutive hits from the same IP
        String timeLimitStr = config.getInitParameter("timeLimit");
        try {
            timeLimit = Long.parseLong(timeLimitStr);
        }catch (Exception e) {
            e.printStackTrace();
            System.err.println("SimpleQueryLimitFilter: " +
                    "Invalid time limit specified: "+timeLimitStr);
            System.err.println("Using default limit of "+timeLimit);
        }

        // log file recording query requests
        String ipLogFileStr = config.getInitParameter("ipLogFile");
        if (ipLogFileStr != null) {
            ipLogFilter = ipLogFileStr;
        }
        try {
            ipLogFilter = config.getServletContext().getRealPath("/") +
                ipLogFilter;
            ipLogWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(ipLogFilter, true), "UTF-8"));
        }catch (Exception e) {
            ipLogWriter = null;
            e.printStackTrace();
            System.err.println("SimpleQueryLimitFilter: " +
                    "A problem ocurred while opening the log file: "+ipLogFilter);
        }
    }  //- init
    
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain)
    throws IOException, ServletException {

        // if request is from an allowed agent or IP, skip this filter and 
        // go to the next filter
        if (allowedUserAgentorIP(request)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Actions taken if request limit is exceeded
        // (1) set status to 403; 
        // (2) redirect to error page;
        if (requestsExceeded(request)) {
            if (response instanceof HttpServletResponse) {
                HttpServletResponse hreq = (HttpServletResponse)response;
                hreq.setStatus(403);
                String context = ((HttpServletRequest)request).getContextPath();
                hreq.sendRedirect(context+redirectUrl);
            }
            return;
        }

        // go to the next filter
        chain.doFilter(request, response);
        
    }  //- doFilter
    
    
    /*
     * Judge if the query limit is exceeded
     */
    private synchronized boolean requestsExceeded(ServletRequest request) {

        updateFlushTime();
        String ipaddr = request.getRemoteAddr();
        HttpServletRequest hreq = (HttpServletRequest)request;
        String rQS = hreq.getQueryString();
        /*
        String keySameQL = ipaddr+rQS;

        if (sameQL.containsKey(keySameQL)) {
            Long lastHit = sameQL.get(keySameQL);
            Long actualTime = System.currentTimeMillis();
            if ((actualTime-lastHit) <= timeLimit) {
                if (ipLogWriter!=null) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append(ipaddr);
                    buffer.append('\t');
                    buffer.append(rQS);
                    buffer.append('\t');
                    buffer.append(new Date(actualTime));
                    try {
                        ipLogWriter.write(buffer.toString());
                        ipLogWriter.newLine();
                        ipLogWriter.flush();
                    }catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("SimpleQueryLimitFilter: " +
                                "A problem ocurred while writing to the log file: "
                                + ipLogFilter);
                    }
                }
                return true;
            }
            sameQL.put(keySameQL, actualTime);
        }else{
            sameQL.put(keySameQL, System.currentTimeMillis());
        }
        */
        if (qlCounts.containsKey(ipaddr)) {
            Integer dlc = qlCounts.get(ipaddr);
            if (dlc >= limit) {
                return true;
            }
            qlCounts.put(ipaddr, dlc+1);
        } else {
            qlCounts.put(ipaddr, new Integer(1));
        }
        return false;
        
    }  //- requestsExceeded
        
    
    private long lastFlushTime = System.currentTimeMillis();
    private static final long ONE_DAY = 1000*60*60*24;
    
    /* if the time interval is greater than one day, re-do the counting and change the flush time
     * otherwise, do not change the flush time
     */
    private void updateFlushTime() {
        long currentTime = System.currentTimeMillis();
        if (currentTime-lastFlushTime >= ONE_DAY) {
            qlCounts.clear();
            sameQL.clear();
            lastFlushTime = currentTime;
        }
        
    }  //- updateFlushTime
    
    
    /* Judge if Servlet request is from an allowed user agent or an allowed IP
     * Check user agent first. If it is not allowed, check IP. 
     */
    private boolean allowedUserAgentorIP(ServletRequest request) {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest hreq = (HttpServletRequest)request;
            String useragent = hreq.getHeader("User-Agent");
            String clientIP = hreq.getRemoteAddr();
            String user = (useragent != null) ? useragent.toLowerCase() : "";
            for (int i=0; i<allowedUserAgents.length; i++) {
                if (user.indexOf(allowedUserAgents[i]) != -1) {
                    return true;
                }
            }
            if(clientIP == null)  { return false; }
            for (int i = 0; i < allowedIPs.length; i++) {
                if ( clientIP.equals(allowedIPs[i])) {
                    return true;
                }
            }
        }
        return false;
        
    }  //- allowedUserAgent

    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        if (ipLogWriter!= null) {
            try {
                ipLogWriter.close();
            }catch (IOException e) {
                e.printStackTrace();
                System.err.println("SimpleQueryLimitFilter: +" +
                        "A problem ocurred while closing the log file: " +
                        ipLogFilter);
            }
        }
    }
    
}  //- class SimpleQueryLimitFilter
