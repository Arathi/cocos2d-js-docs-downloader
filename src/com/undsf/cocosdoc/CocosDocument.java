package com.undsf.cocosdoc;

import com.undsf.util.Constants;
import com.undsf.util.PairList;
import com.undsf.util.UndHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arathi on 2015/08/19.
 */
public class CocosDocument {
    public static Pattern CSSURL_PATTERN = Pattern.compile("url\\([\'\"](.*?)[\'\"]\\)");

    private UndHttpClient hc;
    private String baseUrl;
    private String cachePath;

    public CocosDocument(String baseUrl, String cachePath){
        hc = new UndHttpClient();
        this.baseUrl = baseUrl;
        this.cachePath = cachePath;
    }

    public void start(){
        String indexHtml = download("index.html");
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        Document doc = Jsoup.parse(indexHtml);
        endTime = System.currentTimeMillis();
        System.out.println("DOM解析时间消耗：" + (endTime - startTime) + "ms");
        //获取css
        Elements links = doc.select("link");
        Set<String> cssFileNames = new HashSet<String>();
        List<String> csses = new ArrayList<String>();
        for (Element link : links){
            String href = link.attr("href");
            if (href==null || href.isEmpty()) continue;
            cssFileNames.add(href);
        }
        System.out.println("通过link标签获取到css如下：");
        for (String cssFileName : cssFileNames){
            System.out.println(cssFileName);
            String cssContent = download(cssFileName);
            csses.add(cssContent);
        }
        //读取css，下载css中url指向的资源
        for (String css : csses){
            //目前假设CSS都在`$cachePath/css/`下
            Matcher matcher = CSSURL_PATTERN.matcher(css);
            int startAt = 0;
            while (matcher.find(startAt)){
                String fileName = matcher.group(1);
                int sharpIndex = fileName.indexOf("#");
                if (sharpIndex>=0){
                    fileName = fileName.substring(0, sharpIndex);
                }
                System.out.println(fileName);
                try {
                    hc.download(
                            cachePath + "css" + Constants.DIR_SEPARATOR + fileName,
                            baseUrl + "css/" + fileName,
                            false,
                            false
                    );
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                startAt = matcher.end();
            }
        }
        //获取js
        Elements scripts = doc.select("script");
        System.out.println("通过link标签获取到script文件链接如下：");
        for (Element script : scripts){
            //System.out.println(script);
            String href = script.attr("src");
            if (href==null || href.isEmpty()) continue;
            System.out.println(href);
            download(href);
        }
        //获取子页面
        Elements as = doc.select("a");
        Set<String> hrefs = new HashSet<String>();
        System.out.println("获取到超链接"+as.size()+"个");
        for (Element a : as){
            String href = a.attr("href");
            if (href==null || href.isEmpty()) continue;
            if (a.text().equals("Codeview")) continue;
            hrefs.add(href);
        }
        System.out.println("获取到不重复超链接" + hrefs.size() + "个");
        for (String href : hrefs){
            System.out.println(href);
            download(href);
        }
        //获取图片
        Elements imgs = doc.select("img");
        Set<String> srcs = new HashSet<String>();
        System.out.println("获取到图片链接"+imgs.size()+"个");
        for (Element img : imgs){
            String src = img.attr("src");
            if (src==null || src.isEmpty()) continue;
            System.out.println(src);
            //download(src);
            try {
                hc.download(cachePath+src, baseUrl+src, false, false);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String download(String resourceName){
        PairList<String,String> params = new PairList<>();
        try {
            String content = hc.requestWithCache(
                    baseUrl + resourceName,
                    cachePath + resourceName,
                    UndHttpClient.METHOD_GET,
                    params
            );
            return content;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        CocosDocument cd = new CocosDocument(
                "http://www.cocos2d-x.org/reference/html5-js/V3.6/",
                "C:\\temp\\cocos2d-js-docs\\"
        );
        cd.start();
    }
}
