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


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.wcohen.ss.Jaccard;
import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.SoftTFIDF;
import com.wcohen.ss.UnnormalizedTFIDF;
import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

import edu.psu.citeseerx.utility.StringUtil;
import edu.psu.citeseerx.disambiguation.EmailTokenizer;
import edu.psu.citeseerx.disambiguation.TFIDFTrainer;
import edu.psu.citeseerx.disambiguation.DFTable;
import edu.psu.citeseerx.disambiguation.DisambiguationService;
import edu.psu.citeseerx.disambiguation.dao.Author;
import edu.psu.citeseerx.disambiguation.dao.CiteSeerDoc;

/**
 * ProfileDistance
 * 
 * @author Puck Treeratpituk
 * @version $Rev$ $Date$
 */
public class ProfileDistance
{
    // create a SoftTFIDF distance learner
    double minTokenSimilarity = 0.8;
    
    Tokenizer emailTokenizer  = new EmailTokenizer(true, true);
	Tokenizer tokenizer      = new SimpleTokenizer(true, true);
    SoftTFIDF aff_soft			   = new SoftTFIDF(tokenizer,  
            new JaroWinkler(), minTokenSimilarity);
	UnnormalizedTFIDF aff_tfidf	   = new UnnormalizedTFIDF(tokenizer);
	Jaccard jaccard				   = new Jaccard(tokenizer);
	Jaccard emailJaccard           = new Jaccard(emailTokenizer);
    
	//FreqTable lname_freq;
	DFTable lname_freq;
	
	// TFIDF using SecondString... 
	TFIDFTrainer trainer = new TFIDFTrainer();
	String affModelFile;
	String lnameModelFile;
	//String AFF_MODEL_FILE       = "data/medline/medline_aff.prune.model";
	//String LNAME_MODEL_FILE     = "data/medline/medline_lastname.prune.model";
	
	public enum FeatType {
		DID1, DID2,                                                         // (2)
		AUTH_FIRST, AUTH_MIDDLE, AUTH_ORDER, AUTH_LAST_IDF, EMAIL_JACCARD,  // (5) author related
		AFF_SOFTTFIDF, AFF_TFIDF, AFF_JACCARD,                              // (3) affiliation
		COAUTH_LNAME_SHARED, COAUTH_LNAME_IDF, COAUTH_LNAME_JACCARD,        // (3) coauthors
	    TITLE_SHARED                                                        // (1) title
		//LABEL
	};

	public static final Map<FeatType,String> FEATURES = 
	    new EnumMap<FeatType,String>(FeatType.class) {

            private static final long serialVersionUID = 9122672875982911413L;

        // unamed block...
	    {
			put(FeatType.DID1, "did1 numeric");
			put(FeatType.DID2, "did2 numeric");	        
			
			put(FeatType.AUTH_FIRST,    "auth_fst numeric");
			put(FeatType.AUTH_MIDDLE,   "auth_mid numeric");
			put(FeatType.AUTH_ORDER,    "auth_ord numeric");
			put(FeatType.AUTH_LAST_IDF, "auth_last_idf real");
			put(FeatType.EMAIL_JACCARD, "email_jac real");
			
			put(FeatType.AFF_SOFTTFIDF,  "aff_softtfidf real");
			put(FeatType.AFF_TFIDF,      "aff_tfidf real");
			put(FeatType.AFF_JACCARD,    "aff_jac real");
			
			put(FeatType.COAUTH_LNAME_SHARED,  "coauth_lname_shared numeric");  // number of shared lastname
			put(FeatType.COAUTH_LNAME_IDF,     "coauth_lname_idf real");        // IDF  of shared lastname
			put(FeatType.COAUTH_LNAME_JACCARD, "coauth_lname_jac real");        // Jaccard distance between coauthors' lastname
			
			put(FeatType.TITLE_SHARED,      "title_shared numeric");            //  

    		//put(FeatType.LABEL,  "same {1, 0}");       //	
	    }
	};
	
	public static ProfileDistance pdist = null;
	
	public static ProfileDistance getProfileDistance() {
	    if (pdist == null) {
	        try {
	            pdist = new ProfileDistance();
            } catch (Exception e) { }
	    }
	    return pdist;
	}
	
	public ProfileDistance() { }
    public ProfileDistance(String affModelFile, String lnameModelFile) 
    throws Exception {
		setAffModelFile(affModelFile);
		setLnameModelFile(lnameModelFile);
    }

	public void setAffModelFile(String affModelFile) throws IOException {
		this.affModelFile = affModelFile;
        trainer.readModel(this.affModelFile, aff_soft,  tokenizer);
		trainer.readModel(this.affModelFile, aff_tfidf, tokenizer);		
	}

	public void setLnameModelFile(String lnameModelFile)  throws IOException {
		this.lnameModelFile = lnameModelFile;
		lname_freq = new DFTable(this.lnameModelFile);
	}

    public EnumMap<FeatType,Double> calcFeatures(CiteSeerDoc c1, Author auth1,
            CiteSeerDoc c2, Author auth2) {
		EnumMap<FeatType,Double> feats = 
		    new EnumMap<FeatType,Double>(FeatType.class);
	
		// 1: pmid1, pmid2
		feats.put(FeatType.DID1, Double.valueOf(auth1.getId()));
		feats.put(FeatType.DID2, Double.valueOf(auth2.getId()));
        // 2: author (name, position, etc.)
        calcAuthorFeats(c1, auth1, c2, auth2, feats);
		// 3: affiliations 
		calcAffFeats(auth1.getAffil(), auth2.getAffil(), feats);		
		// 4: shared-coauthors
		calcCoAuthFeats(c1, auth1.getOrder(), c2, auth2.getOrder(), feats);				
        // 7: shared-title
		//System.out.println(c1.getId() + ":" + c2.getId());
        calcTitleFeats(c1.getTitle(), c2.getTitle(), feats);
		// shared-keyword ???
		
		return feats;
	}

	protected void calcTitleFeats(String title1, String title2, 
	        EnumMap<FeatType,Double> feats) {
        // 7) shared title
		if ((title1 != null) && (title2 != null)) {
			if (FEATURES.containsKey(FeatType.TITLE_SHARED)) {
				double jac_sim = jaccard.score(title1, title2);
				if (!(jac_sim >= 0.0))
					jac_sim = 0.0;
				//feats.put(FeatType.TITLE_SHARED, String.valueOf(jac_sim));
				feats.put(FeatType.TITLE_SHARED, new Double(jac_sim));
			}
		}
		else {
			if (FEATURES.containsKey(FeatType.TITLE_SHARED))     
				feats.put(FeatType.TITLE_SHARED, new Double(0));
		}
    }
	protected void calcAuthorFeats(CiteSeerDoc c1, Author auth1, CiteSeerDoc c2,
	        Author auth2, EnumMap<FeatType,Double> feats) {
		// 2) author information

        // first name
        // 3: F1 = F2, both full
        // 2: F1 compat F2, not both full
        // 1: F1 not compat F2, not both full
        // 0: F1 != F2, both full
        if (FEATURES.containsKey(FeatType.AUTH_FIRST)) {
            String f1 = auth1.getFirstName();
            String f2 = auth2.getFirstName();
            double f;
            if ((f1 == null) || (f2 == null)) {
                f = 1.0;
            }
            else if ((f1.length() > 1) && (f2.length() > 1)) {
                if (StringUtil.relaxStrEquals(f1, f2))
                    f = 3.0;
                else f = 0.0;
            }
            else {
                if (f1.charAt(0) == f2.charAt(0)) // "J" = "James"
                    f = 2.0;
                else f = 1.0;
            }
            feats.put(FeatType.AUTH_FIRST, new Double(f));
        }
        
        // IDF of author lastname
        if (FEATURES.containsKey(FeatType.AUTH_LAST_IDF)) {
            double lname_idf = lname_freq.getIDFScore(auth1.getLastName());
            feats.put(FeatType.AUTH_LAST_IDF, new Double(lname_idf));            
        }

		// middle initial
		// 3: M1 = M2, both are given
		// 2: M1 = M2, both are not given 
		// 1: M1 != M2 (one is given, one is not)
		// 0: M1 != M2 (both are given)
		if (FEATURES.containsKey(FeatType.AUTH_MIDDLE)) {
            String m1 = auth1.getMiddleName();
            String m2 = auth2.getMiddleName();

			if ((m1.length() == 1) && (m2.length() > 1))
				m2 = m2.substring(0,1);
			else if ((m2.length() == 1) && (m1.length() > 1))
				m1 = m1.substring(0,1);

            Double f;
            if (m1.equals(m2)) { // m1 == m2
                if (!m1.equals(""))
                    f = 3.0;
                else f = 2.0;
            }
            else { // m1 != m2
                if (!m1.equals("") && !m2.equals(""))
                    f = 0.0;
                else f = 1.0;
            }
            feats.put(FeatType.AUTH_MIDDLE, new Double(f));
		}
		
		// order
		// 2: O1 = O2 = 1, both are first auth
		// 1: both are last auth
		// 0: otherwise
		// 2 = both are 1st author, 1 = one is 1st author, 0 = none is 1st author
		if (FEATURES.containsKey(FeatType.AUTH_ORDER)) {
		    double f;
		    if ((auth1.getOrder() == 1) && (auth2.getOrder() == 1))
		        f = 2.0;
		    else if ((auth1.getOrder() == c1.getNumAuthors()) && 
		            (auth2.getOrder() == c2.getNumAuthors()))
		        f = 1.0;
		    else f = 0.0;
		    feats.put(FeatType.AUTH_ORDER, new Double(f));
		}

		// email
		// Jaccard
		if (FEATURES.containsKey(FeatType.EMAIL_JACCARD)) {
			String email1 = auth1.getEmail();
			String email2 = auth2.getEmail();
			if ((email1 != null) && (email2 != null) && !email1.equals("") &&
			        !email2.equals("")) {
                double jac_sim = jaccard.score(email1, email2);
				if (!(jac_sim >= 0.0))
					jac_sim = 0.0;
			    feats.put(FeatType.EMAIL_JACCARD, new Double(jac_sim));
			}
    		else {
    		    feats.put(FeatType.EMAIL_JACCARD, new Double(0.0));
    		}    		
		}
    }

	// order1, order2 is the order of author1 and author2 ..thus wont be use to
	// compare
	protected void calcCoAuthFeats(CiteSeerDoc c1, int order1, CiteSeerDoc c2,
	        int order2, Map<FeatType,Double> feats) {
		// 4) shared-coauthors, 
		// only use lastnames
		
		String auths1 = c1.getLastNames();
		String auths2 = c2.getLastNames();
		if ((auths1 != null) && (auths2 != null)) {
			List<Author> shared_auths = 
			    c1.intersectCoAuthors(c2, order1-1, order2-1);

			// # Shared-Coauthors
			if (FEATURES.containsKey(FeatType.COAUTH_LNAME_SHARED)) {
			    feats.put(FeatType.COAUTH_LNAME_SHARED, 
			            new Double(shared_auths.size()));
			}
			// IDF
			if (FEATURES.containsKey(FeatType.COAUTH_LNAME_IDF)) {
			    String[] lnames = new String[shared_auths.size()];
			    for (int i = 0; i < shared_auths.size(); i++) { 
			        // create String[]
			        lnames[i] = shared_auths.get(i).getLastName(); 
					// NOTE: the shared_auths is an organization??
					// ex. HyperCP Collaboration (15697968 : 15698162)
					if (lnames[i] == null) { 
						lnames[i] = "*******";
					}
				}
			    double idf_sim   = lname_freq.getIDFScore(lnames);
			    feats.put(FeatType.COAUTH_LNAME_IDF, new Double(idf_sim));    			
			}			
			// Jaccard
			if (FEATURES.containsKey(FeatType.COAUTH_LNAME_JACCARD)) {
				double shared = shared_auths.size();
				double union  = c1.getNumAuthors() + 
				    c2.getNumAuthors() - shared;
				double jac_sim = shared / union;
				if (!(jac_sim >= 0.0)) {
					jac_sim = 0.0;
				}
			    feats.put(FeatType.COAUTH_LNAME_JACCARD, new Double(jac_sim));
			}
		}
		else {
			if (FEATURES.containsKey(FeatType.COAUTH_LNAME_SHARED)) {
			    feats.put(FeatType.COAUTH_LNAME_SHARED, new Double(0));
			}
			if (FEATURES.containsKey(FeatType.COAUTH_LNAME_IDF)) {
			    feats.put(FeatType.COAUTH_LNAME_IDF, new Double(0));
			}
			if (FEATURES.containsKey(FeatType.COAUTH_LNAME_JACCARD)) {
			    feats.put(FeatType.COAUTH_LNAME_JACCARD, new Double(0));
			}
		}
	}		
	
	protected void calcAffFeats(String affil1, String affil2, 
	        EnumMap<FeatType,Double> feats) {		
		// 3) affiliation
		if ((affil1 != null) && (affil2 != null) &&
		    (!affil1.equals("")) && (!affil2.equals(""))) { // NOT NULL			
			// SOFT TFIDF
			if (FEATURES.containsKey(FeatType.AFF_SOFTTFIDF)) {
			    double soft_sim = aff_soft.score(affil1, affil2);
			    feats.put(FeatType.AFF_SOFTTFIDF, new Double(soft_sim));
		    }
    		// TFIDF
    		if (FEATURES.containsKey(FeatType.AFF_TFIDF)) {
    			double tfidf_sim = aff_tfidf.score(affil1, affil2);
    			feats.put(FeatType.AFF_TFIDF, new Double(tfidf_sim));
            }
    		// Jaccard
            if (FEATURES.containsKey(FeatType.AFF_JACCARD)) {
                double jac_sim = jaccard.score(affil1, affil2);
				if (!(jac_sim >= 0.0))
					jac_sim = 0.0;
			    feats.put(FeatType.AFF_JACCARD, new Double(jac_sim));
    		}
		}
		else {
			if (FEATURES.containsKey(FeatType.AFF_SOFTTFIDF)) {
			    feats.put(FeatType.AFF_SOFTTFIDF, new Double(0));
			}
			if (FEATURES.containsKey(FeatType.AFF_TFIDF)) {
			    feats.put(FeatType.AFF_TFIDF, new Double(0));
			}
			if (FEATURES.containsKey(FeatType.AFF_JACCARD)) {
			    feats.put(FeatType.AFF_JACCARD, new Double(0));
			}
		}
	}

	public void printArffDetail(EnumMap<FeatType,Double> feats) {
		for (FeatType type : FeatType.values()) {
			String val = String.valueOf(feats.get(type));

			System.out.println(type.name() + "\t = " + val);
		}
		System.out.println("");
	}
	
	public String getFeatureString(Map<FeatType,Double> feats, 
	        boolean withIDs) {
		String line = "";
		for (FeatType type : FeatType.values()) {
			String val = String.valueOf(feats.get(type));
			if (!withIDs && (type == FeatType.DID1 || type == FeatType.DID2)) {
				// ... 
			}
			else {
				if (type != FeatType.TITLE_SHARED) // last attribute
					val += ",";
				line += val;
			}
		}
		return line;
	}
	
	public void printArffExample(Map<FeatType,Double> feats, boolean withIDs) {
		for (FeatType type : FeatType.values()) {
			String val = String.valueOf(feats.get(type));
			if (!withIDs && (type == FeatType.DID1 || type == FeatType.DID2)) {
				// ... 
			}
			else {
				if (type != FeatType.TITLE_SHARED) // last attribute
					val += ",";
				System.out.print(val);
			}
		}
		System.out.println("");
	}

	public static void main_helper(Connection conn, 
	        DisambiguationService distService, String aid1, String aid2) 
		throws Exception {
	    
		CiteSeerDoc doc1 = new CiteSeerDoc(conn, new Integer(aid1));
		CiteSeerDoc doc2 = new CiteSeerDoc(conn, new Integer(aid2));		
		Author a1 = doc1.getAuthorById(aid1);
		Author a2 = doc2.getAuthorById(aid2);
		
		System.out.println(doc1);
		System.out.println(doc2);
		
		ProfileDistance pdist = ProfileDistance.getProfileDistance();
		
		EnumMap<FeatType,Double> feats = pdist.calcFeatures(doc1, a1, doc2, a2);
		String feat = pdist.getFeatureString(feats, false);
		pdist.printArffDetail(feats);
		//System.out.println(feat);

		double d = distService.calcSingleDistance(feat);
		System.out.println("D:" + d);
	}

	public static void main(String[] args) throws Exception {
	   
		String connectionURL ="jdbc:mysql://localhost:3306/citeseerx";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = 
		    DriverManager.getConnection(connectionURL,"csx-devel","csx-devel");

		DisambiguationService distService = new DisambiguationService();
		//distService.loadRandomForest();
		
		String aid1 = args[0];
		String aid2 = args[1];
		main_helper(conn, distService, aid1, aid2);
	}
}