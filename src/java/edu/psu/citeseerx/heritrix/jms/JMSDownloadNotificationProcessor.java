/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.heritrix.jms;

import org.archive.crawler.admin.CrawlJob;
import org.archive.crawler.admin.CrawlJob.MBeanCrawlController;
import org.archive.crawler.datamodel.CoreAttributeConstants;
import org.archive.crawler.datamodel.CrawlURI;
import org.archive.crawler.framework.CrawlController;
import org.archive.crawler.framework.Processor;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import edu.psu.citeseerx.utility.CSXConstants;
import edu.psu.citeseerx.utility.FileDigest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * This class serves dual purposes: first, for all downloaded files, a
 * metadata file is generated containing extra information regarding the
 * file such as URL, parent (linking) URL, content-type, date crawled,
 * and the SHA1 file hash.  Metadata files are in XML format.
 * Metadata files have a .met extension and are
 * placed right beside the downloaded file.  Second, this class will call
 * JMSInterface to create a message indicating that the files are ready
 * for ingestion, along with file path information.
 * <br><br>
 * <b>Important:</b> this class assumes that MirrorWriterProcessor is used
 * to download files.
 * 
 * @author Isaac Councill
 * @version $Rev$ $Date$
 *
 */
public class JMSDownloadNotificationProcessor extends Processor {

    /**
     * 
     */
    private static final long serialVersionUID = 7079740803764220235L;
    
    public static final String description = "Creates XML metadata for file"
        +" downloads and sends a JMS message indicating that files are ready"
        +" for ingestion";
    
    public JMSDownloadNotificationProcessor(String name) {
        super(name, description);
    }
    
    
    /**
     * Checks whether a CrawlURI has been downloaded, and if it has,
     * causes a metadata file to be generated, and calls JMSInterface
     * to create a message indicating that the content is ready for
     * ingestion.
     */
    public void innerProcess(CrawlURI curi) {

        if (!curi.isSuccess()) {
            return;
        }
        
        String jobName;
        String jobID;
        
        CrawlController ctrl = this.getController();
        if (ctrl instanceof MBeanCrawlController) {
            MBeanCrawlController mctrl = (MBeanCrawlController)ctrl;
            CrawlJob job = mctrl.getCrawlJob();
            jobName = job.getJobName();
            jobID = job.getUID();
            
        } else {
            System.err.println("JMSCrawlStatusProcessor: no jobID found.");
            return;
        }

        if (curi.containsKey(CoreAttributeConstants.A_MIRROR_PATH)) {

            String relPath =
                curi.getString(CoreAttributeConstants.A_MIRROR_PATH);
            String baseDir = getController().getDisk().getPath();
            String baseSeg = "mirror";

            while ((baseSeg.length() > 1) && baseSeg.endsWith(File.separator)) {
                baseSeg = baseSeg.substring(0, baseSeg.length() - 1);
            }
            if (0 == baseSeg.length()) {
                baseDir = getController().getDisk().getPath();
            } else if ((new File(baseSeg)).isAbsolute()) {
                baseDir = baseSeg;
            } else {
                baseDir = getController().getDisk().getPath() + File.separator
                    + baseSeg;
            }
            
            String metaPath = printMetadata(curi, baseDir, relPath);
            String resourceType = CSXConstants.ARTICLE_TYPE;
            
            String pathPrefix = jobName+"-"+jobID+File.separator+baseSeg;
            String fullRelPath = pathPrefix+File.separator+relPath;
            String fullMetaPath = pathPrefix+File.separator+metaPath;
            
            if (curi.containsKey("resource-type")) {
                resourceType = curi.getString("resource-type");
            }
            
            JMSInterface jms = JMSInterface.getInstance();
            jms.notifyIngestion(jobName, fullRelPath,
                    fullMetaPath, resourceType);
            
        }
        
    }  //- innerProcess
    
    
    /**
     * Prints an XML metadata file beside the downloaded file, with a .met
     * extension and returns the relative path to the metadata file.
     * @param curi uri that has been downloaded
     * @param baseDir base directory for writing
     * @param path relative path to downloaded file 
     * @return relative path to metadata file (null if not created)
     */
    public String printMetadata(CrawlURI curi, String baseDir, String path) {

        Element root = new Element("CrawlData");

        Date crawlDate = new Date(System.currentTimeMillis());
        Element dateElt = new Element("crawlDate");
        dateElt.setText(crawlDate.toString());
        root.addContent(dateElt);
        
        String url = curi.getUURI().toString();
        Element urlElt = new Element("url");
        urlElt.setText(url);
        root.addContent(urlElt);
        
        String pUrl;
        if (curi.containsKey(ParentURLAnnotationProcessor.PARENT_URL_KEY)) {
            pUrl = curi.getString(ParentURLAnnotationProcessor.PARENT_URL_KEY);
            Element pUrlElt = new Element("parentUrl");
            pUrlElt.setText(pUrl);
            root.addContent(pUrlElt);
        }

        String contentType = curi.getContentType();
        if (contentType != null) {
            Element cTypeElt = new Element("contentType");
            cTypeElt.setText(contentType);
            root.addContent(cTypeElt);
        }
        
        File file = new File(baseDir + File.separator + path);
        String sha1sum;
        if (file.exists()) {
            sha1sum = FileDigest.sha1Hex(file);
            Element sha1Elt = new Element("SHA1");
            sha1Elt.setText(sha1sum);
            root.addContent(sha1Elt);
        }
        
        Document doc = new Document(root);
        String relMetaPath = changeExtension(path, ".met");
        String metaPath = baseDir + File.separator + relMetaPath;
        
        try {
            XMLOutputter outputter = new XMLOutputter();
            outputter.output(doc, new FileOutputStream(metaPath));
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        return relMetaPath;

    }  //- printMetadata
    
    
    private String changeExtension(String origName, String newExt) {
        int lastDot = origName.lastIndexOf(".");
        if (lastDot != -1) {
            return origName.substring(0, lastDot) + newExt;
        } else {
            return origName + newExt;
        }
        
    }  //- changeExtension
    
}  //- class JMSDownladNotificationProcessor
