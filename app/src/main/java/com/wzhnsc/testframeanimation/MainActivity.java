package com.wzhnsc.testframeanimation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private AnimationDrawable mAnimation;

    private boolean mIsDestroy = false;

    Dialog mPromptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImageView iv = (ImageView)findViewById(R.id.iv_test_anim);

        mAnimation = (AnimationDrawable)iv.getBackground();

        mPromptDialog = new AlertDialog.Builder(this, R.style.full_screent_dialog).create();
        mPromptDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAnimation.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsDestroy) {
                    return;
                }

                if (mPromptDialog.isShowing()) {
                    return;
                }

                Rect rect = new Rect();
                findViewById(R.id.civ_photo).getGlobalVisibleRect(rect);

                int statusBarHeight = 0;
                // 获取状态栏高度资源的ID
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    // 根据资源ID获取响应的尺寸值
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);

                    // 用 AlertDialog 方式显示蒙层会导致状态栏消失，所以要减去状态栏的高度
                    rect.top    -= statusBarHeight * 2;
                    //rect.bottom += statusBarHeight;
                    rect.left   -= statusBarHeight * 2;
                    rect.right  += statusBarHeight * 2;
                }

                PromptView promptView = new PromptView(MainActivity.this,
                                                       rect,
                                                       R.drawable.ic_jab_here,
                                                       PromptView.HollowShape.CIRCLE,
                                                       PromptView.SettingPosition.TOP);

                promptView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mPromptDialog.dismiss();
                        return true;
                    }
                });

                mPromptDialog.show();

                // android.util.AndroidRuntimeException: requestFeature() must be called before adding content
                Window window = mPromptDialog.getWindow();
                if (null != window) {
                    WindowManager.LayoutParams params = window.getAttributes();

                    if (null != params) {
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        window.setAttributes(params);
                    }

                    window.setContentView(promptView);
                }
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mAnimation.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
