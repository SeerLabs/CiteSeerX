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
package edu.psu.citeseerx.ingestion.ws;

import javax.xml.namespace.QName;
import javax.xml.rpc.*;

/**
 * API class for calling the DOIServer to generate a new DOI.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class DOIClient {

    private String endpointAddress;
    
    public void setEndpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }

    private String namespace = "http://doi.citeseerx.psu.edu";
    
    /**
     * @param namespace (default http://doi.citeseerx.psu.edu)
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    } //- setNamespace
    
    protected Service service;

    protected final QName operationName =
        new QName(namespace, "getDOI");
    protected final QName xsdString =
        new QName("http://www.w3.org/2001/XMLSchema", "string");    
    

    public DOIClient() throws Exception {

        ServiceFactory factory = ServiceFactory.newInstance();
        service = factory.createService(null);

    }  //- DOIClient
    
    
    /**
     * Gets a new DOI from the DOIServer.
     * @param doiType type of DOI as defined in CSXConstants
     * @return a new DOI from the DOIServer.
     * @throws Exception
     */
    public String getDOI(int doiType) throws Exception {
        
        Call call = service.createCall();

        call.setTargetEndpointAddress(endpointAddress);
        call.setOperationName(operationName);
        
        String doi = (String)call.invoke(new Object[] { new Integer(doiType) });
        
        return doi;
        
    }  //- getDOI
    
}  //- class DOIClient
