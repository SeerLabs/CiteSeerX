package edu.psu.citeseerx.domain;

import java.util.ArrayList;

import org.apache.http.client.utils.URIBuilder;


public class RepositoryEndPoint {

    private String url = null;
    private String username = null;
    private String password = null;
    private String schema = null;
    private String path = null;
    private Object security = null;
    private String securityType = null;

    // Repository Attributes
    private boolean GET = false;
    private boolean GETSecure = false;
    private boolean POST = false;
    private boolean POSTSecure = false;
    private boolean PUT = false;
    private boolean PUTSecure = false;
    private boolean DELETE = false;
    private boolean DELETESecure = false;

    public boolean isGET() {
                    return GET;
    }
    public void setGET(boolean gET) {
                    GET = gET;
    }
    public boolean isPOST() {
                    return POST;
    }
    public void setPOST(boolean pOST) {
                    POST = pOST;
    }
    public boolean isPUT() {
                    return PUT;
    }
    public void setPUT(boolean pUT) {
                    PUT = pUT;
    }
    public boolean isDELETE() {
                    return DELETE;
    }
    public void setDELETE(boolean dELETE) {
                    DELETE = dELETE;
    }

    public String[] supportedMethods() {
                    ArrayList<String> supported = new ArrayList<String>();
                    if(GET) supported.add("GET");
                    if(POST) supported.add("POST");
                    if(DELETE) supported.add("DELETE");
                    if(PUT) supported.add("PUT");
                    String []supports = new String[supported.size()];
                    supported.toArray(supports);
                    return supports;
    }
    
    // Security Attributes
    protected boolean isGETSecure() {
            return GETSecure;
    }
    protected void setGETSecure(boolean gETSecure) {
            GETSecure = gETSecure;
    }
    protected boolean isPOSTSecure() {
            return POSTSecure;
    }
    protected void setPOSTSecure(boolean pOSTSecure) {
            POSTSecure = pOSTSecure;
    }
    protected boolean isPUTSecure() {
            return PUTSecure;
    }
    protected void setPUTSecure(boolean pUTSecure) {
            PUTSecure = pUTSecure;
    }
    protected boolean isDELETESecure() {
            return DELETESecure;
    }
    protected void setDELETESecure(boolean dELETESecure) {
            DELETESecure = dELETESecure;
    }


    public String getUrl() {
            return url;
    }

    public void setUrl(String url) {
            this.url = url;
    }

    public String getUsername() {
            return username;
    }

    public void setUsername(String username) {
            this.username = username;
    }

    public String getPassword() {
            return password;
    }

    public void setPassword(String password) {
            this.password = password;
    }

    public String getSchema() {
            return schema;
    }

    public void setSchema(String schema) {
            this.schema = schema;
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
            this.path = path;
    }

    public Object authenticate(String method , Object context) {
            // the context should provide the scope/access limitations
            // of the object being accessed
            // if this repository does not support any security - blank
            // else according the security, ensure we can communicate
            // Example: Simple http authentication
            //                      context = URIBuilder
            //          add user name and password and send it back

            URIBuilder ub = (URIBuilder) context;
            
            if(method.equals("GET") && isGETSecure()) {
                ub.setParameter("username", username);
                ub.setParameter("password", password);
            }
            else return context;
            if(method.equals("POST") && isPOSTSecure()) {
                ub.setParameter("username", username);
                ub.setParameter("password", password);
            }
            else return context;
            if(method.equals("DELETE") && isDELETESecure()) {
                ub.setParameter("username", username);
                ub.setParameter("password", password);
            }
            else return context;
            if(method.equals("PUT") && isGETSecure()) {
                ub.setParameter("username", username);
                ub.setParameter("password", password);
            }
            else return context;

            return ub;
    }
}