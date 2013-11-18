
package edu.psu.citeseerx.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wcohen.ss.*;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.Token;
import com.wcohen.ss.tokens.SimpleTokenizer;

/**
 * TFIDF-based distance metric.
 * @author Puck Treeratpituk
 * @author Juan Pablo Fernandez Ramirez
 * @see com.wcohen.ss.SoftTFIDF
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $ 
 */
public class SeerSoftTFIDF extends SoftTFIDF {
	
	static private final double MIN_TOKEN_SIMILARITY = 0.8;

	public SeerSoftTFIDF(double minTokenSimilarity) {
        super(new SimpleTokenizer(false, true), new JaroWinkler(), 
                minTokenSimilarity);
    } //- SeerSoftTFIDF

	public SeerSoftTFIDF() {
	    super(new SimpleTokenizer(false, true), new JaroWinkler(), 
	            MIN_TOKEN_SIMILARITY);
	} //- SeerSoftTFIDF
	
	/**
	 * Train the distance on some strings. This should be a large corpus in 
	 * order to accumulate good frequency estimates.
	 * @param trainingCorpus
	 */
	public void trainFromText(List<String> trainingCorpus) {
		List<StringWrapper> preparedCorpus = new ArrayList<StringWrapper>();
		Iterator<String> corpusIterator = trainingCorpus.iterator();
		while (corpusIterator.hasNext()) {
			preparedCorpus.add(prepare(corpusIterator.next()));
		}
		train(new BasicStringWrapperIterator(preparedCorpus.iterator()));
	} //- trainFromText
	
	/**
	 * 
	 * @param model List of ModelItems each one with a string and its
	 * frequency within the corpus
	 */
	public void loadModel(List<ModelItem> model) {
		Iterator<ModelItem> iter = model.iterator();
		
		setCollectionSize(model.size());
		while(iter.hasNext()) {
			ModelItem item = iter.next();
			Token token = tokenizer.intern(item.getItem());
			setDocumentFrequency(token, item.getFrequency());
		}
	} //- loadModel
	
	/**
	 * Loads the model stored in the given file. This function assumes the
	 * file is UTF-8 encoded.
	 * @param filePath
	 */
	public void loadModel(String filePath) throws IOException {
	    
	    File modelFile = new File(filePath);
	    
	    if (!modelFile.exists()) {
	        throw new IOException("File not found: " + filePath);
	    }
	    
	    BufferedReader bufReader = null;

	    try {
            bufReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(modelFile), "UTF8"));
            
            List<ModelItem> model = 
                new ArrayList<ModelItem>();
            String line = null;
            while (null != (line = bufReader.readLine())) {
                String[] terms  = line.split(" ");
                int df          = Integer.parseInt(terms[1]);
                ModelItem item  = this.new ModelItem();
                item.setFrequency(df);
                item.setItem(terms[0]);
                model.add(item);
            }
            bufReader.close();
            
            loadModel(model);
	    }catch (IOException e) {
            if (bufReader != null) {
                bufReader.close();
            }
            throw e;
        }
	} //- loadModel
	
	/**
	 * 
	 * @return A list of ModelItems indicating where each item has
	 * a string and its frequency within the corpus used to train the
	 * distance metric comparator
	 */
	public List<ModelItem> getModel() {
		List<ModelItem> model = new ArrayList<ModelItem>();
		Iterator<Token> iter = tokenizer.tokenIterator();
		
		while (iter.hasNext()) {
			ModelItem item = new ModelItem();
			Token token = iter.next();
			int freq = getDocumentFrequency(token);
			item.setItem(token.getValue());
			item.setFrequency(freq);
			model.add(item);
		}
		return model;
	} //- getModel
	
	/**
	 * Trains using the provided file. The file is expected to be UTF-8
	 * encoded.
	 * @param corpusFile
	 */
	public void trainFromCorpus(String corpusFile) throws IOException {
	    File inputFile = new File(corpusFile);
	    
	    if (!inputFile.exists()) {
            throw new IOException("File not found: " + corpusFile);
        }
	    
	    BufferedReader bufReader = null;
        try {
            // Load file content, and train based on it.
            bufReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(inputFile), "UTF8"));
            List<String> corpus = new ArrayList<String>();
            String line = null;
            while (null != (line = bufReader.readLine())) {
                corpus.add(line);
            }
            trainFromText(corpus);
        }catch (IOException e) {
            // Ignore training.
            bufReader.close();
            throw e;
        }
	} //- trainFromCorpus
	
	/**
	 * Saves the current model to file.
	 * In order to have a model either loadModel or trainFromCorpus needs to be
	 * call
	 * The generated file is UTF-8 encoded.
	 * @param modelLocation
	 */
	public void saveModel(String modelLocation) throws IOException {
	    
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
	            new FileOutputStream(modelLocation), "UTF-8"));
	    Iterator<Token> iter = tokenizer.tokenIterator();
        
        while (iter.hasNext()) {
            Token token = iter.next();
            int freq = getDocumentFrequency(token);
            writer.write(token.getValue() + " " + freq);
        }
        writer.close();
	} //- saveModel
	
	/* (non-Javadoc)
     * @see com.wcohen.ss.AbstractStatisticalTokenDistance#checkTrainingHasHappened(com.wcohen.ss.api.StringWrapper, com.wcohen.ss.api.StringWrapper)
     */
    @Override
    protected void checkTrainingHasHappened(StringWrapper s, StringWrapper t) {
        if (getCollectionSize() == 0) {
            throw new RuntimeException("The class has not been trained for " +
            		"similarity");
        }
    } //- checkTrainingHasHappened

    /*
	 * Utility class for representing a string and its frequency within
	 * the training corpus 
	 */
	public class ModelItem {
		private String item;
		private int frequency;
		
		/**
		 * @return the item
		 */
		public String getItem() {
			return item;
		} //- getItem
		
		/**
		 * @param item the item to set
		 */
		public void setItem(String item) {
			this.item = item;
		} //- setItem
		
		/**
		 * @return the frequency
		 */
		public int getFrequency() {
			return frequency;
		} //- getFrequency
		
		/**
		 * @param frequency the frequency to set
		 */
		public void setFrequency(int frequency) {
			this.frequency = frequency;
		} //- setFrequency

	} // -Class Model
} //- class SeerSoftTFIDF
