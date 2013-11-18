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
package edu.psu.citeseerx.ingestion;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

/**
 * Test code used for debugging the headparser web service.  This could be
 * made into a generic API if anyone wanted to do so, so I've left it.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 * @deprecated
 */
public class SVMTest {

    protected Service service;

    protected final QName operationName =
        new QName("Parser", "parseHeader");
    protected final QName xsdString =
        new QName("http://www.w3.org/2001/XMLSchema", "string");

    protected String endpointAddress;
    
    public SVMTest() {
        try {
            ServiceFactory factory = ServiceFactory.newInstance();
            service = factory.createService(null);

            endpointAddress = "http://proc6.ist.psu.edu:40000";

            Call call = service.createCall();

            call.setTargetEndpointAddress(endpointAddress);
            call.setOperationName(operationName);
            call.addParameter("filePath", xsdString,
                    String.class, ParameterMode.IN);
            call.addParameter("repositoryID", xsdString,
                    String.class, ParameterMode.IN);
            call.setReturnType(org.apache.axis.Constants.XSD_BASE64);

            byte[] bytes = (byte[])call.invoke(new Object[] {"Ingest1_tmp/USUB-icouncill1192042350800/councill05aai.txt", "rep1"});
            //byte[] dbytes = Base64.decode(new String(bytes));
            System.out.println(new String(bytes));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    public static void main(String[] args) {
        SVMTest test = new SVMTest();
    }
} //- class SVMTest
