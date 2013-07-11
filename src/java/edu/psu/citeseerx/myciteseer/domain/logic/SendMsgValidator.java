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
package edu.psu.citeseerx.myciteseer.domain.logic;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import edu.psu.citeseerx.myciteseer.domain.*;

public class SendMsgValidator implements Validator {

    public boolean supports(Class clazz) {
        return UserMessage.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        UserMessage msg = (UserMessage) obj;

        if (msg == null) {
            errors.rejectValue("", "error.not-specified",
                    null, "Message body required.");
        }
        else {
            if (msg.getMessageTo() == null) {
                errors.rejectValue("messageTo",  "error.not-specified",
                        null, "Username required.");
            }
        }

    }

}