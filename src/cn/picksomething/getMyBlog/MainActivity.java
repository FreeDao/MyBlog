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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.picksomething.getMyBlog.adapter.MyBaseAdapter;
import cn.picksomething.getcsdn.R;

/**
 * @author caobin
 *
 */
public class MainActivity extends Activity {

	private List<HashMap<String, Object>> data = null;
	private ListView listView = null;
	private Handler handler = null;
	private MyBaseAdapter myBaseAdapter = null;
	int[] colors = { Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN };
	private final String BLOGURL = "http://www.picksomething.cn/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.list);
		handler = getHandler();
		threadStart();
	}

	/**
	 * 使用线程处理联网操作
	 * 
	 * @author caobin
	 * @created 2014年10月15日
	 */
	private void threadStart() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		String csdnString = httpGet(BLOGURL);
		// Pattern p =
		// Pattern.compile("<a href=\"(.*?)\" rel=\"bookmark\">\"(.*?)\"</a>");
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		listView.setAdapter(myBaseAdapter);
		// 设置间隔线，Orientation.RIGHT_LEFT表示颜色渐变的方向
		// listView.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT,
		// colors));
		// 设置间距高度
		listView.setDividerHeight(10);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, Object> map = data.get(position);
				String url = (String) (map.get("url"));
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
			}

		});
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
