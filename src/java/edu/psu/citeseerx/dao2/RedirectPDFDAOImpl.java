/**
 * 
 */
package edu.psu.citeseerx.dao2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.PDFRedirect;

/**
 * @author pradeep
 *
 */
public class RedirectPDFDAOImpl extends  JdbcDaoSupport implements RedirectPDFDAO {

	public GetPDFRD getPDFRD;
	public InsertPDFRD insertPDFRD;
	public UpdatePDFRD updatePDFRD;
	public UpdatePDFRDTemplate updatePDFRDTemplate;
	
	 protected void initDao() throws ApplicationContextException {
	        initMappingSqlQueries();
	 } //- initDao
	
	
	 protected void initMappingSqlQueries() throws ApplicationContextException {
		 this.getPDFRD = new GetPDFRD(getDataSource());
		 this.insertPDFRD = new InsertPDFRD(getDataSource());
		 this.updatePDFRD = new UpdatePDFRD(getDataSource());
		 this.updatePDFRDTemplate = new UpdatePDFRDTemplate(getDataSource());
		 
	 }

	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.RedirectPDFDAO#getPDFRedirect(java.lang.String)
	 */
	@Override
	public PDFRedirect getPDFRedirect(String doi) throws DataAccessException {
		return getPDFRD.run(doi);
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.RedirectPDFDAO#insertPDFRedirect(java.lang.String, edu.psu.citeseerx.domain.PDFRedirect)
	 */
	@Override
	public void insertPDFRedirect( PDFRedirect pdfredirect)
			throws DataAccessException {
		this.insertPDFRD.run(pdfredirect);
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.RedirectPDFDAO#updatePDFRedirect(edu.psu.citeseerx.domain.PDFRedirect)
	 */
	@Override
	public void updatePDFRedirect(String doi , PDFRedirect pdfredirect)
			throws DataAccessException {
		this.updatePDFRD.run(doi, pdfredirect);
		// TODO Auto-generated method stub
	}
	
	
	public void updatePDFRedirectTemplate(String label, String urlTemplate) throws DataAccessException
	{
		this.updatePDFRDTemplate.run(label, urlTemplate);
	}
	

	private final String DEF_GET_PDF_REDIRECT =
	"select externalrepoid, redirecttemplates.label, paperid, url, redirecttemplates.urltemplate"+
	" from redirectpdf, redirecttemplates" 
	+" where redirectpdf.paperid = ? and redirectpdf.label = redirecttemplates.label";
	
	private class GetPDFRD extends MappingSqlQuery {
        
        public GetPDFRD(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_PDF_REDIRECT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetPDFRD.GetDoc
        
        public PDFRedirect mapRow(ResultSet rs, int rowNum) throws SQLException {
        	PDFRedirect pdfredirect = new PDFRedirect();
        	pdfredirect.setExternaldoi(rs.getString("externalrepoid"));
        	pdfredirect.setLabel(rs.getString("label"));
        	pdfredirect.setPaperid(rs.getString("paperid"));
        	pdfredirect.setUrl(rs.getString("url"));
        	pdfredirect.setUrlTemplate(rs.getString("urltemplate"));
                        
            return pdfredirect;
        } //- GetPDFRD.mapRow
        
        public PDFRedirect run(String doi) {
            List<PDFRedirect> list = execute(doi);
            if (list.isEmpty()) {
                return null;
            } else {
                return (PDFRedirect)list.get(0);
            }
        } //- GetPDFRD.run
        
    }  //- class GetPDFRD

	// Only updates the redirectpdf table, no changes to the templates !
	private final String DEF_INSERT_PDF_REDIRECT = "INSERT" +
			"INTO redirectpdf (externalrepoid, label, paperid, url) " +
			"values (?,?,?,?)";
	
	private class InsertPDFRD extends SqlUpdate {
		public InsertPDFRD(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_PDF_REDIRECT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertPDFRD.InsertPDFRD
        
        public int run(PDFRedirect pdfredirect) {
            Object[] params = new Object[] {
            		pdfredirect.getExternaldoi(),
            		pdfredirect.getLabel(),
            		pdfredirect.getPaperid(),
            		pdfredirect.getUrl()
            };
            return update(params);
        } //- InsertPDFRD
		
	}
	
	private final String DEF_UPDATE_PDF_REDIRECT = "UPDATE" +
			"redirectpdf SET externalrepoid= ?, SET label = ?, " +
			"paperid = ? , url = ? WHERE paperid = ? ";
	
	private class UpdatePDFRD extends SqlUpdate {
		public UpdatePDFRD(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_PDF_REDIRECT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdatePDFRD.UpdatePDFRD
        
        public int run(String doi, PDFRedirect pdfredirect) {
            Object[] params = new Object[] {
            		pdfredirect.getExternaldoi(),
            		pdfredirect.getLabel(),
            		pdfredirect.getPaperid(),
            		pdfredirect.getUrl(),
            		doi
            };
            return update(params);
        } //- UpdatePDFRD.run
		
	} // UpdatePDFRD
	
	private final String DEF_UPDATE_PDF_REDIRECT_TEMPLATE = "UPDATE" +
			"redirecttemplates SET urltemplate= ? " +
			"where label = ?";
	
	private class UpdatePDFRDTemplate extends SqlUpdate {
		public UpdatePDFRDTemplate(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_PDF_REDIRECT_TEMPLATE);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdatePDFRDTemplate.UpdatePDFRDTemplate
        
        public int run(String label, String urlTemplate) {
            Object[] params = new Object[] {
            		urlTemplate,
            		label,
            };
            return update(params);
        } //- UpdatePDFRDTemplate.run
		
	} // UpdatePDFRD
	

} // Class
