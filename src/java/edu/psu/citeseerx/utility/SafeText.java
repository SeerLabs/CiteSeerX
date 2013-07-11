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
package edu.psu.citeseerx.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Verifier;

/**
 * Utilities for cleaning Strings for HTML and XML use, and translating
 * HTML-encoded entities.
 *
 * @author Isaac Councill
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class SafeText {

    public static final char[] badChars = {0x00, 0x01, 0x02, 0x03,
        0x04, 0x05, 0x06, 0x07, 0x08, 0x0B, 0x0C, 0x0E, 0x0F, 0x10,
        0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A,
        0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x7F};
    
    /**
     * @param str
     * @return a String stripped of all characters that are not safe for XML.
     */
    public static String stripBadChars(String str) {
        if ( null == str) {
            return null;
        }
        
        char []text = str.toCharArray();
        StringBuffer replacement = new StringBuffer(); 
        for (int i = 0, len = text.length; i < len; ++i ) {
            char ch = text[i];
            
            // first check if we have surrogates values.
            if (Character.isHighSurrogate(ch)) {
                i++;
                // check if the following char is the low surrogate.
                if (i < len) {
                    char low = str.charAt(i);
                    if (Character.isLowSurrogate(low)) {
                        /*
                         * Calculate the true value of the character to check 
                         * if it's XML valid.
                         */
                        int trueValue = Character.toCodePoint(ch, low);
                        if (Verifier.isXMLCharacter(trueValue)) {
                            // Add high surrogate and low surrogate to the
                            // string.
                            replacement.append(ch);
                            replacement.append(low);
                        }
                    }
                }
            }else{
                // It's not a surrogate. Let's check if it's valid.
                int codePoint = Character.codePointAt(text, i);
                if (Verifier.isXMLCharacter(codePoint)) {
                    replacement.append(ch);
                }
            }
        }
        return replacement.toString();
    } //- stripBadChars
    
    public static final String[] htmlSpecialChars = {"&", ">", "<", "\"", "'"};
    public static final String[] htmlCharEntities = {"&amp;", "&gt;", "&lt;", 
        "&quot;", "&apos;"};

    /**
     * @param str
     * @return a String with all HTML special characters encoded as & entities.
     */
    public static String encodeHTMLSpecialChars(String str) {
        if (str == null) {
            return null;
        }
        String replacement = str;
        for (int i=0; i<htmlSpecialChars.length; i++) {
            replacement =
                replacement.replace(htmlSpecialChars[i], htmlCharEntities[i]);
        }
        return replacement;
        
    }  //- encodeHTMLSpecialChars
    
    
    /**
     * @param str
     * @return a String with all special HTML entities translated back
     * to their canonical forms. 
     */
    public static String decodeHTMLSpecialChars(String str) {
        if (str == null) {
            return null;
        }
        String replacement = str;
        for (int i=0; i<htmlSpecialChars.length; i++) {
            replacement =
                replacement.replace(htmlCharEntities[i], htmlSpecialChars[i]);
        }
        return replacement;
        
    }  //- decodeHTMLSpecialChars
    
    
    /**
     * @param str
     * @return a String with bad characters removed and HTML characters
     * encoded.
     */
    public static String cleanXML(String str) {
        if (str == null) {
            return null;
        }
        String replacement = stripBadChars(str);
        replacement = encodeHTMLSpecialChars(replacement);
        return replacement;
        
    }  //- cleanXML
    
    
    private static final String punct =
        "\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~";
    private static final String atext = "[\\p{Alnum}" + punct + "]";
    private static final String atom = atext + "+";
    private static final String dotAtom = "\\." + atom;
    private static final String localPart = atom + "(" + dotAtom + ")*";
    private static final String letter = "\\p{Alpha}";
    private static final String letDig = "\\p{Alnum}";
    private static final String letDigHyp = "[\\p{Alnum}\\-]";
    private static final String rfcLabel =
        letDig + "(" + letDigHyp + "{0,61}" + letDig + ")?";
    private static final String domain =
        rfcLabel + "(\\." + rfcLabel + ")*\\." + letter + "{2,6}";
    private static final String addrSpec = "@" + domain;
    private static final Pattern emailPattern = Pattern.compile( addrSpec );
    
    /**
     * @param str
     * @return a String with the domain part of the email hidden.
     */
    public static String hideEmail(String str) {
        if (str == null) {
            return null;
        }
        Matcher m = emailPattern.matcher(str);
        return m.replaceAll("@(email omitted);");
    } //- hideEmail
    
    
    /**
     * @param s
     * @return a String with all non-ASCII characters translated to their
     * ASCII equivalents. 
     */
    public static String removeAccents(String s) {
        
        s = s.replaceAll( "[âàäáãå]", "a" );
        s = s.replaceAll( "[êèëé]", "e" );
        s = s.replaceAll( "[îìïí]", "i" );
        s = s.replaceAll( "[ôòöóõø]", "o" );
        s = s.replaceAll( "[ûùüú]", "u" );
        s = s.replaceAll( "ç", "c" );
        s = s.replaceAll( "ñ", "n" ); 

        s = s.replaceAll( "[ÂÀÄÁÃ]", "A" );
        s = s.replaceAll( "[ÊÈËÉ]", "E" );
        s = s.replaceAll( "[ÎÌÏÍ]", "I" );
        s = s.replaceAll( "[ÔÒÖÓÕØ]", "O" );
        s = s.replaceAll( "[ÛÙÜÚ]", "U" );
        s = s.replaceAll( "Ç", "C" );
        s = s.replaceAll( "Ñ", "N" ); 

        return s;
    } //- removeAccents
    
    /**
     * 
     * @param s
     * @return a String with all accents encoded as HTML entities translated
     * to their Non-ASCII representation
     */
    public static String HtmlAccentsToNonASCII(String s) {
    	
    	s = s.replace("&agrave;", "à");
    	s = s.replace("&aacute;", "á");
    	s = s.replace("&acirc;", "â");
    	s = s.replace("&atilde;", "ã");
    	s = s.replace("&auml;", "ä");
    	s = s.replace("&aring;", "å");
    	
    	s = s.replace("&egrave;", "è");
    	s = s.replace("&eacute;", "é");
    	s = s.replace("&ecirc;", "ê");
    	s = s.replace("&euml;", "ë");
    	
    	s = s.replace("&igrave;", "ì");
    	s = s.replace("&iacute;", "í");
    	s = s.replace("&icirc;", "î");
    	s = s.replace("&iuml;", "ï");
    	
    	s = s.replace("&ograve;", "ò");
    	s = s.replace("&oacute;", "ó");
    	s = s.replace("&ocirc;", "ô");
    	s = s.replace("&otilde;", "õ");
    	s = s.replace("&ouml;", "ö");
    	s = s.replace("&oslash;", "ø");
    	
    	s = s.replace("&ugrave;", "ù");
    	s = s.replace("&uacute;", "ú");
    	s = s.replace("&ucirc;", "û");
    	s = s.replace("&uuml;", "ü");
    	
    	s = s.replace("&ccedil;", "ç");
    	s = s.replace("&ntilde;", "ñ");
    	
    	s = s.replace("&Agrave;", "À");
    	s = s.replace("&Aacute;", "Á");
    	s = s.replace("&Acirc;", "Â");
    	s = s.replace("&Atilde;", "Ã");
    	s = s.replace("&Auml;", "Ä");
    	s = s.replace("&Aring;", "Å");
    	
    	s = s.replace("&Egrave;", "È");
    	s = s.replace("&Eacute;", "É");
    	s = s.replace("&Ecirc;", "Ê");
    	s = s.replace("&Euml;", "Ë");
    	
    	s = s.replace("&Igrave;", "Ì");
    	s = s.replace("&Iacute;", "Í");
    	s = s.replace("&Icirc;", "Î");
    	s = s.replace("&Iuml;", "Ï");
    	
    	s = s.replace("&Ograve;", "Ò");
    	s = s.replace("&Oacute;", "Ó");
    	s = s.replace("&Ocirc;", "Ô");
    	s = s.replace("&Otilde;", "Õ");
    	s = s.replace("&Ouml;", "Ö");
    	s = s.replace("&Oslash;", "Ø");
    	
    	s = s.replace("&Ugrave;", "U");
    	s = s.replace("&Uacute;", "U");
    	s = s.replace("&Ucirc;", "U");
    	s = s.replace("&Uuml;", "U");
    	
    	s = s.replace("&Ccedil;", "Ç");
    	s = s.replace("&Ntilde;", "Ñ");
    	
    	return s;
    } //- removeHtmlAccents
    
    /**
     * Converts accents encoded as HTML entities in their Non-ASCII 
     * counterparts and then convert them to ASCII. 
     * @param s
     * @return the given string with all accents encoded as HTML converted to
     * their ASCII equivalents
     */
    public static String removeAllAccents(String s) {
    	return removeAccents(HtmlAccentsToNonASCII(s));
    } //- removeAllAccents
    
    /**
     * @param s
     * @return a String with all punctuation stripped.
     */
    public static String stripPunctuation(String s) {
        s = s.replaceAll( "[^\\p{L}\\p{Lu}\\d ]","");
        s = s.replaceAll( "  +"," ");
        return s;
    } //- stripPunctuation
    
    
    /**
     * Alias for stripPunctuation.
     * @param s
     * @return a String with all punctuation stripped.
     */
    public static String normalizeText(String s) {
        return stripPunctuation(removeAccents(s));
    } //- normalizeText
    
}  //- class SafeText
