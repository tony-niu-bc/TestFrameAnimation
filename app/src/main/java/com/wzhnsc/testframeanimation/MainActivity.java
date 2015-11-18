package com.wzhnsc.testframeanimation;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private AnimationDrawable mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImageView iv = (ImageView)findViewById(R.id.iv_test_anim);

        mAnimation = (AnimationDrawable)iv.getBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAnimation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mAnimation.stop();
    }
}
