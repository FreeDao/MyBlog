package cn.picksomething.getmyblog;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.picksomething.getmyblog.adapter.MyBaseAdapter;
import cn.picksomething.getmyblog.http.HttpUtils;
import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;


/**
 * @author caobin
 */
public class MainActivity extends Activity {

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
    private String[] mSorts;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Handler handler = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomePage);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);
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
        mSorts = getResources().getStringArray(R.array.sorts_array);
    }

    private void setListeners() {
        mDrawerList.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_items,mSorts));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListner());
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
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
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
        listView.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
            @Override
            public void onItemClick(ZrcListView parent, View view, int position, long id) {
                HashMap<String, Object> map = data.get(position);
                String url = (String) (map.get("url"));
                Intent intent = new Intent(MainActivity.this, MyWebView.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageId = 1;
    }

    private class DrawerItemClickListner implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            clickItem(position);
        }
    }

    private void clickItem(int position) {
        Fragment fragment = new SortsFragment();
        Bundle args = new Bundle();
        args.putInt(SortsFragment.ARG_SORT_NUMBER,position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();

        mDrawerList.setItemChecked(position,true);
        setTitle(mSorts[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
