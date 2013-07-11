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
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import edu.psu.citeseerx.domain.Author;

/**
 * AuthorDAO Implementation using MySQL as a persistent storage
 * @author Isaac Councill
 */
public class AuthorDAOImpl extends JdbcDaoSupport implements AuthorDAO {

    private GetAuthors getAuthors;
    private GetAuthorSrc getAuthorSrc;
    private InsertAuthor insertAuthor;
    private InsertAuthorSrc insertAuthorSrc;
    private UpdateAuthor updateAuthor;
    private UpdateAuthorSrc updateAuthorSrc;
    private UpdateCluster updateCluster;
    private DeleteAuthors deleteAuthors;
    private DeleteAuthor deleteAuthor;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getAuthors = new GetAuthors(getDataSource());
        getAuthorSrc = new GetAuthorSrc(getDataSource());
        insertAuthor = new InsertAuthor(getDataSource());
        insertAuthorSrc = new InsertAuthorSrc(getDataSource());
        updateAuthor = new UpdateAuthor(getDataSource());
        updateAuthorSrc = new UpdateAuthorSrc(getDataSource());
        updateCluster = new UpdateCluster(getDataSource());
        deleteAuthors = new DeleteAuthors(getDataSource());
        deleteAuthor = new DeleteAuthor(getDataSource());
    } //- initMappingSqlQueries
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#getDocAuthors(java.lang.String, boolean)
     */
    public List<Author> getDocAuthors(String doi, boolean getSource)
    throws DataAccessException {
        List<Author> authors = getAuthors.run(doi);
        if (getSource) {
            for (Object o : authors) {
                Author auth = (Author)o;
                Author srcAuth = getAuthorSrc.run(
                        Long.parseLong(auth.getDatum(Author.DOI_KEY)));
                if (srcAuth == null) {  // Just in case...
                    srcAuth = new Author();
                }
                auth.setSource(Author.NAME_KEY,
                        srcAuth.getSource(Author.NAME_KEY));
                auth.setSource(Author.AFFIL_KEY,
                        srcAuth.getSource(Author.AFFIL_KEY));
                auth.setSource(Author.ADDR_KEY,
                        srcAuth.getSource(Author.ADDR_KEY));
                auth.setSource(Author.EMAIL_KEY,
                        srcAuth.getSource(Author.EMAIL_KEY));
                auth.setSource(Author.ORD_KEY,
                        srcAuth.getSource(Author.ORD_KEY));
            }
        }
        return authors;
        
    }  //- getDocAuthors 

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#insertAuthor(java.lang.String, edu.psu.citeseerx.domain.Author)
     */
    public void insertAuthor(String doi, Author auth)
    throws DataAccessException {
        insertAuthor.run(doi, auth);
        if (auth.hasSourceData()) {
            insertAuthorSrc.run(auth);
        }
    }  //- insertAuthors

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#updateAuthor(edu.psu.citeseerx.domain.Author)
     */
    public void updateAuthor(Author auth) throws DataAccessException {
        updateAuthor.run(auth);
        if (auth.hasSourceData()) {
            updateAuthorSrc.run(auth);
        }
    }  //- updateAuthor
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#setAuthCluster(edu.psu.citeseerx.domain.Author, java.lang.Long)
     */
    public void setAuthCluster(Author auth, Long clusterID)
    throws DataAccessException {
        updateCluster.run(auth, clusterID);
    }  //- setCluster
    

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#deleteAuthors(java.lang.String)
     */
    public void deleteAuthors(String doi) throws DataAccessException {
        deleteAuthors.run(doi);
    }  //- deleteAuthors

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#deleteAuthor(java.lang.Long)
     */
    public void deleteAuthor(Long authorID)
    throws DataAccessException {
        deleteAuthor.run(authorID);
    }  //- deleteAuthor

    
    private static final String DEF_GET_AUTH_QUERY =
        "select id, cluster, name, affil, address, email, ord from authors " +
        "where paperid=? order by ord ASC";
    
    private class GetAuthors extends MappingSqlQuery {
        
        public GetAuthors(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_AUTH_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetAuthors.GetAuthors
        
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author auth = new Author();
            auth.setDatum(Author.DOI_KEY, rs.getString("id"));
            auth.setClusterID(rs.getLong("cluster"));
            auth.setDatum(Author.NAME_KEY, rs.getString("name"));
            auth.setDatum(Author.AFFIL_KEY, rs.getString("affil"));
            auth.setDatum(Author.ADDR_KEY, rs.getString("address"));
            auth.setDatum(Author.EMAIL_KEY, rs.getString("email"));
            auth.setDatum(Author.ORD_KEY, rs.getString("ord"));
            return auth;
        } //- GetAuthors.mapRow
        
        public List<Author> run(String doi) {
            return execute(doi);
        } //- GetAuthors.run
    } //- class GetAuthors
    
    
    private static final String DEF_GET_AUTH_SRC_QUERY =
        "select name, affil, address, email, ord from " +
        "authors_versionShadow where id=?";
    
    private class GetAuthorSrc extends MappingSqlQuery {
        
        public GetAuthorSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_AUTH_SRC_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- GetAuthorSrc.GetAuthorSrc
        
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author auth = new Author();
            auth.setSource(Author.NAME_KEY, rs.getString("name"));
            auth.setSource(Author.AFFIL_KEY, rs.getString("affil"));
            auth.setSource(Author.ADDR_KEY, rs.getString("address"));
            auth.setSource(Author.EMAIL_KEY, rs.getString("email"));
            auth.setSource(Author.ORD_KEY, rs.getString("ord"));
            return auth;
        } //- GetAuthorSrc.mapRow
        
        public Author run(Long authorid) {
            List<Author> list = execute(authorid);
            if (list.isEmpty()) {
                return null;
            } else {
                return (Author)list.get(0);
            } 
        } //- GetAuthorSrc.run
    } //- class GetAuthorSrc
        
    
    /* cluster, name, affil, address, email, ord, paperid */
    private static final String DEF_INSERT_AUTH_QUERY =
        "insert into authors values (NULL, ?, ?, ?, ?, ?, ?, ?)";
    
    private class InsertAuthor extends SqlUpdate {
        
        public InsertAuthor(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_AUTH_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            setReturnGeneratedKeys(true);
            compile();
        } //- InsertAuthor.InsertAuthor
        
        public int run(String doi, Author author) {
            Integer ord = null;
            try {
                ord = new Integer(Integer.parseInt(
                        author.getDatum(Author.ORD_KEY))).intValue();
            } catch (Exception e) { }
            
            Object[] params = new Object[] {
                    author.getClusterID(),
                    author.getDatum(Author.NAME_KEY),
                    author.getDatum(Author.AFFIL_KEY),
                    author.getDatum(Author.ADDR_KEY),
                    author.getDatum(Author.EMAIL_KEY),
                    ord, doi
            };
            KeyHolder holder = new GeneratedKeyHolder();
            int n = update(params, holder);
            author.setDatum(Author.DOI_KEY,
                    new Long(holder.getKey().longValue()).toString());
            return n;
        } //- InsertAuthor.run
    } //- class InsertAuthor
    
    
    /* id, name, affil, address, email, ord */
    private static final String DEF_INSERT_AUTH_SRC_QUERY =
        "insert into authors_versionShadow values (?, ?, ?, ?, ?, ?)";

    private class InsertAuthorSrc extends SqlUpdate {
        
        public InsertAuthorSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_AUTH_SRC_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertAuthorSrc.InsertAuthorSrc
        
        public int run(Author auth) {
            Object[] params = new Object[] {
                    Long.parseLong(auth.getDatum(Author.DOI_KEY)),                    
                    auth.getSource(Author.NAME_KEY),
                    auth.getSource(Author.AFFIL_KEY),
                    auth.getSource(Author.ADDR_KEY),
                    auth.getSource(Author.EMAIL_KEY),
                    auth.getSource(Author.ORD_KEY)
            };
            return update(params);
        } //- InsertAuthorSrc.run
    } //- class InsertAuthorSrc
        

    private static final String DEF_UPDATE_AUTH_QUERY =
        "update authors set cluster=?, name=?, affil=?, address=?, email=?, " +
        "ord=? where id=?";
    
    private class UpdateAuthor extends SqlUpdate {
        
        public UpdateAuthor(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_AUTH_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- UpdateAuthor.UpdateAuthor
        
        public int run(Author author) {
            Integer ord = null;
            try {
                ord = new Integer(Integer.parseInt(
                        author.getDatum(Author.ORD_KEY))).intValue();
            } catch (Exception e) { }
            
            Object[] params = new Object[] {
                    author.getClusterID(),
                    author.getDatum(Author.NAME_KEY),
                    author.getDatum(Author.AFFIL_KEY),
                    author.getDatum(Author.ADDR_KEY),
                    author.getDatum(Author.EMAIL_KEY),
                    ord,
                    Long.parseLong(author.getDatum(Author.DOI_KEY))
            };
            return update(params);
        } //- UpdateAuthor.run       
    } //- class UpdateAuthor
    
    
    private static final String DEF_UPDATE_AUTH_SRC_QUERY =
        "update authors_versionShadow set name=?, affil=?, address=?, " +
        "email=?, ord=? where id=?";
    
    private class UpdateAuthorSrc extends SqlUpdate {
        
        public UpdateAuthorSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_AUTH_SRC_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- UpdateAuthor.UpdateAuthor
        
        public int run(Author auth) {
            Object[] params = new Object[] {
                    auth.getSource(Author.NAME_KEY),
                    auth.getSource(Author.AFFIL_KEY),
                    auth.getSource(Author.ADDR_KEY),
                    auth.getSource(Author.EMAIL_KEY),
                    auth.getSource(Author.ORD_KEY),
                    Long.parseLong(auth.getDatum(Author.DOI_KEY)),                    
            };
            return update(params);
        } //- UpdateAuthor.run
    } //- class UpdateAuthor
    
        
    private static final String DEF_UPDATE_CLUSTER_QUERY =
        "update authors set cluster=? where id=?";

    private class UpdateCluster extends SqlUpdate {
        
        public UpdateCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_CLUSTER_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- UpdateCluster.UpdateCluster
        
        public int run(Author auth, Long clusterID) {
            Object[] params = new Object[] {
                    clusterID, Long.parseLong(auth.getDatum(Author.DOI_KEY))
            };
            return update(params);
        } //- UpdateCluster.run
    } //- class UpdateCluster
    
        
    private static final String DEF_DEL_AUTHORS_QUERY =
        "delete from authors where paperid=?";
    
    private class DeleteAuthors extends SqlUpdate {
        
        public DeleteAuthors(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_AUTHORS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteAuthors.DeleteAuthors
        
        public int run(String doi) {
            return update(doi);
        } //- DeleteAuthors.run
    } //- class DeleteAuthors
    
    
    private static final String DEF_DEL_AUTHOR_QUERY =
        "delete from authors where id=?";
        
    private class DeleteAuthor extends SqlUpdate {
        
        public DeleteAuthor(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_AUTHOR_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- DeleteAuthor.DeleteAuthor
        
        public int run(Long authorID) {
            return update(authorID);
        } //- DeleteAuthor.run
    } // class DeleteAuthor
    
}  //- class AuthorDAOImpl
