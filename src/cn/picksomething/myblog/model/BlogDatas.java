package cn.picksomething.myblog.model;

import java.util.ArrayList;
import java.util.HashMap;

import cn.picksomething.myblog.http.HttpUtils;

/**
 * Created by caobin on 15/4/5.
 */
public class BlogDatas {

    private static final String URL = "http://www.picksomething.cn";

    public static ArrayList<HashMap<String, Object>> blogDatas;

    public static void init() {
        getDatas();
    }

    private static void getDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                blogDatas = HttpUtils.getMyBlog(URL);
            }
        }).start();
    }


}
