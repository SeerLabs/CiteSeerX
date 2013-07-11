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
package edu.psu.citeseerx.dbcp;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import edu.psu.citeseerx.utility.ConfigurationKey;
import edu.psu.citeseerx.utility.ConfigurationManager;

public class DBCPFactory {

    public static DataSource createDataSource(String name)
    throws DataSourceInstantiationException {
        DataSource dataSource = null;

        String driverKey = (name != null) ? name+".driver" : "driver";
        String uriKey    = (name != null) ? name+".uri" : "uri";
        String userKey   = (name != null) ? name+".username" : "username";
        String passKey   = (name != null) ? name+".password" : "password";
        
        try {
            ConfigurationKey accessKey = new AccessKey();
            ConfigurationManager cm = new ConfigurationManager();
            String driver = cm.getString(driverKey, accessKey);
            String uri = cm.getString(uriKey, accessKey);
            String user = cm.getString(userKey, accessKey);
            String pass = cm.getString(passKey, accessKey);
            loadDriver(driver);
            String connectURI = buildURI(uri);
            dataSource = setupDataSource(connectURI, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
            DataSourceInstantiationException dsexc =
                new DataSourceInstantiationException(e.getMessage());
            throw(dsexc);
        }
        return dataSource;
        
    }  //- createDataSource
    
    
    protected static String buildURI(String uri) {
        StringBuffer buf = new StringBuffer();
        buf.append(uri);
        buf.append("?useUnicode=true&characterEncoding=UTF-8");
        buf.append("&autoReconnect=true");
        return buf.toString();
        
    }  //- buildURI
    
    
    protected static DataSource setupDataSource(String connectURI,
            String username, String password) {

        org.apache.commons.pool.ObjectPool connectionPool =
            new GenericObjectPool(null);
        KeyedObjectPoolFactory stmtPoolFactory = null;
//            new GenericKeyedObjectPoolFactory(null);
        ConnectionFactory connectionFactory =
            new DriverManagerConnectionFactory(connectURI, username, password);
        
        PoolableConnectionFactory poolableConnectionFactory =
            new PoolableConnectionFactory(connectionFactory, connectionPool,
                    stmtPoolFactory,
                    "select 1;",   // validation query
                    false,         // default read/write
                    false          // default no autocommit
            );
        PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

        return dataSource;
        
    }  //- configureDataSource
    
    
    protected static void loadDriver(String driver)
    throws ClassNotFoundException, InstantiationException,
    IllegalAccessException {
        System.out.println("driver: "+driver);
        Class.forName(driver).newInstance();
    }
    
}  //- class DBCPFactory


class AccessKey extends ConfigurationKey {}
