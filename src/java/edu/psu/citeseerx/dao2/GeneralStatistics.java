package edu.psu.citeseerx.dao2;


import org.springframework.dao.DataAccessException;

public interface GeneralStatistics {
	
	// Gets the number of documents in the collection
	public long getDocumentsInCollection()
	throws DataAccessException;
	
	// Gets the number of citations in the collection
	public long getCitationsInCollection()
	throws DataAccessException;
	
	// Gets the number of public documents in the collection
	public long getPublicDocumentsInCollection()
	throws DataAccessException;
	
	// Gets the number of authors in the collection
	public long getAuthorsInCollection()
	throws DataAccessException;
	
	// Gets the number of unique authors in the collection
	public long getUniqueAuthorsInCollection()
	throws DataAccessException;
	
	// Gets the number of disambiguated authors in the collection
	public long getDisambiguatedAuthorsInCollection()
	throws DataAccessException;
	
	// Gets the number of clusters in the collection (unique citations)
	public long getUniqueEntitiesInCollection() 
	throws DataAccessException;
	
	public long getNumberofUniquePublicDocuments() 
	throws DataAccessException;
	
}
