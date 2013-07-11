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

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.oai.OAIUtils;


/**
 * Handles the ListMetaDataFormats OAI-PMH verb returning the metadata formats 
 * available in the repository 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ListMetadataFormats extends AbstractVerb {

	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {
		"verb:true", "identifier:false"
	};
	
	/*
	 * These three arrays have a one to one relationship
	 */
	protected static final String[] metadataFormats = {"oai_dc"};
	private static final String[] metadataNamespace =
		{"http://www.openarchives.org/OAI/2.0/oai_dc/"};
	private static final String[] metadataSchema =
		{"http://www.openarchives.org/OAI/2.0/oai_dc.xsd"};
	
	public ListMetadataFormats() {
		super();
		for (int i =0; i < expectedArguments.length; ++i) {
			String[] values = expectedArguments[i].split(":");
			addArgument(values[0], Boolean.parseBoolean(values[1]));
		}
	} //- ListMetadataFormats
	
	CSXDAO csxdao;

	/**
	 * Sets the object to have access to the document storage area
	 * @param csxdao
	 */
	public void setCSXDAO(CSXDAO csxdao) {
		this.csxdao = csxdao;
	} //- setCsxdao


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest, org.jdom.Element)
	 */
	@Override
	protected Element doProcess(HttpServletRequest request, Element root)
    throws OAIVerbException {
		
		String identifier = request.getParameter("identifier");
		
		if (identifier != null && isValidIdentifier(identifier)) {
			// split the identifier
			String[] idParts = identifier.split(getDelimiter());
			Document doc = csxdao.getDocumentFromDB(idParts[2]);
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
			        addError(new OAIError(identifier + " has a valid " +
			        		"CiteSeerX identifier, but it maps to no known " +
			        		"item", OAIError.ID_DOES_NOT_EXISTS_ERROR));
			    }
			}
		}
		
		/* 
		 * Metadata information is created on the fly for each
		 * document then if the repository provides a metadata 
		 * format, the format is provided for every document.
		 */
		if (!hasErrors()) {
			// Generate metadata formats supported by the repository
		    Element formats = OAIUtils.addListMetadataFormats(root);
			for (int i = 0; i < metadataFormats.length; i++) {
			    OAIUtils.addMetadataFormat(formats, metadataFormats[i], 
			            metadataNamespace[i], metadataSchema[i]);
			}
		}else{
			throw new OAIVerbException(getErrors());
		}
		return root;
	} //- doProcess
	
} //- class ListMetadataFormats
