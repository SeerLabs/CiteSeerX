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

import edu.psu.citeseerx.domain.Hub;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 * CitationDAO Spring-JDBC Implementation using MySQL as a persistent storage
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class HubDAOImpl extends JdbcDaoSupport implements HubDAO {

    private GetHubs getHubs;
    private GetHubsForUrl getHubsForUrl;
    private GetHub getHub;
    private InsertHub insertHub;
    private UpdateHub updateHub;
    private InsertHubMapping insertHubMapping;
    private GetHubID getHubID;
    private GetUrlID getUrlID;
    private InsertUrl insertUrl;
    private GetUrls getUrls;
    private GetPaperIDSForHub getPaperIDSForHub;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getHubs = new GetHubs(getDataSource());
        getHubsForUrl = new GetHubsForUrl(getDataSource());
        getHub = new GetHub(getDataSource());
        insertHub = new InsertHub(getDataSource());
        updateHub = new UpdateHub(getDataSource());
        insertHubMapping = new InsertHubMapping(getDataSource());
        getHubID = new GetHubID(getDataSource());
        getUrlID = new GetUrlID(getDataSource());
        insertUrl = new InsertUrl(getDataSource());
        getUrls = new GetUrls(getDataSource());
        getPaperIDSForHub = new GetPaperIDSForHub(getDataSource());
    } //- initMappingSqlQueries
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getHubs(java.lang.String)
     */
    public List<Hub> getHubs(String doi) throws DataAccessException {
        return getHubs.run(doi);
    }  //- getHubs
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getHubsForUrl(java.lang.String)
     */
    public List<Hub> getHubsForUrl(String url) throws DataAccessException {
        return getHubsForUrl.run(url);
    } //- getHubsForUrl

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getHub(java.lang.String)
     */
    public Hub getHub(String url) throws DataAccessException {
        return getHub.run(url);
    }  //- getHub

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#insertHub(edu.psu.citeseerx.domain.Hub)
     */
    public long insertHub(Hub hub) throws DataAccessException {
        return insertHub.run(hub);
    }  //- insertHub

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#insertHubMapping(long, long)
     */
    public void insertHubMapping(long urlID, long hubID)
    throws DataAccessException {
        insertHubMapping.run(urlID, hubID);
    }  //- insertHubMapping

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#updateHub(edu.psu.citeseerx.domain.Hub)
     */
    public void updateHub(Hub hub) throws DataAccessException {
        updateHub.run(hub);
    }  //- updateHub

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#addHubMapping(edu.psu.citeseerx.domain.Hub, java.lang.String, java.lang.String)
     */
    public void addHubMapping(Hub hub, String url, String doi)
    throws DataAccessException {
        long hid = getHubID.run(hub.getUrl());
        if (hid <= 0) {
            hid = insertHub.run(hub);
        }
        long uid = getUrlID.run(url);
        if (uid <= 0) {
            uid = insertUrl.run(doi, url);
        }
        insertHubMapping.run(uid, hid);
        
    }  //- addHubMapping
    
    
    public List<String> getUrls(String doi) throws DataAccessException {
        return getUrls.run(doi);
    } //- getUrls

    
    public long insertUrl(String doi, String url) throws DataAccessException {
        return insertUrl.run(doi, url);
    } //- insertUrl
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getPaperIdsFromHubUrl(java.lang.String)
     */
    public List<String> getPaperIdsFromHubUrl(String hubUrl)
            throws DataAccessException {
        List <String> paperIDs = null;
        
        // Get the hubId based on the URL.
        long hid = getHubID.run(hubUrl);
        if (hid < 0) {paperIDs = null;}
        else{
            paperIDs = getPaperIDSForHub.run(hid);
            if (paperIDs.isEmpty()){paperIDs = null;}
        }
        return paperIDs;
    }



    private static final String DEF_GET_HUBS_QUERY =
        "select hubUrls.url, lastCrawl, hubUrls.repositoryID from hubUrls, " +
        "hubMap, urls, papers where papers.id=? and urls.paperid=papers.id " +
        "and urls.id=hubMap.urlid and hubUrls.id=hubMap.hubid";
    
    private class GetHubs extends MappingSqlQuery {
        
        public GetHubs(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_HUBS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetHubs.GetHubs
        
        public Hub mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hub hub = new Hub();
            hub.setUrl(rs.getString(1));
            hub.setLastCrawled(
                    new java.util.Date(rs.getTimestamp(2).getTime()));
            hub.setRepID(rs.getString(3));
            return hub;
        } //- GetHubs.mapRow
        
        public List<Hub> run(String doi) {
            return execute(doi);
        } //- GetHubs.run
        
    }  //- class GetHubs
        

    private static final String DEF_GET_HUBS_FOR_URL_QUERY =
        "select hubUrls.url, lastCrawl, repositoryID from hubUrls, hubMap, " +
        "urls where urls.url=? and urls.id=hubMap.urlid and " +
        "hubUrls.id=hubMap.hubid";
    
    private class GetHubsForUrl extends MappingSqlQuery {
        
        public GetHubsForUrl(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_HUBS_FOR_URL_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetHubsForUrl.GetHubsForUrl
        
        public Hub mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hub hub = new Hub();
            hub.setUrl(rs.getString(1));
            hub.setLastCrawled(
                    new java.util.Date(rs.getTimestamp(2).getTime()));
            hub.setRepID(rs.getString(3));
            return hub;
        } //- GetHubsForUrl.mapRow
        
        public List<Hub> run(String url) {
            return execute(url);
        } //- GetHubsForUrl.run
        
    }  //- class GetHubsForUrl
    
    
    private static final String DEF_GET_HUB_QUERY =
        "select lastCrawl, repositoryID from hubUrls where url=?";
    
    private class GetHub extends MappingSqlQuery {
        
        public GetHub(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_HUB_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetHub.GetHub
        
        public Hub mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hub hub = new Hub();
            hub.setLastCrawled(
                    new java.util.Date(rs.getTimestamp(1).getTime()));
            hub.setRepID(rs.getString(2));
            return hub;
        } //- GetHub.mapRow
        
        public Hub run(String url) {
            List<Hub> list = execute(url);
            if (list.isEmpty()) {
                return null;
            } else {
                Hub hub = (Hub)list.get(0);
                hub.setUrl(url);
                return hub;
            }
        } //- GetHub.run
        
    }  //- class GetHub

    
    /* url, lastCrawled, repid */
    private static final String DEF_INSERT_HUB_STMT =
        "insert into hubUrls values (NULL, ?, ?, ?)";
    
    private class InsertHub extends SqlUpdate {
        
        public InsertHub(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_HUB_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.VARCHAR));
            setReturnGeneratedKeys(true);
            compile();
        } //- InsertHub.InsertHub
        
        public long run(Hub hub) {
            Object[] params = new Object[] {
                    hub.getUrl(),
                    new Timestamp(hub.getLastCrawled().getTime()),
                    hub.getRepID()
            };
            KeyHolder holder = new GeneratedKeyHolder();
            update(params, holder);
            return holder.getKey().longValue();
        } //- InsertHub.run
        
    }  //- class InsertHub
        
    
    private static final String DEF_UPDATE_HUB_STMT =
        "update hubUrls set lastCrawl=?, repositoryID=? where url=?";
    
    private class UpdateHub extends SqlUpdate {
        
        public UpdateHub(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_HUB_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateHub.UpdateHub
        
        public int run(Hub hub) {
            Object[] params = new Object[] {
                    new Timestamp(hub.getLastCrawled().getTime()),
                    hub.getRepID(),
                    hub.getUrl()
            };
            return update(params);
        } //- UpdateHub.run
        
    }  //- class UpdateHub
        
    
    /* paperid, hubid */
    private static final String DEF_INSERT_HUBMAP_STMT =
        "insert into hubMap values (NULL, ?, ?)";
    
    private class InsertHubMapping extends SqlUpdate {
        
        public InsertHubMapping(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_HUBMAP_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- InsertHubMapping.InsertHubMapping
        
        public int run(long urlID, long hubID) {
            Object[] params = new Object[] { new Long(urlID), new Long(hubID) };
            return update(params);
        } //- InsertHubMapping.run
        
    }  //- class InsertHubMapping
        

    private static final String DEF_GET_HUBID_QUERY =
        "select id from hubUrls where url=?";
    
    private class GetHubID extends MappingSqlQuery {
        
        public GetHubID(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_HUBID_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetHubID.GetHubID
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        } //- GetHubID.mapRow
        
        public long run(String url) {
            List<Long> list = execute(url);
            if (list.isEmpty()) {
                return 0;
            } else {
                return (Long)list.get(0);
            }
        } //- GetHubID.run
        
    }  //- class GetHubID
    
    
    private static final String DEF_GET_URLID_QUERY =
        "select id from urls where url=?";
    
    private class GetUrlID extends MappingSqlQuery {
        
        public GetUrlID(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_URLID_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetUrlID.GetUrlID
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        } //- GetUrlID.mapRow
        
        public long run(String url) {
            List<Long> list = execute(url);
            if (list.isEmpty()) {
                return 0;
            } else {
                return (Long)list.get(0);
            }
        } //- GetUrlID.run
        
    }  //- class GetUrlID
    
    
    /* url, paperid */
    private static final String DEF_INS_URL_STMT =
        "insert into urls values (NULL, ?, ?)";
    
    private class InsertUrl extends SqlUpdate {
        
        public InsertUrl(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INS_URL_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            setReturnGeneratedKeys(true);
            compile();
        } //- InsertUrl.InsertUrl
        
        public long run(String doi, String url) {
            Object[] params = new Object[] { url, doi };
            KeyHolder holder = new GeneratedKeyHolder();
            update(params, holder);
            return holder.getKey().longValue();
        } //- InsertUrl.run
        
    }  //- class InsertUrl
    
    
    private static final String DEF_GET_URLS_STMT =
        "select url from urls where paperid=?";
    
    private class GetUrls extends MappingSqlQuery {
        
        public GetUrls(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_URLS_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetUrls.GetUrls
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("url");
        } //- GetUrls.mapRow
        
        public List<String> run(String doi) {
            return execute(doi);
        } //- GetUrls.run
        
    }  //- class GetUrls
    
    private static String DEF_GET_PAPER_IDS_FOR_HUB_STMT = "select " +
    		"urls.paperid from hubUrls, hubMap, urls where " +
    		"hubUrls.id = hubMap.hubid and hubMap.urlid = urls.id " +
    		"and hubUrls.id = ? order by urls.paperid";
    
    private class GetPaperIDSForHub extends MappingSqlQuery {
        public GetPaperIDSForHub(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_PAPER_IDS_FOR_HUB_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- GetPaperIDSForHub.GetPaperIDSForHub
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("paperid");
        } //- GetPaperIDSForHub.mapRow
        
        public List<String> run(long hubUrlId) {
            return execute(hubUrlId);
        } //- GetPaperIDSForHub.run
    } //- class GetPaperIDSForHub
        
}  //- class HubDAOImpl
