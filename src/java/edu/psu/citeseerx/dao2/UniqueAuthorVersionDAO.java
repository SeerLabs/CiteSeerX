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
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.UniqueAuthor;
import java.io.IOException;
import java.util.List;

/**
 * Provides transparent access to canname's version persistence storage 
 * @author Puck Treeratpituk
 * @version $Rev: 1 $ $Date: 2011-09-23 11:35:02 -0400 (Fri, 23 Sep 2011) $
 */
public interface UniqueAuthorVersionDAO {
	public void updateUauthorInfo(String userid, String aid, String new_canname, String new_affil) throws DataAccessException;
	public void mergeUauthors(String userid, String aid1, String aid2) throws DataAccessException;
	public void removeUauthorPapers(String userid, String aid, List<Integer> papers) throws DataAccessException;
}
