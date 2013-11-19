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
package edu.psu.citeseerx.heritrix.jms;

import org.archive.crawler.Heritrix;
import org.archive.crawler.admin.CrawlJob;
import org.archive.crawler.admin.CrawlJobHandler;
import org.archive.crawler.event.CrawlStatusListener;
import org.archive.crawler.framework.CrawlController;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.servlet.http.HttpServlet;

import java.io.File;

import edu.psu.citeseerx.messaging.JMSSender;
import edu.psu.citeseerx.messaging.MsgService;
import edu.psu.citeseerx.messaging.messages.NewRecordToIngestMsg;
import edu.psu.citeseerx.messaging.messages.SubmissionJobCompleteMsg;
import edu.psu.citeseerx.messaging.messages.SubmissionStartedMsg;
import edu.psu.citeseerx.messaging.messages.UrlCrawledMsg;
import edu.psu.citeseerx.messaging.messages.UrlSubmissionMsg;
import edu.psu.citeseerx.utility.ConfigurationKey;
import edu.psu.citeseerx.utility.ConfigurationManager;

/**
 * <p>This is the core class for bridging Heritrix to other applications using
 * JMS message channels.  This class should be loaded as a servlet that
 * initializes on startup in the Heritrix web.xml.  Several configuration
 * parameters are required, using CSX ConfigurationManager, including:</p>
 * 
 * <ul>
 * <li>edu.psu.citeseerx.heritrix.jms.newSubmissionsChannel:
 * the name of the JMS channel over which new job submissions
 * will be received.</li>
 * <li>edu.psu.citeseerx.heritrix.jms.statusUpdateChannel:
 * name of the JMS channel over which crawl status updates will be sent.</li>
 * <li>edu.psu.citeseerx.heritrix.jms.ingestionChannel:
 * name of the JMS channel over which ingestion messages will be sent.</li>
 * <li>edu.psu.citeseerx.heritrix.jms.repositoryID:
 * a unique identifier for this crawler that will be used by 
 * ingestion services to locate files for ingestion.</li>
 * <li>edu.psu.citeseerx.heritrix.jms.submissionProfile:
 * the name of the job profile to use for submitted URLs,
 * e.g. CSX_User_Submission.</li>
 * <li>edu.psu.citeseerx.heritrix.jms.hubLinkIndicator:
 * a regular expression that will be used to check whether outlinks on
 * a page point to potentially interesting content,
 * e.g. <pre>.*(\.(pdf))(\.(g?z))?$</pre></li>
 * </ul>
 * 
 * <p>In addition, corresponding configuration is needed to set up messaging
 * channels using the edu.psu.citeseerx.messaging.MsgService class, as
 * indicated in the related documentation.</p>
 * 
 * <p>Messages received over the submission channel should be of type
 * UserSubmissionMsg, and UrlCrawledMsg and NewRecordToIngestMsg will be
 * sent over the output channels.</p>
 * 
 * <p>Although no Heritrix instance will be available at startup, this class
 * polls the environment until a Heritrix is created.  At that point, the
 * messaging channels will be started and new submissions are possible.</p> 
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class JMSInterface extends HttpServlet implements MessageListener {

    /**
     * 
     */
    private static final long serialVersionUID = -8644364734055592710L;

    private MsgService msgService;

    private String newSubmissionsChannel;
    private String statusUpdateChannel;
    private String ingestionChannel;
    
    private String repositoryID;

    private String submissionProfile;
    
    private final ConfigurationKey accessKey = new AccessKey();    
    
    private static JMSInterface jmsInterface;
    
    /**
     * Creates a new singleton JMSInterface only if there is not already
     * an existing singleton.
     */
    public JMSInterface() {
        if (jmsInterface == null) {
            jmsInterface = new JMSInterface("private");
        }
        
    }  //- JMSInterface
    
    
    /**
     * Sets up the JMS service and starts a thread to poll for a
     * Heritrix instance.
     * @param pLabel - just a junk parameter to allow for both public
     * and private constructors for a singleton instance.
     */
    private JMSInterface(String pLabel) {
        try {
            ConfigurationManager cm = new ConfigurationManager();
            newSubmissionsChannel =
                cm.getString("newSubmissionsChannel", accessKey);
            statusUpdateChannel =
                cm.getString("statusUpdateChannel", accessKey);
            ingestionChannel =
                cm.getString("ingestionChannel", accessKey);
            repositoryID =
                cm.getString("repositoryID", accessKey);
            submissionProfile =
                cm.getString("submissionProfile", accessKey);
            
            msgService = new MsgService();
            msgService.setMessageListener(newSubmissionsChannel, this);
            RegistrationThread rThread = new RegistrationThread();
            rThread.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }  //- private JMSInterface
    

    /**
     * @return - the JMSInterface singleton
     */
    public static JMSInterface getInstance() {
        if (jmsInterface == null) {
            jmsInterface = new JMSInterface("private");
        }
        return jmsInterface;
        
    }  //- getInstance
    
    
    private CrawlJobHandler handler;
    
    private JobSubmitter jobSubmitter;
    

    /**
     * Thread for polling for the existance of a Heritrix instance.  If a
     * new Heritrix instance is found, it is registered with the JMSInterface
     * for creation of new crawl jobs, and messaging services are started.
     * 
     * @author Isaac Councill
     *
     */
    class RegistrationThread extends Thread {
        public RegistrationThread() {
            this.setDaemon(true);
        }
        public void run() {
            while(true) {
                try {
                    CrawlJobHandler cjh = initHandler();
                    if ((cjh != null) && (cjh != handler)) {
                        handler = cjh;
                        jobSubmitter =
                            new JobSubmitter(handler, submissionProfile);
                        msgService.start();
                        System.out.println("JMSInterface: starting listener");
                    }
                    if (cjh == null) {
                        msgService.stop();
                        handler = null;
                    }
                } catch (Exception e) {}
                
                try {
                    sleep(5000);
                } catch (InterruptedException e) {}
            }
        }
        
    }  //- class RegistrationThread
    
    
    /**
     * Returns a CrawlJobHandler from the Heritrix instance, if a heritrix
     * instance can be found.
     * @return a CrawlJobHandler from the Heritrix instance, if a heritrix
     * instance can be found.
     * @throws Exception if no Heritrix is available.
     */
    public CrawlJobHandler initHandler() throws Exception {
    
        CrawlJobHandler handler;
        
        if (Heritrix.isSingleInstance()) {
            Heritrix heritrix = Heritrix.getSingleInstance();
            handler = heritrix.getJobHandler();
        } else {
            throw new Exception("No heritrix instance");
        }
        
        return handler;
        
    }  //- initHandler
    
    
    private boolean inProcess = false;
    
    /**
     * Called when a new submission message is received.  Unpacks the
     * job data and sends the information as a SubmissionData to a
     * JobSubmitter for bridging into the Heritrix submission system.
     */
    public synchronized void onMessage(Message msg) {
        if (msg instanceof MapMessage) {
            UrlSubmissionMsg subMsg = new UrlSubmissionMsg((MapMessage)msg);
            try {
                if (!subMsg.validate()) {
                    throw new JMSException("Invalid UrlSubmissionMsg");
                }
                SubmissionData data = new SubmissionData();
                data.setMetaName(subMsg.getJobID());
                data.setSeeds(subMsg.getUrl());
                data.setJobDescription(subMsg.getDescription());

                CrawlJob job = jobSubmitter.submit(data);
                JobFinishedListener listener = new JobFinishedListener(
                        data, msg, this);
                inProcess = true;
                handler.addJob(job);
                
                CrawlController controller = job.getController();
                // This sucks, but an alternative would require heavy hacking.
                while(controller == null) {
                    Thread.sleep(50);
                    controller = job.getController();
                }
                controller.addCrawlStatusListener(listener);

                sendJobStarted(subMsg.getJobID(), subMsg.getUrl());
                
                // Only take one message at a time.
                while(inProcess) {
                    try {
                        this.wait();
                        if (!inProcess) {
                            break;
                        }
                    } catch (InterruptedException e) { }
                }
                
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (Exception e) {
                //Submission went awry
                e.printStackTrace();
            }
        }
        
    }  //- onMessage
    
    
    /**
     * Listens for CRAWL_FINISHED messages and notifies JMS channels
     * of the status change.  Also acknowledges the successful handling
     * of the job submission.
     * 
     * @author Isaac Councill
     * @version $Rev$ $Date$
     */
    class JobFinishedListener implements CrawlStatusListener {

        private final SubmissionData data;
        private final Message message;
        private final JMSInterface jmsInterface;
        
        public JobFinishedListener(SubmissionData data, Message message,
                JMSInterface jmsInterface) {
            this.data = data;
            this.message = message;
            this.jmsInterface = jmsInterface;
        }
        
        public void crawlStarted(String msg) {}
        public void crawlEnding(String msg) {}
        public void crawlPausing(String msg) {}
        public void crawlPaused(String msg) {}
        public void crawlResuming(String msg) {}
        public void crawlCheckpoint(File checkpointDir) throws Exception {}
        
        public void crawlEnded(String msg) {
            if (msg.equals(CrawlJob.STATUS_FINISHED)) {
                try {
                    jmsInterface.sendJobComplete(data.getMetaName(),
                            data.getSeeds());
                    message.acknowledge();
                    jmsInterface.notifyJobComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        }
        
    }  //- class JobFinishedListener
    
    
    public synchronized void notifyJobComplete() {
        System.out.println("JOB COMPLETED!!!");
        inProcess = false;
        this.notifyAll();
    }  //- notifyJobComplete
    
    
    /**
     * Sends a JMS message over the statusUpdateChannel indicating that
     * the specified job has started.
     * @param jobID
     * @param url
     */
    public void sendJobStarted(String jobID, String url) {
        try {
            System.err.println("SENDING JOB STARTED!!!");
            JMSSender qsender = msgService.getJMSSender(statusUpdateChannel);
            SubmissionStartedMsg msg =
                new SubmissionStartedMsg(qsender, jobID, url, 0);
            msg.send();
            
        } catch (JMSException e) {
            e.printStackTrace();
        }
        
    }  //- sendJobStarted
    
    
    /**
     * Sends a JMS message over the statusUpdateChannel indicating that
     * the specified job has completed.
     * @param jobID
     * @param url
     */
    public void sendJobComplete(String jobID, String url) {
        try {
            JMSSender qsender = msgService.getJMSSender(statusUpdateChannel);
            SubmissionJobCompleteMsg msg =
                new SubmissionJobCompleteMsg(qsender, jobID, url, 0);
            msg.send();
            
        } catch (JMSException e) {
            e.printStackTrace();
        }
        
    }  //- sendJobComplete
    
    
    /**
     * Sends a JMS message over the statusUpdateChannel indicating the
     * status of a crawled resource.
     * @param jobID - the unique job name from the original submission message.
     * @param uri - the URI that was crawled.
     * @param status - status code.
     */
    public void sendStatusUpdate(String jobID, String uri, int status) {
        try {
            JMSSender qsender =
                msgService.getJMSSender(statusUpdateChannel);
            UrlCrawledMsg msg =
                new UrlCrawledMsg(qsender, jobID, uri, status);
            msg.send();

        } catch (JMSException e) {
            e.printStackTrace();
        }
        
    }  //- sendStatusUpdate
    
    
    /**
     * Sends a JMS message over the ingestionChannel indicating that new
     * resources are ready to ingest.
     * @param jobID - the unique job name from the original submission message.
     * @param filePath - relative path from repository root where the
     * file can be found.
     * @param metaPath - relative path from the repository root where the
     * metadata file can be found.
     * @param type - resource type (from CSXConstants).
     */
    public void notifyIngestion(String jobID, String filePath,
            String metaPath, String type) {
        try {
            JMSSender qsender =
                msgService.getJMSSender(ingestionChannel);
            NewRecordToIngestMsg msg = new NewRecordToIngestMsg(
                    qsender, jobID, repositoryID, filePath, metaPath, type);
            msg.send();

        } catch (JMSException e) {
            e.printStackTrace();
        }
        
    }  //- notifyIngestion
    
    
    /**
     * Servlet method for gracefully shutting down the JMS MsgService.
     */
    public void destroy() {
        if (msgService != null) {
            msgService.close();
        }
    }
    
}  //- class JMSInterface


class AccessKey extends ConfigurationKey {}
