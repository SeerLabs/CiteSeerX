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

import edu.psu.citeseerx.corrections.CorrectAuthors;


/**
 * Loads the CorrectAuthors bean and runs it base on command line options
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CorrectAuthorsLoader {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        ListableBeanFactory factory = ContextReader.loadContext();
        CorrectAuthors authorFixer =
            (CorrectAuthors)factory.getBean(
                    "authorFixer");
        System.out.println("Starting author de-duplication process");
        int count = authorFixer.process();
        System.out.println(count + " documents have been fixed");

    } //- main

} //- class CorrectAuthorsLoader
