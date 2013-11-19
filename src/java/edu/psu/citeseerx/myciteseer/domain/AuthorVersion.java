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

/**
 * AuthorVersion data carrier.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class AuthorVersion implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8527179264473837236L;
    
    private String name;
    private String affil;
    private String addr;
    private String nameSrc;
    private String affilSrc;
    private String addrSrc;
    
    public String getAddr() {
        return addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getAddrSrc() {
        return addrSrc;
    }
    public void setAddrSrc(String addrSrc) {
        this.addrSrc = addrSrc;
    }
    public String getAffil() {
        return affil;
    }
    public void setAffil(String affil) {
        this.affil = affil;
    }
    public String getAffilSrc() {
        return affilSrc;
    }
    public void setAffilSrc(String affilSrc) {
        this.affilSrc = affilSrc;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNameSrc() {
        return nameSrc;
    }
    public void setNameSrc(String nameSrc) {
        this.nameSrc = nameSrc;
    }
    
} //- class AuthorVersion
