package cn.picksomething.myblog;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.picksomething.myblog.adapter.MyBaseAdapter;
import cn.picksomething.myblog.http.HttpUtils;
import cn.picksomething.myblog.model.BlogDatas;
import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;

/**
 * Created by caobin on 15/3/16.
 */
public class SortsFragment extends Fragment {

    private static final int FIRST_REQUEST = 0;
    private static final int REQUEST_ERROR = -1;
    private static final int LOAD_MORE_DATA = 1;
    private static final int STOP_LOAD_DATA = 2;

    public static final String ARG_SORT_URL = "sortUrl";

    private ArrayList<HashMap<String, Object>> mSortResults = null;
    private MyBaseAdapter myBaseAdapter;
    private ZrcListView mZrcListView;
    private Handler handler;
    private int pageId = 1;

    public SortsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);
        mZrcListView = (ZrcListView) view.findViewById(R.id.zrcListView);
        String url = getArguments().getString(ARG_SORT_URL);
        handler = getHandler();
        startDownloadDatas(url);
        mZrcListView.startLoadMore();
        // 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
        //setOffset();
        // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        setHeaderStyle();
        // 设置加载更多的样式（可选）
        setFooterStyle();
        // 设置列表项出现动画（可选）
        setAnimStyle();
        setListener();
        return view;
    }

    private void setFooterStyle() {
        SimpleFooter footer = new SimpleFooter(getActivity());
        footer.setCircleColor(0xff33bbee);
        mZrcListView.setFootable(footer);
    }

    private void setHeaderStyle() {
        SimpleHeader header = new SimpleHeader(getActivity());
        header.setTextColor(0xff0066aa);
        header.setCircleColor(0xff33bbee);
        mZrcListView.setHeadable(header);
    }

    private void setOffset() {
        float density = getResources().getDisplayMetrics().density;
        mZrcListView.setFirstTopOffset((int) (50 * density));
    }

    private void setAnimStyle() {
        mZrcListView.setItemAnimForTopIn(R.anim.topitem_in);
        mZrcListView.setItemAnimForBottomIn(R.anim.bottomitem_in);
    }

    private void setListener() {
        mZrcListView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                refresh();
            }
        });
        mZrcListView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                loadMore();
            }
        });
    }

    private void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pageId != 0) {
                    myBaseAdapter.notifyDataSetChanged();
                    mZrcListView.setRefreshSuccess("加载成功"); // 通知加载成功
                    //mZrcListView.startLoadMore(); // 开启LoadingMore功能
                } else {
                    mZrcListView.setRefreshFail("加载失败");
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
                String tempUrl = "http://www.picksomething.cn/?paged=" + pageId;
                tempData = HttpUtils.getMyBlog(tempUrl);
                if (null != tempData) {
                    mSortResults.addAll(tempData);
                    msg.what = LOAD_MORE_DATA;
                } else {
                    msg.what = STOP_LOAD_DATA;
                }
                handler.sendMessage(msg);

            }
        }).start();
    }

    private void refreshListView() {
        myBaseAdapter.notifyDataSetChanged();
        mZrcListView.setLoadMoreSuccess();
    }

    private void startDownloadDatas(final String url) {
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    if (BlogDatas.blogDatas == null) {
                        Log.d("caobin","BlogDatas == null");
                        mSortResults = HttpUtils.getMyBlog(url);
                    } else {
                        Log.d("caobin","BlogDatas != null");
                        mSortResults = BlogDatas.blogDatas;
                    }
                    msg.what = FIRST_REQUEST;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
                handler.sendMessage(msg);
            }
        }.start();
    }


    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case REQUEST_ERROR:
                        Log.d("caobin", "request error");
                        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
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
                        mZrcListView.stopLoadMore();
                    default:
                        break;
                }
            }
        };
    }

    protected void initListView() {
        myBaseAdapter = new MyBaseAdapter(getActivity(), mSortResults);
        mZrcListView.setAdapter(myBaseAdapter);
        //mZrcListView.refresh(); // 主动下拉刷新
        mZrcListView.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
            @Override
            public void onItemClick(ZrcListView parent, View view, int position, long id) {
                HashMap<String, Object> map = mSortResults.get(position);
                String url = (String) (map.get("url"));
                Intent intent = new Intent(getActivity(), MyWebView.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pageId = 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
