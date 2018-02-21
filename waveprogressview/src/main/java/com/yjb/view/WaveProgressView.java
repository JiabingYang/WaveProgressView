package com.yjb.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Jiabing Yang
 */
public class WaveProgressView extends View {

    //测量值
    private int mViewWidthPx = -1;
    private int mViewHeightPx = -1;

    //用户设定值
    private String mCompletedText = "完成";
    private int mCircleColor = Color.WHITE;
    private int mTextColor = Color.GRAY;
    private int mCompletedTextColor = Color.WHITE;
    private int mProgress = 0, mMax = 100;
    private int mWaveCountOne = 1, mWaveCountTwo = 1;
    private float mWaveAmplitudeRatioOne = 0.125f, mWaveAmplitudeRatioTwo = 0.125f;
    private int mWaveColorOne = 0xffa3e6df, mWaveColorTwo = 0xff6DD3BD;
    private float mStepAbsOne = 1, mStepAbsTwo = 2;
    private Direction mDirectionOne = Direction.LEFT, mDirectionTwo = Direction.LEFT;
    private ShowType mShowType = ShowType.BOTH;
    private Speed mSpeed = Speed.NORMAL;

    //计算值
    private float mRadiusPx;
    private float mWaveWidthPxOne, mWaveWidthPxTwo;
    private float mWaveYPx;
    private float mStepPxOne, mStepPxTwo;
    private float mOffsetOne, mOffsetTwo;

    //绘画
    private Paint mCirclePaint;
    private Paint mTextPaint;
    private Paint mWavePaintOne, mWavePaintTwo;
    private Path mPath = new Path();
    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;
    private AnimationThread mAnimationThread;

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initConfig(context, attrs);
        initPaint();
    }

    public String getCompletedText() {
        return mCompletedText;
    }

    public void setCompletedText(String completedText) {
        this.mCompletedText = completedText;
    }

    public int getCircleColor() {
        return mCircleColor;
    }

    public void setCircleColor(@ColorInt int circleColor) {
        this.mCircleColor = circleColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(@ColorInt int textColor) {
        this.mTextColor = textColor;
    }

    public int getCompletedTextColor() {
        return mCompletedTextColor;
    }

    public void setCompletedTextColor(@ColorInt int completedTextColor) {
        this.mCompletedTextColor = completedTextColor;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        progress = Math.abs(progress);
        if (progress > mMax) {
            progress = progress % mMax;
        }
        mProgress = progress;
        calculateWaveParams();
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = Math.abs(max);
        if (mProgress > mMax) {
            mProgress = mProgress % mMax;
        }
        calculateWaveParams();
    }

    public int getWaveCountOne() {
        return mWaveCountOne;
    }

    public void setWaveCountOne(int waveCountOne) {
        this.mWaveCountOne = waveCountOne;
        calculateWaveParams();
    }

    public int getWaveCountTwo() {
        return mWaveCountTwo;
    }

    public void setWaveCountTwo(int waveCountTwo) {
        this.mWaveCountTwo = waveCountTwo;
        calculateWaveParams();
    }

    public float getWaveAmplitudeRatioOne() {
        return mWaveAmplitudeRatioOne;
    }

    public void setWaveAmplitudeRatioOne(float waveAmplitudeRatioOne) {
        this.mWaveAmplitudeRatioOne = waveAmplitudeRatioOne;
    }

    public float getWaveAmplitudeRatioTwo() {
        return mWaveAmplitudeRatioTwo;
    }

    public void setWaveAmplitudeRatioTwo(float waveAmplitudeRatioTwo) {
        this.mWaveAmplitudeRatioTwo = waveAmplitudeRatioTwo;
    }

    public int getWaveColorOne() {
        return mWaveColorOne;
    }

    public void setWaveColorOne(@ColorInt int waveColorOne) {
        this.mWaveColorOne = waveColorOne;
    }

    public int getWaveColorTwo() {
        return mWaveColorTwo;
    }

    public void setWaveColorTwo(@ColorInt int waveColorTwo) {
        this.mWaveColorTwo = waveColorTwo;
    }

    public float getStepAbsOne() {
        return mStepAbsOne;
    }

    public void setStepAbsOne(float stepAbsPxOne) {
        this.mStepAbsOne = stepAbsPxOne;
        calculateWaveParams();
    }

    public float getStepAbsTwo() {
        return mStepAbsTwo;
    }

    public void setStepAbsTwo(float stepAbsPxTwo) {
        this.mStepAbsTwo = stepAbsPxTwo;
        calculateWaveParams();
    }

    public Direction getDirectionOne() {
        return mDirectionOne;
    }

    public void setDirectionOne(Direction directionOne) {
        this.mDirectionOne = directionOne;
        calculateWaveParams();
    }

    public Direction getDirectionTwo() {
        return mDirectionTwo;
    }

    public void setDirectionTwo(Direction directionTwo) {
        this.mDirectionTwo = directionTwo;
        calculateWaveParams();
    }

    public ShowType getShowType() {
        return mShowType;
    }

    public void setShowType(ShowType showType) {
        this.mShowType = showType;
        calculateWaveParams();
    }

    public Speed getSpeed() {
        return mSpeed;
    }

    public void setSpeed(Speed speed) {
        this.mSpeed = speed;
        stopAnimation();
        startAnimation();
    }

    public boolean isAnimationStarted() {
        return mAnimationThread != null;
    }

    public void startAnimation() {
        if (mAnimationThread == null) {
            mAnimationThread = new AnimationThread();
            getHandler().postDelayed(mAnimationThread, 200);
        }
    }

    public void stopAnimation() {
        if (mAnimationThread != null) {
            getHandler().removeCallbacks(mAnimationThread);
            mAnimationThread = null;
        }
    }

    private void initConfig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        mCompletedText = a.getString(R.styleable.WaveProgressView_wpvCompletedText);
        if (mCompletedText == null)
            mCompletedText = "完成";
        mCircleColor = a.getColor(R.styleable.WaveProgressView_wpvCircleColor, Color.WHITE);
        mTextColor = a.getColor(R.styleable.WaveProgressView_wpvTextColor, Color.GRAY);
        mCompletedTextColor = a.getColor(R.styleable.WaveProgressView_wpvCompletedTextColor, Color.WHITE);
        mMax = Math.abs(a.getInteger(R.styleable.WaveProgressView_wpvMax, 100));
        mProgress = Math.abs(a.getInteger(R.styleable.WaveProgressView_wpvProgress, 0));
        if (mProgress > mMax) {
            mProgress = mProgress % mMax;
        }
        mWaveCountOne = a.getInteger(R.styleable.WaveProgressView_wpvWaveCountOne, 1);
        mWaveCountTwo = a.getInteger(R.styleable.WaveProgressView_wpvWaveCountTwo, 1);
        mWaveAmplitudeRatioOne = a.getFloat(R.styleable.WaveProgressView_wpvWaveAmplitudeRatioOne, 0.125f);
        mWaveAmplitudeRatioTwo = a.getFloat(R.styleable.WaveProgressView_wpvWaveAmplitudeRatioTwo, 0.125f);
        mWaveColorOne = a.getColor(R.styleable.WaveProgressView_wpvWaveColorOne, 0xffa3e6df);
        mWaveColorTwo = a.getColor(R.styleable.WaveProgressView_wpvWaveColorTwo, 0xff6DD3BD);
        mStepAbsOne = a.getDimension(R.styleable.WaveProgressView_wpvStepAbsOne, 1f);
        mStepAbsTwo = a.getDimension(R.styleable.WaveProgressView_wpvStepAbsTwo, 2f);
        mDirectionOne = Direction.valueOf(a.getInteger(R.styleable.WaveProgressView_wpvDirectionOne, Direction.LEFT.getValue()));
        mDirectionTwo = Direction.valueOf(a.getInteger(R.styleable.WaveProgressView_wpvDirectionTwo, Direction.LEFT.getValue()));
        mShowType = ShowType.valueOf(a.getInteger(R.styleable.WaveProgressView_wpvShowType, ShowType.BOTH.getValue()));
        mSpeed = Speed.valueOf(a.getInteger(R.styleable.WaveProgressView_wpvSpeed, Speed.NORMAL.getValue()));
        a.recycle();
    }

    private void initPaint() {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setAntiAlias(true);

        mWavePaintOne = new Paint();
        mWavePaintOne.setAntiAlias(true);
        mWavePaintOne.setColor(mWaveColorOne);
        mWavePaintOne.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mWavePaintTwo = new Paint();
        mWavePaintTwo.setAntiAlias(true);
        mWavePaintTwo.setColor(mWaveColorTwo);
        mWavePaintTwo.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidthPx = w;
        mViewHeightPx = h;

        mRadiusPx = Math.min(mViewWidthPx, mViewHeightPx) / 2.0f;
        mBitmap = Bitmap.createBitmap((int) mRadiusPx * 2, (int) mRadiusPx * 2, Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);

        calculateWaveParams();

        startAnimation();
    }

    private void calculateWaveParams() {
        if (mViewWidthPx == -1 || mViewHeightPx == -1)
            return;
        mWaveWidthPxOne = (2.0f * mRadiusPx) / mWaveCountOne;
        if (mDirectionOne == Direction.LEFT) {//left
            mOffsetOne = mViewWidthPx / 2 - mRadiusPx;
            mStepPxOne = -mStepAbsOne;
        } else {//right
            mOffsetOne = mViewWidthPx / 2 - mRadiusPx - mWaveWidthPxOne;
            mStepPxOne = mStepAbsOne;
        }

        mWaveWidthPxTwo = (2.0f * mRadiusPx) / mWaveCountTwo;
        if (mDirectionTwo == Direction.LEFT) {//left
            mOffsetTwo = mViewWidthPx / 2 - mRadiusPx;
            mStepPxTwo = -mStepAbsTwo;
        } else {//right
            mOffsetTwo = mViewWidthPx / 2 - mRadiusPx - mWaveWidthPxTwo;
            mStepPxTwo = mStepAbsTwo;
        }

        mWaveYPx = (1 - (float) mProgress / mMax) * 2 * mRadiusPx + mViewHeightPx / 2.0f - mRadiusPx;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画圆
        mBitmapCanvas.drawCircle(mViewWidthPx / 2, mViewHeightPx / 2, mRadiusPx, mCirclePaint);

        if (mShowType == ShowType.BOTH || mShowType == ShowType.ONE) {
            //画波1
            mPath.reset();
            mPath.moveTo(mOffsetOne, mWaveYPx);
            for (int i = 0; i < mWaveCountOne + 2; i++) {
                mPath.rQuadTo(mWaveWidthPxOne / 4, -mWaveWidthPxOne * mWaveAmplitudeRatioOne, mWaveWidthPxOne / 2, 0);
                mPath.rQuadTo(mWaveWidthPxOne / 4, mWaveWidthPxOne * mWaveAmplitudeRatioOne, mWaveWidthPxOne / 2, 0);
            }
            mPath.lineTo(mViewWidthPx / 2 + mRadiusPx, mViewHeightPx / 2 + mRadiusPx);
            mPath.lineTo(mViewWidthPx / 2 - mRadiusPx, mViewHeightPx / 2 + mRadiusPx);
            mPath.close();
            mBitmapCanvas.drawPath(mPath, mWavePaintOne);
        }
        if (mShowType == ShowType.BOTH || mShowType == ShowType.TWO) {
            //画波2
            mPath.reset();
            mPath.moveTo(mOffsetTwo, mWaveYPx);
            for (int i = 0; i < mWaveCountTwo + 2; i++) {
                mPath.rQuadTo(mWaveWidthPxTwo / 4, -mWaveWidthPxTwo * mWaveAmplitudeRatioTwo, mWaveWidthPxTwo / 2, 0);
                mPath.rQuadTo(mWaveWidthPxTwo / 4, mWaveWidthPxTwo * mWaveAmplitudeRatioTwo, mWaveWidthPxTwo / 2, 0);
            }
            mPath.lineTo(mViewWidthPx / 2 + mRadiusPx, mViewHeightPx / 2 + mRadiusPx);
            mPath.lineTo(mViewWidthPx / 2 - mRadiusPx, mViewHeightPx / 2 + mRadiusPx);
            mPath.close();
            mBitmapCanvas.drawPath(mPath, mWavePaintTwo);
        }
        //画进度文字
        mTextPaint.setTextSize(mRadiusPx / 3);
        String text;
        if (mProgress / mMax == 1) {
            text = mCompletedText;
            mTextPaint.setColor(mCompletedTextColor);
        } else {
            text = 100 * mProgress / mMax + "%";
            mTextPaint.setColor(mTextColor);
        }
        float textWidth = mTextPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mBitmapCanvas.drawText(text, mViewWidthPx / 2 - textWidth / 2, mViewHeightPx / 2 - (fontMetrics.descent + fontMetrics.ascent) / 2, mTextPaint);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    private void moveWave() {
        if (mShowType == ShowType.BOTH || mShowType == ShowType.ONE) {
            mOffsetOne += mStepPxOne;
            if (mOffsetOne < mViewWidthPx / 2 - mRadiusPx - mWaveWidthPxOne) {
                mOffsetOne += mWaveWidthPxOne;
            } else if (mOffsetOne > mViewWidthPx / 2 - mRadiusPx) {
                mOffsetOne -= mWaveWidthPxOne;
            }
        }
        if (mShowType == ShowType.BOTH || mShowType == ShowType.TWO) {
            mOffsetTwo += mStepPxTwo;
            if (mOffsetTwo < mViewWidthPx / 2 - mRadiusPx - mWaveWidthPxTwo) {
                mOffsetTwo += mWaveWidthPxTwo;
            } else if (mOffsetTwo > mViewWidthPx / 2 - mRadiusPx) {
                mOffsetTwo -= mWaveWidthPxTwo;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    public enum Direction {
        LEFT(0), RIGHT(1);

        private int mDirection;

        Direction(int direction) {
            this.mDirection = direction;
        }

        public static Direction valueOf(int value) {
            switch (value) {
                case 0:
                    return LEFT;
                case 1:
                    return RIGHT;
                default:
                    return LEFT;
            }
        }

        public int getValue() {
            return this.mDirection;
        }

        @Override
        public String toString() {
            return String.valueOf(mDirection);
        }
    }

    public enum ShowType {
        BOTH(0), ONE(1), TWO(2);

        private int mShowType;

        ShowType(int showType) {
            this.mShowType = showType;
        }

        public static ShowType valueOf(int value) {
            switch (value) {
                case 0:
                    return BOTH;
                case 1:
                    return ONE;
                case 2:
                    return TWO;
                default:
                    return BOTH;
            }
        }

        public int getValue() {
            return this.mShowType;
        }

        @Override
        public String toString() {
            return String.valueOf(mShowType);
        }
    }

    public enum Speed {
        SLOW(150), NORMAL(100), FAST(50);

        private int mSpeed;

        Speed(int speed) {
            this.mSpeed = speed;
        }

        public static Speed valueOf(int value) {
            switch (value) {
                case 150:
                    return SLOW;
                case 100:
                    return NORMAL;
                case 50:
                    return FAST;
                default:
                    return NORMAL;
            }
        }

        public int getValue() {
            return this.mSpeed;
        }

        @Override
        public String toString() {
            return String.valueOf(mSpeed);
        }
    }

    private class AnimationThread implements Runnable {
        @Override
        public void run() {
            moveWave();
            invalidate();
            getHandler().postDelayed(mAnimationThread, mSpeed.getValue());
        }
    }
}
