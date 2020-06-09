package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/2/24
 */
public class RippleView extends BaseSurfaceView {
    private Random random;
    private PathMeasure pathMeasure;
    private List<Ripple> ripples;
    private int[] rgb = new int[]{255, 255, 255};
    private static final int GAP = 6;
    private int nextColor, colorType, changeGap = GAP;

    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        nextColor = randomRGB();
        colorType = random.nextInt(3);
        pathMeasure = new PathMeasure();
        ripples = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void onReady() {
        if (ripples.size() == 0) {
            for (int i = 0; i < 20; i++) {
                ripples.add(new Ripple());
            }
        }
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        changeColor();
        for (Ripple ripple : ripples) {
            ripple.move();
        }
    }

    private void changeColor() {
        if (changeGap > 0) {
            changeGap--;
            return;
        }
        changeGap = GAP;
        if (rgb[colorType] > nextColor) {
            rgb[colorType]--;
        } else if (rgb[colorType] < nextColor) {
            rgb[colorType]++;
        } else {
            colorType = random.nextInt(3);
            nextColor = randomRGB();
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        for (Ripple ripple : ripples) {
            mPaint.setColor(ripple.color);
            canvas.drawCircle(ripple.pos[0], ripple.pos[1], ripple.radius, mPaint);
//            mPaint.setStyle(Paint.Style.STROKE);
//            mPaint.setStrokeWidth(5);
//            canvas.drawPath(ripple.path, mPaint);
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

    private class Ripple {
        int color;
        long duration, startTime;
        float radius;
        float[] xs, ys;
        Path path;
        private float[] pos;

        Ripple() {
            path = new Path();
            pos = new float[2];
            reset();
            startTime = System.currentTimeMillis() - (long) (random.nextFloat() * duration);
        }

        private void reset() {
            duration = randomDuration();
            startTime = System.currentTimeMillis();
            color = randomColor();
            radius = randomRadius();
            setPoints();
            path.reset();
            path.moveTo(xs[0], ys[0]);
            path.cubicTo(xs[1], ys[1], xs[2], ys[2], xs[3], ys[3]);
        }

        private void setPoints() {
            xs = new float[4];
            ys = new float[4];
            float width = (float) getMeasuredWidth() / 2;
            xs[0] = width / 2 + random.nextFloat() * width;
            for (int i = 1; i < 4; i++) {
                xs[i] = xs[i - 1] - width + random.nextFloat() * width * 2;
            }
            float height = (float) getMeasuredHeight() / 3;
            ys[0] = getMeasuredHeight() + radius;
            ys[1] = height * 2 + height / 2 - random.nextFloat() * height;
            ys[2] = height + height / 2 - random.nextFloat() * height;
            ys[3] = -radius;
        }

        void move() {
            long waste = System.currentTimeMillis() - startTime;
            if (waste > duration) {
                reset();
                waste = 0;
            }
            pathMeasure.setPath(path, false);
            float distance = (float) waste / duration * pathMeasure.getLength();
            pathMeasure.getPosTan(distance, pos, null);
        }
    }

    private float randomRadius() {
        return getMeasuredWidth() * 0.05f + random.nextFloat() * getMeasuredWidth() * 0.06f;
    }

    private int randomDuration() {
        return 4000 + random.nextInt(6000);
    }

    private int randomColor() {
        return Color.argb(150 + random.nextInt(105), randomRGB(), randomRGB(), randomRGB());
    }

    private int randomRGB() {
        return random.nextInt(255);
    }
}
