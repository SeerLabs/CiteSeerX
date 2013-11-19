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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * Controller which generates the captcha images and send it to the user
 * @see org.springframework.web.servlet.mvc.Controller
 * @author Isaac Council
 * @version $Rev$ $Date$
 */
public class CaptchaImageCreateController
implements Controller, InitializingBean{

    private ImageCaptchaService jcaptchaService;
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        String captchaId = request.getSession().getId();
        
        BufferedImage challenge =
            jcaptchaService.getImageChallengeForID(captchaId,
                    request.getLocale());
        
        /*JPEGImageEncoder jpegEncoder =
            JPEGCodec.createJPEGEncoder(jpegOutputStream);
        jpegEncoder.encode(challenge);*/
        
        ImageIO.write(challenge, "jpg", jpegOutputStream);
        
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
        return null;
            
    }  //- handleRequest
    

    public void setJcaptchaService(ImageCaptchaService jcaptchaService) {
        this.jcaptchaService = jcaptchaService;
    } //- setJcaptchaService
    
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if (jcaptchaService == null) {
            throw new RuntimeException("Image captcha service wasn't set!");
        }
    } //- afterPropertiesSet
    
}  //- class CaptchaImageCreateController
