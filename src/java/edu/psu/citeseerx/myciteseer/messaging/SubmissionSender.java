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

import org.springframework.jms.core.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.jms.*;

import edu.psu.citeseerx.myciteseer.domain.*;

/**
 * Service for sending messages relevant to other system services. Specially to
 * the crawler to notify it about new URLs to be crawled.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class SubmissionSender {

    private static final Log log = LogFactory.getLog(SubmissionSender.class);
    
    private JmsTemplate template;
    private Destination destination;
    
    /**
     * Called to send messages to a JMS queue regarding new URLs to be crawled.
     * @param submission
     * @throws JMSException
     */
    public void sendMessage(UrlSubmission submission) throws JMSException {
        UrlMessageCreator creator =
            new UrlMessageCreator(submission.getJobID(), submission.getUrl(),
                    "user submission"); 
        template.send(destination, creator);
        log.info("sent submission message to crawler");
    } //- sendMessage
    
    public Destination getDestination() {
        return destination;
    } //- getDestination
    public void setDestination(Destination destination) {
        this.destination = destination;
    } //- setDestination
    public JmsTemplate getTemplate() {
        return template;
    } //- getTemplate
    public void setTemplate(JmsTemplate template) {
        this.template = template;
    } //- setTemplate
    
}  //- class SubmissionSender


class UrlMessageCreator implements MessageCreator {
    
    final String JID;
    final String URL;
    final String DESC;
    
    public UrlMessageCreator(String JID, String URL, String DESC) {
        this.JID = JID;
        this.URL = URL;
        this.DESC = DESC;
    }
    
    public Message createMessage(Session session) throws JMSException {
        MapMessage msg = session.createMapMessage();
        msg.setString("JID", JID);
        msg.setString("URL", URL);
        msg.setString("DESC", DESC);
        return msg;
    }
    
}  //- class UrlMessageCreator
