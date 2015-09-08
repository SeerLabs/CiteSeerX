package edu.psu.citeseerx.utility;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.HttpParams;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.psu.citeseerx.domain.RepositoryEndPoint;

public class HttpRESTUtils {

    public static String getFromHost(RepositoryEndPoint repEP, Map<String,String> parameters) {
        HttpGet get = new HttpGet();
        URIBuilder ub = new URIBuilder();

        ub.setScheme(repEP.getSchema()).setHost(repEP.getUrl()).setPath(repEP.getPath());

        for (String param: parameters.keySet()) {
            ub.setParameter(param, parameters.get(param));
        }
        ub = (URIBuilder)repEP.authenticate("GET", ub);
        try {
            HttpClient hclient = new DefaultHttpClient();
            get.setURI(ub.build());
            System.out.println(get.getURI().toString());
            HttpResponse hresponse = hclient.execute(get);

            HttpEntity entity = hresponse.getEntity();
            StringWriter swriter = new StringWriter();
            IOUtils.copy(entity.getContent(), swriter, "UTF-8");
            return swriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            return null;
        }
    }
    
    public static InputStream getStreamFromHost(RepositoryEndPoint repEP, Map<String,String> parameters) {
        HttpGet get = new HttpGet();
        URIBuilder ub = new URIBuilder();

        ub.setScheme(repEP.getSchema()).setHost(repEP.getUrl()).setPath(repEP.getPath());

        for (String param: parameters.keySet()) {
            ub.setParameter(param, parameters.get(param));
        }
        ub = (URIBuilder)repEP.authenticate("GET", ub);
        try {
            HttpClient hclient = new DefaultHttpClient();
            get.setURI(ub.build());
            System.out.println(get.getURI().toString());
            HttpResponse hresponse = hclient.execute(get);

            HttpEntity entity = hresponse.getEntity();
            return entity.getContent();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
            return null;
        }
    }
     
    public static int uploadFile(RepositoryEndPoint rep, Map<String,String> p, String fkey, String filePath) {
        HttpPost post = new HttpPost();
        File uFile = new File(filePath);

        MultipartEntity mpe = new MultipartEntity();
        URIBuilder ub = new URIBuilder();
        ub.setScheme(rep.getSchema());
        ub.setHost(rep.getUrl());
        ub.setPath(rep.getPath());

        try {
            ub = (URIBuilder)rep.authenticate("POST", ub);
            post.setURI(ub.build());
            for (String pK: p.keySet()) {
                    mpe.addPart(pK, new StringBody(p.get(pK)));
            }
            mpe.addPart(fkey, new FileBody(uFile));
            post.setEntity(mpe);
            HttpClient hclient = new DefaultHttpClient();
            HttpResponse hres = hclient.execute(post);
            return hres.getStatusLine().getStatusCode();
        }
            catch (Exception e) {
                return -1;
        }
    }

    public static int postToHost(RepositoryEndPoint rep, Map<String,String> p) {
        HttpPost post = new HttpPost();
        
        MultipartEntity mpe = new MultipartEntity();
        URIBuilder ub = new URIBuilder();
        ub.setScheme(rep.getSchema());
        ub.setHost(rep.getUrl());
        ub.setPath(rep.getPath());

        try {
            ub = (URIBuilder)rep.authenticate("POST", ub);
            post.setURI(ub.build());
            for (String pK: p.keySet()) {
                    mpe.addPart(pK, new StringBody(p.get(pK)));
            }
            post.setEntity(mpe);
            HttpClient hclient = new DefaultHttpClient();
            HttpResponse hres = hclient.execute(post);
            return hres.getStatusLine().getStatusCode();
        }
        catch (Exception e) {
            return -1;
       }

    }
}
