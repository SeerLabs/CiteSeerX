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
package edu.psu.citeseerx.myciteseer.updates;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;

/**
 * Utilities for updating a solr index to be consistent to the myciteseerx
 * users table. This class reads in user records within the myciteseerx users
 * table, creates records in solr XML update format, and send the XML to a
 * solr server.
 * <br><br>
 * The UserIndexUpdateManager maintains a timestamp of the last update within 
 * the myciteseerx database so that only records modified since the last
 * update will be processed.
 * <strong>Notes:</strong>
 * to build this functionality an extension has to be
 * done over the users table to store a timestamp with the last modification
 * date.
 * <br><br>
 * There are two classes of deletes that are handle by UserIndexUpdateManager:
 * <ul>
 * <li>Deleted users: Reads records marked for deletion within the myciteseerx
 * user deletions table.</li>
 * <li>Disabled users: User who are being disabled by the administrator would be
 * deleted from the index.</li>
 * </ul>
 * 
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class UserIndexUpdateManager {
	
	private URL solrUpdateUrl;

	/**
	 * Sets the update URL to the solr server
	 * @param solrUpdateUrl Uptade URL to a solr server
	 */
	public void setSolrUpdateUrl(URL solrUpdateUrl) {
		this.solrUpdateUrl = solrUpdateUrl;
	} //- setSolrUpdateUrl
	
	private MyCiteSeerFacade myciteseer;

	/**
	 * Sets the reference to myciteseer bean which is used to perform 
	 * operations within the personal portal.
	 * @param myciteseer
	 */
	public void setMyciteseer(MyCiteSeerFacade myciteseer) {
		this.myciteseer = myciteseer;
	} //- setMyciteseer

	/*
	 * Stores how many users has been indexed in actual run.
	 */
	int indexed = 0;
	
	/**
	 * Updates the index with records stored in the myciteseerx users table.
	 * This function re-index all the records and no index update time is
	 * recorded.
	 * Only valid and enable users are indexed.
	 * @throws IOException
	 */
	public void indexAll() throws IOException {
		boolean finished = false;
		Long lastID = new Long(0);
		indexed = 0;
		
		do {
			List<Account> users = myciteseer.getUsers(lastID, 1000);
			finished = users.isEmpty();
			if (!finished) {
				indexUsers(users);
				lastID = users.get(users.size()-1).getInternalId();
			}
		}while (!finished);
		
		sendOptimize();
		
	} //- indexAll
	
	/**
	 * Updates the index with records stored in the myciteseerx' users table.
	 * This function updates records updated since the last index update time.
	 * Only valid and enable users are indexed.
	 * @throws IOException
	 */
	public void indexSinceLastUpdate() throws IOException {
		boolean finished = false;
		Long lastID = new Long(0);
		Date lastUpdate = myciteseer.getUserLastIndexTime();
        Date currentTime = new Date(System.currentTimeMillis());
		indexed = 0;
		
		do {
			List<Account> users = myciteseer.getUsersSinceTime(lastUpdate, 
					lastID, 1000);
			finished = users.isEmpty();
			if (!finished) {
				indexUsers(users);
				lastID = users.get(users.size()-1).getInternalId();
			}
		}while (!finished);
		
		/*
		 * TO DO: 
		 *  1. Process deletions
		 *  2. Remove deletions
		 */ 
		
		/* 
		 * Disabled users needs to be deleted from the index.
		 * When delete user functionality is implemented. Deleted users
		 * internal ids needs to be added to this list.
		 */
		List<Long> toDelete = myciteseer.getDisabled(currentTime);
		processDeletions(toDelete);
		myciteseer.setUsersLastIndexTime(currentTime);
		sendOptimize();
	} //- indexSinceLastUpdate
	

	/*
	 * Creates Solr update records for all provided users, and send them to Solr
	 * Server.
	 * @param toIndex
	 * @throws IOException
	 */
	private void indexUsers(List<Account> toIndex) throws IOException {
		
		StringBuffer xmlBuffer = new StringBuffer();
        xmlBuffer.append("<add>");
        int nBatch = 0;
        
        for(Account user : toIndex) {
        	if (user.isEnabled()) {
        		buildDocEntry(user, xmlBuffer);
        	}
        	if (++nBatch>=200) {
                xmlBuffer.append("</add>");
                sendPost(xmlBuffer.toString());
                xmlBuffer = new StringBuffer();
                xmlBuffer.append("<add>");
                nBatch = 0;
            }
            indexed++;
        }
        if (nBatch>0) {
            xmlBuffer.append("</add>");
            sendPost(xmlBuffer.toString());
        }
        
        sendCommit();
        System.out.println("commit "+indexed);
		
	} //- indexUsers
	
	/*
	 * Builds a record in Solr update syntax corresponding to the
     * supplied parameters.
	 * @param user
	 * @param buffer
	 */
	private void buildDocEntry(Account user, StringBuffer buffer) {
		buffer.append("<doc>");
		buffer.append("<field name=\"id\">");
        buffer.append(user.getInternalId());
        buffer.append("</field>");
		buffer.append("<field name=\"userid\">");
        buffer.append(user.getUsername());
        buffer.append("</field>");
        
        String fullName = "";
        fullName = user.getFirstName();
        buffer.append("<field name=\"firstName\">");
        buffer.append(user.getFirstName());
        buffer.append("</field>");
        
        String middleName = user.getMiddleName();
        if (middleName != null && middleName.length() > 0) {
        	buffer.append("<field name=\"middleName\">");
            buffer.append(middleName);
            buffer.append("</field>");
            fullName += " " + middleName;
        }
        buffer.append("<field name=\"lastName\">");
        buffer.append(user.getLastName());
        buffer.append("</field>");
        fullName += " " + user.getLastName();
        
        buffer.append("<field name=\"fullName\">");
        buffer.append(fullName);
        buffer.append("</field>");
        
        String affil1 = user.getAffiliation1();
        if (affil1 != null && affil1.length() > 0) {
        	buffer.append("<field name=\"affil1\">");
            buffer.append(affil1);
            buffer.append("</field>");
        }
        
        String affil2 = user.getAffiliation2();
        if (affil2 != null && affil2.length() > 0) {
        	buffer.append("<field name=\"affil2\">");
            buffer.append(affil2);
            buffer.append("</field>");
        }
        
        String country = user.getCountry();
        if (country != null && country.length() > 0) {
        	buffer.append("<field name=\"country\">");
            buffer.append(country);
            buffer.append("</field>");
        }
        
        String province = user.getProvince();
        if (province != null && province.length() > 0) {
        	buffer.append("<field name=\"province\">");
            buffer.append(province);
            buffer.append("</field>");
        }
        
        String webPage = user.getWebPage();
        if (webPage != null && webPage.length() > 0) {
        	buffer.append("<field name=\"webpage\">");
            buffer.append(webPage);
            buffer.append("</field>");
        }
		buffer.append("</doc>");
	} //- buildDocEntry
	
	/*
	 * Sends commit command to Solr server
	 * @throws IOException
	 */
	private void sendCommit() throws IOException {
        sendPost("<commit waitFlush=\"false\" waitSearcher=\"false\"/>");
    } //- sendCommit
	
	/*
	 * Sends optimize command to Solr server
	 * @throws IOException
	 */
	private void sendOptimize() throws IOException {
        sendPost("<optimize/>");
    } //- sendOptimize
	
	/*
	 * Post a command to a Solr server
	 * @param str
	 * @throws IOException
	 */
	private void sendPost(String str) throws IOException {

        HttpURLConnection conn =
            (HttpURLConnection)solrUpdateUrl.openConnection();
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
        
    }  //- sendPost
	
	private static void pipe(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[1024];
        int read = 0;
        while ((read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
        }
        writer.flush();
        
    }  //- pipe
	
	private static final String expectedResponse =
        "<int name=\"status\">0</int>";
    
	/*
	 * Checks the Solr server response. 
	 * @param response
	 * @throws IOException
	 */
    private static void checkExpectedResponse(String response)
    throws IOException {
        if (response.indexOf(expectedResponse) < 0) {
            throw new IOException("Unexpected response from solr: "+response);
        }
    } //- checkExpectedResponse
    
    /*
     * deletes from the index users who has been deleted/disabled.
     * @param list
     * @throws IOException
     */
    private void processDeletions(List<Long> list) throws IOException {
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
        
    }  //- processDeletions
} //- UserIndexUpdateManager
