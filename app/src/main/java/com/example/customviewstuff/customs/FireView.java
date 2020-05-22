package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/22
 * Description: blablabla
 */
public class FireView extends BaseSurfaceView {
    private Random random;
    private List<Flame> flames;
    private Path drawPath;
    private static final double PI = Math.PI;

    public FireView(Context context) {
        super(context);
    }

    public FireView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FireView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        random = new Random();
        flames = new ArrayList<>();
        drawPath = new Path();
    }

    @Override
    protected void onReady() {
        int w = getMeasuredWidth(), h = getMeasuredHeight();
        flames.add(new Flame(p(w * 0.2f, h), p(w * 0.8f, h), p(w >> 1, h >> 1),
                p(w * 0.3f, h * 0.75f), p(w * 0.4f, h * 0.6f),
                p(w * 0.7f, h * 0.75f), p(w * 0.6f, h * 0.6f)));
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Flame flame : flames) {
            flame.move();
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawPath.reset();
        mPaint.setColor(Color.RED);
        for (Flame flame : flames) {
            drawPath.moveTo(flame.bl.x, flame.bl.y);
            float l1x = (float) (flame.bml1c.x + flame.lml1 * cos(flame.ctml1 / (double) flame.dml1 * 2 * PI));
            float l1y = (float) (flame.bml1c.y + flame.lml1 * sin(flame.ctml1 / (double) flame.dml1 * 2 * PI));
            float l2x = (float) (flame.bml2c.x + flame.lml2 * cos(flame.ctml2 / (double) flame.dml2 * 2 * PI));
            float l2y = (float) (flame.bml2c.y + flame.lml2 * sin(flame.ctml2 / (double) flame.dml2 * 2 * PI));
            float tx = (float) (flame.btc.x + flame.lt * cos(flame.ct / (double) flame.dt * 2 * PI));
            float ty = (float) (flame.btc.y + flame.lt * sin(flame.ct / (double) flame.dt * 2 * PI));
            drawPath.cubicTo(l1x, l1y, l2x, l2y, tx, ty);
            float r1x = (float) (flame.bmr1c.x + flame.lmr1 * cos(flame.ctmr1 / (double) flame.dmr1 * 2 * PI));
            float r1y = (float) (flame.bmr1c.y + flame.lmr1 * sin(flame.ctmr1 / (double) flame.dmr1 * 2 * PI));
            float r2x = (float) (flame.bmr2c.x + flame.lmr2 * cos(flame.ctmr2 / (double) flame.dmr2 * 2 * PI));
            float r2y = (float) (flame.bmr2c.y + flame.lmr2 * sin(flame.ctmr2 / (double) flame.dmr2 * 2 * PI));
            drawPath.cubicTo(r2x, r2y, r1x, r1y, flame.br.x, flame.br.y);
            drawPath.close();
        }
        canvas.drawPath(drawPath, mPaint);
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

    class Flame {
        PointF bl, br, btc, bml1c, bml2c, bmr1c, bmr2c;
        float lt, lml1, lml2, lmr1, lmr2;
        long dt, dml1, dml2, dmr1, dmr2;
        long ct, ctml1, ctml2, ctmr1, ctmr2;

        Flame(PointF bl, PointF br, PointF btc, PointF bml1c, PointF bml2c, PointF bmr1c, PointF bmr2c) {
            this.bl = bl;
            this.br = br;
            this.btc = btc;
            this.bml1c = bml1c;
            this.bml2c = bml2c;
            this.bmr1c = bmr1c;
            this.bmr2c = bmr2c;
            dt = randomDuration();
            dml1 = randomDuration();
            dml2 = randomDuration();
            dmr1 = randomDuration();
            dmr2 = randomDuration();
            lt = randomRadius();
            lml1 = randomRadius();
            lml2 = randomRadius();
            lmr1 = randomRadius();
            lmr2 = randomRadius();
        }

        void move() {
            ct += UPDATE_RATE;
            if (ct >= dt) {
                ct = 0;
//                dt = randomDuration();
            }
            ctml1 += UPDATE_RATE;
            if (ctml1 >= dml1) {
                ctml1 = 0;
//                dml2 = randomDuration();
            }
            ctml2 += UPDATE_RATE;
            if (ctml2 >= dml2) {
                ctml2 = 0;
//                dmr2 = randomDuration();
            }
            ctmr1 += UPDATE_RATE;
            if (ctmr1 >= dmr1) {
                ctmr1 = 0;
//                dmr1 = randomDuration();
            }
            ctmr2 += UPDATE_RATE;
            if (ctmr2 >= dmr2) {
                ctmr2 = 0;
//                dmr2 = randomDuration();
            }
        }

    }

    private long randomDuration() {
        return (long) (640 + random.nextFloat() * 1000 * 1.6);
    }

    private float randomRadius() {
        return getMeasuredWidth() * 0.1f + random.nextFloat() * 0.1f;
    }

    private PointF p(float x, float y) {
        return new PointF(x, y);
    }

    private double cos(double angle) {
        return Math.cos(angle);
    }

    private double sin(double angle) {
        return Math.sin(angle);
    }
}
