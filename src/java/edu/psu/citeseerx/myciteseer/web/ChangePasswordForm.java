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

import edu.psu.citeseerx.myciteseer.domain.Account;

/**
 * Command object to manipulate/obtain user input to be used by
 * ChangePasswordFormController
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class ChangePasswordForm implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3524901411123907817L;
    private final Account account;
    private String suppliedPassword;
    private String newPassword;
    private String repeatedPassword;
    
    public ChangePasswordForm(Account account) {
        this.account = account;
    } //- ChangePasswordForm
    
    public Account getAccount() {
        return account;
    } //- getAccount
    
    public void setSuppliedPassword(String suppliedPassword) {
        this.suppliedPassword = suppliedPassword;
    } //- setSuppliedPassword
    
    public String getSuppliedPassword() {
        return suppliedPassword;
    } //- getSuppliedPassword
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    } //- setNewPassword
    
    public String getNewPassword() {
        return newPassword;
    } //- getNewPassword
    
    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    } //- setRepeatedPassword
    
    public String getRepeatedPassword() {
        return repeatedPassword;
    } //- getRepeatedPassword
    
}  //- class ChangePasswordForm
