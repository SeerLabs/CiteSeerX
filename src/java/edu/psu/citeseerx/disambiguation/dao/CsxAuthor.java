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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.psu.citeseerx.utility.StringUtil;

/**
 * CsxAuthor
 * 
 * @author Puck Treeratpituk
 * @version $Rev: 1 $ $Date: 2010-11-19 $
 */
public class CsxAuthor extends Author {
    
	public static Pattern p = 
	    Pattern.compile("^ *(((Dr|dr|Prof|prof|Investigators|Supervisor|" +
	    		"Supervisor Dr) )*)?([^ ]+)( (.+))?( ([^ ]+))$");
	
    public CsxAuthor() { this.type = "CsxAuthor"; }

	public CsxAuthor(ResultSet rs) throws SQLException {
		this.type      = "CsxAuthor";
		this.id        = rs.getString("id");
		String name    = rs.getString("name");
		this.setName(name);
		this.affil     = rs.getString("affil");
		this.addr      = rs.getString("address");
		this.email     = rs.getString("email");
		if (this.email != null) {
			this.email = StringUtil.cleanUpEmail(this.email);
		}
		this.order     = rs.getInt("ord");
	}
	
	public void setName(String name) {
		name = name.replaceAll("Ph\\.? D\\.?", ""); // remove Ph. D. 
		name = name.trim();
		name = StringUtil.dropAccent(name);
		name = name.replaceAll(" -[iI]ng","");    // remove '-ing'
		name = name.replaceAll(" \\(.*$","");     // remove '(...' from parsing error
		name = name.replaceAll("[\\.\\(\\)]",""); // remove '.', '(', ')' 
		name = name.replaceAll(" [a-zA-Z]$", "");
		Matcher m = p.matcher(name);
		if (m.find()) {
			this.firstName 	= m.group(4);
			if (m.group(6) != null) 
				this.middleName = m.group(6);
			else {
				if ((this.firstName != null) && (this.firstName.length() == 2) 
				        && (this.firstName.charAt(1) >= 'A' 
				            && this.firstName.charAt(1) <= 'Z')) {
					this.middleName = this.firstName.substring(1,2);
					this.firstName  = this.firstName.substring(0,1);
				}
				else 
					this.middleName = "";
			}
			this.lastName 	= m.group(8);
		}
	}
	
	public String getName() {
		String name = firstName;
		if (middleName != null) {
			name += " " + middleName;
		}
		name += " " + lastName;
		return name;
	}
	
    public String toString() {
        String xml = "\t<Author id='" + this.id + "'>\n";
        xml += "\t\t<Name>" + firstName + " [" + middleName + "] " + lastName + 
            "</Name>\n";
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