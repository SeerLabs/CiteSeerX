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
package edu.psu.citeseerx.domain;

import java.io.Serializable;

/**
 * Light-weight bean container for table metadata.
 * 
 * @author Shuyi Zheng
 */
public class ThinTable implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 4153638803175121630L;
    
    private String m_id;
    private String m_caption;
    private String m_footNote;
    private String m_refText;
    private String m_paperid;
    private String m_year;
    private String m_ncites;

    /**
     * @return the year
     */
    public String getYear()
    {
	return m_year;
    }

    /**
     * @param year
     *            the year to set
     */
    public void setYear(String year)
    {
	m_year = year;
    }

    /**
     * @return the ncites
     */
    public String getNcites()
    {
	return m_ncites;
    }

    /**
     * @param ncites
     *            the ncites to set
     */
    public void setNcites(String ncites)
    {
	m_ncites = ncites;
    }

    /**
     * @return the id
     */
    public String getId()
    {
	return m_id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id)
    {
	m_id = id;
    }

    /**
     * @return the caption
     */
    public String getCaption()
    {
	return m_caption;
    }

    /**
     * @param caption
     *            the caption to set
     */
    public void setCaption(String caption)
    {
	m_caption = caption;
    }

    /**
     * @return the footNote
     */
    public String getFootNote()
    {
	return m_footNote;
    }

    /**
     * @param footNote
     *            the footNote to set
     */
    public void setFootNote(String footNote)
    {
	m_footNote = footNote;
    }

    /**
     * @return the refText
     */
    public String getRefText()
    {
	return m_refText;
    }

    /**
     * @param refText
     *            the refText to set
     */
    public void setRefText(String refText)
    {
	m_refText = refText;
    }

    /**
     * @return the paperid
     */
    public String getPaperid()
    {
	return m_paperid;
    }

    /**
     * @param paperid
     *            the paperid to set
     */
    public void setPaperid(String paperid)
    {
	m_paperid = paperid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
	StringBuffer buf = new StringBuffer();

	buf.append("ThinTable\n");
	buf.append("ID: " + m_id + "\n");
	buf.append("Caption: " + m_caption + "\n");
	buf.append("FootNote: " + m_footNote + "\n");
	buf.append("RefText: " + m_refText + "\n");
	buf.append("PaperID: " + m_paperid + "\n");

	return buf.toString();
    }

}
