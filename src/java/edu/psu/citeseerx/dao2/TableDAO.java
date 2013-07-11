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

import java.util.List;
import org.springframework.dao.DataAccessException;
import edu.psu.citeseerx.domain.Table;

/**
 * Provides transparent access to PDF-Tables from persistent storage
 * 
 * @author Shuyi
 */
public interface TableDAO
{
    /**
     * Returns a list of tables associated to the given doi
     * 
     * @param id
     *            id of the table
     * @return A table object
     * @throws DataAccessException
     */
	
	public Table getTable(Long id) throws DataAccessException;

	/**
     * Returns a list of tables associated to the given doi
     * or proxy id
     * @param id
     *            id of the table
     * @param proxy
     * 			  is this id a proxy id ?
     * @return A table object
     * @throws DataAccessException
     */
	
	
    public List<Table> getTables(String id, boolean proxy) throws DataAccessException;
    
    /*
     * Deletes one table
     * @param id
     * 	The id of the table to be deleted
     * 
     */
    
    public void deleteTable(Long id) throws DataAccessException;
    
    /*
     * Inserts a Table into the database either by proxy or
     * doi id.
     * @param oneTable 
     * 		The table object to be inserted 
     * 
     */
    
    
    
    public void insertTable(Table oneTable) throws DataAccessException;
    
    /**
     * Returns total number of tables
     * 
     * @return table number
     * @throws DataAccessException
     */
    public Integer countTable() throws DataAccessException;
    
    /*
     * Select and Update The index Times;
     * @returns java.sql.Date 
     * @throws DataAccessException 
     */
    
    public java.sql.Date lastTableIndexTime() throws DataAccessException;
    
    /*
     * Update the last time the index was updated (now)
     * @throws DataAccessException
     */
    
    public void updateTableIndexTime() throws DataAccessException;
    
    /* 
     * Returns all tables updated since the given date
     * @param date from when 
     * @returns list of table elements
     * @throws DataAccessException
    */
    public List<Table> getUpdatedTables(java.sql.Date dt) throws DataAccessException;
    
}
