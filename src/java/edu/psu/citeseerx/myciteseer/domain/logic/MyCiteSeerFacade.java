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
package edu.psu.citeseerx.myciteseer.domain.logic;

import org.springframework.security.userdetails.UserDetailsService;

import edu.psu.citeseerx.myciteseer.dao.*;

/**
 * Provides a single point access to all the Personal Portal persistent storage
 * operations
 * @author Isaac Councill
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public interface MyCiteSeerFacade extends UserDetailsService, 
AccountDAO, SubmissionDAO, MyNetDAO, ConfigurationDAO, CollectionDAO,
SubscriptionDAO, TagDAO, FeedDAO, GroupDAO{ }  //- interface MyCiteSeerFacade
