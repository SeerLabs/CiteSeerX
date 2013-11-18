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
package edu.psu.citeseerx.fixers;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;

/**
 * Throwaway data fixer.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 * @deprecated
 */
public class FixYears {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    public void fix() throws Exception {
        for (String doi : ids) {
            Document doc = csxdao.getDocumentFromDB(doi);
            doc.setDatum(Document.YEAR_KEY, null);
            csxdao.updateDocument(doc);
        }
        
    }
    
    
    public static void main(String[] args) throws Exception {
        FixYears fixer = new FixYears();
        fixer.fix();
    }
    
    private String[] ids = {
            "10.1.1.29.7567",
            "10.1.1.4.1025",
            "10.1.1.27.4714",
            "10.1.1.7.1062",
            "10.1.1.18.1646",
            "10.1.1.15.3862",
            "10.1.1.17.8311",
            "10.1.1.21.9630",
            "10.1.1.29.7177",
            "10.1.1.16.4562",
            "10.1.1.29.7815",
            "10.1.1.29.8233",
            "10.1.1.29.8189",
            "10.1.1.33.218",
            "10.1.1.36.1477",
            "10.1.1.8.7407",
            "10.1.1.6.3762",
            "10.1.1.48.9027",
            "10.1.1.49.2046",
            "10.1.1.42.8871",
            "10.1.1.8.60",
            "10.1.1.45.215",
            "10.1.1.35.4827",
            "10.1.1.53.2991",
            "10.1.1.8.2938",
            "10.1.1.32.2278",
            "10.1.1.41.3115",
            "10.1.1.3.6331",
            "10.1.1.15.6425",
            "10.1.1.29.5879",
            "10.1.1.39.5337",
            "10.1.1.44.6003",
            "10.1.1.17.9969",
            "10.1.1.46.7333",
            "10.1.1.53.518",
            "10.1.1.26.2319",
            "10.1.1.51.4044",
            "10.1.1.17.6044",
            "10.1.1.17.6808",
            "10.1.1.18.1456",
            "10.1.1.35.4728",
            "10.1.1.21.4655",
            "10.1.1.23.7840",
            "10.1.1.37.4786",
            "10.1.1.10.1080",
            "10.1.1.10.8565",
            "10.1.1.11.7191",
            "10.1.1.11.9959",
            "10.1.1.13.4376",
            "10.1.1.2.1920",
            "10.1.1.2.7248",
            "10.1.1.26.9463",
            "10.1.1.27.4585",
            "10.1.1.27.8787",
            "10.1.1.3.3523",
            "10.1.1.32.1036",
            "10.1.1.5.4287",
            "10.1.1.6.792",
            "10.1.1.29.8580",
            "10.1.1.51.3321",
            "10.1.1.21.9441",
            "10.1.1.44.1366",
            "10.1.1.44.8950",
            "10.1.1.1.9884",
            "10.1.1.21.9789",
            "10.1.1.23.1914",
            "10.1.1.35.5375",
            "10.1.1.8.5480",
            "10.1.1.6.8398",
            "10.1.1.43.4070",
            "10.1.1.48.2628",
            "10.1.1.1.7767",
            "10.1.1.3.8255",
            "10.1.1.39.3343",
            "10.1.1.39.4801",
            "10.1.1.22.1075",
            "10.1.1.37.7850",
            "10.1.1.41.1649",
            "10.1.1.5.5224",
            "10.1.1.53.4248",
            "10.1.1.49.5271",
            "10.1.1.51.6164",
            "10.1.1.6.4151",
            "10.1.1.9.8774",
            "10.1.1.30.3305",
            "10.1.1.4.7767",
            "10.1.1.49.22",
            "10.1.1.55.9518",
            "10.1.1.25.1877",
            "10.1.1.28.7874",
            "10.1.1.46.9416"
    };
}
