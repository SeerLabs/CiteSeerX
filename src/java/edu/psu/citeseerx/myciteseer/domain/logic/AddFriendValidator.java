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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Friend;


public class AddFriendValidator implements Validator {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }
    
    public boolean supports(Class clazz) {
        return Friend.class.isAssignableFrom(clazz);
    }
    
    public void validate(Object obj, Errors errors) {
        String friendId = ((Friend)obj).getId();
        Account account = myciteseer.getAccount(friendId);
        if (account == null) {
            errors.rejectValue("friendId", "Username does not exist");
        }
    }
    
}
