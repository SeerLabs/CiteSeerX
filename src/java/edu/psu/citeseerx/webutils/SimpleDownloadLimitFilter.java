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
 * Filter to prevent massive downloads from the same ip address. 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class SimpleDownloadLimitFilter implements Filter {
    
    private int limit = 1000;
    private String redirectUrl = "";

    // 30 Seconds.
    private Long timeLimit = new Long(3000);
    
    private HashMap<String,Integer> dlCounts = new HashMap<String,Integer>();
    private String[] allowedUserAgents = new String[0];
    private String[] allowedIPs = new String[0];
    private String ipLogFilter = "ipLog.txt";
    private BufferedWriter ipLogWriter = null;
    
    private HashMap<String, Long>sameDL = new HashMap<String, Long>();
 
    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        
        String redirectUrlStr = config.getInitParameter("redirectUrl");
        if (redirectUrlStr != null) {
            redirectUrl = redirectUrlStr;
        } else {
            System.err.println("SimpleDownloadLimitFilter: no redirectUrl " +
                    "specified!");
        }
        
        String limitStr = config.getInitParameter("limit");
        try {
            limit = Integer.parseInt(limitStr);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SimpleDownloadLimitFilter: +" +
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
            System.err.println("SimpleDownloadLimitFilter: " +
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
            System.err.println("SimpleDownloadLimitFilter: " +
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
        
        if (downloadsExceeded(request)) {
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
    
    
    private synchronized boolean downloadsExceeded(ServletRequest request) {

        updateFlushTime();
        String ipaddr = request.getRemoteAddr();
        HttpServletRequest hreq = (HttpServletRequest)request;
        String rQS = hreq.getQueryString();
        String keySameDL = ipaddr+rQS;

        if (sameDL.containsKey(keySameDL)) {
            Long lastHit = sameDL.get(keySameDL);
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
                        System.err.println("SimpleDownloadLimitFilter: " +
                                "A problem ocurred while writing to the log file: "
                                + ipLogFilter);
                    }
                }
                return true;
            }
            sameDL.put(keySameDL, actualTime);
        }else{
            sameDL.put(keySameDL, System.currentTimeMillis());
        }
        
        if (dlCounts.containsKey(ipaddr)) {
            Integer dlc = dlCounts.get(ipaddr);
            if (dlc >= limit) {
                return true;
            }
            dlCounts.put(ipaddr, dlc+1);
        } else {
            dlCounts.put(ipaddr, new Integer(1));
        }
        return false;
        
    }  //- downloadsExceeded
        
    
    private long lastFlushTime = System.currentTimeMillis();
    private static final long ONE_DAY = 1000*60*60*24;
    
    private void updateFlushTime() {
        long currentTime = System.currentTimeMillis();
        if (currentTime-lastFlushTime >= ONE_DAY) {
            dlCounts.clear();
            sameDL.clear();
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
                System.err.println("SimpleDownloadLimitFilter: +" +
                        "A problem ocurred while closing the log file: " +
                        ipLogFilter);
            }
        }
    }
    
}  //- class SimpleDownloadFilter
