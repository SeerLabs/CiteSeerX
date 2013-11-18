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

import com.google.code.kaptcha.Constants;

import javax.servlet.http.*;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

/**
 * Filter to validate a captcha response
 * @see org.springframework.web.servlet.mvc.Controller
 * @author Isaac Council
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CaptchaAuthenticationProcessingFilter
extends AuthenticationProcessingFilter {

    private boolean useCaptcha = false;
    
    public void setUseCaptcha(boolean useCaptcha) {
        this.useCaptcha = useCaptcha;
    } //- setUseCaptcha
    
    
    public static final String CAPTCHA_KEY = "j_captcha_response";
    
    /* (non-Javadoc)
     * @see org.springframework.security.ui.webapp.AuthenticationProcessingFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request)
    throws AuthenticationException {

        if (!useCaptcha) {
            return super.attemptAuthentication(request);
        }
        
        String captcha = request.getParameter(CAPTCHA_KEY);
        String captchaTarget = (String)request.getSession()
                .getAttribute(Constants.KAPTCHA_SESSION_KEY);
        
        if (captchaTarget == null) {
            throw new CaptchaAuthenticationException(
                    "Please try again.  Null captcha target: " +
                    "this can be caused by a failover or asynchronous " +
                    "session replication.");
        }
        if (captcha == null || !captchaTarget.equalsIgnoreCase(captcha)) {
            throw new CaptchaAuthenticationException(
                    "Incorrect captcha response");
        }
        return super.attemptAuthentication(request);
    } //- attemptAuthentication
    
} //- class CaptchaAuthenticationProcessingFilter


class CaptchaAuthenticationException extends AuthenticationException {
    
    private static final long serialVersionUID = -4873502366330480986L;

    public CaptchaAuthenticationException(String message) {
        super(message);
    }
} //- CaptchaAuthenticationException
