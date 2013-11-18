/**
 * 
 */
package edu.psu.citeseerx.domain;

/**
 * Data object container for home statistics 
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class HomeStat {
	
	private long documents;
		
	/**
	 * @return number of search able documents
	 */
	public long getDocuments() {
		return documents;
	} //- getDocuments
	
	/**
	 * @param documents number of search able documents
	 */
	public void setDocuments(long documents) {
		this.documents = documents;
	} //-setDocuments
	
	private long citations;
	
	/**
	 * @return number of search able citations
	 */
	public long getCitations() {
		return citations;
	} //- getCitations
	
	/**
	 * @param citations number of search able citations
	 */
	public void setCitations(long citations) {
		this.citations = citations;
	} //- setCitations
	
	private long tables;

    /**
     * @return tables number of search able tables
     */
    public long getTables() {
        return tables;
    } //- getTables

    /**
     * @param tables tables number of search able tables
     */
    public void setTables(long tables) {
        this.tables = tables;
    } //- setTables

    private long previousDocuments;
    
    /**
     * @return number of previous search able documents
     */
    public long getPreviousDocuments() {
        return previousDocuments;
    } //- getPreviousDocuments
    
    /**
     * @param documents number of previous search able documents
     */
    public void setPreviousDocuments(long previousDocuments) {
        this.previousDocuments = previousDocuments;
    } //-setPreviousDocuments
    
    private long previousCitations;
    
    /**
     * @return number of previous search able citations
     */
    public long getPreviousCitations() {
        return previousCitations;
    } //- getPreviousCitations
    
    /**
     * @param citations number of previous search able citations
     */
    public void setPreviousCitations(long previousCitations) {
        this.previousCitations = previousCitations;
    } //- setPreviousCitations
    
    private long previousTables;

    /**
     * @return tables number of previous search able tables
     */
    public long getPreviousTables() {
        return previousTables;
    } //- getPreviousTables

    /**
     * @param tables number of previous search able tables
     */
    public void setPreviousTables(long previousTables) {
        this.previousTables = previousTables;
    } //- setPreviousTables
    
} //- Class HomeStat
