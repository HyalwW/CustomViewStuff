package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.customviewstuff.Pool;
import com.example.customviewstuff.Reusable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/29
 * Description: blablabla
 */
public class HackView extends BaseSurfaceView {
    private long REMAIN_TIME = 600;
    private Random random;
    private Pool<Char> pool;
    private List<Char> list;

    public HackView(Context context) {
        super(context);
    }

    public HackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        pool = new Pool<>(Char::new);
        list = new CopyOnWriteArrayList<>();
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onReady() {
        UPDATE_RATE = 30;
        startAnim();
        doInThread(() -> {
            while (running) {
                Char aChar = pool.get();
                float textSize = textSize();
                aChar.set(random.nextFloat() * (getMeasuredWidth() - textSize), random.nextFloat() * getMeasuredHeight(), 0, maxLevel(), textSize);
                list.add(aChar);
                try {
                    Thread.sleep(REMAIN_TIME / 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private float textSize() {
        int min = Math.min(getMeasuredWidth(), getMeasuredHeight());
        return min * 0.015f + random.nextFloat() * min * 0.02f;
    }

    private int maxLevel() {
        return 20 + random.nextInt(10);
    }

    @Override
    protected void onDataUpdate() {
        if (onTouch && REMAIN_TIME < 3000) {
            REMAIN_TIME += 10;
        }
        if (!onTouch && REMAIN_TIME > 600) {
            REMAIN_TIME -= 10;
        }
        for (Char aChar : list) {
            aChar.move();
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        mPaint.setColor(Color.GREEN);
        for (Char aChar : list) {
            mPaint.setAlpha((int) (255 * (1 - (float) aChar.time / REMAIN_TIME)));
            mPaint.setTextSize(aChar.textSize);
            canvas.drawText(aChar.c, aChar.x, aChar.y, mPaint);
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

    class Char implements Reusable {
        float x, y, textSize;
        long time;
        int level, maxLevel;
        String c;
        Char next;

        Char() {
            c = ramdomC();
        }

        void set(float x, float y, int level, int maxLevel, float textSize) {
            this.x = x;
            this.y = y;
            this.level = level;
            this.maxLevel = maxLevel;
            this.textSize = textSize;
        }

        void move() {
            time += UPDATE_RATE;
            if (next == null && level < maxLevel && time >= REMAIN_TIME / 20) {
                next = pool.get();
                next.set(x, y + textSize, level + 1, maxLevel, textSize);
                list.add(next);
            }
            if (time > REMAIN_TIME) {
                list.remove(this);
            }
        }

        @Override
        public void reset() {
            c = ramdomC();
            time = 0;
            next = null;
            level = 0;
            maxLevel = 0;
            textSize = 0;
        }

        @Override
        public boolean isLeisure() {
            return time > REMAIN_TIME;
        }
    }

    private String ramdomC() {
//        return String.valueOf((char) (random.nextInt(Integer.MAX_VALUE)));
        return String.valueOf((char) (800 + random.nextInt(11100 - 800)));
    }

    private boolean onTouch;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouch = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouch = false;
                break;
        }
        return true;
    }
}
