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
import java.util.Iterator;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import edu.psu.citeseerx.domain.Acknowledgment;

/**
 * AckDAO Implementation using MySQL as a persistent storage
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class AckDAOImpl extends JdbcDaoSupport implements AckDAO {

    private GetAcks getAcks;
    private GetAckSrc getAckSrc;
    private GetContexts getContexts;
    private InsertAck insertAck;
    private InsertAckSrc insertAckSrc;
    private InsertContext insertContext;
    private UpdateCluster updateCluster;
    private UpdateAck updateAck;
    private UpdateAckSrc updateAckSrc;
    private DeleteAcks deleteAcks;
    private DeleteAck deleteAck;
    private DeleteContexts deleteContexts;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getAcks = new GetAcks(getDataSource());
        getAckSrc = new GetAckSrc(getDataSource());
        getContexts = new GetContexts(getDataSource());
        insertAck = new InsertAck(getDataSource());
        insertAckSrc = new InsertAckSrc(getDataSource());
        insertContext = new InsertContext(getDataSource());
        updateCluster = new UpdateCluster(getDataSource());
        updateAck = new UpdateAck(getDataSource());
        updateAckSrc = new UpdateAckSrc(getDataSource());
        deleteAcks = new DeleteAcks(getDataSource());
        deleteAck = new DeleteAck(getDataSource());
        deleteContexts = new DeleteContexts(getDataSource());
    } //- initMappingSqlQueries
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#getAcknowledgments(java.lang.String, boolean, boolean)
     */
    public List<Acknowledgment> getAcknowledgments(String doi, boolean withContexts,
            boolean withSource) throws DataAccessException {

        List<Acknowledgment> acks = getAcks.run(doi);
        if (!withContexts && !withSource) return acks;
        
        for (Object o : acks) {
            Acknowledgment ack = (Acknowledgment)o; 
            if (withContexts) {
                List<String> contexts = getContexts.run(
                        new Long(Long.parseLong(
                                ack.getDatum(Acknowledgment.DOI_KEY))));
                for (Object c : contexts) {
                    ack.addContext((String)c);
                }
            }
            if (withSource) {
                Acknowledgment srcAck = getAckSrc.run(
                        new Long(Long.parseLong(
                                ack.getDatum(Acknowledgment.DOI_KEY))));
                ack.setSource(Acknowledgment.NAME_KEY,
                        srcAck.getSource(Acknowledgment.NAME_KEY));
                ack.setSource(Acknowledgment.ENT_TYPE_KEY,
                        srcAck.getSource(Acknowledgment.ENT_TYPE_KEY));
                ack.setSource(Acknowledgment.ACK_TYPE_KEY,
                        srcAck.getSource(Acknowledgment.ACK_TYPE_KEY));
            }
        }
        return acks;
        
    }  //- getAcknowledgments

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#getAckContexts(java.lang.Long)
     */
    public List<String> getAckContexts(Long ackID) throws DataAccessException {
        return getContexts.run(ackID);
    }  //- getContexts

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#insertAcknowledgment(java.lang.String, edu.psu.citeseerx.domain.Acknowledgment)
     */
    public void insertAcknowledgment(String doi, Acknowledgment ack)
    throws DataAccessException {
        insertAck.run(doi, ack);
        if (ack.hasSourceData()) {
            insertAckSrc.run(ack);
        }
    }  //- insertAcknowledgment
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#insertAckContexts(java.lang.Long, java.util.List)
     */
    public void insertAckContexts(Long ackID, List<String> contexts)
    throws DataAccessException {
        for (Iterator<String> it = contexts.iterator(); it.hasNext(); ) {
            insertContext.run(ackID, it.next());
        }
    }  //- insertContexts
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#setAckCluster(edu.psu.citeseerx.domain.Acknowledgment, java.lang.Long)
     */
    public void setAckCluster(Acknowledgment ack, Long clusterID)
    throws DataAccessException {
        updateCluster.run(ack, clusterID);
    }  //- setCluster

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#updateAcknowledgment(edu.psu.citeseerx.domain.Acknowledgment)
     */
    public void updateAcknowledgment(Acknowledgment ack)
    throws DataAccessException {
        updateAck.run(ack);
        if (ack.hasSourceData()) {
            updateAckSrc.run(ack);
        }
    }  //- -updateAcknowledgment

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#deleteAcknowledgments(java.lang.String)
     */
    public void deleteAcknowledgments(String doi) throws DataAccessException {
        deleteAcks.run(doi);
    }  //- deleteAcknowledgments
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#deleteAcknowledgment(java.lang.Long)
     */
    public void deleteAcknowledgment(Long ackid) throws DataAccessException {
        deleteAck.run(ackid);
    }  //- deleteAcknowledgment

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#deleteAckContexts(java.lang.Long)
     */
    public void deleteAckContexts(Long ackID) throws DataAccessException {
        deleteContexts.run(ackID);
    }  //- deleteContexts

    
    
    private static final String DEF_GET_ACKS_QUERY =
        "select id, cluster, name, entType, ackType from acknowledgments " +
        "where paperid=?";

    private class GetAcks extends MappingSqlQuery {
        
        public GetAcks(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_ACKS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetAcks.GetAcks
        
        public Acknowledgment mapRow(ResultSet rs, int rowNum)
        throws SQLException {
            Acknowledgment ack = new Acknowledgment();
            ack.setDatum(Acknowledgment.DOI_KEY, rs.getString("id"));
            ack.setClusterID(rs.getLong("cluster"));
            ack.setDatum(Acknowledgment.NAME_KEY, rs.getString("name"));
            ack.setDatum(Acknowledgment.ENT_TYPE_KEY, rs.getString("entType"));
            ack.setDatum(Acknowledgment.ACK_TYPE_KEY, rs.getString("ackType"));
            return ack;
        } //- GetAcks.mapRow
        
        public List<Acknowledgment> run(String doi) {
            return execute(doi);
        } //- GetAcks.run
    } //- class GetAcks
    
    
    private static final String DEF_GET_ACK_SRC_QUERY =
        "select name, entType, ackType from acknowledgments_versionShadow " +
        "where id=?";
    
    private class GetAckSrc extends MappingSqlQuery {
        
        public GetAckSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_ACK_SRC_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- GetAckSrc.GetAckSrc
        
        public Acknowledgment mapRow(ResultSet rs, int rowNum)
        throws SQLException {
            Acknowledgment ack = new Acknowledgment();
            ack.setSource(Acknowledgment.NAME_KEY,
                    rs.getString("name"));
            ack.setSource(Acknowledgment.ENT_TYPE_KEY,
                    rs.getString("entType"));
            ack.setSource(Acknowledgment.ACK_TYPE_KEY,
                    rs.getString("ackType"));
            return ack;
        } //- GetAckSrc.mapRow
        
        public Acknowledgment run(Long ackid) {
            List<Acknowledgment> list = execute(ackid);
            if (list.isEmpty()) {
                return null;
            } else {
                return (Acknowledgment)list.get(0);
            }
        } //- GetAckSrc.run
    } //- class GetAckSrc
    
    
    private static final String DEF_GET_CONTEXTS_QUERY =
        "select context from acknowledgmentContexts where ackid=?";

    private class GetContexts extends MappingSqlQuery {
        
        public GetContexts(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CONTEXTS_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- GetContexts.GetContexts
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        } //- GetContexts.mapRow
        
        public List<String> run(Long ackid) {
            return execute(ackid);
        } //- GetContexts.run
    } //- class GetContexts    

    
    /* cluster, name, entType, ackType, paperid */
    private static final String DEF_INSERT_ACK_QUERY =
        "insert into acknowledgments values (NULL, ?, ?, ?, ?, ?)";

    private class InsertAck extends SqlUpdate {
        
        public InsertAck(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_ACK_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            setReturnGeneratedKeys(true);
            compile();
        } //- InsertAck.InsertAck
        
        public int run(String doi, Acknowledgment ack) {
            Object[] params = new Object[] {
                    ack.getClusterID(),
                    ack.getDatum(Acknowledgment.NAME_KEY),
                    ack.getDatum(Acknowledgment.ENT_TYPE_KEY),
                    ack.getDatum(Acknowledgment.ACK_TYPE_KEY),
                    doi
            };
            KeyHolder holder = new GeneratedKeyHolder();
            int n = update(params, holder);
            ack.setDatum(Acknowledgment.DOI_KEY,
                    new Long(holder.getKey().longValue()).toString());
            return n;
        } //- InsertAck.run
    } //- class InsertAck
    
    
    /* id, name, entType, ackType */
    private static final String DEF_INSERT_ACK_SRC_QUERY =
        "insert into acknowledgments_versionShadow values (?, ?, ?, ?)";

    private class InsertAckSrc extends SqlUpdate {
        
        public InsertAckSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_ACK_SRC_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertAckSrc.InsertAckSrc

        public int run(Acknowledgment ack) {
            Object[] params = new Object[] {
                    Long.parseLong(ack.getDatum(Acknowledgment.DOI_KEY)),
                    ack.getSource(Acknowledgment.NAME_KEY),
                    ack.getSource(Acknowledgment.ENT_TYPE_KEY),
                    ack.getSource(Acknowledgment.ACK_TYPE_KEY)                    
            };
            return update(params);
        } //- InsertAckSrc.run
    } //- class InsertAckSrc
        
    
    /* ackid, context */
    private static final String DEF_INSERT_CONTEXT_QUERY =
        "insert into acknowledgmentContexts values (NULL, ?, ?)";

    private class InsertContext extends SqlUpdate {
        
        public InsertContext(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_CONTEXT_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BLOB));
            compile();
        } //- InsertContext.InsertContext
        
        public int run(Long ackid, String context) {
            Object[] params = new Object[] { ackid, context };
            return update(params);
        } //- InsertContext.run
    } //- class InsertContext

    
    private static final String DEF_UPDATE_CLUSTER_QUERY =
        "update acknowledgments set cluster=? where id=?";

    private class UpdateCluster extends SqlUpdate {
        
        public UpdateCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_CLUSTER_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- UpdateCluster.UpdateCluster
        
        public int run(Acknowledgment ack, Long clusterID) {
            Object[] params = new Object[] {
                    clusterID,
                    new Long(Long.parseLong(
                            ack.getDatum(Acknowledgment.DOI_KEY)))
            };
            int n = update(params);
            ack.setClusterID(clusterID);
            return n;
        } //- UpdateCluster.run
    } //- class UpdateCluster
    
        
    private static final String DEF_UPDATE_ACK_QUERY =
        "update acknowledgments set cluster=?, name=?, entType=?, ackType=?, " +
        "where id=?";
    
    private class UpdateAck extends SqlUpdate {
        
        public UpdateAck(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_ACK_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- UpdateAck.UpdateAck
        
        public int run(Acknowledgment ack) {
            Object[] params = new Object[] {
                    ack.getClusterID(),
                    ack.getDatum(Acknowledgment.NAME_KEY),
                    ack.getDatum(Acknowledgment.ENT_TYPE_KEY),
                    ack.getDatum(Acknowledgment.ACK_TYPE_KEY),
                    Long.parseLong(ack.getDatum(Acknowledgment.DOI_KEY))
            };
            return update(params);
        } //- UpdateAck.run
    } //- class UpdateAck
    
    private static final String DEF_UPDATE_ACK_SRC_QUERY =
        "update acknowledgments_versionShadow set name=?, entType=?, " +
        "ackType=? where id=?";

    private class UpdateAckSrc extends SqlUpdate {
        
        public UpdateAckSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_ACK_SRC_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- UpdateAckSrc.UpdateAckSrc

        public int run(Acknowledgment ack) {
            Object[] params = new Object[] {
                    ack.getSource(Acknowledgment.NAME_KEY),
                    ack.getSource(Acknowledgment.ENT_TYPE_KEY),
                    ack.getSource(Acknowledgment.ACK_TYPE_KEY),          
                    Long.parseLong(ack.getDatum(Acknowledgment.DOI_KEY))
            };
            return update(params);
        } //- UpdateAckSrc.run
    } //- class UpdateAckSrc
    
    private static final String DEF_DEL_ACKS_QUERY =
        "delete from acknowledgments where paperid=?";
    
    private class DeleteAcks extends SqlUpdate {
        
        public DeleteAcks(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_ACKS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteAcks.DeleteAcks
        
        public int run(String doi) {
            return update(doi);
        } //- DeleteAcks.run
    } //- class DeleteAcks
    
    
    private static final String DEF_DEL_ACK_QUERY =
        "delete from acknowledgments where id=?";
    
    private class DeleteAck extends SqlUpdate {
        
        public DeleteAck(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_ACK_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- DeleteAck.DeleteAck
        
        public int run(Long ackid) {
            return update(ackid);
        } //- DeleteAck.run
    } //- class DeleteAck
    
    
    private static final String DEF_DEL_CONTEXTS_QUERY =
        "delete from acknowledgmentContexts where ackid=?";
    
    private class DeleteContexts extends SqlUpdate {
        
        public DeleteContexts(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CONTEXTS_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- DeleteContexts.DeleteContexts
        
        public int run(Long ackid) {
            return update(ackid);
        } //- DeleteContexts.run
    } //- DeleteContexts
    
}  //- class AcknowledgmentDAOImpl
