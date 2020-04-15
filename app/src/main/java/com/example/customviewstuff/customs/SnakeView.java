package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.R;

import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/15
 */
public class SnakeView extends BaseSurfaceView {
    private Path snakePath, drawPath, equalPath;
    private PathMeasure measure, equalMeasure;
    private float baseLength, length;
    private PointF lastP, food;
    private boolean isStart;
    private Random random;
    private float headAngle;
    private Bitmap head;
    private Rect headDst;

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
        equalPath = new Path();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(Color.WHITE);
        measure = new PathMeasure();
        equalMeasure = new PathMeasure();
        lastP = new PointF();
        food = new PointF();
        random = new Random();
        head = BitmapFactory.decodeResource(getResources(), R.drawable.snake);
        headDst = new Rect();
    }

    @Override
    protected void onReady() {
        baseLength = Math.min(getMeasuredHeight(), getMeasuredWidth());
        mPaint.setStrokeWidth(baseLength * 0.032f);
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
            return;
        }
        if (isStart) {
            length -= baseLength * 0.001f;
        }
        drawPath.reset();
        measure.setPath(snakePath, false);
        measure.getSegment(measure.getLength() - length, measure.getLength(), drawPath, true);
        float[] tan = new float[2];
        measure.getPosTan(measure.getLength(), null, tan);
        headAngle = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI) - 90;
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFFFF4500);
        canvas.drawPath(drawPath, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(food.x, food.y, 20, mPaint);
        canvas.rotate(headAngle, lastP.x, lastP.y);
        headDst.set(((int) (lastP.x - head.getWidth() / 2)),
                ((int) (lastP.y - head.getHeight() / 2)),
                ((int) (lastP.x + head.getWidth() / 2)),
                ((int) (lastP.y + head.getHeight() / 2)));
        canvas.drawBitmap(head, null, headDst, mPaint);
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
                    if (eatSelf(x, y)) {
                        reset();
                    } else {
                        snakePath.lineTo(x, y);
                        if (feed(x, y)) {
                            randomFood();
                            length += baseLength * 0.15f;
                        }
                        lastP.set(x, y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private boolean eatSelf(float x, float y) {
        if (!drawPath.isEmpty()) {
            equalPath.reset();
            equalPath.addPath(drawPath);
            equalMeasure.setPath(equalPath, false);
            float len = 0;
            float[] pos = new float[2];
            while (len < equalMeasure.getLength() - 10f) {
                equalMeasure.getPosTan(len, pos, null);
                if (distance(x, y, pos[0], pos[1]) < 10f) {
                    return true;
                }
                len += 10f;
            }
        }
        return false;
    }

    private boolean canLine(float x, float y) {
        double dis = distance(x, y, lastP.x, lastP.y);
        return dis <= 60 && dis >= 7;
    }

    private boolean feed(float x, float y) {
        return distance(x, y, food.x, food.y) <= 30;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
