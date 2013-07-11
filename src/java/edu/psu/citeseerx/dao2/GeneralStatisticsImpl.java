/**
 * 
 */
package edu.psu.citeseerx.dao2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;

import edu.psu.citeseerx.dao2.GeneralStatistics;

/**
 * @author pradeep
 *
 */
public class GeneralStatisticsImpl extends JdbcDaoSupport
implements GeneralStatistics {

	protected void initDao() throws ApplicationContextException {
        	initMappingSqlQueries();
    	}


	private static final String DEF_GET_NUM_DOCS =
        	"select count(*) from citeseerx.papers";
    
	private static final String DEF_GET_NUM_CITE =
		"select count(*) from citeseerx.citations";
	
	private static final String DEF_GET_PUB_DOCS =
		"select count(*) from citeseerx.papers where citeseerx.papers.public=1";
	
	private static final String DEF_GET_AUTHORS = 
		"select count(*) from citeseerx.authors";
	
	private static final String DEF_GET_UNIQUE_AUTHORS =
		"select count(distinct(name)) from citeseerx.authors";
	
	private static final String DEF_GET_DISAMBIG_AUTHORS =
		"select count(distinct(cluster)) from citeseerx.authors";
	
	private static final String DEF_GET_UNIQUE_ENTITIES = 
		"select count(*) from csx_citegraph.clusters";
	
	private static final String DEF_GET_UNIQUE_PUBLIC_DOCS =
		"select count(distinct(cluster)) from citeseerx.papers where public=1";
	
	/* 
	 * Instantiate 
	 */
	
	private GetDocumentsInCollection getDocumentsInCollection;
	private GetCitationsInCollection getCitationsInCollection;
	private GetPublicDocumentsInCollection getPublicDocumentsInCollection;
	private GetAuthorsInCollection getAuthorsInCollection;
	private GetUniqueAuthorsInCollection getUniqueAuthorsInCollection;
	private GetDisambiguatedAuthorsInCollection getDisambiguatedAuthorsInCollection;
	private GetUniqueEntitiesInCollection getUniqueEntitiesInCollection;
	private GetUniquePublicDocuments getNumberofUniquePublicDocuments;
	
	
	protected void initMappingSqlQueries() throws ApplicationContextException {
		getDocumentsInCollection = new GetDocumentsInCollection(getDataSource());
		getCitationsInCollection = new GetCitationsInCollection(getDataSource());
		getPublicDocumentsInCollection = new GetPublicDocumentsInCollection(getDataSource());
		getAuthorsInCollection = new GetAuthorsInCollection(getDataSource());
		getUniqueAuthorsInCollection = new GetUniqueAuthorsInCollection(getDataSource());
		getDisambiguatedAuthorsInCollection = new GetDisambiguatedAuthorsInCollection(getDataSource());
		getUniqueEntitiesInCollection = new GetUniqueEntitiesInCollection(getDataSource());
		getNumberofUniquePublicDocuments = new GetUniquePublicDocuments(getDataSource());
	}
	
	
	
    private class GetDocumentsInCollection extends MappingSqlQuery {
        
        public GetDocumentsInCollection(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_NUM_DOCS);
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public Long run() {
            List list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return (Long)list.get(0);
            }
        }
        
    }  //- class GetDocumentsInCollection
	
    private class GetCitationsInCollection extends MappingSqlQuery {
        
        public GetCitationsInCollection(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_NUM_CITE);
            compile();
        }
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }
        
        public Long run() {
            List list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return (Long)list.get(0);
            }
        }
        
    }
	
    private class GetPublicDocumentsInCollection extends MappingSqlQuery {
    
    public GetPublicDocumentsInCollection(DataSource dataSource) {
        setDataSource(dataSource);
        setSql(DEF_GET_PUB_DOCS);
        compile();
    }
    
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(1);
    }
    
    	public Long run() {
        	List list = execute();
        	if (list.isEmpty()) {
            return null;
        	} else {
            return (Long)list.get(0);
        	}
    	}
    
	}

    private class GetAuthorsInCollection extends MappingSqlQuery {
    
    public GetAuthorsInCollection(DataSource dataSource) {
        setDataSource(dataSource);
        setSql(DEF_GET_AUTHORS);
        compile();
    }
    
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(1);
    }
    
    public Long run() {
        List list = execute();
        if (list.isEmpty()) {
            return null;
        } else {
            return (Long)list.get(0);
        }
    }
    
	}

	
    private class GetDisambiguatedAuthorsInCollection extends MappingSqlQuery {
    
    public GetDisambiguatedAuthorsInCollection(DataSource dataSource) {
        setDataSource(dataSource);
        setSql(DEF_GET_DISAMBIG_AUTHORS);
        compile();
    }
    
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(1);
    }
    
    public Long run() {
        List list = execute();
        if (list.isEmpty()) {
            return null;
        } else {
            return (Long)list.get(0);
        }
    }
    
	}

	private class GetUniqueEntitiesInCollection extends MappingSqlQuery {
    
    		public GetUniqueEntitiesInCollection(DataSource dataSource) {
        		setDataSource(dataSource);
        		setSql(DEF_GET_UNIQUE_ENTITIES);
        		compile();
    		}
    
    		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        		return rs.getLong(1);
    		}
    
    		public Long run() {
        		List list = execute();
        		if (list.isEmpty()) {
            			return null;
        		} else {
            			return (Long)list.get(0);
        		}
    		}
    
	}

	private class GetUniquePublicDocuments extends MappingSqlQuery {
        
        	public GetUniquePublicDocuments(DataSource dataSource) {
            		setDataSource(dataSource);
            		setSql(DEF_GET_UNIQUE_PUBLIC_DOCS);
            		compile();
       		}
        
        	public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            		return rs.getLong(1);
        	}
        
        	public Long run() {
            		List list = execute();
            		if (list.isEmpty()) {
                		return null;
            		} else {
                		return (Long)list.get(0);
            		}
        	}
        
    	}

	private class GetUniqueAuthorsInCollection extends MappingSqlQuery {
    
    		public GetUniqueAuthorsInCollection(DataSource dataSource) {
        		setDataSource(dataSource);
        		setSql(DEF_GET_UNIQUE_AUTHORS);
        		compile();
    		}
    
    		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        		return rs.getLong(1);
    		}
    
    		public Long run() {
        		List list = execute();
        		if (list.isEmpty()) {
            			return null;
        		} else {
            			return (Long)list.get(0);
        		}
    		}
    
	}


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.GeneralStatistics#getDocumentsInCollection()
	 */
	
	@Override
	public long getDocumentsInCollection() throws DataAccessException {
		// TODO Auto-generated method stub
		return getDocumentsInCollection.run();
	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.GeneralStatistics#getCitationsInCollection()
	 */
	@Override
	public long getCitationsInCollection() throws DataAccessException {
		// TODO Auto-generated method stub
		return getCitationsInCollection.run();
	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.GeneralStatistics#publicDocumentsInCollection()
	 */
	@Override
	public long getPublicDocumentsInCollection() throws DataAccessException {
		// TODO Auto-generated method stub
		return getPublicDocumentsInCollection.run();
	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.GeneralStatistics#getAuthorsInCollection()
	 */
	@Override
	public long getAuthorsInCollection() throws DataAccessException {
		// TODO Auto-generated method stub
		return getAuthorsInCollection.run();
	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.GeneralStatistics#getUniqueAuthorsInCollection()
	 */
	@Override
	public long getUniqueAuthorsInCollection() throws DataAccessException {
		// TODO Auto-generated method stub
		return getUniqueAuthorsInCollection.run();
	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.GeneralStatistics#getDisambiguatedAuthorsInCollection()
	 */
	@Override
	public long getDisambiguatedAuthorsInCollection()
			throws DataAccessException {
		// TODO Auto-generated method stub
		return getDisambiguatedAuthorsInCollection.run();
	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.GeneralStatistics#getUniqueEntitiesInCollection()
	 */
	@Override
	public long getUniqueEntitiesInCollection() throws DataAccessException {
		// TODO Auto-generated method stub
		return getUniqueEntitiesInCollection.run();
	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.GeneralStatistics#getNumberofUniquePublicDocuments()
	 */
	
	public long getNumberofUniquePublicDocuments() throws DataAccessException {
		return getNumberofUniquePublicDocuments.run();
	}
	
}
