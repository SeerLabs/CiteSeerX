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
package edu.psu.citeseerx.dao2.logic;

import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.domain.AuthorStatContainer;
import edu.psu.citeseerx.domain.ThinDoc;

import java.util.List;

public interface CitationStatisticsDAO {
    
    public List<ThinDoc> getMostCitedArticles(int amount, 
            boolean includeCitations) throws DataAccessException;

    public List<ThinDoc> getMostCitedArticlesByYear(int amount, int year,
            boolean includeCitations) throws DataAccessException;
    
    public List<AuthorStatContainer> getAuthorStats(long startingID, int amount)
    throws DataAccessException;

} //- interface CitationStatisticsDAO
