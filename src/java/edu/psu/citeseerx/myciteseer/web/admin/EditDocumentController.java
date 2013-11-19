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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentProperties;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.updates.UpdateManager;

/**
 * Allow an administrator to do some administrative tasks over a document
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class EditDocumentController implements Controller {

	private CSXDAO csxdao;

	public void setCSXDAO(CSXDAO csxdao) {
		this.csxdao = csxdao;
	} //- setCSXDAO

	private UpdateManager updateManager;

	public void setUpdateManager(UpdateManager updateManager) {
		this.updateManager = updateManager;
	} //- setUpdateManager

	private Document updateDOI(String doiID, boolean isPublic, boolean isDMCA) {

		Document doc = null;		
		doc = csxdao.getDocumentFromDB(doiID, true, false, false, true,
				true, true);
		if( doc != null) {
			try {
				if(isPublic != true) {
					updateManager.deleteDocument(doc);
				}
				if(isDMCA == true) {
					updateManager.setDocumentDMCA(doc);
				}
				if(isPublic == true && isDMCA == false) {
					updateManager.setDocumentPublic(doc);
				}
			}
			catch( Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return doc;
	}

	public class ThinDocumentEditFormat {
		private String documentTitle = null;
		private String doi = null;
		private boolean isPublic = false;
		private boolean isDMCA = true;

		public ThinDocumentEditFormat(String t, String id, boolean pblc, boolean dmca) {
			this.documentTitle = t;
			this.doi = id;
			this.isPublic = pblc;
			this.isDMCA = dmca;
		}

		public String getDocumentTitle() {
			return this.documentTitle;
		}
		public void setDocumentTitle(String title) {
			this.documentTitle = title;
		}
		public String getDOI() {
			return this.doi;
		}
		public void setDOI(String cdoi) {
			this.doi = cdoi;
		}
		public void setisPublic(boolean p) {
			this.isPublic = p;
		}
		public void setisDMCA(boolean d) {
			this.isDMCA = d;
		}
		public boolean getisPublic() {
			return this.isPublic;
		}
		public boolean getisDMCA() {
			return this.isDMCA;
		}
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

		String listDOIs = request.getParameter("listdois");
		String type = request.getParameter("type");
		Document doc = null;
		List<ThinDocumentEditFormat> returnList= null;
		HashMap<String, Object> model = new HashMap<String, Object>();
		
		if(listDOIs != null) {
			
			String []dois = listDOIs.split("[,\\n\\s]+");
			
			if (dois.length > 0) {
				returnList = new ArrayList<ThinDocumentEditFormat>();
				for (String doiID : dois) {
					doc = csxdao.getDocumentFromDB(doiID, true, false, false, true,
							true, true);
					boolean isPublic = true;
					boolean isDMCA = false;
					String title = doc.getDatum(Document.TITLE_KEY);
					if ( doc.getState() != DocumentProperties.IS_PUBLIC) {
						isPublic = false;
					}
					if ( doc.getState() == DocumentProperties.IS_DMCA) {
						isDMCA = true;
					}
					returnList.add(new ThinDocumentEditFormat(title, doiID, isPublic, isDMCA));
				}
			}
			ModelAndView mandv = new ModelAndView("admin/editDocument");
			mandv.addObject("docList", returnList);
			return mandv;
		}
		else if(type != null && type.equals("update")) {
			ArrayList<String> doiSeen = new ArrayList<String>();
			String []listdois = request.getParameterValues("cdoi");
			String []listunpublished = request.getParameterValues("unpublish"); // Note some maybe empty
			String []listdmcaed = request.getParameterValues("dmcaed"); // Note some maybe empty
			if( listunpublished != null ) {
				for (String unpub : listunpublished) {
					this.updateDOI(unpub, false, false);
					doiSeen.add(unpub);
				}
			}
			if ( listdmcaed != null ) {
				for (String dmca: listdmcaed) {
					this.updateDOI(dmca, true, true);
					if (!doiSeen.contains(dmca)) {
						doiSeen.add(dmca);
					}
				}
			}
			if ( listdois != null ) {
				for (String currentdoi: listdois) {
					if(!doiSeen.contains(currentdoi)) {
						this.updateDOI(currentdoi, true, false );
						doiSeen.add(currentdoi);
					}
				}
			}
			returnList = new ArrayList<ThinDocumentEditFormat>();
			for (String doisUpdated: doiSeen) {
				doc = csxdao.getDocumentFromDB(doisUpdated, true, false, false, true,
						true, true);
				boolean isPublic = true;
				boolean isDMCA = false;
				String title = doc.getDatum(Document.TITLE_KEY);
				if ( doc.getState() != DocumentProperties.IS_PUBLIC) {
					isPublic = false;
				}
				if ( doc.getState() == DocumentProperties.IS_DMCA) {
					isDMCA = true;
				}
				returnList.add(new ThinDocumentEditFormat(title, doisUpdated, isPublic, isDMCA));
			}
			
			ModelAndView mandv = new ModelAndView("admin/editDocument");
			mandv.addObject("docList", returnList);
			return mandv;
		}
		else {
			return new ModelAndView("admin/searchDocument", model);
		}

	} //- handleRequest
} //- class EditDocumentController
