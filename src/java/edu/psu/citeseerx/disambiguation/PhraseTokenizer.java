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

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.wcohen.ss.api.Token;
import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.BasicToken;

/**
 * PhraseTokenizer
 *
 * Simple implementation of a Tokenizer. Tokens are sequences of
 * alphanumerics, optionally including single punctuation characters.
 *
 * @author Puck Treeratpituk
 * @version $Rev$ $Date$
 */
public class PhraseTokenizer implements Tokenizer
{
    public static final PhraseTokenizer DEFAULT_TOKENIZER = 
        new PhraseTokenizer(true);
	
    private boolean ignoreCase = true;
	
    public PhraseTokenizer(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    // parameter setting
    public void setIgnoreCase(boolean flag)  { ignoreCase = flag; }
    public String toString() { return "[PhraseTokenizer "+ignoreCase+"]"; }
	
    /**  Return tokenized version of a string.  Tokens are sequences
     * of alphanumerics, or any single punctuation character. */
    public Token[] tokenize(String input) 
    {
        Token[] tokens = new Token[1];
        tokens[0] = internSomething(input);
        return tokens;
    }
    private Token internSomething(String s) 
    {
        return intern( ignoreCase ? s.toLowerCase() : s );
    }
	
    //
    // 'interning' strings as tokens
    //
    private int nextId = 0;
    private Map<String,Token> tokMap = new TreeMap<String,Token>();

    public Token intern(String s) 
    {
        Token tok = tokMap.get(s);
        if (tok==null) {
    	    tok = new BasicToken(++nextId,s);
    	    tokMap.put(s,tok);
        }
        return tok;
    }
	
    public Iterator<Token> tokenIterator()
    {
        return tokMap.values().iterator();
    }

    public int maxTokenIndex()
    {
        return nextId;
    }

    /** Test routine */
    public static void main(String[] argv) 
    {
        PhraseTokenizer tokenizer = DEFAULT_TOKENIZER;
        int n = 0;
        for (int i=0; i<argv.length; i++) {
            System.out.println("argument "+i+": '"+argv[i]+"'");
            Token[] tokens = tokenizer.tokenize(argv[i]);
            for (int j=0; j<tokens.length; j++) {
                System.out.println("token "+(++n)+":"
                    +" id="+tokens[j].getIndex()
                    +" value: '"+tokens[j].getValue()+"'");
            }
        }
    }
}
