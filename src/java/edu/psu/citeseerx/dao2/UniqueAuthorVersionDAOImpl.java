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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;
import java.io.IOException;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import edu.psu.citeseerx.domain.UniqueAuthor;
import edu.psu.citeseerx.loaders.ContextReader;
import edu.psu.citeseerx.utility.CSXConstants;

/**
 * Provides transparent access to canname's version persistence storage 
 * @author Puck Treeratpituk
 * @version $Rev: 1 $ $Date: 2011-09-23 11:35:02 -0400 (Fri, 23 Sep 2011) $
 */
public class UniqueAuthorVersionDAOImpl extends JdbcDaoSupport implements UniqueAuthorVersionDAO {

	private GetMaxVersion getMaxVersion;
	private BackupUauthor backupUauthor;
	private BackupAuthors backupAuthors;
	private UniqueAuthorDAO uauthDAO;

    public void setUniqueAuthDAO(UniqueAuthorDAO uauthDAO) {
        this.uauthDAO = uauthDAO;
    }

	protected void initDao() throws ApplicationContextException {
		initMappingSqlQueries();
	} //- initDao

	protected void initMappingSqlQueries() throws ApplicationContextException {
		backupUauthor = new BackupUauthor(getDataSource());
		backupAuthors = new BackupAuthors(getDataSource());
		getMaxVersion = new GetMaxVersion(getDataSource());		
	}
	
    ///////////////////////////////////////////////////////
	// UniqueAuthorVersion DAO
	public void updateUauthorInfo(String userid, String aid, String new_canname, String new_affil) 
		throws DataAccessException {
		UniqueAuthor uauth = uauthDAO.getAuthor(aid);
		if (uauth == null)
			return;
		if ((new_canname == null) || new_canname.equals("")) // canname cannot be null or empty string...
			return;
		// if the information didn't change....dont do anything
		if (uauth.getCanname().equals(new_canname) &&
			uauth.getAffil().equals(new_affil))
			return;
		
		// backup oldinfo to version table
		int version = getMaxVersion.run(aid) + 1;		
		backupUauthor.changeInfo(userid, uauth, version, CSXConstants.USER_VERSION); 

		// update information
		uauth.setCanname(new_canname);
		uauth.setAffil(new_affil);
		uauthDAO.updateAuthInfo(uauth); // update new infomation 
	}

	public void mergeUauthors(String userid, String aid1, String aid2) throws DataAccessException {
		// dont do antyhing if it's the same aid
		if ((aid1 == null) || (aid2 == null) || aid1.equals(aid2))
			return;
		UniqueAuthor uauth1 = uauthDAO.getAuthor(aid1);
		UniqueAuthor uauth2 = uauthDAO.getAuthor(aid2);
		// dont do anything if one of aid is invalid...
		if ((uauth1 == null) || (uauth2 == null))
			return;
		
		// start backing up
		int version   = getMaxVersion.run(aid1) + 1;
		KeyHolder key = backupUauthor.removePapers(userid, uauth2, version, CSXConstants.USER_VERSION);
		int update_id = key.getKey().intValue();
		// get list of authors record affected...
		List<Integer> authors = uauthDAO.getAuthorRecords(aid2);
		for (Integer author_id: authors) {
			backupAuthors.changeAuthors(update_id, author_id);
		}
		// done backing up..		

		// move papers to uauth1
		uauthDAO.moveAuthorRecords(aid1, authors);
		uauthDAO.updateAuthNdocs(aid1);
		uauthDAO.updateAuthNcites(aid1);
		uauthDAO.updateAuthNdocs(aid2);
	}

	public void removeUauthorPapers(String userid, String aid, List<Integer> papers) throws DataAccessException {
		if ((aid == null) || (papers.size() == 0))
			return;
		UniqueAuthor uauth = uauthDAO.getAuthor(aid);
		if (uauth == null) 
			return;

		// start backing up
		int version   = getMaxVersion.run(aid) + 1;
		KeyHolder key = backupUauthor.removePapers(userid, uauth, version, CSXConstants.USER_VERSION);
		int update_id = key.getKey().intValue();
		// get list of authors record affected...
		List<Integer> authors = uauthDAO.getAuthorRecordsByPapers(aid, papers);
		for (Integer author_id: authors) {
			System.out.println("AID:" + author_id);
			backupAuthors.changeAuthors(update_id, author_id);
		}
		// done backing up..

		// remove papers...
		uauthDAO.moveAuthorRecords(Integer.toString(CSXConstants.USER_REMOVED), authors);
		uauthDAO.updateAuthNdocs(aid);
		uauthDAO.updateAuthNcites(aid);
	}
	
	// END INTERFACE
    ///////////////////////////////////////////////////////

    private static final String DEF_GET_MAX_VERSION_QUERY =
		//	"SELECT version FROM canname_versions WHERE cluster=? ORDER BY version DESC LIMIT 1";
		"SELECT max(version) FROM canname_versions WHERE cluster=?";
    
    private class GetMaxVersion extends MappingSqlQuery {
        
        public GetMaxVersion(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_MAX_VERSION_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetMaxVersion.GetMaxVersion
        
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        } //- GetMaxVersion.mapRow
        
		// aid - author cluster
        public int run(String aid) {
            List<Integer> list = execute(aid);
            if (list.isEmpty()) {
                return 0;
            } else {
                return ((Integer)list.get(0)).intValue();
            }
        } //- GetMaxVersion.run
    } //- class GetMaxVersion

	private static final String DEF_REMOVE_PAPERS_QUERY = 
		"INSERT INTO canname_author_versions (canname_version_id, author_id) VALUES (?,?)";
	private class BackupAuthors extends SqlUpdate {

		public BackupAuthors(DataSource dataSource) {
			setDataSource(dataSource);
			setSql(DEF_REMOVE_PAPERS_QUERY);
			declareParameter(new SqlParameter(Types.INTEGER)); // canname_version_id
			declareParameter(new SqlParameter(Types.INTEGER)); // author_id			
			compile();
		}

		public int changeAuthors(int canname_version_id, int author_id) {
			Object[] params = new Object[] { canname_version_id, author_id };
			return update(params);
		}		
	}
	
	private static final String DEF_UPDATE_UAUTH_QUERY = 
		"INSERT INTO canname_versions (userid, type, cluster, merged_cluster, canname, affil, address, email, version, versionName, versionTime) " +
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
	private class BackupUauthor extends SqlUpdate {
		
		public BackupUauthor(DataSource dataSource) {
			setDataSource(dataSource);
			setSql(DEF_UPDATE_UAUTH_QUERY);
			declareParameter(new SqlParameter(Types.VARCHAR)); // userid
			declareParameter(new SqlParameter(Types.VARCHAR)); // type
			declareParameter(new SqlParameter(Types.INTEGER)); // cluster
			declareParameter(new SqlParameter(Types.INTEGER)); // merged_cluster
			declareParameter(new SqlParameter(Types.VARCHAR)); // canname
			//declareParameter(new SqlParameter(Types.VARCHAR)); // fname
			//declareParameter(new SqlParameter(Types.VARCHAR)); // mname
			//declareParameter(new SqlParameter(Types.VARCHAR)); // lname
			declareParameter(new SqlParameter(Types.VARCHAR)); // affil
			declareParameter(new SqlParameter(Types.VARCHAR)); // address
			declareParameter(new SqlParameter(Types.VARCHAR)); // email

			declareParameter(new SqlParameter(Types.INTEGER)); // version
			declareParameter(new SqlParameter(Types.VARCHAR)); // versionName

			compile();
		}

		public KeyHolder removePapers(String userid, UniqueAuthor uauth, int version, String versionName) {
			Object[] params = new Object[] {
				userid,             // userid
				"RM_PAPERS",        // type
				uauth.getAid(),     // cluster
				null,               // merged_cluster
				uauth.getCanname(), // canname
				uauth.getAffil(),   // affil
				uauth.getAddress(), // addr
				uauth.getEmail(),   // email
				version,            // version
				versionName         // versionName
			};
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			int nrows = update(params, keyHolder);
			if (nrows > 0) 
				return keyHolder;
			else return null;
		}
		
		// backing up uauth info before updating...
		public int changeInfo(String userid, UniqueAuthor uauth, int version, String versionName) {
			Object[] params = new Object[] {
				userid,             // userid
				"UPDATE",           // type
				uauth.getAid(),     // cluster
				null,               // merged_cluster
				uauth.getCanname(), // canname
				uauth.getAffil(),   // affil
				uauth.getAddress(), // addr
				uauth.getEmail(),   // email
				version,            // version
				versionName         // versionName
			};
			return update(params);
		}

	}

	public static void main(String[] args) throws Exception {
		ListableBeanFactory factory = ContextReader.loadContext();
		
		UniqueAuthorVersionDAOImpl corrector = (UniqueAuthorVersionDAOImpl)factory.getBean("uniqueAuthorVersionDAO");;;

		String aid = "275294";
		String userid = "puck";
		//System.out.println("\n\n");
		//System.out.println(uauth.getCanname());
		//System.out.println("\n");		
		//corrector.updateInfo(userid, uauth1, uauth2);

		List<Integer> papers = new ArrayList<Integer>();
		papers.add(4023424);
		papers.add(9421632);
		papers.add(589287);

		corrector.removeUauthorPapers(userid, aid, papers);
		//corrector.changeAuthorsByPapers(userid, uauth1, papers);
	}
}