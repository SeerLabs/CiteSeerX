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

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.BiblioTransformer;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.DocumentProperties;
import edu.psu.citeseerx.domain.DomainTransformer;
import edu.psu.citeseerx.domain.ExternalLink;
import edu.psu.citeseerx.domain.Hub;
import edu.psu.citeseerx.domain.PDFRedirect;
import edu.psu.citeseerx.domain.Tag;
import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.domain.UniqueAuthor;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.utility.GeneratePDFRedirectURL;
import edu.psu.citeseerx.utility.SafeText;
import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.myciteseer.domain.Account;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.RuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides model objects to document summary view.
 * @author Isaac Councill
 * Version: $Rev$ $Date$
 */
public class CitationsController implements Controller {

	private CSXDAO csxdao;

	public void setCSXDAO (CSXDAO csxdao) {
		this.csxdao = csxdao;
	} //- setCSXDAO


	private CiteClusterDAO citedao;

	public void setCiteClusterDAO(CiteClusterDAO citedao) {
		this.citedao = citedao;
	} //- setCiteClusterDAO

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {

		String errorTitle = "Document Not Found";
		String dmcaTitle = "DMCA Notice";
		String removedTitle = "Document Removed";

		String pdfRedirectURL = null;
		String pdfRedirectLabel = null;
		String doi = null;
		String cid = request.getParameter("cid");
		Map<String, Object> model = new HashMap<String, Object>();

		if (cid != null) {
			Long cluster;
			try {
				cluster = Long.parseLong(cid);
			}catch (NumberFormatException e) {
				e.printStackTrace();
				model.put("pagetitle", errorTitle);
				return new ModelAndView("viewDocError", model);
			}
			List<String> dois = citedao.getPaperIDs(cluster);
			if(!dois.isEmpty()) {
				doi = dois.get(0);
				RedirectUtils.sendDocumentCIDRedirect(request, response, doi);
				return null;
			}
			else {
				model.put("pagetitle", errorTitle);
				return new ModelAndView("viewDocError", model);
			}
		}

		if (doi == null) {
			doi = request.getParameter("doi");
		}

		if (doi == null) {
			model.put("pagetitle", errorTitle);
			return new ModelAndView("viewDocError", model);
		}

		String xml = request.getParameter("xml");
		boolean bxml = false;
		try {
			bxml = Boolean.parseBoolean(xml);
		} catch (Exception e) {}

		String src = request.getParameter("src");
		boolean bsrc = false;
		try {
			if (bxml) {
				bsrc = Boolean.parseBoolean(src);
			}
		} catch (Exception e) {}

		String sysData = request.getParameter("sysData");
		boolean bsysData = false;
		try {
			bsysData = Boolean.parseBoolean(sysData);
		} catch (Exception e) {}

		Document doc = null;
		try {
			doc = csxdao.getDocumentFromDB(doi, false, bsrc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (doc == null) {
			model.put("pagetitle", errorTitle);
			return new ModelAndView("baddoi", model);
		}
		else if(doc.getState() == DocumentProperties.IS_PDFREDIRECT) {
			//
			PDFRedirect pdfredirect = csxdao.getPDFRedirect(doi);
			pdfRedirectURL = this.generateRedirectURL(pdfredirect);
			pdfRedirectLabel = pdfredirect.getLabel();
		}
		else if(doc.isDMCA() == true) {
			model.put("pagetitle", dmcaTitle);
			return new ModelAndView("dmcaPage", model);
		}
                else if(doc.isRemoved() == true) {
                        response.setStatus(404);
                        return new ModelAndView("null",model);
                }
		else if (doc.isPublic() == false) {
			model.put("pagetitle", removedTitle);
                        response.setStatus(404);
			return new ModelAndView("docRemovedPage", model);
		}

		List<UniqueAuthor> uauthors = new ArrayList<UniqueAuthor>();
		String authors = "";

		int c = 1;
		for (Author a : doc.getAuthors()) {
			String authorName = a.getDatum(Author.NAME_KEY);
			authors += authorName + ", ";

			// convert to unique authors
			UniqueAuthor uauth = new UniqueAuthor();
			uauth.setCanname(authorName);
			if (a.getClusterID() > 0) {
				uauth.setAid("");
			}
			uauthors.add(uauth);
			c++;
		}
		if (authors.length() == 0) {
			authors = "Unknown Authors";
		}else{
			// There is always a final comma.
			authors = authors.substring(0, authors.lastIndexOf(","));
		}

		String title = doc.getDatum(Document.TITLE_KEY);
		String abs =  doc.getDatum(Document.ABSTRACT_KEY);
		String venue = doc.getDatum(Document.VENUE_KEY);
		String year = doc.getDatum(Document.YEAR_KEY);

		DocumentFileInfo finfo = doc.getFileInfo();
		String rep = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);
		List<String> urls = getClusterURLs(doc.getClusterID());

		Long clusterID = doc.getClusterID();
		List<ThinDoc> citations = null;
		if (clusterID != null) {
			citations = citedao.getCitedDocuments(clusterID, 0, 100);
			for (Object cite : citations) {
				SolrSelectUtils.prepCitation((ThinDoc)cite);
			}
			Collections.sort(citations, new CitationComparator());
		}
                List<String> citationContexts = new ArrayList<String>();
                for (ThinDoc citation : citations){
                    String context = citedao.getContext(clusterID, citation.getCluster());
                    if (context != null){
                        citationContexts.add(context);
                    } else{
                        citationContexts.add("");
                    }
                }

                String repID = doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY);

		String bibtex =
				BiblioTransformer.toBibTeX(DomainTransformer.toThinDoc(doc));
		bibtex = SafeText.cleanXML(bibtex);
		bibtex = bibtex.replaceAll("\\n", "<br/>");
		bibtex = bibtex.replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		model.put("bibtex", bibtex);

		String coins =
				BiblioTransformer.toCOinS(DomainTransformer.toThinDoc(doc),
						request.getRequestURL().toString());
		model.put("coins", coins);

		List<ExternalLink> eLinks = csxdao.getExternalLinks(doi);

		// Obtain the hubUrls that points to this document.
		List<Hub> hubUrls = csxdao.getHubs(doi);

		model.put("pagetype", "citations");
		model.put("pagetitle", title);
		model.put("pagedescription", "Document Details (Isaac Councill, " +
				"Lee Giles, Pradeep Teregowda): " + abs);
		model.put("pagekeywords", authors);
		model.put("title", title);
		model.put("authors", authors);
		model.put("uauthors", uauthors);
		model.put("venue", venue);
		model.put("year", year);
		model.put("urls", urls);
		model.put("doi", doi);
		model.put("clusterid", clusterID);
		model.put("rep", rep);
		model.put("ncites", doc.getNcites());
		model.put("selfCites", doc.getSelfCites());
		model.put("citations", citations);
                model.put("citationContexts", citationContexts);
		model.put("elinks", eLinks);
		model.put("fileTypes", csxdao.getFileTypes(doi, repID));
		model.put("hubUrls", hubUrls);
		model.put("pdfRedirectUrl", pdfRedirectURL);
		model.put("pdfRedirectLabel", pdfRedirectLabel);

		String banner = csxdao.getBanner();
		if (banner != null && banner.length() > 0) {
			model.put("banner", banner);
		}

		return new ModelAndView("citations", model);
	} // handleRequest

	private List<String> getClusterURLs(Long clusterID) {
		List<String> dois = citedao.getPaperIDs(clusterID);
		List<String> urls = new ArrayList<String>();
		if (!dois.isEmpty()) {
			for (String doi : dois) {
				Document doc = csxdao.getDocumentFromDB(doi);
				if (doc.isPublic() || doc.getState() == DocumentProperties.IS_PDFREDIRECT) { // added this to allow
																							 // redirect urls to show
																							 // links too - maybe need
																							 // to change to check for
																							 // not public ?
					DocumentFileInfo finfo = doc.getFileInfo();
					urls.addAll(finfo.getUrls());
				}
			}
		}
		return urls;
	} //- getClusterURLs

	private String generateRedirectURL(PDFRedirect pdfredirect) {
		return GeneratePDFRedirectURL.generateURLFromTemplate(pdfredirect.getUrlTemplate(),
				pdfredirect.getExternaldoi());
	}
}  //- CitationsController

class CitationComparator implements Comparator<ThinDoc> {
	public int compare(ThinDoc o1, ThinDoc o2) {
		if (o1.getNcites() > o2.getNcites()) {
			return -1;
		}
		if (((ThinDoc)o1).getNcites() < ((ThinDoc)o2).getNcites()) {
			return 1;
		}
		return 0;
	} //- compare
} //- class CitationComparator
