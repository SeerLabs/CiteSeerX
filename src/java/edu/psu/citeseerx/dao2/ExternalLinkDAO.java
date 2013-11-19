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

import java.util.List;

import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.domain.ExternalLink;
import edu.psu.citeseerx.domain.LinkType;

/**
 * Provides transparent access to External links persistence storage 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public interface ExternalLinkDAO {
    /**
     * Inserts a new Link type
     * @param link
     * @throws DataAccessException
     */
    public void addLinkType(LinkType link) throws DataAccessException;
    
    /**
     * Updates a LikeType
     * @param link
     * @param oldLabel
     * @throws DataAccessException
     */
    public void updateLinkType(LinkType link, String oldLabel) 
    throws DataAccessException;
    
    /**
     * Deletes the given link type
     * @param link
     * @throws DataAccessException
     */
    public void deleteLinkType(LinkType link) throws DataAccessException;
    
    /**
     * @param label
     * @return the link type associated to label
     * @throws DataAccessException
     */
    public LinkType getLinkType(String label) throws DataAccessException;
    
    /**
     * @return A LinkType List containing all the Links.
     * @throws DataAccessException
     */
    public List<LinkType> getLinkTypes() throws DataAccessException;
    
    /**
     * All the external links associated to the given paper id
     * @param doi
     * @return A List of ExternalLinks associated to a given paper
     * @throws DataAccessException
     */
    public List<ExternalLink> getExternalLinks(String doi) 
    throws DataAccessException;
    
    /**
     * Add a new external link associated to a paper
     * @param eLink
     * @throws DataAccessException
     */
    public void addExternalLink(ExternalLink eLink) throws DataAccessException;
    
    /**
     * Updates an external link with the provided information. If the external
     * link does not exist the method add it.
     * @param extLink
     * @throws DataAccessException
     */
    public void updateExternalLink(ExternalLink extLink) 
    throws DataAccessException;

    /**
     * @param label
     * @param lastID
     * @param amount
     * @return Returns amount number of papers, starting at lastID, which 
     * doesn't have an external link for the given label
     */
    public List<String> getPapersNoELink(String label, String lastID, 
            Long amount) throws DataAccessException;
    
    /**
     * @param label
     * @param doi
     * @return true if a external link exist for the given paper and label, 
     * false otherwise
     * @throws DataAccessException
     */
    public boolean getExternalLinkExist(String label, String doi)
    throws DataAccessException;
    
    /**
     * Deletes the external link for the given doi and label
     * @param doi
     * @param label
     * @throws DataAccessException
     */
    public void deleteExternalLink(String doi, String label)
    throws DataAccessException;
    
    /**
     * 
     * @param doi
     * @param label
     * @return Returns the URL to the resource associated to DOI by the
     * given label or null if no data is found.
     * @throws DataAccessException
     */
    public ExternalLink getLink(String doi, String label)
    throws DataAccessException;
} //- interface ExternalLinksDAO
