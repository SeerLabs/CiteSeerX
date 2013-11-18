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
package edu.psu.citeseerx.domain;

import java.io.*;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Interface to be implemented by domain objects that support bi-directional
 * XML serialization.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public interface XMLSerializable {

    public final static boolean INCLUDE_SYS_DATA = true;
    public final static boolean PUBLIC_ONLY = false;
    
    /**
     * 
     * @param sysData
     * @return A XML version of the object. If sysData is true all private
     * fields are included
     */
    public String toXML(boolean sysData);
    
    /**
     * Stores in buffer a XML version of this object. If sysData is true, 
     * private fields are included.
     * @param buffer
     * @param sysData
     */
    public void buildXML(StringBuffer buffer, boolean sysData);
    
    /**
     * writes to the given output stream the XML version of the object including
     * private fields if sysData is true
     * @param out
     * @param sysData
     * @throws IOException
     */
    public void toXML(OutputStream out, boolean sysData) throws IOException;
    
    /**
     * Builds the current object from data provided by the given input stream
     * @param in
     * @throws IOException
     */
    public void fromXML(InputStream in) throws IOException;
    
    /**
     * Builds the current object from data provided by a XML document
     * @param root
     * @throws JDOMException
     */
    public void fromXML(Element root) throws JDOMException;
    
} //- interface XMLSerializable
