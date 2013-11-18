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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Date;

/* Added */
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/* End Additions */


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.citeseerx.domain.Algorithm;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.Table;
import edu.psu.citeseerx.utility.SafeText;

/**
 * Utility class to send queries to Solr and obtain the data using
 * CiteSeerX domain objects 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class SolrSelectUtils {
    
    /**
     * Send a query to solr and obtain the results as a JSONObject
     * @param urlstr
     * @return The results of the query in a JSONObject
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
	    BufferedReader in = null;
	    if(con != null) {
            	in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    }
	    else {
			System.err.println("[EE] doJSONQuery Connection error");
	    }
        
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
			System.err.println("JSON Query exception: Connection error");
                        throw new SolrException(statusCode);
                    }
                } catch (IOException exc) { }
            }
            throw(e);
        }
                
    }  //- doJSONQuery
    
    /**
     * Transforms results of a query in JSON format to a List of CiteSeerX 
     * objects representing a document or citation
     * @param json
     * @return A list of ThinDoc representing the same objects in JSON format
     * @throws JSONException
     */
    public static List<ThinDoc> buildHitListJSON(JSONObject json)
    throws JSONException {
        return buildHitListJSON(json, null);
    } //- buildHitListJSON
    
    /**
     * Transforms results of a query in JSON format to a List of CiteSeerX 
     * objects representing a document or citation
     * @param json
     * @param clusterid
     * @return A list of ThinDoc representing the same objects in JSON format
     * @throws JSONException
     */
    public static List<ThinDoc> buildHitListJSON(JSONObject json,
            Long clusterid) throws JSONException {
        
        ArrayList<ThinDoc> hits = new ArrayList<ThinDoc>();
        
        JSONObject response     = json.getJSONObject("response");
        JSONArray docList       = response.optJSONArray("docs");
        JSONObject highlighting = json.optJSONObject("highlighting");
        
        if (docList == null) {
            return hits;
        }
        
        for (int i=0; i<docList.length(); i++) {
            JSONObject doc = docList.getJSONObject(i);

            ThinDoc hit = new ThinDoc();
            hit.setCluster(Long.parseLong(doc.getString("id")));
            if (hit.getCluster().equals(clusterid)) {
                continue;
            }
            hit.setDoi(doc.optString("doi"));
            hit.setInCollection(doc.getBoolean("incol"));

            String title = doc.optString("title");
            String abs = doc.optString("abstract");
            String url = doc.optString("url");
            String venue = doc.optString("venue");
            String venType = doc.optString("ventype");
            String pages = doc.optString("pages");
            String publ = doc.optString("publisher");
            String tech = doc.optString("tech");
            Long vtime = doc.optLong("vtime");
            
            if (title == null || title.length()<2 || title.equals("null")) {
                hit.setTitle("unknown title");
            } else {
                hit.setTitle(title);
            }
            if (abs == null || abs.length() < 10 || abs.equals("null")) {
                hit.setAbstract("Abstract not found");
            } else {
                hit.setAbstract(abs);
            }
            if (url != null && !url.equals("null")) {
                hit.setUrl(url);
            }
            if (venue != null && !venue.equals("null")) {
                hit.setVenue(venue);
            }
            if (venType != null && !venType.equals("null")) {
                hit.setVentype(venType);
            }
            if (pages != null && !pages.equals("null")) {
                hit.setPages(pages);
            }
            if (publ != null && !publ.equals("null")) {
                hit.setPublisher(publ);
            }
            if (tech != null && !tech.equals("null")) {
                hit.setTech(tech);
            }
            if (vtime != null) {
                hit.setUpdateTime(new Date(vtime));
            }
            
            JSONArray authArray = doc.optJSONArray("author");
            if (authArray != null && authArray.length() > 0) {
                StringBuffer authBuf = new StringBuffer();
                for (int a=0; a<authArray.length(); a++) {
                    authBuf.append(authArray.getString(a));
                    if (a < authArray.length()-1) {
                        authBuf.append(", ");
                    }
                }
                hit.setAuthors(authBuf.toString());
            } else {
                hit.setAuthors("unknown authors");
            }
            
            try { 
                hit.setNcites(doc.optInt("ncites",0));
            } catch (Exception e) { }
            try {
                hit.setSelfCites(doc.optInt("scites",0));
            } catch (Exception e) { }
            try {
                hit.setYear(doc.optInt("year"));
            } catch (Exception e) { }
            try {
                hit.setVol(doc.optInt("vol"));
            } catch (Exception e) { }
            try {
                hit.setNum(doc.optInt("num"));
            } catch (Exception e) { }
            if (abs != null) {
                if (abs.length() > 300) {
                    hit.setSnippet(hit.getAbstract().substring(0,300));
                } else {
                    hit.setSnippet(abs);
                }
            }

            if (highlighting != null) {
                JSONObject highlights =
                    highlighting.optJSONObject(hit.getCluster().toString());
                if (highlights != null) {
                    JSONArray titles = highlights.optJSONArray("title");
                    JSONArray abstracts = highlights.optJSONArray("abstract");
                    JSONArray snippets = highlights.optJSONArray("text");
                    if (titles != null && titles.length() > 0) {
                        hit.setTitle(normHighlight(titles.getString(0)));
                    }
                    if (abstracts != null && abstracts.length() > 0) {
                        hit.setAbstract(normHighlight(abstracts.getString(0)));
                    }
                    if (snippets != null && snippets.length() > 0) {
                        String snippet =
                            SafeText.hideEmail(snippets.getString(0));
                        snippet = normHighlight(snippet);
                        hit.setSnippet(snippet);
                    }
                }
            }
            
            hits.add(hit);
            
        }

        return hits;
        
    }  //- buildHitList
   

    /**
     * Transforms results of a query in JSON format to a List of CiteSeerX 
     * objects representing a table
     * @param json
     * @return A list of ThinTable representing the same objects in JSON format
     * @throws JSONException
     */
    public static List<Table> buildTableHitListJSON(JSONObject json)
    	throws JSONException {
        
        ArrayList<Table> hits = new ArrayList<Table>();
        
        JSONObject response     = json.getJSONObject("response");
        JSONArray docList       = response.optJSONArray("docs");
                
        if (docList == null) {
            return hits;
        }
        
        for (int i=0; i<docList.length(); i++) {
            JSONObject doc = docList.getJSONObject(i);

            Table hit = new Table();
            int id = doc.optInt("id");
            hit.setID(id);
            hit.setCaption(doc.optString("caption"));
            hit.setFootNote(doc.optString("footNote"));
            hit.setTableReference(doc.optString("refText"));
            hit.setPaperIDForTable(doc.optString("paperid"));
            hit.setYear(doc.optInt("year"));
            hit.setCitedBy(doc.optInt("ncites"));
            hits.add(hit);
            
        }

        return hits;
        
    }  //- buildTableHitListJSON
   
    
    /* Deleted
    public static String normHighlight(String str) {
        str = str.replace('\n', ' ');
        str = str.replace("-=-", "</em>");
        str = str.replace("=-=", "<em>");
        return str;
    } //- normHighlight
    */

    /**
     *
     * @param str
     * @return The highlighted terms in HTML format
     */

    /* Added */

    public static String normHighlight(String str) {
        Pattern tag = Pattern.compile("=-=(.*?)-=-");
        Matcher solrmatch = tag.matcher(str);
        return solrmatch.replaceAll("<em>$1</em>");
    }

    /* End addition */

    
    /**
     * Formats citations for users view 
     * @param cite
     */
    public static void prepCitation(ThinDoc cite) {
        if (cite.getTitle() == null || cite.getTitle().length() < 2) {
            cite.setTitle("unknown title");
        }
        if (cite.getAuthors() == null || cite.getAuthors().length() < 2) {
            cite.setAuthors("unknown authors");
        } else {
            StringTokenizer st = new StringTokenizer(cite.getAuthors(), ",");
            ArrayList<String> names = new ArrayList<String>();
            while(st.hasMoreTokens()) {
                names.add(st.nextToken());
            }
            StringBuffer buffer = new StringBuffer();
            for (int i=0; i<names.size(); i++) {
                String name = names.get(i);
                int lastSpace = name.lastIndexOf(' ');
                buffer.append(name.substring(lastSpace+1));
                if (i < names.size()-1) {
                    buffer.append(", ");
                }
                if (i == 1 && names.size() > 2) {
                    buffer.append("et al.");
                    break;
                }
            }
            cite.setAuthors(buffer.toString());
        }
    }
    
    /**
     * Transforms results of a query in JSON format to a List of CiteSeerX 
     * objects representing a Algorithm
     * @param json
     * @return A list of ThinAlgorithm representing the same objects in JSON format
     * @throws JSONException
     */
    public static List<Algorithm> buildAlgorithmHitListJSON(JSONObject json)
        throws JSONException {
        
        ArrayList<Algorithm> hits = new ArrayList<Algorithm>();
        
        JSONObject response     = json.getJSONObject("response");
        JSONArray docList       = response.optJSONArray("docs");
                
        if (docList == null) {
            return hits;
        }

        for (int i=0; i<docList.length(); i++) {
            JSONObject doc = docList.getJSONObject(i);

            Algorithm hit = new Algorithm();
            long id = doc.optLong("id");
            hit.setID(id);
            hit.setCaption(doc.optString("caption"));
            hit.setAlgorithmReference(doc.optString("reftext"));
            hit.setSynopsis(doc.optString("synopsis"));
            hit.setPaperIDForAlgorithm(doc.optString("paperid"));    
            hit.setNcites(doc.optInt("ncites"));
            
            hits.add(hit);
        }
        return hits;

    }  //- buildAlgorithmHitListJSON
    
}  //- class SolrSelectUtils


class SolrException extends RuntimeException {
    
    private static final long serialVersionUID = 4519412134119901367L;
    
    int statusCode;
    public SolrException(int statusCode) {
        this.statusCode = statusCode;
    } //- SolrException
    public int getStatusCode() {
        return statusCode;
    } //- getStatusCode
} //- SolrException
