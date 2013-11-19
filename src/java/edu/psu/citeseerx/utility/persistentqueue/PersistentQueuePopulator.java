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
package edu.psu.citeseerx.utility.persistentqueue;

import java.sql.*;
import javax.sql.DataSource;
import java.util.concurrent.BlockingQueue;

/**
 * Utility for populating a PersistentQueue from a DataSource backend.  This
 * is used to enter stored jobs into the queue for execution.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public abstract class PersistentQueuePopulator<E extends PersistentJob> {

    private String populateQuery;
    
    public String getPopulateQuery() {
        return populateQuery;
    } //- getPopulateQuery
    
    public void setPopulateQuery(String populateQuery) {
        this.populateQuery = populateQuery;
    } //- setPopulateQuery
    
    
    /**
     * Loads jobs into a BlockingQueue from storage.
     * @param queue
     * @param ds
     * @throws SQLException
     */
    public void populate(BlockingQueue<E> queue, DataSource ds)
    throws SQLException {
        Connection connection = ds.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(populateQuery);
        while(rs.next()) {
            queue.add(mapRow(rs));
        }
        stmt.close();
        rs.close();
        connection.close();
        
    }  //- populate
    
    
    /**
     * Subclasses should override this to map ResultSet rows into
     * PersistentJob implementations.
     * @param rs
     * @return A PersistentJob mapped from the given ResultSet
     * @throws SQLException
     */
    protected abstract E mapRow(ResultSet rs) throws SQLException;
    
}  //- class PersistentQueuePopulator
