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

/**
 * Store information about an OAI error 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class OAIError {
	
	public static final String BAD_VERB_ERROR 				= "badVerb";
	public static final String BAD_ARGUMENT_ERROR 			= "badArgument";
	public static final String ID_DOES_NOT_EXISTS_ERROR 	= "idDoesNotExist";
	public static final String NO_RECORDS_MATCH_ERROR 		= "noRecordsMatch";
	public static final String NO_SET_HIERARCHY_ERROR 		= "noSetHierarchy";
	public static final String BAD_RESUMPTION_TOKEN_ERROR 	= 
		"badResumptionToken";
	public static final String CANNOT_DISEMINATE_FORMAT_ERROR = 
		"cannotDisseminateFormat";
	public static final String NO_METADATA_FORMATS_ERROR = "noMetadataFormats";
	
	private String message;
	private String errorCode;
	
	public OAIError(String message, String errorCode) {
		super();
		this.message = message;
		this.errorCode = errorCode;
	} //- OAIError

	/**
	 * Gets the error message
	 * @return the message encapsulated in this object
	 */
	public String getMessage() {
		return message;
	} //- getMessage

	/**
	 * Sets the error message
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	} //- setMessage

	/**
	 * Returns the error code
	 * @return the statusCode
	 */
	public String getErrorCode() {
		return errorCode;
	} //- getErrorCode

	/**
	 * @param errorCode the error code for this Error
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	} //- getErrorCode

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return errorCode + ": " + message;
	} //- toString

} //- Class OAIError
