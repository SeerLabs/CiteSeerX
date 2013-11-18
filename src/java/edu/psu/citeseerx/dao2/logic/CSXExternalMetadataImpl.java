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
package edu.psu.citeseerx.dao2.logic;

import java.util.List;

import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.dao2.ExternalMetadataDAO;
import edu.psu.citeseerx.domain.ACM;
import edu.psu.citeseerx.domain.CiteULike;
import edu.psu.citeseerx.domain.DBLP;

/**
 *  CSXExternalMetadataFacadeImplementation
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CSXExternalMetadataImpl implements CSXExternalMetadataFacade {

    private ExternalMetadataDAO extMetadataDAO;
    
    public void setExtMetadataDAO(ExternalMetadataDAO extMetadataDAO) {
        this.extMetadataDAO = extMetadataDAO;
    } //- setDblpDAO

    // ExternalMetadataDAO 
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#addDBLPRecord(edu.psu.citeseerx.domain.DBLP)
     */
    public void addDBLPRecord(DBLP record) throws DataAccessException {
        extMetadataDAO.addDBLPRecord(record);
    } //- addDBLPRecord

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#deleteDBLP()
     */
    public void deleteDBLP() throws DataAccessException {
        extMetadataDAO.deleteDBLP();
    } //- deleteDBLP

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#getDBLPRecordsByTitle(java.lang.String)
     */
    public List<DBLP> getDBLPRecordsByTitle(String title)
            throws DataAccessException {
        return extMetadataDAO.getDBLPRecordsByTitle(title);
    } //- getDBLPRecordsByTitle

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#addCiteULikeRecord(edu.psu.citeseerx.domain.CiteULike)
     */
    public void addCiteULikeRecord(CiteULike record) throws DataAccessException {
        extMetadataDAO.addCiteULikeRecord(record);
    } //- addCiteULikeRecord

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#getCiteULikeRecordByDOI(java.lang.String)
     */
    public CiteULike getCiteULikeRecordByDOI(String doi)
            throws DataAccessException {
        return extMetadataDAO.getCiteULikeRecordByDOI(doi);
    } //- getCiteULikeRecordByDOI

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#addACMRecord(edu.psu.citeseerx.domain.ACM)
     */
    @Override
    public void addACMRecord(ACM record) throws DataAccessException {
        extMetadataDAO.addACMRecord(record);
    } //- addACMRecord

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#getACMRecordsByTitle(java.lang.String)
     */
    @Override
    public List<ACM> getACMRecordsByTitle(String title)
            throws DataAccessException {
        return extMetadataDAO.getACMRecordsByTitle(title);
    } //- getACMRecordsByTitle
    
} //- class CSXExternalMetadataImpl
