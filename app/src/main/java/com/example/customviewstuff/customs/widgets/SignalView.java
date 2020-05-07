package com.example.customviewstuff.customs.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class SignalView extends View {
    private Paint signalPaint;
    private float gap, signalWidth;
    private int strength;
    private RectF rectF;
    private int height;

    public SignalView(Context context) {
        this(context, null);
    }

    public SignalView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        signalPaint = new Paint();
        signalPaint.setStyle(Paint.Style.FILL);
        signalPaint.setAntiAlias(true);
        signalPaint.setColor(Color.WHITE);
        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int) (heightSize * 1.5), heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        gap = (float) (getWidth() * 0.05);
        signalWidth = (getWidth() - 6 * gap) / 5;
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 5; i++) {
            rectF.set(gap + (gap + signalWidth) * i, (float) (height * 0.2 * (4 - i)), (gap + signalWidth) * (i + 1), height);
            signalPaint.setColor(strength > i ? Color.WHITE : Color.DKGRAY);
            canvas.drawRoundRect(rectF, 3, 3, signalPaint);
        }
    }

    /**
     * 信号强度
     *
     * @param strength 0~4
     */
    public void setStrength(int strength) {
        this.strength = strength;
        postInvalidate();
    }
}
