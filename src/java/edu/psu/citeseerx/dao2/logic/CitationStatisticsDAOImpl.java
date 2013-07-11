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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;

import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.AuthorStatContainer;


public class CitationStatisticsDAOImpl extends JdbcDaoSupport
implements CitationStatisticsDAO {

    private GetClusters getClusters;
    private GetInCol getInCol;
    private GetClustersByYear getClustersByYear;
    private GetInColByYear getInColByYear;
    private GetAuthorStats getAuthorStats;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getClusters = new GetClusters(getDataSource());
        getInCol = new GetInCol(getDataSource());
        getClustersByYear = new GetClustersByYear(getDataSource());
        getInColByYear = new GetInColByYear(getDataSource());
        getAuthorStats = new GetAuthorStats(getDataSource());
    }
    
    
    public List<ThinDoc> getMostCitedArticles(int amount, 
            boolean includeCitations) throws DataAccessException {
        if (includeCitations) {
            return getClusters.run(amount);
        } else {
            return getInCol.run(amount);
        }
    }

    public List<ThinDoc> getMostCitedArticlesByYear(int amount, int year,
            boolean includeCitations) throws DataAccessException {
        if (includeCitations) {
            return getClustersByYear.run(year, amount);
        } else {
            return getInColByYear.run(year, amount);
        }
    }
    
    public List<AuthorStatContainer> getAuthorStats(long startingID, int amount)
    throws DataAccessException {
        return getAuthorStats.run(startingID, amount);
    }
    
    
    private static final String DEF_GET_CLUSTERS_QUERY =
        "select id, size, incollection, cauth, ctitle, cvenue, cyear " +
        "from clusters order by size desc limit ?";

    private class GetClusters extends MappingSqlQuery {

        public GetClusters(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CLUSTERS_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapThinDoc(rs);
        }
        
        public List<ThinDoc> run(int amount) {
            return execute(amount);
        }
    }
    
    
    private static final String DEF_GET_INCOL_QUERY =
        "select id, size, incollection, cauth, ctitle, cvenue, cyear " +
        "from clusters where incollection=1 order by size desc limit ?";

    private class GetInCol extends MappingSqlQuery {

        public GetInCol(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_INCOL_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapThinDoc(rs);
        }
        
        public List<ThinDoc> run(int amount) {
            return execute(amount);
        }
    }
    
    
    private static final String DEF_GET_CLUSTERS_BY_YEAR_QUERY =
        "select id, size, incollection, cauth, ctitle, cvenue, cyear " +
        "from clusters where cyear=? order by size desc limit ?";
    
    private class GetClustersByYear extends MappingSqlQuery {
        
        public GetClustersByYear(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CLUSTERS_BY_YEAR_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapThinDoc(rs);
        }
        
        public List<ThinDoc> run(int year, int amount) {
            Object[] params = new Object[] {
                    new Integer(year), new Integer(amount)
            };
            return execute(params);
        }
    }
    
    
    private static final String DEF_GET_INCOL_BY_YEAR_QUERY =
        "select id, size, incollection, cauth, ctitle, cvenue, cyear " +
        "from clusters where cyear=? and incollection=1 " +
        "order by size desc limit ?";
    
    private class GetInColByYear extends MappingSqlQuery {
        
        public GetInColByYear(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_INCOL_BY_YEAR_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public ThinDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapThinDoc(rs);
        }
        
        public List<ThinDoc> run(int year, int amount) {
            Object[] params = new Object[] {
                    new Integer(year), new Integer(amount)
            };
            return execute(params);
        }
    }
    
    
    private static ThinDoc mapThinDoc(ResultSet rs) throws SQLException {
        ThinDoc doc = new ThinDoc();
        doc.setCluster(rs.getLong(1));
        doc.setNcites(rs.getInt(2));
        doc.setInCollection(rs.getBoolean(3));
        doc.setAuthors(rs.getString(4));
        doc.setTitle(rs.getString(5));
        doc.setVenue(rs.getString(6));
        doc.setYear(rs.getInt(7));
        return doc;
    }
    
    private static final String DEF_GET_AUTHORS_QUERY =
        "select id, cauth, size, selfCites from clusters where id>=? " +
        "order by id asc limit ?";
    
    private class GetAuthorStats extends MappingSqlQuery {
        
        public GetAuthorStats(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_AUTHORS_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public AuthorStatContainer mapRow(ResultSet rs, int rowNum)
        throws SQLException {
            long id = rs.getLong(1);
            String authors = rs.getString(2);
            int size = rs.getInt(3);
            int self = rs.getInt(4);
            AuthorStatContainer ac = new AuthorStatContainer(authors,size-self);
            ac.setCluster(id);
            return ac;
        }
        
        public List<AuthorStatContainer> run(long startingID, int amount) {
            Object[] params = new Object[] {
                    new Long(startingID), new Integer(amount)
            };
            return execute(params);
        }
    }

}  //- class CitationStatisticsDAOImpl
