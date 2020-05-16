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
    //刷新旋转角度
    private double angle;
    private static final long duration = 5000;
    //立体最小圆半径
    private float baseRadius;
    private List<Circle> circles;
    //每个小圆半径
    private float circleRadius;
    private double rotateAngle;
    private float centerOffset;
    private float maxCenterOffset;

    private boolean spin, isClick;

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
        baseRadius = getMeasuredWidth() * 0.16f;
        rotateAngle = 0;
        centerOffset = getMeasuredWidth() * 0.01f;
        maxCenterOffset = getMeasuredWidth() * 0.012f;
        if (circles.size() == 0) {
            double aa = 0;
            while (aa < Math.PI * 2) {
                aa += Math.PI / 12;
                for (int i = 0; i < 15; i++) {
                    Circle circle = new Circle(i, aa);
                    circles.add(circle);
                }
            }
        }
        spin = true;
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        if (spin) {
            angle += Math.PI * 2 * UPDATE_RATE / duration;
            if (angle % Math.PI * 2 == 0) {
                angle = 0;
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (Circle circle : circles) {
            mPaint.setColor(circle.color);
            float x = (float) (circle.radius * Math.sin(angle + circle.angleOffset) + circle.cx);
            float y = (float) (circle.radius * Math.cos(angle + circle.angleOffset) + circle.cy);
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
        double angleOffset;
        int index, color;

        Circle(int index, double angleOffset) {
            this.index = index;
            this.angleOffset = angleOffset;
            color = randomColor();
            reset();
        }

        void reset() {
            float len = index * centerOffset;
            this.cx = (float) (bx + len * Math.sin(rotateAngle));
            this.cy = (float) (by + len * Math.cos(rotateAngle));
            this.radius = baseRadius + len;
        }
    }

    private int randomColor() {
        return Color.rgb(randomRgb(), randomRgb(), randomRgb());
    }

    private int randomRgb() {
        return random.nextInt(255);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                isClick = false;
                double nowAngle = Math.PI / 2 - Math.atan2(eventY - by, eventX - bx);
                float dis = dis(eventX, eventY, bx, by);
                float max = baseRadius * 1.5f;
                dis = Math.min(dis, max);
                float offset = maxCenterOffset * (dis / max);
                reset(nowAngle, offset);
                break;
            case MotionEvent.ACTION_UP:
                if (isClick) {
                    spin = !spin;
                }
                break;
        }
        return true;
    }

    private float dis(float sx, float sy, float ex, float ey) {
        return (float) Math.sqrt((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy));
    }

    private void reset(double angle, float offset) {
        rotateAngle = angle + Math.PI;
        centerOffset = offset;
        for (Circle circle : circles) {
            circle.reset();
        }
    }
}
