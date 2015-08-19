package com.undsf.util;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.*;

/**
 * Created by Arathi on 2015/04/10.
 */
public class UndHttpClient {
    public static UndHttpClient instance = null;
    public static final String DEFAULT_PROXY_IP = "127.0.0.1";
    public static final int DEFAULT_PROXY_PORT = 8087;
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    protected CloseableHttpClient client;
    protected HttpHost proxy = null;
    protected RequestConfig config = null;

    public static UndHttpClient getInstance() {
        if (instance == null){
            instance = new UndHttpClient();
        }
        return instance;
    }

    public static void initInstance(){
        instance = new UndHttpClient();
    }

    public static void initInstance(boolean useProxy){
        instance = new UndHttpClient(useProxy);
    }

    public static void initInstance(String proxyIp, int proxyPort){
        instance = new UndHttpClient(proxyIp, proxyPort);
    }

    public UndHttpClient(){
        initClient();
        java.util.Properties prop = new PropertiesUTF8();
        try {
            prop.load(UndHttpClient.class.getResourceAsStream("/httpclient.ini"));
            boolean useProxy = Boolean.parseBoolean(prop.getProperty("proxy.enable", "false"));
            String proxyIp = prop.getProperty("proxy.ip", DEFAULT_PROXY_IP);
            int proxyPort = Integer.parseInt(prop.getProperty("proxy.port", DEFAULT_PROXY_PORT+""));
            if (useProxy) {
                initProxy(proxyIp, proxyPort);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public UndHttpClient(boolean useProxy){
        initClient();
        if (useProxy){
            initProxy(DEFAULT_PROXY_IP, DEFAULT_PROXY_PORT);
        }
    }

    public UndHttpClient(String proxyIp, int proxyPort){
        initClient();
        initProxy(proxyIp, proxyPort);
    }

    public void initClient(){
        client = HttpClients.createDefault();
    }

    public void initProxy(String proxyIp, int proxyPort){
        proxy = new HttpHost(proxyIp, proxyPort, "http");
        config = RequestConfig.custom().setProxy(proxy).build();
    }

    public String getRequest(String url) throws IOException{
        String html = "";
        CloseableHttpResponse response = null;
        try{
            HttpGet request = new HttpGet(url);
            if (proxy!=null && config!=null) request.setConfig(config);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new IOException("获取到错误的状态码："+response.getStatusLine().getStatusCode());
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("无法获取到Entity");
            }
            html = EntityUtils.toString(entity);
        }
        catch (IOException e){
            throw e;
        }
        finally {
            if (response!=null){
                response.close();
            }
        }
        return html;
    }

    public String getRequest(String url, PairList<String,String> params) throws IOException{
        String urlWithParam = (url.indexOf("?")>=0)?url:(url+"?");
        if (params!=null && params.size()>0){
            boolean firstParamFlag = true;
            for (Pair<String,String> param : params){
                if (!firstParamFlag){
                    urlWithParam += "&";
                }
                else{
                    firstParamFlag = false;
                }
                String key = param.getKey();
                String value = param.getValue();
                urlWithParam += key+"="+value;
            }
        }
        return getRequest(urlWithParam);
    }

    public String postRequest(String url, PairList<String, String> params) throws IOException{
        String html = "";
        CloseableHttpResponse response = null;
        try{
            HttpPost request = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            //组装nvps
            if (params != null && params.size()>0){
                for (Pair<String, String> param : params){
                    nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }
            try {
                request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (proxy!=null && config!=null) request.setConfig(config);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new IOException("获取到错误的状态码");
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("无法获取到Entity");
            }
            html = EntityUtils.toString(entity);
            response.close();
        }
        catch (IOException e){
            throw e;
        }
        finally {
            if (response!=null){
                response.close();
            }
        }
        return html;
    }

    public String requestWithCache(String url, String path, String method, PairList<String, String> params) throws IOException{
        String html = "";
        //先检查cache是否存在
        FileReader fileReader = new FileReader(path);
        //如果不存在，联网
        if (!fileReader.exists()){
            String METHOD = method.toUpperCase();
            if (METHOD.equals(METHOD_GET)){
                html = getRequest(url, params);
            }
            else if (METHOD.equals(METHOD_POST)){
                html = postRequest(url, params);
            }
            //检查目录是否存在，如果不存在，则创建
            int lastDirSeparatorDos = path.lastIndexOf("\\");
            int lastDirSeparatorUnix = path.lastIndexOf("/");
            int lastDirSeparatorIndex = (lastDirSeparatorDos>lastDirSeparatorUnix)?lastDirSeparatorDos:lastDirSeparatorUnix;
            String directoryName = "";
            if (lastDirSeparatorIndex>0){
                directoryName = path.substring(0, lastDirSeparatorIndex);
            }
            File directory = new File(directoryName);
            if (!directory.exists()){
                System.err.println(directory+"不存在！正在创建……");
                directory.mkdir();
            }
            //获取到html后，写入文件
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(html);
            fileWriter.flush();
            fileWriter.close();
        }
        else{
            html = fileReader.readAllAsString("UTF-8");
        }
        fileReader.close();
        return html;
    }

    public boolean download(String path, String url, boolean omissionFileName, boolean overwrite) throws IOException{
        CloseableHttpResponse response = null;
        try{
            String parent = "";
            String fileFullPath = "";
            if (omissionFileName){
                int indexOfSlash = url.lastIndexOf('/');
                String fileName = url;
                if (indexOfSlash>=0){
                    fileName = url.substring(indexOfSlash);
                }
                parent = path;
                fileFullPath = parent + Constants.DIR_SEPARATOR + fileName;
            }
            else{
                int indexOfSlash = path.lastIndexOf(Constants.DIR_SEPARATOR);
                parent = path.substring(0, indexOfSlash);
                fileFullPath = path;
            }

            File file = new File(fileFullPath);
            if (file.exists()){
                //System.err.println(fileFullPath+"已存在！");
                return false;
            }
            System.out.println("开始下载："+url+" 到 "+fileFullPath);

            //建立目录
            File dir = new File(parent);
            if (!dir.exists()){
                if ( dir.mkdirs()==false ){
                    return false;
                }
            }
            HttpGet request = new HttpGet(url);
            if (proxy!=null && config!=null) request.setConfig(config);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                throw new IOException("获取到错误的状态码");
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("无法获取到Entity");
            }
            FileOutputStream fos = new FileOutputStream(fileFullPath);
            entity.writeTo(fos);
            fos.close();
            //html = EntityUtils.toString(entity);
        }
        catch (IOException e){
            throw e;
        }
        finally {
            if (response!=null){
                response.close();
            }
        }
        return false;
    }

    public boolean close(){
        try{
            client.close();
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

}
