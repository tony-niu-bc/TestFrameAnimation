package com.wzhnsc.testframeanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.View;

public class PromptView extends View {
    private Rect   mRectF;
    private Bitmap mPromptBmp;
    private Bitmap mMaskBmp;

    public enum HollowShape {
        RECTANGLE,
        CIRCLE
    }

    private HollowShape mHollowShape;

    public enum SettingPosition {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        center
    }

    private SettingPosition mSettingPosition;

    public PromptView(Context         context,
                      Rect            rectF,
                      int             resourceId,
                      HollowShape     hollowShape,
                      SettingPosition settingPosition) {
        super(context);

        mRectF           = rectF;
        mPromptBmp       = BitmapFactory.decodeResource(getResources(), resourceId);
        mHollowShape     = hollowShape;
        mSettingPosition = settingPosition;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mMaskBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        Canvas canvas  = new Canvas(mMaskBmp);
        canvas.drawColor(getResources().getColor(R.color.colorBackground));

        Paint paint = new Paint();
        // 画笔颜色
        paint.setColor(Color.TRANSPARENT);
        // 画笔的风格 - 实心
        paint.setStyle(Paint.Style.FILL);
        // 抗锯齿
        paint.setAntiAlias(true);
        // 防抖动
        paint.setDither(true);
        // 设置图形重叠时的处理方式 - SRC_IN - 只在源图像和目标图像相交的地方绘制源图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 以便以后扩展其他透明区域
        if (HollowShape.RECTANGLE == mHollowShape) {
            canvas.drawRect(mRectF.left, mRectF.top, mRectF.right, mRectF.bottom, paint);
        }
        else {
            canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), Math.min(mRectF.width(), mRectF.height()) / 2, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mMaskBmp, 0, 0, null);
        canvas.save();

        // 以下全是要绘制透明底文字图像的位置
        // TOP - 上中
        int left = mRectF.centerX() - mPromptBmp.getWidth() / 2;
        int top  = mRectF.top - mPromptBmp.getHeight();

        // 左中
        if (SettingPosition.LEFT == mSettingPosition) {
            left = mRectF.left - mPromptBmp.getWidth();
            top = mRectF.centerY() - mPromptBmp.getHeight() / 2;
        }
        // 右中
        else if (SettingPosition.RIGHT == mSettingPosition) {
            left = mRectF.right;
            top = mRectF.centerY() - mPromptBmp.getHeight() / 2;
        }
        // 下中
        else if (SettingPosition.BOTTOM == mSettingPosition) {
            top = mRectF.bottom;
        }

        canvas.drawBitmap(mPromptBmp,
                          left,
                          top,
                          null);

        canvas.restore();
    }
}
