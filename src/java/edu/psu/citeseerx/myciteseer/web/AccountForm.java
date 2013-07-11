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

import org.springframework.security.GrantedAuthorityImpl;

import edu.psu.citeseerx.myciteseer.domain.Account;


/**
 * Command object to manipulate/obtain user input to be used by
 * AccountFormController
 * @author Isaac Councill
 * @version $Rev$$ $$Date$
 */
public class AccountForm implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3749460133652474904L;
    private final Account account;
    private final boolean newAccount;
    private String repeatedPassword;
    private String captcha;
    
    public AccountForm(Account account) {
        this.account = account;
        this.newAccount = false;
    } //- AccountForm
    
    public AccountForm() {
        this.account = new Account();
        this.newAccount = true;
        /* Each new user will have this role to be used
         * by the authorization process when required 
         */
        this.account.addGrantedAuthority(
        		new GrantedAuthorityImpl("ROLE_AUTHENTICATED"));
    } //- AccountForm
    
    public Account getAccount() {
        return account;
    } //- getAccount
    
    public boolean isNewAccount() {
        return newAccount;
    } //- isNewAccount

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    } //- setRepeatedPassword
    
    public String getRepeatedPassword() {
        return repeatedPassword;
    } //- getRepeatedPassword

	public String getCaptcha() {
		return captcha;
	} //- getCaptcha

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	} //- setCaptcha
    
}  //- class AccountForm
