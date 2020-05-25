package com.example.customviewstuff.customs.eyes;


import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/25
 * Description: blablabla
 */
public class NormalEye extends Eye {
    private Random random;

    NormalEye(float cx, float cy, float width, float height) {
        super(cx, cy, width, height);
        random = new Random();
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        drawRedBase(canvas, paint);
    }

    @Override
    public Eye next() {
        int ri = random.nextInt(2);
        switch (ri) {
            case 0:
                return new WhiteEye(cx, cy, width, height);
            case 1:
            default:
                return new TriEye(cx, cy, width, height);
        }
    }

}
