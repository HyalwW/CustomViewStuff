package com.example.customviewstuff.customs.eyes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public class RinneganEye extends Eye {
    private RadialGradient gradient;

    RinneganEye(float cx, float cy, float width, float height) {
        super(cx, cy, width, height);
        gradient = new RadialGradient(cx, cy, ballRadius * 0.12f, new int[]{Color.BLACK, Color.DKGRAY, Color.TRANSPARENT}, new float[]{0f, 0.9f, 1f}, Shader.TileMode.CLAMP);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(0xFF8B7B8B);
        canvas.drawCircle(cx, cy, width * 0.5f, paint);
        float radius = 0, br = width * 0.09f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2f);
        for (int i = 0; i < 3; i++) {
            radius += br;
            canvas.drawCircle(cx, cy, radius, paint);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF8B668B);
        canvas.drawCircle(cx, cy, br, paint);
        paint.setShader(gradient);
        canvas.drawCircle(cx, cy, ballRadius * 0.12f, paint);
        paint.setShader(null);
    }

    @Override
    public Eye next() {
        return new MultiTriEye(cx, cy, width, height);
    }
}
