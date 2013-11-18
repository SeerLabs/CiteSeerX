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
package edu.psu.citeseerx.disambiguation;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.REXP;

import org.springframework.beans.factory.ListableBeanFactory;
import edu.psu.citeseerx.loaders.ContextReader;

/**
 * DisambiguationService
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DisambiguationService {
	RConnection c;
	String rf_path; // "../../data/csauthors/csauthors.rf";
	
	public DisambiguationService() throws Exception {
		c = new RConnection();
		c.eval("library(randomForest)");
	}

	public void setModelFile(String modelFile) throws Exception {
		this.rf_path = modelFile;
		c.eval("load('" + this.rf_path + "')");
	}

	public double calcSingleDistance(String feat) throws Exception {
		c.eval("test = c(" + feat + ")");
		REXP x = c.eval("predict(rf, test, proximity=FALSE, type='prob')[,1]");
		return x.asDouble();
	}
	
	public double[] calcDistances(String dist_file) throws Exception {
		
		c.eval("test = read.table('" + dist_file + "', header=TRUE, sep=',')");
		REXP x = c.eval("predict(rf, test, proximity=FALSE, type='prob')[,1]");
		
		double[] probs = x.asDoubles();
		return probs;
	}

	public void startup() throws Exception {
		if (c != null)
			c.shutdown();
		c = new RConnection();
	}
	public void shutdown() throws Exception {
		c.shutdown();
		c = null;
	}

	// java -Dcsx.boot=bootstrap/disambiguation.txt -Dcsx.conf=conf edu.psu.citeseerx.disambiguation.DisambiguationService
    public static void main(String[] args) throws Exception {
        ListableBeanFactory factory = ContextReader.loadContext();
		DisambiguationService c = 
		    (DisambiguationService)factory.getBean("disambiguationService");
		//c.loadRandomForest();
		//c.calcDistances("/home/pxt162/codes/citeseerx/resources/disambiguation/feats.dat");
    } //- main
}