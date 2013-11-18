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
package edu.psu.citeseerx.disambiguation.dao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Author
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class Author {
    
    protected String type;
    
    protected String id;
    protected String lastName, firstName, middleName;
    protected String affil;
    protected String addr;
    protected String email;
    
    protected int order;
    
    public static Pattern p = Pattern.compile("^([^ ]+)( ([^ ]+))?( ([^ ]+))$");
	
    public Author() { this.type = "Author"; }
    public Author(String type) { this.type = type; }

	public String getName() {
	    String name = firstName;
	    if (middleName != null) {
	        name += " " + middleName;
	    }
	    name += " " + lastName;
	    return name;
	}
	public String getLastName() { return this.lastName; }
	public String getFirstName() { return this.firstName; }
	public String getMiddleName() { return this.middleName; }
    /** shortName */
    public String lastAndFirstInit() {
		if (firstName == null) 
			return lastName;
		else
			return lastName + ", " + firstName.substring(0,1);
    }
	public String lastAndFirst() {
		if (firstName == null) {
			return lastName;
		}
		else return lastName + ", " + firstName;
	}

	public String getEmail() { return this.email; }
	public String getId() { return this.id; }
    public String getAffil() { return this.affil; }    
    public String getAddr()  { return this.addr; }        
    public int getOrder() { return this.order; }

	public void setid(String id)        { this.id = id; }
	public void setAffil(String affil)  { this.affil = affil; }
	public void setAddr(String addr)    { this.addr = addr; }
	public void setEmail(String email)  { this.email = email; }
	public void setOrder(int order)     { this.order = order; }
	public void setName(String name) {
	    Matcher m = p.matcher(name);
		if (m.find()) {
			this.firstName 	= m.group(1);
			this.middleName = m.group(3);
			this.lastName 	= m.group(5);
		}
	}
	
	public boolean isMiddleNameCompatible(String middleName) {
		String m = this.getMiddleName();

		if ((m.length() >= 1) && (middleName.length() >= 1) &&
			(m.charAt(0) != middleName.charAt(0))) {
			return false;
		}
		return true;
	}

	public boolean isCompatible(Author auth) {
		return isMiddleNameCompatible(auth.getMiddleName());
	}
		
    public String toString() {
        String xml = "\t<Author>\n";
        xml += "\t\t<Name>" + firstName + " " + lastName + "</Name>\n";
        if (affil != null) 
            xml += "\t\t<Affil>" + affil + "</Affil>\n";
        if (addr != null)
            xml += "\t\t<Addr>" + addr + "</Addr>\n";
        if (email != null)
            xml += "\t\t<Email>" + email + "</Email>\n";        
        xml += "\t</Author>\n";
        return xml;
    }
}