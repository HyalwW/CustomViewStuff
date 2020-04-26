package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/17
 */
public class StayAwayView extends BaseSurfaceView {
    private Random random;
    private List<Ball> balls;
    private boolean pressed;
    private float triggleLen;
    private float px, py;
    private float increment;
    private int type = 1;

    public StayAwayView(Context context) {
        super(context);
    }

    public StayAwayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StayAwayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        balls = new CopyOnWriteArrayList<>();
        random = new Random();
    }

    @Override
    protected void onReady() {
        if (balls.size() == 0) {
            for (int i = 0; i < 600; i++) {
                balls.add(new Ball());
            }
        }
        triggleLen = getMeasuredWidth() * 0.25f;
        increment = getMeasuredWidth() * 0.01f;
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Ball ball : balls) {
            if (type == 0) {
                ball.runAway();
            } else if (type == 1) {
                ball.scale();
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        for (Ball ball : balls) {
            mPaint.setColor(ball.color);
            canvas.drawCircle(ball.x, ball.y, ball.radius, mPaint);
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

    private class Ball {
        float bx, by, br;
        float length, maxLen;
        double angle;
        float x, y, radius;
        int color;

        Ball() {
            reset();
        }

        private void reset() {
            color = randomColor();
            x = bx = randomX();
            y = by = randomY();
            radius = br = randomRadius();
            for (Ball ball : balls) {
                if (distance(ball.x, ball.y) <= radius + ball.radius) {
                    reset();
                    break;
                }
            }
        }

        float distance(float ex, float ey) {
            return (float) Math.sqrt((bx - ex) * (bx - ex) + (by - ey) * (by - ey));
        }

        void runAway() {
            if (pressed && distance(px, py) <= triggleLen) {
                maxLen = triggleLen * 2 - distance(px, py) * 2;
                float xx = px - bx;
                float yy = py - by;
                angle = Math.atan2(yy, xx) + Math.PI;
                if (length < maxLen) {
                    length += increment;
                } else if (length > maxLen) {
                    length -= increment * 1.1f;
                }
            } else {
                if (length > 0) {
                    length -= increment * 0.5f * random.nextFloat();
                } else {
                    length = 0;
                }
            }
            x = (float) (bx + Math.cos(angle) * length);
            y = (float) (by + Math.sin(angle) * length);
        }

        void scale() {
            if (pressed && distance(px, py) <= triggleLen) {
                maxLen = br * 3f * (1 - distance(px, py) / triggleLen) + br;
                if (radius < maxLen) {
                    radius += increment * 0.5f;
                } else if (radius > maxLen) {
                    radius = maxLen;
                }
            } else {
                if (radius > br) {
                    radius -= increment * 0.1f;
                } else {
                    radius = br;
                }
            }
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private float randomRadius() {
        int min = Math.min(getMeasuredHeight(), getMeasuredWidth());
        return min * 0.01f + random.nextFloat() * min * 0.01f;
    }

    private float randomX() {
        return getMeasuredWidth() * random.nextFloat();
    }

    private float randomY() {
        return getMeasuredHeight() * random.nextFloat();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        px = event.getX();
        py = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressed = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pressed = false;
                break;
        }
        return true;
    }
}
