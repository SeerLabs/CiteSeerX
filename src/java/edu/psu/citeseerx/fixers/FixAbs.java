package edu.psu.citeseerx.fixers;

import edu.psu.citeseerx.utility.CSXConstants;
import edu.psu.citeseerx.updates.UpdateManager;
import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;

import java.io.IOException;
import java.util.List;

/**
 * Throwaway data fixer.
 *
 * @author Isaac Councill
 * @version $Rev$ $Date$
 * @deprecated
 */
public class FixAbs {

    private UpdateManager updateManager;
    
    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    }
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    
    public void fixAll() throws IOException {
        String lastDoi = "10.1.1.1.2482";
        int counter=0;
        
        List<String> l = csxdao.getDOIs(lastDoi, 15000);
        lastDoi = (String)l.get(l.size()-1);
        l = null;
        
        while(true) {
            List<String> dois = csxdao.getDOIs(lastDoi, 5000);
            if (dois.size() == 0) {
                break;
            }
            for (Object o : dois) {
                counter++;
                String doi = (String)o;
                lastDoi = doi;
                try {
                    Document currentDoc = csxdao.getDocumentFromDB(doi, false, true);
                    if (currentDoc.getVersionName() != null &&
                            currentDoc.getVersionName().equals(
                                    CSXConstants.INFERENCE_VERSION)) {
                        Document origDoc = csxdao.getDocVersion(doi, 0);
                        String abs = origDoc.getDatum(Document.ABSTRACT_KEY);
                        String src = origDoc.getSource(Document.ABSTRACT_KEY);
                        currentDoc.setDatum(Document.ABSTRACT_KEY, abs);
                        currentDoc.setSource(Document.ABSTRACT_KEY, src);
                        updateManager.updateDocument(currentDoc, true);
                    }
                } catch (Exception e) {
                    System.err.println("Problem with doc "+lastDoi);
                    e.printStackTrace();
                    System.exit(0);
                }
            }
            System.out.println("Processed "+counter);
        }
    }
    
}
