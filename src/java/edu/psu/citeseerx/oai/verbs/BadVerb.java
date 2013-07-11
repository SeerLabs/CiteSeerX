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
import org.springframework.web.bind.ServletRequestUtils;

/**
 * Utility class to generate the anwser when a badVerb error occurs.
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class BadVerb extends AbstractVerb {

    /*
     *  Defines expected parameters and if they are required or not.
     *  Parameters don't matter since the verb is incorrect. All the parameters
     *  area added so a badArgument error is not generated.
     */
    protected static final String[] expectedArguments = {
        "verb:true", "identifier:false", "metadataPrefix:false", "until:false",
        "from:false", "set:false", "resumptionToken:false"};
    
    public BadVerb() {
        super();
        for (int i =0; i < expectedArguments.length; ++i) {
            String[] values = expectedArguments[i].split(":");
            addArgument(values[0], Boolean.parseBoolean(values[1]));
        }
    } //- BadVerb
    
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest, org.jdom.Element)
	 */
	@Override
	protected Element doProcess(HttpServletRequest request, Element root)
			throws OAIVerbException {

		String verb = null;
		verb = ServletRequestUtils.getStringParameter(request, "verb", "");
		addError(new OAIError("Illegal OAI-PMH verb: " + verb, 
				OAIError.BAD_VERB_ERROR));
		throw new OAIVerbException(getErrors());
	} //- doProcess

} //- Class BadVerb
