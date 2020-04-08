package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.example.customviewstuff.Pool;

import java.util.Random;

public class StaringView extends BaseSurfaceView {
    private Random random;
    private Pool<Star> pool;

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
        pool = new Pool<>(new Pool.Creator<Star>() {
            @Override
            public Star instance() {
                return new Star();
            }

            @Override
            public void reset(Star star) {

            }

            @Override
            public boolean isLeisure(Star star) {
                return star.scale > 2f;
            }
        });
        random = new Random();
    }

    @Override
    protected void onReady() {

    }

    @Override
    protected void onDataUpdate() {

    }

    @Override
    protected void onRefresh(Canvas canvas) {

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
        float scale;
        float sx, sy, ex, ey;
        int color;

        public Star() {
            this.sx = randomX();
            this.sy = randomY();
            scale = 0f;
            color = randomColor();
        }
    }

    private float randomX() {
        return getMeasuredWidth() * random.nextFloat();
    }

    private float randomY() {
        return getMeasuredHeight() * random.nextFloat();
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
}
