package edu.psu.citeseerx.myciteseer.web.subscriptions;

import edu.psu.citeseerx.updates.UpdateListener;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.mail.MailManager;
import edu.psu.citeseerx.myciteseer.domain.Account;

import java.util.List;

/**
 * Notifies users monitoring papers of changes in papers metadata.
 * @author Isaac Council
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class MonitorEventHandler implements UpdateListener {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    
    private MailManager mailManager;
    
    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    } //- setMailManager
    
    
    private String csxUrl;
    
    public void setCsxUrl(String csxUrl) {
        this.csxUrl = csxUrl;
    } //- setCsxUrl
    
    
    public void handleUpdate(Document doc) {
        String doi = doc.getDatum(Document.DOI_KEY);
        List<String> uids = myciteseer.getUsersMonitoring(doi);
        if (uids.isEmpty()) {
            return;
        }
        String message = buildMessage(doc);
        for (Object id : uids) {
            Account account = myciteseer.getAccount((String)id);
            String email = account.getEmail();
            mailManager.sendMonitorNotificationMessage(email, message);
        }

    }  //- handleUpdate
    
    
    public String buildMessage(Document doc) {
        
        String doi = doc.getDatum(Document.DOI_KEY);
        
        StringBuilder builder = new StringBuilder();
        builder.append("A paper that you are monitoring has changed.  ");
        builder.append("Please see below for details.\n\n");
        
        builder.append("Document URL: ");
        builder.append(csxUrl+"/viewdoc/summary?doi=");
        builder.append(doi);
        builder.append("\n\nNew metadata:\n\n");
        
        String title = doc.getDatum(Document.TITLE_KEY);
        String authors = buildAuthorList(doc.getAuthors());
        String abs = doc.getDatum(Document.ABSTRACT_KEY);
        String venue = doc.getDatum(Document.VENUE_KEY);
        String year = doc.getDatum(Document.YEAR_KEY);
        String vol = doc.getDatum(Document.VOL_KEY);
        String num = doc.getDatum(Document.NUM_KEY);
        String pages = doc.getDatum(Document.PAGES_KEY);
        String publ = doc.getDatum(Document.PUBLISHER_KEY);
        String pubAddr = doc.getDatum(Document.PUBADDR_KEY);
        String tech = doc.getDatum(Document.TECH_KEY);

        builder.append("TITLE: ");
        builder.append(title);
        builder.append("\n\n");

        builder.append(authors);
        builder.append("\n");
        
        builder.append("ABSTRACT:\n\n");
        builder.append(abs);
        builder.append("\n\n");
        
        builder.append("VENUE: ");
        builder.append(venue);
        builder.append("\n");

        builder.append("YEAR: ");
        builder.append(year);
        builder.append("\n");

        builder.append("VOLUME: ");
        builder.append(vol);
        builder.append("\n");

        builder.append("NUMBER: ");
        builder.append(num);
        builder.append("\n");

        builder.append("PAGES: ");
        builder.append(pages);
        builder.append("\n");

        builder.append("PUBLISHER: ");
        builder.append(publ);
        builder.append("\n");

        builder.append("PUB ADDRESS: ");
        builder.append(pubAddr);
        builder.append("\n");

        builder.append("TECH: ");
        builder.append(tech);
        builder.append("\n");

        return builder.toString();
        
    }  //- buildMessage
    
    
    protected String buildAuthorList(List<Author> list) {
        StringBuilder builder = new StringBuilder();
        builder.append("Author list:\n\n");
        if (list.isEmpty()) {
            builder.append("None\n");
        } else {
            for (Author auth : list) {
                String name = auth.getDatum(Author.NAME_KEY);
                String affil = auth.getDatum(Author.AFFIL_KEY);
                String addr = auth.getDatum(Author.ADDR_KEY);
                
                if (name != null && name.length() > 0) {
                    builder.append(name);
                } else {
                    builder.append("No name");
                }
                builder.append(" | ");
                if (affil != null && affil.length() > 0) {
                    builder.append(affil);
                } else {
                    builder.append("No affiliation");
                }
                builder.append(" | ");
                if (addr != null && addr.length() > 0) {
                    builder.append(addr);
                } else {
                    builder.append("No address");
                }
                builder.append("\n");
            }
        }
        
        return builder.toString();
        
    }  //- buildAuthorList
    
}  //- class MonitorEventHandler
