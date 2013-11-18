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

import java.io.File;
import java.io.IOException;


import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.fixers.LegacyMetadataFixer;
import edu.psu.citeseerx.utility.SeerSoftTFIDF;

/**
 * Loads the legacyMetaDataFixer bean and runs process on the
 * command line arguments.
 * <b>This process needs to be tested and improved if necessary</b>
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class FixMetaDataLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	    if (args.length < 4 || args.length > 5) {
	        System.out.println("Specify directory, model file " +
	                "starting ID, ending ID, [Similarity Factor]");
	        System.exit(0);
	    }
        String dir = args[0];
        String modelFile = args[1];
        int start = Integer.parseInt(args[2]);
        int end = Integer.parseInt(args[3]);
        
        double similarityFactor = 0.0;
        if (args.length == 5) {
        	similarityFactor = Double.parseDouble(args[4]);
	        if (similarityFactor <= 0.0 || similarityFactor >= 1.0) {
	        	System.out.println("Similarity Factor should be a value " +
	        			"between 0.0 and 1.0 borders not included");
	            System.exit(0);
	        }
        }
        
        ListableBeanFactory factory = null;
        try {
        	factory = ContextReader.loadContext();
        }catch (IOException e) {
        	e.printStackTrace();
            System.exit(1);
		}
        LegacyMetadataFixer fixMeta = 
        	(LegacyMetadataFixer)factory.getBean("legacyMetadataFixer");
        SeerSoftTFIDF distance = null;
        if (args.length == 5) {
        	fixMeta.setSimilarityFactor(similarityFactor);
        	 distance = new SeerSoftTFIDF(similarityFactor);
        }
        
        distance = (null == distance) ? new SeerSoftTFIDF() : distance;
        try {
            distance.loadModel(modelFile);
        }catch (IOException e) {
            System.out.println("An error ocurred while loading the model file");
            System.err.println("An error ocurred while loading the model file");
            e.printStackTrace();
            System.exit(1);
        }
        fixMeta.setDistanceMetric(distance);
        String sep = System.getProperty("file.separator");
        
        int processed = 0;
        for (int i=start; i<=end; i++) {
        	File file = new File(dir+sep+i+".xml");
        	if (file.exists()) {
        		try {
        			processed++;
        			fixMeta.process(file.getAbsolutePath(), i);
        		}catch (Exception e) {
        			System.err.println("Processing file: " + 
        					file.getAbsolutePath());
					e.printStackTrace();
				}
        	}
        }
        System.out.println(processed + " files were processed.");
	} //- main

} //- FixMetaDataLoader
