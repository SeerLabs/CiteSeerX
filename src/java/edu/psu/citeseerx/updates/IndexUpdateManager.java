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
package edu.psu.citeseerx.updates;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.google.common.base.CharMatcher;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.common.SolrInputDocument;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.DomainTransformer;
import edu.psu.citeseerx.domain.Keyword;
import edu.psu.citeseerx.domain.Tag;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.utility.SafeText;

/**
 * Utilities for updating a Solr index to be consistent with the csx_citegraph
 * cluster table within the storage backend.  This class reads in cluster
 * records and, if the cluster is marked to have a corresponding document
 * record within the citeseerx papers table, with read in the paper record
 * as well to create XML in the Solr update format and send the XML to
 * a Solr server.
 * <br><br>
 * The IndexUpdateManager maintains a timestamp of the last update within
 * the csx_citegraph database so that only records modified since the last
 * update will be processed.
 * <br><br>
 * Deletions are handled by reading in records marked for deletion within
 * the csx_citegraph deletions table.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class IndexUpdateManager {

    protected final Log logger = LogFactory.getLog(getClass());
    private boolean redoAll;
    private SolrServer solrServer;
    private long lastIndexedCluster;
    private final int indexBatchSize = 1000;

    private URL solrUpdateUrl;

    public void setSolrURL(String solrUpdateUrl) throws MalformedURLException {
        this.solrUpdateUrl = new URL(solrUpdateUrl);
        this.solrServer = new ConcurrentUpdateSolrServer(solrUpdateUrl, 1000, 16);
    } //- setSolrURL


    private CSXDAO csxdao;

    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO


    private CiteClusterDAO citedao;

    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO

    public void setredoAll(boolean redoAll) {
        this.redoAll = redoAll;
    }

    private ExecutorService threadPool;

    {
        threadPool = Executors.newFixedThreadPool(16);
    }

    /**
     * Updates the index only for records that have corresponding document
     * records (document files within the CiteSeerX corpus).  This re-indexes
     * everything - not indexUpdateTime is recorded.
     * @throws IOException
     * @throws SolrServerException
     */
    public void indexInCollection() throws IOException, SolrServerException {
        int counter = 0;
        lastIndexedCluster = 0;

        while(true) {
            List<ThinDoc> docs = new ArrayList<ThinDoc>();
            docs = citedao.getClustersInCollection(new Long(lastIndexedCluster), indexBatchSize);
            if (docs.isEmpty()) {
                break;
            }

            counter += indexClusters(docs);
            solrServer.commit();
            System.out.println(counter + " documents added");
        }

        threadPool.shutdown();
        solrServer.optimize();
    }  //- indexInCollection

    /**
     * Indexes all cluster records modified since the last update time.
     * @throws SQLException
     * @throws IOException
     * @throws SolrServerException
     */
    public void indexAll() throws SQLException, IOException, SolrServerException {
        int counter = 0;
        Date currentTime = new Date(System.currentTimeMillis());
        Date lastUpdate;

        if(redoAll) {
            System.out.println("redo all document indexing...");
            lastUpdate = new Date((long)0);
        } else {
            System.out.println("index new documents...");
            lastUpdate = citedao.getLastIndexTime();
        }

        lastIndexedCluster = 0;

        while(true) {
            System.out.println("lastIndexedCluster=" + lastIndexedCluster);
            List<ThinDoc> docs = new ArrayList<ThinDoc>();

            docs = citedao.getClustersSinceTime(lastUpdate, new Long(lastIndexedCluster), indexBatchSize);
            if (docs.isEmpty()) {
                break;
            }

            counter += indexClusters(docs);
            solrServer.commit();
            System.out.println(counter + " documents added");
        }

        System.out.println("deletion...");
        processDeletions(currentTime);

        citedao.setLastIndexTime(currentTime);

        threadPool.shutdown();
        System.out.println("optimize...");
        solrServer.optimize();
    }  //- indexAll

    private int indexClusters(List<ThinDoc> docs) throws IOException, SolrServerException {
        ArrayList<Future> futures = new ArrayList<Future>();

        for (ThinDoc doc : docs) {
            Long clusterid = doc.getCluster();

            lastIndexedCluster = clusterid;
            futures.add(threadPool.submit(new TaskIndexCluster(doc, clusterid)));
        }

        try {
            for (Future f : futures) {
                f.get();
                System.out.print('.');
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();

        return docs.size();
    }

    private class TaskIndexCluster implements Callable<Void> {
        private final ThinDoc doc;
        Long clusterid;

        public TaskIndexCluster(ThinDoc doc, Long clusterid) {
            this.doc = doc;
            this.clusterid = clusterid;
        }

        public Void call() throws Exception {
            SolrInputDocument solrDoc = buildSolrInputDocumentOfCluster(doc, clusterid);
            solrServer.add(solrDoc);
            return null;
        }
    }

    private SolrInputDocument buildSolrInputDocumentOfCluster(ThinDoc doc, Long clusterid) throws IOException {
        List<Long> cites = new ArrayList<Long>();
        List<Long> citedby = new ArrayList<Long>();

        cites = citedao.getCitedClusters(clusterid);
        citedby = citedao.getCitingClusters(clusterid);

        if (doc.getInCollection() == false) {
            // We don't have the full document. Index the citation
            return buildSolrInputDocument(doc, cites, citedby);
        }

        Document fullDoc = findFullDocument(clusterid);
        if (fullDoc == null) {
            // The full document it's not public. Index the citation
            return buildSolrInputDocument(doc, cites, citedby);
        }

        // Index the full document
        fullDoc.setClusterID(clusterid);
        fullDoc.setNcites(doc.getNcites());
        return buildSolrInputDocument(fullDoc, cites, citedby);
    }

    private Document findFullDocument(Long clusterid) {
        List<String> dois = citedao.getPaperIDs(clusterid);

        for (String doi : dois) {
            Document fullDoc = csxdao.getDocumentFromDB(doi, false, false);

            if (fullDoc != null && fullDoc.isPublic()) {
                return fullDoc;
            }
        }

        return null;
    }

    /**
     * Builds a record in Solr update syntax corresponding to the
     * supplied parameters, and adds it to the supplied element
     * @param doc
     * @param cites
     * @param citedby
     * @throws IOException
     */
    private SolrInputDocument buildSolrInputDocument(Document doc, List<Long> cites,
            List<Long> citedby) throws IOException {
        String id = doc.getClusterID().toString();
        String doi = doc.getDatum(Document.DOI_KEY, Document.ENCODED);
        String title = doc.getDatum(Document.TITLE_KEY, Document.ENCODED);
        String venue = doc.getDatum(Document.VENUE_KEY, Document.ENCODED);
        String year = doc.getDatum(Document.YEAR_KEY, Document.ENCODED);
        String abs = doc.getDatum(Document.ABSTRACT_KEY, Document.ENCODED);
        String text = getText(doc);
        int ncites = doc.getNcites();
        int scites = doc.getSelfCites();

        List<Keyword> keys = doc.getKeywords();
        ArrayList<String> keywords = new ArrayList<String>();
        for (Keyword key : keys) {
            keywords.add(key.getDatum(Keyword.KEYWORD_KEY, Keyword.ENCODED));
        }

        List<Author> authors = doc.getAuthors();
        ArrayList<String> authorNames = new ArrayList<String>();
        for (Author author  : authors) {
            String name = author.getDatum(Author.NAME_KEY, Author.ENCODED);
            if (name != null) {
                authorNames.add(name);
            }
        }

        List<String> authorNorms = buildAuthorNorms(authorNames);

        StringBuffer citesBuffer = new StringBuffer();
        for (Iterator<Long> cids = cites.iterator(); cids.hasNext(); ) {
            citesBuffer.append(cids.next());
            if (cids.hasNext()) {
                citesBuffer.append(" ");
            }
        }

        StringBuffer citedbyBuffer = new StringBuffer();
        for (Iterator<Long> cids = citedby.iterator(); cids.hasNext(); ) {
            citedbyBuffer.append(cids.next());
            if (cids.hasNext()) {
                citedbyBuffer.append(" ");
            }
        }

        SolrInputDocument solrDoc = new SolrInputDocument();

        solrDoc.addField("id", id);
        if (doi != null) {
            solrDoc.addField("doi", doi);
            solrDoc.addField("incol", "1");
        } else {
            solrDoc.addField("incol", "0");
        }

        if (title != null) {
            solrDoc.addField("title", title);
        }

        if (venue != null) {
            solrDoc.addField("venue", venue);
        }

        if (abs != null) {
            solrDoc.addField("abstract", abs);
        }

        solrDoc.addField("ncites", Integer.toString(ncites));
        solrDoc.addField("scites", Integer.toString(scites));

        try {
            int year_i = Integer.parseInt(year);
            solrDoc.addField("year", Integer.toString(year_i));
        } catch (Exception e) { }

        for (String keyword : keywords) {
            solrDoc.addField("keyword", keyword);
        }

        for (String name : authorNames) {
            solrDoc.addField("author", name);
        }

        for (String norm : authorNorms) {
            solrDoc.addField("authorNorms", norm);
        }

        if (text != null) {
            solrDoc.addField("text", text);
        }

        solrDoc.addField("cites", citesBuffer.toString());
        solrDoc.addField("citedby", citedbyBuffer.toString());

        return solrDoc;
    } //- buildSolrInputDocument

    /**
     * Translates the supplied ThinDoc to a Document object and passes
     * control the the Document-based buildSolrInputDocument method.
     * @param thinDoc
     * @param cites
     * @param citedby
     * @throws IOException
     */
    private SolrInputDocument buildSolrInputDocument(ThinDoc thinDoc, List<Long> cites,
            List<Long> citedby) throws IOException {
        Document doc = DomainTransformer.toDocument(thinDoc);
        return buildSolrInputDocument(doc, cites, citedby);
    }  //- buildSolrInputDocument

    /**
     * Builds a list of author normalizations to create more flexible
     * author search.
     * @param names
     * @return
     */
    private static List<String> buildAuthorNorms(List<String> names) {
        HashSet<String> norms = new HashSet<String>();
        for (String name : names) {
            name = name.replaceAll("[^\\p{L} ]", "");
            StringTokenizer st = new StringTokenizer(name);
            String[] tokens = new String[st.countTokens()];
            int counter = 0;
            while(st.hasMoreTokens()) {
                tokens[counter] = st.nextToken();
                counter++;
            }
            norms.add(joinStringArray(tokens));

            if (tokens.length > 2) {

                String[] n1 = new String[tokens.length];
                for (int i=0; i<tokens.length; i++) {
                    if (i<tokens.length-1) {
                        n1[i] = Character.toString(tokens[i].charAt(0));
                    } else {
                        n1[i] = tokens[i];
                    }
                }

                String[] n2 = new String[tokens.length];
                for (int i=0; i<tokens.length; i++) {
                    if (i>0 && i<tokens.length-1) {
                        n2[i] = Character.toString(tokens[i].charAt(0));
                    } else {
                        n2[i] = tokens[i];
                    }
                }

                norms.add(joinStringArray(n1));
                norms.add(joinStringArray(n2));
            }

            if (tokens.length > 1) {

                String[] n3 = new String[2];
                n3[0] = tokens[0];
                n3[1] = tokens[tokens.length-1];

                String[] n4 = new String[2];
                n4[0] = Character.toString(tokens[0].charAt(0));
                n4[1] = tokens[tokens.length-1];

                norms.add(joinStringArray(n3));
                norms.add(joinStringArray(n4));
            }
        }

        ArrayList<String> normList = new ArrayList<String>();
        for (Iterator<String> it = norms.iterator(); it.hasNext(); ) {
            normList.add(it.next());
        }

        return normList;
    }  //- buildAuthorNorms

    private static String joinStringArray(String[] strings) {
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<strings.length; i++) {
            buffer.append(strings[i]);
            if (i<strings.length-1) {
                buffer.append(" ");
            }
        }

        return buffer.toString();
    }  //- joinStringArray

    /**
     * Fetches the full text of a document from the filesystem repository.
     * @param doc
     * @return
     * @throws IOException
     */
    private String getText(Document doc) throws IOException {
        String doi = doc.getDatum(Document.DOI_KEY);
        if (doi == null) {
            return null;
        }

        String repID = doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY);
        FileInputStream ins;
        try {
            try {
                ins = csxdao.getFileInputStream(doi, repID, "body");
            } catch (IOException e) {
                ins = csxdao.getFileInputStream(doi, repID, "txt");
            }
        } catch (IOException e) {
            return null;
        }

        String text = IOUtils.toString(ins, "UTF-8");
        text = SafeText.stripBadChars(text);
        text = CharMatcher.JAVA_ISO_CONTROL.replaceFrom(text, " ");
        try { ins.close(); } catch (IOException e) { }
        return text;
    }  //- getText

    private void processDeletions(Date currentTime) throws IOException, SolrServerException {
        List<Long> list = citedao.getDeletions(currentTime);
        for (Long id : list) {
            solrServer.deleteById(id.toString());
        }

        solrServer.commit();
        citedao.removeDeletions(currentTime);
    }  //- processDeletions

    /*
    public static void main(String[] args) throws Exception {

        DataSource dataSource = DBCPFactory.createDataSource("citeseerx");
        CSXDAO csxdao = new CSXDAO();
        csxdao.setDataSource(dataSource);

        DataSource cgDataSource = DBCPFactory.createDataSource("citegraph");
        CiteClusterDAO citedao = new CiteClusterDAOImpl();
        citedao.setDataSource(cgDataSource);

        DataSource cmDataSource = DBCPFactory.createDataSource("citemaster");
        CiteClusterDAO citemaster = new CiteClusterDAOImpl();
        citemaster.setDataSource(cmDataSource);

        IndexUpdateManager manager = new IndexUpdateManager();
        manager.setSolrURL("http://130.203.133.38:8983/solr/update");
        manager.setCSXDAO(csxdao);
        manager.setCiteClusterDAO(citemaster);
        manager.setCiteMaster(citemaster);
        manager.indexAll();
    }
    */
}  //- class IndexUpdateManager
