/**
 * 
 */
package edu.psu.citeseerx.myciteseer.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Data transfer object used in people search queries.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class PeopleAdvancedSearch implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -5540404069484202768L;
    private String useridQuery;
	private String firstNameQuery;
	private String middleNameQuery;
	private String lastNameQuery;
	private String affil1Query;
	private String affil2Query;
	private String countryQuery;
	private String provinceQuery;
	
	/**
	 * @return the useridQuery
	 */
	public String getUseridQuery() {
		return useridQuery;
	}
	/**
	 * @param useridQuery the useridQuery to set
	 */
	public void setUseridQuery(String useridQuery) {
		this.useridQuery = useridQuery;
	}
	/**
	 * @return the firstNameQuery
	 */
	public String getFirstNameQuery() {
		return firstNameQuery;
	} //- getFirstNameQuery
	/**
	 * @param firstNameQuery the firstNameQuery to set
	 */
	public void setFirstNameQuery(String firstNameQuery) {
		this.firstNameQuery = firstNameQuery;
	} //- setFirstNameQuery
	/**
	 * @return the middleNameQuery
	 */
	public String getMiddleNameQuery() {
		return middleNameQuery;
	} //- getMiddleNameQuery
	/**
	 * @param middleNameQuery the middleNameQuery to set
	 */
	public void setMiddleNameQuery(String middleNameQuery) {
		this.middleNameQuery = middleNameQuery;
	} //- setMiddleNameQuery
	/**
	 * @return the lastNameQuery
	 */
	public String getLastNameQuery() {
		return lastNameQuery;
	} //- getLastNameQuery
	/**
	 * @param lastNameQuery the lastNameQuery to set
	 */
	public void setLastNameQuery(String lastNameQuery) {
		this.lastNameQuery = lastNameQuery;
	} //- setLastNameQuery
	/**
	 * @return the affil1Query
	 */
	public String getAffil1Query() {
		return affil1Query;
	} //- getAffil1Query
	/**
	 * @param affil1Query the affil1Query to set
	 */
	public void setAffil1Query(String affil1Query) {
		this.affil1Query = affil1Query;
	} //- setAffil1Query
	/**
	 * @return the affil2Query
	 */
	public String getAffil2Query() {
		return affil2Query;
	} //- getAffil2Query
	/**
	 * @param affil2Query the affil2Query to set
	 */
	public void setAffil2Query(String affil2Query) {
		this.affil2Query = affil2Query;
	} //- setAffil2Query
	/**
	 * @return the countryQuery
	 */
	public String getCountryQuery() {
		return countryQuery;
	} //- getCountryQuery
	/**
	 * @param countryQuery the countryQuery to set
	 */
	public void setCountryQuery(String countryQuery) {
		this.countryQuery = countryQuery;
	} //- setCountryQuery
	/**
	 * @return the provinceQuery
	 */
	public String getProvinceQuery() {
		return provinceQuery;
	} //- getProvinceQuery
	/**
	 * @param provinceQuery the provinceQuery to set
	 */
	public void setProvinceQuery(String provinceQuery) {
		this.provinceQuery = provinceQuery;
	} //- setProvinceQuery
	
	/**
	 * Builds and returns the query based on the class attributes
	 * @return the query based on attributes that were set
	 */
	public String getQuery() {
		boolean inQuery = false;
		StringBuilder builder = new StringBuilder();
		builder.append("?query=");
		try {
			inQuery = buildFieldQuery("userid", useridQuery, builder, 
					inQuery);
			inQuery = buildFieldQuery("firstName", firstNameQuery, builder, 
					inQuery);
			inQuery = buildFieldQuery("middleName", middleNameQuery, builder, 
					inQuery);
			inQuery = buildFieldQuery("lastName", lastNameQuery, builder, 
					inQuery);
			inQuery = buildFieldQuery("affil1", affil1Query, builder, 
					inQuery);
			inQuery = buildFieldQuery("affil2", affil2Query, builder, 
					inQuery);
			inQuery = buildFieldQuery("country", countryQuery, builder, 
					inQuery);
			inQuery = buildFieldQuery("province", provinceQuery, builder, 
					inQuery);
		}catch (UnsupportedEncodingException e) { }
		return builder.toString();
	} //- getQuery
	
	/*
	 * Inserts a new field into the query
	 */
	private boolean buildFieldQuery(String fieldName, String query,
            StringBuilder builder, boolean inQuery)
    throws UnsupportedEncodingException {
        
        if (query != null && query.length() > 0) {
            if (inQuery) builder.append("+AND+");
            builder.append(fieldName);
            builder.append("%3A");
            builder.append(URLEncoder.encode(query, "UTF-8"));
            return true;
        }
        return inQuery;
        
    }  //- buildFieldQuery
} //- PeopleAdvancedSearch
