package cn.picksomething.getmyblog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

import cn.picksomething.getmyblog.MainActivity;
import cn.picksomething.getmyblog.R;

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

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent i = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(i);
                Splash.this.finish();
            }
        }, 3000);
    }
}
