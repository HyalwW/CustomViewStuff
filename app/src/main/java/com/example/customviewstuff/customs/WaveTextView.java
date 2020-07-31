package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/30
 */
public class WaveTextView extends BaseSurfaceView {
    private List<Text> texts;
    private static final int textInLine = 16;
    private PointF breakPoint;
    private float range, maxRange, increment;
    private boolean spreading, draw;
    private static final long duration = 2000;
    private static final float maxScale = 1.2f;
    private Rect dirty;
    private Runnable drawRun = () -> {
        while (draw || spreading) {
            draw = false;
            range += increment;
            if (range > maxRange) {
                spreading = false;
            }
            for (Text text : texts) {
                text.move();
            }
            try {
                Thread.sleep(UPDATE_RATE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callDraw("draw", dirty);
        }
        breakPoint.set(0, 0);
        range = 0;
    };

    public WaveTextView(Context context) {
        super(context);
    }

    public WaveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        mPaint.setColor(Color.WHITE);
        texts = new CopyOnWriteArrayList<>();
        breakPoint = new PointF(0, 0);
        dirty = new Rect();
    }

    @Override
    protected void onReady() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append("测试测试测试测试测试测试测试测试");
        }
        setText(builder.toString());
        increment = getMeasuredWidth() * 0.01f;
    }

    @Override
    protected void onDataUpdate() {
    }

    @Override
    protected void onRefresh(Canvas canvas) {
    }

    @Override
    protected void draw(Canvas canvas, Object data) {

    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {
        mPaint.setStyle(Paint.Style.FILL);
        for (Text text : texts) {
            mPaint.setTextSize(text.cs);
            canvas.drawText(text.str, text.x, text.y, mPaint);
        }
    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    public void setText(String string) {
        texts.clear();
        dirty.set(0, 0, 0, 0);
        int row = 0;
        for (int i = 0; i < string.length(); i++) {
            String value = String.valueOf(string.charAt(i));
            int col = i % textInLine;
            if (col == 0) {
                row++;
            }
            Text text = new Text();
            text.reset(value, col * text.baseSize, row * text.baseSize);
            dirty.union((int) (text.x + text.baseSize), (int) (text.y + text.baseSize));
            texts.add(text);
        }
        callDraw("draw", dirty);
    }

    private class Text {
        String str;
        float x, y, baseSize;
        float cs;
        float mx;
        final double max = Math.PI * 9;


        Text() {
            reset("", 0, 0);
        }

        void reset(String str, float x, float y) {
            this.str = str;
            this.x = x;
            this.y = y;
            cs = baseSize = (float) getMeasuredWidth() / textInLine;
        }

        void move() {
            if (mx == 0) {
                if (!spreading) return;
                double dis = dis(x, y);
                if (dis >= range - increment && dis <= range + increment) {
                    draw = true;
                    mx += max * UPDATE_RATE / duration;
                    double scale = (1 - mx / max) * maxScale;
                    cs = (float) (baseSize * scale * Math.sin(mx));
                }
            } else {
                Log.e("wwh", "Text --> move: " + mx + "   " + max);
                draw = true;
                mx += max * UPDATE_RATE / duration;
                if (mx >= max) {
                    mx = 0;
                }
                double scale = (1 - mx / max) * maxScale;
                cs = baseSize + (float) (baseSize * scale * Math.sin(mx));
            }
        }

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
        return Math.max(dis(dirty.right, dirty.bottom), Math.max(dis(dirty.right, dirty.top), Math.max(dis(dirty.left, dirty.top), dis(dirty.left, dirty.bottom))));
    }

    private float dis(float x, float y) {
        return (float) Math.sqrt((x - breakPoint.x) * (x - breakPoint.x) + (y - breakPoint.y) * (y - breakPoint.y));
    }
}
