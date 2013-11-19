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
package edu.psu.citeseerx.myciteseer.web.captcha;

import com.octo.captcha.service.image.*;

/**
 * 
 * @author Isaac Council
 * @version $Rev$ $Date$
 */
public class InspectableImageCaptchaService
extends DefaultManageableImageCaptchaService {

    public boolean validateWithoutDeleting(String id, String response) {
        if (store.hasCaptcha(id)) {
            return store.getCaptcha(id).validateResponse(response);
        } else {
            System.err.println("Warning: no captcha for id: "+id);
            return false;
        }
    } //- validateWithoutDeleting
    
} //- class InspectableImageCaptchaService
