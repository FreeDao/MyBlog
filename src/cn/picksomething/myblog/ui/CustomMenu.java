package cn.picksomething.myblog.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.picksomething.myblog.MyWebView;
import cn.picksomething.myblog.R;
import cn.picksomething.myblog.adapter.SlideMenuAdapter;
import cn.picksomething.myblog.model.SettingItems;

public class CustomMenu extends Fragment {
    private View mView = null;
    private Context mContext = null;
    private ListView sortListView = null;
    private ListView functionListView = null;
    private LinearLayout mInfo = null;
    private List<SettingItems> mSorts = null;
    private List<SettingItems> mFunctions = null;
    private SlideMenuAdapter sortAdapter = null;
    private SlideMenuAdapter functionAdapter = null;
    private final static String myurl = "http://www.picksomething.cn/?page_id=2";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.slide_info_layout, container, false);
            findView();
            initDate();
            bindDate();
        }
        return mView;
    }

    /**
     * @author caobin
     * @created 2014年11月4日
     */
    private void findView() {
        sortListView = (ListView) mView.findViewById(R.id.listview_sort);
        functionListView = (ListView) mView.findViewById(R.id.listview_function);
        mInfo = (LinearLayout) mView.findViewById(R.id.person_info);

    }

    /**
     * @author caobin
     * @created 2014年11月4日
     */
    private void initDate() {
        mInfo.setFocusable(true);
        mInfo.setClickable(true);
        mContext = mView.getContext();
        mSorts = new ArrayList<SettingItems>();
        mFunctions = new ArrayList<SettingItems>();
        String[] sortsArray = mContext.getResources().getStringArray(R.array.sorts_array);
        String[] sortsUrlArray = mContext.getResources().getStringArray(R.array.sorts_url);

        for (int i = 0; i < sortsArray.length; i++) {
            SettingItems sortsItems = new SettingItems(sortsArray[i], sortsUrlArray[i]);
            mSorts.add(sortsItems);
        }
    }

    /**
     * @author caobin
     * @created 2014年11月5日
     */
    private void bindDate() {
        mInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyWebView.class);
                intent.putExtra("url", myurl);
                startActivity(intent);
            }
        });
        sortAdapter = new SlideMenuAdapter(mContext, mSorts);
        functionAdapter = new SlideMenuAdapter(mContext, mFunctions);
        sortListView.setAdapter(sortAdapter);
        functionListView.setAdapter(functionAdapter);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SettingItems items = mSorts.get(position);
                String url = items.getSettingItemsURL();
                Intent intent = new Intent(mContext, MyWebView.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

}
