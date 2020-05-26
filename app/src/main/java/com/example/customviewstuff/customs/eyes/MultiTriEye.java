package com.example.customviewstuff.customs.eyes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/26
 * Description: blablabla
 */
public class MultiTriEye extends Eye {
    MultiTriEye(float cx, float cy, float width, float height) {
        super(cx, cy, width, height);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        drawRedBase(canvas, paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(cx, cy, ballRadius * 0.1f, paint);
        float radius = 0, br = ballRadius / 4;
        int startAngle = 0;
        for (int i = 0; i < 3; i++) {
            radius += br;
            startAngle += 60;
            drawTri(canvas, paint, radius, startAngle);
        }
    }

    private void drawTri(Canvas canvas, Paint paint, float radius, int startAngle) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(ballRadius * 0.03f);
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 3; i++) {
            canvas.save();
            int angle = 120 * i + startAngle;
            canvas.rotate(angle, cx, cy);
            float br = ballRadius * 0.1f;
            canvas.drawCircle(cx, cy - radius, br, paint);
            drawPath.reset();
            drawPath.moveTo(cx - br, cy - radius);
            drawPath.quadTo(cx - br, cy - radius - br * 1.5f, cx + br * 1.2f, cy - radius - br * 1.4f);
            drawPath.close();
            canvas.drawPath(drawPath, paint);
            canvas.restore();
        }
    }

    @Override
    public Eye next() {
        return new NormalEye(cx, cy, width, height);
    }
}
