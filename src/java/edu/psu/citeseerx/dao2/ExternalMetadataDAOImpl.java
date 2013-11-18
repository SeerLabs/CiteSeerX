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

import edu.psu.citeseerx.domain.ACM;
import edu.psu.citeseerx.domain.CiteULike;
import edu.psu.citeseerx.domain.DBLP;

/**
 * Spring-based JDBC implementation of ExternalMetadataDAO. 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ExternalMetadataDAOImpl extends JdbcDaoSupport 
implements ExternalMetadataDAO {

    protected InsertDBLPRecord insertDBLPRecord;
    protected UpdateDBLPRecord updateDBLPRecord;
    protected DeleteDBLP deleteDBLPRecords;
    protected GetDBLPRecordsByTitleMapping getByTitle;
    protected InsertCiteULikeRecord insertCiteULike;
    protected UpdateCiteULikeRecord updateCiteULike;
    protected GetCiteULikeRecordsByDOIMapping getCiteULikeByDOI;
    protected InsertACMRecord insertACMRecord;
    protected UpdateACMRecord updateACMRecord;
    protected GetACMRecordsByTitleMapping getACMByTitle;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        insertDBLPRecord = new InsertDBLPRecord(getDataSource());
        updateDBLPRecord = new UpdateDBLPRecord(getDataSource());
        deleteDBLPRecords = new DeleteDBLP(getDataSource());
        getByTitle = new GetDBLPRecordsByTitleMapping(getDataSource());
        insertCiteULike = new InsertCiteULikeRecord(getDataSource());
        updateCiteULike = new UpdateCiteULikeRecord(getDataSource());
        getCiteULikeByDOI = 
            new GetCiteULikeRecordsByDOIMapping(getDataSource());
        insertACMRecord = new InsertACMRecord(getDataSource());
        updateACMRecord = new UpdateACMRecord(getDataSource());
        getACMByTitle = new GetACMRecordsByTitleMapping(getDataSource());
    } //- initMappingSqlQueries
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#addDBLPRecord(edu.psu.citeseerx.domain.DBLP)
     */
    public void addDBLPRecord(DBLP record) throws DataAccessException {
        int updated = updateDBLPRecord.run(record);
        if (updated == 0) {
            insertDBLPRecord.run(record);
        }

    } //- addDBLPRecord

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#deleteDBLP()
     */
    public void deleteDBLP() throws DataAccessException {
        deleteDBLPRecords.update();

    } //- deleteDBLP

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#getRecordsByTitle(java.lang.String)
     */
    public List<DBLP> getDBLPRecordsByTitle(String title)
            throws DataAccessException {
        return getByTitle.run(title);
    } //- getRecordsByTitle

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#addCiteULikeRecord(edu.psu.citeseerx.domain.CiteULike)
     */
    public void addCiteULikeRecord(CiteULike record) throws DataAccessException {
      
        int updated = updateCiteULike.run(record);
        if (updated == 0) {
            insertCiteULike.run(record);
        }
    } //- addCiteULikeRecord

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#getCiteULikeRecordByDOI(java.lang.String)
     */
    public CiteULike getCiteULikeRecordByDOI(String doi)
            throws DataAccessException {
        List<CiteULike> records = getCiteULikeByDOI.execute(doi);
        if (records.isEmpty()) {
            return null;
        }else{
            return records.get(0);
        }
    } //- getCiteULikeRecordByDOI

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#addACMRecord(edu.psu.citeseerx.domain.ACM)
     */
    @Override
    public void addACMRecord(ACM record) throws DataAccessException {
        int updated = updateACMRecord.run(record);
        if (updated == 0) {
            insertACMRecord.run(record);
        }

    } //- addACMRecord

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalMetadataDAO#getACMRecordsByTitle(java.lang.String)
     */
    @Override
    public List<ACM> getACMRecordsByTitle(String title)
            throws DataAccessException {
        return getACMByTitle.run(title);
    } //- getACMRecordsByTitle

    private static final String DEF_INSERT_DBLP_RECORD_QUERY = 
        "insert into dblp values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private class InsertDBLPRecord extends SqlUpdate {

        public InsertDBLPRecord(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_DBLP_RECORD_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.SMALLINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.SMALLINT));
            declareParameter(new SqlParameter(Types.SMALLINT));
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
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } //- InsertDBLPRecord.InsertDBLPRecord
        
        public int run(DBLP record) {
            Object[] params = new Object[] {
                    record.getType(),
                    record.getAuthors(),
                    record.getNumAuthors(),
                    record.getEditor(),
                    record.getTitle(),
                    record.getBookTitle(),
                    record.getPages(),
                    record.getYear(),
                    record.getAddress(),
                    record.getJournal(),
                    record.getVolume(),
                    record.getNumber(),
                    record.getMonth(),
                    record.getUrl(),
                    record.getEe(),
                    record.getCdrom(),
                    record.getCite(),
                    record.getPublisher(),
                    record.getNote(),
                    record.getCrossref(),
                    record.getIsbn(),
                    record.getSeries(),
                    record.getSchool(),
                    record.getChapter(),
                    record.getDkey(),
                    record.getNumCites()
            };
            return update(params);
        } //- InsertDBLPRecord.run
    } //- class InsertDBLPRecord
        
    private static final String DEF_UPDATE_DBLP_RECORD_QUERY = "update dblp " +
    		" set type = ?, authors = ?, numAuthors = ?, editor = ?, " +
    		" title = ?, booktitle = ?, pages = ?, year = ?," +
    		" address = ?, journal = ?, volume = ?, number = ?, month = ?," +
    		" url = ?, ee = ?, cdrom = ?, cite = ?, publisher = ?, note = ?," +
    		" crossref = ?, isbn = ?, series = ?, school = ?, chapter = ?," +
    		" numCites = ? where dkey = ?";
    
    private class UpdateDBLPRecord extends SqlUpdate {

        public UpdateDBLPRecord(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_DBLP_RECORD_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.SMALLINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.SMALLINT));
            declareParameter(new SqlParameter(Types.SMALLINT));
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
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateDBLPRecord.UpdateDBLPRecord
        
        public int run(DBLP record) {
            Object[] params = new Object[] {
                    record.getType(),
                    record.getAuthors(),
                    record.getNumAuthors(),
                    record.getEditor(),
                    record.getTitle(),
                    record.getBookTitle(),
                    record.getPages(),
                    record.getYear(),
                    record.getAddress(),
                    record.getJournal(),
                    record.getVolume(),
                    record.getNumber(),
                    record.getMonth(),
                    record.getUrl(),
                    record.getEe(),
                    record.getCdrom(),
                    record.getCite(),
                    record.getPublisher(),
                    record.getNote(),
                    record.getCrossref(),
                    record.getIsbn(),
                    record.getSeries(),
                    record.getSchool(),
                    record.getChapter(),
                    record.getNumCites(),
                    record.getDkey()
            };
            return update(params);
        } //- UpdateDBLPRecord.run
    } //- class UpdateDBLPRecord
    
    private static final String DEF_DELETE_DBLP_QUERY =
        "delete from dblp";
    
    private class DeleteDBLP extends SqlUpdate {
        public DeleteDBLP(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DELETE_DBLP_QUERY);
            compile();
        } //- DeleteDBLP.DeleteDBLP
    } //- class DeleteDBLP

    private static final String DEF_GET_DBLP_RECORDS_BY_TITLE_QUERY =
        "select id, type, authors, numAuthors, editor, title, booktitle, " +
        "pages, year, address, journal, volume, number, month, url, ee, " +
        "cdrom, cite, publisher, note, crossref, isbn, series, school, " +
        "chapter, dkey, numCites from dblp where title = ?";
    
    private class GetDBLPRecordsByTitleMapping extends MappingSqlQuery {
        public GetDBLPRecordsByTitleMapping(DataSource ds) {
            super(ds, DEF_GET_DBLP_RECORDS_BY_TITLE_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
        } //- GetDBLPRecordsByTitleMapping.GetDBLPRecordsByTitleMapping
        
        protected DBLP mapRow(ResultSet rs, int rownum) 
        throws SQLException {
            DBLP record = new DBLP();
            record.setId(rs.getLong("id"));
            record.setType(rs.getString("type"));
            record.setAuthors(rs.getString("authors"));
            record.setNumAuthors(rs.getInt("numAuthors"));
            record.setEditor(rs.getString("editor"));
            record.setTitle(rs.getString("title"));
            record.setBookTitle(rs.getString("booktitle"));
            record.setPages(rs.getString("pages"));
            record.setYear(rs.getInt("year"));
            record.setAddress(rs.getString("address"));
            record.setJournal(rs.getString("journal"));
            record.setVolume(rs.getInt("volume"));
            record.setNumber(rs.getInt("number"));
            record.setMonth(rs.getString("month"));
            record.setUrl(rs.getString("url"));
            record.setEe(rs.getString("ee"));
            record.setCdrom(rs.getString("cdrom"));
            record.setCite(rs.getString("cite"));
            record.setPublisher(rs.getString("publisher"));
            record.setNote(rs.getString("note"));
            record.setCrossref(rs.getString("crossref"));
            record.setIsbn(rs.getString("isbn"));
            record.setSeries(rs.getString("series"));
            record.setSchool(rs.getString("school"));
            record.setChapter(rs.getString("chapter"));
            record.setDkey(rs.getString("dkey"));
            record.setNumCites(rs.getInt("numCites"));
            return record;
        } //- GetDBLPRecordsByTitleMapping.mapRow
        
        protected List<DBLP> run(String title) {
            Object[] params = new Object[] {title};
            return execute(params);
        } //- GetPapersNoELinkMapping.r
    } //- class GetDBLPRecordsByTitleMapping
    
    private static final String DEF_INSERT_CITEULIKE_RECORD_QUERY =
        "insert into citeulike values (?, ?)";
    
    private class InsertCiteULikeRecord extends SqlUpdate {

        public InsertCiteULikeRecord(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_CITEULIKE_RECORD_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertDBLPRecord.InsertDBLPRecord
        
        public int run(CiteULike record) {
            Object[] params = new Object[] {
                    record.getCiteulikeID(),
                    record.getCiteSeerXID()
            };
            return update(params);
        } //- InsertCiteULikeRecord.run
    } //- class InsertCiteULikeRecord
    
    private static final String DEF_UPDATE_CITEULIKE_RECORD_QUERY =
        "update citeulike set citeseerxid = ? where citeulikeid = ?";
    
    private class UpdateCiteULikeRecord extends SqlUpdate {

        public UpdateCiteULikeRecord(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_CITEULIKE_RECORD_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- UpdateCiteULikeRecord.InsertDBLPRecord
        
        public int run(CiteULike record) {
            Object[] params = new Object[] {
                    record.getCiteSeerXID(),
                    record.getCiteulikeID()
            };
            return update(params);
        } //- UpdateCiteULikeRecord.run
    } //- class UpdateCiteULikeRecord
    
    private static final String DEF_GET_CITEULIKE_RECORD_BY_DOI_QUERY =
        "select citeulikeid, citeseerxid from citeulike where citeseerxid = ?";
    
    private class GetCiteULikeRecordsByDOIMapping extends MappingSqlQuery {
        public GetCiteULikeRecordsByDOIMapping(DataSource ds) {
            super(ds, DEF_GET_CITEULIKE_RECORD_BY_DOI_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
        } //- GetCiteULikeRecordsByDOIMapping.GetCiteULikeRecordsByDOIMapping
        
        protected CiteULike mapRow(ResultSet rs, int rownum) 
        throws SQLException {
            CiteULike record = new CiteULike();

            record.setCiteulikeID(rs.getString("citeulikeid"));
            record.setCiteSeerXID(rs.getString("citeseerxid"));
            return record;
        } //- GetCiteULikeRecordsByDOIMapping.mapRow
    } //- class GetCiteULikeRecordsByDOIMapping
    
    private static final String DEF_INSERT_ACM_RECORD_QUERY =
        "insert into acm values (?, ?, ?, ?, ?, ?, ?, ?)";

    private class InsertACMRecord extends SqlUpdate {

        public InsertACMRecord(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_ACM_RECORD_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.SMALLINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertACMRecord.InsertDBLPRecord

        public int run(ACM record) {
            Object[] params = new Object[] {
                    record.getId(),
                    record.getAuthors(),
                    record.getTitle(),
                    record.getYear(),
                    record.getVenue(),
                    record.getUrl(),
                    record.getPages(),
                    record.getPublication()
            };
            return update(params);
        } //- InsertACMRecord.run
    } //- class InsertACMRecord
    
    private static final String DEF_UPDATE_ACM_RECORD_QUERY = "update acm " +
        "set authors = ?, title = ?, year = ?, venue = ?, url = ?, " +
        "pages = ?, publication = ? where id = ?";

    private class UpdateACMRecord extends SqlUpdate {
    
        public UpdateACMRecord(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_ACM_RECORD_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.SMALLINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- UpdateACMRecord.UpdateDBLPRecord
    
        public int run(ACM record) {
            Object[] params = new Object[] {
                    record.getAuthors(),
                    record.getTitle(),
                    record.getYear(),
                    record.getVenue(),
                    record.getUrl(),
                    record.getPages(),
                    record.getPublication(),
                    record.getId()
            };
            return update(params);
        } //- UpdateACMRecord.run
    } //- class UpdateACMRecord
    
    private static final String DEF_GET_ACM_RECORDS_BY_TITLE_QUERY =
        "select id, authors, title, year, venue, url, pages, publication " +
        "from acm where title = ?";

    private class GetACMRecordsByTitleMapping extends MappingSqlQuery {
        public GetACMRecordsByTitleMapping(DataSource ds) {
            super(ds, DEF_GET_ACM_RECORDS_BY_TITLE_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
        } //- GetACMRecordsByTitleMapping.GetACMRecordsByTitleMapping

        protected ACM mapRow(ResultSet rs, int rownum)
        throws SQLException {
            ACM record = new ACM();
            record.setId(rs.getLong("id"));
            record.setAuthors(rs.getString("authors"));
            record.setTitle(rs.getString("title"));
            record.setYear(rs.getInt("year"));
            record.setVenue(rs.getString("venue"));
            record.setUrl(rs.getString("url"));
            record.setPages(rs.getString("pages"));
            record.setPublication(rs.getString("publication"));
            return record;
        } //- GetACMRecordsByTitleMapping.mapRow

        protected List<ACM> run(String title) {
            Object[] params = new Object[] {title};
            return execute(params);
        } //- GetACMRecordsByTitleMapping.run
    } //- class GetACMRecordsByTitleMapping
    
} //- class ExternalMetadataDAOImpl
