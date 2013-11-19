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

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.oai.OAIUtils;


/**
 * Handles the GetRecord OAI-PMH verb returning metadata for an individual 
 * record within the repository 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class GetRecord extends AbstractList {

	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {
		"verb:true", "identifier:true", "metadataPrefix:true"
	};
	
	public GetRecord() {
		super();
		for (int i =0; i < expectedArguments.length; ++i) {
			String[] values = expectedArguments[i].split(":");
			addArgument(values[0], Boolean.parseBoolean(values[1]));
		}
	} //- GetRecord

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractList#doProcess(javax.servlet.http.HttpServletRequest, org.jdom.Element)
	 */
	@Override
	protected Element doProcess(HttpServletRequest request, Element root)
			throws OAIVerbException {
		String identifier = request.getParameter("identifier");
		String metadataPrefix = request.getParameter("metadataPrefix");
		
		Document doc = null;
		if (isValidIdentifier(identifier) && 
				isValidMetaDataPrefix(metadataPrefix)) {
			String[] idParts = identifier.split(getDelimiter());
			doc = csxdao.getDocumentFromDB(idParts[2], false, false, false, 
					false, true, false);
			if (doc == null) {
				/* 
				 * The document doesn't exist in the repository or it's a bad
				 * DOI
				 */
				addError(new OAIError(identifier + " has a valid CiteSeerX " +
						"identifier, but it maps to no known item", 
						OAIError.ID_DOES_NOT_EXISTS_ERROR));
			}else{
    			if (!doc.isPublic()) {
    			    // The document isn't public anymore.
    			    addError(new OAIError(identifier + " has a valid " +
    			    		"CiteSeerX identifier, but it maps to no known " +
    			    		"item", OAIError.ID_DOES_NOT_EXISTS_ERROR));
    			}
			}
		}
		
		if (!hasErrors()) {
		    int metadataFormat = 0;
		    if (METADATA_OAI_DC.equals(metadataPrefix)) {
		        metadataFormat = OAIUtils.OAI_DC_METADATAFORMAT;
		    }
		    Element getRecord = OAIUtils.addGetRecord(root);
		    buildDocumentRecord(doc, getRecord, metadataFormat);
		}else{
		    root = null;
			throw new OAIVerbException(getErrors());
		}
		return root;
	} //- doProcess
	
} //- class GetRecord
