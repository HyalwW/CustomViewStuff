package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by Wang.Wenhui
 * Date: 2020/3/20
 */
public class TextAnimView extends BaseSurfaceView {
    private final float TEXT_SIZE = 100;
    private Path path, drawPath;
    private long duration = 5000;
    private long time = 0;
    private PathMeasure measure;
    private int viewHeight = 1;

    public TextAnimView(Context context) {
        super(context);
    }

    public TextAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        path = new Path();
        drawPath = new Path();
        mPaint.setFakeBoldText(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        mPaint.setTextSize(TEXT_SIZE);
        measure = new PathMeasure();
    }

    @Override
    protected void onReady() {
        setText("这是一个文字路径，输入一些字符串试试吧！！");
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        time += 16;
        if (time >= duration * 1.2) {
            time = 0;
        }
        measure.setPath(path, false);
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        mPaint.setColor(Color.RED);
        do {
            drawPath.reset();
            measure.getSegment(0, (float) time / duration * measure.getLength(), drawPath, true);
            canvas.drawPath(drawPath, mPaint);
        } while (measure.nextContour());
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        viewHeight = Math.max(viewHeight, heightSize);
        setMeasuredDimension(widthSize, viewHeight);
    }

    public void setText(String text) {
        Path p = new Path();
        path.reset();
        float rowCount = 1, width = 0;
        for (int i = 0; i < text.length(); i++) {
            p.reset();
            String substring = text.substring(i, i + 1);
            float w = mPaint.measureText(substring);
            if (width + w >= getMeasuredWidth()) {
                width = 0;
                rowCount++;
            }
            mPaint.getTextPath(substring, 0, substring.length(), 0, 0, p);
            path.addPath(p, width, TEXT_SIZE * rowCount);
            width += w;
        }
        Path pp = new Path();
        pp.moveTo(5, 0);
        pp.lineTo(10, 5);
        pp.lineTo(0, 5);
        pp.close();
        PathEffect dash = new PathDashPathEffect(pp, 10, 0, PathDashPathEffect.Style.MORPH);
        PathEffect discreteEffect = new DiscretePathEffect(5, 5);
//        mPaint.setPathEffect(new ComposePathEffect(dash, discreteEffect));
        mPaint.setPathEffect(dash);
        measure.setPath(path, false);
        time = 0;
        viewHeight = (int) (TEXT_SIZE * rowCount) + 100;
        requestLayout();
    }
}
