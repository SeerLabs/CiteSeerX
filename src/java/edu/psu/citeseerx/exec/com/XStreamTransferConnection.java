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
package edu.psu.citeseerx.exec.com;

import java.io.*;
import java.net.*;
import com.thoughtworks.xstream.*;

/**
 * Used to create a communication channel around a base socket for
 * transferring objects by XML serialization using XStream.
 * 
 * @author Isaac Councill
 *
 */
public class XStreamTransferConnection extends ObjectTransferConnection {

    protected XStream xstream = new XStream();
    
    /**
     * Call super() to initialize socket I/O channels.
     * @param socket base socket for communication.
     * @param compressed whether to compress communication.
     * @throws IOException
     */
    public XStreamTransferConnection(Socket socket, boolean compressed,
            int compressedBlockSize) throws IOException {
        super(socket, compressed, compressedBlockSize);

    }  //- XStreamTransferConnection
    
    /**
     * Reads an object from the input stream.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object readObjectImpl() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }

    /**
     * Writes an object to the output stream.
     */
    public void writeObjectImpl(Object obj) throws IOException {
        objectOutputStream.writeObject(obj);
    }
    
    /**
     * Initialize socket inputstream as an XStream ObjectInputStream.
     */
    protected void initializeInputStream() throws IOException {
        objectInputStream = xstream.createObjectInputStream(
                new InputStreamReader(inputStream));
    }
    
    /**
     * Initialize socket outputstream as an XStream ObjectOutputStream.
     */
    protected void initializeOutputStream() throws IOException {
        objectOutputStream = xstream.createObjectOutputStream(
                new OutputStreamWriter(outputStream));
    }
    
}  //- XStreamTransferConnection
