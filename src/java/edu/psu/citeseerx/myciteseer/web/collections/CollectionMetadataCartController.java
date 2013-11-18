/**
 * 
 */
package edu.psu.citeseerx.myciteseer.web.collections;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.web.MetadataCartDWR;

/**
 * Adds collection content to metadata cart
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class CollectionMetadataCartController implements Controller {

	// MyCiteSeer data access
	private MyCiteSeerFacade myciteseer;
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
	// CiteSeer data access
	private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private MetadataCartDWR metadataCartDWR;
    
	/**
	 * @param metadataCartDWR the metadataCartDWR to set
	 */
	public void setMetadataCartDWR(MetadataCartDWR metadataCartDWR) {
		this.metadataCartDWR = metadataCartDWR;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {
		
		long collectionID;
		String errMsg = null;
		boolean error = false;
		Account account = MCSUtils.getLoginAccount();

		collectionID = ServletRequestUtils.getLongParameter(request, "cid", -1);
		if (collectionID == -1) {
			error = true;
			errMsg = "Bad collection ID : \"" + collectionID + "\" collection";
		}

		// Obtain collection info.
		Collection collection = null;
		try {
			collection = 
				myciteseer.getCollection(collectionID, account);
			if (collection == null) {
				error = true;
				errMsg = "Bad collection ID : \"" + collectionID + 
					"\" collection";
			}
		}catch (DataAccessException e) {
			errMsg = "An error ocurred while trying to get the collection " + 
			    "data.";
			error = true;
		}
		if (!error) {
			List<PaperCollection> colPapers = null;
			try {
				// Obtain paper's id associated with collectionID and owned by
				// the connected user
				colPapers = 
					myciteseer.getUserCollectionPapers(collectionID, account);
				for (PaperCollection pc : colPapers) {
					try {
						Document doc = 
							csxdao.getDocumentFromDB(pc.getPaperID());
							metadataCartDWR.addToCart(request, 
									new Long(doc.getDatum(Document.CLUST_KEY)));
					} catch (Exception e) {
			            e.printStackTrace();
			        }
				}
				return new ModelAndView(new RedirectView("viewCollections"));
			}catch (DataAccessException e) {
				errMsg = "An error ocurred while trying to get the " + "" +
						"collection data.";
				return MCSUtils.errorPage(errMsg);
			}
		}
		else {
			return MCSUtils.errorPage(errMsg);
		}
	} //- handleRequest

} //- class CollectionMetadataCartController
