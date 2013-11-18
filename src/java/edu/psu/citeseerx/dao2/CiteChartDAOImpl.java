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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

/**
 * CiteChartDAO Implementation using MySQL as a persistent storage
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CiteChartDAOImpl extends JdbcDaoSupport implements CiteChartDAO {

    private GetNcites getNcites;
    private GetUpdate getUpdate;
    private InsertUpdate insertUpdate;
    private UpdateUpdate updateUpdate;
    private GetChartData getChartData;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getNcites = new GetNcites(getDataSource());
        getUpdate = new GetUpdate(getDataSource());
        insertUpdate = new InsertUpdate(getDataSource());
        updateUpdate = new UpdateUpdate(getDataSource());
        getChartData = new GetChartData(getDataSource());
    } //- initMappingSqlQueries
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CiteChartDAO#checkChartUpdateRequired(java.lang.String)
     */
    public boolean checkChartUpdateRequired(String doi)
    throws DataAccessException {
        int lastNcites = getUpdate.run(doi);
        if (lastNcites < 0) {
            return true;
        }
        int ncites = getNcites.run(doi);
        if (ncites < 0) {
            return false;
        }
        if (ncites != lastNcites) {
            return true;
        } else {
            return false;
        }
        
    }  //- checkChartUpdateRequired

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CiteChartDAO#insertChartUpdate(java.lang.String, int)
     */
    public void insertChartUpdate(String doi, int lastNcites, String chartData)
    throws DataAccessException {
        boolean isNew = true;
        int update = getUpdate.run(doi);
        if (update >= 0) {
            isNew = false;
        }
        if (isNew) {
            insertUpdate.run(doi, lastNcites, chartData);
        } else {
            updateUpdate.run(doi, lastNcites, chartData);
        }
    }  //- insertChartUpdate
    

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CiteChartDAO#getCiteChartData(java.lang.String)
     */
    public String getCiteChartData(String doi) throws DataAccessException {
        return getChartData.run(doi);
    } //- getCiteChartData

    private static final String DEF_GET_NCITES_QUERY =
        "select ncites from papers where id=?";
    
    private class GetNcites extends MappingSqlQuery {
        
        public GetNcites(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_NCITES_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetNcites.GetNcites
        
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        } //- GetNcites.mapRow
        
        public int run(String doi) {
            List<Integer> list = execute(doi);
            if (list.isEmpty()) {
                return -1;
            } else {
                return ((Integer)list.get(0)).intValue();
            }
        } //- GetNcites.run
    } //- class GetNcites
    
    
    private static final String DEF_GET_UPDATE_QUERY =
        "select lastNcites from citecharts where id=?";
    
    private class GetUpdate extends MappingSqlQuery {
        
        public GetUpdate(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_UPDATE_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetUpdate.GetUpdate
        
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        } //- GetUpdate.mapRow
        
        public int run(String doi) {
            List<Integer> list = execute(doi);
            if (list.isEmpty()) {
                return -1;
            } else {
                return ((Integer)list.get(0)).intValue();
            }
        } //- GetUpdate.run
    } //- class GetUpdate
    
    
    /* doi, lastNcites */
    private static final String DEF_INS_UPDATE_STMT =
        "insert into citecharts values (?, ?, ?)";
    
    private class InsertUpdate extends SqlUpdate {
        
        public InsertUpdate(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INS_UPDATE_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.BLOB));
            compile();
        } //- InsertUpdate.InsertUpdate
        
        public int run(String doi, int lastNcites, String chartData) {
            Object[] params = new Object[] { 
                    doi, new Integer(lastNcites), chartData };
            return update(params);
        } //- InsertUpdate.run
    } //- class InsertUpdate
    
    
    private static final String DEF_UPDATE_UPDATE_STMT =
        "update citecharts set lastNcites=?, citechartData=? where id=?";
    
    private class UpdateUpdate extends SqlUpdate {
        
        public UpdateUpdate(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_UPDATE_STMT);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateUpdate.UpdateUpdate
        
        public int run(String doi, int lastNcites, String chartData) {
            Object[] params = new Object[] { 
                    new Integer(lastNcites), chartData, doi };
            return update(params);
        } //- UpdateUpdate.run
    } //- class UpdateUpdate
    
    private static final String DEF_GET_CITECHARTDATA_QUERY =
        "select citechartData from citecharts where id = ?";
    
    private class GetChartData extends MappingSqlQuery {
        
        public GetChartData(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITECHARTDATA_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetChartData.GetChartData
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("citechartData");
        } //- GetChartData.mapRow
        
        public String run(String doi) {
            List<String> list = execute(doi);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        } //- GetChartData.run
    } //- class GetChartData
    
}  //- class CharUpdateDAOImpl
