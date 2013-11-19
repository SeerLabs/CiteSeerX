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
package edu.psu.citeseerx.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Utility functions to provide citation information about a document in
 * different formats.
 * @author Isaac Councill 
 * @author Juan Pablo Fernandez Ramirez
 * Version: $$Rev$$ $$Date$$
 */
public class BiblioTransformer {

    /**
     * @param doc
     * @return a String representing the Refer/BibIX format of the document
     * metadata.
     */
    public static String toReferBibIX(ThinDoc doc) {
        
        StringBuffer buffer = new StringBuffer();

        String ventype = doc.getVentype();
        if (ventype == null) {
            ventype = "MISC";
        }else{
            ventype = ventype.toUpperCase();
        }
        
        if (ventype.equals("JOURNAL")) {
            buffer.append("%0 Journal Article\n");
            if (doc.getVenue() != null) {
                buffer.append("%J ");
                buffer.append(doc.getVenue());
                buffer.append("\n");
            }
        } else if (ventype.equals("CONFERENCE")) {
            buffer.append("%0 Conference Paper\n");
            if (doc.getVenue() != null) {
                buffer.append("%B ");
                buffer.append(doc.getVenue());
                buffer.append("\n");
            }
        } else if (ventype.equals("TECHREPORT")) {
            buffer.append("%0 Report\n");
            if (doc.getTech() != null) {
                buffer.append("%R ");
                buffer.append(doc.getTech());
                buffer.append("\n");
            }
        } else {
            buffer.append("%0 Generic\n");
            if (doc.getVenue() != null) {
                buffer.append("%J ");
                buffer.append(doc.getVenue());
                buffer.append("\n");
            }
        }
        if (doc.getTitle() != null) {
            buffer.append("%T ");
            buffer.append(doc.getTitle());
            buffer.append("\n");
        }
        if (doc.getAuthors() != null) {
            StringTokenizer st = new StringTokenizer(doc.getAuthors(), ",");
            while (st.hasMoreTokens()) {
                buffer.append("%A ");
                buffer.append(normalizeAuthorName(st.nextToken().trim()));
                buffer.append("\n");
            }
        }
        if (doc.getYear() > 0) {
            buffer.append("%D ");
            buffer.append(doc.getYear());
            buffer.append("\n");
        }
        if (doc.getPublisher() != null) {
            buffer.append("%I ");
            buffer.append(doc.getPublisher());
            buffer.append("\n");
        }
        if (doc.getPages() != null) {
            buffer.append("%P ");
            buffer.append(doc.getPages());
            buffer.append("\n");
        }
        if (doc.getVol() > 0) {
            buffer.append("%V ");
            buffer.append(doc.getVol());
            buffer.append("\n");
        }
        if (doc.getNum() > 0) {
            buffer.append("%N ");
            buffer.append(doc.getNum());
            buffer.append("\n");
        }

        return buffer.toString();
        
    }  //- toReferBibIX
    
    
    /**
     * @param doc
     * @return A string representing the BibTeX format of the document
     * metadata. 
     */
    public static String toBibTeX(ThinDoc doc) {
        
        String ventype = doc.getVentype();
        if (ventype == null) {
            ventype = "MISC";
        }else{
            ventype = ventype.toUpperCase();
        }
        
        if (ventype.equals("JOURNAL")) {
            return makeArticleBibTeX(doc);
        } else if (ventype.equals("CONFERENCE")) {
            return makeConferenceBibTeX(doc);
        } else if (ventype.equals("TECHREPORT")) {
            return makeTechBibTeX(doc);
        } else {
            return makeMiscBibTeX(doc);
        }
        
    }  //- toBibTeX
    
    /**
     * Returns a percent-encoded string representing a ContextOject for doc
     * @param doc
     * @param url base URL to see the Doc. The string "?doi=" + the DOI's object
     * will be appended to this URL if any.
     * @return Percent-encode string representing the ContextObject for doc. 
     */
    public static String toCOinS(ThinDoc doc, String url) {
    	StringBuffer buffer =  new StringBuffer();
    	
    	/*
    	 * Add OpenURL ContextObject version (1.0) for an article.
    	 */
    	buffer.append("url_ver=Z39.88-2004");
    	buffer.append("&url_ctx_fmt=");
    	buffer.append(encode("info:ofi/fmt:kev:mtx:ctx"));
    	buffer.append("&ctx_ver=Z39.88-2004");
    	buffer.append("&ctx_enc=");
    	buffer.append(encode("info:ofi/enc:UTF-8"));
    	buffer.append("&rft_val_fmt=");
    	buffer.append(encode("info:ofi/fmt:kev:mtx:journal"));

    	if (url != null && url.trim().length() > 0) {
    		buffer.append("&rft_id=");
    		buffer.append(encode(url));
    		buffer.append(encode("?doi="));
    		buffer.append(encode(doc.getDoi()));
    	}
    	if (doc.getTitle() != null) {
    		buffer.append("&rft.atitle=");
    		buffer.append(encode(doc.getTitle()));
    	}
    	if (doc.getVenue() != null) {
    		buffer.append("&rft.jtitle=");
    		buffer.append(encode(doc.getVenue().toUpperCase()));
    	}
    	if (doc.getYear() > 0) {
    		buffer.append("&rft.date=");
    		buffer.append(doc.getYear());
    	}
    	if (doc.getVol() > 0) {
    		buffer.append("&rft.volume=");
    		buffer.append(doc.getVol());
    	}
    	if (doc.getPages() != null) {
    	    String pages = doc.getPages(); 
    	    int index = pages.indexOf('-');
    	    if (index > -1) {
    	        try {
        	        int start = Integer.parseInt(pages.substring(0, index));
        	        int end = Integer.parseInt(pages.substring(index+2));
        	        buffer.append("&rft.spage=");
        	        buffer.append(start);
        	        buffer.append("&rft.epage=");
                    buffer.append(end);
    	        }catch (NumberFormatException e) {
                    return null;
                }
    	    }
    		buffer.append("&rft.pages=");
    		buffer.append(encode(doc.getPages()));
    	}
    	
    	if (doc.getNum() > 0) {
    	    buffer.append("&rft.issue=");
    	    buffer.append(doc.getNum());
    	}
    	
    	String ventype = doc.getVentype();
        if ( (ventype != null) && (ventype.equals("JOURNAL") || 
                (ventype.equals("CONFERENCE") || ventype.equals("TECHREPORT"))) 
           ) {
            ventype="article";
        } else {
        	ventype="unknown";
        }
    	buffer.append("&rft.genre=");
    	buffer.append(encode(ventype));
    	if (doc.getAuthors() != null) {
            String []tokens = doc.getAuthors().split(",");
            for (int i = 0; i < tokens.length; ++i) {
                if ( i == 0) {
                    String name = normalizeAuthorName(tokens[i].trim());
                    int index = name.indexOf(",");
                    if (index != -1) {
                        buffer.append("&rft.aulast=");
                        buffer.append(encode(name.substring(0, index)));
                        buffer.append("&rft.aufirst=");
                        buffer.append(encode(name.substring(index+1)));
                    }else{
                        buffer.append("&rft.aulast=");
                        buffer.append(encode(name));
                    }
                }
                buffer.append("&rft.au=");
                buffer.append(
                		encode(normalizeAuthorName(tokens[i].trim())));
            }
        }
    	
    	return buffer.toString();
    } //- toCOinS
    
    /**
     * @param tDocList
     * @param url base URL to see the documents. The string "?doi=" + the DOI's
     *  object will be appended to this URL if any.
     * @param String domain domain to be added as part of the referrer id
     * @return a list of percent-encode string representing a ContextObject for
     * each one of the documents in tDocList. The returned list will have the 
     * size of tDocList.
     */
    public static List<String> toCOinS(List<ThinDoc> tDocList, String url) {
    	List<String> ctxObjects = new ArrayList<String>();
    	Iterator<ThinDoc> it = tDocList.iterator();
    	
    	while (it.hasNext()) {
    		ThinDoc doc = it.next();
		String coinString = toCOinS(doc,url);
		if(coinString != null) {
    			ctxObjects.add(coinString);
		}
    	}
    	return ctxObjects;
    } //- toCOinS
    
    
    private static String makeConferenceBibTeX(ThinDoc doc) {
        
        String marker = makeMarker(doc);
        
        StringBuffer buffer = new StringBuffer();

        buffer.append("@INPROCEEDINGS{");
        buffer.append(marker);
        
        buffer.append(makeAuthorsBibTeX(doc));

        buffer.append(",\n\ttitle = {");
        if (doc.getTitle() != null) {
            buffer.append(doc.getTitle());
        }
        buffer.append("}");
        
        buffer.append(",\n\tbooktitle = {");
        if (doc.getVenue() != null) {
            buffer.append(doc.getVenue());
        }
        buffer.append("}");

        buffer.append(",\n\tyear = {");
        if (doc.getYear() > 0) {
            buffer.append(doc.getYear());
        }
        buffer.append("}");
        
        if (doc.getPages() != null) {
            buffer.append(",\n\tpages = {");
            buffer.append(doc.getPages());
            buffer.append("}");
        }

        if (doc.getPublisher() != null) {
            buffer.append(",\n\tpublisher = {");
            buffer.append(doc.getPublisher());
            buffer.append("}");
        }

        buffer.append("\n}\n");
        return buffer.toString();
        
    }  //- makeConferenceBibTeX
    
    
    private static String makeArticleBibTeX(ThinDoc doc) {
        
        String marker = makeMarker(doc);
        
        StringBuffer buffer = new StringBuffer();

        buffer.append("@ARTICLE{");
        buffer.append(marker);
        
        buffer.append(makeAuthorsBibTeX(doc));

        buffer.append(",\n\ttitle = {");
        if (doc.getTitle() != null) {
            buffer.append(doc.getTitle());
        }
        buffer.append("}");
        
        
        buffer.append(",\n\tjournal = {");
        if (doc.getVenue() != null) {
            buffer.append(doc.getVenue());
        }
        buffer.append("}");

        buffer.append(",\n\tyear = {");
        if (doc.getYear() > 0) {
            buffer.append(doc.getYear());
        }
        buffer.append("}");

        if (doc.getVol() > 0) {
            buffer.append(",\n\tvolume = {");
            buffer.append(doc.getVol());
            buffer.append("}");
        }
        
        if (doc.getNum() > 0) {
            buffer.append(",\n\tnumber = {");
            buffer.append(doc.getNum());
            buffer.append("}");
        }

        if (doc.getPages() != null) {
            buffer.append(",\n\tpages = {");
            buffer.append(doc.getPages());
            buffer.append("}");
        }

        buffer.append("\n}\n");
        return buffer.toString();
        
    }  //- makeArticleBibTeX
    
    
    private static String makeTechBibTeX(ThinDoc doc) {
        
        String marker = makeMarker(doc);
        
        StringBuffer buffer = new StringBuffer();

        buffer.append("@TECHREPORT{");
        buffer.append(marker);
        
        buffer.append(makeAuthorsBibTeX(doc));

        buffer.append(",\n\ttitle = {");
        if (doc.getTitle() != null) {
            buffer.append(doc.getTitle());
        }
        buffer.append("}");
        
        buffer.append(",\n\tinstitution = {");
        if (doc.getVenue() != null) {
            buffer.append(doc.getVenue());
        }
        buffer.append("}");

        buffer.append(",\n\tyear = {");
        if (doc.getYear() > 0) {
            buffer.append(doc.getYear());
        }
        buffer.append("}");

        buffer.append("\n}\n");
        return buffer.toString();
        
    }  //- makeTechBibTeX
    
    
    private static String makeMiscBibTeX(ThinDoc doc) {
        
        String marker = makeMarker(doc);
        
        StringBuffer buffer = new StringBuffer();

        buffer.append("@MISC{");
        buffer.append(marker);
        
        buffer.append(makeAuthorsBibTeX(doc));

        buffer.append(",\n\ttitle = {");
        if (doc.getTitle() != null) {
            buffer.append(doc.getTitle());
        }
        buffer.append("}");
        
        buffer.append(",\n\tyear = {");
        if (doc.getYear() > 0) {
            buffer.append(doc.getYear());
        }
        buffer.append("}");
        
        buffer.append("\n}\n");
        return buffer.toString();
        
    }  //- makeMiscBibTeX
    
    
    private static String makeAuthorsBibTeX(ThinDoc doc) {
        
        StringBuffer authorBuf = new StringBuffer();
        authorBuf.append(",\n\tauthor = {");
        if (doc.getAuthors() != null) {
            StringTokenizer st = new StringTokenizer(doc.getAuthors(), ",");
            while (st.hasMoreTokens()) {
                authorBuf.append(st.nextToken().trim());
                if (st.hasMoreTokens()) {
                    authorBuf.append(" and ");
                }
            }
        }
        authorBuf.append("}");
        return authorBuf.toString();
        
    }  //- makeAuthorsBibTeX
    
    
    private static String[] linkingNames = {
        "van", "von", "der", "den",
        "de", "di", "le", "el"
    };
    private static HashSet<String> linkingNamesRef = new HashSet<String>();
    static {
        for (String name : linkingNames) {
            linkingNamesRef.add(name);
        }
    }
    
    private static String normalizeAuthorName(String name) {
        
        if (name == null) {
            return null;
        }
        
        StringBuffer first = new StringBuffer(); 
        StringBuffer last = new StringBuffer();
        StringTokenizer st = new StringTokenizer(name);

        int n = st.countTokens();
        int i = 1;
        boolean parsingFirst = true;
        
        while (st.hasMoreTokens()) {
            if (i == n) {
                last.append(st.nextToken());
                break;
            }
            String token = st.nextToken();
            if (parsingFirst) {
                if (linkingNamesRef.contains(token.toLowerCase())) {
                    last.append(token);
                    last.append(" ");
                    parsingFirst = false;
                } else {
                    first.append(token);
                    first.append(" ");
                }
            } else {
                last.append(token);
                last.append(" ");
            }
            i++;
        }
        String s1 = first.toString();
        String s2 = last.toString();
        s1 = s1.trim();
        s2 = s2.trim();
        if (s1.length() > 0) {
            return s2+", "+s1;
        } else {
            return s2;
        }
        
    }  //- normalizeAuthorName
    
    
    private static String makeMarker(ThinDoc doc) {
        
        String authors = doc.getAuthors();
        if (authors == null) authors = "";
        
        int firstComma = authors.indexOf(',');
        if (firstComma > 0) {
            authors = authors.substring(0, firstComma);
        }
        authors = authors.trim();
        String authorTag = authors;
        int lastSpace = authorTag.lastIndexOf(' ');
        if (lastSpace>=0) {
            authorTag = authorTag.substring(lastSpace+1);
        }
        
        int year = doc.getYear();
        String yearTag = "_";
        if (year > 0) {
            yearTag = Integer.toString(year);
            if (yearTag.length() == 4) {
                yearTag = yearTag.substring(2);
            }
        }
        
        String title = doc.getTitle();
        String titleTag = "";
        if (title != null) {
            StringTokenizer st = new StringTokenizer(title);
            int maxTokens = 2;
            int count = 1;
            while (st.hasMoreTokens()) {
                titleTag += st.nextToken().toLowerCase();
                if (++count > maxTokens) {
                    break;
                }
            }
        }
        
        return authorTag+yearTag+titleTag;
        
    }  //- makeMarker
    
    private static String encode(String s) {
    	try {
    		if (s != null) {
    			s = URLEncoder.encode(s, "UTF-8");
    		}
		}catch (UnsupportedEncodingException e) {
			s = "";
		}
		return s;
    } //- encode
    
    
    public static void main(String[] args) {
        String[] tests = { "M D Ernst", "J Cockrell", "J-S Gutmann", "K van der Waald" };
        for (String test : tests) {
            System.out.println(normalizeAuthorName(test));
        }
    } //- main
    
}  //- class BiblioTransformer
