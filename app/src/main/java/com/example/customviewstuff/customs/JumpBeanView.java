package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/2/19
 */
public class JumpBeanView extends BaseSurfaceView {
    private Random random;
    private final String LEFT = "left", TOP = "top", RIGHT = "right", BOTTOM = "bottom";
    private List<Bean> list;
    private Bitmap bitmap;
    private Rect dst;

    public JumpBeanView(Context context) {
        super(context);
    }

    public JumpBeanView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JumpBeanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        list = new ArrayList<>();
        mPaint.setStyle(Paint.Style.FILL);
        dst = new Rect();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.goods);
    }

    @Override
    protected void onReady() {
        for (int i = 0; i < 50; i++) {
            Bean bean = new Bean(randomColor(), randomX(), randomY());
            list.add(bean);
            bean.setStartTime(System.currentTimeMillis());
        }
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Bean bean : list) {
            bean.move(System.currentTimeMillis(), 360);
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        for (Bean bean : list) {
            mPaint.setColor(bean.color);
            dst.set(((int) (bean.nx - bitmap.getWidth() / 2)), ((int) (bean.ny - bitmap.getHeight() / 2)), ((int) (bean.nx + bitmap.getWidth() / 2)), (int) (bean.ny + bitmap.getHeight() / 2));
            canvas.drawBitmap(bitmap, null, dst, mPaint);
//            canvas.drawCircle(bean.nx, bean.ny, bean.radius, mPaint);
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

    private float randomRadius() {
        return random.nextFloat() * 6 + 10;
    }

    private float randomXSpeed() {
        float v = 200 + random.nextFloat() * 200;
        return random.nextInt(2) == 0 ? v : 0 - v;
    }

    private float randomYSpeed() {
        return 300 + random.nextFloat() * 600;
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private float randomX() {
        return random.nextFloat() * getMeasuredWidth();
    }

    private float randomY() {
        return random.nextFloat() * getMeasuredHeight();
    }

    private class Bean {
        int color;
        float xSpeed, ySpeed;
        float x, y;
        float radius;
        long startTime;
        float nx, ny, nSpeed;

        Bean(int color, float x, float y) {
            this.color = color;
            this.x = x;
            this.y = y;
            radius = randomRadius();
            xSpeed = randomXSpeed();
            ySpeed = randomYSpeed();
            startTime = System.currentTimeMillis();
        }

        void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        /**
         * @param timeStamp 时间戳
         * @param g         重力加速度
         */
        void move(long timeStamp, float g) {
            float time = (float) (timeStamp - startTime) / 1000;
            nx = x + xSpeed * time;
            if (xSpeed > 0) {
                xSpeed -= ySpeed == 0 ? 10 : 0.05;
            } else if (xSpeed < 0) {
                xSpeed += ySpeed == 0 ? 10 : 0.05;
            }
            if (ySpeed == 0) {
                ny = getMeasuredHeight() - radius;
            } else {
                ny = getMeasuredHeight() - (y + ySpeed * time - g * time * time / 2);
                nSpeed = ySpeed - g * time;
                if (ny + radius >= getMeasuredHeight()) {
                    hitWall(BOTTOM);
                }
            }
            if (nx - radius <= 0) {
                hitWall(LEFT);
            } else if (nx + radius >= getMeasuredWidth()) {
                hitWall(RIGHT);
            }
        }

        void hitWall(String direction) {
            switch (direction) {
                case LEFT:
                    x = -x + radius;
                    xSpeed = -xSpeed;
                    break;
                case RIGHT:
                    x = getMeasuredWidth() * 2 - x - radius;
                    xSpeed = -xSpeed;
                    break;
                case BOTTOM:
                    ySpeed = Math.abs((float) (nSpeed * 0.85));
                    if (ySpeed < 2) {
                        ySpeed = 0;
                    }
                    y = radius;
                    x = nx;
                    startTime = System.currentTimeMillis();
                    break;
            }
        }

        void reset() {
            x = nx != 0 ? nx : randomX();
            y = ny != 0 ? getMeasuredHeight() - ny : randomY();
            xSpeed = randomXSpeed();
            ySpeed = randomYSpeed();
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
//                safeModifyData(() -> {
//                    list.add(new Bean(randomColor(), event.getX(), event.getY()));
//                });
                break;
            case MotionEvent.ACTION_UP:
                safeModifyData(() -> {
                    for (Bean bean : list) {
                        bean.reset();
                    }
                });
                break;
        }
        return true;
    }
}
