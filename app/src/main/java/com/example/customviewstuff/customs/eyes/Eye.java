package com.example.customviewstuff.customs.eyes;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/25
 * Description: blablabla
 */
public abstract class Eye {
    protected float cx, cy, width, height, ballRadius;
    protected Path drawPath;
    private RadialGradient gradient;

    Eye(float cx, float cy, float width, float height) {
        this.cx = cx;
        this.cy = cy;
        this.width = width;
        this.height = height;
        ballRadius = width * 0.2f;
        drawPath = new Path();
    }

    void drawRedBase(Canvas canvas, Paint paint) {
        if (gradient == null) {
            gradient = new RadialGradient(cx, cy, ballRadius, new int[]{Color.RED, 0xFF8B0000}, null, Shader.TileMode.CLAMP);
        }
        paint.setShader(gradient);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(5f);
        canvas.drawCircle(cx, cy, ballRadius, paint);
        paint.setShader(null);
    }

    void blackDot(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);
        canvas.drawCircle(cx, cy, ballRadius * 0.18f, paint);
    }

    public abstract void draw(Canvas canvas, Paint paint);

    public abstract Eye next();
}
