package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.R;

import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/3/9
 */
public class ArrowView extends BaseSurfaceView {
    private Path mPath;
    private Bitmap arrow;
    private PathMeasure measure;
    private boolean flying;
    private Rect src, dst;
    private Random random;

    public ArrowView(Context context) {
        super(context);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        mPath = new Path();
        arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        measure = new PathMeasure();
        dst = new Rect();
        src = new Rect(0, -arrow.getHeight(), arrow.getWidth(), arrow.getHeight() * 2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.CYAN);
        mPaint.setPathEffect(new CornerPathEffect(20));
        random = new Random();
    }

    @Override
    protected void onReady() {
        callDraw("clear");
    }

    @Override
    protected void onDataUpdate() {

    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(randomColor());
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        canvas.drawColor(Color.WHITE);
        if (data instanceof Path) {
            Path path = (Path) data;
            canvas.drawPath(path, mPaint);
        } else if (data instanceof Float) {
            float length = (float) data;
            float[] pos = new float[2];
            float[] tan = new float[2];
            measure.getPosTan(length, pos, tan);
            canvas.save();
            dst.set((int) pos[0] - arrow.getWidth() / 2,
                    (int) pos[1] - arrow.getHeight() / 2,
                    (int) pos[0] + arrow.getWidth() / 2,
                    (int) pos[1] + arrow.getHeight() / 2);
            float degress = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI) + 90;
            canvas.rotate(degress, pos[0], pos[1]);
            canvas.drawBitmap(arrow, null, dst, mPaint);
            canvas.restore();
            Path path = new Path();
            measure.getSegment(length - getMeasuredHeight() * 0.2f, length, path, true);
            canvas.drawPath(path, mPaint);
            if (length < measure.getLength() && flying) {
                length += (float) getMeasuredHeight() / 70;
                callDrawDelay(length, 16);
            }
        } else if (data instanceof String) {
            canvas.drawColor(Color.WHITE);
        }
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
                flying = false;
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                callDraw(mPath);
                break;
            case MotionEvent.ACTION_UP:
                measure.setPath(mPath, false);
                flying = true;
                callDraw(0f);
                break;
        }
        return true;
    }

}
