package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/29
 * Description: blablabla
 */
public class RadarView extends BaseSurfaceView {
    private Random random;
    private Path scannerPath, snackPath;
    private List<Spot> spots;
    private float scannerRadius;
    private RadialGradient gradient;
    private float dx, dy;
    private Matrix matrix;
    private boolean isScanning;

    public RadarView(Context context) {
        super(context);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        scannerPath = new Path();
        snackPath = new Path();
        spots = new CopyOnWriteArrayList<>();
        random = new Random();
    }

    @Override
    protected void onReady() {
        if (spots.size() == 0) {
            for (int i = 0; i < 20; i++) {
                spots.add(new Spot());
            }
        }
        scannerRadius = getMeasuredWidth() * 0.15f;
        gradient = new RadialGradient(dx, dy, scannerRadius, new int[]{0x339E9E9E, 0x559E9E9E, 0xAA9370DB, 0x00000000}, new float[]{0f, 0.9f, 0.95f, 1f}, Shader.TileMode.CLAMP);
        matrix = new Matrix();
        UPDATE_RATE = 32;
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        snackPath.reset();
        scannerPath.reset();
        for (Spot spot : spots) {
            spot.move();
            snackPath.addCircle(spot.x, spot.y, spot.radius, Path.Direction.CW);
        }
        if (isScanning) {
            scannerPath.addCircle(dx, dy, scannerRadius, Path.Direction.CW);
        }
        snackPath.op(scannerPath, Path.Op.INTERSECT);
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        mPaint.setColor(Color.BLACK);
        canvas.drawPath(snackPath, mPaint);
        if (isScanning) {
            matrix.setTranslate(dx, dy);
            gradient.setLocalMatrix(matrix);
            mPaint.setShader(gradient);
            canvas.drawPath(scannerPath, mPaint);
            mPaint.setShader(null);
        }
        for (Spot spot : spots) {
            if (spot.captureTime != 0) {
                mPaint.setColor(spot.color);
                mPaint.setAlpha((int) (255 * spot.captureTime / 1500f));
                canvas.drawCircle(spot.x, spot.y, spot.radius, mPaint);
            }
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isScanning = true;
            case MotionEvent.ACTION_MOVE:
                dx = event.getX();
                dy = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                isScanning = false;
                break;
        }
        return true;
    }

    private class Spot {
        private final float bs;
        float x, y, radius, speed;
        int color;
        double md;
        long captureTime;
        boolean isCatch;

        Spot() {
            x = randomX();
            y = randomY();
            color = randomColor();
            radius = randomRadius();
            md = randomDirection();
            speed = bs = randomSpeed();
        }

        void move() {
            if (isCatch) {
                return;
            }
            md += randomDI();
            if (isScanning && dis(this) < scannerRadius) {
                captureTime += UPDATE_RATE;
                if (captureTime > 1500) {
                    isCatch = true;
                }
                speed = getMeasuredWidth() * 0.016f;
                md += randomDI() * 3;
            } else {
                captureTime = 0;
                speed = bs;
            }
            if (x - radius <= 0 || x + radius >= getMeasuredWidth()) {
                if (x - radius <= 0) {
                    x = radius;
                } else {
                    x = getMeasuredWidth() - radius;
                }
                this.md = Math.PI - this.md;
            }
            if (y - radius <= 0 || y + radius >= getMeasuredHeight()) {
                if (y - radius <= 0) {
                    y = radius;
                } else {
                    y = getMeasuredHeight() - radius;
                }
                this.md = -this.md;
            }
            x = x + (float) Math.cos(this.md) * speed;
            y = y + (float) Math.sin(this.md) * speed;
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private double randomDI() {
        return Math.PI / 40 - Math.PI / 20 * random.nextFloat();
    }

    private float dis(Spot spot) {
        return (float) Math.sqrt((spot.x - dx) * (spot.x - dx) + (spot.y - dy) * (spot.y - dy));
    }

    private float randomSpeed() {
        int width = getMeasuredWidth();
        return width * 0.002f + width * 0.008f * random.nextFloat();
    }

    private double randomDirection() {
        return Math.PI * 2 * random.nextFloat();
    }

    private float randomRadius() {
        int width = getMeasuredWidth();
        return width * 0.01f + width * 0.03f * random.nextFloat();
    }

    private float randomY() {
        return getMeasuredHeight() * random.nextFloat();
    }

    private float randomX() {
        return getMeasuredWidth() * random.nextFloat();
    }

}
