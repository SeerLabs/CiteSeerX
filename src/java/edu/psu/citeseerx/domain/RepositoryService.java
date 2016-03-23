package edu.psu.citeseerx.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import edu.psu.citeseerx.repository.DocumentUnavailableException;

/**	RepositoryService 
	Interface for storage of files and content. 
	History: forked from FileSysDAO
	Reason: Separation of responsibility
	@author Pradeep Teregowda
 	@version $Rev: 191 $ $Date: 2012-08-28 14:32:39 -0500 (Wed, 08 Feb 2012) $
*/



public interface RepositoryService {
	
	// Constants used in most implementations	
	public static final String FILETYPE ="type";
	public static final String TEXTFILE = "TXT";
	public static final String PDFFILE = "PDF";
	public static final String PSFILE = "PS";
	public static final String RICHTEXT = "RTF";
	public static final String MSWORD = "DOC";
	public static final String XMLFILE = "XML";
	public static final String REPOSITORYID = "repid";
	public static final String BODYFILE = "BODY";
	public static final String QUERY = "q";
	public static final String FILEQUERY = "file";
	public static final String VERSIONKEY = "version";
	
	/* storeDocument
	 * Basic method for storing a document in the repository
	 * @param p  HashMap containing more information about the file being stored (should include doi, repid, type etc) 
	 * @param fpath  path to file being stored
	 * @return nothing
	 * 
	*/
	public void storeDocument(Map<String,String> p, String fpath) throws IOException;

	/* writeVersion
	 * Basic method for writing a versioned document in the repository
	 * @param doc  Document to be written
	 * 
	 */
	
	public void writeVersion(Document doc) throws IOException;
	
	/* writeXML
	 * Basic method for writing a Documen in xml format
         * @param doc  Document to be written
	 */
	
	public void writeXML(Document doc) throws IOException;
	
	/* getDocument
	 * Basic method for obtaining file stream for documents
	 * @param  p HashMap containing more information about the file being stored (should include doi, repid, type etc)
	 * @return InputStream for the document
	 */
	
	public InputStream getDocument(Map<String,String> p) throws IOException, DocumentUnavailableException;
	
	/* fileTypes
	 *  List the filetypes available 
	 *  @param p HashMap containing more information about the document, (doi and repid are required)
	 */
	
	public String[] fileTypes(Map<String,String> p) throws IOException;
	
	/* getDocFromXML
	 * 	Get CSX document by providing the doi, the repository service fetches the xml and extracts the document from it
	 *  @param repID - String identifying the repository
	 *  @param relpath - relative path to the document
	 */
	
	public Document getDocFromXML(String repID, String relpath) throws IOException, DocumentUnavailableException;
	
	/* getDocumentContent
	 * @param p HashMap containing more information about the document, (doi and repid are required)
	 * @return String contents of the document being requested
	 */
	
	
	public String getDocumentContent(Map<String,String> p) throws IOException, DocumentUnavailableException;
	
}
