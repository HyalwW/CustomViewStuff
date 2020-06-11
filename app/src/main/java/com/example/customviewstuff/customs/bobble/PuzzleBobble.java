package com.example.customviewstuff.customs.bobble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.example.customviewstuff.Pool;
import com.example.customviewstuff.customs.BaseSurfaceView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/11
 * Description: blablabla
 */
public class PuzzleBobble extends BaseSurfaceView {
    private static final int[] colors = new int[]{0xFFFF1493, 0xFF4B0082, 0xFF1E90FF, 0xFF00FF7F, 0xFFFF1493, 0xFFFF1493};
    private Path levelPath;
    private PathMeasure measure;
    private static final long goForwardTime = 300;
    private float moveIncrement, radius, forwardIncrement;
    private List<Bobble> bobbles;
    private Pool<Bobble> pool;
    private Random random;
    private int level, nextLevel;

    public PuzzleBobble(Context context) {
        super(context);
    }

    public PuzzleBobble(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PuzzleBobble(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        levelPath = new Path();
        measure = new PathMeasure();
        bobbles = new CopyOnWriteArrayList<>();
        random = new Random();
        pool = new Pool<>(new Pool.Creator<Bobble>() {
            @Override
            public Bobble instance() {
                return new Bobble();
            }

            @Override
            public void reset(Bobble bobble) {
                bobble.reset();
            }

            @Override
            public boolean isLeisure(Bobble bobble) {
                return bobble.distance == -1;
            }
        });
    }

    @Override
    protected void onReady() {
        moveIncrement = Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.003f;
        radius = Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.01f;
        forwardIncrement = radius * UPDATE_RATE / goForwardTime;
        if (level == 0) {
            levelUp();
        }
    }

    private void levelUp() {
        nextLevel += 1;
    }

    private void reset(int level) {
        levelPath.reset();
        Level.level(level, getMeasuredWidth(), getMeasuredHeight()).getPath(levelPath);
        measure.setPath(levelPath, false);
        for (Bobble bobble : bobbles) {
            bobble.destroy();
        }
        bobbles.clear();
    }

    @Override
    protected void onDataUpdate() {
        if (level != nextLevel) {
            reset(++level);
        }
        if (bobbles.size() == 0) {
            bobbles.add(pool.get());
        } else {
            if (bobbles.get(0).distance >= radius * 2) {
                bobbles.add(0, pool.get());
            }
        }
        for (Bobble bobble : bobbles) {
            bobble.move();
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {

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

    class Bobble {
        int color;
        float[] pos;
        float distance, forwardDistance;

        Bobble() {
            pos = new float[2];
            reset();
        }

        void reset() {
            distance = 0;
            color = colors[random.nextInt(colors.length)];
        }

        void destroy() {
            distance = -1;
        }

        void move() {
            distance += moveIncrement;
            measure.getPosTan(distance, pos, null);
            if (forwardDistance > 0) {
                distance += forwardIncrement;
                forwardDistance -= forwardIncrement;
            }
        }

        void goForward() {
            forwardDistance = radius * 2;
        }

        void draw(Canvas canvas, Paint paint) {
            paint.setColor(color);
            canvas.drawCircle(pos[0], pos[1], radius, paint);
        }
    }

}
