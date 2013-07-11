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
package edu.psu.citeseerx.myciteseer.web.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.citeseerx.myciteseer.domain.Account;

/**
 * Utility class to query Solr instances and build the result list objects for
 * MyCiteSeerX searches.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev$$ $$Date$$
 */
public class SolrSelectUtils {

	/**
	 * Executes a query to a Solr instance. The result of the query is expected to be
	 * a JSON string.
	 * @param urlstr Url with the query to be executed.
	 * @return A JSON object with the results of the query.
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws JSONException
	 * @throws SolrException
	 */
    public static JSONObject doJSONQuery(String urlstr) throws IOException,
    MalformedURLException, JSONException, SolrException {
        
        URL url = new URL(urlstr);
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection)url.openConnection();
            BufferedReader in =
                new BufferedReader(new InputStreamReader(con.getInputStream()));
        
            StringBuffer buffer = new StringBuffer();
            String str;
            while((str = in.readLine()) != null) {
                buffer.append(str+"\n");
            }
            in.close();

            JSONObject response = new JSONObject(buffer.toString());
            return response;
            
        } catch (IOException e) {
            if (con != null) {
                try {
                    int statusCode = con.getResponseCode();
                    if (statusCode >= 400) {
                    	throw 
                    	(new SolrSelectUtils()).new SolrException(statusCode);
                    }
                } catch (IOException exc) { }
            }
            throw(e);
        }
                
    }  //- doJSONQuery
    
    /**
     * Change the highlight delimiters into an HTML tag
     * @param str String with highlighted terms
     * @return The same string content but the highligthed terms enclosed by
     * the <code>&lt;em&gt;</code> tag
     */
    public static String normHighlight(String str) {
        Pattern tag = Pattern.compile("=-=(.*?)-=-");
        Matcher solrmatch = tag.matcher(str);
        return solrmatch.replaceAll("<em>$1</em>");
    } //- normHighlight
    
    /**
     * Translates the JSONObject accounts information into a MyCiteSeerX list
     * of Account domains objects.
     * @param json Accounts information in JSON format
     * @return List of MyCiteSeerX accounts
     * @throws JSONException
     */
    public static List<Account> buildHitAccountListJSON(JSONObject json) 
	throws JSONException {
		
		List<Account> hits = new ArrayList<Account>();
		JSONObject response     = json.getJSONObject("response");
        JSONArray peopleList    = response.optJSONArray("docs");
        JSONObject highlighting = json.optJSONObject("highlighting");
        
        if (peopleList == null) {
            return hits;
        } 
        for (int i = 0; i < peopleList.length(); ++i) {
        	JSONObject doc = peopleList.getJSONObject(i);
        	Account hit = new Account();
        	hit.setInternalId(doc.optLong("id"));
        	hit.setUsername(doc.optString("userid"));
        	hit.setFirstName(doc.optString("firstName"));
        	hit.setLastName(doc.optString("lastName"));
        	hit.setEmail(doc.optString("email"));
    		hit.setMiddleName(doc.optString("middleName"));
    		hit.setAffiliation1(doc.optString("affil1"));
    		hit.setAffiliation2(doc.optString("affil2"));
    		hit.setCountry(doc.optString("country"));
    		hit.setProvince(doc.optString("province"));
    		hit.setWebPage(doc.optString("webpage"));
        	
        	// If there is something highlighted we add it here
        	if (highlighting != null) {
        		JSONObject highlights = 
        			highlighting.getJSONObject(hit.getInternalId().toString());
        		if (highlights != null) {
        			JSONArray firstNames = 
        				highlights.optJSONArray("firstName");
        			JSONArray middleNames = 
        				highlights.optJSONArray("middleName");
        			JSONArray lastNames = highlights.optJSONArray("lastName");
        			JSONArray affil1s = highlights.optJSONArray("affil1");
        			JSONArray affil2s = highlights.optJSONArray("affil2");
        			JSONArray countrys = highlights.optJSONArray("country");
        			JSONArray provinces = highlights.optJSONArray("province");
        			JSONArray webpages = highlights.optJSONArray("webpage");
        			if(firstNames != null && firstNames.length() > 0) {
                        hit.setFirstName(
                        		normHighlight(firstNames.getString(0)));
        			}
                    if(middleNames != null && middleNames.length() > 0) {
                        hit.setMiddleName(normHighlight(
                        		middleNames.getString(0)));
                    }
                    if(lastNames != null && lastNames.length() > 0) {
                        hit.setLastName(normHighlight(lastNames.getString(0)));
                    }
                    if(affil1s != null && affil1s.length() > 0) {
                        hit.setAffiliation1(normHighlight(
                        		affil1s.getString(0)));
                    }
                    if(affil2s != null && affil2s.length() > 0) {
                        hit.setAffiliation2(normHighlight(
                        		affil2s.getString(0)));
                    }
                    if(countrys != null && countrys.length() > 0) {
                        hit.setCountry(normHighlight(countrys.getString(0)));
                    }
                    if(provinces != null && provinces.length() > 0) {
                        hit.setProvince(normHighlight(provinces.getString(0)));
                    }
                    if(webpages != null && webpages.length() > 0) {
                        hit.setWebPage(normHighlight(webpages.getString(0)));
                    }
        		}
        	}
        	hits.add(hit);
        }
        return hits;
	} //- buildHitAccountListJSON
    
    public class SolrException extends RuntimeException {
        /**
		 * 
		 */
		private static final long serialVersionUID = -7969893105452834750L;
		int statusCode;
        public SolrException(int statusCode) {
            this.statusCode = statusCode;
        } //- SolrException
        public int getStatusCode() {
            return statusCode;
        } //- getStatusCode
    } //- class SolrException
    
}  //- class SolrSelectUtils
