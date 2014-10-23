package cn.picksomething.getMyBlog;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.picksomething.getmyblog.R;
import android.app.Activity;
import android.os.Bundle;

public class SlideInfo extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slide_info_layout);
		
		/*** 初始化侧滑菜单 Begin ***/
        SlidingMenu menu = new SlidingMenu( this);
        menu.setMode(SlidingMenu. LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN );
        menu.setShadowWidthRes(R.dimen. shadow_width);        // 1）
        menu.setShadowDrawable(R.drawable. shadow);           // 2）
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset ); // 3）
        menu.setFadeDegree(0.35f);
        menu.attachToActivity( this, SlidingMenu.SLIDING_CONTENT );
        menu.setMenu(R.layout.activity_main); // 4）
        /*** 初始化侧滑菜单 End ***/
	}

}
