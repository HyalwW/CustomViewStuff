package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/19
 */
public class MultiTouchView extends BaseSurfaceView {
    private List<Compass> compasses;
    private Random random;
    private float circleRadius;
    private float strokeRadius, strokeWidth;
    private DashPathEffect dashPathEffect;

    private long keepTime, cd, cu, max;
    private static final long start = 1000, min = 50;
    private long nowGap, loopTime, maintainTime;
    private boolean isLooping;
    private int selectedIndex, selectedColor, toColor, state;

    private boolean isChoosing;
    private float maxRadius, nowRadius;
    private float cx, cy;

    public MultiTouchView(Context context) {
        super(context);
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        compasses = new CopyOnWriteArrayList<>();
        random = new Random();
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setFakeBoldText(true);
        selectedColor = Color.WHITE;
    }

    @Override
    protected void onReady() {
        circleRadius = getMeasuredWidth() * 0.15f;
        strokeRadius = circleRadius + getMeasuredWidth() * 0.03f;
        strokeWidth = getMeasuredWidth() * 0.03f;
        dashPathEffect = new DashPathEffect(new float[]{2, 10}, 0);
        mPaint.setTextSize(strokeRadius);
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Compass compass : compasses) {
            compass.rotate();
        }
        if (isLooping) {
            if (compasses.size() == 0) {
                isLooping = false;
                return;
            }
            loopTime += UPDATE_RATE;
            if (loopTime > nowGap) {
                selectedIndex++;
                if (selectedIndex >= compasses.size()) {
                    selectedIndex = 0;
                }
                loopTime = 0;
                if (state == 4) {
                    isLooping = false;
                    if (selectedIndex < compasses.size()) {
                        Compass compass = compasses.get(selectedIndex);
                        toColor = compass.color;
                        isChoosing = true;
                        nowRadius = 0;
                        maxRadius = maxRadius(compass.x, compass.y);
                    }
                }
            }
            switch (state) {
                case 1:
                    if (nowGap > min) {
                        nowGap -= cd;
                    } else {
                        state = 2;
                    }
                    break;
                case 2:
                    if (maintainTime < keepTime) {
                        maintainTime += UPDATE_RATE;
                    } else {
                        state = 3;
                    }
                    break;
                case 3:
                    if (nowGap < max) {
                        nowGap += cu;
                    } else {
                        state = 4;
                    }
                    break;
            }
        }
        if (isChoosing) {
            if (nowRadius < maxRadius) {
                nowRadius += getMeasuredWidth() * 0.05f;
            } else {
                isChoosing = false;
                selectedColor = toColor;
            }
        }
    }

    private float maxRadius(float x, float y) {
        cx = x;
        cy = y;
        return (float) Math.max(dis(x, y, 0, 0), Math.max(dis(x, y, 0, getMeasuredHeight()), Math.max(dis(x, y, getMeasuredWidth(), 0), dis(x, y, getMeasuredWidth(), getMeasuredHeight()))));
    }

    private double dis(float sx, float sy, float ex, float ey) {
        return Math.sqrt((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy));
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(selectedColor);
        mPaint.setColor(toColor);
        mPaint.setStyle(Paint.Style.FILL);
        if (isChoosing) {
            canvas.drawCircle(cx, cy, nowRadius, mPaint);
        }
        for (Compass compass : compasses) {
            canvas.save();
            canvas.rotate(compass.angle, compass.x, compass.y);
            mPaint.setColor(compass.color);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setPathEffect(dashPathEffect);
            if (isLooping && compasses.indexOf(compass) == selectedIndex) {
                mPaint.setColor(Color.RED);
                mPaint.setStrokeWidth(strokeWidth * 2);
            }
            canvas.drawCircle(compass.x, compass.y, strokeRadius, mPaint);
            canvas.restore();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setPathEffect(null);
            canvas.drawCircle(compass.x, compass.y, circleRadius, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawText(String.valueOf(compass.index + 1), compass.x, compass.y + mPaint.getTextSize() / 3, mPaint);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                float eventX = event.getX(index);
                float eventY = event.getY(index);
                Compass compass = new Compass(eventX, eventY, id);
                compasses.add(compass);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int pointerId = event.getPointerId(i);
                    Compass cp = find(pointerId);
                    if (cp != null) {
                        cp.move(event.getX(i), event.getY(i));
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                compasses.remove(find(id));
                break;
        }
        return true;
    }

    Compass find(int index) {
        for (Compass compass : compasses) {
            if (compass.index == index) {
                return compass;
            }
        }
        return null;
    }

    public void start() {
        if (!isLooping && compasses.size() > 0) {
            keepTime = randomKeepTime();
            maintainTime = 0;
            loopTime = 0;
            state = 1;
            cd = randomCd();
            cu = randomCd();
            max = randomMax();
            nowGap = start;
            isLooping = true;
        }
    }

    private long randomMax() {
        return (long) (1400 + random.nextFloat() * 1000);
    }

    private int randomCd() {
        return (int) (2 + random.nextFloat() * 3);
    }

    private long randomKeepTime() {
        return (long) (3000 + random.nextFloat() * 2000);
    }

    class Compass {
        float x, y;
        int color, index;
        int angle;

        Compass(float x, float y, int index) {
            this.x = x;
            this.y = y;
            this.index = index;
            color = randomColor();
        }

        void move(float mx, float my) {
            x = mx;
            y = my;
        }

        void rotate() {
            angle += 10;
            if (angle == 360) {
                angle = 0;
            }
        }
    }

    private int randomColor() {
        int color;
        while (!checkColor(color = getColor())) {
        }
        return color;
    }

    private boolean checkColor(int color) {
        if (color == selectedColor || color == Color.RED)
            return false;
        for (Compass compass : compasses) {
            if (compass.color == color)
                return false;
        }
        return true;
    }

    private int getColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
}
