package edu.psu.citeseerx.ingestion;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.CheckSum;
import edu.psu.citeseerx.domain.Table;
import edu.psu.citeseerx.domain.TableSet;
import edu.psu.citeseerx.repository.RepositoryMap;
import edu.psu.citeseerx.utility.FileNamingUtils;
import edu.psu.citeseerx.utility.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.List;

/**
 * 
 * @author 
 * @version $Rev$ $Date$
 *
 */
public class TableIngester {
	private CSXDAO csxdao;
	private RepositoryMap repositoryMap;
	protected String repositoryID;
	
    private final static String sep = System.getProperty("file.separator");
	
	public void setRepositoryMap(RepositoryMap repositoryMap) {
        this.repositoryMap = repositoryMap;
    }
    
   
    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }
	
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    private FilenameFilter filter = new FilenameFilter() {
    	public boolean accept(File dir, String filepath) {
    		if(filepath.endsWith(".tbl")) {
    			return true;
    		}
    		return false;
    	}
    };
    
    
    public int importTable(Table tobj, String doi)
    throws Exception {
        	tobj.setPaperIDForTable(doi);
        	csxdao.insertTable(tobj);
        	return 0;
    }
    
    public int importTableSet(String fileName) {
    	
    	TableSet set = new TableSet();
    	try {
    		File tblFile = new File(fileName);
    		set.fromXML(new FileInputStream(tblFile));
    		String doi = "";
    		CheckSum matchingDoc = findDocument(set.getProxyKey().toLowerCase());
    		if(matchingDoc == null) {
    			System.out.println("Cant find document, ingestion failed");
    			return 0;
    		}
    		else {
    				doi = matchingDoc.getDOI();
    				for (Table indiv: set.getTables()) {
    						importTable(indiv, doi);
    				}
    		}
        	
        	String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        	String fullDestDir =
                repositoryMap.getRepositoryPath(repositoryID) + sep + dir;
        	String dest = fullDestDir + sep + doi + ".tbl";
        	File destFile = new File(dest);
            FileUtils.copy(tblFile, destFile);
        	// Move the table file into the repository
        	
        	return 0;
    	}
    	catch(Exception e) {
    		System.out.println("Ingestion Failed");
    		e.printStackTrace();
    		return -1;
    	}
    }
    
    
    protected CheckSum findDocument(String sha1)
    throws SQLException {
    	// SHA1 key returned
        List<CheckSum> chksumDoc = csxdao.getChecksums(sha1);
        if(chksumDoc.isEmpty()) {
        	return null;
        }
        else {
        	return chksumDoc.get(0);
        }
    }
    
    public void ingestDirectories(String[] args) {
        if (args.length <= 0) {
            System.out.println("Please specify one or more directories from " +
                    "which to ingest content");
            System.exit(0);
        }
                
        for (String dir : args) {
           File file = new File(dir);
           if (!file.isDirectory()) {
               System.err.println("Input " + dir +
                       " is not a directory: skipping");
               continue;
           }
           File[] files = file.listFiles(filter);
           for (File source : files) {
               System.out.println("trying "+source.getName());
                  try {
                       importTableSet(source.getAbsolutePath());
                       System.out.println("Done\n");
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
        }
        
       }
}
