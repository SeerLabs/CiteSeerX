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
package edu.psu.citeseerx.myciteseer.web.mail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;


/**
 * Used to send different types of messages to the registered user e-mail 
 * address. This class builds the message accord to the type and send it.  
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class MailManager {

    private MailSender mailSender;
    
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    } //- setMailSender

    
    private SimpleMailMessage accountActivationTemplate;

    public void setAccountActivationTemplate(SimpleMailMessage template) {
        this.accountActivationTemplate = template;
    } //- setAccountActivationTemplate
    
    
    private SimpleMailMessage invitationTemplate;
    
    public void setInvitationTemplate(SimpleMailMessage template) {
        this.invitationTemplate = template;
    } //- setInvitationTemplate
    
    
    private SimpleMailMessage feedbackTemplate;
    
    public void setFeedbackTemplate(SimpleMailMessage template) {
        this.feedbackTemplate = template;
    } //- setFeedbackTemplate
    
    private String feedbackBoilerplate = "";
    
    public void setFeedbackBoilerplate(String boilerplate) {
        this.feedbackBoilerplate = boilerplate;
    } //- setFeedbackBoilerplate
    
    
    private SimpleMailMessage forgottenAccountTemplate;
    
    public void setForgottenAccountTemplate(SimpleMailMessage template) {
        this.forgottenAccountTemplate = template;
    } //- setForgottenAccountTemplate
    
    private String forgottenAccountBoilerplate = "";
    
    public void setForgottenAccountBoilerplate(String boilerplate) {
        this.forgottenAccountBoilerplate = boilerplate;
    } //- setForgottenAccountBoilerplate
    
    
    private SimpleMailMessage monitorNotificationTemplate;
    
    public void setMonitorNotificationTemplate(SimpleMailMessage template) {
        this.monitorNotificationTemplate = template;
    } //- setMonitorNotificationTemplate
    
    private String monitorNotificationBoilerplate = "";
    
    public void setMonitorNotificationBoilerplate(String boilerplate) {
        this.monitorNotificationBoilerplate = boilerplate;
    } //- setMonitorNotificationBoilerplate


    private String urlMarker = "=URL=";
    
    public void setUrlMarker(String urlMarker) {
        this.urlMarker = urlMarker;
    } //- setUrlMarker
    
    
    private String feedbackUrl = "";
    
    public void setFeedbackUrl(String feedbackUrl) {
        this.feedbackUrl = feedbackUrl;
    } //- setFeedbackUrl
        
    
    private String feedbackSubjectPrefix = "[CSX Feedback]";
    
    public void setFeedbackSubjectPrefix(String prefix) {
        feedbackSubjectPrefix = prefix;
    } //- setFeedbackSubjectPrefix
    
    
    private static final String NEW_LINE = System.getProperty("line.separator");

    
    public void sendFeedbackMessage(String fromName, String fromAddr,
            String subject, String message)
    throws MailException {
    
        subject = feedbackSubjectPrefix+" "+subject;
        String fromComplete = fromName + " <"+fromAddr+">";
        
        SimpleMailMessage msg = new SimpleMailMessage(this.feedbackTemplate);
        msg.setFrom(fromComplete);
        msg.setSubject(subject);
        msg.setText(message);
        
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        builder.append(NEW_LINE);
        builder.append(NEW_LINE);  
        builder.append("============");
        builder.append(NEW_LINE);  
        builder.append(feedbackBoilerplate.replace(urlMarker, feedbackUrl));
        
        SimpleMailMessage copy = new SimpleMailMessage();
        copy.setFrom(fromComplete);
        copy.setTo(fromAddr);
        copy.setSubject(subject);
        copy.setText(builder.toString());
        
        this.mailSender.send(msg);
        this.mailSender.send(copy);

    }  //- sendFeedbackMessage
    
    
    public void sendForgottenAccountMessage(String toAddr, String username,
            String newPass) {
        
        SimpleMailMessage msg =
            new SimpleMailMessage(this.forgottenAccountTemplate);
        msg.setTo(toAddr);
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("Your CiteSeerX password has been reset.");
        builder.append(NEW_LINE);
        builder.append(NEW_LINE);
        builder.append("Your user name is: " + username);
        builder.append(NEW_LINE);
        builder.append("Your new password is: " + newPass);
        builder.append(NEW_LINE);
        builder.append(NEW_LINE);
        builder.append("============");
        builder.append(NEW_LINE);  
        builder.append(forgottenAccountBoilerplate.replace(
                urlMarker, feedbackUrl));
        
        msg.setText(builder.toString());
        this.mailSender.send(msg);
        
    }  //- sendForgottenAccountMessage
    
    
    public void sendAccountActivationMessage(String address, String url) {
        
        SimpleMailMessage msg =
            new SimpleMailMessage(this.accountActivationTemplate);
        msg.setTo(address);
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("A CiteSeerX account has been automatically ");
        builder.append("generated for you.  To activate this account ");
        builder.append("please visit the following URL:");
        builder.append(NEW_LINE);
        builder.append(NEW_LINE);
        builder.append(url);

        msg.setText(builder.toString());
        
        try {
            this.mailSender.send(msg);
        } catch (MailException e) {
            e.printStackTrace();
        }
        
    }  //- sendAccountActivationMessage
    
    
    public void sendInvitationMessage(String address, List<String> ccAddrs,
            String customMessage, String url) throws MailException {
        
        SimpleMailMessage msg =
            new SimpleMailMessage(this.invitationTemplate);
        msg.setTo(address);
        if (ccAddrs.size() > 0) {
            String[] cc = new String[ccAddrs.size()];
            for (int i=0; i<ccAddrs.size(); i++) {
                cc[i] = ccAddrs.get(i);
            }
            msg.setCc(cc);
        }
        
        StringBuilder builder = new StringBuilder();
        if (customMessage != null && customMessage.length() > 0) {
            builder.append(customMessage);
            builder.append(NEW_LINE);
            builder.append(NEW_LINE);
        }
        builder.append("A one-time ticket has been issued for you ");
        builder.append("to join the CiteSeerX user community.  Please ");
        builder.append("activate your account ");
        builder.append("through the following URL:");
        builder.append(NEW_LINE);
        builder.append(NEW_LINE);
        builder.append(url);
        builder.append(NEW_LINE);
        builder.append(NEW_LINE);
        builder.append("Best regards,");
        builder.append(NEW_LINE);
        builder.append("The CiteSeerX Team");
        
        msg.setText(builder.toString());
        
        this.mailSender.send(msg);
        
    }  //- sendInvitationMessage
    
    
    public void sendMonitorNotificationMessage(String email, String message)
    throws MailException {
        
        SimpleMailMessage msg =
            new SimpleMailMessage(this.monitorNotificationTemplate);
        msg.setTo(email);
        
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        builder.append(NEW_LINE);
        builder.append(NEW_LINE);
        builder.append("============");
        builder.append(NEW_LINE);  
        builder.append(monitorNotificationBoilerplate.replace(
                urlMarker, feedbackUrl));
        
        msg.setText(builder.toString());
        
        this.mailSender.send(msg);
        
    }  //- sendMonitorNotificationMessage
    
    
    public static boolean isValidEmailAddress(String address) {
        if (address == null) {
            return false;
        }
        boolean valid = true;
        try {
            new InternetAddress(address);
            if (! hasNameAndDomain(address)) {
                valid = false;
            }
        } catch (AddressException e) {
            valid = false;
        }
        return valid;
        
    }  //- isValidEmailAddress
    
    
    private static boolean hasNameAndDomain(String address) {
        String[] tokens = address.split("@");
        return
            tokens.length == 2 &&
            tokens[0].length()>0 &&
            tokens[1].length() > 0;
    }

}  //- class MailManager
