package com.example.customviewstuff.customs.eyes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public class WhiteEye extends Eye {
    private RadialGradient gradient;

    WhiteEye(float cx, float cy, float width, float height) {
        super(cx, cy, width, height);
        gradient = new RadialGradient(cx, cy, ballRadius * 1.2f, new int[]{Color.WHITE, Color.WHITE, Color.LTGRAY, Color.WHITE, Color.LTGRAY,  Color.DKGRAY, Color.TRANSPARENT}, new float[]{0f, 0.22f, 0.34f, 0.36f, 0.8f, 0.93f, 1f}, Shader.TileMode.CLAMP);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setShader(gradient);
        canvas.drawCircle(cx, cy, ballRadius * 1.2f, paint);
        paint.setShader(null);
    }

    @Override
    public Eye next() {
        return new NormalEye(cx, cy, width, height);
    }
}
