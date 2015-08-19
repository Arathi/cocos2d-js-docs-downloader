package com.undsf.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Arathi on 2015/06/26.
 */
public abstract class Properties extends java.util.Properties {
    public static final String INTERNAL_ENCODING = "ISO8859-1";
    public abstract String getTargetEncoding();

    @Override
    public String getProperty(String key) {
        String raw = super.getProperty(key);
        String target = null;
        try {
            target = new String(raw.getBytes(INTERNAL_ENCODING), getTargetEncoding());
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return target;
    }

    public synchronized void load(String path) throws IOException{
        InputStream is = null;
        boolean internalResource = false;
        if (path.startsWith("cp:")){
            path = path.substring(3);
            internalResource = true;
        }
        else if (path.startsWith("classpath:")){
            path = path.substring(10);
            internalResource = true;
        }
        if (internalResource){
            is = this.getClass().getResourceAsStream(path);
        }
        else{
            is = new FileInputStream(path);
        }
        this.load(is);
        is.close();
        is = null;
    }
}
