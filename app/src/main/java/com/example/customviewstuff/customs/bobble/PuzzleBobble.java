package com.example.customviewstuff.customs.bobble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.customviewstuff.Pool;
import com.example.customviewstuff.Reusable;
import com.example.customviewstuff.customs.BaseSurfaceView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/11
 * Description: 泡泡龙游戏，目前只有第一关
 */
public class PuzzleBobble extends BaseSurfaceView {
    private static final int[] colors = new int[]{0xFFFF1493, 0xFF4B0082, 0xFF1E90FF, 0xFF00FF7F};
    private Path levelPath;
    private PathMeasure measure;
    private static final long goForwardTime = 300, shotCoolTime = 300;
    private long createBobbleBetween, createTime;
    private float moveIncrement, radius, forwardIncrement, backwardIncrement, shotIncrement;
    private List<Bobble> bobbles;
    private Pool<Bobble> bobblePool;
    private Random random;
    private int level, score, targetScore;

    private Pool<ShotBall> shotBallPool;
    private float baseShotX, baseShotY;
    private List<ShotBall> shotBalls;
    private long lastShotTime;
    private Level mLevel;

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
        bobblePool = new Pool<>(Bobble::new);
        shotBallPool = new Pool<>(ShotBall::new);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setFakeBoldText(true);
    }

    @Override
    protected void onReady() {
        int min = Math.min(getMeasuredWidth(), getMeasuredHeight());
        shotIncrement = min * 0.04f;
        radius = min * 0.03f;
        forwardIncrement = radius * 2 * UPDATE_RATE / goForwardTime;
        backwardIncrement = forwardIncrement * 2f;
        if (level == 0) {
            reset(++level);
        }
        startAnim();
    }


    private void reset(int level) {
        this.level = level;
        score = 0;
        mLevel = Level.level(level, getMeasuredWidth(), getMeasuredHeight(), radius);
        mLevel.getPath(levelPath);
        measure.setPath(levelPath, false);
        float[] shotPosition = mLevel.shotPosition();
        baseShotX = shotPosition[0];
        baseShotY = shotPosition[1];
        targetScore = mLevel.score();
        moveIncrement = mLevel.moveIncrement();
        createBobbleBetween = (long) (2 * radius / moveIncrement * UPDATE_RATE);
        for (Bobble bobble : bobbles) {
            bobble.destroy();
        }
        bobbles.clear();
        for (ShotBall shotBall : shotBalls) {
            shotBall.destroy();
        }
        shotBalls.clear();
        isFail = false;
    }

    @Override
    protected void onDataUpdate() {
        if (isFail && bobbles.size() == 0) {
            reset(level);
            isFail = false;
        }
        if (score >= targetScore) {
            reset(++level);
        }
        handleBobbleMove();
        handleShotBallMove();
    }

    private void handleBobbleMove() {
        if (!isFail) {
            if (bobbles.size() == 0) {
                bobbles.add(bobblePool.get());
                createTime = System.currentTimeMillis();
            } else {
                long now = System.currentTimeMillis();
                if (bobbles.get(0).distance >= radius * 2) {
                    if (now - createTime >= createBobbleBetween) {
                        bobbles.add(0, bobblePool.get());
                        createTime = now;
                    }
                }
            }
        }
        for (Bobble bobble : bobbles) {
            bobble.move();
            if (bobble.distance >= measure.getLength()) {
                bobbles.remove(bobble);
                if (!isFail) {
                    isFail = true;
                    moveIncrement *= 12;
                }
            }
        }
        adjustPositions();
    }

    private void adjustPositions() {
        if (bobbles.size() > 1) {
            int index = 1;
            while (index < bobbles.size() - 1) {
                Bobble bobble = bobbles.get(index);
                if (bobble.forwardDistance == 0 && bobble.backwardDistance == 0) {
                    float d = bobble.distance - bobbles.get(index - 1).distance;
                    if (d > 2 * radius) {
                        float b = d - 2 * radius;
                        for (int i = index; i < bobbles.size(); i++) {
                            bobbles.get(i).goBackWard(b);
                        }
                        break;
                    } else if (d < 2 * radius) {
                        float f = 2 * radius - d;
                        for (int i = index; i < bobbles.size(); i++) {
                            bobbles.get(i).goForward(f);
                        }
                        break;
                    }
                }
                index++;
            }
        }
    }

    private void handleShotBallMove() {
        if (shotBalls.size() == 0) {
            shotBalls.add(shotBallPool.get());
        }
        for (ShotBall shotBall : shotBalls) {
            shotBall.move();
            if (shotBall.r < getMeasuredHeight()) {
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
                        Bobble b = bobblePool.get();
                        b.color = shotBall.color;
                        b.distance = bobble.distance;
                        b.forwardDistance = bobble.forwardDistance;
                        b.backwardDistance = bobble.backwardDistance;
                        bobbles.add(index, b);
                        b.check(1);
                        shotBall.destroy();
                        shotBalls.remove(shotBall);
                        for (int j = i + 1; j < bobbles.size(); j++) {
                            bobbles.get(j).goForward(radius * 2);
                        }
                        break;
                    }
                }
            } else {
                shotBall.destroy();
                shotBalls.remove(shotBall);
                score--;
            }
        }
        if (shotBalls.size() > 0) {
            if (shotBalls.get(0).r != 0) {
                shotBalls.add(0, shotBallPool.get());
            }
        }
    }

    /**
     * 从index开始左右两边开始找相同颜色的球
     *
     * @param index
     */
    private void findAndRemove(int index) {
        Bobble select = bobbles.get(index);
        int color = select.color;
        int fIndex = index, bIndex = index;
        while (fIndex < bobbles.size() - 1 && bobbles.get(fIndex).color == color) {
            fIndex++;
        }
        if (bobbles.get(fIndex).color != color) {
            fIndex--;
        }
        while (bIndex > 0 && bobbles.get(bIndex).color == color) {
            bIndex--;
        }
        if (bobbles.get(bIndex).color != color) {
            bIndex++;
        }
        if (fIndex - bIndex > 1) {
            float backwardDistance = (fIndex - bIndex + 1) * 2 * radius;
            score += (fIndex - bIndex + 1) * select.combo;
            for (int i = fIndex + 1; i < bobbles.size(); i++) {
                Bobble bobble = bobbles.get(i);
                bobble.goBackWard(backwardDistance);
                if (i == fIndex + 1) {
                    bobble.check(select.combo + 1);
                }
            }
            List<Bobble> subList = bobbles.subList(bIndex, fIndex + 1);
            for (Bobble bobble : subList) {
                bobble.destroy();
            }
            bobbles.removeAll(subList);
        }
        select.combo = 0;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        drawPath(canvas);
        drawBobbles(canvas);
        drawShotBalls(canvas);
        drawScore(canvas);
    }

    private void drawPath(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        mPaint.setStrokeWidth(10f);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Paint.Style.STROKE);
        PathEffect effect = mLevel.getPathEffect();
        if (mLevel != null && effect != null) {
            mPaint.setPathEffect(effect);
        }
        canvas.drawPath(levelPath, mPaint);
        mPaint.setPathEffect(null);
    }

    private void drawBobbles(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        for (Bobble bobble : bobbles) {
            bobble.draw(canvas, mPaint);
        }
    }

    private void drawShotBalls(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        for (ShotBall shotBall : shotBalls) {
            shotBall.draw(canvas, mPaint);
        }
    }

    private void drawScore(Canvas canvas) {
        mPaint.setColor(Color.RED);
        float min = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mPaint.setTextSize(min * 0.06f);
        canvas.drawText("得分：" + score, getMeasuredWidth() >> 1, min * 0.08f, mPaint);
        mPaint.setColor(0xFFFF8C00);
        mPaint.setTextSize(min * 0.04f);
        canvas.drawText("目标分数：" + targetScore, getMeasuredWidth() >> 1, min * 0.14f, mPaint);
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
                shotDirection = Math.atan2(eventY - baseShotY, eventX - baseShotX);
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

    class Bobble implements Reusable {
        int color, combo;
        float[] pos;
        float distance, forwardDistance, backwardDistance;
        boolean needCheck;

        Bobble() {
            pos = new float[2];
            reset();
        }

        @Override
        public void reset() {
            distance = 0;
            color = colors[random.nextInt(colors.length)];
        }

        @Override
        public boolean isLeisure() {
            return distance == -1;
        }

        void destroy() {
            distance = -1;
            needCheck = false;
            pos[0] = pos[1] = 0;
            forwardDistance = backwardDistance = 0;
        }

        void move() {
            distance += moveIncrement;
            measure.getPosTan(distance, pos, null);
            if (forwardDistance > 0) {
                if (forwardDistance > forwardIncrement) {
                    distance += forwardIncrement;
                    forwardDistance -= forwardIncrement;
                } else {
                    distance += forwardDistance;
                    forwardDistance = 0;
                }
            }
            if (backwardDistance > 0) {
                if (backwardDistance > backwardIncrement) {
                    distance -= backwardIncrement;
                    backwardDistance -= backwardIncrement;
                } else {
                    distance -= backwardDistance;
                    backwardDistance = 0;
                }
            }
            if (forwardDistance == 0 && backwardDistance == 0) {
                if (needCheck) {
                    needCheck = false;
                    findAndRemove(bobbles.indexOf(this));
                }
            }
        }

        void goForward(float distance) {
            forwardDistance += distance;
        }

        void goBackWard(float distance) {
            backwardDistance += distance;
        }

        void draw(Canvas canvas, Paint paint) {
            paint.setColor(color);
            canvas.drawCircle(pos[0], pos[1], radius, paint);
        }

        void check(int combo) {
            needCheck = true;
            this.combo = combo;
        }
    }

    class ShotBall implements Reusable {
        int color;
        double direction;
        float r, x, y;

        ShotBall() {
            reset();
        }

        @Override
        public void reset() {
            color = colors[random.nextInt(colors.length)];
            direction = 99999;
            r = 0;
            x = baseShotX;
            y = baseShotY;
        }

        @Override
        public boolean isLeisure() {
            return r == 999999;
        }

        void move() {
            if (direction != 99999) {
                r += shotIncrement;
                x = (float) (baseShotX + r * Math.cos(direction));
                y = (float) (baseShotY + r * Math.sin(direction));
            }
        }

        void draw(Canvas canvas, Paint paint) {
            paint.setColor(color);
            canvas.drawCircle(x, y, radius, paint);
        }

        void destroy() {
            r = 999999;
            direction = 99999;
        }
    }

}
