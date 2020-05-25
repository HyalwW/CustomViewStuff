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
    private int[] colors = new int[]{0xBBFF4040, 0xBBFF4500, 0xBBFF0000, 0xBBEE4000, 0xBBEE2C2C};

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
        flames.add(new Flame(p(w * 0.3f, h), p(w * 0.7f, h), p(w >> 1, h >> 1),
                p(w * 0.3f, h * 0.8f), p(w * 0.4f, h * 0.6f),
                p(w * 0.7f, h * 0.8f), p(w * 0.6f, h * 0.6f)));
//        if (flames.size() == 0) {
//            for (int i = 0; i < 16; i++) {
//                flames.add(randomFlame());
//            }
//        }
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
        mPaint.setColor(Color.RED);
        for (Flame flame : flames) {
            drawPath.reset();
            drawPath.moveTo(flame.bl.x, flame.bl.y);
            float l1x = (float) (flame.bml1c.x + flame.lml1 * cos(flame.al1));
            float l1y = (float) (flame.bml1c.y + flame.lml1 * sin(flame.al1));
            float l2x = (float) (flame.bml2c.x + flame.lml2 * cos(flame.al2));
            float l2y = (float) (flame.bml2c.y + flame.lml2 * sin(flame.al2));
            float tx = (float) (flame.btc.x + flame.lt * cos(flame.at));
            float ty = (float) (flame.btc.y + flame.lt * sin(flame.at));
            drawPath.cubicTo(l1x, l1y, l2x, l2y, tx, ty);
            float r1x = (float) (flame.bmr1c.x + flame.lmr1 * cos(flame.ar1));
            float r1y = (float) (flame.bmr1c.y + flame.lmr1 * sin(flame.ar1));
            float r2x = (float) (flame.bmr2c.x + flame.lmr2 * cos(flame.ar2));
            float r2y = (float) (flame.bmr2c.y + flame.lmr2 * sin(flame.ar2));
            drawPath.cubicTo(r2x, r2y, r1x, r1y, flame.br.x, flame.br.y);
//            drawPath.lineTo(l1x, l1y);
//            drawPath.lineTo(l2x, l2y);
//            drawPath.lineTo(tx, ty);
//            drawPath.lineTo(r2x, r2y);
//            drawPath.lineTo(r1x, r1y);
//            drawPath.lineTo(flame.br.x, flame.br.y);
            drawPath.close();
            mPaint.setColor(flame.color);
            canvas.drawPath(drawPath, mPaint);
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

    class Flame {
        PointF bl, br, btc, bml1c, bml2c, bmr1c, bmr2c;
        float lt, lml1, lml2, lmr1, lmr2;
        double at, al1, al2, ar1, ar2;
        double ait, ail1, ail2, air1, air2;
        int color;

        Flame(PointF bl, PointF br, PointF btc, PointF bml1c, PointF bml2c, PointF bmr1c, PointF bmr2c) {
            this.bl = bl;
            this.br = br;
            this.btc = btc;
            this.bml1c = bml1c;
            this.bml2c = bml2c;
            this.bmr1c = bmr1c;
            this.bmr2c = bmr2c;
            lt = randomRadius();
            lml1 = randomRadius();
            lml2 = randomRadius();
            lmr1 = randomRadius();
            lmr2 = randomRadius();
            color = randomColor();
            ait = randomAngleIncrement();
            ail1 = randomAngleIncrement();
            ail2 = randomAngleIncrement();
            air1 = randomAngleIncrement();
            air2 = randomAngleIncrement();
        }

        void move() {
            at += ait;
            ait +=  randomAngleIncrement() / 70;
            al1 += ail1;
            ail1 += randomAngleIncrement() / 70;
            al2 += ail2;
            ail2 += randomAngleIncrement() / 70;
            ar1 += air1;
            air1 += randomAngleIncrement() / 70;
            ar2 += air2;
            air2 += randomAngleIncrement() / 70;
        }

    }

    private double randomAngleIncrement() {
        return random.nextFloat() * PI / 20 - PI / 40;
    }

    private int randomColor() {
        return colors[random.nextInt(colors.length - 1)];
    }

    private float randomRadius() {
        return getMeasuredWidth() * 0.07f + random.nextFloat() * 0.07f;
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

    private Flame randomFlame() {
        int w = getMeasuredWidth(), h = getMeasuredHeight();
        float bxl = random.nextFloat() * w * 0.7f;
        float bxr = bxl + random.nextFloat() * w * 0.3f + w * 0.15f;
        float by = h * 0.9f + random.nextFloat() * h * 0.1f;

        float tx = (bxl + bxr) / 2;

        float ml1x = bxl;
        float ml1y = by - h * 0.1f + h * 0.07f * random.nextFloat();

        float ml2x = (ml1x + tx) / 2 - w * 0.02f + w * 0.04f * random.nextFloat();
        float ml2y = ml1y - h * 0.10f + h * 0.09f * random.nextFloat();


        float mr1x = bxr;
        float mr1y = by - h * 0.1f + h * 0.07f * random.nextFloat();

        float mr2x = (mr1x + tx) / 2 - w * 0.02f + w * 0.04f * random.nextFloat();
        float mr2y = mr1y - h * 0.12f + h * 0.09f * random.nextFloat();

        float ty = Math.min(ml2y, mr2y) - h * 0.15f + h * 0.12f * random.nextFloat();

        return new Flame(p(bxl, by), p(bxr, by), p(tx, ty), p(ml1x, ml1y), p(ml2x, ml2y), p(mr1x, mr1y), p(mr2x, mr2y));
    }
}
