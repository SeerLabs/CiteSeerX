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
package edu.psu.citeseerx.web.domain;

import java.io.Serializable;

/**
 * Author data carrier. Used in corrections
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class AuthorContainer implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2319548312741033337L;
    private String name;
    private String affil;
    private String address;
    private String email;
    private String order;
    
    public String getAddress() {
        return address;
    } //- getAddress
    public void setAddress(String address) {
        this.address = address;
    } //- setAddress
    public String getAffil() {
        return affil;
    } //- getAffil
    public void setAffil(String affil) {
        this.affil = affil;
    } //- setAffil
    public String getEmail() {
        return email;
    } //- getEmail
    public void setEmail(String email) {
        this.email = email;
    } //- setEmail
    public String getName() {
        return name;
    } //- getName
    public void setName(String name) {
        this.name = name;
    } //- setName
    public String getOrder() {
        return order;
    } //- getOrder
    public void setOrder(String order) {
        this.order = order;
    } //- setOrder
    
    private boolean deleted = false;
    
    public boolean getDeleted() {
        return deleted;
    } //- getDeleted
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    } //- setDeleted
    
} //- AuthorContainer
