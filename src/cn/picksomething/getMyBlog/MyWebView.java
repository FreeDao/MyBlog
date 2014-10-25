package cn.picksomething.getMyBlog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.picksomething.getmyblog.R;

public class MyWebView extends Activity {

	private WebView myWebView = null;
	private Intent intent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_layout);
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new MyWebViewClient());
		intent = getIntent();
		String url = intent.getStringExtra("url");
		Log.d("caobin", url);
		myWebView.loadUrl(url);
	}
	
	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        if (Uri.parse(url).getHost().equals("www.picksomething.cn")) {
	            // This is my web site, so do not override; let my WebView load the page
	            return false;
	        }
	        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	        startActivity(intent);
	        return true;
	    }
	}

}
