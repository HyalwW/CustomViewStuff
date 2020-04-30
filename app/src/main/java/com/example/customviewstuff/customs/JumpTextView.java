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
 * Date: 2020/4/29
 */
public class JumpTextView extends BaseSurfaceView {
    private float textSize;
    private int textInLine = 20;
    private String text;
    private float maxHeight;
    private int stopIndex;
    private List<Double> hs;
    private static final long duration = 500, NEXT_DELAY = 400;
    private static final int JUMP_GAP = 7;

    public JumpTextView(Context context) {
        super(context);
    }

    public JumpTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JumpTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        hs = new CopyOnWriteArrayList<>();
        mPaint.setColor(Color.WHITE);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            builder.append("测试测试测试测试测试测试测试测测试测试测");
        }
        setText(builder.toString());
    }

    @Override
    protected void onReady() {
        textSize = (float) getMeasuredWidth() / textInLine;
        mPaint.setTextSize(textSize);
        maxHeight = getMeasuredHeight() * 0.05f;
        stopIndex = 0;
        startAnim();
    }

    private int gap = 0;

    @Override
    protected void onDataUpdate() {
        for (int i = 0; i < stopIndex; i++) {
            double ff = hs.get(i);
            ff += Math.PI * UPDATE_RATE / duration;
            if (ff - Math.PI > NEXT_DELAY * Math.PI / duration) {
                ff = 0;
            }
            hs.set(i, ff);
        }
        if (stopIndex < hs.size()) {
            if (gap < JUMP_GAP) {
                gap++;
            } else {
                stopIndex++;
                gap = 0;
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        int row = 0;
        for (int i = 0; i < text.length(); i++) {
            String str = String.valueOf(text.charAt(i));
            int col = i % textInLine;
            if (col == 0) {
                row++;
            }
            double x = hs.get(i) > Math.PI ? Math.PI : hs.get(i);
            mPaint.setColor(Color.rgb(255, (int) (255 - Math.sin(x) * 255), 255));
            canvas.drawText(str, col * textSize, (float) ((textSize + maxHeight) * row - Math.sin(x) * maxHeight), mPaint);
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

    public void setText(String text) {
        this.text = text;
        hs.clear();
        for (int i = 0; i < text.length(); i++) {
            hs.add(0d);
        }
    }
}
