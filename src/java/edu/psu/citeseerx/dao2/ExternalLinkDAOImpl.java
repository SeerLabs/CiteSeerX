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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import edu.psu.citeseerx.domain.ExternalLink;
import edu.psu.citeseerx.domain.LinkType;

/**
 * Spring-based JDBC implementation of ExternalLinkDAO. 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ExternalLinkDAOImpl extends JdbcDaoSupport implements
        ExternalLinkDAO {

    protected InsertLinkType insertLinkType;
    protected GetExternalLinksMapping getExternalLinksMapping;
    protected GetLinkTypeMapping getLinkTypeMapping;
    protected GetLinkTypesMapping getLinkTypesMapping;
    protected InsertExternalLink insertExternalLink;
    protected UpdateLinkType updateLinkType;
    protected DeleteLinkType deleteLinkType;
    protected UpdateExternalLink updateExternalLink;
    protected GetPapersNoELinkMapping getPapersNoELinkMapping;
    protected GetExternalLinkMapping getExternalLinkMapping;
    protected DeleteExternalLink deleteELink;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        insertLinkType = new InsertLinkType(getDataSource());
        getExternalLinksMapping = new GetExternalLinksMapping(getDataSource());
        getLinkTypeMapping = new GetLinkTypeMapping(getDataSource());
        getLinkTypesMapping = new GetLinkTypesMapping(getDataSource());
        insertExternalLink = new InsertExternalLink(getDataSource());
        updateLinkType = new UpdateLinkType(getDataSource());
        deleteLinkType = new DeleteLinkType(getDataSource());
        updateExternalLink = new UpdateExternalLink(getDataSource());
        getPapersNoELinkMapping = new GetPapersNoELinkMapping(getDataSource());
        getExternalLinkMapping = new GetExternalLinkMapping(getDataSource());
        deleteELink = new DeleteExternalLink(getDataSource());
    } //- initMappingSqlQueries
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#addLinkType(edu.psu.citeseerx.domain.LinkType)
     */
    public void addLinkType(LinkType link) throws DataAccessException {
        insertLinkType.run(link);
    } //- addLinkType

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getExternalLiks(java.lang.String)
     */
    public List<ExternalLink> getExternalLinks(String doi)
            throws DataAccessException {
        return getExternalLinksMapping.execute(doi);
    } //- getExternalLiks

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getLinkType(java.lang.String)
     */
    public LinkType getLinkType(String label) throws DataAccessException {
        List<LinkType> links = getLinkTypeMapping.execute(label);
        LinkType link = null;
        if (!links.isEmpty()) {
            link = links.get(0);
        }
        return link;
    } //- getLinkType

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getLinkTypes()
     */
    public List<LinkType> getLinkTypes() throws DataAccessException {
        return getLinkTypesMapping.execute();
    } //- getLinkTypes

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#AddExternalLink(edu.psu.citeseerx.domain.ExternalLink)
     */
    public void addExternalLink(ExternalLink eLink) throws DataAccessException {
        insertExternalLink.run(eLink);
    } //- AddExternalLink

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#UpdateLinkType(edu.psu.citeseerx.domain.LinkType, java.lang.String)
     */
    public void updateLinkType(LinkType link, String oldLabel)
            throws DataAccessException {
        updateLinkType.run(link, oldLabel);
    } //- UpdateLinkType

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#DeleteLinkType(edu.psu.citeseerx.domain.LinkType)
     */
    public void deleteLinkType(LinkType link) throws DataAccessException {
        deleteLinkType.run(link.getLabel());
    } //- DeleteLinkType


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#updateExternalLink(edu.psu.citeseerx.domain.ExternalLink)
     */
    public void updateExternalLink(ExternalLink extLink)
            throws DataAccessException {
        int updated = updateExternalLink.run(extLink);
        if (0 == updated) {
            addExternalLink(extLink);
        }
    } //- updateExternalLink


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getPapersNoELink(java.lang.String, java.lang.String, java.lang.Long)
     */
    public List<String> getPapersNoELink(String label, String lastID,
            Long amount) throws DataAccessException {
        return getPapersNoELinkMapping.run(label, lastID, amount);
    } //- getPapersNoELink


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getExternalLinkExist(java.lang.String, java.lang.String)
     */
    public boolean getExternalLinkExist(String label, String doi)
            throws DataAccessException {
        
        boolean linkExist = false;
        List<ExternalLink> links = getExternalLinkMapping.run(label, doi);
        if (!links.isEmpty()) {
            linkExist = true;
        }
        return linkExist;
    } //- getExternalLinkExist


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#deleteExternalLink(java.lang.String, java.lang.String)
     */
    public void deleteExternalLink(String doi, String label)
            throws DataAccessException {
        deleteELink.run(doi, label);
    } //- deleteExternalLink

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getLink(java.lang.String, java.lang.String)
     */
    public ExternalLink getLink(String doi, String label)
            throws DataAccessException {
        List<ExternalLink> links = getExternalLinkMapping.run(label, doi);
        ExternalLink link = null;
        if (!links.isEmpty()) {
            link = links.get(0);
        }
        return link;
    } //- getLink



    private static final String DEF_INSERT_LINK_TYPE_QUERY = 
        "insert into link_types values (?, ?)";
    
    private class InsertLinkType extends SqlUpdate {

        public InsertLinkType(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_LINK_TYPE_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertLinkType.InsertLinkType
        
        public int run(LinkType link) {
            Object[] params = new Object[] {
                    link.getLabel(),
                    link.getBaseURL()
            };
            return update(params);
        } //- InsertLinkType.run
        
    } //- class InsertLinkType 
    
    private static final String DEF_GET_ELINKS_QUERY =
        "select e.paperid, e.label, l.baseURL, e.url from elinks e, " +
        "link_types l where e.paperid = ? and e.label = l.label";
    
    private class GetExternalLinksMapping extends MappingSqlQuery {
        public GetExternalLinksMapping(DataSource ds) {
            super(ds, DEF_GET_ELINKS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetExternalLinksMapping.GetExternalLinksMapping
        
        protected ExternalLink mapRow(ResultSet rs, int rownum) 
        throws SQLException {
            ExternalLink eLink = new ExternalLink();
            eLink.setLabel(rs.getString("label"));
            eLink.setPaperID(rs.getString("paperid"));
            eLink.setUrl(rs.getString("baseURL") + rs.getString("url"));

            return eLink;
        } //- GetExternalLinksMapping.mapRow
    } //- class GetExternalLinksMapping
    
    private static final String DEF_GET_ELINK_QUERY =
        "select e.paperid, e.label, l.baseURL, e.url from elinks e, " +
        "link_types l where e.paperid = ? and e.label = ? and " +
        "e.label = l.label";
    
    private class GetExternalLinkMapping extends MappingSqlQuery {
        public GetExternalLinkMapping(DataSource ds) {
            super(ds, DEF_GET_ELINK_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetExternalLinkMapping.GetExternalLinkMapping
        
        protected ExternalLink mapRow(ResultSet rs, int rownum) 
        throws SQLException {
            ExternalLink eLink = new ExternalLink();
            eLink.setLabel(rs.getString("label"));
            eLink.setPaperID(rs.getString("paperid"));
            eLink.setUrl(rs.getString("baseURL") + rs.getString("url"));

            return eLink;
        } //- GetExternalLinkMapping.mapRow
        
        public List<ExternalLink> run(String label, String doi) {
            Object[] params = new Object[] {
                    doi,
                    label
            };
            return execute(params);
        }
    } //- class GetExternalLinkMapping
    
    private final static String DEF_GET_LINK_TYPE_QUERY = 
        "select label, baseURL from link_types where label = ?";
    
    private class GetLinkTypeMapping extends MappingSqlQuery {
        public GetLinkTypeMapping(DataSource ds) {
            super(ds, DEF_GET_LINK_TYPE_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetExternalLinkMapping.GetExternalLinkMapping
        
        protected LinkType mapRow(ResultSet rs, int rownum) 
        throws SQLException {
            LinkType link = new LinkType();
            link.setLabel(rs.getString("label"));
            link.setBaseURL(rs.getString("baseURL"));

            return link;
        } //- GetLinkTypeMapping.mapRow
        
    } //- class GetLinkTypeMapping
    
    private final static String DEF_GET_LINK_TYPES_QUERY = 
        "select label, baseURL from link_types";
    
    private class GetLinkTypesMapping extends MappingSqlQuery {
        public GetLinkTypesMapping(DataSource ds) {
            super(ds, DEF_GET_LINK_TYPES_QUERY);
            compile();
        } //- GetLinkTypesMapping.GetLinkTypesMapping
        
        protected LinkType mapRow(ResultSet rs, int rownum) 
        throws SQLException {
            LinkType link = new LinkType();
            link.setLabel(rs.getString("label"));
            link.setBaseURL(rs.getString("baseURL"));

            return link;
        } //- GetLinkTypesMapping.mapRow
        
    } //- class GetLinkTypesMapping
    
    private static final String DEF_INSERT_EXTERNAL_LINK_TYPE_QUERY = 
        "insert into elinks values (?, ?, ?)";
    
    private class InsertExternalLink extends SqlUpdate {

        public InsertExternalLink(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_EXTERNAL_LINK_TYPE_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertExternalLink.InsertExternalLink
        
        public int run(ExternalLink eLink) {
            Object[] params = new Object[] {
                    eLink.getPaperID(),
                    eLink.getLabel(),
                    eLink.getUrl()
            };
            return update(params);
        } //- InsertExternalLink.run
        
    } //- class InsertExternalLink
    
    private static final String DEF_UPDATE_LINK_TYPE_QUERY =
        "update link_types set label = ?, baseURL = ? where label = ?";
    
    private class UpdateLinkType extends SqlUpdate {
        public UpdateLinkType(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_LINK_TYPE_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateLinkType.UpdateLinkType
        
        public int run(LinkType link, String oldLabel) {
            
            Object[] params = new Object[] {
                    link.getLabel(),
                    link.getBaseURL(),
                    oldLabel
            };
            return update(params);
        } //- UpdateLinkType.run
    } //- class UpdateLinkType
     
    private static final String DEF_DELETE_LINK_TYPE =
        "delete from link_types where label = ?";
    
    private class DeleteLinkType extends SqlUpdate {
        public DeleteLinkType(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DELETE_LINK_TYPE);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteCites.DeleteCites
        
        public int run(String label) {
            return update(label);
        } //- DeleteLinkType.run
    } //- class DeleteLinkType
    
    private static final String DEF_UPDATE_EXTERNAL_QUERY =
        "update elinks set url = ? where label = ? and paperid = ?";
    
    private class UpdateExternalLink extends SqlUpdate {
        public UpdateExternalLink(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_EXTERNAL_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateExternalLink.UpdateExternalLink
        
        public int run(ExternalLink link) {
            
            Object[] params = new Object[] {
                    link.getUrl(),
                    link.getLabel(),
                    link.getPaperID()
            };
            return update(params);
        } //- UpdateExternalLink.run
    } //- class UpdateExternalLink
    
    private static final String DEF_GET_PAPERS_NO_ELINKS_QUERY =
        "select id from papers where id not in (select paperid from elinks " +
        "where label = ?) and id > ? order by id limit ?";
    
    private class GetPapersNoELinkMapping extends MappingSqlQuery {
        public GetPapersNoELinkMapping(DataSource ds) {
            super(ds, DEF_GET_PAPERS_NO_ELINKS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- GetPapersNoELinkMapping.GetPapersNoELinkMapping
        
        protected String mapRow(ResultSet rs, int rownum) 
        throws SQLException {
            return rs.getString("id");
        } //- GetPapersNoELinkMapping.mapRow
        
        protected List<String> run(String label, String lastID, Long amount) {
            Object[] params = new Object[] {label, lastID, amount};
            return execute(params);
        } //- GetPapersNoELinkMapping.run
    } //- class GetExternalLinksMapping
    
    private static final String DEF_DELETE_EXTERNAL_LINK =
        "delete from elinks where paperid = ? and label = ?";
    
    private class DeleteExternalLink extends SqlUpdate {
        public DeleteExternalLink(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DELETE_EXTERNAL_LINK);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteExternalLink.DeleteExternalLink
        
        public int run(String doi, String label) {
            Object[] params = new Object[] {doi, label};
            return update(params);
        } //- DeleteExternalLink.run
    } //- class DeleteLinkType
    
} //- class ExternalLinkDAOImpl
