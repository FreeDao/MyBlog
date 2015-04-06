package cn.picksomething.myblog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

import cn.picksomething.myblog.MainActivity;
import cn.picksomething.myblog.R;
import cn.picksomething.myblog.model.BlogDatas;

/**
 * Created by caobin on 15/3/17.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        setContentView(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.dream_splash);
        BlogDatas.init();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startHome();
            }
        }, 3000);
    }

    private void startHome() {
        Intent i = new Intent(this, MainActivity.class);
        Splash.this.startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        Splash.this.finish();
    }

}
