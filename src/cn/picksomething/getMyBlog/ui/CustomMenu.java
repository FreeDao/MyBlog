package cn.picksomething.getMyBlog.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.picksomething.getMyBlog.adapter.SlideMenuAdapter;
import cn.picksomething.getMyBlog.model.SettingItems;
import cn.picksomething.getmyblog.R;

public class CustomMenu extends Fragment {
	private View mView = null;
	private Context mContext = null;
	private ListView sortListView = null;
	private ListView functionListView = null;
	private List<SettingItems> mSorts = null;
	private List<SettingItems> mFunctions = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (null == mView) {
			mView = inflater.inflate(R.layout.slide_info_layout, container, false);
			findView();
			initDate();
			bindDate();
		}
		return mView;
	}

	/**
	 * 
	 * @author caobin
	 * @created 2014年11月4日
	 */
	private void findView() {
		// TODO Auto-generated method stub
		sortListView = (ListView) mView.findViewById(R.id.listview_sort);
		functionListView = (ListView) mView.findViewById(R.id.listview_function);

	}

	/**
	 * 
	 * @author caobin
	 * @created 2014年11月4日
	 */
	private void initDate() {
		// TODO Auto-generated method stub
		mContext = mView.getContext();
		mSorts = new ArrayList<SettingItems>();
		mFunctions = new ArrayList<SettingItems>();
		String[] sortsArray = mContext.getResources().getStringArray(R.array.sorts);
		String[] functionsArray = mContext.getResources().getStringArray(R.array.functions);
		for (int i = 0; i < sortsArray.length; i++) {
			SettingItems sortsItems = new SettingItems(sortsArray[i]);
			mSorts.add(sortsItems);
		}
		for (int j = 0; j < functionsArray.length; j++) {
			SettingItems functionsItems = new SettingItems(functionsArray[j]);
			mFunctions.add(functionsItems);
		}
	}

	private void bindDate() {
		// TODO Auto-generated method stub
		sortListView.setAdapter(new SlideMenuAdapter(mContext, mSorts));
		functionListView.setAdapter(new SlideMenuAdapter(mContext, mFunctions));
	}

}
