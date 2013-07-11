/**
 * 
 */
package edu.psu.citeseerx.dao2;

import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.domain.PDFRedirect;

/**
 * @author pradeep
 *
 */
public interface RedirectPDFDAO {

	
	// Most common function used when document property is set to 
	// redirect
	public PDFRedirect getPDFRedirect(String doi)
			throws DataAccessException;
	
	
	// Not sure if this should be used at all, the
	// admin direct injection into the db seems better
	// Here so that we can implement it in the web interface
	// sometime in the future.
	
	public void insertPDFRedirect(PDFRedirect pdfredirect)
		    throws DataAccessException;
	
	// Update the link
	public void updatePDFRedirect(String doi, PDFRedirect pdfredirect)
		    throws DataAccessException;
	
	// Update the label - link (template)
	public void updatePDFRedirectTemplate(String label, String urltemplate)
		throws DataAccessException;
	
}
