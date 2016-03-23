package edu.psu.citeseerx.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.RepositoryEndPoint;
import edu.psu.citeseerx.domain.RepositoryService;
import edu.psu.citeseerx.utility.HttpRESTUtils;

public class RESTRepository implements RepositoryService {

    private RepositoryEndPoint repositoryEndPoint;


    public RepositoryEndPoint getRepositoryEndPoint() {
        return repositoryEndPoint;
    }

    public void setRepositoryEndPoint(RepositoryEndPoint repositoryEndPoint) {
        this.repositoryEndPoint = repositoryEndPoint;
    }

    @Override
    public void storeDocument(Map<String, String> p, String file)
            throws IOException {
        if(!(p.containsKey(Document.DOI_KEY) || p.containsKey(RepositoryService.FILETYPE) || p.containsKey(RepositoryService.REPOSITORYID)))
            return;
        // Excepted by the repository - doi, type
        HttpRESTUtils.uploadFile(repositoryEndPoint, p, RepositoryService.FILEQUERY, file);
    }

    @Override
    public InputStream getDocument(Map<String, String> p) throws IOException {
        if(!(p.containsKey(Document.DOI_KEY) || p.containsKey(RepositoryService.FILETYPE) || p.containsKey(RepositoryService.REPOSITORYID)))
            return null;
        return HttpRESTUtils.getStreamFromHost(repositoryEndPoint, p);
    }

    @Override
    public String[] fileTypes(Map<String, String> p) throws IOException {
        if(!(p.containsKey(Document.DOI_KEY) || p.containsKey(RepositoryService.REPOSITORYID)))
            return null;
        p.put(RepositoryService.QUERY, "filetypes");
        String c = HttpRESTUtils.getFromHost(repositoryEndPoint, p);
        String []types = c.trim().split(",");

       List<String> validTypes = new ArrayList<String>();

        for (String type : types) {
            if (typeLookup.contains(type.toUpperCase())) {
                validTypes.add(type);
            }
        }
        Collections.sort(validTypes);
        String list[] = new String[validTypes.size()];
        validTypes.toArray(list);

        return list;
    }

    public static final String[] supportedTypes = new String[] {
            "PDF", "PS", "DOC", "RTF"
    };

    public static final Set<String> typeLookup = new HashSet<String>();

    static {
        for (String str : supportedTypes) {
            typeLookup.add(str);
        }
    }

    @Override
    public void writeVersion(Document doc) throws IOException {
        String repID = doc.getVersionRepID();
        String doi = doc.getDatum(Document.DOI_KEY, Document.UNENCODED);
        String fileType = RepositoryService.XMLFILE;
        HashMap<String,String> p = new HashMap<String,String>();
        p.put(REPOSITORYID, repID);
        p.put(Document.DOI_KEY, doi);
        p.put(VERSIONKEY, Integer.toString(doc.getVersion()));
        p.put(RepositoryService.FILETYPE, RepositoryService.XMLFILE);
        File tXMLFile = File.createTempFile("citeseerx", "xml");
        FileOutputStream out = new FileOutputStream(tXMLFile);
        doc.toXML(out, Document.INCLUDE_SYS_DATA);
        out.close();
        HttpRESTUtils.uploadFile(repositoryEndPoint, p, RepositoryService.FILEQUERY, tXMLFile.getAbsolutePath());
    }

    @Override
    public void writeXML(Document doc) throws IOException {
        // TODO Auto-generated method stub
        HashMap<String,String> p = new HashMap<String,String>();
        p.put(RepositoryService.FILETYPE, RepositoryService.XMLFILE);
        p.put(Document.DOI_KEY, doc.getDatum(Document.DOI_KEY, Document.UNENCODED));
        DocumentFileInfo finfo = doc.getFileInfo();
        String repID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY,
                DocumentFileInfo.UNENCODED);
        p.put(RepositoryService.REPOSITORYID, repID);
        File tXMLFile = File.createTempFile("citeseerx", "xml");
        FileOutputStream out = new FileOutputStream(tXMLFile);
        doc.toXML(out, Document.INCLUDE_SYS_DATA);
        out.close();
        HttpRESTUtils.uploadFile(repositoryEndPoint, p, RepositoryService.FILEQUERY, tXMLFile.getAbsolutePath());
    }

    @Override
    public Document getDocFromXML(String repID, String relpath)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDocumentContent(Map<String, String> p) throws IOException {
        if(!(p.containsKey(Document.DOI_KEY) || p.containsKey(RepositoryService.REPOSITORYID)))
            return null;
        return HttpRESTUtils.getFromHost(repositoryEndPoint, p);
    }
}
