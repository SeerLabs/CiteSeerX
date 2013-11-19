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
package edu.psu.citeseerx.fixers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.wcohen.ss.api.StringWrapper;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.updates.UpdateManager;
import edu.psu.citeseerx.utility.SafeText;
import edu.psu.citeseerx.utility.SeerSoftTFIDF;

/**
 * Used for apply corrections made in the old citeseer into the new one.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class LegacyMetadataFixer {
	
	private static final String CORRECTION_SOURCE = "user correction - " +
			"Legacy Corrections"; 
	private static final String	USERNAME = "SystemCorrections"; 
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private UpdateManager updateManager;
    
    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    } //- setUpdateManager
    
    private double similarityFactor = 0.8;
    
    public void setSimilarityFactor(double similarityFactor) {
    	this.similarityFactor = similarityFactor;
    } //- setAcceptedDistance
    
	private SeerSoftTFIDF distanceMetric = null;
	
	public void setDistanceMetric(SeerSoftTFIDF distanceMetric) {
	    this.distanceMetric = distanceMetric;
	} //- setDistanceMetric
	
	/**
     * Apply the correction contained in a xmlFile to a citeseerx document 
     * if legacyId maps to an actual document and there are corrections to be 
     * made
     * @param xmlFile
     * @param legacyId
     * @throws Exception
     */
    public void process(String xmlFile, int legacyId) throws Exception {
    	
    	File file = new File(xmlFile);
    	if (!file.exists()) {
    		throw new IOException("File not found: " + xmlFile);
    	}
    	logger.info("Loading file: " + xmlFile);
    	FileInputStream in = new FileInputStream(file);
    	LegacyData oldData = new LegacyData(in);
    	
    	Document doc = null;
    	String doi = csxdao.getNewID(legacyId);
    	boolean corrected = false;
    	if (doi != null) {
    		try {
    		    logger.info("Processing document: " + doi);
    			// Got the citeseerx id now retrieve the document
    			doc = csxdao.getDocumentFromDB(doi);
    			if (doc != null) {
	    			// Do corrections.
	    			corrected = doCorrections(doc, oldData);
	    			
	    			if (corrected) {
	    				// Something changed send the corrections to
	    				// storage.
	    			    logger.info("Document: " + doi + " changed. " +
	    			    		"Storing new data.");
	    				updateManager.doCorrection(doc, USERNAME);
	    			}
	    			logger.info("Document: " + doi + " processed.");
    			}else{
    			    logger.info("No Document with id: " + doi + 
    						" was found. Check your legacy id mappings table");
    			}
    		}catch (Exception e) {
    		    logger.error("An error ocurred while correcting document: " 
    		            + doi, e);
    		    throw(e);
			}
    	}else{
    	    logger.info("LegacyId: " + legacyId + " doesn't have a " +
    				"mapping in CiteSeerX.");
    	}
    	logger.info("File: " + xmlFile + " was processed");
    } //- process
    
    /*
     * Apply corrections if any to a CiteSeerX document using the
     * legacy data
     */
    private boolean doCorrections(Document doc, LegacyData lData) {
    	boolean corrected = false;
    	boolean fieldCorrected = false;
    	
    	String versionName = doc.getVersionName(); 
    	if (versionName != null && versionName.compareTo("USER") == 0) {
    	    corrected = correctEmpty(doc, lData);
    	}else{
    	
        	String oldTitle = lData.title.toLowerCase(); 
        	if ( (oldTitle.trim().length() > 0) && 
        			(oldTitle.compareTo("unknown") != 0) ) {
        	    fieldCorrected = updateDocField(doc, Document.TITLE_KEY, 
        	            lData.title);
        	    corrected = fieldCorrected ? fieldCorrected : corrected;
        	    
        	}
        	if ( (lData.abs.trim().length() > 0) && 
        			(lData.abs.toLowerCase().compareTo("unknown") != 0) ) {
        	    fieldCorrected = 
        			updateDocField(doc, Document.ABSTRACT_KEY, lData.abs);
        		corrected = fieldCorrected ? fieldCorrected : corrected;
        	}
        	if (lData.volume.trim().length() > 0 && 
        			(lData.volume.toLowerCase().compareTo("unknown") != 0)) {
        		try {
        			Integer.parseInt(lData.volume);
        			fieldCorrected = 
            			updateDocField(doc, Document.VOL_KEY, lData.volume);
        			corrected = fieldCorrected ? fieldCorrected : corrected;
        		}catch(NumberFormatException nfE) {}
        	}
        	if (lData.year.trim().length() > 0 && 
        			(lData.year.toLowerCase().compareTo("unknown") != 0)) {
        		try {
        			Integer.parseInt(lData.year);
        			fieldCorrected = 
            			updateDocField(doc, Document.YEAR_KEY, lData.year);
        			corrected = fieldCorrected ? fieldCorrected : corrected;
        		}catch(NumberFormatException nfE) {}
        	}
        	if ( (lData.publisher.trim().length() > 0) && 
        			(lData.publisher.toLowerCase().compareTo("unknown") != 0) ) {
        	    fieldCorrected = 
        			updateDocField(doc, Document.PUBLISHER_KEY, lData.publisher);
        		corrected = fieldCorrected ? fieldCorrected : corrected;
        	}
        	
        	
        	// Now the authors.
        	List<Author> currentAuthors = doc.getAuthors();
        	boolean authorAdded = false;
        	boolean authorCorrected = false;
        	
        	for (int i = 0; i < lData.authors.length; ++i) {
        		boolean matchFound = false;
    
        		String oldAuthor = lData.authors[i];
        		if (oldAuthor.trim().length() == 0 || 
        		        oldAuthor.toLowerCase().contains("et al")) {
        		    continue;
        		}
        		StringWrapper oldAuthPrep = distanceMetric.prepare(oldAuthor);
        		double similarity = 0;
        		for (Author currentAuthor : currentAuthors) {
        			String currentAuthName =  
        			    currentAuthor.getDatum(Author.NAME_KEY);
        			similarity = distanceMetric.score(oldAuthPrep, 
        			        distanceMetric.prepare(currentAuthName));
        			if (similarity >= 1.0) {
        				// An exact match found
        				matchFound = true;
        				break;
        			}else if(similarity >= similarityFactor && similarity < 1.0) {
        				// They are similar but not equals. Changed it
        				currentAuthor.setDatum(Author.NAME_KEY, 
        				        lData.authors[i]);
        				authorCorrected = true;
        				matchFound = true;
        				break;
        			}
        		}
        		if (!matchFound) {
    				// it's one that we don't have
    				Author newAuthor = new Author();
    				newAuthor.setDatum(Author.NAME_KEY, lData.authors[i]);
    				authorAdded = true;
    				if (i < currentAuthors.size()) {
    				    currentAuthors.add(i, newAuthor);
    				}else{
    				    currentAuthors.add(newAuthor);
    				}
    				authorCorrected = true;
    			}
        	}

        	corrected = authorCorrected ? authorCorrected : corrected;
        	
    		// when adding an author the order could change. Fix it!
    		for (int j = 0; j < currentAuthors.size(); ++j) {
    		    if (authorAdded) {
    		        String order = 
    		            currentAuthors.get(j).getDatum(Author.ORD_KEY);
    		        order = (order == null) ? "-1" : order;
        			int currentOrder = Integer.parseInt(order);
        			if (currentOrder != j+1) {
        				currentAuthors.get(j).setDatum(
        						Author.ORD_KEY, Integer.toString(j+1));
        				currentAuthors.get(j).setSource(
        						Author.ORD_KEY, CORRECTION_SOURCE);
        			}
    		    }
    		    if (authorCorrected) {
    		        // update source of correction for the rest of the fields
    		        setCorrectionSource(currentAuthors.get(j));
    		    }
    		}
    	}

    	return corrected;
    } //- doCorrections
    
    /*
     * Sets the correction source for the author
     */
    private void setCorrectionSource(Author author) {
        String authorKeys[] = {Author.NAME_KEY, Author.AFFIL_KEY, 
                Author.ADDR_KEY, Author.EMAIL_KEY};
        
        for (int j = 0; j < authorKeys.length; ++j) {
            author.setSource(authorKeys[j], CORRECTION_SOURCE);
        }
    } //- setCorrectionSource
    
    /**
     * Corrects only empty data since a USER correction was found.
     * @param doc
     * @param lData
     * @return
     */
    private boolean correctEmpty(Document doc, LegacyData lData) {
        boolean fieldCorrected = false;
        boolean corrected = false;
        
        String value = doc.getDatum(Document.TITLE_KEY); 
        String oldValue;
        if (value == null || value.length() == 0) {
            oldValue = lData.title.toLowerCase();
            if ( (oldValue.trim().length() > 0) && 
                    (oldValue.compareTo("unknown") != 0) ) {
                fieldCorrected = updateDocField(doc, Document.TITLE_KEY, 
                        lData.title);
                corrected = fieldCorrected ? fieldCorrected : corrected; 
            }
        }
        value = doc.getDatum(Document.ABSTRACT_KEY);
        if (value == null || value.length() == 0) {
            oldValue = lData.abs.toLowerCase();
            if ( (oldValue.trim().length() > 0) && 
                    (oldValue.compareTo("unknown") != 0) ) {

                fieldCorrected = 
                    updateDocField(doc, Document.ABSTRACT_KEY, lData.abs);
                corrected = fieldCorrected ? fieldCorrected : corrected;
            }
        }
        value = doc.getDatum(Document.VOL_KEY);
        if (value == null || value.length() == 0) {
            oldValue = lData.volume.toLowerCase();
            if ( (oldValue.trim().length() > 0) && 
                    (oldValue.compareTo("unknown") != 0) ) {
                try {
                    Integer.parseInt(lData.volume);
                    fieldCorrected = 
                        updateDocField(doc, Document.VOL_KEY, lData.volume);
                    corrected = fieldCorrected ? fieldCorrected : corrected;
                }catch(NumberFormatException nfE) {}
            }
        }
        value = doc.getDatum(Document.YEAR_KEY);
        if (value == null || value.length() == 0) {
            oldValue = lData.year.toLowerCase();
            if ( (oldValue.trim().length() > 0) && 
                    (oldValue.compareTo("unknown") != 0) ) {
                try {
                    Integer.parseInt(lData.year);
                    fieldCorrected = 
                        updateDocField(doc, Document.YEAR_KEY, lData.year);
                    corrected = fieldCorrected ? fieldCorrected : corrected;
                }catch(NumberFormatException nfE) {}
            }
        }
        
        value = doc.getDatum(Document.PUBLISHER_KEY);
        if (value == null || value.length() == 0) {
            oldValue = lData.publisher.toLowerCase();
            if ( (oldValue.trim().length() > 0) && 
                    (oldValue.compareTo("unknown") != 0) ) {
                fieldCorrected = updateDocField(doc, Document.PUBLISHER_KEY, 
                        lData.publisher);
                corrected = fieldCorrected ? fieldCorrected : corrected; 
            }
        }
        return corrected;
    } //- correctEmpty
    
    /*
     * Updates a document field with the value provided
     */
    private boolean updateDocField(Document doc, String key,
            String newVal) {

    	boolean update = false;
        if (doc.getDatum(key) == null && newVal == null) {
            return update;
        }
        
        if (doc.getDatum(key) == null && newVal != null) {
            update = true;
        } else if (doc.getDatum(key) != null && newVal == null) {
            update = true;
        } else if (!doc.getDatum(key).equals(newVal)) {
            update = true;
        }
        if (update) {
            doc.setDatum(key, newVal);
            doc.setSource(key, CORRECTION_SOURCE);
        }
        return update;
        
    }  //- updateDocField
    
    /*private void printDocument(Document doc) {
        System.out.println("Id: " + doc.getDatum(Document.DOI_KEY));
        System.out.println("Title: " + doc.getDatum(Document.TITLE_KEY));
        System.out.println("Volume: " + doc.getDatum(Document.VOL_KEY));
        System.out.println("Year: " + doc.getDatum(Document.YEAR_KEY));
        System.out.println("Abstract: " + doc.getDatum(Document.ABSTRACT_KEY));
        List<Author> authors = doc.getAuthors();
        for (Author author : authors) {
            System.out.println("Author Name: " + author.getDatum(Author.NAME_KEY));
            System.out.println("Author Ord: " + author.getDatum(Author.ORD_KEY));
        }
    }*/ //- printDocument
    
    class LegacyData {
    	String title;
    	String abs;
    	String[] authors;
    	String publisher;
    	String volume;
    	String year;
    	
    	public LegacyData (InputStream in) throws Exception {
    		SAXBuilder builder = new SAXBuilder();
    		
    		org.jdom.Document xmldoc = builder.build(in);
            Element root = xmldoc.getRootElement();
            
            if (!root.getName().equals("record")) {
                throw new Exception("Expected 'record' root element, " +
                        "found " + root.getName());
            }
            
            List<Element> children = root.getChildren();
            for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            	Element child = (Element)it.next();
            	if (child.getName().equals("publisher")) {
            		publisher = 
            			SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}else if (child.getName().equals("title")) {
            		title = SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}else if (child.getName().equals("abstract")) {
            		abs = SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}else if (child.getName().equals("author")) {
            		authors = child.getValue().split(",");
            		for (int i = 0; i < authors.length; ++i) {
        		        authors[i] = 
        		            SafeText.decodeHTMLSpecialChars(authors[i]);
        		        authors[i] =
        		            SafeText.HtmlAccentsToNonASCII(authors[i]);
            		}
            	}else if (child.getName().equals("volume")) {
            		volume = SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}else if (child.getName().equals("year")) {
            		year = SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}
            }
    	} //- LegacyData
    	
    } // class legacyData

} //- LegacyMetadataFixer
