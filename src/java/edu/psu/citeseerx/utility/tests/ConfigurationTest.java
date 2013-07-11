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
package edu.psu.citeseerx.utility.tests;

import edu.psu.citeseerx.utility.*;
import org.junit.*;

public class ConfigurationTest {

    ConfigurationManager cm;
    AccessKey key;

    class AccessKey extends ConfigurationKey {}

    //@Before
    public void setUp() throws Exception {
        cm = new ConfigurationManager();
        key = new AccessKey();
    }

    String testProperty = "testProperty";
    String testValue1 = "testVal1";
    String testValue2 = "testVal2";

     public void addProperty() {
        try {
            cm.addProperty(testProperty, testValue1, key);
            String val = cm.getString(testProperty, key);
            Assert.assertTrue(val.equals(testValue1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     public void changeProperty() {
        try {
            cm.setProperty(testProperty, testValue2, key);
            String val = cm.getString(testProperty, key);
            Assert.assertTrue(val.equals(testValue2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


     public void clearProperty() {
        try {
            cm.clearProperty(testProperty, key);
            Assert.assertNull(cm.getString(testProperty, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
