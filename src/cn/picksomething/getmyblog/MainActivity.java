package cn.picksomething.getmyblog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	private Handler mUIHandler = new Handler();
	private ListView mListview;
	private HandlerThread mThread;
	private Handler mThreadHandler;
	ArrayList<HashMap<String, Object>> arrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListview = (ListView) findViewById(R.id.listview);
		arrayList = new ArrayList<HashMap<String, Object>>();
		startLoadData();
		mThread = new HandlerThread("jsoup");
		mThread.start();
		mThreadHandler = new Handler(mThread.getLooper());
		mThreadHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mUIHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), arrayList,
								R.layout.list, new String[] { "article", "time" }, new int[] { R.id.textView1,
										R.id.textView2 });
						mListview.setAdapter(simpleAdapter);
					}
				},1000);
			}
		});
	}

	private void startLoadData() {
		// TODO Auto-generated method stub
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getTestData();
//				getBlog();
				super.run();
			}
		}.start();
	}
	
	public ArrayList<HashMap<String,Object>> getTestData(){
		for(int i = 0; i < 10; i++){
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("article", i);
			hashMap.put("time", i+1);
			arrayList.add(hashMap);
		}
		
		return arrayList;
		
	}

	public ArrayList<HashMap<String, Object>> getBlog() {
		// ArrayList<HashMap<String, Object>> arrayList = new
		// ArrayList<HashMap<String, Object>>();
		Response response = null;
		try {
			while (response == null || response.statusCode() != 200) {
				response = Jsoup.connect("http://zwgk.mcprc.gov.cn/?classInfoId=27").timeout(3000).execute();
				Thread.sleep(1000);
			}
			Document doc = response.parse();
			Elements notice = doc.getElementsByTag("tbody");
			notice = notice.get(1).getElementsByTag("tr");
			for (Element e : notice) {
				String t = e.child(1).text();
				String time = e.child(2).text();
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("article", t);
				hashMap.put("time", time);
				arrayList.add(hashMap);
			}
			return arrayList;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		return null;
	}
}
