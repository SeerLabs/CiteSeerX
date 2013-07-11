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

/**
 * Generic interface for clusterable data objects.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface Clusterable {

    public static final String CLUST_KEY = "clusterid";
    
    /**
     * 
     * @return The cluster this object is part of
     */
    public Long getClusterID();
    
    /**
     * Gives a cluster id to the object
     * @param id
     */
    public void setClusterID(Long id);
    
    /**
     * 
     * @return Informs if the object is clustered or not
     */
    public boolean isClustered();
    
} //- interface Clusterable
