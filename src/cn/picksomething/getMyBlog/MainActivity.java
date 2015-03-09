package cn.picksomething.getMyBlog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

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

import cn.picksomething.getMyBlog.adapter.MyBaseAdapter;
import cn.picksomething.getMyBlog.ui.CustomMenu;
import cn.picksomething.getmyblog.R;
import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;

/**
 * @author caobin
 */
public class MainActivity extends SlidingFragmentActivity {

    private static final int FIRST_REQUEST = 0;
    private static final int REQUEST_ERROR = -1;
    private static final int REFRESH_DATA = 1;
    private ArrayList<HashMap<String, Object>> data = null;
    private ZrcListView listView = null;
    Handler handler = null;
    private Handler mHander = null;
    private MyBaseAdapter myBaseAdapter = null;
    private int pageId = 1;
    private String url = "http://www.picksomething.cn/?paged=" + pageId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomePage);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);
        initSlideMenu(savedInstanceState);
        findViews();
        initDatas();
        startDownloadDatas(url);
        // 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
        //setOffset();
        // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        setHeaderStyle();
        // 设置加载更多的样式（可选）
        setFooterStyle();
        // 设置列表项出现动画（可选）
        setAnimStyle();

        setListeners();
    }

    private void initDatas() {
        handler = getHandler();
        mHander = new Handler();
    }

    private void setListeners() {
        // 下拉刷新事件回调（可选）
        listView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                refresh();
            }
        });

        // 加载更多事件回调（可选）
        listView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                loadMore();
            }
        });
    }

    private void setAnimStyle() {
        listView.setItemAnimForTopIn(R.anim.topitem_in);
        listView.setItemAnimForBottomIn(R.anim.bottomitem_in);
    }

    private void setFooterStyle() {
        SimpleFooter footer = new SimpleFooter(this);
        footer.setCircleColor(0xff33bbee);
        listView.setFootable(footer);
    }

    private void setHeaderStyle() {
        SimpleHeader header = new SimpleHeader(this);
        header.setTextColor(0xff0066aa);
        header.setCircleColor(0xff33bbee);
        listView.setHeadable(header);
    }

    private void setOffset() {
        float density = getResources().getDisplayMetrics().density;
        listView.setFirstTopOffset((int) (50 * density));
    }

    private void findViews() {
        listView = (ZrcListView) findViewById(R.id.zListView);
    }

    private void refresh() {
        mHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pageId == 1) {
                    myBaseAdapter.notifyDataSetChanged();
                    listView.setRefreshSuccess("加载成功"); // 通知加载成功
                    listView.startLoadMore(); // 开启LoadingMore功能
                } else {
                    listView.setRefreshFail("加载失败");
                }
            }
        }, 2 * 1000);
    }

    private void loadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                ArrayList<HashMap<String, Object>> tempData = null;
                pageId++;
                tempData = getMyBlog(url);
                if (null == tempData) {
                    data.addAll(tempData);
                    msg.what = REFRESH_DATA;
                } else {
                    //listView.stopLoadMore();
                }
                handler.sendMessage(msg);
            }
        }).start();
    }


    /**
     * @param savedInstanceState
     * @author caobin
     * @created 2014年11月5日
     */
    private void initSlideMenu(Bundle savedInstanceState) {
        /*** 初始化侧滑菜单 Begin ***/
        setBehindContentView(R.layout.menu_frame);
        getFragmentManager().beginTransaction().replace(R.id.menu_frame, new CustomMenu()).commit();
        SlidingMenu menu = getSlidingMenu();
        menu.setShadowWidthRes(R.dimen.shadow_width); // 1）
        menu.setShadowDrawable(R.drawable.shadow); // 2）
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset); // 3）
        menu.setFadeDegree(0.35f);
        menu.setTouchModeAbove(SlidingMenu.SLIDING_CONTENT);
        /*** 初始化侧滑菜单 End ***/

    }

    /**
     * 使用线程处理联网操作
     *
     * @author caobin
     * @created 2014年10月15日
     */
    private void startDownloadDatas(final String url) {
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    data = getMyBlog(url);
                    msg.what = FIRST_REQUEST;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = REQUEST_ERROR;
                }
                super.run();
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 通过正则表达式匹配首页数据 将匹配的结果放到hashmap中
     *
     * @return
     * @author caobin
     * @created 2014年10月15日
     */
    protected ArrayList<HashMap<String, Object>> getMyBlog(String pageUrl) {
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        String myBlogString = httpGet(pageUrl);
        Pattern p = Pattern.compile("<h1 class=\"entry-title\"><a href=\"(.*?)\" rel=\"bookmark\">(.*?)</a></h1>");
        Matcher m = p.matcher(myBlogString);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("url", mr.group(1));
            map.put("rel", mr.group(2));
            result.add(map);
        }
        return result;
    }

    /**
     * 请求URL，失败时尝试3次
     *
     * @param pageUrl
     * @return 网页内容的html字符串
     * @author caobin
     * @created 2014年10月15日
     */
    private String httpGet(String pageUrl) {
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
    private HttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        // 设定连续超时和读取超时时间
        HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
        HttpConnectionParams.setSoTimeout(httpParams, 3000);
        return new DefaultHttpClient(httpParams);
    }

    /**
     * @return
     * @author caobin
     * @created 2014年10月15日
     */
    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case REQUEST_ERROR:
                        Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        break;
                    case FIRST_REQUEST:
                        initListView();
                        break;
                    case REFRESH_DATA:
                        refreshListView();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void refreshListView() {
        Log.d("caobin","date size is " + data.size());
        myBaseAdapter.notifyDataSetChanged();
        listView.setLoadMoreSuccess();
    }

    /**
     * 将数据传到自定义的Adapter上，并绑定listview
     *
     * @author caobin
     * @created 2014年10月15日
     */
    protected void initListView() {
        Log.d("caobin","date size is " + data.size());
        myBaseAdapter = new MyBaseAdapter(MainActivity.this, data);
        listView.setAdapter(myBaseAdapter);
        //listView.refresh(); // 主动下拉刷新
        /**listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> map = data.get(position);
        String url = (String) (map.get("url"));
        Intent intent = new Intent(MainActivity.this, MyWebView.class);
        intent.putExtra("url", url);
        startActivity(intent);
        }

        });**/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
