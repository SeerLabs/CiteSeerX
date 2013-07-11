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
package edu.psu.citeseerx.messaging.tests;

import edu.psu.citeseerx.messaging.*;
import javax.jms.*;

public class SenderTest {

    public static void main(String args[]) {
        try {
            MsgService msgService = new MsgService();

            JMSSender qsender = msgService.getJMSSender("JMSQUEUETEST");
            MapMessage msg = qsender.createMapMessage();
            msg.setString("TestArg", "QueueVal");
            qsender.postMessage(msg);

            JMSSender tsender = msgService.getJMSSender("JMSTOPICTEST");
            MapMessage msg2 = tsender.createMapMessage();
            msg2.setString("TestArg", "TopicVal");
            tsender.postMessage(msg2);
            
            msgService.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
