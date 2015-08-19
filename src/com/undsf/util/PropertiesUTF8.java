package com.undsf.util;

/**
 * Created by Arathi on 2015/06/26.
 */
public class PropertiesUTF8 extends Properties {
    private static final String TARGET_ENCODING = "UTF8";

    @Override
    public String getTargetEncoding(){
        return TARGET_ENCODING;
    }
}
