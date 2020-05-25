package com.example.customviewstuff.customs.eyes;


import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/25
 * Description: blablabla
 */
public class NormalEye extends Eye {

    NormalEye(float cx, float cy, float width, float height) {
        super(cx, cy, width, height);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        drawRedBase(canvas, paint);
    }

}
