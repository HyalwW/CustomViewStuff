package com.example.customviewstuff.customs.bobble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

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
    private static final long goForwardTime = 300, shotCoolTime = 1000;
    private float moveIncrement, baseIncrement, radius, forwardIncrement, backwardIncrement, shotIncrement;
    private List<Bobble> bobbles;
    private Pool<Bobble> pool;
    private Random random;
    private int level, nextLevel;


    private List<ShotBall> shotBalls;
    private long lastShotTime;

    private boolean isFail;

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
        shotBalls = new CopyOnWriteArrayList<>();
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
        moveIncrement = baseIncrement = Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.0012f;
        shotIncrement = Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.04f;
        radius = Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.03f;
        forwardIncrement = radius * 2 * UPDATE_RATE / goForwardTime;
        if (level == 0) {
            levelUp();
        }
        startAnim();
    }

    private void levelUp() {
        nextLevel += 1;
    }

    private void reset(int level) {
        levelPath.reset();
        Level.level(level, getMeasuredWidth(), getMeasuredHeight(), radius).getPath(levelPath);
        measure.setPath(levelPath, false);
        for (Bobble bobble : bobbles) {
            bobble.destroy();
        }
        bobbles.clear();
        isFail = false;
        moveIncrement = baseIncrement;
    }

    @Override
    protected void onDataUpdate() {
        if (level != nextLevel) {
            reset(++level);
        }
        if (!isFail) {
            if (bobbles.size() == 0) {
                bobbles.add(pool.get());
            } else {
                if (bobbles.get(0).distance >= radius * 2) {
                    bobbles.add(0, pool.get());
                }
            }
        }
        for (Bobble bobble : bobbles) {
            bobble.move();
            if (bobble.distance >= measure.getLength()) {
                bobbles.remove(bobble);
                if (!isFail) {
                    isFail = true;
                    moveIncrement = baseIncrement * 10;
                }
            }
        }
        if (shotBalls.size() == 0) {
            shotBalls.add(new ShotBall());
        }
        for (ShotBall shotBall : shotBalls) {
            shotBall.move();
            if (distance(shotBall.x, shotBall.y, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f) < getMeasuredHeight()) {
                for (int i = bobbles.size() - 1; i >= 0; i--) {
                    Bobble bobble = bobbles.get(i);
                    if (distance(bobble.pos[0], bobble.pos[1], shotBall.x, shotBall.y) <= 2 * radius) {
                        int index = i;
                        if (index > 0 && index < bobbles.size() - 1) {
                            Bobble before = bobbles.get(index - 1);
                            Bobble after = bobbles.get(index + 1);
                            if (distance(before.pos[0], before.pos[1], shotBall.x, shotBall.y) > distance(after.pos[0], after.pos[1], shotBall.x, shotBall.y)) {
                                index++;
                            }
                        }
                        Bobble b = pool.get();
                        b.color = shotBall.color;
                        b.distance = bobble.distance;
                        bobbles.add(index, b);
                        shotBalls.remove(shotBall);
                        for (int j = i + 1; j < bobbles.size(); j++) {
                            bobbles.get(j).goForward();
                        }
                        break;
                    }
                }
            } else {
                shotBalls.remove(shotBall);
            }
        }
        if (shotBalls.get(0).r != 0) {
            shotBalls.add(0, new ShotBall());
        }
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        mPaint.setStrokeWidth(10f);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(levelPath, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        for (Bobble bobble : bobbles) {
            bobble.draw(canvas, mPaint);
        }
        for (ShotBall shotBall : shotBalls) {
            shotBall.draw(canvas, mPaint);
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

    private double shotDirection;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isFail) return false;
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                shotDirection = Math.atan2(eventY - getMeasuredHeight() / 2f, eventX - getMeasuredWidth() / 2f);
                break;
            case MotionEvent.ACTION_UP:
                long nowTime = System.currentTimeMillis();
                if (nowTime - lastShotTime > shotCoolTime) {
                    shotBalls.get(0).direction = shotDirection;
                    lastShotTime = nowTime;
                }
                break;
        }
        return true;
    }

    class Bobble {
        int color;
        float[] pos;
        float distance, forwardDistance, backwardDistance;

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
            if (backwardDistance > 0) {
                distance -= backwardIncrement;
                backwardDistance -= backwardIncrement;
            }
        }

        void goForward() {
            forwardDistance += radius * 2;
        }

        void goBackWard(float distance) {
            backwardDistance += distance;
        }

        void draw(Canvas canvas, Paint paint) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawCircle(pos[0], pos[1], radius, paint);
        }
    }

    class ShotBall {
        int color;
        double direction;
        float r, x, y;

        ShotBall() {
            reset();
        }

        void reset() {
            color = colors[random.nextInt(colors.length)];
            direction = 99999;
            r = 0;
            x = getMeasuredWidth() / 2f;
            y = getMeasuredHeight() / 2f;
        }

        void move() {
            if (direction != 99999) {
                r += shotIncrement;
                x = (float) ((getMeasuredWidth() >> 1) + r * Math.cos(direction));
                y = (float) ((getMeasuredHeight() >> 1) + r * Math.sin(direction));
            }
        }

        void draw(Canvas canvas, Paint paint) {
            paint.setColor(color);
            canvas.drawCircle(x, y, radius, paint);
        }
    }

}
