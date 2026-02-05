package com.dying.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {
    public static String extractPathAfterTop(String url) {
        if (url == null) {
            return null;
        }
        
        Pattern pattern = Pattern.compile("^http://bcpp\\.seestars\\.top/(.+)$");
        Matcher matcher = pattern.matcher(url);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    public static void main(String[] args) {
        String testUrl = "htt://bcpp.seestars.top/public/2026-02-03_krThGSdjgKXqPsZ6.jpg";
        String result = extractPathAfterTop(testUrl);
        System.out.println("提取结果: " + result);
        // 输出: public/2026-02-03_krThGSdjgKXqPsZ6.jpg
    }
}