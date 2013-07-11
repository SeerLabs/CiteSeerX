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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import edu.psu.citeseerx.oai.OAIUtils;


/**
 * Handles the Identify OAI-PMH verb returning information about the repository 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class Identify extends AbstractVerb {

	private final static String PROTOCOL_VERSION = "2.0";
	
	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {"verb:true"};
	
	private String repositoryName;
	
	/**
	 * Sets the OAI-PMH repository name.
	 * @param repositoryName	Repository name
	 */
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	} //- setRepositoryName
	
	private String deletedRecord;
	
	/**
	 * Sets the support of deleted records within the repository
	 * @param deletedRecord
	 */
	public void setDeletedRecord(String deletedRecord) {
		this.deletedRecord = deletedRecord;
	} //- setDeletedRecord
	
	private List<String> adminEmail = new ArrayList<String>();
	
	/**
	 * Sets the e-mail for the repository manager
	 * @param adminEmail
	 */
	public void setAdminEmail(String adminEmail) {
	    String[] mails = adminEmail.split(",");
	    for (int i = 0; i < mails.length; ++i) {
	        this.adminEmail.add(mails[i]);
	    }
	} //- setAdminEmail
	
	private String compressionFormats;
	
	/**
	 * Sets the compression formats supported by the repository.
	 * I could happen that the software support some compression format but
	 * it's not listed here because performance issues.
	 * @param compressionFormats
	 */
	public void setCompressionFormats(String compressionFormats) {
		this.compressionFormats = compressionFormats;
	} //- setCompressionFormats
	
	private String eprintsContent;
	
	/**
	 * Sets the ePrints content
	 * @param eprintsContent
	 */
	public void setEprintsContent(String eprintsContent) {
		this.eprintsContent = eprintsContent;
	} //- setEprintsContent

	private String eprintsMetadataPolicy;
	
	/**
	 * Sets the ePrints metadata policy
	 * @param eprintsMetadataPolicy
	 */
	public void setEprintsMetadataPolicy(String eprintsMetadataPolicy) {
		this.eprintsMetadataPolicy = eprintsMetadataPolicy;
	} //- setEprintsMetadaPolicy
	
	private String eprintsDataPolicy;
	
	/**
	 * Sets the ePrints data policy
	 * @param eprintsDataPolicy
	 */
	public void setEprintsDataPolicy(String eprintsDataPolicy) {
		this.eprintsDataPolicy = eprintsDataPolicy;
	} //- setEprintsMetadaPolicy
	
	public Identify() {
		super();
		for (int i =0; i < expectedArguments.length; ++i) {
			String[] values = expectedArguments[i].split(":");
			addArgument(values[0], Boolean.parseBoolean(values[1]));
		}
	} //- Identify

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest, org.jdom.Element)
	 */
	@Override
	protected Element doProcess(HttpServletRequest request, Element root)
    throws OAIVerbException {
	    String[] cFormats = compressionFormats.split(",");
	    String sampleID=  OAI_SCHEMA + getDelimiter() + 
	        getRepositoryIdentifier() + getDelimiter() + DOI_SAMPLE;
	    OAIUtils.addIdentify(root, repositoryName, getBaseURL(), 
	            PROTOCOL_VERSION, adminEmail, getEarliestDatestamp(), 
	            deletedRecord, getGranularity(), cFormats, OAI_SCHEMA, 
	            getRepositoryIdentifier(), getDelimiter(), sampleID, 
	            eprintsContent, eprintsMetadataPolicy, eprintsDataPolicy);
		return root;
	} //- doProcess
} //- class Identify
