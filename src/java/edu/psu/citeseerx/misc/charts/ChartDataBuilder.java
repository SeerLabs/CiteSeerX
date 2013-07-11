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
package edu.psu.citeseerx.misc.charts;

import edu.psu.citeseerx.updates.UpdateListener;

/**
 * Generic inteface to collect the data used to generate citation histograms
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public interface ChartDataBuilder extends UpdateListener {
    
    /**
     * Builds a JSON string containing the data to build the citation histogram 
     * for documents which the citation count has changed in the corpus. 
     */
    public void buildChartData();
    
    /**
     * Builds a JSON string containing the data to build the citation histogram 
     * for all the documents in the corpus.
     */
    public void buildAllChartData();

} //- interface ChartDataBuilder
