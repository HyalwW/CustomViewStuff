package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.example.customviewstuff.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/3/16
 * Description:一个蹦爱心的view
 */
public class HeartView extends BaseSurfaceView {
    private static final long BASE_TIME = 500;
    private static final float G = 6000;
    private Random random;
    private List<Heart> list, recycles;
    private HeartPool pool;
    private Bitmap bitmap;
    private int h, w;
    private RectF dst;

    public HeartView(Context context) {
        super(context);
    }

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        list = new ArrayList<>();
        recycles = new ArrayList<>();
        pool = new HeartPool();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        h = bitmap.getHeight() / 2;
        w = bitmap.getWidth() / 2;
        dst = new RectF();
    }

    @Override
    protected void onReady() {
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Heart heart : list) {
            heart.move();
            if (heart.isRecycled) {
                recycles.add(heart);
            }
        }
        if (recycles.size() > 0) {
            list.removeAll(recycles);
            recycles.clear();
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (Heart heart : list) {
            mPaint.setAlpha((int) (255 * heart.alpha));
            dst.set(heart.cx - w, heart.cy - h, heart.cx + w, heart.cy + h);
            canvas.drawBitmap(bitmap, null, dst, mPaint);
        }
        mPaint.setAlpha(255);
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                safeModifyData(() -> {
                    if (list.size() < 40) {
                        list.add(pool.get(event.getX(), getMeasuredHeight() - event.getY()));
                    }
                });
                break;
        }
        return true;
    }

    private class HeartPool {
        private SparseArray<Heart> pool;

        HeartPool() {
            pool = new SparseArray<>();
        }

        synchronized Heart get(float x, float y) {
            for (int i = 0; i < pool.size(); i++) {
                Heart heart = pool.valueAt(i);
                if (heart.isRecycled) {
                    heart.reset(x, y);
                    return heart;
                }
            }
            Heart heart = new Heart(x, y);
            pool.put(pool.size(), heart);
            return heart;
        }
    }

    private class Heart {
        float xSpeed, ySpeed, alpha;
        float baseX, cx, baseY, cy;
        long time;
        boolean isRecycled;

        Heart(float baseX, float baseY) {
            reset(baseX, baseY);
        }

        void move() {
            time += 16;
            alpha = 1 - (float) time / BASE_TIME;
            float t = (float) time / 1000;
            cx = baseX + xSpeed * t;
            cy = getMeasuredHeight() - (baseY + ySpeed * t - G * t * t / 2);
            if (time >= BASE_TIME) {
                isRecycled = true;
            }
        }

        void reset(float baseX, float baseY) {
            this.baseX = baseX;
            this.baseY = baseY;
            xSpeed = randomXSpeed();
            ySpeed = randomYSpeed();
            time = 0;
            alpha = 1;
            isRecycled = false;
        }
    }

    private float randomXSpeed() {
        float v = 100 + random.nextFloat() * 300;
        return random.nextInt(2) == 0 ? v : 0 - v;
    }

    private float randomYSpeed() {
        return 600 + random.nextFloat() * 600;
    }
}
