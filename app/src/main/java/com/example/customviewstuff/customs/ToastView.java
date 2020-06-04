package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/4
 * Description: blablabla
 */
public class ToastView extends BaseSurfaceView {
    private List<Tag> tags;
    private float[] ys;
    private int maxCount = 12;
    private float textSize, gap;
    private float incrementX, incrementY;

    public ToastView(Context context) {
        super(context);
    }

    public ToastView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        tags = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void onReady() {
        textSize = getMeasuredWidth() * 0.05f;
        ys = new float[maxCount];
        gap = (float) getMeasuredHeight() / maxCount;
        incrementY = gap / 10;
        incrementX = gap / 5;
        if (textSize > gap) {
            textSize = gap;
        }
        for (int i = 0; i < maxCount; i++) {
            ys[i] = gap * (i + 1);
        }
        mPaint.setTextSize(textSize);
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        for (Tag tag : tags) {
            tag.move();
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        for (Tag tag : tags) {
            canvas.drawText(tag.content, tag.x, tag.y, mPaint);
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

    public void toast(String content) {
        float x, y;
        x = -mPaint.measureText(content);
        if (tags.size() < maxCount) {
            y = tags.size() * gap + gap;
            Tag tag = new Tag(content, x, y);
            tags.add(tag);
        } else {
            Tag tag = tags.remove(0);
            y = gap * maxCount;
            tag.reset(content, x, y);
            sort();
            tags.add(tag);
        }
    }

    private void sort() {
        int index = 0;
        for (Tag tag : tags) {
            tag.ty = ys[index++];
        }
    }

    private class Tag {
        String content;
        float bx, x, y, ty;
        long keepTime;

        Tag(String content, float x, float y) {
            reset(content, x, y);
        }

        public void reset(String content, float x, float y) {
            this.content = content;
            this.x = this.bx = x;
            this.ty = this.y = y;
            keepTime = 0;
        }

        void move() {
            if (y > ty) {
                y -= incrementY;
            } else {
                y = ty;
            }
            if (keepTime == 0 && x < 0) {
                x += incrementX;
            } else {
                keepTime += UPDATE_RATE;
            }
            if (keepTime >= 3000) {
                x -= incrementX;
                if (x <= bx) {
                    tags.remove(this);
                    sort();
                }
            }
        }
    }
}
