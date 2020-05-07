package com.example.customviewstuff.customs.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/2
 */
public class CarSpeedView extends View {
    private static final long UPDATE_MILLIS = 30;
    private float speed;
    private float xIncrement, x = 0;
    private Paint mPaint;
    private boolean running = true;

    public CarSpeedView(Context context) {
        this(context, null);
    }

    public CarSpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarSpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10f);
        }
        setSpeed(0);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Runnable run = () -> {
            while (running) {
                x += xIncrement;
                if (x >= Math.PI) {
                    x = 0;
                }
                postInvalidate();
                try {
                    Thread.sleep(UPDATE_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(run).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    public void setSpeed(float speed) {
        if (speed < 0) {
            speed = 0;
        } else if (speed > 300) {
            speed = 300;
        }
        this.speed = speed;
        long duration;
        if (speed == 0) {
            duration = 12000;
        } else if (speed < 60) {
            duration = 7000;
        } else if (speed < 80) {
            duration = 3000;
        } else {
            duration = 1000;
        }
        xIncrement = (float) (Math.PI / (duration / UPDATE_MILLIS));
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = getMeasuredHeight() * 0.4f;
        float h = getMeasuredHeight() * 0.1f;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, radius, mPaint);
        if (speed == 0) {
            mPaint.setColor(Color.GRAY);
        } else if (speed < 60) {
            mPaint.setColor(Color.GREEN);
        } else if (speed < 80) {
            mPaint.setColor(Color.YELLOW);
        } else {
            mPaint.setColor(Color.RED);
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth((float) (h * Math.sin(x)));
        canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, radius, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setFakeBoldText(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(getMeasuredWidth() * 0.27f);
        canvas.drawText(String.format("%.1f", speed), getMeasuredWidth() >> 1, (getMeasuredHeight() >> 1) + mPaint.getTextSize() / 3, mPaint);
        mPaint.setFakeBoldText(false);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setTextSize(getMeasuredWidth() * 0.15f);
        canvas.drawText("km/h", getMeasuredWidth() >> 1, getMeasuredHeight() * 0.78f, mPaint);
    }

    public void destroy() {
        running = false;
    }

}
