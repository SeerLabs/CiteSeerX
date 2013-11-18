/**
 * 
 */
package edu.psu.citeseerx.misc.charts;

import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.ThinDoc;

/**
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 *
 */
public class ChartDataBuilderImpl implements ChartDataBuilder {
    
    private static final int MAX_CITING     = 5000;
    private static final String LABEL       = "label";
    private static final String YEAR        = "year";
    private static final String CIT_COUNT   = "ccount";
    

    private CSXDAO csxdao;

    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;        
    } //- setCSXDAO

    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;        
    } //- setCiteClusterDAO
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.misc.charts.ChartDataBuilder#buildChartData()
     */
    public void buildChartData() {
        String lastDoi = "0.0.0.0.0";
        int amount = 200;
        
        while(true) {
            java.util.List<String> dois = csxdao.getDOIs(lastDoi, amount);
            if (dois.isEmpty()) {
                break;
            }
            for (String doi : dois) {
                lastDoi = doi;
                if (!csxdao.checkChartUpdateRequired(doi)) {
                    continue;
                }
                Document doc = csxdao.getDocumentFromDB(doi, false, false);
                if (doc.getNcites() < 2) {
                    continue;
                }
                System.out.println("collecting data for document: " + doi);
                buildChartData(doc);
            }
        }
        
    } // buildChartData


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.misc.charts.ChartDataBuilder#buildAllChartData()
     */
    public void buildAllChartData() {
        String lastDoi = "0.0.0.0.0";
        int amount = 200;
        
        while(true) {
            java.util.List<String> dois = csxdao.getDOIs(lastDoi, amount);
            if (dois.isEmpty()) {
                break;
            }
            for (String doi : dois) {
                lastDoi = doi;
                Document doc = csxdao.getDocumentFromDB(doi, false, false);
                if (doc.getNcites() < 2) {
                    continue;
                }
                System.out.println("collecting data for document: " + doi);
                buildChartData(doc);
            }
        }
        
    } //- buildAllChartData


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.updates.UpdateListener#handleUpdate(edu.psu.citeseerx.domain.Document)
     */
    public void handleUpdate(Document doc) {
        // Do we need to update the citechart data?
        if (!csxdao.checkChartUpdateRequired(doc.getDatum(Document.DOI_KEY))) {
            // No update is requiered
            return;
        }
        if (doc.getNcites() < 2) {
            // There are no enough citations
            return;
        }
        buildChartData(doc);
    }

    /*
     * Returns a collection of DataPoints ordered by year
     */
    private DataPoint[] collectData(java.util.List<ThinDoc> docs,
            Integer baseYear) {
        
        int maxyear = -1;
        int minyear = 999999;
        
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);

        boolean found = false;
        
        HashMap<Integer,DataPoint> data = new HashMap<Integer,DataPoint>();
        for (ThinDoc doc : docs) {
            try {
                Integer year = new Integer(doc.getYear());
                if (year.intValue() < 1930 ||
                        (baseYear != null && year < baseYear) ||
                        year.intValue() > currentYear + 2) {
                    continue;
                }
                DataPoint point;
                if (data.containsKey(year)) {
                    point = data.get(year);
                } else {
                    point = new DataPoint(year.intValue());
                    data.put(year, point);
                }
                point.ncites++;
                if (year > maxyear) {
                    maxyear = year;
                }
                if (year < minyear) {
                    minyear = year;
                }
                found = true;
            } catch (Exception e) { }
        }

        if (!found) {
            return null;
        }

        DataPoint[] datalist = new DataPoint[maxyear-minyear+1];
        for (int i=minyear; i<=maxyear; i++) {
            Integer key = new Integer(i);
            if (data.containsKey(key)) {
                datalist[i-minyear] = data.get(key);
            } else {
                DataPoint point = new DataPoint(i);
                point.ncites = 0;
                datalist[i-minyear] = point;
            }
        }
        
        return datalist;
        
    } //- collectData

    /**
     * Collect the citation data needed to built the cite chart for the given 
     * document.
     * @param doc
     */
    private void buildChartData(Document doc) {
        Long clusterid = doc.getClusterID();
        if (clusterid == null) {
            // Nothing to do just return
            return;
        }
        Integer year = null;
        try {
            year = new Integer(doc.getDatum(Document.YEAR_KEY));
        } catch (Exception e) { }
        
        // Get the citing documents and collect data.
        java.util.List<ThinDoc> citingDocs =
            citedao.getCitingDocuments(clusterid, 0, MAX_CITING);
        DataPoint[] dataset = collectData(citingDocs, year);
        if (dataset == null || dataset.length <= 3) {
            // There is no data or its not enough
            return;
        }
        
        // Convert data to JSON format and store
        JSONArray citationData = new JSONArray();
        for (int i=0; i<dataset.length; i++) {
            JSONObject point = new JSONObject();
            try {
                point.put(LABEL, dataset[i].year);
                point.put(YEAR, dataset[i].year);
                point.put(CIT_COUNT, dataset[i].ncites);
                citationData.put(point);
            }catch (JSONException e) {
                // This should not happen since all data should be valid
                return;
            }
        }
        csxdao.insertChartUpdate(doc.getDatum(Document.DOI_KEY), 
                doc.getNcites(), citationData.toString());
        
    } //- buildChartData
} //- class ChartDataBuilderImpl
