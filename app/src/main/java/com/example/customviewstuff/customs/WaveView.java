package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class WaveView extends BaseSurfaceView {
    private List<Water> waters;
    private PointF breakPoint;
    private float range, maxRange, increment;
    private boolean spreading, draw;
    private RectF drawRect;
    private Random random;
    private static final int waterInLine = 20;
    private static final long duration = 2000;
    private static final float maxRadius = 1.2f;
    private static final double maxX = Math.PI * 9;

    private Runnable drawRun = () -> {
        while (draw || spreading) {
            draw = false;
            range += increment;
            if (range > maxRange) {
                spreading = false;
            }
            for (Water water : waters) {
                water._float();
            }
            try {
                Thread.sleep(UPDATE_RATE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callDraw("draw");
        }
        breakPoint.set(0, 0);
        range = 0;
    };

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        drawRect = new RectF();
        waters = new ArrayList<>();
        random = new Random();
        breakPoint = new PointF();
    }

    @Override
    protected void onReady() {
        increment = getMeasuredWidth() * 0.01f;
        initWaters();
    }

    private void initWaters() {
        float baseRadius = getMeasuredWidth() / (waterInLine * 2f);
        int row = 1;
        while (baseRadius * row * 2 < getMeasuredHeight()) {
            int col = 0;
            while (col * baseRadius < getMeasuredWidth()) {
                col++;
                if (col % 2 != 0) {
                    Water water = new Water();
                    water.set(col * baseRadius, 2 * row * baseRadius - baseRadius, baseRadius);
                    waters.add(water);
                }
            }
            row++;
        }
        callDraw("draw");
    }

    @Override
    protected void onDataUpdate() {
    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        for (Water water : waters) {
            mPaint.setColor(water.color);
            drawRect.set(water.cx - water.currentRadius, water.cy - water.currentRadius, water.cx + water.currentRadius, water.cy + water.currentRadius);
//            canvas.drawRect(drawRect, mPaint);
            canvas.drawArc(drawRect, 0, 360, true, mPaint);
        }
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {
    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    private class Water {
        float cx, cy, baseRadius;
        float currentRadius, currentX;
        int color;

        Water() {
            set(0, 0, 0);
        }

        void set(float cx, float cy, float baseRadius) {
            this.cx = cx;
            this.cy = cy;
            color = randomColor();
            currentRadius = this.baseRadius = baseRadius;
        }

        void _float() {
            if (currentX == 0) {
                if (!spreading) return;
                double dis = distance(cx, cy);
                if (dis >= range - increment && dis <= range + increment) {
                    draw = true;
                    currentX += maxX * UPDATE_RATE / duration;
                    double scale = (1 - currentX / maxX) * maxRadius;
                    currentRadius = (float) (baseRadius * scale * Math.sin(currentX));
                }
            } else {
                draw = true;
                currentX += maxX * UPDATE_RATE / duration;
                if (currentX >= maxX) {
                    currentX = 0;
                }
                double scale = (1 - currentX / maxX) * maxRadius;
                currentRadius = baseRadius + (float) (baseRadius * scale * Math.sin(currentX));
            }
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!spreading && !isDrawing) {
                    breakPoint.set(event.getX(), event.getY());
                    maxRange = calMaxRange();
                    spreading = true;
                    doInThread(drawRun);
                }
                break;
        }
        return true;
    }

    private float calMaxRange() {
        return Math.max(distance(getMeasuredWidth(), getMeasuredHeight()), Math.max(distance(getMeasuredWidth(), 0), Math.max(distance(0, 0), distance(0, getMeasuredHeight()))));
    }

    private float distance(float x, float y) {
        return (float) Math.sqrt((x - breakPoint.x) * (x - breakPoint.x) + (y - breakPoint.y) * (y - breakPoint.y));
    }
}
