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

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import edu.psu.citeseerx.domain.DOIInfo;
import edu.psu.citeseerx.oai.OAIUtils;
import edu.psu.citeseerx.utility.DateUtils;


/**
 * Handles the ListIdentifiers OAI-PMH verb returning headers instead of records 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ListIdentifiers extends AbstractList {

	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {
		"verb:true", "metadataPrefix:true", "from:false", "until:false", 
		"set:false", "resumptionToken:false"
	};
	
	public ListIdentifiers() {
		super();
		for (int i =0; i < expectedArguments.length; ++i) {
			String[] values = expectedArguments[i].split(":");
			addArgument(values[0], Boolean.parseBoolean(values[1]));
		}
	} //- ListIdentifiers

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractList#doProcess(javax.servlet.http.HttpServletRequest, org.jdom.Element)
	 */
	@Override
	protected Element doProcess(HttpServletRequest request, Element root)
    throws OAIVerbException {
		// Get verb arguments.
		String metadataPrefix = request.getParameter("metadataPrefix");
		String from = request.getParameter("from");
		String until = request.getParameter("until");
		String set = request.getParameter("set");
		String resumptionToken = request.getParameter("resumptionToken");
		
		if (set != null) {
			addError(new OAIError(getRepositoryIdentifier() + "does not " +
					"support sets", OAIError.NO_SET_HIERARCHY_ERROR));
		}
		
		if (from == null) {
			from = getEarliestDatestamp();
		}else{
			validateDate(from);
		}
		if (until == null) {
			until = DateUtils.formatDateISO8601UTC(new Date());
		}else{
			validateDate(until);
		}
		String[] resTokens = null;
		String iniDOI = null;
		Integer completeSetCount = null;
		Integer recordsSent = null;
		if (resumptionToken != null) {
			if (isValidResumptionToken(resumptionToken)) {
				resTokens = resumptionToken.split("-");
				iniDOI = resTokens[0];
				completeSetCount = Integer.parseInt(resTokens[1]);
				recordsSent = Integer.parseInt(resTokens[2]);
				metadataPrefix = resTokens[3];
			}
		}
		
		isValidMetaDataPrefix(metadataPrefix);
		if (!hasErrors()) {
			// So far so good! get the records.
			try { 
				iniDOI = (null == iniDOI) ? "0" : iniDOI;
				recordsSent = (null == recordsSent) ? new Integer(0) : 
					recordsSent;
				List<DOIInfo> ids = csxdao.getSetDOIs(
						DateUtils.parseDateToUTCDate(from), 
						DateUtils.parseDateToUTCDate(until), iniDOI, 
						getMaxReturnRecords());
				if (ids.size() == 0) {
					addError(new OAIError("No records were found", 
							OAIError.NO_RECORDS_MATCH_ERROR));
				}else{
				    Element identifiers = OAIUtils.addListIdentifiers(root); 
					for (DOIInfo doi : ids) {
					    OAIUtils.addRecordHeader(identifiers, 
					            buildIdentifier(doi.getDoi()), 
					            doi.getModifiedDate(), 
					            getGranularity());
					}

					// Build a resumption token if necessary
					if (resumptionToken == null) {
						// Get the complete set count only if it's the 
						// first call
						completeSetCount = csxdao.getSetDOICount(
								DateUtils.parseDateToUTCDate(from), 
								DateUtils.parseDateToUTCDate(until), 
								iniDOI);
					}
					recordsSent = new Integer(
							ids.size()+recordsSent.intValue());

					String newResumptionToken = "";
					if (recordsSent < completeSetCount) {
					    newResumptionToken = generateResumptionToken(
                                ids.get(ids.size()-1).getDoi(), 
                                completeSetCount, recordsSent, 
                                metadataPrefix);
					}else if ((recordsSent.intValue() == completeSetCount.intValue()) && 
							(resumptionToken != null)) {
					    newResumptionToken = "";
					}
					OAIUtils.addResumptionToken(identifiers, newResumptionToken);
				}
			}catch (ParseException e) {
				// This shouldn't happen since date were validated.
				addError(new OAIError("Invalid dates were found", 
						OAIError.BAD_ARGUMENT_ERROR));
			}
		}
		
		/*
		 * This is not an else since errors could happen while creating 
		 * the response
		 */
		if (hasErrors()) {
			throw new OAIVerbException(getErrors());
		}
		return root;
	} //- doProcess
	
} //- class ListIdentifiers