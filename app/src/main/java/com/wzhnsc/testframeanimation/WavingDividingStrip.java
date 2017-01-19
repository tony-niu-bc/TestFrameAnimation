package com.wzhnsc.testframeanimation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

// 波浪动画式分隔条
public class WavingDividingStrip extends View {
    // 刷新频率
    private final static long  mRefreshRate = 100;

    // 绘制波表的画笔
    private final static Paint mWaveSurfacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // 绘制波底的画笔
    private final static Paint mWaveBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // 波浪曲线路径
    private final static Path  mWavePath = new Path();

    // 潮汐定时器
    private final static Timer mTimer = new Timer();

    // 两条波浪间的间隔
    private int mWaveSpacing = 0;

    // 自身视图的控件宽度
    private int mSelfWidth = 0;

    // 波幅
    private float mAmplitude;

    // 波面推进速度
    private int mWaveSurfaceVelocity = 80;

    // 波底推进速度
    private int mWaveBaseVelocity = 10;

    // 水平中轴线
    private int mHorzontalCentralAxis;

    // 可变偏移量
    private int mVariableOffset = 0;

    // 是否还要运行
    private boolean mIsRunning;

    public WavingDividingStrip(Context context) {
        this(context, null);
    }

    public WavingDividingStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WavingDividingStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WavingDividingStrip);
        mAmplitude = a.getDimension(R.styleable.WavingDividingStrip_amplitude,
                                    (10 * getResources().getDisplayMetrics().density) + 0.5f);
        // 辅浪颜色
        int mSideWaveColor = a.getColor(R.styleable.WavingDividingStrip_sideWaveColor, Color.argb(0x77, 0x00, 0x00, 0xcc));
        // 天空颜色
        int mSkyColor = a.getColor(R.styleable.WavingDividingStrip_skyColor, Color.rgb(0x00, 0xbb, 0xcc));
        mWaveSurfaceVelocity = a.getInt(R.styleable.WavingDividingStrip_waveSurfaceVelocity, mWaveSurfaceVelocity);
        mWaveBaseVelocity = a.getInt(R.styleable.WavingDividingStrip_waveSurfaceVelocity, mWaveBaseVelocity);
        a.recycle();

        mWaveSurfacePaint.setColor(mSideWaveColor);
        mWaveSurfacePaint.setAntiAlias(true);
        mWaveBasePaint.setColor(mSkyColor);
        mWaveBasePaint.setAntiAlias(true);

        mTimer.schedule(new TimerTask() { // 重复绘制波纹
            @Override
            public void run() {
                if (mIsRunning && mSelfWidth != 0) {
                    // 波面速度
                    mVariableOffset += mSelfWidth / mWaveSurfaceVelocity;
                    mVariableOffset %= 2 * mSelfWidth;

                    // 波底速度
                    mWaveSpacing += mWaveBaseVelocity;
                    mWaveSpacing %= 2 * mSelfWidth;

                    // 重绘自身
                    postInvalidate();
                }
            }
        }, mRefreshRate, mRefreshRate);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mIsRunning = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mIsRunning = false; // 在不显示的时候，停止运行
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mHorzontalCentralAxis = MeasureSpec.getSize(heightMeasureSpec) - (int)mAmplitude;
        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(makeWave(mVariableOffset), mWaveSurfacePaint);
        canvas.drawPath(makeWave(mVariableOffset + mWaveSpacing), mWaveBasePaint);
    }

    private Path makeWave(int offset) {
        offset = offset % (2 * mSelfWidth);

        mWavePath.reset();
        mWavePath.moveTo(mSelfWidth, 0);
        mWavePath.lineTo(0, 0);

        int var = -(2 * mSelfWidth) + offset;

        mWavePath.lineTo(var, mHorzontalCentralAxis);

        boolean upOrDown = true; // true - 沉，false - 浮
        while (var <= mSelfWidth) {
            // 绘制贝塞尔曲线
            mWavePath.quadTo(var + mSelfWidth / 2,
                             upOrDown ? mHorzontalCentralAxis - mAmplitude : mHorzontalCentralAxis + mAmplitude,
                             var + mSelfWidth,
                             mHorzontalCentralAxis);
            upOrDown = !upOrDown;
            var += mSelfWidth;
        }

        mWavePath.lineTo(mSelfWidth, 0);

        return mWavePath;
    }
}
