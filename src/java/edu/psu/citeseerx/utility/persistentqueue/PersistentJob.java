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

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Interface for jobs that can be persisted within a PersistentQueue.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public interface PersistentJob extends Runnable {

    /**
     * Implementations should store all information necessary for recreating
     * the job using the supplied DataSource.
     * @param ds
     * @throws SQLException
     */
    public void submit(DataSource ds) throws SQLException;
    
    /**
     * Implementations should mark the job state as checked out using
     * the supplied DataSource.
     * @param ds
     * @throws SQLException
     */
    public void checkout(DataSource ds) throws SQLException;
    
    /**
     * Implementations should mark the job state as completed using the
     * supplied DataSource.
     * @param ds
     * @throws SQLException
     */
    public void complete(DataSource ds) throws SQLException;
    
}  //- interface PersistentJob
