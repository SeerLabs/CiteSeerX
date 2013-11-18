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
package edu.psu.citeseerx.myciteseer.dao;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.context.ApplicationContextException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;

import javax.sql.DataSource;

/**
 * ConfigurationDao implementation using MYSQL as a persistent storage.
 * @author Isaac Councill
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ConfigurationDAOImpl extends JdbcDaoSupport 
implements ConfigurationDAO {

    public static final String NEW_ACCOUNTS_ENABLED = "newAccountsEnabled";
    public static final String URL_SUBMIT_ENABLED = "urlSubmissionsEnabled";
    public static final String CORRECT_ENABLED = "correctionsEnabled";
    public static final String GROUPS_ENABLED = "groupsEnabled";
    public static final String PEOPLE_SEARCH_ENABLED= "peopleSearchEnabled";
    public static final String PERSONAL_PORTAL_ENABLED = 
    	"personalPortalEnabled";
    
    private GetConfiguration getConfiguration;
    private UpdateConfiguration updateConfiguration;
    
    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() {
        getConfiguration = new GetConfiguration(getDataSource());
        updateConfiguration = new UpdateConfiguration(getDataSource());
    } //- initMappingSqlQueries
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.ConfigurationDAO#getConfiguration()
     */
    public MCSConfiguration getConfiguration() {

        MCSConfiguration config = new MCSConfiguration();
        List<ConfigurationParameter> params = getConfiguration.execute();
        for (Object o : params) {
            ConfigurationParameter param = (ConfigurationParameter)o;
            if (param.getName().equalsIgnoreCase(NEW_ACCOUNTS_ENABLED)) {
                boolean enabled = Boolean.parseBoolean(param.getValue());
                config.setNewAccountsEnabled(enabled);
            }
            if (param.getName().equalsIgnoreCase(URL_SUBMIT_ENABLED)) {
                boolean enabled = Boolean.parseBoolean(param.getValue());
                config.setUrlSubmissionsEnabled(enabled);
            }
            if (param.getName().equalsIgnoreCase(CORRECT_ENABLED)) {
                boolean enabled = Boolean.parseBoolean(param.getValue());
                config.setCorrectionsEnabled(enabled);
            }
            if (param.getName().equalsIgnoreCase(GROUPS_ENABLED)) {
                boolean enabled = Boolean.parseBoolean(param.getValue());
                config.setGroupsEnabled(enabled);
            }
            if (param.getName().equalsIgnoreCase(PEOPLE_SEARCH_ENABLED)) {
                boolean enabled = Boolean.parseBoolean(param.getValue());
                config.setPeopleSearchEnabled(enabled);
            }
            if (param.getName().equalsIgnoreCase(PERSONAL_PORTAL_ENABLED)) {
                boolean enabled = Boolean.parseBoolean(param.getValue());
                config.setPersonalPortalEnabled(enabled);
            }
        }
        return config;
        
    }  //- getConfiguration
    
        
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.ConfigurationDAO#saveConfiguration(edu.psu.citeseerx.myciteseer.domain.MCSConfiguration)
     */
    public void saveConfiguration(MCSConfiguration configuration) {
        
        updateConfiguration.run(NEW_ACCOUNTS_ENABLED,
                Boolean.toString(configuration.getNewAccountsEnabled()));
        updateConfiguration.run(URL_SUBMIT_ENABLED,
                Boolean.toString(configuration.getUrlSubmissionsEnabled()));
        updateConfiguration.run(CORRECT_ENABLED,
                Boolean.toString(configuration.getCorrectionsEnabled()));
        updateConfiguration.run(GROUPS_ENABLED,
                Boolean.toString(configuration.getGroupsEnabled()));
        updateConfiguration.run(PEOPLE_SEARCH_ENABLED,
                Boolean.toString(configuration.getPeopleSearchEnabled()));
        updateConfiguration.run(PERSONAL_PORTAL_ENABLED,
                Boolean.toString(configuration.getPersonalPortalEnabled()));
        
    }  //- saveConfiguration

    
    private static final String DEF_GET_CONFIGURATION_QUERY =
        "select param, value from configuration";

    protected class GetConfiguration extends MappingSqlQuery {
        
        public GetConfiguration(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CONFIGURATION_QUERY);
            compile();
        } //- GetConfiguration.GetConfiguration
        
        public ConfigurationParameter mapRow(ResultSet rs, int row)
        throws SQLException {
            ConfigurationParameter param = new ConfigurationParameter();
            param.setName(rs.getString(1));
            param.setValue(rs.getString(2));
            return param;
        } //- GetConfiguration.mapRow
        
    }  //- class GetConfiguration
    
    
    protected class ConfigurationParameter {
        private String name;
        private String value;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        
    }  //- class ConfigurationParameter
    
    
    private static final String DEF_UPDATE_CONFIGURATION_QUERY =
        "update configuration set value=? where param=?";

    protected class UpdateConfiguration extends SqlUpdate {
        
        public UpdateConfiguration(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_CONFIGURATION_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateConfiguration.UpdateConfiguration
        
        public int run(String param, String value) {
            return update(new Object[] { value, param });
        } //- UpdateConfiguration.run
        
    }  //- class UpdateConfiguration
    
}  //- class ConfigurationDAOImpl
