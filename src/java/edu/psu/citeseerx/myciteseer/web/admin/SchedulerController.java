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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.Scheduler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Simple controller to start/stop the scheduler.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class SchedulerController implements Controller {

    private Scheduler scheduler;

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }



    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Account adminAccount = MCSUtils.getLoginAccount();
        if (!adminAccount.isAdmin()) {
            return new ModelAndView("admin/adminRequired", null);
        }

        Boolean schedulerActive = true;
        if ((!scheduler.isStarted()) || (scheduler.isStarted() &&
                (scheduler.isShutdown() || scheduler.isInStandbyMode()))) {
            schedulerActive = false;
        }
        
        String type = request.getParameter("type");
        if (type != null && type.equals("update")) {
        
            String action = request.getParameter("action");
            Boolean doStart = false;
            
            if (null != action && action.equals("start")) {
                doStart = true;
            }
            if (schedulerActive && !doStart) {
                System.out.println("Stopping scheduler");
                scheduler.standby();
                schedulerActive = false;
            }else if (!schedulerActive && doStart){
                System.out.println("Starting up scheduler");
                scheduler.start();
                schedulerActive = true;
            }
        }
        
        HashMap<String, Object> model = new HashMap<String, Object>();
        
        System.out.println("Status: " + schedulerActive);
        model.put("running", schedulerActive);
        return new ModelAndView("admin/handleScheduler", model);
    } //- handleRequest

} //- class SchedulerController
