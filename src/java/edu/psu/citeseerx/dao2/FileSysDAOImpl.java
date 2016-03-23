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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;

import com.lowagie.text.pdf.PdfReader;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.utility.FileNamingUtils;
import edu.psu.citeseerx.utility.FileUtils;

/**
 * Spring-based JDBC and filesystem implementation of FileSysDAO. 
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class FileSysDAOImpl extends JdbcDaoSupport implements FileSysDAO {

    
    
    private GetVersionByNum getVersionByNum;
    private GetVersionByName getVersionByName;
    private GetRepID getRepID;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getVersionByNum = new GetVersionByNum(getDataSource());
        getVersionByName = new GetVersionByName(getDataSource());
        getRepID = new GetRepID(getDataSource());
    } //- initMappingSqlQueries
    
    
    private static final String DEF_GET_VERSION_BY_NUM_QUERY =
        "select name, repositoryID, path, deprecated, spam from "+
        "paperVersions where paperid=? and version=?";
        
    private class GetVersionByNum extends MappingSqlQuery {
        
        public GetVersionByNum(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_VERSION_BY_NUM_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- GetVersionByNum.GetVersionByNum
        
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document doc = new Document();
            doc.setVersionName(rs.getString(1));
            doc.setVersionRepID(rs.getString(2));
            doc.setVersionPath(rs.getString(3));
            doc.setVersionDeprecated(rs.getBoolean(4));
            doc.setVersionSpam(rs.getBoolean(5));
            return doc;
        } //- GetVersionByNum.mapRow
        
        public Document run(String doi, int version) {
            Object[] params = new Object[] { doi, new Integer(version) };
            List<Document> list = execute(params);
            if (list.isEmpty()) {
                return null;
            } else {
                return (Document)list.get(0);
            }
        } //- GetVersionByNum.run
    } //- class GetVersionByNum

    
    private static final String DEF_GET_VERSION_BY_NAME_QUERY =
        "select version, repositoryID, path, deprecated, spam from "+
        "paperVersions where paperid=? and name=?";
    
    private class GetVersionByName extends MappingSqlQuery {
        
        public GetVersionByName(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_VERSION_BY_NAME_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetVersionByName.GetVersionByName 
        
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document doc = new Document();
            doc.setVersionName(rs.getString(1));
            doc.setVersionRepID(rs.getString(2));
            doc.setVersionPath(rs.getString(3));
            doc.setVersionDeprecated(rs.getBoolean(4));
            doc.setVersionSpam(rs.getBoolean(5));
            return doc;
        } //- GetVersionByName.mapRow
        
        public Document run(String doi, String name) {
            Object[] params = new Object[] { doi, name };
            List<Document> list = execute(params);
            if (list.isEmpty()) {
                return null;
            } else {
                return (Document)list.get(0);
            }
        } //- GetVersionByName.run
    } //- class GetVersionByName
    
    
    private static final String DEF_GET_REPID_QUERY =
        "select repositoryID from papers where id=?";
    
    private class GetRepID extends MappingSqlQuery {
        
        public GetRepID(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_REPID_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetRepID.GetRepID
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        } //- GetRepID.mapRow
        
        public String run(String doi) {
            List<String> list = execute(doi);
            if (list.isEmpty()) {
                return null;
            } else {
                return (String)list.get(0);
            }
        } //- GetRepID.run
    } //- class GetRepID

    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getRepositoryID(java.lang.String)
     */
    public String getRepositoryID(String doi) {
        return getRepID.run(doi);
    }  //- getRepositoryID


    @Override
    public Document getDocVersion(String doi, int version) throws IOException {
        return getVersionByNum.run(doi, version);
    }


    @Override
    public Document getDocVersion(String doi, String name) throws IOException {
        return getVersionByName.run(doi,name);
    }
    

}  //- class FileSysDAOImpl
