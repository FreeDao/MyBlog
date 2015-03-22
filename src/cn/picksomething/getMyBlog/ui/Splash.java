package cn.picksomething.getmyblog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.picksomething.getmyblog.MainActivity;
import cn.picksomething.getmyblog.R;
import cn.picksomething.getmyblog.http.HttpUtils;

/**
 * Created by caobin on 15/3/17.
 */
public class Splash extends Activity {

    public static final int LOAD_ERROR = 99;
    public static final int LOAD_SUCCESS = 100;

    private ArrayList<HashMap<String, Object>> mSortResults = null;
    private String url = "http://www.picksomething.cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        setContentView(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.dream_splash);
        new Thread(new LoadData()){}.start();
    }

    private class LoadData implements Runnable{

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long endTime = startTime;
            Message msg = Message.obtain();
            mSortResults = HttpUtils.getMyBlog(url);
            endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            if(time < 5000){
                msg.what = LOAD_SUCCESS;
                handler.sendMessage(msg);
            }else{
                msg.what = LOAD_ERROR;
                handler.sendMessage(msg);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOAD_SUCCESS:
                    startHome();
                    break;
                case LOAD_ERROR:
                    startHome();
                    break;
                default:
            }
        }
    };

    private void startHome() {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
        this.finish();
    }


}
