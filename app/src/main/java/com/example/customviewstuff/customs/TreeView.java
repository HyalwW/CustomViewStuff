package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.example.customviewstuff.Pool;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Description:BFS
 */
public class TreeView extends BaseSurfaceView {
    private static final long GROW_TIME = 50;
    private static final long SHOW_TIME = 10000;
    private static final long DISAPPEAR_TIME = 150;
    private static long DURATION = GROW_TIME + SHOW_TIME + DISAPPEAR_TIME;
    private Random random;
    private List<Lighting> lightings;
    private static final float pi = (float) Math.PI;
    private float getChildProb = 0.12f;
    private Path drawPath;
    private Pool<Lighting> pool;
    private float minX, maxX;
    private boolean grow;

    public TreeView(Context context) {
        super(context);
    }

    public TreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        lightings = new CopyOnWriteArrayList<>();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        drawPath = new Path();
        pool = new Pool<>(2000, new Pool.Creator<Lighting>() {
            @Override
            public Lighting instance() {
                return new Lighting();
            }

            @Override
            public void reset(Lighting lighting) {

            }

            @Override
            public boolean isLeisure(Lighting lighting) {
                return lighting.time > DURATION;
            }
        });
    }

    @Override
    protected void onReady() {
        grow = true;
        start();
    }

    private void start() {
        lightings.clear();
        lightings.add(createRoot());
        doInThread(() -> {
            while (grow) {
                for (Lighting lighting : lightings) {
                    if (lighting.move() && !lighting.hasChild) {
                        lighting.hasChild = true;
                        if (lighting.serial > 1) {
                            Lighting sl = pool.get();
                            sl.set(lighting.ex, lighting.ey, sameLevelAngle(lighting.angle), lighting.level, lighting.serial - 1, lighting.width * 0.9f, childLength(lighting.length, true));
                            lightings.add(sl);
                        }
                        if (lighting.level < 7) {
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
        minX = 0;
        maxX = getMeasuredWidth();
        Lighting root = pool.get();
        root.set(getMeasuredWidth() >> 1, getMeasuredHeight(), -pi / 2, 1, 7, getMeasuredWidth() * 0.02f, getMeasuredHeight() * 0.15f);
        return root;
    }

    private float childLength(float length, boolean isSame) {
        return isSame ? length * 0.8f + length * 0.15f * random.nextFloat()
                : length * 0.5f + length * 0.2f * random.nextFloat();
    }

    private int getSerial(int level) {
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
            if (maxX > getMeasuredWidth() || minX < 0) {
                float half = getMeasuredWidth() >> 1;
                float scale = Math.min(half / (half - minX), half / (maxX - half));
                canvas.scale(scale, scale, getMeasuredWidth() >> 1, getMeasuredHeight());
            }
            for (Lighting lighting : lightings) {
                if (lighting.color != 0) {
                    mPaint.setColor(lighting.color);
                } else {
                    mPaint.setColor(Color.WHITE);
                }
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

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        grow = false;
    }

    private class Lighting {
        private float angle;
        float sx, sy, ex, ey;
        int level, serial;
        long time;
        float length, width, alpha;
        boolean hasChild;
        int color;

        Lighting() {
        }

        void set(float sx, float sy, float angle, int level, int serial, float width, float length) {
            this.ex = this.sx = sx;
            this.ey = this.sy = sy;
            minX = Math.min(minX, ex);
            maxX = Math.max(maxX, ex);
            this.angle = angle;
            this.level = level;
            this.serial = serial;
            this.length = length;
            this.width = width;
            hasChild = false;
            time = 0;
            alpha = 1;
            if (level > 5) {
                color = randomColor();
            }
        }

        boolean move() {
            if (time > DURATION) {
                return false;
            }
            float trueLen = (float) time / GROW_TIME * length;
            if (time >= GROW_TIME) {
                trueLen = length;
            }
            if (time > GROW_TIME + SHOW_TIME) {
                alpha = 1 - (time - (GROW_TIME + SHOW_TIME)) / (float) DISAPPEAR_TIME;
            }
            this.ex = (float) (trueLen * Math.cos(angle) + sx);
            this.ey = (float) (trueLen * Math.sin(angle) + sy);
            minX = Math.min(minX, ex);
            maxX = Math.max(maxX, ex);
            time += UPDATE_RATE;
            return time > GROW_TIME;
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

}
