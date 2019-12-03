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
package edu.psu.citeseerx.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.psu.citeseerx.repository.RepositoryMap;
import edu.psu.citeseerx.utility.FileNamingUtils;
import edu.psu.citeseerx.webutils.RedirectUtils;

/**
 * Servlet providing the chart for a document.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class CiteChartServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -579137840114667796L;
    private final static String repID = "chartRepository";
    private RepositoryMap repMap = new RepositoryMap();
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        String chartRepository =
            getServletConfig().getInitParameter(repID);
        Map<String, String> map = new HashMap<String, String>();
        map.put(repID, chartRepository);
        repMap.setRepositoryMap(map);
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String doi = request.getParameter("doi");
        //String repID = request.getParameter("rep");

        if (doi != null && repID != null) {
            String relPath = FileNamingUtils.getDirectoryFromDOI(doi);
            relPath += "citechart.png";
            String path = repMap.buildFilePath(repID, relPath);
            File chartFile = new File(path);
            if (!chartFile.exists()) {
                RedirectUtils.sendRedirect(request, response,
                    "/images/nochart.png");
                return;
            }
            
            response.reset();
            response.setContentType("image/png");
            //response.setHeader("Content-Disposition",
            //        "attachment; filename=\""+doi+".pdf\"");
            
            FileInputStream in = new FileInputStream(chartFile);
            BufferedInputStream input = new BufferedInputStream(in);
            
            int contentLength = input.available();
            response.setContentLength(contentLength);
        
            BufferedOutputStream output =
                new BufferedOutputStream(response.getOutputStream());
            while(contentLength-- > 0) {
                output.write(input.read());
            }
            output.flush();

        } else {
            RedirectUtils.sendRedirect(request, response,
                    "/images/nochart.png");
        }
        
    }  //- doGet
    
}  //- class CiteChartServlet
