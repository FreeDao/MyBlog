package cn.picksomething.getMyBlog.customView;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.picksomething.getmyblog.R;

public class Header extends LinearLayout {
	private static final String TAG = "Header";
	private Context context;
	private TextView textView;

	public Header(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		View view = LayoutInflater.from(context).inflate(R.layout.header_layout, null);
		// LinearLayout.LayoutParams lp = new
		// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT);
		// setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.CENTER);
		// 下面两句的顺序不能调换，先addview,然后才能通过findViewById找到该TextView
		this.addView(view);
		textView = (TextView) view.findViewById(R.id.header);
	}

	/**
	 * 
	 * @author caobin
	 * @created 2014年10月21日
	 * @param text
	 */
	public void setTextView(String text) {
		textView.setText(text);
	}

}
