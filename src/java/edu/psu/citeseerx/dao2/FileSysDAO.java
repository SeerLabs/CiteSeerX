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
package edu.psu.citeseerx.dao2;

import java.io.IOException;
import java.io.FileInputStream;
import org.springframework.dao.DataAccessException;

import com.lowagie.text.pdf.PdfReader;

import edu.psu.citeseerx.domain.*;
import java.util.List;

/**
 * File access methods.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface FileSysDAO {

    /**
     * Build a Document object from the XML file located at relPath, a path
     * relative to the root of the repository specified by repID.
     * @param repID
     * @param relPath
     * @return A CiteSeerX document from a XML file.
     * @throws IOException
     */
    public Document getDocFromXML(String repID, String relPath)
    throws IOException;
    
    /**
     * Writes an XML representation of the specified document to
     * persistent storage.
     * @param doc
     * @throws IOException
     */
    public void writeXML(Document doc) throws IOException;
    
    /**
     * Retrieve a Document object at the version specified.  If the version
     * does not exist, a null value will be returned.
     * @param doi
     * @param version
     * @return A specific version of the document or null if that version does
     * not exist.
     * @throws DataAccessException
     * @throws IOException
     */
    public Document getDocVersion(String doi, int version) throws IOException;

    /**
     * Retrieve a Document object based on the named version specified.  If
     * the named version does not exist, a null value will be returned.
     * @param doi
     * @param name
     * @return A specific version of the document based on the version name or 
     * null if that version name does not exist.
     * @throws DataAccessException
     * @throws IOException
     */
    public Document getDocVersion(String doi, String name) throws IOException;
    
    /**
     * Stores version data for a Document object.  The version number and name
     * will be read from the Document.
     * @param doc
     * @throws IOException
     */
    public void writeVersion(Document doc) throws IOException;
    
    /**
     * Retrieves a FileInputStream for a file with the specified extension
     * (type) located on the specified repository.
     * @param doi the ID of the document whose file is to be retrieved.
     * @param repID the ID of the repository on which to search.
     * @param type the file extension.
     * @return A FileInputStream for the given parameters
     * @throws IOException If the file is not found within the repository
     */
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException;
    
    /**
     * Retrieves a com.lowagie.text.pdf.PDFReader representing a PDF document
     * located on the specified repository.
     * The intended used of this method is to allow PDF manipulation. For
     * instance, returning only a page instead of the whole document.
     * @param doi the ID of the document whose file is to be retrieved.
     * @param repID the ID of the repository on which to search.
     * @return A com.lowagie.text.pdf.PDFReader for the given parameters
     * @throws IOException If the file is not found within the repository
     */
    public PdfReader getPdfReader(String doi, String repID)
    throws IOException;
    
    /**
     * Returns a list of file extensions available for a specific document
     * within a specific repository. 
     * @param doi
     * @param repID
     * @return All the file extensions for a specific document
     * @throws IOException
     */
    public List<String> getFileTypes(String doi, String repID)
    throws IOException;

    /**
     * @param doi
     * @return the ID of the repository on which a given document is stored.
     */
    public String getRepositoryID(String doi);
    
} //- interface FileSysDAO
