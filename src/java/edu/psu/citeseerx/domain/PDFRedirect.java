/**
 * 
 */
package edu.psu.citeseerx.domain;

import java.io.Serializable;

/**
 * @author pradeep
 *
 */
public class PDFRedirect implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 44898958166731232L;
	
	private String urlTemplate; // Template
	private String externaldoi; // External id (id at the other site)
	private String url; // url for the document at the other site (complete url)
	private String label; // label for the other site
	private String paperid; // our paperid
	
	public String getUrlTemplate() {
		return urlTemplate;
	}
	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}
	public String getExternaldoi() {
		return externaldoi;
	}
	public void setExternaldoi(String externaldoi) {
		this.externaldoi = externaldoi;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getPaperid() {
		return paperid;
	}
	public void setPaperid(String paperid) {
		this.paperid = paperid;
	}

	
	
	
}
