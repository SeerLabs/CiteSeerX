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
package edu.psu.citeseerx.dao2;

import org.springframework.dao.DataAccessException;
import java.util.List;
import edu.psu.citeseerx.domain.UniqueAuthor;
import edu.psu.citeseerx.domain.ThinDoc;

public interface UniqueAuthorDAO {

    public UniqueAuthor getAuthor(String aid) throws DataAccessException;
    public List<String> getAuthVarnames(String aid) throws DataAccessException;
    public List<ThinDoc> getAuthDocsOrdByCites(String aid) throws DataAccessException;
    public List<ThinDoc> getAuthDocsOrdByYear(String aid) throws DataAccessException;
	public List<Integer> getAuthorRecords(String aid) throws DataAccessException;
	public List<Integer> getAuthorRecordsByPapers(String aid, List<Integer> papers) throws DataAccessException;
	public void updateAuthNdocs(String aid) throws DataAccessException;
	public void updateAuthNcites(String aid) throws DataAccessException;
	public void updateAuthInfo(UniqueAuthor uauth) throws DataAccessException;
	public void removeAuthor(String aid) throws DataAccessException;
	public void moveAuthorRecords(String target_aid, List<Integer> author_records);
}