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
package edu.psu.citeseerx.utility;

import org.apache.commons.configuration.*;
import java.math.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

/**
 * Basic container for global configuration settings.  When using this
 * class, the CSX_HOME environment variable must be set, and configuration
 * is read and stored in $CSX_HOME/conf/service.xml unless otherwise specified.
 * 
 * To find out what kind of functionality this class provides, please read
 * the user documentation for the Commons Configuration project.
 * 
 * This class acts as a proxy for functionality in the Commons Configuration
 * XMLConfiguration class, with one major enhancement. A scoping mechanism
 * is provided so that clients can only read and write configuration specific
 * to their implementation.  This is achieved by the following means:
 * 
 * 1) By convention, all configuration specified in the underlying XML file
 * is positioned according to the full class name of the object or package
 * to be configured.  To make this clear, consider the example of configuring
 * an object of class edu.psu.citeseerx.corecom.ObjectServer and another
 * object of class edu.psu.citeseerx.utility.ConfigurationManager.
 * The XML would be:
 * 
 * <pre>
 * <rootElement>
 *   <edu>
 *     <psu>
 *       <citeseerx>
 *       
 *         <corecom>
 *           <ObjectServer>
 *             <conf1>someValue1</conf1>
 *             <conf2>someValue2</conf2>
 *             <conf3>someValue3</conf3>
 *           </ObjectServer>
 *         </corecom>
 *       
 *         <utility>
 *           <ConfigurationManager>
 *             <conf1>someValue1</conf1>
 *           </ConfigurationManager>
 *         </utility>
 *         
 *       </citeseerx>
 *     </psu>
 *   </edu>
 * </rootElement>
 * </pre>
 * 
 * The name of the root element does not matter, but it is required to have one.
 * When writing new configuration, it's a good idea to share as much of the 
 * XML structure as possible so that no duplicate paths exist.
 * 
 * 2) Objects that wish to read or write configuration must specify a key
 * that is a subclass of ConfigurationKey.  The package of the subclass 
 * indicates the configuration space that is available to the caller.  For
 * example, if the key edu.psu.citeseerx.corecom.ServerConfiguration$AccessKey
 * is specified, the caller only has access to the configuration space
 * below edu.psu.citeseerx.corecom.  The caller must then specify configuration
 * elements relative to the key path.  Thus, if the aforementioned key is
 * supplied when trying to access configuration for the value of
 * ObjectServer.conf1, then "ObjectServer.conf1" will be specified by the
 * caller, NOT the full path to the configuration element.
 * 
 * In order to enforce scoping, public configuration keys are not accepted.
 * This prevents developers from making a public subclass of ConfigurationKey
 * that allows global access to some or all of the configuration.  Of course, if
 * developers know where the configuration file is, the file can read and
 * written directly unless some other precautions are taken.
 * 
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 *
 */
public class ConfigurationManager {

    private String defaultConfigurationFile = "conf/service.xml";
    private String configurationLocation;
    private XMLConfiguration config;
    
    /**
     * Reads in the default configuration file.
     * @throws ConfigurationException
     */
    public ConfigurationManager() throws ConfigurationException {
        findConfiguration();
        readConfiguration();
        //configureSelf();
        
    }  //- ConfigurationManager
    
    
    /**
     * Reads in configuration from a specified file (in the default location).
     * @param fn configuration file name (should be a full path relative
     * to the CSX_HOME variable, e.g. "conf/service.xml".
     * @throws ConfigurationException
     */
    public ConfigurationManager(String fn) throws ConfigurationException {
        this.defaultConfigurationFile = fn;
        findConfiguration();
        readConfiguration();
        configureSelf();
        
    }  //- ConfigurationManager
    
    
    /**
     * Reads in any configuration values specific to the ConfigurationManager
     * utility.
     */
    private void configureSelf() {
        try {
            boolean autoSave = config.getBoolean(
                "edu.psu.citeseerx.utility.ConfigurationManager.autoSave");
            config.setAutoSave(autoSave);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("warning: ConfigurationManager autoSave "+
                    "property is not set.");
        }
        
    }  //- configureSelf
    
    
    /**
     * Determines whether to accept the specified key.  Currently, this
     * method makes sure that the key is not a public object.
     * @param key
     * @return true if the method returns.
     * @throws InvalidAccessKeyException if the key should not be accepted.
     */
    private boolean isValidKey(ConfigurationKey key)
            throws InvalidAccessKeyException {
        Class keyClass = key.getClass();
        int m = keyClass.getModifiers();
        if (Modifier.isPublic(m)) {
            throw new InvalidAccessKeyException("Key has innapropriate scope");
        }
        return true;
        
    }  //- isValidKey
    
    
    /**
     * Returns the keycode associated with the specified key.  The key code
     * is the full package path of the ConfigurationKey object up to
     * the last "."; e.g., the code for
     * edu.psu.citeseerx.corecom.ServerConfiguration$AccessKey is
     * "edu.psu.citeseer.corecom".
     * @param key
     * @return Key code associated with the specified key.
     */
    private String getKeyCode(ConfigurationKey key) {

        Class keyClass = key.getClass();
        String keyClassName = keyClass.getName();
        
        Pattern pattern = Pattern.compile("^(.*)\\.");
        Matcher matcher = pattern.matcher(keyClassName);
        matcher.find();
        
        // Match the inner group.
        String keyCode = matcher.group(1);
        return keyCode;
        
    }  //- getKeyCode  
    
    
    class InvalidAccessKeyException extends RuntimeException {

        private static final long serialVersionUID = -4657998602556279209L;

        public InvalidAccessKeyException(String msg) {
            super(msg);
        }
        
    }  //- class InvalidAccessKeyException
    
    
    private String buildConfigurationPath(String path, ConfigurationKey key)
            throws InvalidAccessKeyException {
        isValidKey(key);  // Throws exception if invalid.
        return getKeyCode(key) + "." + path;
    }
    
    
    /**
     * Sets up the absolute path to the configuration file.
     * @throws NullPointerException if CSX_HOME environement variable is not
     * set.
     */
    private void findConfiguration() throws NullPointerException {
        String csx_home = System.getProperty("CSX_HOME");
        if (csx_home == null) {
            throw new NullPointerException(
                    "CSX_HOME environment variable is undefined");
        }
        String confFile = System.getProperty("CSX_CONF");
        if (confFile == null) {
            confFile = defaultConfigurationFile;
        }
        configurationLocation = csx_home+"/"+confFile;
        
    }  //- findConfiguration
    
    
    private void readConfiguration() throws ConfigurationException {
        config = new XMLConfiguration(configurationLocation);
    } //- readConfiguration
    
    
    /**
     * Saves the current configuration to file.  This is only needed if
     * autoSave is turned off.
     * @throws IOException
     * @throws ConfigurationException
     */
    public synchronized void saveConfiguration()
            throws IOException, ConfigurationException {
        FileWriter writer = new FileWriter(configurationLocation);
        config.save(writer);
        writer.close();
        
    }  //- saveConfiguration
    
    
    /**
     * Gets a new Configuration object that contains all the configuration
     * that is accessible given the specified key.
     * @param key
     * @return configuration object for the specified key
     * @throws InvalidAccessKeyException
     */
    public Configuration getConfiguration(ConfigurationKey key)
            throws InvalidAccessKeyException {
        isValidKey(key);  // Throws exception if invalid;
        return config.subset(getKeyCode(key));
        
    }  //- getConfiguration
    
    
    /*
     * The remaining methods are keyed versions of targeted methods
     * in the XMLConfiguration implementation.
     */
    
    
    public synchronized void addProperty(String name, Object value,
            ConfigurationKey accessKey) throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        config.addProperty(path, value);
    } //- addProperty
    
    public synchronized void setProperty(String name, Object value,
            ConfigurationKey accessKey) throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        config.setProperty(path, value);
    } //- setProperty
    
    public synchronized void clearProperty(String name,
            ConfigurationKey accessKey) throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        config.clearProperty(path);
    } //- clearProperty
    
    public boolean containsKey (String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.containsKey(path);
    } //- containsKey
    
    public BigDecimal getBigDecimal(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getBigDecimal(path);
    } //- getBigDecimal
    
    public BigInteger getBigInteger(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getBigInteger(path);
    } //-getBigInteger
    
    public boolean getBoolean(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getBoolean(path);
    } //- getBoolean
    
    public byte getByte(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getByte(path);
    } //- getByte
    
    public double getDouble(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getDouble(path);
    } //- getDouble
    
    public float getFloat(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getFloat(path);
    } //-getFloat
    
    public int getInt(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getInt(path);
    } //- getInt
    
    public List getList(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getList(path);
    } //- getList
    
    public long getLong(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getLong(path);
    } //- getLong
    
    public Object getProperty(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getProperty(path);
    } //- getProperty
    
    public short getShort(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getShort(path);
    } //- getShort
       
    public String getString(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getString(path);
    } //- getString
    
    public String[] getStringArray(String name, ConfigurationKey accessKey)
            throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.getStringArray(path);
    } //- getStringArray
    
    public HierarchicalConfiguration configurationAt(String name,
            ConfigurationKey accessKey) throws Exception {
        String path = buildConfigurationPath(name, accessKey);
        return config.configurationAt(path);
    } //- configurationAt
    
}  //- class ConfigurationManager
