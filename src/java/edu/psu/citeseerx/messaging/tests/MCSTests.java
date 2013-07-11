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

import edu.psu.citeseerx.messaging.JMSSender;
import edu.psu.citeseerx.messaging.MsgService;
import edu.psu.citeseerx.messaging.messages.UrlCrawledMsg;

public class MCSTests {

    public static void main(String args[]) {
        try {
            MsgService msgService = new MsgService();
            JMSSender tsender =
                msgService.getJMSSender("csx.submission.notifications");

            String jobID = "testjob1";
            int N = 10;
            int counter = 1;
            while (counter <= N) {
                String url = "http://testurl.com/paper"+(5000+counter)+".pdf";
                int status = 0;
                
                UrlCrawledMsg ucm = new UrlCrawledMsg(tsender, jobID,
                        url, status);
                ucm.send();
                counter++;
                Thread.currentThread();
                Thread.sleep(1000);
            }
            /*
            for (int i=1; i<=N; i++) {
                String url = "http://testurl.com/paper"+i+".pdf";
                int status = 0;
                
                UrlCrawledMsg ucm = new UrlCrawledMsg(tsender, jobID, url, status);
                ucm.send();
                
            }
            Thread.currentThread().sleep(10000);
            for (int i=1; i<=N; i++) {
                String url = "http://testurl.com/paper"+i+".pdf";
                int status = 1;
                IngestStatusMsg ism = new IngestStatusMsg(tsender, jobID, 
                        url, status);
                ism.send();                
            }
            
            String url = "http://testurl.com";
            int status = 1;
            SubmissionJobCompleteMsg sjcm = new SubmissionJobCompleteMsg(
                    tsender, jobID, url, status);
            sjcm.send();
        */
            msgService.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
