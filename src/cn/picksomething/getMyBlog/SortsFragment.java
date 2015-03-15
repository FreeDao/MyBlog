package cn.picksomething.getmyblog;

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

import cn.picksomething.getmyblog.adapter.MyBaseAdapter;
import cn.picksomething.getmyblog.http.HttpUtils;
import zrc.widget.ZrcListView;

/**
 * Created by caobin on 15/3/16.
 */
public class SortsFragment extends Fragment {
    public static final String ARG_SORT_NUMBER = "sort_number";

    private ArrayList<HashMap<String, Object>> mSortResults = null;
    private MyBaseAdapter myBaseAdapter;
    private ZrcListView mZrcListView;
    private Handler handler;

    public SortsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);
        mZrcListView = (ZrcListView) view.findViewById(R.id.zrcListView);
        int i = getArguments().getInt(ARG_SORT_NUMBER);
        String sort_url = "http://www.picksomething.cn/?cat=" + ++i;
        handler = getHandler();
        startDownloadDatas(sort_url);
        return view;
    }

    private void startDownloadDatas(final String url) {
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    mSortResults = HttpUtils.getMyBlog(url);
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
                initListView();
            }
        };
    }

    protected void initListView() {
        myBaseAdapter = new MyBaseAdapter(getActivity(), mSortResults);
        mZrcListView.setAdapter(myBaseAdapter);
        mZrcListView.refresh(); // 主动下拉刷新
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
}
