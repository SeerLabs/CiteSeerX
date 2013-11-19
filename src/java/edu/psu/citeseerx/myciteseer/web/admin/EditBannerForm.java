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
package edu.psu.citeseerx.myciteseer.web.admin;

import java.io.Serializable;

/**
 * Command object to manipulate/obtain user input to be used by
 * EditBannerFormController
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class EditBannerForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8311423994485407647L;
    private String banner;
    
    public void setBanner(String banner) {
        this.banner = banner;
    } //- setBanner
    
    public String getBanner() {
        return banner;
    } //- getBanner
    
} //- class EditBannerForm
