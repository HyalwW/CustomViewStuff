package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.customviewstuff.R;

/**
 * 创建者：wyz
 * 创建时间：2020-05-19
 * 功能描述：
 * 更新者：
 * 更新时间：
 * 更新描述：
 */
public class RectView extends View {
    private int width, height;
    private TextPaint paint;
    private float radius = 40;
    private float x, y;
    private Rect rectss[][];
    private int step = 5;
    private float stepPi;
    private boolean ishow;

    public RectView(Context context) {
        this(context, null);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        paint = new TextPaint();
        paint.setColor(getResources().getColor(R.color.colorAccent));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(40);
        paint.setUnderlineText(true);
        stepPi = (float) (Math.PI / step);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initList();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Rect[] r : rectss) {
            for (Rect rect : r) {
                paint.setColor(rect.getClolor());
                canvas.drawCircle(rect.getPointX() + radius / 2, rect.getPointY() + radius / 2, rect.getRadius() / 2, paint);
                if (ishow) {
                    rect.updata();
                } else {
                    canvas.drawCircle(rect.getPointX() + radius / 2, rect.getPointY() + radius / 2, radius / 2, paint);
                }
            }
            if (ishow) {
                invalidate();
            }
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


        }
    }

    //随机产生颜色
    public int color() {
        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }

    public void initList() {
        int numX = (int) (width / radius);
        int numY = (int) (height / radius);
        if (width % radius > 0) {
            numX = numX + 1;
        }
        if (height % radius > 0) {
            numY = numY + 1;
        }
        rectss = new Rect[numX][numY];
        for (int i = 0; i < numX; i++) {
            for (int j = 0; j < numY; j++) {
                int color = color();
                Rect rect1 = new Rect(i * radius, j * radius, radius, color);
                rectss[i][j] = rect1;
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
            initTouch(x, y);
            ishow = true;
            invalidate();
//                Handler handler = new Handler();
//                handler.postDelayed(runner, 5000);
        }
        return super.onTouchEvent(event);
    }

    private void initTouch(float x, float y) {
        for (Rect[] r : rectss) {
            for (Rect rect : r) {
                float dimmision = (float) (Math.sqrt(Math.pow(rect.getPointX() + radius / 2 - x, 2) + Math.pow(rect.getPointY() + radius / 2 - y, 2)));
                float num = (dimmision / radius) % step;
                rect.setStartDelt(num * stepPi);
            }
        }
    }

    private Runnable runner = new Runnable() {
        @Override
        public void run() {
            ishow = false;
        }
    };
}
