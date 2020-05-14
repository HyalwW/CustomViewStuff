package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.Pool;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/14
 */
public class FadeAwayView extends BaseSurfaceView {
    private PathMeasure measure;
    private Pool<Star> pool;
    private Random random;
    private List<Star> stars;
    private float[] pos;

    public FadeAwayView(Context context) {
        super(context);
    }

    public FadeAwayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeAwayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        measure = new PathMeasure();
        pos = new float[2];
        random = new Random();
        stars = new CopyOnWriteArrayList<>();
        pool = new Pool<>(new Pool.Creator<Star>() {
            @Override
            public Star instance() {
                return new Star(0, 0, 9999);
            }

            @Override
            public void reset(Star star) {
                star.reset(0, 0, 9999);
            }

            @Override
            public boolean isLeisure(Star star) {
                return star.curTime >= star.duration;
            }
        });
    }

    @Override
    protected void onReady() {
        baseLen = getMeasuredHeight() * 0.03f;
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Star star : stars) {
            star.move();
            if (star.curTime >= star.duration) {
                stars.remove(star);
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (Star star : stars) {
            mPaint.setColor(star.color);
            measure.setPath(star.path, false);
            float ss = (float) star.curTime / star.duration;
            measure.getPosTan(ss * measure.getLength(), pos, null);
            canvas.drawCircle(pos[0], pos[1], star.radius * (1 - ss), mPaint);
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

    private class Star {
        float x, y, radius;
        int color;
        long curTime, duration;
        Path path;

        Star(float x, float y, float radius) {
            path = new Path();
            reset(x, y, radius);
        }

        private void reset(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            color = randomColor();
            curTime = 0;
            duration = randomDuration();
            path.reset();
            double direction = randomDirection();
            float[] pos = new float[6];
            int index = 0;
            for (int i = 1; i < 4; i++) {
                double angle = direction - baseAngle + random.nextFloat() * 2 * baseAngle;
                float len = baseLen * i + random.nextFloat() * getMeasuredWidth() * 0.2f;
                pos[index++] = (float) (x + Math.cos(angle) * len);
                pos[index++] = (float) (y + Math.sin(angle) * len);
            }
            path.moveTo(x, y);
            path.cubicTo(pos[0], pos[1], pos[2], pos[3], pos[4], pos[5]);
        }

        void move() {
            if (curTime < duration) {
                curTime += UPDATE_RATE;
            } else {
                curTime = duration;
            }
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private static final double baseAngle = Math.PI / 3;
    private float baseLen;

    private double randomDirection() {
        return 2 * Math.PI * random.nextFloat();
    }

    private long randomDuration() {
        return (long) (500 + random.nextFloat() * 1200);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (stars.size() < 500) {
                    for (int i = 0; i < 20; i++) {
                        Star star = pool.get();
                        star.reset(event.getX(), event.getY(), randomRadius());
                        stars.add(star);
                    }
                }
                break;
        }
        return true;
    }

    private float randomRadius() {
        return 0.05f + random.nextFloat() * getMeasuredWidth() * 0.05f;
    }
}
