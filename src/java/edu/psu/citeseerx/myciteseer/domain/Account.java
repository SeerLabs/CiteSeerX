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

import java.util.ArrayList;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.GrantedAuthorityImpl;

/**
 * User Account data carrier.
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class Account implements Serializable, UserDetails {

    /**
     * 
     */
    private static final long serialVersionUID = 5338982532448066159L;
    
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String status;
    private String affiliation1;
    private String affiliation2;
    private String country;
    private String province;
    private String webPage;
    private boolean enabled = false;
    private Long internalId;
    private java.util.Date updated;
    private String appid;

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
    } //- setUsername

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    } //- setPassword

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
    } //- setEmail

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    } //- setFirstName

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    } //- getFirstName

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    } //- setLastName

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
    } //- setStatus
    
    public String getAffiliation1() { return affiliation1; }
    public void setAffiliation1(String affiliation1) {
        this.affiliation1 = affiliation1;
    } //- setAffiliation1

    public String getAffiliation2() { return affiliation2; }
    public void setAffiliation2(String affiliation2) {
        this.affiliation2 = affiliation2;
    } //- setAffiliation2
    
    public String getCountry() { return country; }
    public void setCountry(String country) {
        this.country = country;
    } //- setCountry
    
    public String getProvince() { return province; }
    public void setProvince(String province) {
        this.province = province;
    } //- setProvince
    
    public String getWebPage() { return webPage; }
    public void setWebPage(String webPage) {
        this.webPage = webPage;
    } //- setWebPage
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    } //- setEnabled
    
    public String getAppid() {
        return appid;
    } //- getAppid

    public void setAppid(String appid) {
        this.appid = appid;
    } //- setAppid
    
    public Long getInternalId() {
        return internalId;
    } //- getInternalId
    
    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    } //- setInternalId

    public java.util.Date getUpdated() {
        return updated;
    } //- getUpdated

    public void setUpdated(java.util.Date updated) {
        this.updated = updated;
    } //- setUpdated
    
    //  ========================================================
    //  Additional Spring Security UserDetails Interface
    //  ========================================================

    public final ArrayList<GrantedAuthority> grantedAuthorities =
        new ArrayList<GrantedAuthority>();
    
    public void addGrantedAuthority(GrantedAuthority authority) {
        grantedAuthorities.add(authority);
    }
    
    public void removeGrantedAuthority(GrantedAuthority authority) {
        for (GrantedAuthority auth : grantedAuthorities) {
            if (auth.getAuthority().equals(authority.getAuthority())) {
                grantedAuthorities.remove(auth);
                break;
            }
        }
    }
    
    public GrantedAuthority[] getAuthorities() {
        if (grantedAuthorities.isEmpty()) {
            return new GrantedAuthority[] {new GrantedAuthorityImpl("HOLDER")};
        } else {
            GrantedAuthority[] authorities =
                new GrantedAuthority[grantedAuthorities.size()];
            for (int i=0; i<grantedAuthorities.size(); i++){
                authorities[i] = grantedAuthorities.get(i);
            }
            return authorities;
        }
        
    }  //- getAuthorities
    
    public boolean isAccountNonExpired() {
        return true;
    }
    
    public boolean isAccountNonLocked() {
        return true;
    }
    
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isComplete() {
        if ((firstName != null) &&
                (lastName != null) &&
                (email != null) &&
                (affiliation1 != null)) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isAdmin() {
        GrantedAuthority[] authorities = this.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ADMIN")) {
                return true;
            }
        }
        return false;
    }
    
    public void setAdmin(boolean admin) {
        if (admin && !isAdmin()) {
            addGrantedAuthority(new GrantedAuthorityImpl("ADMIN"));
        }
        if (!admin && isAdmin()) {
            removeGrantedAuthority(new GrantedAuthorityImpl("ADMIN"));
        }
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("username: ");
        buf.append(this.getUsername());
        buf.append("; ");

        buf.append("password: ");
        buf.append(this.getPassword());
        buf.append("; ");
        
        buf.append("firstName: ");
        buf.append(this.getFirstName());
        buf.append("; ");
        
        buf.append("middleName: ");
        buf.append(this.getMiddleName());
        buf.append("; ");
        
        buf.append("lastName: ");
        buf.append(this.getLastName());
        buf.append("; ");

        buf.append("email: ");
        buf.append(this.getEmail());
        buf.append("; ");

        buf.append("affil1: ");
        buf.append(this.getAffiliation1());
        buf.append("; ");
        
        buf.append("country: ");
        buf.append(this.getCountry());
        buf.append("; ");

        buf.append("province: ");
        buf.append(this.getProvince());
        buf.append("; ");

        buf.append("webPage: ");
        buf.append(this.getWebPage());
        buf.append("; ");

        buf.append("status: ");
        buf.append(this.getStatus());
        buf.append("; ");

        buf.append("enabled: ");
        buf.append(this.isEnabled());
        buf.append("; ");
        
        buf.append("updated: ");
        buf.append(this.getUpdated());
        buf.append("; ");
        
        buf.append("internalId: ");
        buf.append(this.getInternalId());
        buf.append("; ");
        
        GrantedAuthority[] authorities = this.getAuthorities();
        buf.append("authorities: ");
        for (int i=0; i<authorities.length; i++) {
            buf.append(authorities[i].getAuthority());
            if (i<authorities.length-1) {
                buf.append(", ");
            }
        }

        return buf.toString();
        
    }  //- toString();
    
} //- class Account
