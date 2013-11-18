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
package edu.psu.citeseerx.citeinf;

import edu.psu.citeseerx.dao2.InferenceBuilder;
import edu.psu.citeseerx.dao2.logic.*;
import edu.psu.citeseerx.domain.*;

import java.util.*;

import org.json.*;

/**
 * Utility for updating all cluster records with up-to-date inference data.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class BuildAll {

    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    }
    
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    
    /**
     * Builds and stores new inference data for all cluster records that have
     * been updated since the last execution of this method.
     * @throws JSONException
     */
    public void buildAll() throws JSONException {
        Long min = citedao.getMinClusterID();
        Long max = citedao.getMaxClusterID();
        if (min==null || max==null) {
            System.err.println("Null value for min or max at buildAll");
        }
        long counter = 0;
        for (Long i=min; i<=max; i++) {
            counter++;
            Date updateTime = citedao.checkInfUpdateRequired(i);
            if (updateTime != null) {
                updateCluster(i);
                citedao.insertInfUpdateTime(i, updateTime);
            }
            if ((counter%1000)==0) {
                System.out.println("Processed "+counter);
            }
        }
        
    }  //- buildAll
    
    
    /**
     * Updates the inference data for a single cluster.
     * @param clusterid
     * @throws JSONException
     */
    protected void updateCluster(Long clusterid) throws JSONException {
        
        ThinDoc cluster = citedao.getThinDoc(clusterid);
        if (cluster == null || cluster.getNcites() <= 0) return;

        JSONObject json = new JSONObject();
        
        List<Citation> citations = csxdao.getCitationsForCluster(clusterid);
        if (citations.size() == 0) {
            return;
        }
        for (Citation citation : citations) {
            ThinDoc thinDoc = DomainTransformer.toThinDoc(citation);
            InferenceBuilder.addObservation(thinDoc, json);
        }

        cluster.setObservations(json.toString());
        
        ThinDoc bundle = InferenceBuilder.toThinDoc(json);
        bundle.setCluster(clusterid);
        bundle.setObservations(cluster.getObservations());
        citedao.updateCluster(bundle, true);
        
    }  //- updateCluster
        
}  //- class BuildAll
