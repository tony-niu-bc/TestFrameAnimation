package com.wzhnsc.testframeanimation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public class CircularImageView extends ImageView
{
	// 自身显示的形状
	private enum ShapeType {
        TYPE_CIRCULAR_SHAPE, // 圆形
        TYPE_CIRCULAR_BEAD   // 圆角矩形
    }

	private ShapeType mShapeType;

	// 图像画笔
	private Paint mBitmapPaint;

    // 自身的宽度
	private int mSelfWidth;

    // 圆的半径
    private int mRadius;

    // 圆角矩形区域
	private RectF mCircularBeadRect;

    // 圆角半径
    private int mCornerRadius;

    public CircularImageView(Context context) {
        this(context, null);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView);

        // 默认为10dp
		mCornerRadius = a.getDimensionPixelSize(R.styleable.CircularImageView_cornerRadius,
                                                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                               10,
                                                                               getResources().getDisplayMetrics()));
        // 默认为圆形
		mShapeType = a.getInt(R.styleable.CircularImageView_shapeType, 0) == 0 ?
                     ShapeType.TYPE_CIRCULAR_SHAPE : ShapeType.TYPE_CIRCULAR_BEAD;

		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 如果形状是圆形，则强制改变宽高一致，以两者最小值为准
		if (ShapeType.TYPE_CIRCULAR_SHAPE == mShapeType) {
			mSelfWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
			mRadius    = mSelfWidth / 2;

			setMeasuredDimension(resolveSizeAndState(mSelfWidth, widthMeasureSpec,  MeasureSpec.UNSPECIFIED),
                                 resolveSizeAndState(mSelfWidth, heightMeasureSpec, MeasureSpec.UNSPECIFIED));
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
        // 更换图像的情况下要能显示新图像
        Drawable drawable = getDrawable();
        if (null == drawable) {
            return;
        }

        fixBmpShader(drawable);

		if (ShapeType.TYPE_CIRCULAR_BEAD == mShapeType) {
			canvas.drawRoundRect(mCircularBeadRect, mCornerRadius, mCornerRadius, mBitmapPaint);
		}
        else {
			canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
		}
	}

    @Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		// 圆角矩形的区域
		if (ShapeType.TYPE_CIRCULAR_BEAD == mShapeType) {
            mCircularBeadRect = new RectF(0, 0, w, h);
        }
	}

    private void fixBmpShader(Drawable drawable) {
        Bitmap bmp = drawable2Bmp(drawable);

        float scale = 1.0f;

        if (ShapeType.TYPE_CIRCULAR_SHAPE == mShapeType) {
            // 强制改变宽高一致，以两者最小值为准
            scale = (float)mSelfWidth / Math.min(bmp.getWidth(), bmp.getHeight());

        }
        else if (ShapeType.TYPE_CIRCULAR_BEAD == mShapeType) {
            // 如果图片的宽或者高与自身的宽高不匹配
            if (!(bmp.getWidth() == getWidth() && bmp.getHeight() == getHeight())) {
                // 计算出需要缩放的比例，缩放后的图片的宽高，一定要大于自身的宽高，所以取最大值；
                scale = Math.max((float)getWidth()  / bmp.getWidth(),
                        (float)getHeight() / bmp.getHeight());
            }
        }

        BitmapShader bmpShader = new BitmapShader(bmp,
                // 如果渲染器超出原始边界范围，会复制范围内边缘染色
                TileMode.CLAMP, TileMode.CLAMP);

        // 用于放大或者缩小
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        // 设置矩阵
        bmpShader.setLocalMatrix(matrix);

        // 设置渲染器
        mBitmapPaint.setShader(bmpShader);
    }

    private Bitmap drawable2Bmp(Drawable drawable)
	{
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		}

		int width  = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();

		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		drawable.setBounds(0, 0, width, height);
		drawable.draw(new Canvas(bmp));

		return bmp;
	}

	private static final String INSTANCE_STATE = "instance_state";
	private static final String SHAPE_TYPE     = "shape_type";
	private static final String CORNER_RADIUS  = "corner_radius";

	@Override
	protected Parcelable onSaveInstanceState()
	{
		Bundle bundle = new Bundle();

		bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putInt(SHAPE_TYPE, ShapeType.TYPE_CIRCULAR_SHAPE == mShapeType ? 0 : 1);
		bundle.putInt(CORNER_RADIUS, mCornerRadius);

		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		if (state instanceof Bundle) {
			super.onRestoreInstanceState(((Bundle)state).getParcelable(INSTANCE_STATE));

			mShapeType = ((Bundle)state).getInt(SHAPE_TYPE) == 0 ?
                         ShapeType.TYPE_CIRCULAR_SHAPE :
                         ShapeType.TYPE_CIRCULAR_BEAD;
			mCornerRadius = ((Bundle)state).getInt(CORNER_RADIUS);
		}
        else {
			super.onRestoreInstanceState(state);
		}
	}
}