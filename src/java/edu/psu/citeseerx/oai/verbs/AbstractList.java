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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.oai.OAIUtils;
import edu.psu.citeseerx.utility.DateUtils;

/**
 * Base class for all the Verbs which returns information about
 * 1 or more records in the repository
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public abstract class AbstractList extends AbstractVerb {

	protected final static int MAX_RECORDS = 1000;
	protected final static String RIGHTS = "Metadata may be used without " +
		"restrictions as long as the oai identifier remains attached to it.";
	
	private Integer maxReturnRecords = MAX_RECORDS;
	
	/**
	 * @return Maximum number of records to return when not returning
	 * the complete list
	 */
	public Integer getMaxReturnRecords() {
		return maxReturnRecords;
	} //- getMaxReturnRecords

	/**
	 * @param maxReturnRecords Maximum number of records to return when not 
	 * returning the complete list
	 */
	public void setMaxReturnRecords(Integer maxReturnRecords) {
		this.maxReturnRecords = maxReturnRecords;
	} //- setMaxReturnRecords
	
	private String contributor;
	
	/**
	 * @return The repository contributor
	 */
	public String getContributor() {
		return contributor;
	} //- getContributor

	/**
	 * @param contributor The repository contributor
	 */
	public void setContributor(String contributor) {
		this.contributor = contributor;
	} //- setContributor

	private  String viewDocURL;
	
	/**
	 * @return URL to view the document info within the library
	 */
	public String getViewDocURL() {
		return viewDocURL;
	} //- getViewDocUrl

	/**
	 * @param viewDocUrl URL to view the document info within the library
	 */
	public void setViewDocURL(String viewDocUrl) {
		this.viewDocURL = viewDocUrl;
	} //- getViewDocUrl

	protected CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
	
	private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
	

    /**
     * @param clusterID
     * @return All the DOIs of documents citing the document represented by 
     * clusterID.
     * <b>Note</b> This method only includes citing documents that are part of
     * the corpus. Citations that doesn't have the correspondent document aren't
     * included. 
     */
    private List<String>getCitingDocuments(Long clusterID) {
        int start = 0;
        int nRecords = 10;
        List<String>citing = new ArrayList<String>();
        List<ThinDoc> citations = null;
        if (clusterID != null) {
            citations = citedao.getCitingDocuments(clusterID, start, nRecords);
            boolean finished = citations.isEmpty();
            while (!finished) {
                for (ThinDoc cite : citations) {
                    if (cite.getInCollection()) {
                        // Obtain the DOIs associated to this cluster
                        List<String> dois = 
                            citedao.getPaperIDs(cite.getCluster());
                        Document citingDoc = null;
                        for (String doi : dois) {
                            citingDoc = csxdao.getDocumentFromDB(doi, 
                                    false, false);
                            if (citingDoc != null && citingDoc.isPublic()) {
                                citing.add(citingDoc.getDatum(Document.DOI_KEY));
                                break;
                            }
                        }
                    }
                }
                start += nRecords;
                if (nRecords <= citations.size()) {
                    citations = citedao.getCitingDocuments(clusterID, start, 
                            nRecords);
                }else{
                    finished = true;
                }
                    
            }
        }
        return citing;
    } //- getCitingDocuments
    
    
    /**
     * @param clusterID
     * @return All the cited documents by the document represented by clusterID
     * or an empty list if no citations are found.
     * <b>Note</b> This method only includes cited documents that are part of
     * the corpus. Citations that doesn't have the correspondent document aren't
     * included.
     */
    private List<String>getCitedDocuments(Long clusterID) {

        int start = 0;
        int nRecords = 100;
        List<String>cited = new ArrayList<String>();
        List<ThinDoc> citations = null;
        if (clusterID != null) {
            citations = citedao.getCitedDocuments(clusterID, start, nRecords);
            
            boolean finished = citations.isEmpty();
            while (!finished) {
                for (ThinDoc cite : citations) {
                    if (cite.getInCollection()) {
                        // Obtain the DOIs associated to this cluster
                        List<String> dois = 
                            citedao.getPaperIDs(cite.getCluster());
                        Document citedDoc = null;
                        for (String doi : dois) {
                            citedDoc = csxdao.getDocumentFromDB(doi, 
                                    false, false);
                            if (citedDoc != null && citedDoc.isPublic()) {
                                cited.add(citedDoc.getDatum(Document.DOI_KEY));
                                break;
                            }
                        }
                    }
                }
                start += nRecords;
                if (nRecords <= citations.size()) {
                    citations = citedao.getCitedDocuments(clusterID, start, 
                            nRecords);
                }else{
                    finished = true;
                }
            }
        }
        return cited;
    } //- getCitedDocuments
    
	/**
	 * Generates the resumption token
	 * @param doi
	 * @param totalCount
	 * @param itemsSent
	 * @return A resumption token built based on the given parameters
	 */
	protected String generateResumptionToken(String doi, 
			Integer totalCount, Integer itemsSent, String metadataPrefix) {
		StringBuffer buf = new StringBuffer();
		if (doi != null && totalCount != null && itemsSent != null) {
			buf.append(doi);
			buf.append("-");
			buf.append(totalCount);
			buf.append("-");
			buf.append(itemsSent);
			buf.append("-");
			buf.append(metadataPrefix);
		}
		return buf.toString();
		
	} //- generateResumptionTokenElement
	
	/**
	 * @param date
	 * @return True if the given date comply with the repository granularity.
	 * If Granularity is not valid an error is aggregated to the errors list
	 */
	protected boolean isValidGranularity(String date) {
		boolean isValid = true;
		if ("YYYY-MM-DDThh:mm:ssZ".equals(getGranularity()) &&
				date.length() != 20) {
			isValid = false;
		}else if ("YYYY-MM-DD".equals(getGranularity()) &&
				date.length() != 10) {
			isValid = false;
		}
		if (!isValid) {
			addError(new OAIError("Invalid granularity", 
					OAIError.BAD_ARGUMENT_ERROR));
		}
		return isValid;
	} //- isValidGranularity
	
	/**
	 * Validate the date in both short and long granularity. Valid Granularity
	 * is checked in 
	 * @see edu.psu.citeseerx.oai.verbs.AbstractList#isValidGranularity(String)
	 * If date is not valid an error is aggregated to the errors list
	 * @param date
	 * @return true is the date is a valid OAI-PMH date. 
	 */
	protected boolean isValidDate(String date) {
		String shortPattern = "([0-9]{4,4})-{1,1}([0-9]{2,2})-{1,1}([0-9]{2,2})";
		String longPatter = shortPattern +
			"T{1,1}([0-9]{2,2}):{1,1}([0-9]{2,2}):{1,1}([0-9]{2,2})Z{1,1}";

		boolean valid = true;
		try {
			Pattern pattern = Pattern.compile("^"+shortPattern+"$");
			Matcher matcher = pattern.matcher(date);
			if (!matcher.find()) {
				valid = false;
			}else{
				int year = Integer.parseInt(matcher.group(1));
				int month = Integer.parseInt(matcher.group(2));
				int day = Integer.parseInt(matcher.group(3));
				int daysInMonth;
				
				if ( ((((year%4)==0) && ((year%100)!=0)) || ((year%400) == 0))
						&& (2 == month)) {
					daysInMonth = 29;
				}else{
					switch (month) {
					case 4: case 6: case 9: case 11:
						daysInMonth = 30;
						break;
					case 2:
						daysInMonth = 28;
					default:
						daysInMonth = 31;
						break;
					}
				}
				if ( (month <= 0) || (month > 12) || (day <= 0) || 
						(day > daysInMonth) || (year <= 0) ) {
					valid = false;
				}
			}
			pattern = Pattern.compile("^"+longPatter+"$");
			matcher = pattern.matcher(date);
			if (matcher.find()) {
				int hour = Integer.parseInt(matcher.group(1));
				int minutes = Integer.parseInt(matcher.group(2));
				int seconds = Integer.parseInt(matcher.group(3));
				if (hour < 0 || hour > 23 || minutes < 0 || minutes > 59 ||
						seconds < 0 || seconds > 59) {
					valid = false;
				}
			}else if ((date.length() > 10)) {
				valid = false;
			}
			Date repoMinDate = 
				DateUtils.parseDateToUTCDate(getEarliestDatestamp());
			Date otherDate = 
				DateUtils.parseDateToUTCDate(date);
			if (otherDate.before(repoMinDate)) {
				valid = false;
			}
		}catch (Exception e) {
			valid = false;
			addError(new OAIError("Invalid date", 
					OAIError.BAD_ARGUMENT_ERROR));
		}
		return valid;
	} //- isValidDate
	
	/**
	 * Determines if a date is OAI-PMH valid and if it complies with the
	 * repository granularity.
	 * Calling this method is the same that making the and of 
	 * @see edu.psu.citeseerx.oai.verbs.AbstractList#isValidGranularity(String)
	 * and  @see edu.psu.citeseerx.oai.verbs.AbstractList#isValidDate(String) 
	 * @param date
	 * @return true if data is a valid date for OAI-PMH
	 */
	protected boolean validateDate(String date) {
		return (isValidGranularity(date) && isValidDate(date));
	} //- validateDate
	
	/**
	 * Validate if the repository provides metadata in the given format. If not
	 * false is returned and an error is created.
	 * @param metadataPrefix
	 * @return true if the repository provides metadata in the given format, 
	 * false otherwise
	 */
	protected boolean isValidMetaDataPrefix(String metadataPrefix) {
		boolean isValid = false;
		
		if (metadataPrefix != null) {
			for (String prefix : metadataFormats) {
				if (metadataPrefix.equals(prefix)) {
					isValid = true;
					break;
				}
			}
		}
		if (!isValid) {
			addError(new OAIError(metadataPrefix + " not supported", 
					OAIError.CANNOT_DISEMINATE_FORMAT_ERROR));
		}
		return isValid;
	} //- isValidMetaDataPrefix
	
	/**
	 * Validates the resumption token. If the resumption token is not valid 
	 * errors are added to the error list
	 * @param resumptionToken
	 * @return true is the given resumptionToken is valid.
	 */
	protected boolean isValidResumptionToken(String resumptionToken) {
		boolean isValid = true;
		
		String doiRegExpr = 
			"^[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,4}$";

		String[] tokens = resumptionToken.split("-");
		
		if (tokens.length != 4) {
			isValid = false;
		}else{
			Pattern pattern = Pattern.compile(doiRegExpr);
			Matcher matcher = pattern.matcher(tokens[0]);
			if (!matcher.find()) {
				isValid = false;
			}
			try {
				Integer.parseInt(tokens[1]);
				Integer.parseInt(tokens[2]);
			}catch (NumberFormatException e) {
				isValid = false;
			}
			if (!isValidMetaDataPrefix(tokens[3])) {
				isValid = false;
			}
		}
		
		if (!isValid) {
			addError(new OAIError("The value of the resumptionToken argument " +  
					"is invalid", OAIError.BAD_RESUMPTION_TOKEN_ERROR));
		}
		return isValid;
	} //- isValidResumptionToken
	
	/**
	 * Returns the repository identifier for the given item
	 * @param doi
	 * @return the repository identifier for the given item
	 */
	protected String buildIdentifier(String doi) {
		return OAI_SCHEMA + getDelimiter() + getRepositoryIdentifier() + 
			getDelimiter() + doi;
	} //- buildIdentifier
	

	/**
	 * Builds a OAI record for a corpus document
	 * @param doc
	 * @param toAddTo
	 * @param metadataFormat
	 * @return the created record.
	 */
	protected Element buildDocumentRecord(Document doc, Element toAddTo, 
	        int metadataFormat) {

	    
	    List<String> cited = null; //getCitedDocuments(doc.getClusterID());
	    List<String> citing = null; //getCitingDocuments(doc.getClusterID());
	    return OAIUtils.addRecord(toAddTo, getViewDocURL(), cited, citing, 
	            RIGHTS, doc, contributor, getGranularity(), metadataFormat, 
	            buildIdentifier(doc.getDatum(Document.DOI_KEY)), 
	            doc.getVersionTime());
	} //- buildDocumentRecord
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest, org.jdom.Element)
	 */
	@Override
	protected abstract Element doProcess(HttpServletRequest request, 
	        Element root) throws OAIVerbException;

} //- Class AbstractList
