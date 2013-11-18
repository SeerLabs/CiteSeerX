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
package edu.psu.citeseerx.doi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Singleton class for thread-safe DOI service.  This class is intended to
 * maintain a database containing service configuration and records of the 
 * DOIs that the service has given out.  Each service using this class should
 * have a unique combination of SITE ID and DEPLOYMENT ID attributes set in
 * it's database (see external documentation) and multiple instances of
 * DOIHandler should never share databases.
 * <br><br>
 * The handle syntax is SITE_ID.DEP_ID.DOI_TYPE.BIN.REC, where all variables
 * are integers and the definitions are as follows:
 * <br><br>
 * SITE_ID: unique site identifier
 * DEP_ID: deployment identifier for multiple servers on a single site
 * DOI_TYPE: marker to determine the type of object that this DOI references
 * BIN: top-level ID space, each bin can contain only 9999 REC
 * REC: low-level ID space, record identifiers
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class DOIHandler {
    
    private DOIHandler() throws NamingException, SQLException {
        initializeConnection();
        collectPrefixInfo();
        
    }  //- DOIHandler


    private final String getMarkerInfoQuery =
        "select max(bin), max(rec) from doi_granted where doi_type=?";
    private final String insertDOIQuery =
        "insert into doi_granted values (NULL, ?, ?, ?, NULL)";
    private final String validationQuery =
        "select current_date";
    
    private Connection connection;
    
    private PreparedStatement getMarkerInfoStmnt;
    private PreparedStatement insertDOIStmnt;
    private PreparedStatement validationStmnt;
    
    /**
     * Gets a database connection from a JNDI resource that is expected
     * to have been configured in the app container.  Also prepares
     * statements using the connection.
     */
    private void initializeConnection() throws NamingException, SQLException {
        Context ctx = new InitialContext();
        
        DataSource source = (DataSource)ctx.lookup(
                "java:comp/env/jdbc/DOIDB");
        if (source == null) {
            throw new NamingException("Null DataSource");
        }
        connection = source.getConnection();
        getMarkerInfoStmnt = connection.prepareStatement(getMarkerInfoQuery);
        insertDOIStmnt = connection.prepareStatement(insertDOIQuery);
        validationStmnt = connection.prepareStatement(validationQuery);
        
    }  //- getConnection
    

    private final String getPrefixQuery =
        "select site_id, deployment_id from configuration";

    private int siteID;
    private int deploymentID;

    /**
     * Fetches the site and deployment IDs from the database.
     * @throws SQLException
     */
    private void collectPrefixInfo() throws SQLException {
        validateConnection();
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(getPrefixQuery);
        rs.first();
        int sid = rs.getInt(1);
        int did = rs.getInt(2);
        if ((sid <= 0) || (did <= 0)) {
            throw new SQLException("Configuration Error: site and deployment" +
                    " identifiers are not set or incorrectly set " +
                    "(must be greater than 0)");
        }
        siteID = sid;
        deploymentID = did;
        
    }  //- collectPrefixInfo

    
    private static DOIHandler instance;

    /**
     * @return the singleton instance of this server class.
     * @throws NamingException
     * @throws SQLException
     */
    public static DOIHandler getInstance() throws NamingException, SQLException {
        if (instance == null) {
            instance = new DOIHandler();
        }
        return instance;
        
    }  //- getInstance
    

    private Hashtable<Integer,DOIMarker> markerCache =
        new Hashtable<Integer,DOIMarker>();
    
    private static final String DELIM = ".";
    
    /**
     * Gets a new DOI for the specified DOI type and persists the DOI
     * to the database backend.
     * @param type
     * @return A new DOI from the DOI type
     * @throws SQLException
     */
    public synchronized String getDOI(int type) throws SQLException {
        validateConnection();
        Integer key = new Integer(type);
        DOIMarker marker;
        if (markerCache.containsKey(key)) {
            marker = markerCache.get(key);
        } else {
            marker = buildMarker(type);
            markerCache.put(key, marker);
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(siteID);
        buffer.append(DELIM);
        buffer.append(deploymentID);
        buffer.append(DELIM);
        buffer.append(type);
        buffer.append(DELIM);
        buffer.append(marker.bin);
        buffer.append(DELIM);
        buffer.append(marker.rec);
        
        insertDOI(type, marker.bin, marker.rec);
        marker.increment();
        
        return buffer.toString();
        
    }  //- getDOI
    
    
    /**
     * Executes a simple validation query to make sure the connection
     * is alive and well.  If the validation query fails, an attempt is
     * made to re-initialize the connection and all prepared statements.
     * A SQLException is thrown if that doesn't work - it's time to give up.
     */
    private void validateConnection() throws SQLException {
        try {
            if (validationStmnt.execute()) {
                return;
            }
        } catch (SQLException e) { /* move on */ }
        try {
            initializeConnection();
            return;
        } catch (NamingException e) {
            throw new SQLException ("No data source: " + e.getMessage());
        } catch (SQLException e) {
            throw e;
        }
        
    }  //- connectionValid
    
    
    /**
     * Calls the database to find the max values of BIN and REC for the
     * supplied DOI type in order to build a marker for the next DOI
     * that should be returned for this type.
     * @param doiType
     * @return a marker for the next DOI for the given type
     * @throws SQLException
     */
    private DOIMarker buildMarker(int doiType) throws SQLException {
        validateConnection();
        getMarkerInfoStmnt.setInt(1, doiType);
        ResultSet rs = getMarkerInfoStmnt.executeQuery();
        rs.first();
        int bin = rs.getInt(1);
        if (bin == 0) {
            bin = 1;
        }
        int rec = rs.getInt(2);  // If 0, will be incremented to 1.
        DOIMarker marker = new DOIMarker(bin, rec);
        marker.increment();
        return marker;
        
    }  //- buildMarker
    
    
    /**
     * Inserts a record into database to indicate that a specific
     * DOI has been assigned.
     * @param type the type of DOI
     * @param bin
     * @param rec
     * @throws SQLException
     */
    private void insertDOI(int type, int bin, int rec) throws SQLException {
        validateConnection();
        insertDOIStmnt.setInt(1, type);
        insertDOIStmnt.setLong(2, bin);
        insertDOIStmnt.setLong(3, rec);
        insertDOIStmnt.executeUpdate();
        
    }  //- insertDOI
    
    
    /**
     * Returns the String representation of the prefix this DOI Server
     * is using.  This is made up of two integers separated by a "." delimeter
     * that will begin each DOI that is created using this server. 
     */
    public String getPrefix() {
        return siteID+DELIM+deploymentID;
    } //- getPrefix


    /* Number of RECs in a BIN before incrementing
     * BIN and starting REC from 1. */
    private static final long BIN_SIZE = 9999;
    
    /**
     * Used to cache the value of the next DOI to be handed out,
     * so we don't have to poll the database for every new DOI. 
     */
    private class DOIMarker {
        public int bin;
        public int rec;
        public DOIMarker(int bin, int rec) {
            this.bin = bin;
            this.rec = rec;
        }
        public void increment() {
            if (rec >= BIN_SIZE) {
                bin++;
                rec = 1;
            } else {
                rec++;
            }
        }
        
    }  //- class DOIMarker
    
}  //- class DOIHandler
