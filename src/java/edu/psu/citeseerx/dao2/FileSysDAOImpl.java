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

    private RepositoryMap repMap;
    
    public void setRepositoryMap(RepositoryMap repMap) {
        this.repMap = repMap;
    } //- setRepositoryMap
    
    
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
    
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocFromXML(java.lang.String, java.lang.String)
     */
    public Document getDocFromXML(String repID, String relPath)
    throws IOException {

        String path = repMap.buildFilePath(repID, relPath);
        FileInputStream in = new FileInputStream(path);

        Document doc = new Document();
        doc.fromXML(in);

        in.close();
        return doc;
        
    }  //- getDocFromXML
        
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocVersion(java.lang.String, int)
     */
    public Document getDocVersion(String doi, int version) throws IOException {
        
        String repID, path;
        String name = null;
        boolean deprecated = false;
        boolean spam = false;
        
        if (version > 0) {
            Document holder = getVersionByNum.run(doi, version);
            if (holder == null) return null;

            repID = holder.getVersionRepID();
            path = holder.getVersionPath();
            name = holder.getVersionName();
            deprecated = holder.isDeprecatedVersion();
            spam = holder.isSpamVersion();
            
        } else {
            version = 0;
            repID = getRepID.run(doi);
            path = FileNamingUtils.buildXMLPath(doi);
        }
        
        Document doc = getDocFromXML(repID, path);
        
        doc.setVersion(version);
        doc.setVersionName(name);
        doc.setVersionRepID(repID);
        doc.setVersionPath(path);
        doc.setVersionDeprecated(deprecated);
        doc.setVersionSpam(spam);
        
        return doc;
        
    }  //- getVersion
    
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getRepositoryID(java.lang.String)
     */
    public String getRepositoryID(String doi) {
        return getRepID.run(doi);
    }  //- getRepositoryID
    
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocVersion(java.lang.String, java.lang.String)
     */
    public Document getDocVersion(String doi, String name) throws IOException {
        Document holder = getVersionByName.run(doi, name); 
        if (holder == null) return null;
        
        Document doc = getDocFromXML(holder.getVersionRepID(),
                holder.getVersionPath());
        
        doc.setVersion(holder.getVersion());
        doc.setVersionName(holder.getVersionName());
        doc.setVersionRepID(holder.getVersionRepID());
        doc.setVersionPath(holder.getVersionPath());
        doc.setVersionDeprecated(holder.isDeprecatedVersion());
        doc.setVersionSpam(holder.isSpamVersion());
        
        return doc;
        
    }  //- getVersion
    

    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#writeXML(edu.psu.citeseerx.domain.Document)
     */
    public void writeXML(Document doc) throws IOException {

        String doi = doc.getDatum(Document.DOI_KEY, Document.UNENCODED);
        
        DocumentFileInfo finfo = doc.getFileInfo();
        String repID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY,
                DocumentFileInfo.UNENCODED);
        String relPath = FileNamingUtils.buildXMLPath(doi);
        String path = repMap.buildFilePath(repID, relPath);
        
        FileOutputStream out = new FileOutputStream(path);
        doc.toXML(out, Document.INCLUDE_SYS_DATA);
        out.close();
        
    }  //- writeXML
    
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#writeVersion(edu.psu.citeseerx.domain.Document)
     */
    public void writeVersion(Document doc) throws IOException {

        String repID = doc.getVersionRepID();
        String relPath = doc.getVersionPath();
        String path = repMap.buildFilePath(repID, relPath);
        
        FileOutputStream out = new FileOutputStream(path);
        doc.toXML(out, Document.INCLUDE_SYS_DATA);
        out.close();
        
    }  //- writeVersion
    
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getFileInputStream(java.lang.String, java.lang.String, java.lang.String)
     */
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException {
        
        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String fn = doi+"."+type;
        String relPath = dir+System.getProperty("file.separator")+fn;
        String path = repMap.buildFilePath(repID, relPath);
        
        return new FileInputStream(path);
        
    }  //- getFileInputStream

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getPdfReader(java.lang.String, java.lang.String)
     */
    public PdfReader getPdfReader(String doi, String repID)
    throws IOException {
      
        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String fn = doi+".pdf";
        String relPath = dir+System.getProperty("file.separator")+fn;
        String path = repMap.buildFilePath(repID, relPath);
        PdfReader reader = new PdfReader(path);
        return reader;
        
    } //- getPdfReader
    
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
    
    
    public static final String[] supportedTypes = new String[] {
        "PDF", "PS", "DOC", "RTF"
    };
    
    public static final Set<String> typeLookup = new HashSet<String>();
    
    static {
        for (String str : supportedTypes) {
            typeLookup.add(str);
        }
    }
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getFileTypes(java.lang.String, java.lang.String)
     */
    public List<String> getFileTypes(String doi, String repID)
    throws IOException {
        
        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String path = repMap.buildFilePath(repID, dir);
        
        String[] files = new File(path).list();
        List<String> types = new ArrayList<String>();
        
        if (files == null) {
            return types;
        }
        
        for (String file : files) {
            String ext = FileUtils.getExtension(file);
            ext = ext.substring(1);
            if (typeLookup.contains(ext.toUpperCase())) {
                types.add(ext);
            }
        }
        Collections.sort(types);
        return types;
        
    }  //- getFileTypes

    
}  //- class FileSysDAOImpl
