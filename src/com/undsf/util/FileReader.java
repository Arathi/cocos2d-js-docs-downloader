package com.undsf.util;

import java.io.*;

/**
 * Created by Arathi on 2015/4/11.
 */
public class FileReader {
    private File file;

    public FileReader(String path){
        file = new File(path);
    }

    public FileReader(File file){
        this.file = file;
    }

    public boolean exists(){
        if (file==null) return false;
        return file.exists();
    }

    public String readAllAsString(String encoding){
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    public void close(){
        file = null;
    }

}
