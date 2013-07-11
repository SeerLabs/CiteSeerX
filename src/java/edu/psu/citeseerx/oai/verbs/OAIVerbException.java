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

import java.util.List;

/**
 * OAI exceptions
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class OAIVerbException extends Exception {
	
	private static final long serialVersionUID = 4583304250207863410L;
	List<OAIError> errors;
	
	public OAIVerbException(List<OAIError> errors) {
		this.errors = errors;
	} //- OAIVerbException

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		StringBuffer message = new StringBuffer();
		for (OAIError err : errors) {
			message.append(err.getErrorCode());
			message.append(": ");
			message.append(err.getMessage());
			message.append('\n');
		}
		return message.toString();
	} //- getMessage

	/* (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return getMessage();
	} //- toString

	/**
	 * @return the list of errors within the exception
	 */
	public List<OAIError> getErrors() {
		return errors;
	} //- getErrors
	
} //- OAIVerbException
