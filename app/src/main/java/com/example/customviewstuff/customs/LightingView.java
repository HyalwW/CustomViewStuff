package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/2
 */
public class LightingView extends BaseSurfaceView {
    //闪电形成时间
    private static final long GROW_TIME = 50;
    //闪电持续时间
    private static final long SHOW_TIME = 600;
    //闪电消失时间
    private static final long DISAPPEAR_TIME = 150;
    private static long DURATION = GROW_TIME + SHOW_TIME + DISAPPEAR_TIME;
    private Random random;
    private List<Lighting> lightings;
    private static final float pi = (float) Math.PI;
    private float getChildProb = 0.25f;
    private Path drawPath;
    private LightingPool pool;

    public LightingView(Context context) {
        super(context);
    }

    public LightingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        lightings = new CopyOnWriteArrayList<>();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        drawPath = new Path();
        pool = new LightingPool();
    }

    @Override
    protected void onReady() {
        start();
    }

    private void start() {
        lightings.clear();
        lightings.add(createRoot());
        doInThread(() -> {
            while (true) {
                for (Lighting lighting : lightings) {
                    if (lighting.move() && !lighting.hasChild) {
                        lighting.hasChild = true;
                        if (lighting.serial > 1) {
                            Lighting sl = pool.get();
                            sl.set(lighting.ex, lighting.ey, sameLevelAngle(lighting.angle), lighting.level, lighting.serial - 1, lighting.width * 0.9f, childLength(lighting.length, true));
                            lightings.add(sl);
                        }
                        if (lighting.level < 4) {
                            if (random.nextFloat() > getChildProb * lighting.level) {
                                Lighting lc = pool.get();
                                lc.set(lighting.ex, lighting.ey, childAngle(lighting.angle, false), lighting.level + 1, getSerial(lighting.level + 1), lighting.width * 0.8f, childLength(lighting.length, false));
                                lightings.add(lc);
                            }
                            if (random.nextFloat() > getChildProb * lighting.level) {
                                Lighting rc = pool.get();
                                rc.set(lighting.ex, lighting.ey, childAngle(lighting.angle, true), lighting.level + 1, getSerial(lighting.level + 1), lighting.width * 0.8f, childLength(lighting.length, false));
                                lightings.add(rc);
                            }
                        }
                    }
                    if (lighting.time > DURATION) {
                        lightings.remove(lighting);
                    }
                }
                if (lightings.size() == 0) {
                    sleep(2000);
                    lightings.add(createRoot());
                }
                callDraw("lighting");
                sleep(UPDATE_RATE);
            }
        });
    }

    private Lighting createRoot() {
        Lighting root = pool.get();
        root.set(getMeasuredWidth() >> 1, 0, pi / 2, 1, 7, getMeasuredWidth() * 0.016f, getMeasuredHeight() * 0.15f);
//        Lighting root = pool.get();
//        root.set(getMeasuredWidth() >> 1, getMeasuredHeight(), -pi / 2, 1, 7, getMeasuredWidth() * 0.016f, getMeasuredHeight() * 0.15f);
        return root;
    }

    private float childLength(float length, boolean isSame) {
        return isSame ? length * 0.9f + length * 0.1f * random.nextFloat()
                : length * 0.4f + length * 0.25f * random.nextFloat();
//        return isSame ? length * 0.9f + length * 0.1f * random.nextFloat()
//                : length * 0.6f + length * 0.2f * random.nextFloat();
    }

    private int getSerial(int level) {
//        return (int) (7 - level * 1.5);
        return 7 - level;
    }

    private float childAngle(float angle, boolean isRight) {
        return isRight ? angle + pi / 8f + random.nextFloat() * pi / 8f
                : angle - pi / 8f - random.nextFloat() * pi / 8f;
    }

    private float sameLevelAngle(float angle) {
        //(angle-15 ~ angle+15)
        return angle - pi / 12 + random.nextFloat() * pi / 6;
    }

    @Override
    protected void onDataUpdate() {
    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        if (data instanceof String) {
            for (Lighting lighting : lightings) {
                mPaint.setAlpha((int) (255 * lighting.alpha));
                drawPath.reset();
                drawPath.moveTo(lighting.sx, lighting.sy);
                drawPath.lineTo(lighting.ex, lighting.ey);
                mPaint.setStrokeWidth(lighting.width);
                canvas.drawPath(drawPath, mPaint);
            }
        }
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    private class Lighting {
        private float angle;
        float sx, sy, ex, ey;
        int level, serial;
        long time;
        float length, width, alpha;
        boolean hasChild;

        Lighting() {
        }

        void set(float sx, float sy, float angle, int level, int serial, float width, float length) {
            this.ex = this.sx = sx;
            this.ey = this.sy = sy;
            this.angle = angle;
            this.level = level;
            this.serial = serial;
            this.length = length;
            this.width = width;
            hasChild = false;
            time = 0;
            alpha = 1;
        }

        boolean move() {
            float trueLen = (float) time / GROW_TIME * length;
            if (time >= GROW_TIME) {
                trueLen = length;
            }
            if (time > GROW_TIME + SHOW_TIME) {
                alpha = 1 - (time - (GROW_TIME + SHOW_TIME)) / (float) DISAPPEAR_TIME;
            }
            this.ex = (float) (trueLen * Math.cos(angle) + sx);
            this.ey = (float) (trueLen * Math.sin(angle) + sy);
            time += UPDATE_RATE;
            return time > GROW_TIME;
        }
    }

    private class LightingPool {
        private SparseArray<Lighting> pools;

        LightingPool() {
            pools = new SparseArray<>();
        }

        public synchronized Lighting get() {
            for (int i = 0; i < pools.size(); i++) {
                if (pools.valueAt(i).time > DURATION + 10) {
                    return pools.valueAt(i);
                }
            }
            Lighting lighting = new Lighting();
            pools.put(pools.size(), lighting);
            return lighting;
        }
    }
}
