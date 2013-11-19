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
package edu.psu.citeseerx.web;

import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.utility.SafeText;

/**
 * Utility class to handle citation contexts
 * @author Isaac Councill
 * @version $Rev$ $Date$
 */
public class CiteContextUtil {

    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
    
    
    private String premark = "=-=";
    private String postmark = "-=-";
    private String startTag = "<em>";
    private String endTag = "</em>";
    
    public void setPremark(String premark) {
        this.premark = premark;
    } //- setPremark
    
    public void setPostmark(String postmark) {
        this.postmark = postmark;
    } //- setPostmark
    
    public void setStartTag(String startTag) {
        this.startTag = startTag;
    } //- setStartTag
    
    public void setEndTag(String endTag) {
        this.endTag = endTag;
    } //- setEndTag
    
    /**
     * Obtain the context of a citation within a paper
     * @param citing
     * @param cited
     * @return the context where cited citation is cited in the citing paper
     */
    public String getContext(Long citing, Long cited) {
        String context = "No context found.";
        try {
            String rContext = citedao.getContext(citing, cited);
            if (rContext != null) {
                context = SafeText.cleanXML(rContext);
                context = context.replace(premark, startTag);
                context = context.replace(postmark, endTag);
                context = "..."+context+"...";
            }
        } catch (Exception e) { };

        return context;
        
    }  //- getContext
    
}  //- class CiteContextUtil
