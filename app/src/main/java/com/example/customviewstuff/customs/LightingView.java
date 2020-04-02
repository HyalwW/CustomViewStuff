package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/2
 */
public class LightingView extends BaseSurfaceView {
    private Random random;
    private List<Lighting> lightings;
    private static final float pi = (float) Math.PI;
    private int index;

    public LightingView(Context context) {
        super(context);
    }

    public LightingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        lightings = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void onReady() {
        start();
    }

    private void start() {
        lightings.clear();
        Lighting e = new Lighting();
        e.set(0, 0, pi / 6, 1, 1, getMeasuredHeight() * 0.2f);
        lightings.add(e);
        index = 0;
        doInThread(() -> {
            while (index < lightings.size()) {

                sleep(UPDATE_RATE);
            }
        });
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

    private class Lighting {
        private float angle;
        float sx, sy, ex, ey;
        int level, serial;
        float length, width;

        public Lighting() {
        }

        void set(float sx, float sy, float angle, int level, int serial, float width) {
            this.sx = sx;
            this.sy = sy;
            this.angle = angle;
            this.level = level;
            this.serial = serial;
            this.length = randomLength();
            this.width = width;
        }
    }

    private float randomLength() {
        return getMeasuredHeight() * 0.2f + random.nextFloat() * 0.2f;
    }
}
