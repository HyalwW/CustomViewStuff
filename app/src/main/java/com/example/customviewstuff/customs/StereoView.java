package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/15
 */
public class StereoView extends BaseSurfaceView {
    private Random random;
    private float bx, by;
    private double angle;
    private double rotateAngle;
    private float centerOffset;
    private static final long duration = 5000;
    private static float baseRadius;
    private List<Circle> circles;
    private float circleRadius;
    private double nowAngle;

    public StereoView(Context context) {
        super(context);
    }

    public StereoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StereoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        circles = new ArrayList<>();
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onReady() {
        bx = getMeasuredWidth() >> 1;
        by = getMeasuredHeight() >> 1;
        circleRadius = getMeasuredWidth() * 0.02f;
        baseRadius = getMeasuredWidth() * 0.2f;
        rotateAngle = 0;
        centerOffset = getMeasuredWidth() * 0.01f;
        double aa = 0;
        while (aa < Math.PI * 2) {
            aa += Math.PI / 12;
            for (int i = 0; i < 15; i++) {
                float len = i * centerOffset;
                double a = rotateAngle - Math.PI;
                Circle circle = new Circle(((float) (bx + len * Math.sin(a))), ((float) (by + len * Math.cos(a))), baseRadius + len, aa);
                circles.add(circle);
            }
        }
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        angle += Math.PI * 2 * UPDATE_RATE / duration;
        if (angle % Math.PI * 2 == 0) {
            angle = 0;
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (Circle circle : circles) {
            mPaint.setColor(circle.color);
            float x = (float) (circle.radius * Math.sin(angle + circle.radiusOffset) + circle.cx);
            float y = (float) (circle.radius * Math.cos(angle + circle.radiusOffset) + circle.cy);
            canvas.drawCircle(x, y, circleRadius, mPaint);
        }
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

    private class Circle {
        float cx, cy, radius;
        double radiusOffset;
        int color;

        Circle(float cx, float cy, float radius, double radiusOffset) {
            reset(cx, cy, radius, radiusOffset);
        }

        void reset(float cx, float cy, float radius, double radiusOffset) {
            this.cx = cx;
            this.cy = cy;
            this.radius = radius;
            this.radiusOffset = radiusOffset;
            color = randomColor();
        }
    }

    private int randomColor() {
        return Color.rgb(randomRgb(), randomRgb(), randomRgb());
    }

    private int randomRgb() {
        return random.nextInt(255);
    }

    private float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                downY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                nowAngle = Math.PI / 2 - Math.atan2(eventY - downY, eventX - downX);
                resetAngle(nowAngle);
                break;
        }
        return true;
    }

    private void resetAngle(double angle) {
        this.rotateAngle = angle;
        for (Circle circle : circles) {
            double a = rotateAngle - Math.PI;
            float len = circle.radius - baseRadius;
            circle.reset(((float) (bx + len * Math.sin(a))), ((float) (by + len * Math.cos(a))), circle.radius, circle.radiusOffset);
        }
    }
}
