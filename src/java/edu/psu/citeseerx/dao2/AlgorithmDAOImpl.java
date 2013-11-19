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
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.context.ApplicationContextException;

import edu.psu.citeseerx.domain.Algorithm;

/**
 * AlgorithmDAO Implementation using MySQL as a persistent storage
 * @author Sumit Bhatia
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class AlgorithmDAOImpl extends JdbcDaoSupport implements AlgorithmDAO {
    
    protected GetAlgorithmByIdMapping getById;
    protected CountAlgorithmMapping countAlgorithm;
    protected InsertAlgorithm insertAlgorithm;
    protected GetAlgorithmLastIndexDate getAlgorithmLastIndex;
    protected GetAlgorithmByUpdateTime getListUpdatedAlgorithms;
    protected GetAlgorithmsByProxy getAlgorithmProxy;
    protected UpdateAlgorithmLastIndexDate updateAlgorithmIndexTime;
    
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    @Override
    protected void initDao() throws Exception {
        initMappingSqlQueries();
    } //- initDAO
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getById = new GetAlgorithmByIdMapping(getDataSource());
        countAlgorithm = new CountAlgorithmMapping(getDataSource());
        insertAlgorithm = new InsertAlgorithm(getDataSource());
        getAlgorithmLastIndex = new GetAlgorithmLastIndexDate(getDataSource());
        getListUpdatedAlgorithms = 
            new GetAlgorithmByUpdateTime(getDataSource());
        getAlgorithmProxy = new GetAlgorithmsByProxy(getDataSource());
        updateAlgorithmIndexTime = 
            new UpdateAlgorithmLastIndexDate(getDataSource());
    } //- initMappingSqlQueries

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#countAlgorithm()
     */
    public Integer countAlgorithm() throws DataAccessException {
        return countAlgorithm.run();
    } //- countAlgorithm

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#getAlgorithm(java.lang.String)
     */
    public Algorithm getAlgorithm(long id) throws DataAccessException {
        return getById.run(id);
    } //- getAlgorithm

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#getUpdatedAlgorithms(java.util.Date)
     */
    public List<Algorithm> getUpdatedAlgorithms(Date dt)
            throws DataAccessException {
        return getListUpdatedAlgorithms.run(dt);
    } //- getUpdatedAlgorithms

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#insertAlgorithm(edu.psu.citeseerx.domain.Algorithm)
     */
    public void insertAlgorithm(Algorithm oneAlgorithm)
            throws DataAccessException {
        insertAlgorithm.run(oneAlgorithm);
    } //- insertAlgorithm

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#lastAlgorithmIndexTime()
     */
    public Date lastAlgorithmIndexTime() throws DataAccessException {
        return getAlgorithmLastIndex.run();
    } //- lastAlgorithmIndexTime

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#updateAlgorithmIndexTime()
     */
    public void updateAlgorithmIndexTime() throws DataAccessException {
        updateAlgorithmIndexTime.run();
    } //- updateAlgorithmIndexTime
    
    private static final String DEF_GET_ALGORITHM_QUERY =
        "select id, paperid, proxyID, inDocID, caption, reftext, synopsis, " +
        "pageNum, ncites, year from eAlgorithms where id=?";
    
    private class GetAlgorithmByIdMapping extends MappingSqlQuery {

        public GetAlgorithmByIdMapping(DataSource ds) {
            super(ds, DEF_GET_ALGORITHM_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- GetAlgorithmByIdMapping.GetAlgorithmByIdMapping

        public Algorithm mapRow(ResultSet rs, int rowNum)
        throws SQLException {
            Algorithm algorithm = new Algorithm();
            algorithm.setID(rs.getLong("id"));
            algorithm.setPaperIDForAlgorithm(rs.getString("paperid"));
            algorithm.setProxyKey(rs.getString("proxyID"));
            algorithm.setAlgorithmID(rs.getString("inDocID"));
            algorithm.setCaption(rs.getString("caption"));
            algorithm.setAlgorithmReference(rs.getString("reftext"));
            algorithm.setSynopsis(rs.getString("synopsis"));
            algorithm.setAlgorithmOccursInPage(rs.getInt("pageNum"));
            algorithm.setNcites(rs.getInt("ncites"));
            algorithm.setPaperYear(rs.getInt("year"));

            return algorithm;
        } //- GetAcks.mapRow

        public Algorithm run(long id) {
            List<Algorithm> list = execute(id);
            if (list.isEmpty()) {
                return null;
            } else {
                return (Algorithm)list.get(0);
            }
        } //- GetAlgorithmByIdMapping.run 

    } //- class GetAlgorithmByIdMapping
    
    private static final String DEF_COUNT_ALGORITHM_QUERY =
        "select count(*) as total from eAlgorithms";

    private class CountAlgorithmMapping extends MappingSqlQuery {

        public CountAlgorithmMapping(DataSource ds) {
            super(ds, DEF_COUNT_ALGORITHM_QUERY);
            compile();
        } //- CountAlgorithmMapping.CountAlgorithmMapping

        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        } //- CountAlgorithmMapping.mapRow

         public Integer run() {
            List<Integer> rlist = execute();
            if(rlist != null) {
                if(rlist.isEmpty()) {
                    return -1;
                }
                else {
                    return (Integer)rlist.get(0);
                }
            }
            else {
                return -1;
            }
        } //- CountAlgorithmMapping.run
    } //- class CountAlgorithmMapping
    
    private static final String DEF_INSERT_ALGORITHM =
        "INSERT INTO eAlgorithms (id, paperid, proxyID, inDocID, caption," +
        "synopsis, reftext, pageNum, ncites, year, updateTime) VALUES " +
        "(NULL,?,?,?,?,?,?,?,?,?,NOW());";

    private class InsertAlgorithm extends SqlUpdate {
        public InsertAlgorithm(DataSource ds) {
                setDataSource(ds);
            setSql(DEF_INSERT_ALGORITHM);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.INTEGER));
            setReturnGeneratedKeys(true);
            compile();
        } //- InsertAlgorithm.InsertAlgorithm

        public int run(Algorithm aobj) {

            Object[] params = new Object [] {
                    aobj.getPaperIDForAlgorithm(),
                    aobj.getProxyKey(),
                    aobj.getAlgorithmID(),
                    aobj.getCaption(),
                    aobj.getSynopsis(),
                    aobj.getAlgorithmReference(),
                    aobj.getAlgorithmOccursInPage(),
                    aobj.getNcites(),
                    aobj.getPaperYear()
            };
            KeyHolder hold = new GeneratedKeyHolder();
            int n = update(params,hold);
            return n;
        }//- InsertAlgorithm.run

    }//- class InsertAlgorithm
    
    private static final String DEF_GET_ALGORITHM_BYPROXY =
        "SELECT id, paperid, proxyID, inDocID, caption,synopsis, " +
        "reftext , pageNum, ncites, year from eAlgorithms WHERE proxyID = ?";

    private class GetAlgorithmsByProxy extends MappingSqlQuery {

        public GetAlgorithmsByProxy(DataSource ds) {
            super(ds, DEF_GET_ALGORITHM_BYPROXY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetAlgorithmsByProxy.GetAlgorithmsByProxy

        public Algorithm mapRow(ResultSet rs, int rowNum)
        throws SQLException {
            Algorithm algorithm = new Algorithm();
            algorithm.setID(rs.getLong("id"));
            algorithm.setPaperIDForAlgorithm(rs.getString("paperid"));
            algorithm.setProxyKey(rs.getString("proxyID"));
            algorithm.setAlgorithmID(rs.getString("inDocID"));
            algorithm.setCaption(rs.getString("caption"));
            algorithm.setSynopsis(rs.getString("synopsys"));
            algorithm.setAlgorithmReference(rs.getString("reftext"));
            algorithm.setAlgorithmOccursInPage(rs.getInt("pageNum"));
            algorithm.setNcites(rs.getInt("ncites"));
            algorithm.setPaperYear(rs.getInt("year"));
            return algorithm;
        } //- GetAlgorithmsByProxy.mapRow

        public List<Algorithm> run(String id) {
                List<Algorithm> algorithmlist = execute(id);
            if (algorithmlist.isEmpty()) {
                return null;
            } else {
                return algorithmlist;
            }
        } //- GetAlgorithmsByProxy.run
    } //- class GetAlgorithmsByProxy

    private static final String DEF_GET_ALGORITHM_BYUPDATE =
        "SELECT id, paperid, proxyID, inDocID, caption,synopsis, " +
        "reftext , pageNum, ncites, year FROM eAlgorithms WHERE " +
        "updateTime > ?";

    private class GetAlgorithmByUpdateTime extends MappingSqlQuery {

        public GetAlgorithmByUpdateTime(DataSource ds) {
            super(ds, DEF_GET_ALGORITHM_BYUPDATE);
            declareParameter(new SqlParameter(Types.DATE));
            compile();
        } //- GetAlgorithmByUpdateTime.GetAlgorithmByUpdateTime

        public Algorithm mapRow(ResultSet rs, int rowNum)
        throws SQLException {
            Algorithm algorithm = new Algorithm();
            algorithm.setID(rs.getLong("id"));
            algorithm.setPaperIDForAlgorithm(rs.getString("paperid"));
            algorithm.setProxyKey(rs.getString("proxyID"));
            algorithm.setAlgorithmID(rs.getString("inDocID"));
            algorithm.setCaption(rs.getString("caption"));
            algorithm.setSynopsis(rs.getString("synopsis"));
            algorithm.setAlgorithmReference(rs.getString("reftext"));
            algorithm.setAlgorithmOccursInPage(rs.getInt("pageNum"));
            algorithm.setNcites(rs.getInt("ncites"));
            algorithm.setPaperYear(rs.getInt("year"));

            return algorithm;

        } //- GetAlgorithmByUpdateTime.mapRow

        public List<Algorithm> run(java.util.Date dt) {
                Object [] params = new Object [] {
                                dt
                };
            List<Algorithm> algorithmlist = execute(params);
            if (algorithmlist.isEmpty()) {
                return null;
            } else {
                return algorithmlist;
            }
        } //- GetAlgorithmByUpdateTime.run

    } //- class GetAlgorithmByUpdateTime
    
    private static final String DEF_UPDATE_LAST_INDEX_DATE =
        "update myciteseerx.indexTime set lastIndex = now() where " +
        "param =\"algorithmIndex\"";
    
    public class UpdateAlgorithmLastIndexDate extends SqlUpdate {

        public UpdateAlgorithmLastIndexDate(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_UPDATE_LAST_INDEX_DATE);
            compile();
        } //- UpdateAlgorithmLastIndexDate.UpdateAlgorithmLastIndexDate

        public int run() {
            return update();
        } //- UpdateAlgorithmLastIndexDate.run
    } //- UpdateAlgorithmLastIndexDate

    private static final String DEF_GET_LAST_INDEX_DATE =
        "select lastIndex from myciteseerx.indexTime where " +
        "param =\"algorithmIndex\"";
    public class GetAlgorithmLastIndexDate extends MappingSqlQuery {

        public GetAlgorithmLastIndexDate(DataSource ds) {
                super(ds,DEF_GET_LAST_INDEX_DATE);
                compile();
        } //- GetAlgorithmLastIndexDate.GetAlgorithmLastIndexDate
        
        public Date mapRow(ResultSet rs, int rowNum)
        throws SQLException {
                return rs.getDate(0);
        } //- GetAlgorithmLastIndexDate.mapRow

        public java.sql.Date run() {
            List<java.sql.Date> dList = execute();
            if(dList.isEmpty()) {
                return null;
            }
            else {
                return dList.get(0);
            } 
        } //- GetAlgorithmLastIndexDate.run

    } //- GetAlgorithmLastIndexDate
    
} //- class AlgorithmDAOImpl
