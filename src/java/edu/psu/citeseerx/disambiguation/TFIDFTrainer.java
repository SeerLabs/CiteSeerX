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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.SoftTFIDF;
import com.wcohen.ss.TFIDF;
import com.wcohen.ss.UnnormalizedTFIDF;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.Token;
import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

public class TFIDFTrainer
{
	public TFIDFTrainer () { }
	
	/**
	 * readModel
	 */
	public void readModel(String modelfile, TFIDF model, Tokenizer tokenizer) 
	throws IOException {
		BufferedReader bufReader = new BufferedReader(
		        new InputStreamReader(new FileInputStream(
		                new File(modelfile))));
					
		String line;
		line = bufReader.readLine();
		model.setCollectionSize(Integer.parseInt(line.substring(1)));
		
		// NOTE: assume case insensitive....
		while (null != (line = bufReader.readLine())) {
			String[] terms = line.split("\t");
			int df 		= Integer.parseInt(terms[0]);
			Token token = tokenizer.intern(terms[1].toLowerCase());
			
			model.setDocumentFrequency(token,  df);
		}
	}

	/**
	 * print model
	 */
	public void saveModel(String modelfile, TFIDF model, Tokenizer tokenizer)
	throws IOException {
		BufferedWriter bufWriter = 
		    new BufferedWriter(new FileWriter(modelfile));
		
		bufWriter.write("%" + model.getCollectionSize() + "\n");

		Iterator<Token> it = tokenizer.tokenIterator();
		while (it.hasNext()) {
			Token token = (Token)it.next();
			int df 		= model.getDocumentFrequency(token);
			bufWriter.write(df + "\t" + token.getValue() + "\n");
		}
		bufWriter.close();
	}
		
	/**
	 * trainFromText
	 */
	public void trainFromText(String filename, TFIDF model, Tokenizer tokenizer)
	throws IOException {
		BufferedReader bufReader = new BufferedReader(
		        new InputStreamReader(new FileInputStream(new File(filename))));
		
		List<StringWrapper> list = new ArrayList<StringWrapper>();

		int nLine = 0;
		String line;
		while (null != (line = bufReader.readLine())) {
			nLine++;
			list.add( model.prepare(line) );
			model.train( new BasicStringWrapperIterator(list.iterator()) );
			list.clear();
		}
	}
	
	public static void Usage() {
		System.out.println("Usage: TFIDFTrainer -txt=textfile -df=modelfile " +
				"-in=infile -save=savefile");
	}
	
	static void runTest(SoftTFIDF distance, String infile) throws IOException {
		BufferedReader bufReader = new BufferedReader(
		        new InputStreamReader(new FileInputStream(new File(infile))));
		
		Pattern p = Pattern.compile("\\[([^\\]]+)\\] \\[([^\\]]+)\\]"); // [.*]
		String line;
		while (null != (line = bufReader.readLine())) {
			Matcher m 	= p.matcher(line);
			m.find();
			myCompare(distance, m.group(1), m.group(2));
		}
				
		return;
	}
	
    static void myCompare(TFIDF distance, String s, String t)
    {
        // compute the similarity
        double d = distance.score(s,t);

        // print it out
        System.out.println("========================================");
        System.out.println("String s:  '"+s+"'");
        System.out.println("String t:  '"+t+"'");
        System.out.println("Similarity: "+d);

        // a sort of system-provided debug output
        System.out.println("Explanation:\n" + distance.explainScore(s,t));
    }

	public static void main2(String[] args)
    {		
		String textfile, modelfile, infile, savefile;
		textfile = modelfile = infile = savefile = "";
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-txt")) {
				String[] tokens = arg.split("=");
				textfile	= tokens[1];
			}
			else if (arg.startsWith("-df")) {
				String[] tokens = arg.split("=");
				modelfile	= tokens[1];				
			}
			else if (arg.startsWith("-in")) {
				String[] tokens = arg.split("=");
				infile	= tokens[1];				
			}
			else if (arg.startsWith("-save")) {
				String[] tokens = arg.split("=");
				savefile = tokens[1];				
			}
			else {
				Usage();
				System.exit(0);
			}
		}
		
		TFIDFTrainer trainer = new TFIDFTrainer();
        // create a SoftTFIDF distance learner
        double minTokenSimilarity = 0.8;
		Tokenizer tokenizer = new SimpleTokenizer(true, true);
        SoftTFIDF dist = 
            new SoftTFIDF(tokenizer,new JaroWinkler(), minTokenSimilarity);

		try {
			if (!modelfile.equals("")) 
				trainer.readModel(modelfile, dist, tokenizer);
			else if (!textfile.equals(""))
				trainer.trainFromText(textfile, dist, tokenizer);
			
			if (!savefile.equals(""))
				trainer.saveModel(savefile, dist, tokenizer);
				
			if (!infile.equals(""))
				runTest(dist, infile);
		} catch (Exception e) { e.printStackTrace(); }
		
		return;
    }    

    public static void main(String [] args)
    {
        try {
        	// create a SoftTFIDF distance learner
        	PhraseTokenizer tokenizer = new PhraseTokenizer(true);    	

        	// TFIDF using SecondString... 
        	TFIDFTrainer trainer = new TFIDFTrainer();
        	//String MODEL_FILE  = "data/medline/medline_journal.model";    	
        	String MODEL_FILE  = "data/medline/medline_aff.prune.model";    	
        	UnnormalizedTFIDF journal_tfidf = new UnnormalizedTFIDF(tokenizer);

            System.out.println(tokenizer);
    		trainer.readModel(MODEL_FILE, journal_tfidf, tokenizer);
			
			//String j4 = "Correspondence to: Long Yu, The State Key Laboratory of; Genetic Engineering, Fudan University";
			String j5 = "Depts of Electrical & Computer Engineering and Computer Science,; University of Illinois at Urbana-Champaign, Urbana, Illinois.; Tandem Computers Inc., Cupertino, California.";
			//String j3 = "Katy Börner";
			SimpleTokenizer st = new SimpleTokenizer(true, true);
			Token[] list = st.tokenize(j5);
			for (Token t: list) {
				System.out.println(t);
			}
			System.exit(0);

    		String j1 = "Journal of geophysical research";
    		String j2 = "Brain research";

			//StringWrapper sw1 = journal_tfidf.prepare(j1);
			//Token t = journal_tfidf.getTokens()[0];			

    		StringWrapper sw1 = journal_tfidf.prepare(j1);
    		Token t = journal_tfidf.getTokens()[0];
    		System.out.println(t.getIndex() + ":" + t.getValue());
		    
    		int df1   = journal_tfidf.getDocumentFrequency(t);
            double w1 = Math.log( journal_tfidf.getCollectionSize()/df1 );
                		
    		System.out.println(df1 + " " + w1 + " " + sw1);
    		
    		StringWrapper sw2 = journal_tfidf.prepare(j2);
    		int df2   = journal_tfidf.getDocumentFrequency(
    		        journal_tfidf.getTokens()[0]);
            double w2 = Math.log( journal_tfidf.getCollectionSize()/df2 );
    		System.out.println(df2 + " " + w2 + " " + sw2);    		
    		
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }
}
