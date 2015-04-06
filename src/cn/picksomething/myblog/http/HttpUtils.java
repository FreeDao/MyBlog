package cn.picksomething.myblog.http;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caobin on 15-3-10.
 */
public class HttpUtils {

    /**
     * 通过正则表达式匹配首页数据 将匹配的结果放到hashmap中
     *
     * @return
     * @author caobin
     * @created 2014年10月15日
     */
    public static ArrayList<HashMap<String, Object>> getMyBlog(String pageUrl) {
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        String myBlogString = httpGet(pageUrl);
        String regxName = "<h1 class=\"title\"><a href=\".*?\">(.*?)</a></h1>";
        String regxURL = "<h1 class=\"title\"><a href=\"(.*?)\">.*?</a></h1>";
        String regxDate = "<span class=\"entry-date\"><i class=\"icon-time\" ></i>(.*?)</span>";
        ArrayList<String> blogName = matchResult(myBlogString, regxName);
        ArrayList<String> blogURL = matchResult(myBlogString, regxURL);
        ArrayList<String> blogDate = matchResult(myBlogString, regxDate);
        for (int i = 0; i < blogName.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", blogName.get(i));
            map.put("url", blogURL.get(i));
            map.put("date", blogDate.get(i));
            result.add(map);
        }
        return result;
    }

    private static ArrayList<String> matchResult(String myBlogString, String regxString) {
        ArrayList<String> matchArray = new ArrayList<String>();
        Pattern p = Pattern.compile(regxString);
        Matcher m = p.matcher(myBlogString);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            matchArray.add(mr.group(1));
            Log.d("caobin", "match result = " + mr.group(1));
        }
        return matchArray;
    }

    /**
     * 请求URL，失败时尝试3次
     *
     * @param pageUrl
     * @return 网页内容的html字符串
     * @author caobin
     * @created 2014年10月15日
     */
    private static String httpGet(String pageUrl) {
        final int RETRY_TIME = 3;
        HttpClient httpClient = null;
        HttpGet httpGet = null;

        String responseBody = "";
        int time = 0;
        do {
            httpClient = getHttpClient();
            httpGet = new HttpGet(pageUrl);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    // 用UTF-8编码转化为字符串
                    byte[] bResult = EntityUtils.toByteArray(response.getEntity());
                    if (bResult != null) {
                        responseBody = new String(bResult, "utf-8");
                    }
                }
                break;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                e.printStackTrace();
            } finally {
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        return responseBody;
    }

    /**
     * @return
     * @author caobin
     * @created 2014年10月15日
     */
    private static HttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        // 设定连续超时和读取超时时间
        HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
        HttpConnectionParams.setSoTimeout(httpParams, 3000);
        return new DefaultHttpClient(httpParams);
    }


}
