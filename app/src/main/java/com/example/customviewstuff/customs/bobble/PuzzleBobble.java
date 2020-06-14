package com.example.customviewstuff.customs.bobble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.customviewstuff.Pool;
import com.example.customviewstuff.R;
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
    //    private static final int[] colors = new int[]{0xFFFF1493, 0xFF4B0082, 0xFF1E90FF, 0xFF00FF7F};
    private Bitmap[] balls;
    private Path levelPath;
    private PathMeasure measure;
    private static final long goForwardTime = 300, shotCoolTime = 400;
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

    private boolean isFail, isSuccess;
    private PathEffect effect;

    private boolean isCustom;
    private int customState;
    private CustomLevel customLevel;
    private RectF dst;

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
        dst = new RectF();
        balls = new Bitmap[5];
        balls[0] = BitmapFactory.decodeResource(getResources(), R.drawable.ball_red);
        balls[1] = BitmapFactory.decodeResource(getResources(), R.drawable.ball_green);
        balls[2] = BitmapFactory.decodeResource(getResources(), R.drawable.ball_blue);
        balls[3] = BitmapFactory.decodeResource(getResources(), R.drawable.ball_yellow);
        balls[4] = BitmapFactory.decodeResource(getResources(), R.drawable.ball_black);
        targetScore = 99999;
    }

    @Override
    protected void onReady() {
        int min = Math.min(getMeasuredWidth(), getMeasuredHeight());
        shotIncrement = min * 0.045f;
        radius = min * 0.03f;
        forwardIncrement = radius * 2 * UPDATE_RATE / goForwardTime;
        backwardIncrement = forwardIncrement * 2f;
        if (level == 0) {
            reset(1);
        }
        startAnim();
    }


    private void reset(int level) {
        if (level > 5) {
            level = 5;
        }
        this.level = level;
        score = 0;
        if (!isCustom) {
            mLevel = Level.level(level, getMeasuredWidth(), getMeasuredHeight(), radius);
        }
        if (!isCustom && mLevel instanceof CustomLevel) {
            isCustom = true;
            customLevel = (CustomLevel) mLevel;
            levelPath.reset();
            stopAnim();
            callDraw("custom");
        }
        if (isCustom) {
            if (!customLevel.isReady()) {
                customState = 1;
                return;
            } else {
                isCustom = false;
            }
        }
        mLevel.getPath(levelPath);
        measure.setPath(levelPath, false);
        float[] shotPosition = mLevel.shotPosition();
        baseShotX = shotPosition[0];
        baseShotY = shotPosition[1];
        targetScore = mLevel.score();
        moveIncrement = mLevel.moveIncrement();
        createBobbleBetween = (long) (2 * radius / moveIncrement * UPDATE_RATE);
        effect = mLevel.getPathEffect();
        for (Bobble bobble : bobbles) {
            bobble.destroy();
        }
        bobbles.clear();
        for (ShotBall shotBall : shotBalls) {
            shotBall.destroy();
        }
        shotBalls.clear();
        isFail = false;
        customLevel = null;
    }

    @Override
    protected void onDataUpdate() {
        if (!isCustom) {
            if (isFail && bobbles.size() == 0) {
                reset(level);
                isFail = false;
            }
            if (score >= targetScore) {
                isSuccess = true;
            }
            handleBobbleMove();
            handleShotBallMove();
            if (isSuccess) {
                if (bobbles.size() > 0) {
                    bobbles.remove(bobbles.size() - 1);
                } else {
                    reset(++level);
                    isSuccess = false;
                }
            }
        }
    }

    private void handleBobbleMove() {
        if (!isFail && !isSuccess) {
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
                        if (shotBall.type < 4) {
                            if (index > 0 && index < bobbles.size() - 1) {
                                Bobble before = bobbles.get(index - 1);
                                Bobble after = bobbles.get(index + 1);
                                if (distance(before.pos[0], before.pos[1], shotBall.x, shotBall.y) > distance(after.pos[0], after.pos[1], shotBall.x, shotBall.y)) {
                                    index++;
                                }
                            }
                            Bobble b = bobblePool.get();
                            b.type = shotBall.type;
                            b.distance = bobble.distance;
                            b.forwardDistance = bobble.forwardDistance;
                            b.backwardDistance = bobble.backwardDistance;
                            bobbles.add(index, b);
                            b.check(1);
                            for (int j = i + 1; j < bobbles.size(); j++) {
                                bobbles.get(j).goForward(radius * 2);
                            }
                        } else {
                            int front = index, back = index;
                            while (back > 0 && back > index - 2) {
                                back--;
                            }
                            while (front < bobbles.size() - 1 && front < index + 2) {
                                front++;
                            }
                            List<Bobble> subList = bobbles.subList(back, front + 1);
                            score += subList.size();
                            for (int j = front + 1; j < bobbles.size(); j++) {
                                Bobble b = bobbles.get(j);
                                b.goBackWard(subList.size() * 2 * radius);
                                if (j == front + 1) {
                                    b.check(1);
                                }
                            }
                            bobbles.removeAll(subList);
                        }
                        shotBall.destroy();
                        shotBalls.remove(shotBall);
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
        int type = select.type;
        int fIndex = index, bIndex = index;
        while (fIndex < bobbles.size() - 1 && bobbles.get(fIndex).type == type) {
            fIndex++;
        }
        if (bobbles.get(fIndex).type != type) {
            fIndex--;
        }
        while (bIndex > 0 && bobbles.get(bIndex).type == type) {
            bIndex--;
        }
        if (bobbles.get(bIndex).type != type) {
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
        canvas.drawColor(Color.WHITE);
        drawPath(canvas);
        drawBobbles(canvas);
        drawShotBalls(canvas);
        drawScore(canvas);
    }

    private void drawPath(Canvas canvas) {
        mPaint.setStrokeWidth(10f);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Paint.Style.STROKE);
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
        clearCanvas(canvas);
        canvas.drawColor(Color.WHITE);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(getMeasuredWidth() * 0.08f);
        canvas.drawText(customState == 1 ? "请一笔画完整条路线" : "请选择发射台位置", getMeasuredWidth() >> 1, getMeasuredHeight() * 0.05f, mPaint);
        drawPath(canvas);
        if (customState == 2) {
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(baseShotX, baseShotY, radius, mPaint);
        }
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
        if (!isCustom) {
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
        } else {
            float eventX = event.getX();
            float eventY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (customState == 1) {
                        levelPath.reset();
                        levelPath.moveTo(eventX, eventY);
                    } else {
                        baseShotX = eventX;
                        baseShotY = eventY;
                    }
                    callDraw("custom");
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (customState == 1) {
                        levelPath.lineTo(eventX, eventY);
                    } else {
                        baseShotX = eventX;
                        baseShotY = eventY;
                    }
                    callDraw("custom");
                    break;
                case MotionEvent.ACTION_UP:
                    if (customState == 1) {
                        baseShotX = baseShotY = -9999;
                        customState = 2;
                        measure.setPath(levelPath, false);
                        customLevel.setPath(levelPath, measure.getLength());
                        callDraw("custom");
                    } else {
                        customLevel.setCenter(eventX, eventY);
                        reset(level);
                        startAnim();
                    }
                    break;
            }
        }
        return true;
    }

    class Bobble implements Reusable {
        int type, combo;
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
            type = random.nextInt(4);
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
            dst.set(pos[0] - radius, pos[1] - radius, pos[0] + radius, pos[1] + radius);
            canvas.drawBitmap(balls[type], null, dst, paint);
        }

        void check(int combo) {
            needCheck = true;
            this.combo = combo;
        }
    }

    class ShotBall implements Reusable {
        int type;
        double direction;
        float r, x, y;

        ShotBall() {
            reset();
        }

        @Override
        public void reset() {
            type = random.nextInt(4);
            if (random.nextInt(10) == 1) {
                type = 4;
            }
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
            dst.set(x - radius, y - radius, x + radius, y + radius);
            canvas.drawBitmap(balls[type], null, dst, paint);
        }

        void destroy() {
            r = 999999;
            direction = 99999;
        }
    }

}
