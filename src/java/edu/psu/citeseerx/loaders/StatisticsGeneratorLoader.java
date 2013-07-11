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
import edu.psu.citeseerx.updates.StatisticsGenerator;

/**
 * Loads the statisticsGenerator bean and runs genStats, using the supplied
 * command line argument if there is one.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class StatisticsGeneratorLoader {

    public static void main(String[] args) throws IOException {
        ListableBeanFactory factory = ContextReader.loadContext();
        StatisticsGenerator generator =
            (StatisticsGenerator)factory.getBean("statisticsGenerator");
        if (args.length > 0) {
            generator.genStats(args[0]);
        } else {
            generator.genStats();
        }
    } //- main
} //- class StatisticsGeneratorLoader
