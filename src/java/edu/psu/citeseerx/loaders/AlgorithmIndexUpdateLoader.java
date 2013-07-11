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

import edu.psu.citeseerx.updates.AlgorithmIndexUpdater;

/**
 * @author Sumit Bathia
 * @version $Rev$ $Date$
 */
public class AlgorithmIndexUpdateLoader {
    public static void main(String[] args) throws IOException {
        ListableBeanFactory factory = ContextReader.loadContext();
        AlgorithmIndexUpdater updater =
            (AlgorithmIndexUpdater)factory.getBean("algorithmIndexUpdater");
        try {
            updater.indexAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } //- main
} //- class AlgorithmIndexUpdateLoader
