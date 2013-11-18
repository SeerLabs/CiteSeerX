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
package edu.psu.citeseerx.citematch.utils;

/**
 * Contains a list of default stopwords that are generally safe to remove
 * from title strings in citations.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DefaultStopwordList {

    private static final String[] stopwords = {
            "a",
            "an",
            "and",
            "any",
            "are",
            "as",
            "at",
            "b",
            "be",
            "but",
            "by",
            "c",
            "can",
            "d",
            "e",
            "f",
            "for",
            "g",
            "h",
            "he",
            "her",
            "him",
            "his",
            "how",
            "i",
            "in",
            "into",
            "is",
            "it",
            "its",
            "j",
            "k",
            "l",
            "m",
            "me",
            "mr",
            "mrs",
            "my",
            "n",
            "o",
            "of",
            "on",
            "or",
            "our",
            "p",
            "q",
            "r",
            "s",
            "she",
            "so",
            "t",
            "the",
            "this",
            "to",
            "u",
            "us",
            "v",
            "w",
            "was",
            "we",
            "x",
            "y",
            "you",
            "your",
            "yours",
            "z" 
    };
    
    public static String[] getStopwords() {
        return stopwords;
    } //- getStopwords
    
}  //- class DefaultStopwordList
