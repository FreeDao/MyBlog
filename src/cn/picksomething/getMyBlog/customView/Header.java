package cn.picksomething.getMyBlog.customView;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.picksomething.getmyblog.R;

public class Header extends LinearLayout {
	private TextView title,intro;

	public Header(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		View view = LayoutInflater.from(context).inflate(R.layout.header_layout, null);
		this.setGravity(Gravity.CENTER);
		// 下面两句的顺序不能调换，先addview,然后才能通过findViewById找到该TextView
		this.addView(view);
		title = (TextView) view.findViewById(R.id.header);
		intro = (TextView)view.findViewById(R.id.introduction);
	}

	/**
	 * 
	 * @author caobin
	 * @created 2014年10月21日
	 * @param text
	 */
	public void setTextView(String title_str, String intro_str) {
		title.setText(title_str);
		intro.setText(intro_str);
	}

}
