package cn.picksomething.getMyBlog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.picksomething.getMyBlog.adapter.MyBaseAdapter;
import cn.picksomething.getMyBlog.ui.CustomMenu;
import cn.picksomething.getmyblog.R;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * @author caobin
 *
 */
public class MainActivity extends SlidingFragmentActivity {

	private List<HashMap<String, Object>> data = null;
	private ListView listView = null;
	private Handler handler = null;
	private MyBaseAdapter myBaseAdapter = null;
	private final static String BLOGURL = "http://www.picksomething.cn/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.HomePage);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);
		initSlideMenu(savedInstanceState);
		listView = (ListView) findViewById(R.id.list);
		handler = getHandler();
		threadStart();
	}

	/**
	 * 
	 * @author caobin
	 * @created 2014年11月5日
	 * @param savedInstanceState
	 */
	private void initSlideMenu(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
	private void threadStart() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				try {
					data = getMyBlog();
					msg.what = data.size();
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				super.run();
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 
	 * 通过正则表达式匹配首页数据 将匹配的结果放到hashmap中
	 * 
	 * @author caobin
	 * @created 2014年10月15日
	 * @return
	 */
	protected List<HashMap<String, Object>> getMyBlog() {
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		String csdnString = httpGet(BLOGURL);
		Pattern p = Pattern.compile("<h1 class=\"entry-title\">\\n.*?<a href=\"(.*?)\" rel=\"bookmark\">(.*?)</a>");
		Matcher m = p.matcher(csdnString);
		while (m.find()) {
			MatchResult mr = m.toMatchResult();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("url", mr.group(1));
			map.put("rel", mr.group(2));
			result.add(map);
		}
		return result;
	}

	/**
	 * 请求URL，失败时尝试3次
	 * 
	 * @author caobin
	 * @created 2014年10月15日
	 * @param url
	 * @return 网页内容的html字符串
	 */
	private String httpGet(String url) {
		final int RETRY_TIME = 3;
		HttpClient httpClient = null;
		HttpGet httpGet = null;

		String responseBody = "";
		int time = 0;
		do {
			httpClient = getHttpClient();
			httpGet = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == 200) {
					// 用UTF-8编码转化为字符串
					byte[] bResult = EntityUtils.toByteArray(response.getEntity());
					if (bResult != null) {
						responseBody = new String(bResult, "utf-8");
					}
				}
				break;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					continue;
				}
				e.printStackTrace();
			} finally {
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return responseBody;
	}

	/**
	 * 
	 * @author caobin
	 * @created 2014年10月15日
	 * @return
	 */
	private HttpClient getHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		// 设定连续超时和读取超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		return new DefaultHttpClient(httpParams);
	}

	/**
	 * 
	 * @author caobin
	 * @created 2014年10月15日
	 * @return
	 */
	private Handler getHandler() {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what < 0) {
					Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
				} else {
					initListView();
				}
			}
		};
	}

	/**
	 * 将数据传到自定义的Adapter上，并绑定listview
	 * 
	 * @author caobin
	 * @created 2014年10月15日
	 */
	protected void initListView() {
		myBaseAdapter = new MyBaseAdapter(MainActivity.this, data);
		// Header headerView = new Header(MainActivity.this);
		// headerView.setTextView("一路追梦想", "Android,Linux,Python爱好者");
		// listView.addHeaderView(headerView, null, false);
		listView.setAdapter(myBaseAdapter);
		// 设置间距高度
		// listView.setDividerHeight(30);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// position -= 1;
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
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
