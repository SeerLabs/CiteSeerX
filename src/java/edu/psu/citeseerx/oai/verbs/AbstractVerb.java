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
package edu.psu.citeseerx.oai.verbs;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;

import edu.psu.citeseerx.oai.OAIUtils;

/**
 * Base class for all the OAI-PMH verbs implemented in the repository
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public abstract class AbstractVerb implements Verb {
	
	protected final static String OAI_SCHEMA = "oai";
	protected final static String DOI_SAMPLE = "10.1.1.1.1867";
	
	protected final static String METADATA_OAI_DC = "oai_dc";
	
	protected static final String[] metadataFormats = {METADATA_OAI_DC};
	
	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {};
	
	private String baseURL;
	
	/**
	 * @return the OAI-PMH base URL for the repository
	 */
	public String getBaseURL() {
		return baseURL;
	} //- getBaseURL

	/**
	 * @param baseURL OAI-PMH base URL for the repository
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	} //- setBaseURL

	private String granularity;
	
	/**
	 * Sets the granularity supported by the repository
	 * @param granularity
	 */
	public void setGranularity(String granularity) {
		this.granularity = granularity;
	} //- setGranularity
	
	/**
	 * @return the granularity supported by this repository
	 */
	public String getGranularity() {
		return granularity;
	} //- getGranularity
	
	private String delimiter;
	
	/**
	 * Sets the delimiter used in the identifier for this repository
	 * @param delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	} //- delimiter
	
	/**
	 * @return the delimiter used by this repository
	 */
	public String getDelimiter() {
		return delimiter;
	} //- getDelimiter
	
	private String repositoryIdentifier;
	
	/**
	 * Sets the PMH-OAI repository identifier
	 * @param repositoryIdentifier
	 */
	public void setRepositoryIdentifier(String repositoryIdentifier) {
		this.repositoryIdentifier = repositoryIdentifier;
	} //- setRepositoryIdentifier
	
	/**
	 * @return this repository identifier.
	 */
	public String getRepositoryIdentifier() {
		return repositoryIdentifier;
	} //- getRepositoryIdentifier
	
	private String earliestDatestamp;
	
	/**
	 * Sets the earliest date stamp for the repository
	 * @param earliestDatestamp
	 */
	public void setEarliestDatestamp(String earliestDatestamp) {
		this.earliestDatestamp = earliestDatestamp; 
	} //- setEarliestDatestamp
	
	/**
	 * @return the earliest date stamp for the repository
	 */
	public String getEarliestDatestamp() {
		return earliestDatestamp;
	} //- getEarliestDatestamp
	
	private List<OAIError> errors = new ArrayList<OAIError>();
	
	/**
	 * Add an error to the error list
	 * @param error
	 */
	protected void addError(OAIError error) {
		errors.add(error);
	} //- addError
	
	/**
	 * @return the error list. 
	 */
	protected List<OAIError> getErrors() {
		return errors;
	} //- getError
	
	/**
	 * Informs if errors have occurred 
	 * @return true if errors have occurred
	 */
	protected boolean hasErrors() {
		return errors.size() > 0;
	} //- hasErrors
	
	private List<String> requiredArguments = new ArrayList<String>();
	private List<String> validArguments = new ArrayList<String>();
	
	/**
	 * Stores the expected parameters for the verb indicating if the parameter is required or not
	 * @param parameter	Name of a valid parameter for this verb
	 * @param required	Indicates if the parameter is required or not
	 */
	public void addArgument(String parameter, boolean required) {
		if (required) {
			requiredArguments.add(parameter);
		}
		validArguments.add(parameter);
	} //- addArgument
	
	/**
	 * Validates the parameters send to the verb.
	 * @param request	Object containing the arguments for the verb
	 * @return True is parameters are OK false otherwise.
	 */
	protected boolean checkArguments(HttpServletRequest request) throws OAIVerbException {
		boolean valid = true;
		
		// check for resumptionToken exclusivity
		String resumptionToken = request.getParameter("resumptionToken");
		if (resumptionToken != null && resumptionToken.trim().length() > 0) {
			if (countParameters(request) > 2) {
				valid = false;
				addError(new OAIError("resumptionToken cannot be combined " +
						"with other parameters", OAIError.BAD_ARGUMENT_ERROR));
			}
		}else{
			// Check we got all the required arguments.
			Iterator<String> reqArgIter = requiredArguments.listIterator();
			while (reqArgIter.hasNext()) {
				String reqArg = reqArgIter.next();
				String argValue = request.getParameter(reqArg);
				if (argValue == null || argValue.trim().length() == 0) {
					valid = false;
				 	addError(new OAIError(reqArg + " is required",
	    			                OAIError.BAD_ARGUMENT_ERROR));
				}
			}
		}
		
		// Check illegal parameters and / or multi values
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (!validArguments.contains(paramName)) {
				valid = false;
				addError(new OAIError(paramName + " is an Illegal argument", 
						OAIError.BAD_ARGUMENT_ERROR));
			}
			
			// Check if the parameter has been set more than once.
			if (request.getParameterValues(paramName).length > 1) {
				valid = false;
				addError(new OAIError("multiple values are not allowed for " + 
						"the " + paramName + " argument", 
						OAIError.BAD_ARGUMENT_ERROR));
			}
		}

		if (false == valid) {
			throw new OAIVerbException(errors);
		}
		return valid;
	} //- areValidArguments
	
	/**
	 * @param request
	 * @return A map parameterName, parameterValue with the OAI parameters
	 * within the request.
	 */
	protected Map<String, String> getRequestElements(
	        HttpServletRequest request) {
	    
	    Map<String, String> elements = new HashMap<String, String>();
	    Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            if (validArguments.contains(paramName)) {
                String paramValue = request.getParameter(paramName);
                if (paramValue != null && paramValue.trim().length() > 0) {
                    elements.put(paramName, paramValue);
                }
            }
        }
	    return elements;
	} //- getRequestElements
	
	/**
	 * Creates the @see <a href="">OAI-PMH error</a> element with data provided 
	 * by the OAIException. The errors created are attached to the given root
	 * element.
	 * @param e Data to be used when creating the error element.
	 */
	protected void generateErrors(OAIVerbException e, Element root) {
	    for (OAIError err: e.getErrors()) {
	        Map<String, String> attributes = new HashMap<String, String>();
	        attributes.put("code", err.getErrorCode());
	        OAIUtils.addError(root, err.getMessage(), attributes);
	    }
	} //- generateErrors

	/**
	 * Generates the XML declaration, the root, responseDate and request element
	 * for the OAI-PMH response. This elements are attached to the given root 
	 * element
	 * @param request request object containing the arguments for the verb.
	 * @param includeAttributes Indicates if attributes needs to be included 
	 * when generating the request element within the response
	 */
	protected void generateHeader(HttpServletRequest request, 
            boolean includeAttributes, Element root) {
	    
	    Map<String, String> attributes = null;
	    if (includeAttributes) {
	        attributes = getRequestElements(request);
	    }
	    OAIUtils.addResponseDate(root, new Date(System.currentTimeMillis()));
	    OAIUtils.addRequest(root, getBaseURL(), attributes);
	} //- generateHeader
	
	/**
	 * Determines if the given identifier have the structure of this repository
	 * identifiers. If the identifier doesn't comply with the structure an 
	 * error is created and stored in the errors list. 
	 * @param identifier Identifier encoded in UTF-8
	 * @return True is the identifier has this repository identifiers structure
	 * false otherwise. 
	 */
	protected boolean isValidIdentifier(String identifier) {
		
		boolean valid = true;
		String doiRegExpr = 
			"^[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,4}$";
		
		// If the identifier argument is present. Check it's well formed.
		try {
			new URI(identifier);
			String[] tokens = identifier.split(getDelimiter());
			
			Pattern pattern = Pattern.compile(doiRegExpr);
			Matcher matcher = pattern.matcher(tokens[2]);
			if (!matcher.find() || (tokens[0].compareTo(OAI_SCHEMA) != 0) ||
					(tokens[1].compareTo(getRepositoryIdentifier()) != 0)) {
				valid = false;
				addError(new OAIError(identifier + " has an invalid structure " +
						"for this repository identifiers", 
						OAIError.ID_DOES_NOT_EXISTS_ERROR));
			}
		}catch (Exception e) {
			valid = false;
			addError(new OAIError(identifier + " is not a valid URI", 
					OAIError.ID_DOES_NOT_EXISTS_ERROR));
		}
		return valid;
	} //- isValidIdentifier
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.Verb#processRequest(javax.servlet.http.HttpServletRequest)
	 */
	public Document processRequest(HttpServletRequest request) {
		
		Element root = OAIUtils.createElementRoot();
		
		try {
			// Checking we have all the arguments we need.
			checkArguments(request);
			generateHeader(request, true, root);
			root = doProcess(request, root);
		}catch (OAIVerbException e) {

		    root = OAIUtils.createElementRoot();
		    /*
             * The request element must not include attributes when errors 
             * happens.
             */
		    generateHeader(request, false, root);
		    generateErrors(e, root);
		}finally{
    	    // Clean any errors occurred in this call.
    	    errors.clear();
		}
		return OAIUtils.createDocument(root);
	} //- processRequest
	
	/**
	 * Processes the request and produces the XML answer.
	 * This method is intended to be implemented by specialized classes which 
	 * know how to process each verb 
	 * @param request
	 * @return The XML, contained in a JDOM document, response or null if an 
	 * error occurs
	 * @throws OAIVerbException
	 */
	protected abstract Element doProcess(HttpServletRequest request, 
	        Element root) throws OAIVerbException;
	
	/*
	 * Counts how many parameters were sent
	 */
	private int countParameters(HttpServletRequest request) {
		
		int i = 0;
		Enumeration<String> params = request.getParameterNames();
		for (i = 0; params.hasMoreElements(); i++ ) {
			params.nextElement();
		}
		return i;
	} //- countArguments

} // class AbstractVerb
