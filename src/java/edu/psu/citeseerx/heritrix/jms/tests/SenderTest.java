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
package edu.psu.citeseerx.heritrix.jms.tests;

import edu.psu.citeseerx.messaging.*;
import edu.psu.citeseerx.messaging.messages.*;
import edu.psu.citeseerx.utility.CSXConstants;
import javax.jms.*;

public class SenderTest {

    public static void main(String args[]) {
        try {
            MsgService msgService = new MsgService();
            
            StatusGetter sGetter = new StatusGetter();
            msgService.setMessageListener("csx.ingestion.statusUpdates", sGetter);
            
            IngestGetter iGetter = new IngestGetter();
            msgService.setMessageListener("csx.ingestion.documentsToIngest", iGetter);
            
            msgService.start();

            JMSSender qsender = msgService.getJMSSender("csx.ingestion.newSubmissions");
            
            String jobName = CSXConstants.USER_SUBMISSION_PREFIX+"foo11";
            String url = "http://www.personal.psu.edu/igc2/pubs.html"
                +"\nhttp://clgiles.ist.psu.edu/papers/ISI2007-LDA-SNA.pdf"
                +"\nhttp://clgiles.ist.psu.edu/papers/ICDM2007-corank-hetero-networks_long.pdf";
            String desc = "Testing various conditions";
            
            UrlSubmissionMsg msg = new UrlSubmissionMsg(qsender, jobName,
                    url, desc);
            msg.send();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class StatusGetter implements MessageListener {
    
    public synchronized void onMessage(Message msg) {
        if (msg instanceof MapMessage) {
            try {
                UrlCrawledMsg imsg = new UrlCrawledMsg((MapMessage)msg);
                System.out.println(imsg.toString());
                imsg.acknowledge();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
/*    
    public synchronized void onMessage(Message msg) {
        System.out.println("GOT STATUS MESSAGE");
        try {
            msg.acknowledge();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    */
}

class IngestGetter implements MessageListener {
    
    public synchronized void onMessage(Message msg) {
        if (msg instanceof MapMessage) {
            try {
                NewRecordToIngestMsg nmsg =
                    new NewRecordToIngestMsg((MapMessage)msg);
                System.out.println(nmsg.toString());
                nmsg.acknowledge();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    public synchronized void onMessage(Message msg) {
        System.out.println("GOT INGEST MESSAGE");
        try {
            msg.acknowledge();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    */
}