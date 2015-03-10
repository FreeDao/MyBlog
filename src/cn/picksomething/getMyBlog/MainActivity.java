package cn.picksomething.getMyBlog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;

import cn.picksomething.getMyBlog.adapter.MyBaseAdapter;
import cn.picksomething.getMyBlog.http.HttpUtils;
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
    private static final int LOAD_MORE_DATA = 1;
    private static final int STOP_LOAD_DATA = 2;
    private static final String TAG = "caobin";
    private ArrayList<HashMap<String, Object>> data = null;
    private ZrcListView listView = null;
    private Handler mHander = null;
    private MyBaseAdapter myBaseAdapter = null;
    private int pageId = 1;
    private String url = "http://www.picksomething.cn";
    Handler handler = null;


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
                if (pageId != 0) {
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
                String tempurl = "http://www.picksomething.cn/?paged=" + pageId;
                Log.d("caobin", "tempurl = " + tempurl);
                tempData = HttpUtils.getMyBlog(tempurl);
                Log.d(TAG, "tempDate size is " + tempData.size());
                if (null != tempData) {
                    data.addAll(tempData);
                    Log.d(TAG, "date size is " + data.size());
                    msg.what = LOAD_MORE_DATA;
                } else {
                    msg.what = STOP_LOAD_DATA;
                }
                handler.sendMessage(msg);
                tempData = null;
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
                    data = HttpUtils.getMyBlog(url);
                    msg.what = FIRST_REQUEST;
                } catch (Exception e) {
                    msg.what = REQUEST_ERROR;
                    e.printStackTrace();
                }
                super.run();
                handler.sendMessage(msg);
            }
        }.start();
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
                switch (msg.what) {
                    case REQUEST_ERROR:
                        Log.d("caobin", "request error");
                        Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        break;
                    case FIRST_REQUEST:
                        Log.d("caobin", "first request");
                        initListView();
                        break;
                    case LOAD_MORE_DATA:
                        Log.d("caobin", "load more");
                        refreshListView();
                        break;
                    case STOP_LOAD_DATA:
                        Log.d("caobin", "stop load more");
                        listView.stopLoadMore();
                    default:
                        break;
                }
            }
        };
    }

    private void refreshListView() {
        Log.d("caobin", "refreshListView date size is " + data.size());
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
        Log.d("caobin", "date size is " + data.size());
        myBaseAdapter = new MyBaseAdapter(MainActivity.this, data);
        listView.setAdapter(myBaseAdapter);
        listView.refresh(); // 主动下拉刷新
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
        pageId = 1;
    }
}
