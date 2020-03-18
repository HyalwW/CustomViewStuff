package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/7
 */
public class WebView extends BaseSurfaceView {
    private Random random;
    private List<WebDot> dots;

    public WebView(Context context) {
        this(context, null);
    }

    public WebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void drawDots(Canvas canvas) {
        for (WebDot dot : dots) {
            mPaint.setColor(Color.rgb(dot.r, dot.g, dot.b));
            canvas.drawCircle(dot.x, dot.y, 2, mPaint);
        }
    }

    private void drawLines(Canvas canvas) {
        Collections.sort(dots);
        for (int i = 0; i < dots.size() - 1; i++) {
            for (int j = i + 1; j < dots.size(); j++) {
                WebDot sd = dots.get(i), ed = dots.get(j);
                float distance = sd.distance(ed);
                if (Math.abs(sd.x - ed.x) > 200)
                    break;
                if (distance < 200 && sd != ed) {
                    int alpha;
                    if (distance < 150) {
                        alpha = 255;
                    } else {
                        alpha = (int) (255 - 255 * (distance - 150) / 50);
                    }
                    mPaint.setColor(Color.argb(alpha, (sd.r + ed.r) / 2, (sd.g + ed.g) / 2, (sd.b + ed.b) / 2));
                    canvas.drawLine(sd.x, sd.y, ed.x, ed.y, mPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                stopAnim();
                break;
            case MotionEvent.ACTION_MOVE:
                callDraw("move");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                startAnim();
                break;
        }
        return true;
    }

    @Override
    protected void onInit() {
        random = new Random();
    }

    @Override
    protected void onReady() {
        if (dots == null) {
            dots = new ArrayList<>();
            for (int i = 0; i < 120; i++) {
                dots.add(new WebDot());
            }
        }
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        synchronized (dots) {
            for (WebDot dot : dots) {
                dot.move();
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        drawDots(canvas);
        drawLines(canvas);
    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        if (data instanceof String) {
            drawDots(canvas);
            drawLines(canvas);
        }
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    private class WebDot implements Comparable<WebDot> {

        float x, y, icmX, icmY;
        int r, g, b;

        WebDot() {
            x = random.nextInt(getMeasuredWidth());
            y = random.nextInt(getMeasuredHeight());
            icmX = -5 + random.nextFloat() * 10;
            icmY = -5 + random.nextFloat() * 10;
            r = random.nextInt(255);
            g = random.nextInt(255);
            b = random.nextInt(255);
        }

        void move() {
            if (x > getMeasuredWidth() || x < 0) {
                if (x > getMeasuredWidth()) {
                    x = getMeasuredWidth();
                } else {
                    x = 0;
                }
                icmX = -icmX;
            }
            if (y > getMeasuredHeight() || y < 0) {
                if (y > getMeasuredHeight()) {
                    y = getMeasuredHeight();
                } else {
                    y = 0;
                }
                icmY = -icmY;
            }
            x += icmX;
            y += icmY;
        }

        float distance(WebDot dot) {
            return (float) Math.sqrt((x - dot.x) * (x - dot.x) + (y - dot.y) * (y - dot.y));
        }

        @Override
        public int compareTo(WebDot o) {
            return ((int) x - (int) o.x);
        }
    }
}
