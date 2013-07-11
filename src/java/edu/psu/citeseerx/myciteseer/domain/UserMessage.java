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
package edu.psu.citeseerx.myciteseer.domain;

import java.io.Serializable;

public class UserMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4334176428027160311L;
    
    private String messageFrom = null;
    private String messageTo = null;
    private String messageBody = null;
    private String messageTime = null;
    private boolean messageViewed = false;

    public void setMessageFrom(String s) {
        messageFrom = s;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageTo(String s) {
        messageTo = s;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageBody(String s) {
        messageBody = s;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageTime(String s) {
        messageTime = s;
    }

    public String getMessageTime() {
        return messageTime;
    }


    public void setMessageViewed(boolean b) {
        messageViewed = b;
    }

    public boolean getMessageViewed() {
        return messageViewed;
    }


}
