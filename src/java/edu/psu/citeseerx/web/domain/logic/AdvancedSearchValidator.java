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
package edu.psu.citeseerx.web.domain.logic;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.psu.citeseerx.web.domain.AdvancedSearch;

/**
 * Provides validation utilities to validate user input in the advance search
 * form
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class AdvancedSearchValidator implements Validator {

    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return AdvancedSearch.class.isAssignableFrom(clazz);
    } //- supports
    
    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object obj, Errors errors) {
        AdvancedSearch advSearch = (AdvancedSearch)obj;
        
        if (advSearch.getYear() != null && advSearch.getYear().length()>0) {
            try {
                Integer.parseInt(advSearch.getYear());
            } catch (NumberFormatException e) {
                errors.rejectValue("year",
                        "INVALID_YEAR", "Invalid year format");
            }
        }

        if (advSearch.getYearFrom() != null
                && advSearch.getYearFrom().length()>0) {
            try {
                Integer.parseInt(advSearch.getYearFrom());
            } catch (NumberFormatException e) {
                errors.rejectValue("yearFrom",
                        "INVALID_YEAR", "Invalid year format");
            }
        }

        if (advSearch.getYearTo() != null && advSearch.getYearTo().length()>0) {
            try {
                Integer.parseInt(advSearch.getYearTo());
            } catch (NumberFormatException e) {
                errors.rejectValue("yearTo",
                        "INVALID_YEAR", "Invalid year format");
            }
        }
        
        if (advSearch.getMinCitations() != null
                && advSearch.getMinCitations().length()>0) {
            try {
                Integer.parseInt(advSearch.getMinCitations());
            } catch (NumberFormatException e) {
                errors.rejectValue("minCitations",
                        "INVALID_CITES", "Invalid number format");
            }
        }

    } //- validate
    
} //- class AdvancedSearchValidator
