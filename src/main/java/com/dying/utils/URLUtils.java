package com.dying.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author daylight
 */
public class URLUtils {
    public static String extractPathAfterTop(String url) {
        if (url == null) {
            return null;
        }

        // 同时匹配 http 和 https
        Pattern pattern = Pattern.compile("^https?://bcpp\\.seestars\\.top/(.+)$");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static void main(String[] args) {
        // 测试 https
        String testUrl1 = "https://bcpp.seestars.top/public/2026-02-03_krThGSdjgKXqPsZ6.jpg";
        String result1 = extractPathAfterTop(testUrl1);
        System.out.println("HTTPS 提取结果: " + result1);

        // 测试 http
        String testUrl2 = "http://bcpp.seestars.top/public/2026-02-03_krThGSdjgKXqPsZ6.jpg";
        String result2 = extractPathAfterTop(testUrl2);
        System.out.println("HTTP 提取结果: " + result2);

        // 测试无效URL
        String testUrl3 = "ftp://bcpp.seestars.top/public/test.jpg";
        String result3 = extractPathAfterTop(testUrl3);
        System.out.println("FTP 提取结果: " + result3);

        // 测试不匹配的域名
        String testUrl4 = "https://example.com/public/test.jpg";
        String result4 = extractPathAfterTop(testUrl4);
        System.out.println("其他域名提取结果: " + result4);
    }
}