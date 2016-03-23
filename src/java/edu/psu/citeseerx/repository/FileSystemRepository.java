package edu.psu.citeseerx.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.RepositoryService;
import edu.psu.citeseerx.utility.FileNamingUtils;
import edu.psu.citeseerx.utility.FileUtils;
import edu.psu.citeseerx.utility.SafeText;

public class FileSystemRepository implements RepositoryService {

    private RepositoryMap repMap;
    private CSXDAO csxdao;

    public RepositoryMap getRepMap() {
        return repMap;
    }

    public void setRepMap(RepositoryMap repMap) {
        this.repMap = repMap;
    }

    public CSXDAO getCsxdao() {
        return csxdao;
    }

    public void setCsxdao(CSXDAO csxdao) {
        this.csxdao = csxdao;
    }

    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocFromXML(java.lang.String, java.lang.String)
     */
    public Document getDocFromXML(String repID, String relPath)
    throws IOException {

        String path = repMap.buildFilePath(repID, relPath);
        FileInputStream in = new FileInputStream(path);

        Document doc = new Document();
        doc.fromXML(in);

        in.close();
        return doc;

    }  //- getDocFromXML


    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocVersion(java.lang.String, int)
     */
    public Document getDocVersion(String doi, int version) throws IOException {

        String repID, path;
        String name = null;
        boolean deprecated = false;
        boolean spam = false;

        if (version > 0) {

            Document holder = csxdao.getDocVersion(doi, version);
            if (holder == null) return null;

            repID = holder.getVersionRepID();
            path = holder.getVersionPath();
            name = holder.getVersionName();
            deprecated = holder.isDeprecatedVersion();
            spam = holder.isSpamVersion();

        } else {
            version = 0;
            repID = csxdao.getRepositoryID(doi);
            path = FileNamingUtils.buildXMLPath(doi);
        }

        Document doc = getDocFromXML(repID, path);

        doc.setVersion(version);
        doc.setVersionName(name);
        doc.setVersionRepID(repID);
        doc.setVersionPath(path);
        doc.setVersionDeprecated(deprecated);
        doc.setVersionSpam(spam);

        return doc;

    }  //- getVersion


    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getDocVersion(java.lang.String, java.lang.String)
     */
    public Document getDocVersion(String doi, String name) throws IOException {
        Document holder = csxdao.getDocVersion(doi, name);
        if (holder == null) return null;

        Document doc = getDocFromXML(holder.getVersionRepID(),
                holder.getVersionPath());

        doc.setVersion(holder.getVersion());
        doc.setVersionName(holder.getVersionName());
        doc.setVersionRepID(holder.getVersionRepID());
        doc.setVersionPath(holder.getVersionPath());
        doc.setVersionDeprecated(holder.isDeprecatedVersion());
        doc.setVersionSpam(holder.isSpamVersion());

        return doc;

    }  //- getVersion


    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#writeXML(edu.psu.citeseerx.domain.Document)
     */
    public void writeXML(Document doc) throws IOException {

        String doi = doc.getDatum(Document.DOI_KEY, Document.UNENCODED);

        DocumentFileInfo finfo = doc.getFileInfo();
        String repID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY,
                DocumentFileInfo.UNENCODED);
        String relPath = FileNamingUtils.buildXMLPath(doi);
        String path = repMap.buildFilePath(repID, relPath);

        FileOutputStream out = new FileOutputStream(path);
        doc.toXML(out, Document.INCLUDE_SYS_DATA);
        out.close();

    }  //- writeXML


    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#writeVersion(edu.psu.citeseerx.domain.Document)
     */
    public void writeVersion(Document doc) throws IOException {

        String repID = doc.getVersionRepID();
        String relPath = doc.getVersionPath();
        String path = repMap.buildFilePath(repID, relPath);

        FileOutputStream out = new FileOutputStream(path);
        doc.toXML(out, Document.INCLUDE_SYS_DATA);
        out.close();

    }  //- writeVersion


    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getFileInputStream(java.lang.String, java.lang.String, java.lang.String)
     */
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException {

        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String fn = doi+"."+type;
        String relPath = dir+System.getProperty("file.separator")+fn;
        String path = repMap.buildFilePath(repID, relPath);

        return new FileInputStream(path);

    }  //- getFileInputStream



    @Override
    public void storeDocument(Map<String, String> p, String filePath)
            throws IOException {
        if(!(p.containsKey(Document.DOI_KEY) || p.containsKey(RepositoryService.FILETYPE) || p.containsKey(RepositoryService.REPOSITORYID)))
                return;
        String doi = p.get(Document.DOI_KEY);
        String type = p.get(RepositoryService.FILETYPE);
        String repID = p.get(RepositoryService.REPOSITORYID);
        String fn = doi+"."+ type;
        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String relPath = dir+System.getProperty("file.separator")+fn;
        String path = repMap.buildFilePath(repID, relPath);
        File fileObj = new File(relPath);
        File fromObjc = new File(filePath);
        try {
            IOUtils.copy(new FileInputStream(filePath), new FileOutputStream(fileObj));
        } catch(IOException e) {
            e.printStackTrace();
        }
        // TODO Auto-generated method stub

    }

    @Override
    public InputStream getDocument(Map<String, String> p) throws IOException, DocumentUnavailableException {
        if(!(p.containsKey(Document.DOI_KEY) || p.containsKey(RepositoryService.FILETYPE) || p.containsKey(RepositoryService.REPOSITORYID)))
            return null;
        String doi = p.get(Document.DOI_KEY);
        String type = p.get(RepositoryService.FILETYPE);
        String repID = p.get(RepositoryService.REPOSITORYID);

        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String fn = doi+"."+type;
        String relPath = dir+System.getProperty("file.separator")+fn;
        String path = repMap.buildFilePath(repID, relPath);
        if(new File(path).exists()) {
            return new FileInputStream(path);
        }
        else {
            throw new DocumentUnavailableException();
        }

    }

    @Override
    public String[] fileTypes(Map<String, String> p) throws IOException {
        if(!(p.containsKey(Document.DOI_KEY) || p.containsKey(RepositoryService.REPOSITORYID)))
            return null;
        String doi = p.get(Document.DOI_KEY);
        String repID = p.get(RepositoryService.REPOSITORYID);

        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String path = repMap.buildFilePath(repID, dir);

        String[] files = new File(path).list();
        List<String> types = new ArrayList<String>();

        if (files == null) {
            String list[] = new String[types.size()];
            types.toArray(list);
            return list;
        }

        for (String file : files) {
            String ext = FileUtils.getExtension(file);
            ext = ext.substring(1);
            if (typeLookup.contains(ext.toUpperCase())) {
                types.add(ext);
            }
        }
        Collections.sort(types);
        String list[] = new String[types.size()];
        types.toArray(list);
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

    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileSysDAO#getFileTypes(java.lang.String, java.lang.String)
     */
    public List<String> getFileTypes(String doi, String repID)
    throws IOException {

        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String path = repMap.buildFilePath(repID, dir);

        String[] files = new File(path).list();
        List<String> types = new ArrayList<String>();

        if (files == null) {
            return types;
        }

        for (String file : files) {
            String ext = FileUtils.getExtension(file);
            ext = ext.substring(1);
            if (typeLookup.contains(ext.toUpperCase())) {
                types.add(ext);
            }
        }
        Collections.sort(types);
        return types;
    }  //- getFileTypes


    @Override
    public String getDocumentContent(Map<String, String> p)
            throws IOException {
        if(!(p.containsKey(Document.DOI_KEY) || p.containsKey(RepositoryService.REPOSITORYID)))
            return null;

        String doi = p.get(Document.DOI_KEY);
        if (doi == null) {
            return null;
        }
        FileInputStream ins = null;
        BufferedReader reader = null;
        try {
            ins = (FileInputStream)getDocument(p);

        } catch (DocumentUnavailableException e) { // No body file
            p.put(RepositoryService.FILETYPE, RepositoryService.TEXTFILE);
            try {
                ins = (FileInputStream)getDocument(p);
            }
            catch(DocumentUnavailableException f) {} // No text file either - we give up
        }
        try {
            reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
            StringWriter sw = new StringWriter();
            IOUtils.copy(reader, sw);
            String text = sw.toString();
            return text;

        } catch (IOException e) {
            throw(e);
        } finally {
            try { reader.close(); } catch (Exception e) { }
            try { ins.close(); } catch(Exception e) { }
        }
    }
}
