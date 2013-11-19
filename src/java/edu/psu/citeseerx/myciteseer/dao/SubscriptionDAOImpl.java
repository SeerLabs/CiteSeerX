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
package edu.psu.citeseerx.myciteseer.dao;

import edu.psu.citeseerx.myciteseer.domain.Account;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import java.util.List;

/**
 * SubscriptionDAO implementation using MYSQL as a persistent storage.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class SubscriptionDAOImpl extends JdbcDaoSupport
implements SubscriptionDAO {

    private InsertMonitor insertMonitor;
    private DeleteMonitor deleteMonitor;
    private GetMonitorMapping getMonitor;
    private GetMonitorsMapping getMonitors;
    private GetUsersMonitoring getUsersMonitoring;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    protected void initMappingSqlQueries() {
        insertMonitor = new InsertMonitor(this.getDataSource());
        deleteMonitor = new DeleteMonitor(this.getDataSource());
        getMonitor = new GetMonitorMapping(this.getDataSource());
        getMonitors = new GetMonitorsMapping(this.getDataSource());
        getUsersMonitoring = new GetUsersMonitoring(this.getDataSource());
    } //- initMappingSqlQueries
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO#addMonitor(edu.psu.citeseerx.myciteseer.domain.Account, java.lang.String)
     */
    public void addMonitor(Account account, String paperid)
    throws DataAccessException {
        List<Long> ids = getMonitor.run(account, paperid);
        if (ids.isEmpty()) {
            insertMonitor.run(account, paperid);
        }
    } //- addMonitor
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO#deleteMonitor(edu.psu.citeseerx.myciteseer.domain.Account, java.lang.String)
     */
    public void deleteMonitor(Account account, String paperid)
    throws DataAccessException {
        deleteMonitor.run(account, paperid);
    } //- deleteMonitor
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO#getMonitors(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public List<String> getMonitors(Account account)
    throws DataAccessException {
        return getMonitors.run(account);
    } //- getMonitors
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.SubscriptionDAO#getUsersMonitoring(java.lang.String)
     */
    public List<String> getUsersMonitoring(String paperid) {
        return getUsersMonitoring.run(paperid);
    } //- getUsersMonitoring
    
    private static final String DEF_INS_MONITOR_STMT =
        "insert into monitors values (NULL, ?, ?)";
    
    protected class InsertMonitor extends SqlUpdate {
        public InsertMonitor(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INS_MONITOR_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertMonitor.InsertMonitor
        
        public int run(Account account, String paperid) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    paperid };
            return update(params);
        } //- InsertMonitor.run
        
    }  //- class InsertMonitor
    
    
    private static final String DEF_GET_MONITOR_QUERY =
        "select id from monitors where userid=? and paperid=?";
    
    protected class GetMonitorMapping extends MappingSqlQuery {
        
        public GetMonitorMapping(DataSource ds) {
            super(ds, DEF_GET_MONITOR_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetMonitorMapping.GetMonitorMapping
        
        protected Long mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getLong("id");
        } //- GetMonitorMapping.mapRow

        public List<Long> run(Account account, String paperid) {
            Object[] params = new Object[] { account.getUsername(), paperid };
            return execute(params);
        } //- GetMonitorMapping.run
        
    }  //- class GetMonitorMapping

    
    private static final String DEF_DEL_MONITOR_STMT =
        "delete from monitors where userid=? and paperid=?";
    
    protected class DeleteMonitor extends SqlUpdate {
        public DeleteMonitor(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_MONITOR_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteMonitor.DeleteMonitor
        
        public int run(Account account, String paperid) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    paperid };
            return update(params);
        } //- DeleteMonitor.run
        
    }  //- class DeleteMonitor
    
    
    private static final String DEF_GET_MONITORS_QUERY =
        "select paperid from monitors where userid=? order by paperid";
    
    protected class GetMonitorsMapping extends MappingSqlQuery {
        
        public GetMonitorsMapping(DataSource ds) {
            super(ds, DEF_GET_MONITORS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetMonitorsMapping.GetMonitorsMapping
        
        protected String mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getString("paperid");
        } //- GetMonitorsMapping.mapRow

        public List<String> run(Account account) {
            return execute(account.getUsername());
        } //- GetMonitorsMapping.run        

    }  //- class GetMonitorsMapping
    
    
    private static final String DEF_GET_USERS_MONITORING_QUERY =
        "select userid from monitors where paperid=?";
    
    protected class GetUsersMonitoring extends MappingSqlQuery {
        
        public GetUsersMonitoring(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_GET_USERS_MONITORING_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetUsersMonitoring.GetUsersMonitoring
        
        protected String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("userid");
        } //- GetUsersMonitoring.mapRow
        
        public List<String> run(String paperid) {
            return execute(paperid);
        } //- GetUsersMonitoring.run
        
    }  //- class GetUsersMonitoring

    
}  //- class SubscriptionDAOImpl
