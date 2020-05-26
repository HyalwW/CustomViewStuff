package com.example.customviewstuff.customs.eyes;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/25
 * Description: blablabla
 */
public class TriEye extends Eye {
    private Random random;
    private int state;

    TriEye(float cx, float cy, float width, float height) {
        super(cx, cy, width, height);
        state = 1;
        random = new Random();
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        drawRedBase(canvas, paint);
        blackDot(canvas, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ballRadius * 0.03f);
        canvas.drawCircle(cx, cy, ballRadius * 0.7f, paint);
        paint.setStyle(Paint.Style.FILL);
        int base = 360 / state;
        for (int i = 0; i < state; i++) {
            canvas.save();
            int angle = base * i;
            canvas.rotate(angle, cx, cy);
            canvas.drawCircle(cx, cy - ballRadius * 0.7f, ballRadius * 0.15f, paint);
            drawPath.reset();
            drawPath.moveTo(cx - ballRadius * 0.15f, cy - ballRadius * 0.7f);
            drawPath.quadTo(cx - ballRadius * 0.15f, cy - ballRadius * 0.9f, cx + ballRadius * 0.12f, cy - ballRadius * 0.92f);
            drawPath.close();
            canvas.drawPath(drawPath, paint);
            canvas.restore();
        }
    }

    @Override
    public Eye next() {
        if (state == 3) {
            int ri = random.nextInt(2);
            switch (ri) {
                case 0:
                    return new SasukeEye(cx, cy, width, height);
                case 1:
                default:
                    return new RinneganEye(cx, cy, width, height);
            }
        }
        state++;
        return this;
    }
}
