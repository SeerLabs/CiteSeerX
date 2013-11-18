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
package edu.psu.citeseerx.domain;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/*
 * @author Puck Treeratpituk
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class UniqueAuthor implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1197884706130868495L;
    
    private String aid;
    private String canname;
    private List<String> varnames = new ArrayList<String>();
    private String email;
    //private String affil;
	private List<String> affils = new ArrayList<String>();
    private String addr;
    private int ndocs  = 0;
    private int ncites = 0;
    private int hindex = 0;
    private String url;
    
    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    public UniqueAuthor() {
        super();
    }

	public UniqueAuthor(UniqueAuthor uauth) {
		super();
		this.aid      = uauth.aid;
		this.canname  = uauth.canname;
		this.varnames = new ArrayList<String>(uauth.varnames);
		this.email  = uauth.email;
		this.affils = new ArrayList<String>(uauth.affils);
		this.addr   = uauth.addr;
		this.ndocs  = uauth.ndocs;
		this.ncites = uauth.ncites;
		this.hindex = uauth.hindex;
		this.url    = uauth.url;
	}

    public String getAid() {
        return aid;
    }
    public void setAid(String aid) {
        this.aid = aid;
    }
    public String getCanname() {
        return canname;
    }
    public void setCanname(String canname) {
    	this.canname = canname;
    }
    public List<String> getVarnames() {
        return varnames;
    }
    public void setVarnames(List<String> varnames) {
        this.varnames = varnames;
    }
    public String getEmail() {
    	return email;
    }
    public void setEmail(String email) {
    	this.email = email;
    }
    public String getAffil() {
		if (affils.size() > 0)
			return affils.get(0);
		else return null;
    }
	public List<String> getAffils() {
		return affils;
	}
    public void setAffil(String affil) {
		this.affils.clear();
    	this.affils.add(affil);
    }
	public void setAffils(List<String> affils) {
		this.affils = affils;
	}
	public void addAffil(String affil) {
		if (affil != null)
			this.affils.add(affil);
	}
    public String getAddress() {
    	return addr;
    }
    public void setAddress(String addr) {
    	this.addr = addr;
    }
    public int getNdocs() {
    	return ndocs;
    }
    public void setNDocs(int ndocs) {
    	this.ndocs = ndocs;
    }
    public int getNCites() {
    	return ncites;
    }
    public void setNCites(int ncites) {
    	this.ncites = ncites;
    }
    public int getHindex() {
    	return hindex;
    }
    public void setHindex(int hindex) {
    	this.hindex = hindex;
    }

    
}  //- class UniqueAuthor
