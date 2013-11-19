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
package edu.psu.citeseerx.myciteseer.web.utils;

import java.util.Random;

/**
 * Random string generator 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class RandomStringGenerator {

    private static final Random rn = new Random();
    
    private static int rand(int lo, int hi) {
        int n = hi-lo+1;
        int i = rn.nextInt() % n;
        if (i<0)
            i = -i;
        return lo+i;
    } //- rand
    
    /**
     * 
     * @param minLength Minimum length of the generated string
     * @param maxLength Maximum length of the generated string
     * @return a random string with length between minLength and maxLenght
     */
    public static String randomString(int minLength, int maxLength) {
        int n = rand(minLength, maxLength);
        byte b[] = new byte[n];
        for (int i=0; i<n; i++)
            b[i] = (byte)rand('a', 'z');
        return new String(b);
    } //- randomString
    
} //- class RandomStringGenerator
