package cn.picksomething.getMyBlog.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.picksomething.getmyblog.R;

public class CustomMenu extends Fragment{
	private View mView = null;
	private ListView sortListView = null;
	private ListView functionListView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(null == mView){
			mView = inflater.inflate(R.layout.slide_info_layout, container, false);
		}
		findView();
		initDate();
		bindDate();
		return mView;
	}

	private void findView() {
		// TODO Auto-generated method stub
		sortListView = (ListView)mView.findViewById(R.id.listview_sort);
		functionListView = (ListView)mView.findViewById(R.id.listview_function);
	}

	private void initDate() {
		// TODO Auto-generated method stub
		
	}

	private void bindDate() {
		// TODO Auto-generated method stub
		
	}

}
