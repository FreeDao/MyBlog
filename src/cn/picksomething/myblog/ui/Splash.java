package cn.picksomething.myblog.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.picksomething.myblog.MainActivity;
import cn.picksomething.myblog.R;
import cn.picksomething.myblog.http.HttpUtils;
import cn.picksomething.myblog.model.BlogDatas;

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
        BlogDatas.init();
        startHome();
        //new Thread(new LoadData()){}.start();
    }

    private class LoadData implements Runnable {

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long endTime = startTime;
            Log.d("caobin", "startTime = " + startTime);
            Message msg = Message.obtain();
            mSortResults = HttpUtils.getMyBlog(url);
            endTime = System.currentTimeMillis();
            Log.d("caobin", "endTime = " + endTime);
            long time = endTime - startTime;
            if (mSortResults.size() == 0) {
                msg.what = LOAD_ERROR;
                handler.sendMessage(msg);
            } else {
                msg.what = LOAD_SUCCESS;
                handler.sendMessage(msg);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_SUCCESS:
                    startHome();
                    break;
                case LOAD_ERROR:
                    showUpdateDialog();
                    break;
                default:
                    break;
            }
        }
    };

    private void startHome() {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
        this.finish();
    }

    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("网络未连接或者不太给力，是否继续等待加载？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }


}
