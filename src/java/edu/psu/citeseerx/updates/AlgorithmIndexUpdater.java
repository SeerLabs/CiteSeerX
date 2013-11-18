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
package edu.psu.citeseerx.updates;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Algorithm;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.utility.SafeText;

/**
 * Utilities for updating a Solr index to be consistent with the information
 * in the database.
 * @author Sumit Bathia
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class AlgorithmIndexUpdater {
    
    private static final String expectedResponse = 
        "<int name=\"status\">0</int>";
    
    private URL solrAlgorithmUpdateUrl;
    
    public void setSolrURL(String solrUpdateUrl) throws MalformedURLException {
        this.solrAlgorithmUpdateUrl = new URL(solrUpdateUrl);
    } //- setSolrURL
    
    private CSXDAO csxdao;
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    /*
     *  <field name="id" 
     *  <field name="caption" 
     *  <field name="synopsis" 
     *  <field name="reftext" 
     *  <field name="paperid" 
     *  <field name="year" 
     *  <field name="ncites" 
     *  <field name="page"
     */
    public void indexAll() {

        Date lastUpdate = csxdao.lastAlgorithmIndexTime();

        List<Algorithm> algorithmList = csxdao.getUpdatedAlgorithms(lastUpdate);
        StringBuffer xmlBuffer = new StringBuffer();
        xmlBuffer.append("<add>");
        for (Algorithm eachAlgorithm: algorithmList) {
            String doi = eachAlgorithm.getPaperIDForAlgorithm();
            Document doc = csxdao.getDocumentFromDB(doi);
            if(doc.isPublic()) {
                xmlBuffer.append("<doc>");
                addField(xmlBuffer,"id",Long.toString(eachAlgorithm.getID()));
                addField(xmlBuffer,"caption",eachAlgorithm.getCaption());
                addField(xmlBuffer,"synopsis",eachAlgorithm.getSynopsis());
                addField(xmlBuffer,"reftext",
                        eachAlgorithm.getAlgorithmReference());
                addField(xmlBuffer,"page",
                        Integer.toString(eachAlgorithm.getAlgorithmOccursInPage()));
                addField(xmlBuffer,"paperid",doi);
                addField(xmlBuffer,"year",doc.getDatum(Document.YEAR_KEY));
                addField(xmlBuffer,"ncites",Long.toString(doc.getNcites()));
                xmlBuffer.append("</doc>");
            }
        }
        xmlBuffer.append("</add>");
        try { // Since the Algorithm data is generally smaller than
            // document data, we just do one commit and sends.
            sendPost(xmlBuffer.toString());
            sendCommit();
            sendOptimize();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        csxdao.updateAlgorithmIndexTime();

    } //- indexAll
    
    private void addField(StringBuffer buffer, String fieldName, String value) {
        buffer.append("<field name=\"");
        buffer.append(fieldName);
        buffer.append("\">");
        String newvalue = value;
        
        // Get rid of XML bad characters. Note: Don't call SafeText.cleanXML
        // since all the values received are already encoded. SafeText could
        // produce things like: &amp;amp; since the text already has &amp;

        /* 
                Added Pradeep Teregowda (26 May 2009), this should clean up
                some of the mess ?
        */
        try {
            byte[] utf8Bytes = value.getBytes("UTF-8");
            newvalue = new String(utf8Bytes,"UTF-8");
        }
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /*
                Ends addition (new value continues)
        */
        buffer.append(SafeText.stripBadChars(newvalue));
        buffer.append("</field>");
    } //- addField
    
    private void sendOptimize() throws IOException {
        sendPost("<optimize/>");
    } //- sendOptimize
    
    private void sendPost(String str) throws IOException {

        HttpURLConnection conn =
            (HttpURLConnection)solrAlgorithmUpdateUrl.openConnection();
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) { /* unlikely... */ }
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

        Writer wr = new OutputStreamWriter(conn.getOutputStream());

        try {
            pipe(new StringReader(str), wr);
        } catch (IOException e) {
            throw(e);
        } finally {
            try { wr.close(); } catch (Exception e) { }
        }

        Reader reader = new InputStreamReader(conn.getInputStream());
        try {
            StringWriter output = new StringWriter();
            pipe(reader,output);
            checkExpectedResponse(output.toString());
        } catch (IOException e) {
            throw(e);
        } finally {
            try { reader.close(); } catch (Exception e) { }
        }

    } //- sendPost
    
    private static void pipe(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[1024];
        int read = 0;
        while ((read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
        }
        writer.flush();

    }  //- pipe
    
    private static void checkExpectedResponse(String response)
    throws IOException {
        if (response.indexOf(expectedResponse) < 0) {
            throw new IOException("Unexpected response from solr: "+response);
        }
    } //- checkExpectedResponse
    
    private void sendCommit() throws IOException {
        sendPost("<commit waitFlush=\"false\" waitSearcher=\"false\"/>");
    } //- sendCommit
} //- class AlgorithmIndexUpdater
