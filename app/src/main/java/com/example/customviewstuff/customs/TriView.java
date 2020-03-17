package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/3/17
 */
public class TriView extends BaseSurfaceView {
    private Random random;
    //小边长度
    private float gap;
    //宽度分割块数
    private int triCount = 20;
    private Path path;
    private List<TriAngle> list, recycles;
    private TriPool pool;

    public TriView(Context context) {
        super(context);
    }

    public TriView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TriView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        path = new Path();
        list = new CopyOnWriteArrayList<>();
        recycles = new ArrayList<>();
        pool = new TriPool();
    }

    @Override
    protected void onReady() {
        gap = (float) getMeasuredWidth() / triCount;
        startAnim();
        doInThread(() -> {
            while (running) {
                for (int i = 0; i < 20; i++) {
                    list.add(pool.get());
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDataUpdate() {
        for (TriAngle triAngle : list) {
            triAngle.move();
            if (triAngle.isRecycled) {
                recycles.add(triAngle);
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
        for (TriAngle triAngle : list) {
            mPaint.setColor(triAngle.color);
            mPaint.setAlpha(triAngle.alpha);
            triAngle.getPath(path);
            canvas.drawPath(path, mPaint);
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

    private class TriAngle {
        PointF base, inline, p1, p2;
        long time, duration;
        int color, alpha;
        boolean isRecycled;

        TriAngle() {
            reset();
        }

        void reset() {
            base = p1 = p2 = null;
            PointF[] points = new PointF[3];
            points[0] = randomPoint();
            points[1] = new PointF(points[0].x, points[0].y + gap);
            points[2] = new PointF(random.nextInt(2) == 0 ? points[0].x - gap : points[0].x + gap, points[0].y + gap / 2);
            int anInt = random.nextInt(3);
            base = points[anInt];
            points[anInt] = null;
            int index = 0;
            while (index < points.length || p1 == null || p2 == null) {
                if (points[index] != null) {
                    if (p1 == null) {
                        p1 = points[index];
                    } else {
                        p2 = points[index];
                    }
                }
                index++;
            }
            inline = new PointF((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
            color = randomColor();
            alpha = randomAlpha();
            time = 0;
            duration = randomDuration();
            isRecycled = false;
        }

        void move() {
            if (time >= duration) {
                isRecycled = true;
                return;
            }
            time += 16;
        }

        void getPath(Path mPath) {
            mPath.reset();
            mPath.moveTo(p1.x, p1.y);
            mPath.lineTo(p2.x, p2.y);
            float x, y;
            if (time < duration / 4) {
                x = inline.x + (4f * time / duration) * (base.x - inline.x);
                y = inline.y + (4f * time / duration) * (base.y - inline.y);
            } else if (time < duration * 3 / 4) {
                x = base.x;
                y = base.y;
            } else {
                x = inline.x + ((duration - time) * 4f / duration) * (base.x - inline.x);
                y = inline.y + ((duration - time) * 4f / duration) * (base.y - inline.y);
            }
            mPath.lineTo(x, y);
            mPath.close();
        }
    }

    private class TriPool {
        private SparseArray<TriAngle> pools;

        TriPool() {
            pools = new SparseArray<>();
        }

        TriAngle get() {
            for (int i = 0; i < pools.size(); i++) {
                TriAngle triAngle = pools.valueAt(i);
                if (triAngle.isRecycled) {
                    triAngle.reset();
                    return triAngle;
                }
            }
            TriAngle triAngle = new TriAngle();
            pools.put(pools.size(), triAngle);
            return triAngle;
        }
    }

    private long randomDuration() {
        return 2000 + random.nextInt(1000);
    }

    private int randomAlpha() {
        return 20 + random.nextInt(235);
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private PointF randomPoint() {
        float x, y;
        int anInt = random.nextInt(triCount);
        x = anInt * gap;
        y = random.nextInt((int) (getMeasuredHeight() / gap)) * gap;
        if (anInt % 2 != 0) {
            y += gap / 2;
        }
        return new PointF(x, y);
    }
}
