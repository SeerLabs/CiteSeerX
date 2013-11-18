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
package edu.psu.citeseerx.web.oai;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.psu.citeseerx.oai.verbs.Verb;

import org.jdom.Document;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller used to handle OAI-PMH requests 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class OAIController implements Controller {

	private boolean serviceUnavailable;
	
	public void setServiceUnavailable(String serviceUnavailable) {
		this.serviceUnavailable = Boolean.parseBoolean(serviceUnavailable);
	} //- setServiceUnavailable
	
	/*
     * Informs if the PMH-OAI service is unavailable
     */
    private boolean isServiceUnavailable() {
    	return serviceUnavailable;
    } //- isServiceUnavailable
	
    private Verb getRecord;
    
    /**
     * Sets the object to handle OAI-PMH GetRecord requests.
     * @param getRecord
     */
    public void setGetRecord(Verb getRecord) {
    	this.getRecord = getRecord;
    } //- setGetRecord
    
    private Verb identify;
    
    /**
     * Sets the object to handle OAI-PMH Identify requests
     * @param identify
     */
    public void setIdentify(Verb identify) {
    	this.identify = identify;
    } //- setIdentify
    
    private Verb listIdentifiers;
    
    /**
     * Sets the object to handle OAI-PMH ListIdentifiers requests
     * @param listIdentifiers
     */
    public void setListIdentifiers(Verb listIdentifiers) {
    	this.listIdentifiers = listIdentifiers;
    } //- setListIdentifiers
    
    private Verb listMetadataFormats;
    
    /**
     * Sets the object to handle OAI-PMH ListMetadataFormats requests
     * @param listMetadataFormats
     */
    public void setListMetadataFormats(Verb listMetadataFormats) {
    	this.listMetadataFormats = listMetadataFormats;
    } //- ListMetadataFormats
    
    private Verb listRecords;
    
    /**
     * Sets the object to handle OAI-PMH ListRecords requests
     * @param listRecords
     */
    public void setListRecords(Verb listRecords) {
    	this.listRecords = listRecords;
    } //- setListRecords
    
    private Verb listSets;

    /**
     * Sets the object to handle OAI-PMH ListSets requests
     * @param listSets
     */
    public void setListSets(Verb listSets) {
    	this.listSets = listSets;
    } //- setListSets
    
    private Verb badVerb;
    
    /**
     * Set the object to handle badVerb error
     * @param badVerb
     */
    public void setBadVerb(Verb badVerb) {
    	this.badVerb = badVerb;
    } //- setBadVerb
    
    private int retryAfter;
    
    /*
     * Time, expressed as an integer, that a harvester should wait before 
     * trying to send a request again
     */
    public String getRetryAfter() {
    	return Integer.toString(retryAfter);
    } //- getRetryAfter
    
    /**
     * 
     * @param retryAfter Time, expressed as an integer, that a harvester should wait before 
     * trying to send a request again
     */
    public void setRetryAfter(String retryAfter) {
    	try {
    		this.retryAfter =  Integer.parseInt(retryAfter);
    	}catch (NumberFormatException e) {
			this.retryAfter = 0;
		}
    } //- setRetryAfter
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
    	if (isServiceUnavailable()) {
    		response.setHeader("Retry-After", getRetryAfter());
    		response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
    				"Sorry. This server is down for maintenance");
    	}

    	String verb = null;
		verb = ServletRequestUtils.getStringParameter(request, "verb", "");

		Document xmlResponse;

		if(verb.equals("GetRecord")) {
			xmlResponse = getRecord.processRequest(request);
		}
		else if(verb.equals("Identify")) {
			xmlResponse = identify.processRequest(request);
		}
		else if(verb.equals("ListIdentifiers")) {
			xmlResponse = listIdentifiers.processRequest(request);
		}
		else if(verb.equals("ListMetadataFormats")) {
			xmlResponse = listMetadataFormats.processRequest(request);
		}
		else if(verb.equals("ListRecords")) {
			xmlResponse = listRecords.processRequest(request);
		}
		else if(verb.equals("ListSets")) {
			xmlResponse = listSets.processRequest(request);
		}
		else {
			xmlResponse = badVerb.processRequest(request);
		}
		
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("xml", xmlResponse);
		return new ModelAndView("oai2", model);

    } //- handleRequest
    
}  //- Class OAIController

