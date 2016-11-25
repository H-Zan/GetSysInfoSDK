package com.admai.sdk.util;


import android.util.DisplayMetrics;

import com.admai.sdk.mai.MaiManager;
import com.admai.sdk.util.log.L;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlProcessor {

    /**
     * SDK 19及以上调用
     *
     * @param rawHtml html内容
     * @return
     */
    public static String processRawHtml(String rawHtml,int height) {
        StringBuffer processedHtml = new StringBuffer(rawHtml);
        removeMraidTag(processedHtml);

        String ls = System.getProperty("line.separator");
        if (!checkSanity(rawHtml, processedHtml, ls)) {
            return null;
        }
        // Add meta and style tags to head tag.
        String metaTag = "<meta name='viewport' content='width=device-width, minimum-scale=0.1, maximum-scale=10, user-scalable=yes' />";
//        String metaTag = "<meta name='viewport' content='width=" + 320 + ",target-densitydpi=device-dpi, minimum-scale=0.1, maximum-scale=10, user-scalable=yes' />";

        addTag(processedHtml, ls, metaTag,height);
        return processedHtml.toString();
    }

    /**
     * SDK 19以下调用
     *
     * @param rawHtml    html内容
     * @param mediaWidth 物料宽度
     * @return
     */
    public static String processRawHtml(String rawHtml, String mediaWidth,int height) {
        StringBuffer processedHtml = new StringBuffer(rawHtml);
        removeMraidTag(processedHtml);

        String ls = System.getProperty("line.separator");
        if (!checkSanity(rawHtml, processedHtml, ls)) {
            L.e("MRAIDHtmlProcessor", "checkSanity is false");
            return null;
        }
        // Add meta and style tags to head tag.
        String metaTag = "<meta name='viewport' content='width=" + mediaWidth + ",target-densitydpi=device-dpi, minimum-scale=0.1, maximum-scale=10, user-scalable=yes' />";

        addTag(processedHtml, ls, metaTag,height);
        return processedHtml.toString();
    }

    private static void addTag(StringBuffer processedHtml, String ls,
                               String metaTag,int height) {
        String regex;
        Pattern pattern;
        Matcher matcher;
        //判断android版本号，以19为分割条件
//        String styleTag = "<style>"
//                + ls
//                + "body {margin:0; padding:0;}"
//                + ls
//                + "*:not(input) { -webkit-touch-callout:none; -webkit-user-select:none; -webkit-text-size-adjust:none; }"
//                + ls + "</style>";
        String styleTag = "<style>"
                + ls
                + "body {margin:0; padding:0;}"
                + ls
                + "*:not(input) { -webkit-touch-callout:none; -webkit-user-select:none; -webkit-text-size-adjust:none; }"
                + ls + "img{max-width: 100%;width: 100%; height:"+px2dip(height)+""+";}</style>";


        regex = "<head[^>]*>";
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(processedHtml);
        int idx = 0;
        while (matcher.find(idx)) {
            processedHtml.insert(matcher.end(), ls + metaTag + ls + styleTag);
            idx = matcher.end();
        }
    }


    private static Boolean checkSanity(String rawHtml, StringBuffer processedHtml, String ls) {
        String regex;
        Pattern pattern;
        Matcher matcher;

        // Add html, head, and/or body tags as needed.
        boolean hasHtmlTag = (rawHtml.toLowerCase().indexOf("<html") != -1);
        boolean hasHeadTag = (rawHtml.toLowerCase().indexOf("<head") != -1);
        boolean hasBodyTag = (rawHtml.toLowerCase().indexOf("<body") != -1);

        // basic sanity checks
        if ((!hasHtmlTag && (hasHeadTag || hasBodyTag))
                || (hasHtmlTag && !hasBodyTag)) {
            return false;
        }


        if (!hasHtmlTag) {
            processedHtml.insert(0, "<html>" + ls + "<head>" + ls + "</head>"
                    + ls + "<body>" + ls);
            processedHtml.append("</body>" + ls + "</html>");
        } else if (!hasHeadTag) {
            // html tag exists, head tag doesn't, so add it
            regex = "<html[^>]*>";
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(processedHtml);
            int idx = 0;
            while (matcher.find(idx)) {
                processedHtml.insert(matcher.end(), ls + "<head>" + ls
                        + "</head>");
                idx = matcher.end();
            }
        }
        return true;
    }

    private static void removeMraidTag(StringBuffer processedHtml) {
        // Remove the mraid.js script tag.
        // We expect the tag to look like this:
        // <script src='mraid.js'></script>
        // But we should also be to handle additional attributes and whitespace
        // like this:
        // <script type = 'text/javascript' src = 'mraid.js' > </script>

        String regex;
        Pattern pattern;
        Matcher matcher;

        // Remove the mraid.js script tag.
        regex = "<script\\s+[^>]*\\bsrc\\s*=\\s*([\\\"\\\'])mraid\\.js\\1[^>]*>\\s*</script>\\n*";
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(processedHtml);
        String mraidTag ="<script src=\"file:///android_asset/com.our.assets/mraid.js\" type=\"text/javascript\"></script>";
        if (matcher.find()) {

//          processedHtml.delete(matcher.start(), matcher.end());
            processedHtml.replace(matcher.start(),matcher.end(),mraidTag);

        }

        L.e("MRAIDHtmlProcessor", processedHtml);
    }
    private static int px2dip(int pixels) {  // px-->dip 变小

        return pixels * DisplayMetrics.DENSITY_DEFAULT
                   / MaiManager.outMetrics.densityDpi;
    }
}
