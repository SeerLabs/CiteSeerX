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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;

import edu.psu.citeseerx.dao2.logic.CSXDAO;

/**
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ViewPDFPageController implements Controller {
    
    private static int OUTPUT_BYTE_ARRAY_INITIAL_SIZE = 4096;
    
    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {
        
        String errorTitle = "Document Not Found";
        
        String doi = request.getParameter("doi");
        String rep = request.getParameter("rep");
        String page = request.getParameter("page");

        Map<String, Object> model = new HashMap<String, Object>();
        if (doi == null || rep == null) {
            model.put("pagetitle", errorTitle);
            return new ModelAndView("viewDocError", model);
        }
        
        int iPage;
        try {
            iPage = Integer.parseInt(page);
        }catch (NumberFormatException e) {
            e.printStackTrace();
            model.put("pagetitle", errorTitle);
            return new ModelAndView("viewDocError", model);
        }
        
        try {
            PdfReader reader = csxdao.getPdfReader(doi, rep);
            Document document = new Document(
                    reader.getPageSizeWithRotation(iPage));
            ByteArrayOutputStream baos =
                new ByteArrayOutputStream(OUTPUT_BYTE_ARRAY_INITIAL_SIZE);
            PdfCopy copy = new PdfCopy(document, baos);
            document.open();
            PdfImportedPage docPage = copy.getImportedPage(reader, iPage);
            copy.addPage(docPage);
            document.close();
            
            response.setContentType("application/pdf");
            response.setContentLength(baos.size());
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            out.flush();
        }catch (IOException e) {
            e.printStackTrace();
            model.put("pagetitle", errorTitle);
            return new ModelAndView("viewDocError", model);
        }catch(DocumentException e) {
            e.printStackTrace();
            model.put("pagetitle", errorTitle);
            return new ModelAndView("viewDocError", model);
        }
        return null;
    } //- handleRequest
} //- class ViewPDFTableController
