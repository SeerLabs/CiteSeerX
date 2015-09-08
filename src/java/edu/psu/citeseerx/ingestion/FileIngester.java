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

import java.io.IOException;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.repository.UnknownRepositoryException;

/**
 * Generic interface for utilities that import files from some location.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface FileIngester {

    /**
     * Copies all file resources for a given Document object from the location
     * specified in fileBase to the repository specified within the Document
     * object.  Files to be copied include all files with the following
     * extensions appended to fileBase: pdf, ps, doc, rtf, txt, body, cite.
     * @param doc
     * @param fileBase the path to the files that should be imported, plus
     * the base name (without extension) of the files to copy; e.g., to
     * import files /path/to/dir/1.pdf, /path/to/dir/1.body, and
     * /path/to/dir/1.txt, fileBase should be set to "/path/to/dir/1".
     * @throws IOException
     * @throws UnknownRepositoryException
     */
    public void importFileData(Document doc, String fileBase)
    throws IOException, UnknownRepositoryException;
    
} //- interface FileIngester
