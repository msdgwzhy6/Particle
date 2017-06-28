package com.example.jadynai.particle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.jadynai.particle.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @version:
 * @FileDescription: 萤火虫飞舞view
 * @Author:jing
 * @Since:2017/4/26
 * @ChangeList:
 */

public class FirewormsView extends View {

    private static final String TAG = "FirewormsFlyView";
    public static final int MAX_NUM = 20;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private Random mRandom = new Random();

    private List<Particle> mCircles = new ArrayList<>();

    private Paint mPaint;
    private Bitmap mStarBitmap;
    private Matrix mMatrix;
    private Context mContext;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;

    public FirewormsView(Context context) {
        super(context);
        init(context, null);
    }

    public FirewormsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FirewormsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMeasuredWidth == 0) {
            mMeasuredWidth = getMeasuredWidth();
            mMeasuredHeight = getMeasuredHeight();
        }
        if (mCircles.size() == 0) {
            for (int i = 0; i < MAX_NUM; i++) {
                Particle f = new Particle(mStarBitmap, mMatrix, mPaint, getF() * mMeasuredWidth, getF() * mMeasuredHeight, mMeasuredWidth, mMeasuredHeight);
                mCircles.add(f);
            }
        }
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mPaint = new Paint();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FirewormsView);
        int resourceId = typedArray.getResourceId(R.styleable.FirewormsView_particalSrc, R.drawable.fish_master);

        // 打开抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(Particle.ALPHA_MAX);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mMatrix = new Matrix();
        mStarBitmap = getParticleBitmap(resourceId);

        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mBufferBitmap) {
            mBufferBitmap = Bitmap.createBitmap(mMeasuredWidth, mMeasuredHeight, Bitmap.Config.ARGB_8888);
            mBufferCanvas = new Canvas(mBufferBitmap);
        }
        //每次重绘之前先把BufferBitmap清空
        mBufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Particle circle : mCircles) {
            circle.drawItem(mBufferCanvas);
        }
        canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        invalidate();
    }

    public void setParticleSrcID(@DrawableRes int resId) {
        for (Particle circle : mCircles) {
            circle.setDrawBitmap(getParticleBitmap(resId));
        }
        invalidate();
    }

    public void setParticleSrcBitmap(Bitmap bitmap) {
        for (Particle circle : mCircles) {
            circle.setDrawBitmap(bitmap);
        }
        invalidate();
    }

    private Bitmap getParticleBitmap(@DrawableRes int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getContext().getResources(), resId, options),
                dip2px(30), dip2px(30), true);
    }

    private float getF() {
        float v = mRandom.nextFloat();
        if (v < 0.15f) {
            return v + 0.15f;
        } else if (v >= 0.85f) {
            return v - 0.15f;
        } else {
            return v;
        }
    }

    private int dip2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
    }

}
