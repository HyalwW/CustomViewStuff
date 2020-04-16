package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class StaringView extends BaseSurfaceView {
    private Random random;
    private List<Star> stars;
    private List<Line> lines;
    private float length, radius;
    private float scaleIncrement;

    public StaringView(Context context) {
        super(context);
    }

    public StaringView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StaringView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        stars = new CopyOnWriteArrayList<>();
        lines = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void onReady() {
        length = (float) (Math.sqrt(getMeasuredWidth() * getMeasuredWidth() + getMeasuredHeight() * getMeasuredHeight()) / 2);
        radius = getMeasuredHeight() * 0.01f;
        scaleIncrement = 0;
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        while (stars.size() < 400) {
            stars.add(new Star());
        }
        while (lines.size() < 30) {
            lines.add(new Line());
        }
        if (touching) {
            if (scaleIncrement < 0.5f) {
                scaleIncrement += 0.0003f;
            }
        } else {
            if (scaleIncrement > 0) {
                scaleIncrement -= 0.0005f;
            } else {
                scaleIncrement = 0;
            }
        }
        for (Star star : stars) {
            star.move();
        }
        for (Line line : lines) {
            line.move();
        }
        Line l = lines.get(0);
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        for (Star star : stars) {
            mPaint.setColor(star.color);
            float[] pos = star.getPos();
            float r = radius * star.scale;
            if (r > 0) {
                mPaint.setShader(new RadialGradient(pos[0], pos[1], r, star.color, Color.TRANSPARENT, Shader.TileMode.CLAMP));
            }
            canvas.drawCircle(pos[0], pos[1], r, mPaint);
        }
        mPaint.setShader(null);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getMeasuredWidth() * 0.003f);
        for (Line line : lines) {
            canvas.drawLine(line.sx, line.sy, line.ex, line.ey, mPaint);
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

    private class Star {
        float scale, maxScale;
        float sx, sy, ex, ey;
        int color;
        float increment;

        Star() {
            reset();
            scale = random.nextFloat() * 0.5f;
        }

        void reset() {
            float l = length * random.nextFloat();
            float a = (float) (Math.PI * 2 * random.nextFloat());
            sx = (float) (l * Math.cos(a) + (getMeasuredWidth() >> 1));
            sy = (float) (l * Math.sin(a) + (getMeasuredHeight() >> 1));
            ex = (float) (length * Math.cos(a) + (getMeasuredWidth() >> 1));
            ey = (float) (length * Math.sin(a) + (getMeasuredHeight() >> 1));
            scale = 0f;
            maxScale = 1f + (length - l) / length * 2f;
            increment = 0;
            color = randomColor();
        }

        float[] getPos() {
            float[] pos = new float[2];
            pos[0] = scale / maxScale * (ex - sx) + sx;
            pos[1] = scale / maxScale * (ey - sy) + sy;
            return pos;
        }

        public void move() {
            if (scale > maxScale) {
                reset();
                return;
            }
            if (touching) {
                increment += 0.0015f;
            } else {
                if (increment > 0) {
                    increment -= 0.0005f;
                } else {
                    increment = 0;
                }
            }
            scale += scaleIncrement + increment;
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private class Line {
        float ml;
        float angle;
        float sx, sy, ex, ey;
        float increment;

        Line() {
            reset();
        }

        void reset() {
            ml = length * 0.8f * random.nextFloat();
            angle = (float) (Math.PI * 2 * random.nextFloat());
            ex = sx = (float) (ml * Math.cos(angle) + (getMeasuredWidth() >> 1));
            ey = sy = (float) (ml * Math.sin(angle) + (getMeasuredHeight() >> 1));
            increment = 0;
        }

        void move() {
            if (ml > length) {
                reset();
                return;
            }
            if (touching) {
                increment += length * 0.04f * scaleIncrement;
            } else {
                if (increment > 0) {
                    increment -= 0.02f;
                } else {
                    increment = 0;
                }
            }
            ml += increment;
            sx = (float) (ml * Math.cos(angle) + (getMeasuredWidth() >> 1));
            sy = (float) (ml * Math.sin(angle) + (getMeasuredHeight() >> 1));
            float el = ml + increment * 10;
            ex = (float) (el * Math.cos(angle) + (getMeasuredWidth() >> 1));
            ey = (float) (el * Math.sin(angle) + (getMeasuredHeight() >> 1));
        }
    }

    private boolean touching;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touching = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touching = false;
                break;
        }
        return true;
    }
}
