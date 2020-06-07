package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.customviewstuff.helpers.Stack;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/4
 * Description: blablabla
 */
public class StackView extends BaseSurfaceView {
    private Stack<Block>[] stacks;
    private RectF[] rects;
    private float[] bxs;
    private Block selected;
    private int owner;
    private float baseLength, blockHeight;
    private int level, step;
    private static final long moveTime = 300;
    private RectF drawRect;
    private Random random;
    private float topY;
    private LinearGradient red, green;
    private Runnable nextLevel = () -> {
        canTouch = true;
        resetLevel(++level);
    };
    private List<Block> pool;

    public StackView(Context context) {
        super(context);
    }

    public StackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        stacks = new Stack[]{new Stack<Block>(), new Stack<Block>(), new Stack<Block>()};
        rects = new RectF[3];
        bxs = new float[3];
        random = new Random();
        pool = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void onReady() {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        float containerH = h * 0.4f;
        rects[0] = new RectF(0, containerH, w / 3f, h);
        bxs[0] = (rects[0].left + rects[0].right) / 2;
        rects[1] = new RectF(w / 3f, containerH, w / 3f * 2, h);
        bxs[1] = (rects[1].left + rects[1].right) / 2;
        rects[2] = new RectF(w / 3f * 2, containerH, w, h);
        bxs[2] = (rects[2].left + rects[2].right) / 2;
        red = new LinearGradient(0, h, 0, rects[0].top, new int[]{0xAAFF0000, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        green = new LinearGradient(0, h, 0, rects[0].top, new int[]{0xAA00FF00, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        topY = h * 0.35f;
        drawRect = new RectF();
        baseLength = w * 0.3f;
        blockHeight = w * 0.03f;
        if (level == 0) {
            resetLevel(1);
        }
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Stack<Block> stack : stacks) {
            for (Block block : stack) {
                block.move();
            }
        }
        if (selected != null) {
            selected.move();
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawLevel(canvas);
        drawContainer(canvas);
        for (Stack<Block> stack : stacks) {
            drawStack(canvas, stack);
        }
        if (selected != null) {
            mPaint.setColor(selected.color);
            float hw = selected.length / 2;
            float hh = blockHeight / 2;
            drawRect.set(selected.x - hw, selected.y - hh, selected.x + hw, selected.y + hh);
            canvas.drawRoundRect(drawRect, blockHeight, blockHeight, mPaint);
            if (selected.x == selected.tx) {
                mPaint.setColor(0xCCCCCCCC);
                canvas.drawLine(selected.x - hw, selected.y, selected.x - hw, getMeasuredHeight(), mPaint);
                canvas.drawLine(selected.x + hw, selected.y, selected.x + hw, getMeasuredHeight(), mPaint);
            }
        }
    }

    private void drawLevel(Canvas canvas) {
        mPaint.setTextSize(getMeasuredWidth() * 0.16f);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("第" + level + "关", getMeasuredWidth() >> 1, getMeasuredHeight() * 0.14f, mPaint);

        mPaint.setTextSize(getMeasuredWidth() * 0.05f);
        canvas.drawText("共移动了" + step + "步", getMeasuredWidth() >> 1, getMeasuredHeight() * 0.2f, mPaint);
    }

    private void drawStack(Canvas canvas, Stack<Block> stack) {
        for (Block block : stack) {
            mPaint.setColor(block.color);
            float hw = block.length / 2;
            float hh = blockHeight / 2;
            drawRect.set(block.x - hw, block.y - hh, block.x + hw, block.y + hh);
            canvas.drawRoundRect(drawRect, blockHeight, blockHeight, mPaint);
        }
    }

    private void drawContainer(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(rects[1].left, rects[1].bottom, rects[1].left, rects[1].top, mPaint);
        canvas.drawLine(rects[1].right, rects[1].bottom, rects[1].right, rects[1].top, mPaint);
        if (selected != null && inContainer != -1) {
            mPaint.setShader(canDrop() ? green : red);
            canvas.drawRect(rects[inContainer], mPaint);
            mPaint.setShader(null);
        }
    }

    private boolean canDrop() {
        return stacks[inContainer].size() == 0 || stacks[inContainer].top().length > selected.length;
    }

    @Override
    protected void draw(Canvas canvas, Object data) {

    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        removeCallbacks(nextLevel);
    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    public void resetLevel(int level) {
        if (level > 10) {
            level = 10;
        }
        this.level = level;
        for (Stack<Block> stack : stacks) {
            stack.clear();
        }
        float len = baseLength * 0.1f, y = getMeasuredHeight() - blockHeight / 2;
        for (int i = 0; i < level; i++) {
            Block block;
            if (pool.size() > 0) {
                block = pool.remove(0);
                block.reset(bxs[0], y, baseLength - len * i);
            } else {
                block = new Block(bxs[0], y, baseLength - len * i);
            }
            y -= blockHeight;
            stacks[0].pull(block);
        }
        pool.addAll(stacks[0]);
        Collections.shuffle(pool);
    }

    private class Block {
        float x, tx, y, ty;
        float incX, incY;
        float length;
        int color;

        Block(float x, float y, float length) {
            set(x, y);
            this.length = length;
            color = randomColor();
        }

        void reset(float x, float y, float length) {
            set(x, y);
            this.length = length;
            color = randomColor();
        }

        void move() {
            if (tx != x || ty != y) {
                x += incX;
                if ((incX > 0 && x > tx) || (incX < 0 && x < tx)) {
                    x = tx;
                }
                y += incY;
                if ((incY > 0 && y > ty) || (incY < 0 && y < ty)) {
                    y = ty;
                }
                if (x == tx && y == ty && stacks[2].size() == level) {
                    canTouch = false;
                    postDelayed(nextLevel, 500);
                }
            }
        }

        void set(float newX, float newY) {
            if (tx != newX || ty != newY) {
                this.tx = newX;
                this.ty = newY;
                incX = (tx - x) * UPDATE_RATE / moveTime;
                incY = (ty - y) * UPDATE_RATE / moveTime;
            }
        }

    }

    private int randomColor() {
        return Color.rgb(random.nextInt(225), random.nextInt(225), random.nextInt(225));
    }

    private volatile int inContainer = -1;
    private boolean canTouch = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canTouch) {
            return false;
        }
        float eventX = event.getX();
        float eventY = event.getY();
        int whichContainer = inWhichContainer(eventX, eventY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (whichContainer != -1 && !stacks[whichContainer].isEmpty()) {
                    owner = whichContainer;
                    inContainer = whichContainer;
                    selected = stacks[whichContainer].pop();
                    selected.set(selected.x, topY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (selected != null && whichContainer != -1) {
                    selected.set(bxs[whichContainer], selected.ty);
                    inContainer = whichContainer;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (selected != null && inContainer != -1) {
                    whichContainer = inContainer;
                    inContainer = -1;
                    Stack<Block> newStack = stacks[whichContainer];
                    Stack<Block> ownerStack = stacks[owner];
                    if (newStack.isEmpty() || newStack.top().length > selected.length) {
                        setXY(selected, newStack, bxs[whichContainer]);
                        newStack.pull(selected);
                        selected = null;
                        if (owner != whichContainer) {
                            step++;
                        }
                    } else {
                        setXY(selected, ownerStack, bxs[owner]);
                        ownerStack.pull(selected);
                        selected = null;
                    }
                }
                break;
        }
        return true;
    }

    private void setXY(Block block, Stack<Block> stack, float bx) {
        int size = stack.size();
        float newY = getMeasuredHeight() - blockHeight / 2 - size * blockHeight;
        block.set(bx, newY);
    }

    private int inWhichContainer(float x, float y) {
        for (int i = 0; i < rects.length; i++) {
            if (rects[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }
}
