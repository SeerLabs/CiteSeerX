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
package edu.psu.citeseerx.myciteseer.web;

import java.io.Serializable;

import org.springframework.beans.support.PagedListHolder;

import edu.psu.citeseerx.myciteseer.domain.Account;

public class UserSession implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3786087802578085402L;

    private Account account;
    
    private PagedListHolder myList;
    
    public UserSession(Account account) {
        this.account = account;
    }
    
    public Account getAccount() {
        return account;
    }
    
    public void setMyList(PagedListHolder myList) {
        this.myList = myList;
    }
    
    public PagedListHolder getMyList() {
        return myList;
    }
    
}
