package com.example.customviewstuff.customs.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Wang.Wenhui
 * Date: 2020/3/27
 */
public class VolumeControlView extends View {
    private Paint mPaint, textPaint;
    private int progress;
    private Path bgPath;
    private RectF oval, bgRect, volumeRect;
    private float[] radiusArray;
    private static final int gap = 25;
    private boolean drawVolume;
    private int minVal = 0, maxVal = 100;
    private OnVolumeChangedListener listener;
    private int bgColor = Color.DKGRAY, rectColor = Color.GRAY;

    public VolumeControlView(Context context) {
        this(context, null);
    }

    public VolumeControlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oval = new RectF();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setFakeBoldText(true);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLUE);

        radiusArray = new float[8];
        for (int i = 0; i < radiusArray.length; i++) {
//            radiusArray[i] = getResources().getDimension(R.dimen.qb_px_8);
            radiusArray[i] = 8f;
        }
        bgPath = new Path();
        bgRect = new RectF();
        volumeRect = new RectF();
        bgColor = Color.DKGRAY;
        rectColor = Color.GRAY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width < height * 4) {
            setMeasuredDimension(height * 4, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        mPaint.setStrokeWidth(getMeasuredHeight() * 0.05f);
        mPaint.setTextSize(getMeasuredHeight() * 0.4f);
        textPaint.setTextSize(getMeasuredHeight() * 0.4f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBg(canvas);
        super.onDraw(canvas);
        drawVolume(canvas);
    }

    private void drawBg(Canvas canvas) {
        bgPath.reset();
        bgRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        bgPath.addRoundRect(bgRect, radiusArray, Path.Direction.CW);
        canvas.clipPath(bgPath);
        canvas.drawColor(bgColor);
    }


    private void drawVolume(Canvas canvas) {
        float cx = (float) (getMeasuredWidth() * 0.1);
        float cy = getMeasuredHeight() >> 1;
        mPaint.setAlpha(255);
        if (progress > 0) {
            mPaint.setColor(rectColor);
            volumeRect.set(0, 0, getMeasuredWidth() * progress / 100f, getMeasuredHeight());
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(volumeRect, mPaint);

            mPaint.setColor(Color.parseColor("#FF6347"));
            int p = progress;
            float temp = getMeasuredHeight() * 0.1f;
            float radius = temp;
            mPaint.setStyle(Paint.Style.FILL);
            oval.set(cx - radius / 2, cy - radius / 2, cx + radius / 2, cy + radius / 2);
            canvas.drawArc(oval, 0, 360, false, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            while (p > gap) {
                oval.set(cx - radius, cy - radius, cx + radius, cy + radius);
                canvas.drawArc(oval, -30, 60, false, mPaint);
                radius += temp;
                p -= gap;
            }
            mPaint.setAlpha(gap == p ? 255 : (int) (255 * (p % gap) / (float) gap));
            oval.set(cx - radius, cy - radius, cx + radius, cy + radius);
            canvas.drawArc(oval, -30, 60, false, mPaint);
        } else {
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText("✕", cx, cy + mPaint.getTextSize() / 3, mPaint);
        }
        if (drawVolume) {
            canvas.drawText(String.valueOf(progress2RealVolume()), getMeasuredWidth() >> 1, getMeasuredHeight() * 0.67f, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawVolume = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                int p = progress2RealVolume();
                if (event.getX() < 0) {
                    progress = 0;
                } else if (event.getX() > getMeasuredWidth()) {
                    progress = 100;
                } else {
                    progress = (int) (100 * event.getX() / (float) getMeasuredWidth());
                }
                invalidate();
                if (p != progress2RealVolume()) {
                    if (listener != null) {
                        listener.onVolumeChanged(progress2RealVolume());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                drawVolume = false;
                invalidate();
                break;
        }
        return true;
    }

    private int progress2RealVolume() {
        return (int) (minVal + progress / 100f * (maxVal - minVal));
    }

    public void setListener(OnVolumeChangedListener listener) {
        this.listener = listener;
    }

    /**
     * 设置音量
     *
     * @param volume (minVal ~ maxVal)
     */
    public void setVolume(int volume) {
        this.progress = (int) ((float) (volume - minVal) / (maxVal - minVal) * 100);
        postInvalidate();
    }

    public void setMinVolume(int min) {
        minVal = min;
    }

    public void setMaxVolume(int max) {
        maxVal = max;
    }

    public interface OnVolumeChangedListener {
        /**
         * 音量变化
         *
         * @param volume [minVal ~ maxVal]
         */
        void onVolumeChanged(int volume);
    }
}
