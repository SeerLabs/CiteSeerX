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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Generic functionality for object transfer connections.  This class
 * sets up I/O streams from a specified socket (which should be a valid
 * connection) and specifies the API for handling such connections.
 * 
 * <b>Compression is currently broken - don't use it!</b>
 * 
 * @author Isaac Councill
 *
 */
public abstract class ObjectTransferConnection {

    private final Socket socket;
    
    protected OutputStream outputStream;
    protected InputStream inputStream;
 
    protected ObjectInputStream objectInputStream;
    protected ObjectOutputStream objectOutputStream;

    private boolean open = true;
    
    /* Amount of compressed outputstreams to buffer before compressing and
     * flushing. */
    protected final int compressedBlockSize;
    
    /**
     * Configuration option for setting status-specific read timeouts,
     * such as validation requests.
     */
    protected int statusReqTimeout = 50; 
    
    /**
     * Build a new ObjectTransferConnection around a specified socket.
     * @param socket a raw socket to use for underlying comm
     * @param compression whether or not to use GZip compression on I/O streams.
     * @throws IOException
     */
	public ObjectTransferConnection(Socket socket, boolean compression,
            int compressedBlockSize) throws IOException {
        this.socket = socket;
        this.compressedBlockSize = compressedBlockSize;
        if (compression) {
            outputStream = new CompressedBlockOutputStream(
                    socket.getOutputStream(),
                    this.compressedBlockSize);  //- buffer size, in bytes
            inputStream =
                new CompressedBlockInputStream(socket.getInputStream());
        } else {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        }
        
    }  //- ObjectTransferConnection
    

    /**
     * Wrapper for underlying readObject implementation.  This method handles
     * any validation requests before passing retrieved objects to the caller.
     * Lazily initializes the inputStream. 
     * @return object read from input stream.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    final public Object readObject() throws IOException,
            ClassNotFoundException {
        if (objectInputStream == null) {
            initializeInputStream();
        }
        Object obj;
        while ((obj = readObjectImpl()) instanceof ValidationRequest) {
            writeObject(new ValidationResponse(
                    ((ValidationRequest)obj).getID()));
        }
        return obj;
        
    }  //- readObject
    

    /**
     * Wrapper for underlying writeObject implementation.  Lazily initializes
     * outputStream before passing the object to the write implementation.
     * @param obj read from input stream.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void writeObject(Object obj) throws IOException {        
        if (objectOutputStream == null) {
            initializeOutputStream();
        }
        writeObjectImpl(obj);
        flush();
        
    }  //- writeObject
    
    
    /**
     * Subclasses must override to initialize the ObjectInputStream
     * inputStream correctly.
     * @throws IOException
     */
    protected abstract void initializeInputStream() throws IOException;

    /**
     * Subclasses must override to initialize the ObjectOutputStream
     * outputStream correctly.
     * @throws IOException
     */
    protected abstract void initializeOutputStream() throws IOException;
    
    /**
     * Subclasses must override to read objects from the input stream in
     * a manner specific to the connection implementation.
     * @return object read from input stream.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws EOFException
     */
    protected abstract Object readObjectImpl() throws IOException,
        ClassNotFoundException;
    
    /**
     * Subclasses must override to write objects to the output stream
     * in a manner specific to the connection implementation.
     * @param obj object to be written to output stream.
     * @throws IOException
     */
    protected abstract void writeObjectImpl(Object obj) throws IOException;
        
    
    /**
     * Cleanly shuts down stream resources.
     * @throws IOException
     */
    public final synchronized void terminate() {
        open = false;
        terminateImpl();
        try {
            socket.close();
        } catch (IOException e) {/*ignore*/}
    }
    
    /**
     * Subclasses may override this method to produce termination logic
     * specific to the connection implementation.
     * @throws IOException
     */
    protected void terminateImpl() {
        try {
            objectOutputStream.close();
        } catch (IOException e) {/*ignore*/}
        try {
            objectInputStream.close();
        } catch (IOException e) {/*ignore*/}
    }

    /**
     * Flushes the output stream.
     * @throws IOException
     */
    public final void flush() throws IOException {
        flushImpl();
    }
    
    /**
     * Subclasses can override to provide implementation-specific flush
     * behavior. 
     * @throws IOException
     */
    protected void flushImpl() throws IOException {
        objectOutputStream.flush();
    }

    /**
     * Sets the read timeout, in ms.
     */
    public final void setSoTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
    }
    
    /**
     * Sets the timeout specific to status requests.
     */
    public final void setStatusTimeout(int timeout) {
        statusReqTimeout = timeout;
    }
    
    /**
     * Gets the read timeout, in ms.  A value of 0 indicates that timeouts
     * are disabled.
     */
    public final int getSoTimeout() throws SocketException {
        return socket.getSoTimeout();
    }
    
    /**
     * Gets the read timeout for status requests, in ms.
     */
    public final int getStatusTimeout() {
        return statusReqTimeout;
    }
    
    /**
     * @return whether this stream is open for communication.
     */
    public final boolean isOpen() {
        return open;
    }
    
    /**
     * Makes sure that this connection is behaving as expected.  In response
     * to a validation request, the very next object returned should be
     * a validation response with the same ID.
     * @return whether this connection is valid.
     * @throws IOException
     * @throws InvalidException
     * @throws ClassNotFoundException
     */
    public synchronized final boolean validate() throws
            IOException, InvalidException, ClassNotFoundException {
        int timeout = getSoTimeout();
        try {
            // Set a timeout for the validation request.
            setSoTimeout(statusReqTimeout);
            ValidationRequest request = ValidationMessage.createRequest();
            writeObject(request);
            ValidationResponse response = 
                (ValidationResponse)readObject();
            if (!response.type().equals(ValidationMessage.RESPONSE) ||
                    (request.getID() != response.getID())) {
                throw new InvalidException();
            }
            return true;
        } catch (IOException e) {
            throw(e);
        } finally {
            // Return the socket to it's original timeout setting.
            setSoTimeout(timeout);
        }
        
    }  //- validate
    
    
    protected void finalize() throws Throwable {
        try {
            terminate();
        } finally {
            super.finalize();
        }
        
    }  //- finalize
    
    public class InvalidException extends RuntimeException {
        /**
         * 
         */
        private static final long serialVersionUID = 897923016051404962L;

        public InvalidException() {
            super("Corrupt validation response");
        }
    }
    
} //- class ObjectTransferConnection
