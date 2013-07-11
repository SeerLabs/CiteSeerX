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

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import edu.psu.citeseerx.messaging.MsgService;

public class ReceiverTest implements MessageListener {

    
    public ReceiverTest() {
        try {
            MsgService msgService = new MsgService();
            msgService.setMessageListener("JMSQUEUETEST",this);
            msgService.setMessageListener("JMSTOPICTEST",this);
            msgService.start();
            Thread.currentThread();
            Thread.sleep(20000);
            msgService.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void onMessage(Message msg) {
        if (msg instanceof MapMessage) {
            MapMessage m = (MapMessage)msg;
            try {
                System.out.println(m.getString("TestArg"));
                msg.acknowledge();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String args[]) {
        ReceiverTest test = new ReceiverTest();
    }
}
