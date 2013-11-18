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
package edu.psu.citeseerx.myciteseer.messaging;

import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.MapMessage;

import edu.psu.citeseerx.messaging.messages.*;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.utility.UrlStatusMappings;
import edu.psu.citeseerx.myciteseer.domain.*;


/**
 * Service for receiving messages relevant to MyCiteSeer, particularly
 * status updates for URL submissions.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class NotificationReceiver implements MessageListener {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    
    /**
     * Called to handle incoming messages from JMS.  This method distinguishes
     * the message type and passes execution to handlers specific to each
     * message type.
     */
    public synchronized void onMessage(Message msg) {
        System.err.println("GOT NEW MESSAGE: ");
        MapMessage mmsg;
        if (msg instanceof MapMessage) {
            mmsg = (MapMessage)msg;
            System.err.println(mmsg.toString());
        } else {
            System.err.println("Received message that is not an instance of" +
                    " MapMessage.  Ignoring.");
            return;
        }
        try {
            SubmissionNotificationItem item = new SubmissionNotificationItem();
            int type = mmsg.getInt(SubmissionNotification.typeLabel);
            switch(type) {

            case SubmissionNotification.URLCRAWLSTATUS:
                System.err.println("GOT CRAWL MESSAGE");
                try {
                    UrlCrawledMsg ucm = new UrlCrawledMsg(mmsg);
                    System.err.println("1");
                    item.setJobID(ucm.getJobID());
                    item.setURL(ucm.getUrl());
                    item.setStatus(ucm.getStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.err.println("HERE");
                myciteseer.insertSubmissionComponent(item);
                break;
                
            case SubmissionNotification.JOBSTARTED:
                System.err.println("GOT JOBSTART MESSAGE");
                try {
                    SubmissionStartedMsg ssm = new SubmissionStartedMsg(mmsg);
                    String jobID = ssm.getJobID();
                    myciteseer.updateJobStatus(jobID,
                            UrlStatusMappings.CSX_CRAWL_STARTED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
                
            case SubmissionNotification.JOBCOMPLETION:
                System.err.println("GOT JOBCOMPLETE MESSAGE");
                try {
                    SubmissionJobCompleteMsg sjcm =
                        new SubmissionJobCompleteMsg(mmsg);
                    String jobID = sjcm.getJobID();
                    myciteseer.updateJobStatus(jobID,
                            UrlStatusMappings.CSX_CRAWL_COMPLETE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case SubmissionNotification.INGESTSTATUS:
                System.err.println("GOT INGEST MESSAGE");
                IngestStatusMsg ism = new IngestStatusMsg(mmsg);
                item.setDID(ism.getDOI());
                item.setJobID(ism.getJobID());
                item.setURL(ism.getUrl());
                item.setStatus(ism.getStatus());
                myciteseer.insertSubmissionComponent(item);
                break;
                
            default:
                System.err.println("Received message of unkown type. "+
                        "Ignoring.");
            }
            System.err.println("RECEIVED MESSAGE");
            msg.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }  //- onMessage
    
    
}  //- class NotificationReceiver
