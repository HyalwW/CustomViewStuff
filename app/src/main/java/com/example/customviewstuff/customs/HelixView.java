package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/15
 */
public class HelixView extends BaseSurfaceView {
    private Random random;
    private List<Helix> helixes;

    public HelixView(Context context) {
        super(context);
    }

    public HelixView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HelixView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        helixes = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void onReady() {
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Helix helix : helixes) {
            helix.move();
        }
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

    private class Helix {
        Helix() {
            reset(0);
        }

        void reset(float baseX) {
        }

        void move() {
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
}
