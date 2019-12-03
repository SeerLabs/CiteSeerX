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
package edu.psu.citeseerx.ingestion;

import edu.psu.citeseerx.dbcp.*;
import edu.psu.citeseerx.ingestion.ws.BpelClient;
import edu.psu.citeseerx.messaging.*;
import edu.psu.citeseerx.messaging.messages.*;
import edu.psu.citeseerx.repository.RepositoryMap;
import edu.psu.citeseerx.repository.UnknownRepositoryException;
import edu.psu.citeseerx.dao2.logic.*;
import edu.psu.citeseerx.dao2.*;

import javax.jms.*;
import javax.sql.DataSource;

import java.util.concurrent.*;


/**
 * Service implementation for managing content acquisition.  Crawlers will
 * download content from the web and message the IngestionManager using JMS
 * when new content is ready for ingestion.  The IngestionManager imports the
 * file data from the crawlers and posts the new records to an ActiveBPEL
 * service orchestrator.  The BPEL engine will directly manage the ingestion
 * pipeline and send the results back to the IngestionManager, which is then
 * responsible for passing the results into the core system for final
 * incorporation into the CiteSeerX repository.
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 * 
 */
public class IngestionManager {

    private final MsgService msgService;
    private String newDocChannel;
    private String statusUpdateChannel;
    private String contentUpdateChannel;
    
    private final MessageConsumer newDocConsumer;
    private final JMSSender statusUpdateSender;
    private final JMSSender contentUpdateSender;
    
    private final ThreadPoolExecutor ingestThreadPool;
    private final LinkedBlockingQueue<Runnable> ingestWorkQueue =
        new LinkedBlockingQueue<Runnable>();
    
    private BpelClient bpelClient;
    private DataSource dataSource;
    private CSXDAO csxdao;
    
    private int ingestPoolSize = 5;
    private String repositoryID;
    private String tmpDir;
    
    public String getTmpDir() {
        return tmpDir;
    }
    
    private final RepositoryMap repositoryMap;
    
    /**
     * Reads configuration in order to set up appropriate thread pools
     * for job handling and appropriate JMS message channels for comm.
     * Outstanding jobs are read from a persistent backend to provide
     * clean recovery from service failures.
     * @throws Exception
     */
    public IngestionManager() throws Exception {

        repositoryMap = new RepositoryMap();
        bpelClient = new BpelClient();
        dataSource = DBCPFactory.createDataSource("core");
    //    csxdao = new CSXDAO();
    //    csxdao.setDataSource(dataSource);
        
        
        // Set up thread pool for ingesting imported records.
        ingestThreadPool =
            new ThreadPoolExecutor(ingestPoolSize,        // core threads
                                   ingestPoolSize,        // max threads
                                   Long.MAX_VALUE,        // keepalive time
                                   TimeUnit.NANOSECONDS,  // time unit
                                   ingestWorkQueue);      // job queue
        
        msgService = new MsgService();
        newDocConsumer = msgService.getMessageConsumer(newDocChannel);
        statusUpdateSender = msgService.getJMSSender(statusUpdateChannel);
        contentUpdateSender = msgService.getJMSSender(contentUpdateChannel);
        
    }  //- IngestionManager
    
        
    /* Used for stopping the thread that receives incoming messages. */
    private boolean stopped = false;
    
 
    /**
     * Thread for synchronously polling the JMS queue for new content
     * notifications.  If too many jobs are already in the work queue
     * (more than maxQueued), this thread will wait for the work queue
     * to clear before accepting any new messages.
     * 
     * @author Isaac Councill
     *
     */
    class ReceiverThread extends Thread {
        
        private final IngestionManager manager;
        
        public ReceiverThread(IngestionManager manager) {
            super("MsgReceiver");
            this.manager = manager;
        }
        
        /**
         * Accepts new messages from crawlers and submits
         * jobs to the ingestThreadPool when received.
         */
        public void run() {
            while (!stopped) {
                while (ingestThreadPool.getActiveCount() >=
                    ingestThreadPool.getCorePoolSize()) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) { /*ignore*/ }
                }
                try {
                    Message msg = newDocConsumer.receive();
                    if (msg instanceof MapMessage) {

                        MapMessage mmsg = (MapMessage)msg;
                        NewRecordToIngestMsg newReqMsg =
                            new NewRecordToIngestMsg(mmsg);
                        IngestWorker worker =
                            new IngestWorker(newReqMsg, bpelClient,
                                    csxdao, manager);
                        ingestThreadPool.submit(worker);
                        
                    } else {
                        System.err.println("BOGUS MESSAGE: "+msg.toString());
                        msg.acknowledge();
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            
        }  //- ReceiverThread.run
        
    }  //- class ReceiverThread
    
    /**
     * Send a message to notify the user about the status of the given JobID
     * @param jobID
     * @param statusCode
     * @param url
     * @param doi
     */
    public void notifyUser(String jobID, int statusCode,
            String url, String doi) {
        
        try {
            IngestStatusMsg notification =
                new IngestStatusMsg(statusUpdateSender);
            notification.setJobID(jobID);
            notification.setStatus(statusCode);
            notification.setUrl(url);
            notification.setDOI(doi);
            notification.send();
            
        } catch (JMSException e){
            e.printStackTrace();
        }
        
    }  //- notifyUser
    
    
    /**
     * Called by IngestWorkers when their task is done.  This method reads
     * status information from the workers and passes the results on
     * to the core system (if successful) and creates a notification
     * if this job is being tracked.
     * @param worker
     */
    public void notifyIngestComplete(IngestWorker worker) {
        /*
        
        if (worker.getStatus() == 1) {
            // TODO: status must be OK to reach this block.  If it is,
            // message the results to the core system.
        }
        if (worker.getNotificationJobID() != null) {
            try {
                //MapMessage msg = subNotificationSender.createMapMessage();
                //subNotificationSender.postMessage(msg);
                
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
            */
        
    }  //- notifyIngestComplete.
        
    
    /**
     * Starts the message channels so that new document notifications will
     * be received, and starts a new thread for receiving messages.
     * @throws JMSException
     */
    public void start() throws JMSException {
        stopped = false;
        msgService.start();
        ReceiverThread rcv = new ReceiverThread(this);
        rcv.start();
    }
    
    /**
     * Stops the ingestion service from receiving new document notifications.
     * Execution may be resumed by calling the start() method.
     * @throws JMSException
     */
    public void stop() throws JMSException {
        stopped = true;
        msgService.stop();
    }
    
    /**
     * Shuts down all resources gracefully.
     */
    public void shutdown() {
        stopped = true;
        msgService.close();
        ingestThreadPool.shutdown();
        System.exit(0);
    }
    
    
    public String getCrawlerRepositoryPath(String repID)
    throws UnknownRepositoryException {
        return repositoryMap.getRepositoryPath(repID);
    } //- getCrawlerRepositoryPath
    
    
    public String getRepositoryID() {
        return repositoryID;
    } //- getRepositoryID
    
    
    public String getRepositoryPath() throws UnknownRepositoryException {
        return repositoryMap.getRepositoryPath(repositoryID);
    } //- v

    public void test(NewRecordToIngestMsg msg) {
        IngestWorker worker =
            new IngestWorker(msg, bpelClient,
                    csxdao, this);
        ingestThreadPool.submit(worker);
    } //- test
    
    /**
     * Creates a new IngestionManager and fires it up.
     * @param args
     */
    public static void main(String args[]) {
        try {
            IngestionManager manager = new IngestionManager();
            manager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }  //- main

}  //- class IngestionManager


