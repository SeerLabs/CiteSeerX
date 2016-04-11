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

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Filter to prevent massive pageviews from the same ip address. 
 * Returns a Pageview Limit Exceeded page after 2000 pages are viewed OR
 * if hitting rate is faster than 3 times per second.
 * Default limits can be overridden in web.xml file. 
 * pageviewFilter.txt records IPs who have exceeded this limit.
 * Exceptional IPs or agents can be added in web.xml. 
 * @author Jian Wu
 * @version $Rev$ $Date$
 */
public class SimplePageviewLimitFilter implements Filter {
    
    private int limit = 2000;
    private String redirectUrl = "";

    // 3 Seconds.
    private Long timeLimit = new Long(3000);
    
    // pvCounts: pageview counts
    private HashMap<String,Integer> pvCounts = new HashMap<String,Integer>();
    private String[] allowedUserAgents = new String[0];
    private String[] allowedIPs = new String[0];
    private String ipLogFilter = "pageviewFilter.txt";
    private BufferedWriter ipLogWriter = null;
    //a PV is a concatenation of an IP and its request; samePV records the times of hits from the same PV
    private HashMap<String, Long>samePV = new HashMap<String, Long>();
 
    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        
        String redirectUrlStr = config.getInitParameter("redirectUrl");
        if (redirectUrlStr != null) {
            redirectUrl = redirectUrlStr;
        } else {
            System.err.println("SimplePageviewLimitFilter: no redirectUrl " +
                    "specified!");
        }
        
        String limitStr = config.getInitParameter("limit");
        try {
            limit = Integer.parseInt(limitStr);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SimplePageviewLimitFilter: +" +
                    "Invalid limit specified: "+limitStr);
            System.err.println("Using default limit of "+limit);
        }
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
        String allowedip = config.getInitParameter("allowedIPs");
        if(allowedip != null) {
            allowedIPs = allowedip.split(",");
            for(int i=0; i < allowedIPs.length; i++) {
                String str = allowedIPs[i];
                str = str.trim();
                allowedIPs[i] = str;
            }	    
        }
        String timeLimitStr = config.getInitParameter("timeLimit");
        try {
            timeLimit = Long.parseLong(timeLimitStr);
        }catch (Exception e) {
            e.printStackTrace();
            System.err.println("SimplePageviewLimitFilter: " +
                    "Invalid time limit specified: "+timeLimitStr);
            System.err.println("Using default limit of "+timeLimit);
        }
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
            System.err.println("SimplePageviewLimitFilter: " +
                    "A problem ocurred while opening the log file: "+ipLogFilter);
        }
    }  //- init
    
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain)
    throws IOException, ServletException {

        if (allowedUserAgentorIP(request)) {
            chain.doFilter(request, response);
            return;
        }
        
        if (pageviewsExceeded(request)) {
            if (response instanceof HttpServletResponse) {
                HttpServletResponse hreq = (HttpServletResponse)response;
                hreq.setStatus(403);
                String context = ((HttpServletRequest)request).getContextPath();
                hreq.sendRedirect(context+redirectUrl);
            }
            return;
        }
        chain.doFilter(request, response);
        
    }  //- doFilter
    
    
    private synchronized boolean pageviewsExceeded(ServletRequest request) {

        updateFlushTime();
        String ipaddr = request.getRemoteAddr();
        HttpServletRequest hreq = (HttpServletRequest)request;
        String rQS = hreq.getQueryString();
        String keySamePV = ipaddr+rQS;

        if (samePV.containsKey(keySamePV)) {
            Long lastHit = samePV.get(keySamePV);
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
                        System.err.println("SimplePageviewLimitFilter: " +
                                "A problem ocurred while writing to the log file: "
                                + ipLogFilter);
                    }
                }
                return true;
            }
            samePV.put(keySamePV, actualTime);
        }else{
            samePV.put(keySamePV, System.currentTimeMillis());
        }
        
        if (pvCounts.containsKey(ipaddr)) {
            Integer pvc = pvCounts.get(ipaddr);
            if (pvc >= limit) {
                return true;
            }
            pvCounts.put(ipaddr, pvc+1);
        } else {
            pvCounts.put(ipaddr, new Integer(1));
        }
        return false;
        
    }  //- downloadsExceeded
        
    
    private long lastFlushTime = System.currentTimeMillis();
    private static final long ONE_DAY = 1000*60*60*24;
    
    private void updateFlushTime() {
        long currentTime = System.currentTimeMillis();
        if (currentTime-lastFlushTime >= ONE_DAY) {
            pvCounts.clear();
            samePV.clear();
            lastFlushTime = currentTime;
        }
        
    }  //- updateFlushTime
    
    
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
                System.err.println("SimplePageviewLimitFilter: +" +
                        "A problem ocurred while closing the log file: " +
                        ipLogFilter);
            }
        }
    }
    
}  //- class SimpleDownloadFilter
