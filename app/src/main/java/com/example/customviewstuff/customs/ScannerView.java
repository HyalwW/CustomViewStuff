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

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/29
 * Description: blablabla
 */
public class ScannerView extends BaseSurfaceView {
    private Random random;
    private Path scannerPath, snackPath;
    private List<Spot> spots;
    private float scannerRadius;
    private RadialGradient gradient;
    private float dx, dy;
    private Matrix matrix;

    public ScannerView(Context context) {
        super(context);
    }

    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        scannerPath = new Path();
        snackPath = new Path();
        spots = new ArrayList<>();
        random = new Random();
    }

    @Override
    protected void onReady() {
        for (int i = 0; i < 20; i++) {
            spots.add(new Spot());
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
        for (Spot spot : spots) {
            spot.move();
            snackPath.addCircle(spot.x, spot.y, spot.radius, Path.Direction.CW);
        }
        snackPath.op(scannerPath, Path.Op.INTERSECT);
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        mPaint.setColor(Color.BLACK);
        canvas.drawPath(snackPath, mPaint);
        if (!scannerPath.isEmpty()) {
            matrix.setTranslate(dx, dy);
            gradient.setLocalMatrix(matrix);
            mPaint.setShader(gradient);
            canvas.drawPath(scannerPath, mPaint);
            mPaint.setShader(null);
        }
//        for (Spot spot : spots) {
//            mPaint.setColor(spot.color);
//            canvas.drawCircle(spot.x, spot.y, spot.radius, mPaint);
//        }
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
        scannerPath.reset();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                dx = event.getX();
                dy = event.getY();
                scannerPath.addCircle(dx, dy, scannerRadius, Path.Direction.CW);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private class Spot {
        float x, y, radius, speed;
        int color;
        double md;
        boolean isCatch;

        Spot() {
            x = randomX();
            y = randomY();
            color = randomColor();
            radius = randomRadius();
            md = randomDirection();
            speed = randomSpeed();
        }

        void move() {
            md += randomDI();
            if (x - radius <= 0 || x + radius >= getMeasuredWidth()) {
                if (x - radius <= 0) {
                    x = radius;
                } else {
                    x = getMeasuredWidth() - radius;
                }
                md = Math.PI - md;
            }
            if (y - radius <= 0 || y + radius >= getMeasuredHeight()) {
                if (y - radius <= 0) {
                    y = radius;
                } else {
                    y = getMeasuredHeight() - radius;
                }
                md = -md;
            }
            x = x + (float) Math.cos(md) * speed;
            y = y + (float) Math.sin(md) * speed;
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private double randomDI() {
        return Math.PI / 40 - Math.PI / 20 * random.nextFloat();
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
