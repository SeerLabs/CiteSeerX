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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import edu.psu.citeseerx.domain.*;

/**
 * DocumentDAO Implementation using MySQL as a persistent storage
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class DocumentDAOImpl extends JdbcDaoSupport implements DocumentDAO {

    private GetDoc getDoc;
    private GetDocSrc getDocSrc;
    private InsertDoc insertDoc;
    private InsertDocSrc insertDocSrc;
    private UpdateDoc updateDoc;
    private UpdateDocSrc updateDocSrc;
    private SetState setState;
    private SetCluster setCluster;
    private SetNcites setNcites;
    private CountDocs countDocs;
    private GetDOIs getDOIs;
    private GetSetDOIs getSetDOIs;
    private GetSetDOICount getSetDOICount;
    private GetCrawledDOIs getCrawledDOIs;
    private GetLatestDocuments getLatestDocuments;
    private GetKeyphrase getKeyphrase;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getDoc = new GetDoc(getDataSource());
        getDocSrc = new GetDocSrc(getDataSource());
        insertDoc = new InsertDoc(getDataSource());
        insertDocSrc = new InsertDocSrc(getDataSource());
        updateDoc = new UpdateDoc(getDataSource());
        updateDocSrc = new UpdateDocSrc(getDataSource());
        setState = new SetState(getDataSource());
        setCluster = new SetCluster(getDataSource());
        setNcites = new SetNcites(getDataSource());
        countDocs = new CountDocs(getDataSource());
        getDOIs = new GetDOIs(getDataSource());
        getSetDOIs = new GetSetDOIs(getDataSource());
        getSetDOICount = new GetSetDOICount(getDataSource());
        getCrawledDOIs = new GetCrawledDOIs(getDataSource());
        getLatestDocuments = new GetLatestDocuments(getDataSource());
        getKeyphrase = new GetKeyphrase(getDataSource());
    } //- initMappingSqlQueries
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getDocument(java.lang.String, boolean)
     */
    public Document getDocument(String doi, boolean getSource)
    throws DataAccessException {
        Document doc = getDoc.run(doi);
        if (doc == null) return null;
        if (getSource) {
            Document srcDoc = getDocSrc.run(doi);
            if (srcDoc == null) {
                System.err.println("WARNING: Null Source Doc for "+doi);
                srcDoc = new Document();  // Just in case...
            }
            doc.setSource(Document.TITLE_KEY,
                    srcDoc.getSource(Document.TITLE_KEY));
            doc.setSource(Document.ABSTRACT_KEY,
                    srcDoc.getSource(Document.ABSTRACT_KEY));
            doc.setSource(Document.YEAR_KEY,
                    srcDoc.getSource(Document.YEAR_KEY));
            doc.setSource(Document.VENUE_KEY,
                    srcDoc.getSource(Document.VENUE_KEY));
            doc.setSource(Document.VEN_TYPE_KEY,
                    srcDoc.getSource(Document.VEN_TYPE_KEY));
            doc.setSource(Document.PAGES_KEY,
                    srcDoc.getSource(Document.PAGES_KEY));
            doc.setSource(Document.VOL_KEY,
                    srcDoc.getSource(Document.VOL_KEY));
            doc.setSource(Document.NUM_KEY,
                    srcDoc.getSource(Document.NUM_KEY));
            doc.setSource(Document.PUBLISHER_KEY,
                    srcDoc.getSource(Document.PUBLISHER_KEY));
            doc.setSource(Document.PUBADDR_KEY,
                    srcDoc.getSource(Document.PUBADDR_KEY));
            doc.setSource(Document.TECH_KEY,
                    srcDoc.getSource(Document.TECH_KEY));
            doc.setSource(Document.CITES_KEY,
                    srcDoc.getSource(Document.CITES_KEY));
        }
        return doc;

    }  //- getDocument

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#insertDocument(edu.psu.citeseerx.domain.Document)
     */
    public void insertDocument(Document doc)
    throws DataAccessException {
        insertDoc.run(doc);
    }  //- insertDocument

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#insertDocumentSrc(edu.psu.citeseerx.domain.Document)
     */
    public void insertDocumentSrc(Document doc) {
        if (doc.hasSourceData()) {
            insertDocSrc.run(doc);
        }
    }  //- insertDocumentSrc

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#updateDocument(edu.psu.citeseerx.domain.Document)
     */
    public void updateDocument(Document doc)
    throws DataAccessException {
        updateDoc.run(doc);
        if (doc.hasSourceData()) {
            updateDocSrc.run(doc);
        }
    }  //- updateDocument


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#setDocPublic(edu.psu.citeseerx.domain.Document, int)
     */
    public void setDocState(Document doc, int toState)
    throws DataAccessException {
        setState.run(doc, toState);
    }  //- setPublic


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#setDocCluster(edu.psu.citeseerx.domain.Document, java.lang.Long)
     */
    public void setDocCluster(Document doc, Long clusterID)
    throws DataAccessException {
        setCluster.run(doc, clusterID);
    }  //- setCluster

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#setDocNcites(edu.psu.citeseerx.domain.Document, int)
     */
    public void setDocNcites(Document doc, int ncites)
    throws DataAccessException {
        setNcites.run(doc, ncites);
    }  //- setNcites

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getNumberOfDocumentRecords()
     */
    public Integer getNumberOfDocumentRecords() throws DataAccessException {
        return countDocs.run();
    }  //- getNumberOfDocumentRecords

    
    public List<String> getDOIs(String start, int amount)
    throws DataAccessException {
        return getDOIs.run(start, amount);
    }  //- getDOIs

    /* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.DocumentDAO#getSetDOIs(java.util.Date, java.util.Date, java.lang.String, int)
	 */
    public List<DOIInfo> getSetDOIs(Date start, Date end, String prev,  
    		int amount) throws DataAccessException {
    	return getSetDOIs.run(start, end , prev,amount);
    } // - getSetDOIs 
   
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getSetDOICount(java.util.Date, java.util.Date, java.lang.String)
     */
    public Integer getSetDOICount(Date start, Date end, String prev) 
    throws DataAccessException {
    	return getSetDOICount.run(start, end, prev);
    } //- getSetDOICount
    
	/* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getCrawledDOIs(java.util.Date, java.util.Date, java.lang.String, int)
     */
    public List<String> getCrawledDOIs(Date start, Date end, String lastDOI,
            int amount) throws DataAccessException {
        return getCrawledDOIs.run(start, end, lastDOI, amount);
    } //- getCrawledDOIs

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getLastDocuments(java.lang.String, int)
     */
    public List<String> getLastDocuments(String lastDOI, int amount)
            throws DataAccessException {
        return getLatestDocuments.run(lastDOI, amount);
    } //- getLastDocuments

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getKeyphrase(java.util.Date, java.util.Date, java.lang.String, int)
     */
    public List<String> getKeyphrase(String doi)
	    throws DataAccessException {
        return getKeyphrase.run(doi);
    } //- getKeyphrase

    private static final String DEF_GET_DOC_QUERY =
        "select id, version, cluster, title, abstract, year, venue, " +
        "venueType, pages, volume, number, publisher, pubAddress, tech, " +
        "public, ncites, versionName, crawlDate, repositoryID, " +
        "conversionTrace, selfCites, versionTime from papers where id=?";

    private class GetDoc extends MappingSqlQuery {
        
        public GetDoc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_DOC_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetDoc.GetDoc
        
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document doc = new Document();
            doc.setDatum(Document.DOI_KEY, rs.getString("id"));
            doc.setVersion(rs.getInt("version"));
            doc.setClusterID(rs.getLong("cluster"));
            doc.setDatum(Document.TITLE_KEY, rs.getString("title"));
            doc.setDatum(Document.ABSTRACT_KEY, rs.getString("abstract"));
            doc.setDatum(Document.YEAR_KEY, rs.getString("year"));
            doc.setDatum(Document.VENUE_KEY, rs.getString("venue"));
            doc.setDatum(Document.VEN_TYPE_KEY, rs.getString("venueType"));
            doc.setDatum(Document.PAGES_KEY, rs.getString("pages"));
            doc.setDatum(Document.VOL_KEY, rs.getString("volume"));
            doc.setDatum(Document.NUM_KEY, rs.getString("number"));
            doc.setDatum(Document.PUBLISHER_KEY, rs.getString("publisher"));
            doc.setDatum(Document.PUBADDR_KEY, rs.getString("pubAddress"));
            doc.setDatum(Document.TECH_KEY, rs.getString("tech"));
            doc.setNcites(rs.getInt("ncites"));
            doc.setSelfCites(rs.getInt("selfCites"));
            doc.setVersionName(rs.getString("versionName"));
	    doc.setState(rs.getInt("public"));
/*
            if (rs.getBoolean("public")) {
                doc.setState(DocumentProperties.IS_PUBLIC);
            }
	    else{
                doc.setState(DocumentProperties.LOGICAL_DELETE);
            }
*/
            doc.setVersionTime(new Date(
                    rs.getTimestamp("versionTime").getTime()));

            DocumentFileInfo finfo = new DocumentFileInfo();
            finfo.setDatum(DocumentFileInfo.CRAWL_DATE_KEY,
                    DateFormat.getDateInstance().format(
                            rs.getTimestamp("crawlDate")));
            finfo.setDatum(DocumentFileInfo.REP_ID_KEY,
                    rs.getString("repositoryID"));
            finfo.setDatum(DocumentFileInfo.CONV_TRACE_KEY,
                    rs.getString("conversionTrace"));
            doc.setFileInfo(finfo);
            
            return doc;
        } //- GetDoc.mapRow
        
        public Document run(String doi) {
            List<Document> list = execute(doi);
            if (list.isEmpty()) {
                return null;
            } else {
                return (Document)list.get(0);
            }
        } //- GetDoc.run
        
    }  //- class GetDoc
    
    private static final String DEF_GET_KEYPHRASE_QUERY =
        "select ngram from paper_keywords_noun " + 
	      "where paper_id=? order by count desc";

    private class GetKeyphrase extends MappingSqlQuery {
        
        public GetKeyphrase(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_KEYPHRASE_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetKeyphrase.GetKeyphrase
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("ngram");
        } //- GetKeyphrase.mapRow

        public List<String> run(String doi) {
            Object[] params = new Object[] { doi };
            return execute(params);
        } //- GetKeyphrase.run
        
    }  //- class GetKeyphrase
    
    private static final String DEF_GET_DOC_SRC_QUERY =
        "select title, abstract, year, venue, venueType, pages, volume, " +
        "number, publisher, pubAddress, tech, citations from " +
        "papers_versionShadow where id=?";

    private class GetDocSrc extends MappingSqlQuery {
        
        public GetDocSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_DOC_SRC_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetDocSrc.GetDocSrc
        
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document doc = new Document();
            doc.setSource(Document.TITLE_KEY, rs.getString("title"));
            doc.setSource(Document.ABSTRACT_KEY, rs.getString("abstract"));
            doc.setSource(Document.YEAR_KEY, rs.getString("year"));
            doc.setSource(Document.VENUE_KEY, rs.getString("venue"));
            doc.setSource(Document.VEN_TYPE_KEY,
                    rs.getString("venueType"));
            doc.setSource(Document.PAGES_KEY, rs.getString("pages"));
            doc.setSource(Document.VOL_KEY, rs.getString("volume"));
            doc.setSource(Document.NUM_KEY, rs.getString("number"));
            doc.setSource(Document.PUBLISHER_KEY,
                    rs.getString("publisher"));
            doc.setSource(Document.PUBADDR_KEY,
                    rs.getString("pubAddress"));
            doc.setSource(Document.TECH_KEY, rs.getString("tech"));
            doc.setSource(Document.CITES_KEY, rs.getString("citations"));
            return doc;
        }
        
        public Document run(String doi) {
            List<Document> list = execute(doi);
            if (list.isEmpty()) {
                return null;
            } else {
                return (Document)list.get(0);
            }
        } //- GetDocSrc.run
        
    }  //- class GetDocSrc


    /* id, version, cluster, title, abstract, year, venue, venueType, pages,
     * volume, number, publisher, pubAddress, tech, public,
     * size, versionName, crawlDate, repositoryID, conversionTrace, 
     * selfCites, versionTime */
    private static final String DEF_INSERT_DOC_QUERY =
        "insert into papers values (?, ?, ?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

    private class InsertDoc extends SqlUpdate {
        
        public InsertDoc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_DOC_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- InsertDoc.InsertDoc
        
        public int run(Document doc) {
            Integer year = null;
            try {
                year = Integer.parseInt(doc.getDatum(Document.YEAR_KEY));
            } catch (Exception e) { }            
            Integer vol = null;
            try {
                vol = Integer.parseInt(doc.getDatum(Document.VOL_KEY));
            } catch (Exception e) { }
            Integer num = null;
            try {
                num = Integer.parseInt(doc.getDatum(Document.NUM_KEY));
            } catch (Exception e) { }
            
            DocumentFileInfo finfo = doc.getFileInfo();
            java.util.Date crawlDate = null;
            try {
                crawlDate = DateFormat.getDateInstance().parse(
                        finfo.getDatum(DocumentFileInfo.CRAWL_DATE_KEY));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                /* that's ok - we'll use current_timestamp */
                crawlDate = new java.util.Date(System.currentTimeMillis());
            }
            
            Object[] params = new Object[] {
                    doc.getDatum(Document.DOI_KEY),
                    doc.getVersion(),
                    doc.getClusterID(),
                    doc.getDatum(Document.TITLE_KEY),
                    doc.getDatum(Document.ABSTRACT_KEY),
                    year,
                    doc.getDatum(Document.VENUE_KEY),
                    doc.getDatum(Document.VEN_TYPE_KEY),
                    doc.getDatum(Document.PAGES_KEY),
                    vol, num,
                    doc.getDatum(Document.PUBLISHER_KEY),  
                    doc.getDatum(Document.PUBADDR_KEY),
                    doc.getDatum(Document.TECH_KEY),
                    doc.getState(),
                    doc.getNcites(),
                    doc.getVersionName(),
                    new Timestamp(crawlDate.getTime()),
                    finfo.getDatum(DocumentFileInfo.REP_ID_KEY),
                    finfo.getDatum(DocumentFileInfo.CONV_TRACE_KEY),
                    doc.getSelfCites()
            };
            return update(params);
        } //- InsertDoc.run
        
    }  //- class InsertDoc
    
    
    /* id, title, abstract, year, venue, venueType, pages,
     * volume, number, publisher, pubAddress, tech, cites */
    private static final String DEF_INSERT_DOC_SRC_QUERY =
        "insert into papers_versionShadow values (?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?)";

    private class InsertDocSrc extends SqlUpdate {
        
        public InsertDocSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_DOC_SRC_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertDocSrc.InsertDocSrc
        
        public int run(Document doc) {
            Object[] params = new Object[] {
                    doc.getDatum(Document.DOI_KEY),
                    doc.getSource(Document.TITLE_KEY),
                    doc.getSource(Document.ABSTRACT_KEY),
                    doc.getSource(Document.YEAR_KEY),
                    doc.getSource(Document.VENUE_KEY),
                    doc.getSource(Document.VEN_TYPE_KEY),
                    doc.getSource(Document.PAGES_KEY),
                    doc.getSource(Document.VOL_KEY),
                    doc.getSource(Document.NUM_KEY),
                    doc.getSource(Document.PUBLISHER_KEY),  
                    doc.getSource(Document.PUBADDR_KEY),
                    doc.getSource(Document.TECH_KEY),
                    doc.getSource(Document.CITES_KEY)
            };
            return update(params);
        } //- InsertDocSrc
        
    }  //- class InsertDocSrc


    private static final String DEF_UPDATE_DOC_QUERY =
        "update papers set version=?, title=?, abstract=?, " +
        "year=?, venue=?, venueType=?, pages=?, volume=?, number=?, " +
        "publisher=?, pubAddress=?, tech=?, public=?, " +
        "versionName=?, crawlDate=?, repositoryID=?, conversionTrace=? " +
        "where id=?";

    private class UpdateDoc extends SqlUpdate {
        
        public UpdateDoc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_DOC_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));            
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateDoc.UpdateDoc
        
        public int run(Document doc) {
            Integer year = null;
            try {
                year = Integer.parseInt(doc.getDatum(Document.YEAR_KEY));
            } catch (Exception e) { }            
            Integer vol = null;
            try {
                vol = Integer.parseInt(doc.getDatum(Document.VOL_KEY));
            } catch (Exception e) { }
            Integer num = null;
            try {
                num = Integer.parseInt(doc.getDatum(Document.NUM_KEY));
            } catch (Exception e) { }
            
            DocumentFileInfo finfo = doc.getFileInfo();
            java.util.Date crawlDate = null;
            try {
                crawlDate = DateFormat.getDateInstance().parse(
                        finfo.getDatum(DocumentFileInfo.CRAWL_DATE_KEY));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                /* that's ok - we'll use current_timestamp */
                crawlDate = new java.util.Date(System.currentTimeMillis());
            }
            
            Object[] params = new Object[] {
                    doc.getVersion(),
                    doc.getDatum(Document.TITLE_KEY),
                    doc.getDatum(Document.ABSTRACT_KEY),
                    year,
                    doc.getDatum(Document.VENUE_KEY),
                    doc.getDatum(Document.VEN_TYPE_KEY),
                    doc.getDatum(Document.PAGES_KEY),
                    vol, num,
                    doc.getDatum(Document.PUBLISHER_KEY),  
                    doc.getDatum(Document.PUBADDR_KEY),
                    doc.getDatum(Document.TECH_KEY),
                    doc.getState(),
                    doc.getVersionName(),
                    new Timestamp(crawlDate.getTime()),
                    finfo.getDatum(DocumentFileInfo.REP_ID_KEY),
                    finfo.getDatum(DocumentFileInfo.CONV_TRACE_KEY),
                    doc.getDatum(Document.DOI_KEY)
            };
            return update(params);
        } //- UpdateDoc.run
        
    }  //- class UpdateDoc
    
    
    private static final String DEF_UPDATE_DOC_SRC_QUERY =
        "update papers_versionShadow set title=?, abstract=?, " +
        "year=?, venue=?, venueType=?, pages=?, volume=?, number=?, " +
        "publisher=?, pubAddress=?, tech=?, citations=? where id=?";

    private class UpdateDocSrc extends SqlUpdate {
        
        public UpdateDocSrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_DOC_SRC_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateDocSrc.UpdateDocSrc
        
        public int run(Document doc) {
            Object[] params = new Object[] {
                    doc.getSource(Document.TITLE_KEY),
                    doc.getSource(Document.ABSTRACT_KEY),
                    doc.getSource(Document.YEAR_KEY),
                    doc.getSource(Document.VENUE_KEY),
                    doc.getSource(Document.VEN_TYPE_KEY),
                    doc.getSource(Document.PAGES_KEY),
                    doc.getSource(Document.VOL_KEY),
                    doc.getSource(Document.NUM_KEY),
                    doc.getSource(Document.PUBLISHER_KEY),  
                    doc.getSource(Document.PUBADDR_KEY),
                    doc.getSource(Document.TECH_KEY),
                    doc.getSource(Document.CITES_KEY),
                    doc.getDatum(Document.DOI_KEY)
            };
            return update(params);
        } //- UpdateDocSrc.run
        
    }  //- class UpdateDocSrc


    private static final String DEF_SET_PUBLIC_QUERY =
        "update papers set public=? where id=?";

    private class SetState extends SqlUpdate {
        
        public SetState(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_PUBLIC_QUERY);
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- SetPublic.SetPublic
        
        public int run(Document doc, int toState) {
            Object[] params = new Object[] {
                    new Integer(doc.getState()), doc.getDatum(Document.DOI_KEY)
            };
            return update(params);
        } //- SetPublic.run
        
    }  //- class SetPublic
    
    
    private static final String DEF_SET_CLUSTER_QUERY =
        "update papers set cluster=? where id=?";

    private class SetCluster extends SqlUpdate {
        
        public SetCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_CLUSTER_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- SetCluster.SetCluster
        
        public int run(Document doc, Long clusterID) {
            Object[] params = new Object[] {
                    clusterID, doc.getDatum(Document.DOI_KEY)
            };
            return update(params);
        } //- SetCluster.run
        
    }  //- class SetCluster
    
    
    private static final String DEF_SET_CITES_STMT =
        "update papers set ncites=? where id=?";
    
    private class SetNcites extends SqlUpdate {
        
        public SetNcites(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_CITES_STMT);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- SetNcites.SetNcites
        
        public int run(Document doc, int ncites) {
            Object[] params = new Object[] {
                    new Integer(ncites), doc.getDatum(Document.DOI_KEY)
            };
            return update(params);
        } //- SetNcites.run
        
    }  //- class SetNCites


    private static final String DEF_COUNT_DOCUMENTS_QUERY =
        "select count(id) from papers";

    private class CountDocs extends MappingSqlQuery {
        
        public CountDocs(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_COUNT_DOCUMENTS_QUERY);
            compile();
        } //- CountDocs.CountDocs
        
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        } //- CountDocs.mapRow
        
        public Integer run() {
            List<Integer> list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return (Integer)list.get(0);
            }
        } //- CountDocs.run
        
    }  //- class CountDocs
        
    
    private static final String DEF_GET_DOIS_QUERY =
        "select id from papers where id>? order by id asc limit ?";
    
    private class GetDOIs extends MappingSqlQuery {
        
        public GetDOIs(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_DOIS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- GetDOIs.GetDOIs
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        } //- GetDOIs.mapRow
        
        public List<String> run(String start, int amount) {
            Object[] params = new Object[] { start, new Integer(amount) };
            return execute(params);
        } //- GetDOIs.run
        
    }  //- class GetDOIs

    /* start, end, prev, count */
    private static final String DEF_GET_SET_DOIS_QUERY =
        "select id, versionTime from papers where Date(versionTime) >= ? and " +
        "Date(versionTime) <= ? and id > ? and public = 1 order by id asc limit ?";
    
    private class GetSetDOIs extends MappingSqlQuery {
        
        public GetSetDOIs(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_SET_DOIS_QUERY);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- GetSetDOIs.GetSetDOIs
        
        public DOIInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        	DOIInfo doi = new DOIInfo();
        	
            doi.setDoi(rs.getString("id"));
            doi.setModifiedDate(rs.getTimestamp("versionTime"));
            return doi;
        } //- GetSetDOIs.mapRow
        
        public List<DOIInfo> run(Date start, Date end, String prev, 
        		int amount) {
            Object[] params = new Object[] { start, end , prev ,  
            		new Integer(amount) };
            return execute(params);
        } //- GetSetDOIs.run
        
    }  //- class GetSetDOIs

    /* start, end, prev */
    private static final String DEF_GET_SET_DOI_COUNT_QUERY =
        "select count(id) from papers where Date(versionTime) >= ? and " +
        "Date(versionTime) <= ? and id > ? and public = 1";

    private class GetSetDOICount extends MappingSqlQuery {

        public GetSetDOICount(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_SET_DOI_COUNT_QUERY);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetSetDOICount

        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        } //- GetSetDOICount.mapRow

        public Integer run(Date start, Date end, String prev) {
            Object[] params = new Object[] { start, end , prev };
            List<Integer> rlist = execute(params);
		    if(rlist != null) {
		    	if(rlist.isEmpty()) {
		    		return null;
		    	}
		    	else {
		    		return (Integer)rlist.get(0);
		    	}
		    }
		    else {
		    	return null;
		    }
        } //- GetSetDOICount.run
    }  //- class GetSetDOICount
 
    private static final String DEF_GET_CRAWLED_DOIS_BETWEEN_QUERY =
        "select id from papers where crawlDate > ? and crawlDate <= ? and " +
        "id > ? order by id asc limit ?";

    private class GetCrawledDOIs extends MappingSqlQuery {
        
        public GetCrawledDOIs(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CRAWLED_DOIS_BETWEEN_QUERY);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- GetSetDOIs.GetSetDOIs
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("id");
        } //- GetCrawledDOIs.mapRow
        
        public List<String> run(Date start, Date end, String lastID, 
                int amount) {
            Object[] params = new Object[] { start, end , lastID,  
                    new Integer(amount) };
            return execute(params);
        } //- GetCrawledDOIs.run
        
    }  //- class GetCrawledDOIs
    
    private static final String DEF_GET_LATEST_DOCUMENTS_QUERY =
        "select id from papers where id < ? order by crawlDate desc, " +
        "id desc limit ?";
    
    private class GetLatestDocuments extends MappingSqlQuery {
        
        public GetLatestDocuments(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_LATEST_DOCUMENTS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- GetSetDOIs.GetSetDOIs
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("id");
        } //- GetCrawledDOIs.mapRow
        
        public List<String> run(String lastID, int amount) {
            Object[] params = new Object[] { lastID, new Integer(amount) };
            return execute(params);
        } //- GetLatestDocuments.run
        
    }  //- class GetLatestDocuments
    
}  //- class DocumentDAOImpl
