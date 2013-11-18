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
package edu.psu.citeseerx.utility;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A basic validator for the SAX API.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DOMValidator extends DefaultHandler {
    
    public boolean validationError = false;
    public SAXParseException saxParseException = null;

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception)
            throws SAXException {
        validationError = true;
        saxParseException = exception;
    } //- error
    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception)
            throws SAXException {
        validationError = true;
        saxParseException=exception;
    } //- fatalError
    
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception)
        throws SAXException { exception.printStackTrace(); }
    
}  //- class DOMValidator