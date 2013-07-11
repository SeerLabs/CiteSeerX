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
package edu.psu.citeseerx.exec.respmod.handler;

/**
 * Thrown when the response modifier does not conform to the response
 * modifier development policy. Currently, the policy is to subclass
 * the response modifier base class.
 * 
 * @author Levent Bolelli
 *
 */

public class InvalidResponseModifierException extends Exception{

	static final long serialVersionUID = 7871215085972144821L;
	final Class<?> responseModifier;
	
	public InvalidResponseModifierException(Class<?> c) {
		responseModifier = c;
	}
	
	public String getMessage() {
        return "Class " + responseModifier.toString() + " does not extend" +
        		  " base response modifier class\n";
    }
}