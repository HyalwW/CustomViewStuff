package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/15
 */
public class SnakeView extends BaseSurfaceView {
    private Path snakePath, drawPath;
    private PathMeasure measure;
    private float baseLength, length;
    private static final float LINE_LIMIT = 50f;
    private PointF lastP, food;
    private boolean isStart;
    private Random random;

    public SnakeView(Context context) {
        super(context);
    }

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        snakePath = new Path();
        drawPath = new Path();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(Color.WHITE);
        measure = new PathMeasure();
        lastP = new PointF();
        food = new PointF();
        random = new Random();
    }

    @Override
    protected void onReady() {
        baseLength = Math.min(getMeasuredHeight(), getMeasuredWidth());
        mPaint.setStrokeWidth(baseLength * 0.03f);
        if (snakePath.isEmpty()) {
            reset();
        }
        startAnim();
    }

    private void reset() {
        isStart = false;
        length = baseLength * 0.3f;
        snakePath.reset();
        snakePath.moveTo(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
        double angle = random.nextFloat() * Math.PI * 2;
        lastP.set(((float) (baseLength * 0.3f * Math.cos(angle) + (getMeasuredWidth() >> 1))),
                (float) (baseLength * 0.3f * Math.sin(angle) + (getMeasuredHeight() >> 1)));
        snakePath.lineTo(lastP.x, lastP.y);
        randomFood();
    }

    private void randomFood() {
        food.set(random.nextFloat() * getMeasuredWidth(), random.nextFloat() * getMeasuredHeight());
    }

    @Override
    protected void onDataUpdate() {
        if (length <= 0) {
            length = 0;
            reset();
            Log.e("wwh", "SnakeView --> onDataUpdate: reset");
            return;
        }
        if (isStart) {
            length -= baseLength * 0.001f;
        }
        drawPath.reset();
        measure.setPath(snakePath, false);
        measure.getSegment(measure.getLength() - length, measure.getLength(), drawPath, true);
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawPath(drawPath, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(food.x, food.y, 20, mPaint);
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
        float x = event.getX(), y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isStart && canLine(x, y)) {
                    snakePath.lineTo(x, y);
                    lastP.set(x, y);
                    isStart = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (canLine(x, y)) {
                    snakePath.lineTo(x, y);
                    if (feed(x, y)) {
                        randomFood();
                        length += baseLength * 0.15f;
                    }
                    lastP.set(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private boolean canLine(float x, float y) {
        return Math.sqrt((lastP.x - x) * (lastP.x - x) + (lastP.y - y) * (lastP.y - y)) <= LINE_LIMIT;
    }

    private boolean feed(float x, float y) {
        return Math.sqrt((food.x - x) * (food.x - x) + (food.y - y) * (food.y - y)) <= 20;
    }
}
