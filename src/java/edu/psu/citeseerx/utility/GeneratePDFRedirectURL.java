/**
 * 
 */
package edu.psu.citeseerx.utility;

/**
 * @author Pradeep
 *
 */
public class GeneratePDFRedirectURL {

	
	/* generateURLFromTemplate
	 * Given
	 * 	urltemplate (http://www.website.com/documenthere/viewdocument?_ID_
	 *  parameter (external doi ?)
	 *  
	 * Return
	 *  a url by substitution
	*/
	public static String generateURLFromTemplate(String urlTemplate, Object parameter) {
		// please modify following if you are going to use other types of parameters
		
		if(parameter != null) {
			String id = (String)parameter; // The idea is to extend this so we can handle multiple parameters
			String newUrl = urlTemplate.replaceAll("_ID_", id);
			return newUrl;
		}
		return urlTemplate;
	}

}
