package edu.psu.citeseerx.repository;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.RepositoryService;
import edu.psu.citeseerx.utility.SafeText;

import com.google.common.base.CharMatcher;

import java.io.IOException;
import java.util.HashMap;

public class RepositoryUtilities {
    public static String[] getFileTypes(RepositoryService repositoryService, String doi, String rep) throws IOException {
        HashMap<String,String> fileTypesQuery = new HashMap<String,String>();
        fileTypesQuery.put(Document.DOI_KEY, doi);
        fileTypesQuery.put(RepositoryService.REPOSITORYID, rep);
        return repositoryService.fileTypes(fileTypesQuery);
    }

    public static String getDocumentText(RepositoryService repositoryService, Document doc, boolean bodyFile) throws DocumentUnavailableException, IOException {
        HashMap<String,String> parameters = new HashMap<String,String>();
        String doi = doc.getDatum(Document.DOI_KEY);
        parameters.put(Document.DOI_KEY, doi);
        parameters.put(RepositoryService.REPOSITORYID, doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY));
        if (bodyFile) {
            parameters.put(RepositoryService.FILETYPE, RepositoryService.BODYFILE);
        }
        else {
            parameters.put(RepositoryService.FILETYPE, RepositoryService.TEXTFILE);
        }
        String text = repositoryService.getDocumentContent(parameters);
        text = SafeText.stripBadChars(text);
        text = CharMatcher.JAVA_ISO_CONTROL.replaceFrom(text, " ");
        
        return text;
    }
}