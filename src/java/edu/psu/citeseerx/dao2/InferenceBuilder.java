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

import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.citeseerx.domain.ThinDoc;

/**
 * Generates canonical metadata for cluster records based on the most frequent
 * values for updatable fields among citations within the cluster.
 * Fields checked include the following:
 * <br>
 * <ul>
 * <li>title</li>
 * <li>authors</li>
 * <li>year</li>
 * <li>venue</li>
 * <li>venue type</li>
 * <li>pages</li>
 * <li>volume</li>
 * <li>number</li>
 * <li>publisher</li>
 * <li>tech report number</li>
 * </ul>
 * <br>
 * This class uses JSON strings to store intermediary frequency and canonical
 * data so that frequency information can be updated iteratively without
 * going back to each citation source.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class InferenceBuilder {

    private static final String TITLE       = "title";
    private static final String AUTH        = "auths";
    private static final String YEAR        = "year";
    private static final String VENUE       = "venue";
    private static final String VENTYPE     = "ventype";
    private static final String PAGES       = "pages";
    private static final String VOL         = "vol";
    private static final String NUM         = "num";
    private static final String PUBLISHER   = "publisher";
    private static final String TECH        = "tech";
    private static final String SIZE        = "size";
    
    private static final String OBS         = "obs";
    private static final String CANON       = "canon";
    private static final String CANON_SIZE  = "ccount";
    
    private static float minConfidence = (float)0.1;


    /**
     * Adds a metadata record to the specified JSON metadata object,
     * updating frequency and canonical data in a iterative fashion.
     * @param doc
     * @param metadata
     * @return whether any field has changed its canonical value.
     * @throws JSONException
     */
    public static boolean addObservation(ThinDoc doc, JSONObject metadata)
    throws JSONException {
        
        boolean changed = false;
        
        int size = metadata.optInt(SIZE);

        String title = doc.getTitle();
        if (title != null) {
            if (insertValue(title, TITLE, metadata, size)) {
                changed = true;
            }
        }
        String auth = doc.getAuthors();
        if (auth != null) {
            if (insertValue(auth, AUTH, metadata, size)) {
                changed = true;
            }
        }
        int year = doc.getYear();
        if (year > 0) {
            if (insertValue(Integer.toString(year), YEAR, metadata, size)) {
                changed = true;
            }
        }
        String venue = doc.getVenue();
        if (venue != null) {
            if (insertValue(venue, VENUE, metadata, size)) {
                changed = true;
            }
        }
        String ventype = doc.getVentype();
        if (ventype != null) {
            if (insertValue(ventype, VENTYPE, metadata, size)) {
                changed = true;
            }
        }
        String pages = doc.getPages();
        if (pages != null) {
            if (insertValue(pages, PAGES, metadata, size)) {
                changed = true;
            }
        }
        int vol = doc.getVol();
        if (vol > 0) {
            if (insertValue(Integer.toString(vol), VOL, metadata, size)) {
                changed = true;
            }
        }
        int num = doc.getNum();
        if (num > 0) {
            if (insertValue(Integer.toString(num), NUM, metadata, size)) {
                changed = true;
            }
        }
        String publisher = doc.getPublisher();
        if (publisher != null) {
            if (insertValue(publisher, PUBLISHER, metadata, size)) {
                changed = true;
            }
        }
        String tech = doc.getTech();
        if (tech != null) {
            if (insertValue(tech, TECH, metadata, size)) {
                changed = true;
            }
        }
        
        metadata.put(SIZE, ++size);
        
        return changed;
        
    }  //- addObservation
    
    
    /**
     * Removes a metadata record from the JSON data bundle, updating frequency
     * and canonical data iteratively.
     * @param doc
     * @param metadata
     * @return whether any field value has changed its canonical value.
     * @throws JSONException
     */
    public static boolean deleteObservation(ThinDoc doc, JSONObject metadata)
    throws JSONException {
        
        boolean changed = false;
        
        int size = metadata.optInt(SIZE);

        String title = doc.getTitle();
        if (title != null) {
            if (removeValue(title, TITLE, metadata, size)) {
                changed = true;
            }
        }
        String auth = doc.getAuthors();
        if (auth != null) {
            if (removeValue(auth, AUTH, metadata, size)) {
                changed = true;
            }
        }
        int year = doc.getYear();
        if (year > 0) {
            if (removeValue(Integer.toString(year), YEAR, metadata, size)) {
                changed = true;
            }
        }
        String venue = doc.getVenue();
        if (venue != null) {
            if (removeValue(venue, VENUE, metadata, size)) {
                changed = true;
            }
        }
        String ventype = doc.getVentype();
        if (ventype != null) {
            if (removeValue(ventype, VENTYPE, metadata, size)) {
                changed = true;
            }
        }
        String pages = doc.getPages();
        if (pages != null) {
            if (removeValue(pages, PAGES, metadata, size)) {
                changed = true;
            }
        }
        int vol = doc.getVol();
        if (vol > 0) {
            if (removeValue(Integer.toString(vol), VOL, metadata, size)) {
                changed = true;
            }
        }
        int num = doc.getNum();
        if (num > 0) {
            if (removeValue(Integer.toString(num), NUM, metadata, size)) {
                changed = true;
            }
        }
        String publisher = doc.getPublisher();
        if (publisher != null) {
            if (removeValue(publisher, PUBLISHER, metadata, size)) {
                changed = true;
            }
        }
        String tech = doc.getTech();
        if (tech != null) {
            if (removeValue(tech, TECH, metadata, size)) {
                changed = true;
            }
        }
        
        metadata.put(SIZE, --size);
        
        return changed;
        
    }  //- deleteObservation
    
    
    /**
     * Returns a ThinDoc representation of the canonical metadata represented
     * by the JSON metadata bundle.
     * @param metadata
     * @return Returns a ThinDoc representation of the canonical metadata represented
     * by the JSON metadata bundle.
     */
    public static ThinDoc toThinDoc(JSONObject metadata) {

        int size = metadata.optInt(SIZE);
        ThinDoc doc = new ThinDoc();
 
        JSONObject title       = metadata.optJSONObject(TITLE);
        JSONObject auth        = metadata.optJSONObject(AUTH);
        JSONObject venue       = metadata.optJSONObject(VENUE);
        JSONObject ventype     = metadata.optJSONObject(VENTYPE);
        JSONObject pages       = metadata.optJSONObject(PAGES);
        JSONObject publisher   = metadata.optJSONObject(PUBLISHER);
        JSONObject tech        = metadata.optJSONObject(TECH);
        JSONObject year        = metadata.optJSONObject(YEAR);
        JSONObject vol         = metadata.optJSONObject(VOL);
        JSONObject num         = metadata.optJSONObject(NUM);
        
        if (title != null) {
            int csize = title.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                doc.setTitle(title.optString(CANON));
            }
        }
        if (auth != null) {
            int csize = auth.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                doc.setAuthors(auth.optString(CANON));
            }
        }
        if (venue != null) {
            int csize = venue.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                doc.setVenue(venue.optString(CANON));
            }
        }
        if (ventype != null) {
            int csize = ventype.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                doc.setVentype(ventype.optString(CANON));
            }
        }
        if (pages != null) {
            int csize = pages.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                doc.setPages(pages.optString(CANON));
            }
        }
        if (publisher != null) {
            int csize = publisher.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                doc.setPublisher(publisher.optString(CANON));
            }
        }
        if (tech != null) {
            int csize = tech.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                doc.setTech(tech.optString(CANON));
            }
        }
        if (year != null) {
            int csize = year.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                try { doc.setYear(year.getInt(CANON)); } catch (Exception e) {}                
            }
        }
        if (vol != null) {
            int csize = vol.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                try { doc.setVol(vol.getInt(CANON)); } catch (Exception e) {}                
            }
        }
        if (num != null) {
            int csize = num.optInt(CANON_SIZE);
            if (checkConfidence(size, csize)) {
                try { doc.setNum(num.getInt(CANON)); } catch (Exception e) {}                
            }
        }
        doc.setNcites(size);
        doc.setObservations(metadata.toString());

        return doc;
        
    }  //- toThinDoc
    
    
    private static boolean insertValue(String value, String key,
            JSONObject metadata, int priorSize) throws JSONException {
        
        if (value == null) {
            return false;
        }
        
        JSONObject json = metadata.optJSONObject(key);
        if (json == null) {
            json = new JSONObject();
            metadata.put(key, json);
        }
        String priorCanon = json.optString(CANON);
        JSONObject observations = json.optJSONObject(OBS);
        if (observations == null) {
            observations = new JSONObject();
            json.put(OBS, observations);
        }
        int priorLargestCount = 0;
        int largestCount      = 0;
        
        boolean found = false;
        
        String[] names = JSONObject.getNames(observations);
        if (names == null) names = new String[0]; 
        for (int i=0; i<names.length; i++) {
            String obsVal = names[i];
            int obsCount = observations.getInt(obsVal);
            if (obsCount > priorLargestCount) {
                priorLargestCount = obsCount;
            }
            if (value.equalsIgnoreCase(obsVal)) {
                found = true;
                obsCount++;
                if (obsCount > largestCount) {
                    largestCount = obsCount;
                }
                observations.put(obsVal, obsCount);
            }
        }
        if (found == false) {
            observations.put(value, 1);
            if (1 > largestCount) largestCount = 1;
        }
        
        boolean priorConfident = checkConfidence(priorSize, priorLargestCount);
        boolean postConfident = checkConfidence(priorSize+1, largestCount);

        boolean confidenceChanged = (priorConfident != postConfident);
        boolean valueChanged = false;
        
        if (largestCount > priorLargestCount &&
                !value.equalsIgnoreCase(priorCanon)) {
            json.put(CANON, value);
            valueChanged = true;
        }
        if (largestCount > priorLargestCount) {
            json.put(CANON_SIZE, largestCount);
        }

        if (confidenceChanged) {
            return true;
        } else {
            return valueChanged;
        }

    }  //- insertValue
    
    
    private static boolean removeValue(String value, String key,
            JSONObject metadata, int priorSize) throws JSONException {
        
        if (value == null) {
            return false;
        }
        
        JSONObject json = metadata.optJSONObject(key);
        if (json == null) {
            return false;
        }

        String priorCanon = json.optString(CANON);
        
        JSONObject observations = json.optJSONObject(OBS);
        if (observations == null) {
            return false;
        }
        
        int largestCount      = 0;
        String largestValue   = null;
        
        String[] names = JSONObject.getNames(observations);
        if (names == null) names = new String[0]; 
        for (int i=0; i<names.length; i++) {
            String obsVal = names[i];
            int obsCount = observations.getInt(obsVal);
            if (value.equalsIgnoreCase(obsVal)) {
                obsCount--;
                if (obsCount <= 0) {
                    observations.remove(obsVal);
                } else {
                    observations.put(obsVal, obsCount);
                }
            }
            if (obsCount > largestCount) {
                largestCount = obsCount;
                largestValue = obsVal;
            }
        }

        boolean canonChanged = false;
        int priorLargestCount = largestCount;
        if (value.equalsIgnoreCase(priorCanon)) {
            canonChanged = true;
            priorLargestCount--;
        }
        
        boolean priorConfident = checkConfidence(priorSize, priorLargestCount);
        boolean postConfident = checkConfidence(priorSize-1, largestCount);

        boolean confidenceChanged = (priorConfident != postConfident);
        boolean valueChanged = false;
        
        if (canonChanged && !priorCanon.equalsIgnoreCase(largestValue)) {
            if (largestValue != null) {
                json.put(CANON, largestValue);
            } else {
                metadata.remove(key);
            }
            valueChanged = true;
        }
        if (largestCount != priorLargestCount) {
            json.put(CANON_SIZE, largestCount);
        }
        if (largestCount <= 0) {
            metadata.remove(key);
        }

        if (confidenceChanged) {
            return true;
        } else {
            return valueChanged;
        }

    }  //- removeValue

    
    private static boolean checkConfidence(int size, int count) {
        float percent = (float)count/(float)size;
        if (percent >= minConfidence) {
            return true;
        } else {
            return false;
        }
    }
    
}  //- class InferenceBuilder
