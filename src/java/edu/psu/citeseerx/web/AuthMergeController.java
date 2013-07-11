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
package edu.psu.citeseerx.web;

import edu.psu.citeseerx.loaders.ContextReader;
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.UniqueAuthor;
import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.*;
import org.springframework.beans.factory.ListableBeanFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Handle correction of unique authors
 * @author Puck Treeratpituk
 */
public class AuthMergeController extends SimpleFormController {
	
    private CSXDAO csxdao;
	private String solrAuthorSelectUrl;
	private int NROWS = 10;

    private MyCiteSeerFacade myciteseer;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO

    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer

	public void setSolrAuthorSelectUrl(String solrAuthorSelectUrl)
		throws MalformedURLException {
		try { 
			new URI("http", solrAuthorSelectUrl, null).toURL();
			this.solrAuthorSelectUrl = solrAuthorSelectUrl;
		} catch (URISyntaxException e) {
			throw new MalformedURLException(e.getMessage());
		}
	}

	public AuthMergeController() {
		setCommandClass(AuthMergeObj.class);
		setCommandName("command");
	}

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
	protected Object formBackingObject(HttpServletRequest request) 
		throws Exception {
		String aid    = request.getParameter("aid");
		String query  = request.getParameter("query");
		Integer cstart = null;
		try {
			cstart = new Integer(request.getParameter("cstart"));
		} catch (Exception e) { cstart = 0; }

		AuthMergeObj command = new AuthMergeObj();
		UniqueAuthor uauth = null;
		if (aid != null) {
			reloadCommand(command, aid);
			uauth = command.getUauth();
		}
		if ((query == null) && (uauth != null)) {
			query = uauth.getCanname();
		}
		if ((query != null) && (aid != null)) {
			command.setCstart(cstart);
			command.setQuery(query);
			doAuthorSearch(command);
		}
		return command;
	}

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
     */
    protected Map<String, Object> referenceData(HttpServletRequest request) 
		throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        MCSConfiguration config = myciteseer.getConfiguration();
        model.put("correctionsEnabled",
				  new Boolean(config.getCorrectionsEnabled()));
		return model;
	}

	protected void reloadCommand(AuthMergeObj command, String aid) { 
		UniqueAuthor uauth = csxdao.getAuthor(aid);
		List<ThinDoc> docs = csxdao.getAuthDocsOrdByCites(aid);
		command.setUauth(uauth);
		command.setDocs(docs);	
	}
	
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
	protected ModelAndView onSubmit(HttpServletRequest request,
									HttpServletResponse response,
									Object command, BindException errors) 
		throws Exception {
		String userid = MCSUtils.getLoginAccount().getUsername();;
		String aid    = request.getParameter("aid");

        if (userid == null) {
            // TODO add error page
            System.out.println("NO USERID!!!");
            return super.onSubmit(request, response, command, errors);
        }
        
		if (request.getParameter("correctInfo") != null) {
			UniqueAuthor uauth = ((AuthMergeObj)command).getUauth();
			csxdao.updateUauthorInfo(userid, aid, uauth.getCanname(), uauth.getAffil());
			ModelAndView mav = showForm(request, response, errors);
			mav.addObject("message", "metadata is updated!");
			return mav;
		}
		else if (request.getParameter("removePapers") != null) {
			List<Integer> papers = new ArrayList<Integer>();
			List<ThinDoc> docs = ((AuthMergeObj)command).getDocs();
			for (ThinDoc doc: docs) {
				Integer cluster = new Integer(doc.getCluster().intValue());
				if (null != request.getParameter(Integer.toString(cluster)))
					papers.add(cluster);
			}
			csxdao.removeUauthorPapers(userid, aid, papers);
			reloadCommand((AuthMergeObj)command, aid);
			ModelAndView mav = showForm(request, response, errors);
			mav.addObject("message", "remove papers!");
			return mav;
		}
		else if (request.getParameter("mergeAuthors") != null) {
			String aid2 = request.getParameter("merge_author");

			csxdao.mergeUauthors(userid, aid, aid2);
			reloadCommand((AuthMergeObj)command, aid);
			doAuthorSearch((AuthMergeObj)command);
			ModelAndView mav = showForm(request, response, errors);
			mav.addObject("message", "merge authors!");
			return mav;
		}

		onBindAndValidate(request, command, errors);
		if (errors.hasErrors()) {
			return showForm(request, response, errors);
		}
		return new ModelAndView(getSuccessView());
	}

	private String buildSolrQuery(String query, String aid, Integer start) {
		StringBuffer queryString = new StringBuffer("?q=" + query + 
													" -id:" + aid);
		queryString.append("&sort=ncites+desc");
		queryString.append("&start=" + start + "&rows=" + NROWS);
		queryString.append("&qt=standard");
		queryString.append("&hl=true&wt=json");
		return queryString.toString();
	}
	
    /*
     * Executes a query towards a Solr intance unsing the supplied parameters
     */
    private JSONObject executeSolrQuery(String solrSelectUrl, 
										String queryString) 
		throws SolrException, URISyntaxException, JSONException, 
			   MalformedURLException, IOException {
        JSONObject output = null;
        URI uri = null;
		//uri = new URI("http", solrAuthorSelectUrl + solrQuery.toString(), null);        
        uri = new URI(
				/*
                 *  Need to modify the configuration file for this 
                 *  (no need for http: but it needs the //).
                 *  We use the 3 parameter constructor since 
                 *  solrSelectUrl already contains: the host, port and
                 *  path. We just need to append the query and pass no
                 *  fragment 
                 */
            "http", solrSelectUrl + queryString.toString(), null);
        output =
            SolrSelectUtils.doJSONQuery(uri.toURL().toString());
        return output;
    } //- executeSolrQuery

	private List<UniqueAuthor> filterNonEmptyAuthors(List<UniqueAuthor> list) {
		List<UniqueAuthor> ret = new ArrayList<UniqueAuthor>();
		for (UniqueAuthor uauth: list) {
			UniqueAuthor dbauth = csxdao.getAuthor(uauth.getAid());
			if (dbauth.getNdocs() != 0)
				ret.add(dbauth);
		}
		return ret;
	}
	
	private void doAuthorSearch(AuthMergeObj command) {
		Boolean error = false;
		Integer numFound = 0;

		List<UniqueAuthor> hits = new ArrayList<UniqueAuthor>();
		
		String solrQuery = buildSolrQuery(command.getQuery(), command.getUauth().getAid(), 
										  command.getCstart());
		try {
			JSONObject output = 
				executeSolrQuery(solrAuthorSelectUrl, solrQuery);
			JSONObject responseObject = output.getJSONObject("response");
			numFound = responseObject.getInt("numFound");
			hits     = AuthorSolrSelectUtils.buildHitListJSON(output);
			List<UniqueAuthor> candidates = filterNonEmptyAuthors(hits);

			command.setNumFound(numFound);
			command.setCandidates(candidates);

			Integer newStart = command.getCstart() + NROWS;
			if (newStart < numFound) {
				StringBuffer nextCandidatePage = new StringBuffer();
				nextCandidatePage.append("aid="    + command.getUauth().getAid());
				nextCandidatePage.append("&query=" + command.getQuery());
				nextCandidatePage.append("&cstart="+ newStart);
				command.setNextCandidatePage(nextCandidatePage.toString());
			}
		} catch (SolrException e) {
			error = true;
		} catch (Exception e) {
			// ...
		}
	}
	
	public class AuthMergeObj {

		UniqueAuthor uauth;
		int ndocs;
		List<ThinDoc> docs;
		Integer cstart, numFound;
		List<UniqueAuthor> candidates = new ArrayList<UniqueAuthor>();

		String query;
		String nextCandidatePage;

		public AuthMergeObj() { }
		
		public UniqueAuthor getUauth() { return this.uauth; }
		public List<ThinDoc> getDocs() { return docs; }
		public int getNdocs() { return ndocs; }
		public List<UniqueAuthor> getCandidates() { return this.candidates; }
		public String getQuery() { return this.query; }
		public Integer getCstart() { return this.cstart; }
		public Integer getNumFound() { return this.numFound; }
		public String getNextCandidatePage() { return this.nextCandidatePage; }

		public void setUauth(UniqueAuthor uauth) { this.uauth = uauth; }
		public void setDocs(List<ThinDoc> docs) { 
			this.docs  = docs; 
			this.ndocs = docs.size();
		}
		public void setCandidates(List<UniqueAuthor> candidates) { this.candidates = candidates; }
		public void setQuery(String query) { this.query = query; }
		public void setCstart(Integer cstart) { this.cstart = cstart; }
		public void setNumFound(Integer numFound) { this.numFound = numFound; }
		public void setNextCandidatePage(String nextCandidatePage) { this.nextCandidatePage = nextCandidatePage; }
	}

}  //- class AuthMergeFormController
