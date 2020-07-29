package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/7/1
 * Description: blablabla
 */
public class IllusionView extends BaseSurfaceView {
    private float x, xInc;
    private boolean goRight;
    private PathEffect effect;

    private static final int count = 9;
    private int[] colors;
    private float rectLength, rectHeight;
    private boolean onTouch;
    private Random random;

    public IllusionView(Context context) {
        super(context);
    }

    public IllusionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IllusionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        colors = new int[count];
        for (int i = 0; i < count; i++) {
            colors[i] = randomColor();
        }
    }

    @Override
    protected void onReady() {
        xInc = getMeasuredWidth() * 0.002f;
        rectLength = getMeasuredWidth() * 0.3f;
        rectHeight = rectLength / 2;
        effect = new DashPathEffect(new float[]{20f, 20f}, 0);
        x = -rectLength;
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        if (x <= -rectLength) {
            goRight = true;
            x = 0;
        } else if (x >= getMeasuredWidth() + rectLength) {
            goRight = false;
            x = getMeasuredWidth();
        }
        x += goRight ? xInc : -xInc;
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        if (!onTouch) {
            drawBg(canvas);
        }
        drawRects(canvas);
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private void drawRects(Canvas canvas) {
        float length = goRight ? -rectLength : rectLength;
        float height = rectHeight;
        mPaint.setStrokeWidth(rectHeight);
        for (int color : colors) {
            mPaint.setColor(color);
            canvas.drawLine(x + length, height, x, height, mPaint);
            height += rectHeight * 1.5f;
        }

    }

    private void drawBg(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getMeasuredHeight());
        mPaint.setPathEffect(effect);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(0, getMeasuredHeight() >> 1, getMeasuredWidth(), getMeasuredHeight() >> 1, mPaint);
        mPaint.setPathEffect(null);
    }

    @Override
    protected void draw(Canvas canvas, Object data) {

    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouch = true;
                break;
            case MotionEvent.ACTION_UP:
                onTouch = false;
                break;
        }
        return true;
    }
}
