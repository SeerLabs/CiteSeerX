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
import java.util.ArrayList;
import java.lang.StringBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;

import edu.psu.citeseerx.domain.UniqueAuthor;
import edu.psu.citeseerx.domain.ThinDoc;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
//import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
//import org.springframework.jdbc.core.simple.ParameterizedSingleColumnRowMapper<Integer>;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

public class UniqueAuthorDAOImpl extends JdbcDaoSupport implements UniqueAuthorDAO {

    private GetAuthor getAuthor;
    private GetAuthVarnames getAuthVarnames;
    private GetAuthDocsOrdByCites getAuthDocsOrdByCites;
    private GetAuthDocsOrdByYear getAuthDocsOrdByYear;
	private GetAuthorRecords getAuthorRecords;
	private GetAuthorRecordsByPapers getAuthorRecordsByPapers;

	private UpdateAuthor updateAuthor;
	private UpdateAuthorNdocs updateAuthorNdocs;
	private UpdateAuthorNcites updateAuthorNcites;
	private MoveAuthorRecords moveAuthorRecords;
	private RemoveAuthor removeAuthor;

    protected void initDao() throws ApplicationContextException {
		initMappingSqlQueries();
    } //- initDao
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
		getAuthor   = new GetAuthor(getDataSource());
		getAuthVarnames	        = new GetAuthVarnames(getDataSource());
		getAuthDocsOrdByCites	= new GetAuthDocsOrdByCites(getDataSource());
		getAuthDocsOrdByYear	= new GetAuthDocsOrdByYear(getDataSource());
		getAuthorRecords        = new GetAuthorRecords(getDataSource());
		getAuthorRecordsByPapers = new GetAuthorRecordsByPapers(getDataSource());
		updateAuthor            = new UpdateAuthor(getDataSource());
		updateAuthorNdocs       = new UpdateAuthorNdocs(getDataSource());
		updateAuthorNcites      = new UpdateAuthorNcites(getDataSource());		
		moveAuthorRecords       = new MoveAuthorRecords(getDataSource());
		removeAuthor            = new RemoveAuthor(getDataSource());
    }
    
    public UniqueAuthor getAuthor(String aid) throws DataAccessException {
    	UniqueAuthor uauth = getAuthor.run(aid);    	
    	return uauth;
    }
    
    public List<String> getAuthVarnames(String aid) throws DataAccessException {
		return getAuthVarnames.run(aid);
    }
    
    public List<ThinDoc> getAuthDocsOrdByCites(String aid) throws DataAccessException {
		return getAuthDocsOrdByCites.run(aid);
    }
    
    public List<ThinDoc> getAuthDocsOrdByYear(String aid) throws DataAccessException {
		return getAuthDocsOrdByYear.run(aid);
    }

	public List<Integer> getAuthorRecords(String aid) 
		throws DataAccessException {
		return getAuthorRecords.run(aid);
	}
	public List<Integer> getAuthorRecordsByPapers(String aid, List<Integer> papers) 
		throws DataAccessException {
		return getAuthorRecordsByPapers.run(aid, papers);
	}

	public void updateAuthNdocs(String aid) throws DataAccessException {
		updateAuthorNdocs.run(aid);
	}

	public void updateAuthNcites(String aid) throws DataAccessException {
		updateAuthorNcites.run(aid);
	}

	public void updateAuthInfo(UniqueAuthor uauth) 
		throws DataAccessException {
		updateAuthor.run(uauth);
	}

	public void removeAuthor(String aid) throws DataAccessException {
		removeAuthor.run(aid);
	}

	public void moveAuthorRecords(String target_aid, List<Integer> author_records) throws DataAccessException {
		moveAuthorRecords.run(target_aid, author_records);
	}

    private static final String DEF_GET_DOCS_NCITES_ORD_QUERY =
		"SELECT paperid, p.cluster, title, ncites, venue, year FROM authors AS a JOIN papers AS p " + 
		"ON p.id=a.paperid " +
		"where a.cluster=? GROUP BY p.cluster ORDER BY p.ncites DESC";
	
    private class GetAuthDocsOrdByCites extends MappingSqlQuery {
		public GetAuthDocsOrdByCites(DataSource dataSource) {
			setDataSource(dataSource);
			setSql(DEF_GET_DOCS_NCITES_ORD_QUERY);
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}
		
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			ThinDoc thinDoc = new ThinDoc();
			
			thinDoc.setDoi(rs.getString("paperid"));
			thinDoc.setCluster(rs.getLong("p.cluster"));
			thinDoc.setTitle(rs.getString("title"));
			//    thinDoc.setAuthors(rs.getString("coau"));
			//    thinDoc.setAbstract(rs.getString("abstract"));
			thinDoc.setNcites(rs.getInt("ncites"));
			thinDoc.setYear(rs.getInt("year"));
			thinDoc.setVenue(rs.getString("venue"));
			
			return thinDoc;
        }
		
        public List<ThinDoc> run(String aid) {
			return execute(aid);
		}
    }

	private static final String DEF_GET_AUTH_RECORDS_QUERY = 
		"SELECT id FROM authors WHERE cluster=?";
	private class GetAuthorRecords extends MappingSqlQuery {

		public GetAuthorRecords(DataSource dataSource) {
			setDataSource(dataSource);
			setSql(DEF_GET_AUTH_RECORDS_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt("id");
		}
		public List<Integer> run(String aid) {
			return execute(new Object[] { aid });
        }
	}

	/*private static final String DEF_GET_AUTH_RECORDS_BY_PAPERS_QUERY = 
		"SELECT a.id FROM authors AS a JOIN papers AS p " +
		"ON p.id=a.paperid " + 
		"WHERE a.cluster=? AND p.cluster IN (?)";
	private class GetAuthorRecordsByPapers extends MappingSqlQuery {
		
		public GetAuthorRecordsByPapers(DataSource dataSource) {
			setDataSource(dataSource);
			System.out.println(DEF_GET_AUTH_RECORDS_BY_PAPERS_QUERY);
			setSql(DEF_GET_AUTH_RECORDS_BY_PAPERS_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.ARRAY));
			compile();
		}
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt("id");
		}
		// @papers - list of paper clusters
		public List<Integer> run(String aid, List<Integer> papers) {
			if (papers.size() == 0)
				return new ArrayList<Integer>();
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(papers.get(0));
			for (int i = 1; i < papers.size(); i++) {
				Integer paper_cluster = papers.get(i);
				buffer.append(",");
				buffer.append(paper_cluster);
			}
			System.out.println("PAPERS");
			System.out.println(buffer.toString());
			//return execute(new Object[] { aid, buffer.toString() });
			return execute(new Object[] { aid, papers });
        }
	}*/

	private static final String DEF_GET_AUTH_RECORDS_BY_PAPERS_QUERY = 
		"SELECT a.id FROM authors AS a JOIN papers AS p " +
		"ON p.id=a.paperid " + 
		"WHERE a.cluster=:cluster AND p.cluster IN (:papers)";
	private class GetAuthorRecordsByPapers extends NamedParameterJdbcDaoSupport
		implements ParameterizedRowMapper {
		private String sql = DEF_GET_AUTH_RECORDS_BY_PAPERS_QUERY;
		//private RowMapper rowMapper;

		public GetAuthorRecordsByPapers(DataSource dataSource) {
			//super(dataSource);
			setDataSource(dataSource);
		}
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt("id");
		}
		// @papers - list of paper clusters
		public List<Integer> run(String aid, List<Integer> papers) {
			if (papers.size() == 0)
				return new ArrayList<Integer>();
			
			System.out.println("PAPERS");
			for (int i = 0; i < papers.size(); i++) {
				System.out.println(papers.get(i));
			}

			//Map parameters = new HashMap();
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("papers", papers);
			parameters.addValue("cluster", aid);
			//return null;
			return getNamedParameterJdbcTemplate().query(sql, parameters, this);
			//return query(sql, parameters);
        }
	}

    private static final String DEF_GET_DOCS_YEAR_ORD_QUERY =
		"SELECT paperid, title, ncites, venue, year FROM authors AS a JOIN papers AS p " + 
		"ON p.id=a.paperid " +
		"where a.cluster=? GROUP BY p.cluster ORDER BY p.year DESC";    

    private class GetAuthDocsOrdByYear extends MappingSqlQuery {
		public GetAuthDocsOrdByYear(DataSource dataSource) {
			setDataSource(dataSource);
            setSql(DEF_GET_DOCS_YEAR_ORD_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}
		
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			ThinDoc thinDoc = new ThinDoc();
			
			thinDoc.setDoi(rs.getString("paperid"));
			thinDoc.setTitle(rs.getString("title"));
			thinDoc.setNcites(rs.getInt("ncites"));
			thinDoc.setYear(rs.getInt("year"));
			thinDoc.setVenue(rs.getString("venue"));
			
            return thinDoc;
        }
		
        public List<ThinDoc> run(String aid) {
            return execute(aid);
        }
    }
    
    private static final String DEF_GET_VARNAMES_QUERY =
		"SELECT name FROM authors WHERE cluster=? GROUP BY name";
    
    private class GetAuthVarnames extends MappingSqlQuery {
		public GetAuthVarnames(DataSource dataSource) {
			setDataSource(dataSource);
            setSql(DEF_GET_VARNAMES_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("name");
        }
		
        public List<String> run(String aid) {
			return execute(aid);
        }
    }
    
    private static final String DEF_GET_AUTH_QUERY =
		"SELECT id, canname, ndocs, ncites, email, affil, affil2, affil3, address, hindex, url" + 
		" FROM cannames WHERE id=?";
    
    private class GetAuthor extends MappingSqlQuery {
		
        public GetAuthor(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_AUTH_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            UniqueAuthor uauth = new UniqueAuthor();
			
            uauth.setAid(rs.getString("id"));
            uauth.setCanname(rs.getString("canname"));
            
            String email = rs.getString("email");
            if (email != null) {
            	uauth.setEmail(rs.getString("email"));
            }
			uauth.addAffil(rs.getString("affil"));
			uauth.addAffil(rs.getString("affil2"));
			uauth.addAffil(rs.getString("affil3"));
            //uauth.setAffil(rs.getString("affil");				

            uauth.setAddress(rs.getString("address"));
            uauth.setNDocs(rs.getInt("ndocs"));
            uauth.setHindex(rs.getInt("hindex"));
            uauth.setUrl(rs.getString("url"));
            
            return uauth;
        }
		
        public UniqueAuthor run(String aid) {
	    if( aid == null) return null;
            List list = execute(aid);
            if (list.isEmpty()) {
                return null;
            } else {
                return (UniqueAuthor)list.get(0);
            }
        }
    }
	
	private static final String DEF_REMOVE_PAPERS_QUERY = 
		"UPDATE authors SET cluster=:cluster WHERE id IN (:authors)";
	//private class MoveAuthorRecords extends SqlUpdate {
	private class MoveAuthorRecords extends NamedParameterJdbcDaoSupport {
		String sql = DEF_REMOVE_PAPERS_QUERY;
		public MoveAuthorRecords(DataSource dataSource) {
			setDataSource(dataSource);
			/*setSql(DEF_REMOVE_PAPERS_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER)); // ID
			declareParameter(new SqlParameter(Types.VARCHAR)); // ID
			compile();*/
		}
		public int run(String target_aid, List<Integer> author_records) {
			/*StringBuffer buffer = new StringBuffer();
			buffer.append(author_records.get(0));
			for (int i = 1; i < author_records.size(); i++) {
				Integer record_id = author_records.get(i);
				buffer.append(",");
				buffer.append(record_id);
			}
			System.out.println("AUTHORS");
			System.out.println(buffer.toString());
			return update(new Object[] { target_aid, buffer.toString() });*/

			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("authors", author_records);
			parameters.addValue("cluster", target_aid);
			//return null;
			return getNamedParameterJdbcTemplate().update(sql, parameters);
		}
	}

	private static final String DEF_UPDATE_NDOCS_QUERY = 
		"UPDATE cannames SET ndocs=(SELECT count(distinct p.cluster) FROM authors AS a JOIN papers as p " +
		"ON p.id=a.paperid WHERE a.cluster=?) where id=?";
	private class UpdateAuthorNdocs extends SqlUpdate {
		public UpdateAuthorNdocs(DataSource dataSource) {
			setDataSource(dataSource);
			setSql(DEF_UPDATE_NDOCS_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER)); // ID
			declareParameter(new SqlParameter(Types.INTEGER)); // ID
			compile();
		}
		public int run(String aid) {
			return update(new Object[] { aid, aid });
		}
	}

	private static final String DEF_UPDATE_NCITES_QUERY = 
		"UPDATE cannames SET ncites=(SELECT SUM(s.ncites) FROM " + 
		"(SELECT distinct p.cluster, ncites FROM authors AS a JOIN papers as p " + 
		"ON p.id=a.paperid WHERE a.cluster=?) as s) WHERE id=?";
	private class UpdateAuthorNcites extends SqlUpdate {
		public UpdateAuthorNcites(DataSource dataSource) {
			setDataSource(dataSource);
			setSql(DEF_UPDATE_NCITES_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER)); // ID
			declareParameter(new SqlParameter(Types.INTEGER)); // ID
			compile();
		}
		public int run(String aid) {
			return update(new Object[] { aid, aid });
		}
	}

	private static final String DEF_REMOVE_AUTH_QUERY = 
		"DELETE FROM cannames WHERE id=?";
	private class RemoveAuthor extends SqlUpdate {
		public RemoveAuthor(DataSource dataSource) {
			setDataSource(dataSource);
			setSql(DEF_REMOVE_AUTH_QUERY);
			declareParameter(new SqlParameter(Types.INTEGER)); // ID
			compile();
		}
		public int run(String aid) {
			return update(new Object[] { aid });
		}
	}

	private static final String DEF_UPDATE_AUTH_QUERY = 
		"UPDATE cannames SET canname=?, affil=?, address=?, email=? WHERE id=?";
	private class UpdateAuthor extends SqlUpdate {
		public UpdateAuthor(DataSource dataSource) {
			setDataSource(dataSource);
			setSql(DEF_UPDATE_AUTH_QUERY);
			declareParameter(new SqlParameter(Types.VARCHAR)); // canname
			declareParameter(new SqlParameter(Types.VARCHAR)); // affil
			declareParameter(new SqlParameter(Types.VARCHAR)); // address
			declareParameter(new SqlParameter(Types.VARCHAR)); // email
			declareParameter(new SqlParameter(Types.INTEGER)); // ID
			compile();
		}
		
		public int run(UniqueAuthor uauth) {			
			Object[] params = new Object[] {
				uauth.getCanname(),
				uauth.getAffil(),
				uauth.getAddress(),
				uauth.getEmail(),
				uauth.getAid()
			};
			return update(params);
		}
	}	

}
