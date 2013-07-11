package edu.psu.citeseerx.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.citeseerx.domain.UniqueAuthor;

public class AuthorSolrSelectUtils {

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
                        throw new SolrException(statusCode);
                    }
                } catch (IOException exc) { }
            }
            throw(e);
        }                
    }  //- doJSONQuery
    
    public static List<UniqueAuthor> buildHitListJSON(JSONObject json) 
    throws JSONException {
        ArrayList<UniqueAuthor> hits = new ArrayList<UniqueAuthor>();

    	JSONObject response     = json.getJSONObject("response");
        JSONArray docList       = response.optJSONArray("docs");
        
        if (docList == null) {
            return hits;
        }
        
        for (int i=0; i<docList.length(); i++) {
            JSONObject doc = docList.getJSONObject(i);
	    
            UniqueAuthor hit = new UniqueAuthor();
	    
	    hit.setAid(doc.optString("id"));
            hit.setCanname(doc.optString("canname"));
            
	    JSONArray varnameArray = doc.optJSONArray("varname");
            if (varnameArray != null) {
		List<String> varnames = new ArrayList<String>();
                for (int j=0; j<varnameArray.length(); j++) {
                    varnames.add(varnameArray.getString(j));
                }
                hit.setVarnames(varnames);
            }
	    JSONArray emailArray = doc.optJSONArray("email");
	    if (emailArray != null && emailArray.length() > 0) {
		hit.setEmail(emailArray.getString(0));
	    }
	    JSONArray affilArray = doc.optJSONArray("affil");
	    if (affilArray != null && affilArray.length() > 0) {
		hit.setAffil(affilArray.getString(0));
	    }
	    JSONArray addrArray = doc.optJSONArray("address");
	    if (addrArray != null && addrArray.length() > 0) {
		hit.setAddress(addrArray.getString(0));
	    }
	    hit.setNDocs(doc.optInt("ndocs", 0));
            
            hits.add(hit);
        }
    	return hits;
    }
}
