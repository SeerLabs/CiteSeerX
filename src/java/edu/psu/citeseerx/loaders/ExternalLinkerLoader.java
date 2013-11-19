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
package edu.psu.citeseerx.loaders;

import java.io.IOException;

import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.updates.external.links.ACMExternalLinkUpdater;
import edu.psu.citeseerx.updates.external.links.CiteULikeExternalLinkUpdater;
import edu.psu.citeseerx.updates.external.links.DBLPExternalLinkUpdater;

/**
 * Loads the ExternalLinkUpdaters and runs updateExternalLinks which will try
 * to update the Links to external sources for CiteSeerX papers which 
 * doesn't have it.
 * Currently, it handles DBLP and CiteULike
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class ExternalLinkerLoader {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        ListableBeanFactory factory = ContextReader.loadContext();
        DBLPExternalLinkUpdater dblpUpdater =
            (DBLPExternalLinkUpdater)factory.getBean("dblpExtLinkUpdater");
        CiteULikeExternalLinkUpdater citeulikeUpdater =
            (CiteULikeExternalLinkUpdater)factory.getBean("citeulikeExtLinkUpdater");
        ACMExternalLinkUpdater acmUpdater =
            (ACMExternalLinkUpdater)factory.getBean("acmExtLinkUpdater");

        try {
            // TODO: We should run each updater in a different thread
            dblpUpdater.updateExternalLinks();
            citeulikeUpdater.updateExternalLinks();
            acmUpdater.updateExternalLinks();
        } catch (Exception e) {
            e.printStackTrace();
        }

    } //- main

} //- class ExternalLinkerLoader
