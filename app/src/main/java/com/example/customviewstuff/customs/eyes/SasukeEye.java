package com.example.customviewstuff.customs.eyes;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

public class SasukeEye extends Eye {
    private Path mPath;

    SasukeEye(float cx, float cy, float width, float height) {
        super(cx, cy, width, height);
        Path path = new Path();
        mPath = new Path();
        float offset = ballRadius * 0.8f;
        path.moveTo(cx, cy - ballRadius);
        path.quadTo(cx - offset, cy, cx, cy + ballRadius);
        path.quadTo(cx + offset, cy, cx, cy - ballRadius);
        Matrix matrix = new Matrix();
        for (int i = 0; i < 3; i++) {
            matrix.setRotate(i * 60, cx, cy);
            mPath.addPath(path, matrix);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        drawRedBase(canvas, paint);
        blackDot(canvas, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        canvas.drawPath(mPath, paint);
        paint.setStyle(Paint.Style.FILL);
        drawPath.reset();
        drawPath.addCircle(cx, cy, ballRadius + 5f, Path.Direction.CCW);
        drawPath.op(mPath, Path.Op.DIFFERENCE);
        canvas.drawPath(drawPath, paint);
    }

    @Override
    public Eye next() {
        return new NormalEye(cx, cy, width, height);
    }
}
