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

import edu.psu.citeseerx.dao2.logic.CitationStatisticsDAO;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.AuthorStatContainer;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Utilities for generating citations statistics for Document, Citations,
 * and Authors.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class StatisticsGenerator {

    private CitationStatisticsDAO citestat;
    
    public void setCitationStatisticsDAO(CitationStatisticsDAO citestat) {
        this.citestat = citestat;
    } //- setCitationStatisticsDAO
    
    
    private int numArticles = 10000;
    private int numAuthors = 10000;
    private int startingYear = 1990;
    
    /**
     * Set the max number of top-cited articles to process.
     * @param numArticles (default 10000).
     */
    public void setNumArticles(int numArticles) {
        this.numArticles = numArticles;
    } //- setNumArticles

    /**
     * Set the max number of top-cited authors to process.
     * @param numAuthors (default 10000).
     */
    public void setNumAuthors(int numAuthors) {
        this.numAuthors = numAuthors;
    } //- setNumAuthors
    
    /**
     * Set the year at which to start generating article and citation
     * statistics.
     * @param startingYear (default 1990).
     */
    public void setStartingYear(int startingYear) {
        this.startingYear = startingYear;
    } //- setStartingYear
    
    
    private String outputDir = "stats";
    
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    } //- setOutputDir

    
    /**
     * Generate all statistics.
     * @throws IOException
     */
    public void genStats() throws IOException {
        makeOutputDir();
        genArticleStats(true);
        genArticleStats(false);
        genAuthorStats();    
    } //- genStats
    
    
    /**
     * Generate all statistics and output to the given directory.
     * @param outputDir
     * @throws IOException
     */
    public void genStats(String outputDir) throws IOException {
        setOutputDir(outputDir);
        genStats();
    } //- genStats
    
    
    private void makeOutputDir() throws IOException {
        File file = new File(outputDir);
        if (!file.isDirectory()) {
            file.mkdir();
        }
    } //- makeOutputDir
    
    
    /**
     * Generate article statistics.
     * @param includeCitations set to true if processing stats for all
     * citations, not just for articles in the repository.
     * @throws IOException
     */
    public void genArticleStats(boolean includeCitations) throws IOException {
        
        List<ThinDoc> docs = citestat.getMostCitedArticles(numArticles,includeCitations);
        String fileName = (includeCitations) ? "citations" : "articles";
        writeDocs(docs, outputDir+FILE_SEP+fileName);
        
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i=startingYear; i<=currentYear; i++) {
            docs = citestat.getMostCitedArticlesByYear(
                    numArticles, i, includeCitations);
            writeDocs(docs, outputDir+FILE_SEP+fileName+"_"+i);
        }
        
    }  //- genArticleStats
    
    
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String FILE_SEP = System.getProperty("file.separator");
    
    private static void writeDocs(List<ThinDoc> docs, String fileName)
    throws IOException {
                
        FileWriter writer = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(writer);
        
        // First line is the stats creation time.
        String creation = Long.toString(System.currentTimeMillis());
        out.write(creation);
        out.write(NEW_LINE);
        for (Object o : docs) {
            ThinDoc doc = (ThinDoc)o;
            out.write(Long.toString(doc.getCluster()));
            out.write("\t");
            out.write(Integer.toString(doc.getNcites()));
            out.write("\t");
            out.write(Boolean.toString(doc.getInCollection()));
            out.write("\t");
            if (doc.getAuthors() != null) {
                out.write(doc.getAuthors());
            } else {
                out.write("NULL");
            }
            out.write("\t");
            if (doc.getTitle() != null) {
                out.write(doc.getTitle());
            } else {
                out.write("NULL");
            }
            out.write("\t");
            if (doc.getVenue() != null) {
                out.write(doc.getVenue());
            } else {
                out.write("NULL");
            }
            out.write("\t");
            if (doc.getYear() > 0) {
                out.write(Integer.toString(doc.getYear()));
            } else {
                out.write("NULL");
            }
            out.write(NEW_LINE);
        }
        out.close();
        
    } //- writeDocs
    
    
    /**
     * Generate author statistics.
     * @throws IOException
     */
    public void genAuthorStats() throws IOException {
        HashMap<String,AuthorContainer> authors =
            new HashMap<String,AuthorContainer>();
        long start = 0;
        int batch = 10000;
        while(true) {
            start = start+1;
            List<AuthorStatContainer> authorStats = 
                citestat.getAuthorStats(start, batch);
            if (authorStats.isEmpty()) {
                break;
            }
            for (Object o : authorStats) {
                AuthorStatContainer container = (AuthorStatContainer)o;
                for (String name : container.getAuthors()) {
                    AuthorContainer ac = new AuthorContainer(name);
                    ac.addCitations(container.getNcites());
                    if (authors.containsKey(ac.getKey())) {
                        authors.get(ac.getKey()).add(ac);
                    }
                    else {
                        authors.put(ac.getKey(), ac);
                    }
                }
                start = container.getCluster();
            }
        }
        Collection<AuthorContainer> authc = authors.values();
        List<AuthorContainer> authl = new ArrayList<AuthorContainer>(authc);
        Collections.sort(authl, new DescendingComparator());
        int size = (authl.size() > numAuthors) ? numAuthors : authl.size();
        writeAuthStats(authl.subList(0, size), outputDir+FILE_SEP+"authors");
        
    }  //- genAuthorStats
    
    
    private static void writeAuthStats(List<AuthorContainer> authorStats, 
            String fileName) throws IOException {

        FileWriter writer = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(writer);
        
        // First line is the stats creation time.
        String creation = Long.toString(System.currentTimeMillis());
        out.write(creation);
        out.write(NEW_LINE);
        for (Object o : authorStats) {
            AuthorContainer container = (AuthorContainer)o;
            out.write(container.getCanonicalName());
            out.write("\t");
            out.write(Integer.toString(container.getCitationCount()));
            out.write(NEW_LINE);
        }
        out.close();
        
    }  //- writeAuthStats
    
}  //- class StatisticsGenerator


/**
 * Comparator for sorting authors by number of citations in decending order.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
class DescendingComparator implements Comparator<AuthorContainer> {
    public final int compare(AuthorContainer ac1, AuthorContainer ac2) {
        return -1*ac1.compareTo(ac2);
    }
}  //- class DescendingComparator


/**
 * Generic container for author stat data.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
class AuthorContainer implements Comparable<AuthorContainer> {
    
    private HashMap<String,Integer> observations =
        new HashMap<String,Integer>();
    
    private String key;
    
    public String getKey() {
        return key;
    }
    
    
    public AuthorContainer(String name) {
        if (name == null);
        key = name.toLowerCase();
        observations.put(name, new Integer(1));
    }
    
    
    public void addObservation(String name) {
        if (observations.containsKey(name)) {
            observations.put(name, observations.get(name)+1);
        } else {
            observations.put(name, new Integer(1));
        }
    }  //- addObservation
    
    
    private int citations = 0;
    
    public void addCitations(int citations) {
        this.citations += citations; 
    }
    
    public int getCitationCount() {
        return citations;
    }
    
    
    public String getCanonicalName() {
        int maxVal = 0;
        String maxName = "";
        for (String name : observations.keySet()) {
            int val = observations.get(name).intValue();
            if (val > maxVal) {
                maxVal = val;
                maxName = name;
            }
        }
        return maxName;
        
    }  //- getCanonicalName
    
    
    public void add(AuthorContainer ac) {
        this.addObservation(ac.getCanonicalName());
        this.addCitations(ac.getCitationCount());
    }
    
    
    public int compareTo(AuthorContainer ac) {
        if (ac==null) {
            return 1;
        }
        if (this.getCitationCount() == ac.getCitationCount()) {
            return 0;
        } else if (this.getCitationCount() > ac.getCitationCount()) {
            return 1;
        } else {
            return -1;
        }
        
    }  //- compareTo
        
}  //- class AuthorContainer


class InvalidNameException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1645972504189230195L;}
