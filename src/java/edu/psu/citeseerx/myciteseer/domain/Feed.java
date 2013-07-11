package edu.psu.citeseerx.myciteseer.domain;

import java.io.Serializable;

public class Feed implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8951832947365013271L;
    
    public static final String DOC_TYPE = "ds";
    public static final String CIT_TYPE = "cs";
    
    public static boolean isValidType(String type) {
        if (type.equals(DOC_TYPE) || type.equals(CIT_TYPE)) {
            return true;
        } else {
            return false;
        }
    }
    
    private long id;
    private String userid;
    private String title;
    private String desc;
    private String type;
    private String params;
    
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getParams() {
        return params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
}
