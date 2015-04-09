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
package edu.psu.citeseerx.misc.charts;

import edu.psu.citeseerx.repository.UnknownRepositoryException;

/**
 * Generic interface for building citation histograms.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface ChartBuilder {

    /**
     * Builds histograms for all documents in the collection.  Only documents
     * whose citation counts have changed since the last update (or 
     * documents that are new) will be processed.
     * @throws UnknownRepositoryException
     */
    public void buildAll() throws UnknownRepositoryException;
    
} //- interface ChartBuilder
