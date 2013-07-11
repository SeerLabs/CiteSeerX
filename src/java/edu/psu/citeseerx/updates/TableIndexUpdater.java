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
import java.util.List;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.Table;
import edu.psu.citeseerx.utility.SafeText;

public class TableIndexUpdater {
	private URL solrTableUpdateUrl;
	private static final String expectedResponse =
        "<int name=\"status\">0</int>";
    
    public void setSolrURL(String solrUpdateUrl) throws MalformedURLException {
        this.solrTableUpdateUrl = new URL(solrUpdateUrl);
    } //- setSolrURL
    
    
    private CSXDAO csxdao;
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    
    /*
     *  <field name="id" 
     *	<field name="caption" 
     *	<field name="footNote" 
     *	<field name="refText" 
     *	<field name="paperid" 
     *	<field name="year" 
     *	<field name="ncites" 
     *   
     */
    
    
    public void indexAll() {
    	
    	java.sql.Date lastUpdate = csxdao.lastTableIndexTime();
    	if(lastUpdate == null) {
    		lastUpdate = new java.sql.Date(0);
    	}
    	System.out.println("Last Update was "+lastUpdate.toString());
    	List<Table> tblList = csxdao.getUpdatedTables(lastUpdate);
    	if(tblList == null ) { 
    		System.out.println("No Tables to Update"); 
    		return; 
    	}
    	StringBuffer xmlBuffer = new StringBuffer();
    	xmlBuffer.append("<add>");
        for (Table eachTable: tblList) {
        	if(eachTable.getPaperIDForTable() == "-1") {
        		xmlBuffer.append("<delete><id>"+Long.toString(eachTable.getID())+"</id></delete>");
        		continue;
        	}
        	String doi = eachTable.getPaperIDForTable();
        	Document doc = csxdao.getDocumentFromDB(doi);
        	if(doc.isPublic()) {
        		xmlBuffer.append("<doc>");
        		addField(xmlBuffer,"id",Long.toString(eachTable.getID()));
        		addField(xmlBuffer,"caption",eachTable.getCaption());
        		addField(xmlBuffer,"footNote",eachTable.getFootNote());
        		addField(xmlBuffer,"refText",eachTable.getTableReference());
        		addField(xmlBuffer,"paperid",doi);
        		addField(xmlBuffer,"year",doc.getDatum(Document.YEAR_KEY));
        		addField(xmlBuffer,"ncites",Long.toString(doc.getNcites()));
        		xmlBuffer.append("</doc>");
        	}
        }
        xmlBuffer.append("</add>");
        try { // Since the table data is generally smaller than
        	// document data, we just do one commit and sends.
        	sendPost(xmlBuffer.toString());
        	sendCommit();
        	sendOptimize();
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        csxdao.updateTableIndexTime();
    }
    
    
    private void addField(StringBuffer buffer, String fieldName, String value) {
    	if(value == null) {
    		return;
    	}
        buffer.append("<field name=\"");
        buffer.append(fieldName);
        buffer.append("\">");
       	String newvalue = value; 
        // Get rid of XML bad characters. Note: Don't call SafeText.cleanXML
        // since all the values received are already encoded. SafeText could
        // produce things like: &amp;amp; since the text already has &amp;
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
    } //- addElement
    
    
    private void sendOptimize() throws IOException {
        sendPost("<optimize/>");
    }
    
    private void sendPost(String str) throws IOException {

        HttpURLConnection conn =
            (HttpURLConnection)solrTableUpdateUrl.openConnection();
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
        
    }
    
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
    }
    
    /*private void processDeletions(List list) throws IOException {
        if (list.isEmpty()) {
            return;
        }
        for (Object o : list) {
            String del = "<delete><id>";
            del += (Long)o;
            del += "</id></delete>";
            sendPost(del);  // Have to send multiple posts due to Solr bug.
        }
        sendCommit();
        
    } */
    
    private void sendCommit() throws IOException {
        sendPost("<commit waitFlush=\"false\" waitSearcher=\"false\"/>");
    }
    
}