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

import org.springframework.dao.DataAccessException;

import com.lowagie.text.pdf.PdfReader;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import edu.psu.citeseerx.dao2.AckDAO;
import edu.psu.citeseerx.dao2.AdminDAO;
import edu.psu.citeseerx.dao2.AlgorithmDAO;
import edu.psu.citeseerx.dao2.AuthorDAO;
import edu.psu.citeseerx.dao2.CitationDAO;
import edu.psu.citeseerx.dao2.CiteChartDAO;
import edu.psu.citeseerx.dao2.DocumentDAO;
import edu.psu.citeseerx.dao2.ExternalLinkDAO;
import edu.psu.citeseerx.dao2.FileDAO;
import edu.psu.citeseerx.dao2.FileSysDAO;
import edu.psu.citeseerx.dao2.GeneralStatistics;
import edu.psu.citeseerx.dao2.HubDAO;
import edu.psu.citeseerx.dao2.KeywordDAO;
import edu.psu.citeseerx.dao2.LegacyIDDAO;
import edu.psu.citeseerx.dao2.RedirectPDFDAO;
import edu.psu.citeseerx.dao2.TableDAO;
import edu.psu.citeseerx.dao2.TagDAO;
import edu.psu.citeseerx.dao2.UniqueAuthorDAO;
import edu.psu.citeseerx.dao2.UniqueAuthorVersionDAO;
import edu.psu.citeseerx.dao2.VersionDAO;
import edu.psu.citeseerx.domain.Acknowledgment;
import edu.psu.citeseerx.domain.Algorithm;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.CheckSum;
import edu.psu.citeseerx.domain.Citation;
import edu.psu.citeseerx.domain.DOIInfo;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.ExternalLink;
import edu.psu.citeseerx.domain.Hub;
import edu.psu.citeseerx.domain.Keyword;
import edu.psu.citeseerx.domain.LinkType;
import edu.psu.citeseerx.domain.PDFRedirect;
import edu.psu.citeseerx.domain.Table;
import edu.psu.citeseerx.domain.Tag;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.UniqueAuthor;
import edu.psu.citeseerx.utility.FileNamingUtils;


/**
 * Provides a single point access to all Document related persistent storage
 * operations
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class CSXDAOImpl implements CSXDAO {

    private AckDAO ackDAO;
    private AdminDAO adminDAO;
    private AuthorDAO authDAO;
    private CitationDAO citeDAO;
    private CiteChartDAO citeChartDAO;
    private GeneralStatistics generalStatistics;
    private DocumentDAO docDAO;
    private FileDAO fileDAO;
    private FileSysDAO fileSysDAO;
    private HubDAO hubDAO;
    private KeywordDAO keywordDAO;
    private LegacyIDDAO legacyIDDAO;
    private TagDAO tagDAO;
    private UniqueAuthorDAO uauthDAO;
	private UniqueAuthorVersionDAO uauthVersionDAO;
    private VersionDAO versionDAO;
    private ExternalLinkDAO externalLinkDAO;
    private TableDAO tableDAO;
    private AlgorithmDAO algorithmDAO;
    private RedirectPDFDAO redirectPDFDAO;
    
    public void setAckDAO(AckDAO ackDAO) {
        this.ackDAO = ackDAO;
    } //- setAckDAO

    public void setAdminDAO(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    } //- setAdminDAO

    public void setAuthDAO(AuthorDAO authDAO) {
        this.authDAO = authDAO;
    } //- setAuthDAO

    public void setUniqueAuthDAO(UniqueAuthorDAO uauthDAO) {
        this.uauthDAO = uauthDAO;
    }

    public void setUniqueAuthVersionDAO(UniqueAuthorVersionDAO uauthVersionDAO) {
        this.uauthVersionDAO = uauthVersionDAO;
    }
    
    public void setCiteChartDAO(CiteChartDAO citeChartDAO) {
        this.citeChartDAO = citeChartDAO;
    } //- setCiteChartDAO

    public void setCiteDAO(CitationDAO citeDAO) {
        this.citeDAO = citeDAO;
    } //- setCiteDAO

    public void setDocDAO(DocumentDAO docDAO) {
        this.docDAO = docDAO;
    } //- setDocDAO

    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    } //- setFileDAO

    public void setFileSysDAO(FileSysDAO fileSysDAO) {
        this.fileSysDAO = fileSysDAO;
    } //- setFileSysDAO

    public void setHubDAO(HubDAO hubDAO) {
        this.hubDAO = hubDAO;
    } //- setHubDAO

    public void setKeywordDAO(KeywordDAO keywordDAO) {
        this.keywordDAO = keywordDAO;
    } //- setKeywordDAO

    public void setLegacyIDDAO(LegacyIDDAO legacyIDDAO) {
        this.legacyIDDAO = legacyIDDAO;
    } //- setLegacyIDDAO

    public void setTagDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    } //- setTagDAO
    
    public void setVersionDAO(VersionDAO versionDAO) {
        this.versionDAO = versionDAO;
    } //- setVersionDAO
    
    public void setExternalLinkDAO(ExternalLinkDAO externalLinkDAO) {
        this.externalLinkDAO = externalLinkDAO;
    } //- setExternalLinkDAO

    public void setTableDAO(TableDAO tableDAO) {
        this.tableDAO = tableDAO;
    } //- setTableDAO
    
    public void setGeneralStatistics(GeneralStatistics generalStatistics) {
    	this.generalStatistics = generalStatistics;
    }
    
    public void setAlgorithmDAO(AlgorithmDAO algorithmDAO) {
        this.algorithmDAO = algorithmDAO;
    } //- setAlgorithmDAO
    
    public void setRedirectPDFDAO(RedirectPDFDAO redirectPDFDAO) {
        this.redirectPDFDAO = redirectPDFDAO;
    }
    
    ///////////////////////////////////////////////////////
    //  CSX Operations                               
    ///////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CSXOperations#insertDocumentEntry(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void insertDocumentEntry(Document doc) throws DataAccessException {
        docDAO.insertDocument(doc);
    } //- insertDocumentEntry

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CSXOperations#importDocument(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void importDocument(Document doc)
    throws DataAccessException, IOException {
        String doi = doc.getDatum(Document.DOI_KEY);
        
        docDAO.insertDocumentSrc(doc);
        
        DocumentFileInfo finfo = doc.getFileInfo();
        for (String url : finfo.getUrls()) {
            hubDAO.insertUrl(doi, url);
        }
        for (Hub hub : finfo.getHubs()) {
            for (String url : finfo.getUrls()) {
                hubDAO.addHubMapping(hub, url, doi);
            }
        }
        for (CheckSum sum : finfo.getCheckSums()) {
            sum.setDOI(doi);
            fileDAO.insertChecksum(sum);
        }
        
        insertAuthors(doi, doc.getAuthors());

        insertCitations(doi, doc.getCitations());

        insertAcknowledgments(doi, doc.getAcknowledgments());

        insertKeywords(doi, doc.getKeywords());
            
        for (Tag tag : doc.getTags()) {
            tagDAO.addTag(doi, tag.getTag());
        }

        fileSysDAO.writeXML(doc);

    }  //- importDocument


    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CSXOperations#getDocumentFromDB(java.lang.String, boolean, boolean, boolean, boolean, boolean, boolean)
     */
    @Override
    public Document getDocumentFromDB(String doi, boolean getCitations,
            boolean getContexts, boolean getSource, boolean getAcks,
            boolean getKeywords, boolean getTags) throws DataAccessException {
        
        Document doc = docDAO.getDocument(doi, getSource);

        if (doc == null) {
            return null;
        }

        DocumentFileInfo finfo = doc.getFileInfo();
        List<String> urls = hubDAO.getUrls(doi);
        for (Object o : urls) {
            finfo.addUrl((String)o);
        }
        
        List<Author> authors = authDAO.getDocAuthors(doi, getSource);
        for (Author author : authors) {
                doc.addAuthor(author);
        }

        if (getCitations) {
            List<Citation> citations =
                citeDAO.getCitations(doi, getContexts);
            for (Citation citation : citations) {
                doc.addCitation(citation);
            }
        }
        if (getAcks) {
            List<Acknowledgment> acks = 
                ackDAO.getAcknowledgments(doi, getContexts, getSource);
            for (Acknowledgment ack : acks) {
                doc.addAcknowledgment(ack);
            }
        }
        if (getKeywords) {
            List<Keyword> keywords = keywordDAO.getKeywords(doi, getSource);
            for (Keyword keyword : keywords) {
                doc.addKeyword(keyword);
            }
        }
        if (getTags) {
            List<Tag> tags = tagDAO.getTags(doi);
            doc.setTags(tags);
        }

        return doc;
        
    }  //- getDocumentFromDB

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CSXOperations#getDocumentFromDB(java.lang.String, boolean, boolean)
     */
    @Override
    public Document getDocumentFromDB(String doi, boolean getContexts,
            boolean getSource) throws DataAccessException {

        return getDocumentFromDB(doi, true, getContexts, getSource,
                true, true, true);

    }  //- getDocumentFromDB

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CSXOperations#getDocumentFromDB(java.lang.String)
     */
    @Override
    public Document getDocumentFromDB(String doi) throws DataAccessException {
        
        return getDocumentFromDB(doi, false, false, false, false, false, false);
    } //- getDocumentFromDB

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CSXOperations#getDocumentFromXML(java.lang.String)
     */
    @Override
    public Document getDocumentFromXML(String doi)
    throws DataAccessException, IOException {
        Document doc = docDAO.getDocument(doi, false);
        String repID = doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY);
        String relPath = FileNamingUtils.buildXMLPath(doi);
        return fileSysDAO.getDocFromXML(repID, relPath);

    }  //- getDocumentFromXML

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CSXOperations#updateDocumentData(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void updateDocumentData(Document doc)
    throws DataAccessException, IOException {
        updateDocumentData(doc, true, true, true, true);
    } //- updateDocumentData
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.logic.CSXOperations#updateDocumentData(edu.psu.citeseerx.domain.Document, boolean, boolean, boolean, boolean)
     */
    @Override
    public void updateDocumentData(Document doc, boolean updateAuthors,
            boolean updateCitations,
            boolean updateAcknowledgements, boolean updateKeywords)
    throws DataAccessException, IOException {
        
        String doi = doc.getDatum(Document.DOI_KEY);

        docDAO.updateDocument(doc);
        //fileDAO.updateFileInfo(doi, doc.getFileInfo(), con);

        if (updateAuthors) {
            authDAO.deleteAuthors(doi);
            insertAuthors(doi, doc.getAuthors());
        }

        if (updateCitations) {
            citeDAO.deleteCitations(doi);
            insertCitations(doi, doc.getCitations());
        }

        if (updateAcknowledgements) {
            ackDAO.deleteAcknowledgments(doi);
            insertAcknowledgments(doi, doc.getAcknowledgments());
        }
        
        if (updateKeywords) {
            keywordDAO.deleteKeywords(doi);
            insertKeywords(doi, doc.getKeywords());
        }

    }  //- updateDocumentData
    
    

    ///////////////////////////////////////////////////////
    //  Acknowledgment DAO                               
    ///////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#deleteAcknowledgment(java.lang.Long)
     */
    @Override
    public void deleteAcknowledgment(Long ackID) throws DataAccessException {
        ackDAO.deleteAcknowledgment(ackID);
    }  //- deleteAcknowledgment
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#deleteAcknowledgments(java.lang.String)
     */
    @Override
    public void deleteAcknowledgments(String doi) throws DataAccessException {
        ackDAO.deleteAcknowledgments(doi);
    }  //- deleteAcknowledgments

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#deleteAckContexts(java.lang.Long)
     */
    @Override
    public void deleteAckContexts(Long ackID) throws DataAccessException {
        ackDAO.deleteAckContexts(ackID);
    }  //- deleteAckContexts


    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#getAcknowledgments(java.lang.String, boolean, boolean)
     */
    @Override
    public List<Acknowledgment> getAcknowledgments(String doi, 
            boolean getContexts, boolean getSource) throws DataAccessException {
        return ackDAO.getAcknowledgments(doi, getContexts, getSource);
    }  //- getAcknowledgments


    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#getAckContexts(java.lang.Long)
     */
    @Override
    public List<String> getAckContexts(Long ackID) throws DataAccessException {
        return ackDAO.getAckContexts(ackID);
    }  //- getAckContexts

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#insertAcknowledgment(java.lang.String, edu.psu.citeseerx.domain.Acknowledgment)
     */
    @Override
    public void insertAcknowledgment(String doi, Acknowledgment ack)
    throws DataAccessException {
        ackDAO.insertAcknowledgment(doi, ack);
    }  //- insertAcknowledgment

    /**
     * Insert each one of the given acknowledgments associating them to the
     * given document identifier. 
     * @param doi
     * @param acks
     * @throws DataAccessException
     */
    private void insertAcknowledgments(String doi, List<Acknowledgment> acks)
    throws DataAccessException {
        for (Acknowledgment ack : acks) {
            insertAcknowledgment(doi, ack);
        }
    } //- insertAcknowledgments
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#insertAckContexts(java.lang.Long, java.util.List)
     */
    @Override
    public void insertAckContexts(Long ackID, List<String> contexts)
    throws DataAccessException {
        ackDAO.insertAckContexts(ackID, contexts);
    }  //- insertAckContexts


    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#setAckCluster(edu.psu.citeseerx.domain.Acknowledgment, java.lang.Long)
     */
    @Override
    public void setAckCluster(Acknowledgment ack, Long clusterID)
    throws DataAccessException {
        ackDAO.setAckCluster(ack, clusterID);
    }  //- setAckCluster


    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AckDAO#updateAcknowledgment(edu.psu.citeseerx.domain.Acknowledgment)
     */
    @Override
    public void updateAcknowledgment(Acknowledgment ack)
    throws DataAccessException {
        ackDAO.updateAcknowledgment(ack);
    }  //- updateAcknowledgment


    ///////////////////////////////////////////////////////
    //  Author DAO                               
    ///////////////////////////////////////////////////////

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#getDocAuthors(java.lang.String, boolean)
     */
    @Override
    public List<Author> getDocAuthors(String docID, boolean getSource)
            throws DataAccessException {
        return authDAO.getDocAuthors(docID, getSource);
    }  //- getdocAuthors
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#insertAuthor(java.lang.String, edu.psu.citeseerx.domain.Author)
     */
    @Override
    public void insertAuthor(String docID, Author auth)
    throws DataAccessException {
        authDAO.insertAuthor(docID, auth);
    }  //- insertAuthor

    /**
     * Stores the given authors associating them to the given document 
     * identifier. 
     * @param doi
     * @param authors
     * @throws DataAccessException
     */
    private void insertAuthors(String doi, List<Author> authors) 
    throws DataAccessException {
        for (Author author : authors) {
            insertAuthor(doi, author);
        }
    } //- insertAuthors
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#updateAuthor(edu.psu.citeseerx.domain.Author)
     */
    @Override
    public void updateAuthor(Author auth) throws DataAccessException {
        authDAO.updateAuthor(auth);
    }  //- updateAuthor

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#setAuthCluster(edu.psu.citeseerx.domain.Author, java.lang.Long)
     */
    @Override
    public void setAuthCluster(Author auth, Long clusterID)
    throws DataAccessException {
        authDAO.setAuthCluster(auth, clusterID);
    }  //- setAuthCluster

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#deleteAuthors(java.lang.String)
     */
    @Override
    public void deleteAuthors(String docID) throws DataAccessException {
        authDAO.deleteAuthors(docID);
    }  //- deleteAuthors

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AuthorDAO#deleteAuthor(java.lang.Long)
     */
    @Override
    public void deleteAuthor(Long authorID) throws DataAccessException {
        authDAO.deleteAuthor(authorID);
    }  //- deleteAuthor



    ///////////////////////////////////////////////////////
    //  Citation DAO                               
    ///////////////////////////////////////////////////////


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#getCitations(java.lang.String, boolean)
     */
    @Override
    public List<Citation> getCitations(String docID, boolean getContexts)
            throws DataAccessException {
        return citeDAO.getCitations(docID, getContexts);
    }  //- getCitations    
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#getCitations(long, int)
     */
    @Override
    public List<Citation> getCitations(long startID, int n)
    throws DataAccessException {
        return citeDAO.getCitations(startID, n);
    }  //- getCitations

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#getCitationsForCluster(java.lang.Long)
     */
    @Override
    public List<Citation> getCitationsForCluster(Long clusterid)
    throws DataAccessException {
        return citeDAO.getCitationsForCluster(clusterid);
    }  //- getCitationsForCluster
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#getCitation(long)
     */
    @Override
    public Citation getCitation(long id) throws DataAccessException {
        return citeDAO.getCitation(id);
    }  //- getCitation

    /**
     * Stores all the given citations associating them to the given document
     * @param DOI
     * @param citations
     * @throws DataAccessException
     */
    private void insertCitations(String DOI, List<Citation> citations)
    throws DataAccessException {
        for (Citation citation : citations) {
            citeDAO.insertCitation(DOI, citation);
        }
    }  //- insertCitations
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#insertCitation(java.lang.String, edu.psu.citeseerx.domain.Citation)
     */
    @Override
    public void insertCitation(String DOI, Citation citation)
    throws DataAccessException {
        citeDAO.insertCitation(DOI, citation);
    } //- insertCitation


    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#getCiteContexts(java.lang.Long)
     */
    @Override
    public List<String> getCiteContexts(Long citationID)
    throws DataAccessException {
        return citeDAO.getCiteContexts(citationID);
    }  //- getCitationContexts

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#insertCiteContexts(java.lang.Long, java.util.List)
     */
    @Override
    public void insertCiteContexts(Long citationID, List<String> contexts)
    throws DataAccessException {
        citeDAO.insertCiteContexts(citationID, contexts);
    }  //- insertCitationContexts

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#setCiteCluster(edu.psu.citeseerx.domain.Citation, java.lang.Long)
     */
    @Override
    public void setCiteCluster(Citation citation, Long clusterID)
    throws DataAccessException {
        citeDAO.setCiteCluster(citation, clusterID);
    }  //- setCitationCluster

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#deleteCitations(java.lang.String)
     */
    @Override
    public void deleteCitations(String DOI) throws DataAccessException {
        citeDAO.deleteCitations(DOI);
    }  //- deleteCitations

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#deleteCitation(java.lang.Long)
     */
    @Override
    public void deleteCitation(Long citationID) throws DataAccessException {
        citeDAO.deleteCitation(citationID);
    }  //- deleteCitation

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CitationDAO#deleteCiteContexts(java.lang.Long)
     */
    @Override
    public void deleteCiteContexts(Long citationID)
    throws DataAccessException {
        citeDAO.deleteCiteContexts(citationID);
    }  //- deleteCitationContexts
    
    /* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.CitationDAO#getNumberOfCitationsRecords()
	 */
	public Integer getNumberOfCitationsRecords() throws DataAccessException {
		return citeDAO.getNumberOfCitationsRecords();
	} //- getNumberOfCitationsRecords


    ///////////////////////////////////////////////////////
    //  Document DAO                               
    ///////////////////////////////////////////////////////

	/* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getDocument(java.lang.String, boolean)
     */
    @Override
    public Document getDocument(String docID, boolean getSource)
            throws DataAccessException {
        return docDAO.getDocument(docID, getSource);
    } //- getDocument
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#updateDocument(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void updateDocument(Document doc) throws DataAccessException {
        docDAO.updateDocument(doc);
    } //- updateDocument
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#insertDocument(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void insertDocument(Document doc) throws DataAccessException {
        docDAO.insertDocument(doc);
    } //- insertDocument
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#insertDocumentSrc(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void insertDocumentSrc(Document doc) throws DataAccessException {
        docDAO.insertDocumentSrc(doc);
    } //- insertDocumentSrc
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#setDocState(edu.psu.citeseerx.domain.Document, int)
     */
    @Override
    public void setDocState(Document doc, int toState)
    throws DataAccessException {
        docDAO.setDocState(doc, toState);
    }  //- setDocState

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#setDocCluster(edu.psu.citeseerx.domain.Document, java.lang.Long)
     */
    @Override
    public void setDocCluster(Document doc, Long clusterID)
    throws DataAccessException {
        docDAO.setDocCluster(doc, clusterID);
    }  //- setDocCluster
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#setDocNcites(edu.psu.citeseerx.domain.Document, int)
     */
    @Override
    public void setDocNcites(Document doc, int ncites)
    throws DataAccessException {
        docDAO.setDocNcites(doc, ncites);
    }  //- setNcites

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getNumberOfDocumentRecords()
     */
    @Override
    public Integer getNumberOfDocumentRecords() throws DataAccessException {
        return docDAO.getNumberOfDocumentRecords();
    }  //- getNumberOfDocumentRecords
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getDOIs(java.lang.String, int)
     */
    @Override
    public List<String> getDOIs(String start, int amount)
    throws DataAccessException {
        return docDAO.getDOIs(start, amount);
    }  //- getDOIs

    /* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.DocumentDAO#getSetDOIs(java.util.Date, java.util.Date, java.lang.String, int)
	 */
	public List<DOIInfo> getSetDOIs(Date start, Date end, String prev, 
			int amount) throws DataAccessException {
		return docDAO.getSetDOIs(start, end, prev, amount);
	} //- getSetDOIs


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getSetDOICount(java.util.Date, java.util.Date, java.lang.String)
     */
    public Integer getSetDOICount(Date start, Date end, String prev) 
    throws DataAccessException {
    	return docDAO.getSetDOICount(start, end, prev);
    } //- getSetDOICount

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getCrawledDOIs(java.util.Date, java.util.Date, java.lang.String, int)
     */
    public List<String> getCrawledDOIs(Date start, Date end, String lastDOI,
            int amount) throws DataAccessException {
        return docDAO.getCrawledDOIs(start, end, lastDOI, amount);
    } //- getCrawledDOIs
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getLastDocuments(java.lang.String, int)
     */
    public List<String> getLastDocuments(String lastDOI, int amount)
            throws DataAccessException {
        return docDAO.getLastDocuments(lastDOI, amount);
    } //- getLastDocuments
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.DocumentDAO#getKeyphrase(java.lang.String)
     */
    public List<String> getKeyphrase(String doi)
            throws DataAccessException {
        return docDAO.getKeyphrase(doi);
    } //- getKeyPhrase

    ///////////////////////////////////////////////////////
    //  File DAO                               
    ///////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#insertChecksum(edu.psu.citeseerx.domain.CheckSum)
     */
    @Override
    public void insertChecksum(CheckSum checksum) {
        fileDAO.insertChecksum(checksum);
    }  //- insertChecksum
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#insertChecksums(java.lang.String, java.util.List)
     */
    @Override
    public void insertChecksums(String doi, List<CheckSum> checksums) {
        fileDAO.insertChecksums(doi, checksums);
    }  //- insertChecksums

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#getChecksums(java.lang.String)
     */
    @Override
    public List<CheckSum> getChecksums(String sha1) throws DataAccessException {
        return fileDAO.getChecksums(sha1);
    }  //- getChecksums
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#deleteChecksums(java.lang.String)
     */
    @Override
    public void deleteChecksums(String doi) throws DataAccessException {
        fileDAO.deleteChecksums(doi);
    }
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#updateChecksums(java.lang.String, java.util.List)
     */
    @Override
    public void updateChecksums(String doi, List<CheckSum> checksums)
    throws DataAccessException {
        fileDAO.updateChecksums(doi, checksums);
    } //- updateChecksums
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#getChecksumsForDocument(java.lang.String)
     */
    @Override
    public List<CheckSum> getChecksumsForDocument(String doi) {
        return fileDAO.getChecksumsForDocument(doi);
    } //- getChecksumsForDocument


    ///////////////////////////////////////////////////////
    //  Keyword DAO                               
    ///////////////////////////////////////////////////////


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.KeywordDAO#getKeywords(java.lang.String, boolean)
     */
    @Override
    public List<Keyword> getKeywords(String doi, boolean getSource)
            throws DataAccessException {
        return keywordDAO.getKeywords(doi, getSource);
    }  //- getKeywords

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.KeywordDAO#insertKeyword(java.lang.String, edu.psu.citeseerx.domain.Keyword)
     */
    @Override
    public void insertKeyword(String docID, Keyword keyword)
    throws DataAccessException {
        keywordDAO.insertKeyword(docID, keyword);
    }  //- insertKeywords

    /**
     * Inserts the given keywords associating them to the given document
     * identifier.
     * @param docID
     * @param keywords
     * @throws DataAccessException
     */
    private void insertKeywords(String docID, List<Keyword> keywords)
    throws DataAccessException {
        for (Keyword keyword : keywords) {
            insertKeyword(docID, keyword);
        }
    } //- insertKeywords

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.KeywordDAO#updateKeyword(java.lang.String, edu.psu.citeseerx.domain.Keyword)
     */
    @Override
    public void updateKeyword(String docID, Keyword keyword)
    throws DataAccessException {
        keywordDAO.updateKeyword(docID, keyword);
    }  //- updateKeyword
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.KeywordDAO#deleteKeyword(java.lang.String, edu.psu.citeseerx.domain.Keyword)
     */
    @Override
    public void deleteKeyword(String docID, Keyword keyword)
    throws DataAccessException {
        keywordDAO.deleteKeyword(docID, keyword);
    }  //- deleteKeyword
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.KeywordDAO#deleteKeywords(java.lang.String)
     */
    @Override
    public void deleteKeywords(String docID) throws DataAccessException {
        keywordDAO.deleteKeywords(docID);
    }  //- deleteKeywords


    ///////////////////////////////////////////////////////
    //  UserCorrection DAO                               
    ///////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////
    //  Version DAO                               
    ///////////////////////////////////////////////////////


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#setVersion(java.lang.String, int)
     */
    @Override
    public void setVersion(String doi, int version) throws DataAccessException,
            IOException {
        Document doc = fileSysDAO.getDocVersion(doi, version);
        updateDocumentData(doc);
        versionDAO.deprecateVersionsAfter(doi, doc.getVersion());
        
    }  //- setVersion

    /*
     * NOTE:
     * Why this one is not in the interface?
     */
    public void setVersion(String doi, String name)
    throws DataAccessException, IOException {

        Document doc = fileSysDAO.getDocVersion(doi, name);
        versionDAO.deprecateVersionsAfter(doi, doc.getVersion());
        updateDocumentData(doc);
        
    }  //- setVersion

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#insertVersion(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public boolean insertVersion(Document doc)
    throws DataAccessException, IOException {
        
        versionDAO.insertVersion(doc);
        fileSysDAO.writeVersion(doc);
        return true;
        
    }  //- createNewVersion
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#setVersionName(java.lang.String, int, java.lang.String)
     */
    @Override
    public void setVersionName(String doi, int version, String name)
    throws DataAccessException {
        versionDAO.setVersionName(doi, version, name);
    }  //- setVersionName

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#setVersionSpam(java.lang.String, int, boolean)
     */
    @Override
    public void setVersionSpam(String doi, int version, boolean isSpam)
    throws DataAccessException {
        versionDAO.setVersionSpam(doi, version, isSpam);
    }  //- setSpam
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#deprecateVersion(java.lang.String, int)
     */
    @Override
    public void deprecateVersion(String doi, int version)
    throws DataAccessException {
        versionDAO.deprecateVersion(doi, version);
    } //- deprecateVersion
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#deprecateVersionsAfter(java.lang.String, int)
     */
    @Override
    public void deprecateVersionsAfter(String doi, int version)
    throws DataAccessException {
        versionDAO.deprecateVersionsAfter(doi, version);
    } //- deprecateVersionsAfter
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#createNewVersion(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void createNewVersion(Document doc) throws DataAccessException {
        versionDAO.createNewVersion(doc);
    } //- createNewVersion
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#insertCorrection(java.lang.String, java.lang.String, int)
     */
    @Override
    public void insertCorrection(String userid, String paperid, int version)
    throws DataAccessException {
        versionDAO.insertCorrection(userid, paperid, version);
    } //- createNewVersion
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.VersionDAO#getCorrector(java.lang.String, int)
     */
    @Override
    public String getCorrector(String paperid, int version)
    throws DataAccessException {
        return versionDAO.getCorrector(paperid, version);
    } //- createNewVersion


    ///////////////////////////////////////////////////////
    //  FileSys DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocVersion(java.lang.String, int)
     */
    @Override
    public Document getDocVersion(String doi, int version)
    throws DataAccessException, IOException {
        return fileSysDAO.getDocVersion(doi, version);
    }  //- getDocVersion

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocVersion(java.lang.String, java.lang.String)
     */
    @Override
    public Document getDocVersion(String doi, String name)
    throws DataAccessException, IOException {
        return fileSysDAO.getDocVersion(doi, name);
    }  //- getDocVersion
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getFileInputStream(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException {
        return fileSysDAO.getFileInputStream(doi, repID, type);
    } //- getFileInputStream
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getPdfReader(java.lang.String, java.lang.String)
     */
    public PdfReader getPdfReader(String doi, String repID)
            throws IOException {
        return fileSysDAO.getPdfReader(doi, repID);
    } //- getPdfReader

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#writeXML(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void writeXML(Document doc) throws IOException {
        fileSysDAO.writeXML(doc);
    } //- writeXML
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#writeVersion(edu.psu.citeseerx.domain.Document)
     */
    @Override
    public void writeVersion(Document doc) throws IOException {
        fileSysDAO.writeVersion(doc);
    } //- writeVersion
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocFromXML(java.lang.String, java.lang.String)
     */
    @Override
    public Document getDocFromXML(String repID, String relPath)
    throws IOException {
        return fileSysDAO.getDocFromXML(repID, relPath);
    } //- getDocFromXML
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getFileTypes(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getFileTypes(String doi, String repID)
    throws IOException {
        return fileSysDAO.getFileTypes(doi, repID);
    } //- getFileTypes

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getRepositoryID(java.lang.String)
     */
    @Override
    public String getRepositoryID(String doi) {
        return fileSysDAO.getRepositoryID(doi);
    } //- getRepositoryID
    
    ///////////////////////////////////////////////////////
    //  Hub DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#insertHub(edu.psu.citeseerx.domain.Hub)
     */
    @Override
    public long insertHub(Hub hub) throws DataAccessException {
        Hub existingHub = hubDAO.getHub(hub.getUrl());
        if (existingHub == null) {
            return hubDAO.insertHub(hub);
        } else {
            hubDAO.updateHub(hub);
            return 0;
        }
    }  //- insertHub
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#addHubMapping(edu.psu.citeseerx.domain.Hub, java.lang.String, java.lang.String)
     */
    @Override
    public void addHubMapping(Hub hub, String url, String doi)
    throws DataAccessException {
        hubDAO.addHubMapping(hub, url, doi);
    }  //- addHubMapping
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getHubs(java.lang.String)
     */
    @Override
    public List<Hub> getHubs(String doi) throws DataAccessException {
        return hubDAO.getHubs(doi);
    }  //- getHubs
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getHubsForUrl(java.lang.String)
     */
    @Override
    public List<Hub> getHubsForUrl(String url) throws DataAccessException {
        return hubDAO.getHubsForUrl(url);
    } //- getHubsForUrl

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getHub(java.lang.String)
     */
    @Override
    public Hub getHub(String url) throws DataAccessException {
        return hubDAO.getHub(url);
    } //- getHub
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#updateHub(edu.psu.citeseerx.domain.Hub)
     */
    @Override
    public void updateHub(Hub hub) throws DataAccessException {
        hubDAO.updateHub(hub);
    } //- updateHub
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#insertHubMapping(long, long)
     */
    @Override
    public void insertHubMapping(long urlID, long hubID)
    throws DataAccessException {
        hubDAO.insertHubMapping(urlID, hubID);
    } //- insertHubMapping
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#insertUrl(java.lang.String, java.lang.String)
     */
    @Override
    public long insertUrl(String doi, String url) {
        return hubDAO.insertUrl(doi, url);
    } //- insertUrl
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getUrls(java.lang.String)
     */
    @Override
    public List<String> getUrls(String doi) {
        return hubDAO.getUrls(doi);
    } //- getUrls
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.HubDAO#getPaperIdsFromHubUrl(java.lang.String)
     */
    public List<String> getPaperIdsFromHubUrl(String hubUrl)
            throws DataAccessException {
        // TODO Auto-generated method stub
        return hubDAO.getPaperIdsFromHubUrl(hubUrl);
    } //- getPaperIdsFromHubUrl
    
    ///////////////////////////////////////////////////////
    //  CiteChart DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CiteChartDAO#checkChartUpdateRequired(java.lang.String)
     */
    @Override
    public boolean checkChartUpdateRequired(String doi)
    throws DataAccessException {
        return citeChartDAO.checkChartUpdateRequired(doi);
    }  //- chartUpdateRequired
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CiteChartDAO#insertChartUpdate(java.lang.String, int, java.lang.String)
     */
    @Override
    public void insertChartUpdate(String doi, int lastNcites, String chartData)
    throws DataAccessException {
        citeChartDAO.insertChartUpdate(doi, lastNcites, chartData);
    }  //- insertChartUpdate
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.CiteChartDAO#getCiteChartData(java.lang.String)
     */
    @Override
    public String getCiteChartData(String doi) throws DataAccessException {
        return citeChartDAO.getCiteChartData(doi);
    } //- getCiteChartData
    
    
    ///////////////////////////////////////////////////////
    //  Legacy ID DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.LegacyIDDAO#getNewID(int)
     */
    @Override
    public String getNewID(int legacyID) throws DataAccessException {
        return legacyIDDAO.getNewID(legacyID);
    }  //- getNewID
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.LegacyIDDAO#insertLegacyIDMapping(java.lang.String, int)
     */
    @Override
    public void insertLegacyIDMapping(String csxID, int legacyID)
    throws DataAccessException {
        legacyIDDAO.insertLegacyIDMapping(csxID, legacyID);
    }  //- insertLegacyIDMapping

    ///////////////////////////////////////////////////////
    //  Tag DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TagDAO#addTag(java.lang.String, java.lang.String)
     */
    @Override
    public void addTag(String paperid, String tag) throws DataAccessException {
        tagDAO.addTag(paperid, tag);
    }  //- addTag
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TagDAO#deleteTag(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteTag(String paperid, String tag) throws DataAccessException {
        tagDAO.deleteTag(paperid, tag);
    }  //- deleteTag
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TagDAO#getTags(java.lang.String)
     */
    @Override
    public List<Tag> getTags(String paperid) throws DataAccessException {
        return tagDAO.getTags(paperid);
    }  //- getTags
    
    
    ///////////////////////////////////////////////////////
    //  Admin DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AdminDAO#setBanner(java.lang.String)
     */
    @Override 
    public void setBanner(String banner) throws DataAccessException {
        adminDAO.setBanner(banner);
    }  //- setBanner
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AdminDAO#getBanner()
     */
    @Override
    public String getBanner() throws DataAccessException {
        return adminDAO.getBanner();
    }  //- getBanner

    ///////////////////////////////////////////////////////
    //  ExternalLink DAO                               
    ///////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#AddExternalLink(edu.psu.citeseerx.domain.ExternalLink)
     */
    @Override
    public void addExternalLink(ExternalLink eLink) throws DataAccessException {
        externalLinkDAO.addExternalLink(eLink);
    } //- AddExternalLink

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#addLinkType(edu.psu.citeseerx.domain.LinkType)
     */
    @Override
    public void addLinkType(LinkType link) throws DataAccessException {
        externalLinkDAO.addLinkType(link);
    } //- addLinkType

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getExternalLiks(java.lang.String)
     */
    @Override
    public List<ExternalLink> getExternalLinks(String doi)
            throws DataAccessException {
        return externalLinkDAO.getExternalLinks(doi);
    } //- getExternalLiks

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getLinkType(java.lang.String)
     */
    @Override
    public LinkType getLinkType(String label) throws DataAccessException {
        return externalLinkDAO.getLinkType(label);
    } //- getLinkType

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getLinkTypes()
     */
    @Override
    public List<LinkType> getLinkTypes() throws DataAccessException {
        return externalLinkDAO.getLinkTypes();
    } //- getLinkTypes

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#DeleteLinkType(edu.psu.citeseerx.domain.LinkType)
     */
    @Override
    public void deleteLinkType(LinkType link) throws DataAccessException {
        externalLinkDAO.deleteLinkType(link);
    } //- DeleteLinkType

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#UpdateLinkType(edu.psu.citeseerx.domain.LinkType, java.lang.String)
     */
    @Override
    public void updateLinkType(LinkType link, String oldLabel)
            throws DataAccessException {
        externalLinkDAO.updateLinkType(link, oldLabel);
    } //- updateLinkType

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#updateExternalLink(edu.psu.citeseerx.domain.ExternalLink)
     */
    @Override
    public void updateExternalLink(ExternalLink extLink)
            throws DataAccessException {
        externalLinkDAO.updateExternalLink(extLink);
    } //- updateExternalLink

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getPapersNoELink(java.lang.String, java.lang.String, java.lang.Long)
     */
    @Override
    public List<String> getPapersNoELink(String label, String lastID,
            Long amount) {
        return externalLinkDAO.getPapersNoELink(label, lastID, amount);
    } //- getPapersNoELink
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getExternalLinkExist(java.lang.String, java.lang.String)
     */
    @Override
    public boolean getExternalLinkExist(String label, String doi)
            throws DataAccessException {
        return externalLinkDAO.getExternalLinkExist(label, doi);
    } //- getExternalLinkExist


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#deleteExternalLink(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteExternalLink(String doi, String label)
            throws DataAccessException {
        externalLinkDAO.deleteExternalLink(doi, label);
    } //- deleteExternalLink

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.ExternalLinkDAO#getLink(java.lang.String, java.lang.String)
     */
    @Override
    public ExternalLink getLink(String doi, String label)
            throws DataAccessException {
        return externalLinkDAO.getLink(doi, label);
    } //- getLink

    ///////////////////////////////////////////////////////
    //  Table DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TableDAO#getTable(java.lang.Long)
     */
    @Override
    public Table getTable(Long id) throws DataAccessException {
        return tableDAO.getTable(id);
    } //- getTable
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TableDAO#countTable()
     */
    @Override
    public Integer countTable() throws DataAccessException {
        return tableDAO.countTable();
    } //- countTable

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TableDAO#insertTable(edu.psu.citeseerx.domain.Table)
     */
    @Override
    public void insertTable(Table tobj) throws DataAccessException {
    	tableDAO.insertTable(tobj);
    } //- insertTable
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TableDAO#deleteTable(java.lang.Long)
     */
    @Override
    public void deleteTable(Long id) throws DataAccessException {
    	tableDAO.deleteTable(id);
	} //- deleteTable
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TableDAO#updateTableIndexTime()
     */
    @Override
    public void updateTableIndexTime() throws DataAccessException {
    	tableDAO.updateTableIndexTime();
    } //- updateTableIndexTime
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TableDAO#getUpdatedTables(java.sql.Date)
     */
    @Override
    public List<Table> getUpdatedTables(java.sql.Date dt)
    throws DataAccessException {
    	return tableDAO.getUpdatedTables(dt);
    } //- getUpdatedTables
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TableDAO#getTables(java.lang.String, boolean)
     */
    @Override
    public List<Table> getTables(String id, boolean idtype)
    throws DataAccessException {
    	return tableDAO.getTables(id, idtype);
    } //- getTables
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.TableDAO#lastTableIndexTime()
     */
    @Override
    public java.sql.Date lastTableIndexTime() throws DataAccessException {
    	return tableDAO.lastTableIndexTime();
    } //-lastTableIndexTime
    
    ///////////////////////////////////////////////////////
    //  UniqueAuthor DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#getAuthor(java.lang.String)
     */
    @Override
    public UniqueAuthor getAuthor(String aid) throws DataAccessException {
        return uauthDAO.getAuthor(aid);
    }  //- getdocAuthors
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#getAuthVarnames(java.lang.String)
     */
    @Override
    public List<String> getAuthVarnames(String aid) throws DataAccessException {
        return uauthDAO.getAuthVarnames(aid);
    }  //- getAuthVarnames

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#getAuthDocsOrdByCites(java.lang.String)
     */
    @Override
    public List<ThinDoc> getAuthDocsOrdByCites(String aid)
    throws DataAccessException {
        return uauthDAO.getAuthDocsOrdByCites(aid);
    }  //- getAuthDocs

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#getAuthDocsOrdByYear(java.lang.String)
     */
    @Override
    public List<ThinDoc> getAuthDocsOrdByYear(String aid)
    throws DataAccessException {
        return uauthDAO.getAuthDocsOrdByYear(aid);
    }  //- getAuthDocs

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#getAuthorRecords(java.lang.String, java.util.List<java.lang.Integer>)
     */
    @Override	
	public List<Integer> getAuthorRecords(String aid) throws DataAccessException {
		return uauthDAO.getAuthorRecords(aid);
	} //- getAuthorsByPapers

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#getAuthorRecordsByPapers(java.lang.String, java.util.List<java.lang.Integer>)
     */
    @Override	
	public List<Integer> getAuthorRecordsByPapers(String aid, List<Integer> papers) throws DataAccessException {
		return uauthDAO.getAuthorRecordsByPapers(aid, papers);
	} //- getAuthorsByPapers

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#updateAuthNdocs(java.lang.String)
     */
	public void updateAuthNdocs(String aid) throws DataAccessException {
		uauthDAO.updateAuthNdocs(aid);
	} //- updateAuthNdocs

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#updateAuthNcites(java.lang.String)
     */
	public void updateAuthNcites(String aid) throws DataAccessException {
		uauthDAO.updateAuthNcites(aid);
	} //- updateAuthNcites

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#updateAuthInfo(edu.psu.citeseerx.domain.UniqueAuthor)
     */
	public void updateAuthInfo(UniqueAuthor uauth) throws DataAccessException {
		uauthDAO.updateAuthInfo(uauth);
	} //- updateAuthInfo

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#removeAuthor(java.lang.String)
     */
	public void removeAuthor(String aid) throws DataAccessException {
		uauthDAO.removeAuthor(aid);
	} //- removeAuthor

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorDAO#moveAuthorRecord(java.lang.String, java.util.List<java.lang.String>)
     */
	public void moveAuthorRecords(String target_aid, List<Integer> author_records) throws DataAccessException {
		uauthDAO.moveAuthorRecords(target_aid, author_records);
	} //- moveAuthorRecords
		

    ///////////////////////////////////////////////////////
    //  UniqueAuthorVersion DAO                               
    ///////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorVersionDAO#updateUauthorInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public void updateUauthorInfo(String userid, String aid, String new_canname, String new_affil) 
		throws DataAccessException {
		uauthVersionDAO.updateUauthorInfo(userid, aid, new_canname, new_affil);
	} //- updateUauthorInfo

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorVersionDAO#mergeUauthors(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public void mergeUauthors(String userid, String aid1, String aid2)
		throws DataAccessException {
		uauthVersionDAO.mergeUauthors(userid, aid1, aid2);
	} //- mergeUauthors

    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.UniqueAuthorVersionDAO#removeUauthorPapers(java.lang.String, java.lang.String, java.util.List<java.lang.Integer>)
     */
    @Override
	public void removeUauthorPapers(String userid, String aid, List<Integer> papers) 
		throws DataAccessException {
		uauthVersionDAO.removeUauthorPapers(userid, aid, papers);
	} //- removeUauthorPapers

    ///////////////////////////////////////////////////////
    //  AlgorithmDAO                               
    ///////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#countAlgorithm()
     */
    @Override
    public Integer countAlgorithm() throws DataAccessException {
        return algorithmDAO.countAlgorithm();
    } //- countAlgorithm

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#getAlgorithm(java.lang.String)
     */
    @Override
    public Algorithm getAlgorithm(long id) throws DataAccessException {
        return algorithmDAO.getAlgorithm(id);
    } //- getAlgorithm

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#getUpdatedAlgorithms(java.util.Date)
     */
    @Override
    public List<Algorithm> getUpdatedAlgorithms(Date dt)
            throws DataAccessException {
        return algorithmDAO.getUpdatedAlgorithms(dt);
    } //- getUpdatedAlgorithms

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#insertAlgorithm(edu.psu.citeseerx.domain.Algorithm)
     */
    @Override
    public void insertAlgorithm(Algorithm oneAlgorithm)
            throws DataAccessException {
        algorithmDAO.insertAlgorithm(oneAlgorithm);
    } //- insertAlgorithm

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#lastAlgorithmIndexTime()
     */
    @Override
    public Date lastAlgorithmIndexTime() throws DataAccessException {
        return algorithmDAO.lastAlgorithmIndexTime();
    } //- lastAlgorithmIndexTime

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.AlgorithmDAO#updateAlgorithmIndexTime()
     */
    @Override
    public void updateAlgorithmIndexTime() throws DataAccessException {
        algorithmDAO.updateAlgorithmIndexTime();
    } //- updateAlgorithmIndexTime
    
    ///////////////////////////////////////////////////////
    //  GeneralStatistics                               
    ///////////////////////////////////////////////////////
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.GeneralStatistics#getAuthorsInCollection()
     */
    @Override
    public long getAuthorsInCollection() {
        return generalStatistics.getAuthorsInCollection();
    } //- getAuthorsInCollection
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.GeneralStatistics#getCitationsInCollection()
     */
    @Override
    public long getCitationsInCollection() {
    	return generalStatistics.getCitationsInCollection();
    } //- getCitationsInCollection
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.GeneralStatistics#getDocumentsInCollection()
     */
    @Override
    public long getDocumentsInCollection() {
    	return generalStatistics.getDocumentsInCollection();
    } //- getDocumentsInCollection
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.GeneralStatistics#getPublicDocumentsInCollection()
     */
    @Override
    public long getPublicDocumentsInCollection() {
    	return generalStatistics.getPublicDocumentsInCollection();
    } //- getPublicDocumentsInCollection
    
    /*
     * (non-Javadoc)
     * @see edu.psu.citeseerx.dao2.GeneralStatistics#getDisambiguatedAuthorsInCollection()
     */
    @Override
    public long getDisambiguatedAuthorsInCollection() {
    	return generalStatistics.getDisambiguatedAuthorsInCollection();
    } //- getDisambiguatedAuthorsInCollection
    
    @Override
    public long getUniqueAuthorsInCollection() {
    	return generalStatistics.getUniqueAuthorsInCollection();
    } //- getUniqueAuthorsInCollection
    
    @Override
    public long getNumberofUniquePublicDocuments() {
    	return generalStatistics.getNumberofUniquePublicDocuments();
    } //- getNumberofUniquePublicDocuments
    
    @Override
    public long getUniqueEntitiesInCollection() {
    	return generalStatistics.getUniqueEntitiesInCollection();
    } //- getUniqueEntitiesInCollection

    
    ///////////////////////////////////////////////////////
    //  RedirectPDFDAO                               
    ///////////////////////////////////////////////////////
	@Override
	public PDFRedirect getPDFRedirect(String doi) throws DataAccessException {
		return redirectPDFDAO.getPDFRedirect(doi);
	}

	@Override
	public void insertPDFRedirect(PDFRedirect pdfredirect)
			throws DataAccessException {
		
		redirectPDFDAO.insertPDFRedirect(pdfredirect);
	}

	@Override
	public void updatePDFRedirect(String doi, PDFRedirect pdfredirect)
			throws DataAccessException {
		redirectPDFDAO.updatePDFRedirect(doi, pdfredirect);
		
	}

	@Override
	public void updatePDFRedirectTemplate(String label, String urltemplate)
			throws DataAccessException {
		 redirectPDFDAO.updatePDFRedirectTemplate(label, urltemplate);
	}
    
}  //- class CSXDAOImpl
