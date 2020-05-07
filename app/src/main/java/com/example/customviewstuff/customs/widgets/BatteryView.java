package com.example.customviewstuff.customs.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.customviewstuff.R;


public class BatteryView extends View {
    private Paint strokePaint, quantityPaint, numPaint, chargePaint, headPaint;

    private float strokeWidth, textSize, batteryLeft, batteryTop, batteryRight, batteryBottom;
    private int quantity = 100;
    private int height;
    private int width;
    private boolean inCharging;
    private Bitmap bitmap;
    private Rect dst;

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        strokePaint.setColor(Color.GRAY);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);

        quantityPaint = new Paint();
        quantityPaint.setAntiAlias(true);
        quantityPaint.setStyle(Paint.Style.FILL);

        numPaint = new Paint();
        numPaint.setAntiAlias(true);
        numPaint.setColor(Color.WHITE);
        numPaint.setFakeBoldText(true);
        numPaint.setTextAlign(Paint.Align.CENTER);

        chargePaint = new Paint();
        chargePaint.setAntiAlias(true);
        chargePaint.setFilterBitmap(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.charge);

        headPaint = new Paint();
        headPaint.setAntiAlias(true);
        headPaint.setStyle(Paint.Style.FILL);
        headPaint.setColor(Color.GRAY);

        dst = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int) (heightSize * 2.5), heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        height = getHeight();
        width = getWidth();
        batteryLeft = (float) (width * 0.15);
        batteryRight = (float) (width * 0.9);
        batteryTop = (float) (height * 0.15);
        batteryBottom = (float) (height * 0.85);
        strokeWidth = (float) (height * 0.1);
        textSize = (float) (height * 0.5);
        dst.set(((int) (width * 0.15)), ((int) (height * 0.15)), ((int) (width * 0.35)), ((int) (height * 0.85)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStroke(canvas);
        drawQuantity(canvas);
        drawText(canvas);
        drawHead(canvas);
        if (inCharging) {
            drawCharge(canvas);
        }
    }

    private void drawStroke(Canvas canvas) {
        strokePaint.setStrokeWidth(strokeWidth);
        RectF rectf = new RectF(batteryLeft, batteryTop, batteryRight, batteryBottom);
        canvas.drawRoundRect(rectf, 1, 1, strokePaint);
    }

    private void drawQuantity(Canvas canvas) {
        if (quantity > 30) {
            quantityPaint.setColor(Color.parseColor("#00CD66"));
        } else if (quantity > 15) {
            quantityPaint.setColor(Color.YELLOW);
        } else {
            quantityPaint.setColor(Color.RED);
        }
        RectF rectf = new RectF(batteryLeft, batteryTop, batteryLeft + (batteryRight - batteryLeft) * ((float) quantity / 100), batteryBottom);
        canvas.drawRoundRect(rectf, 1, 1, quantityPaint);
    }

    private void drawText(Canvas canvas) {
        numPaint.setTextSize(textSize);
        canvas.drawText(String.valueOf(quantity), (float) (width * 0.54), (float) ((height >> 1) + textSize / 2.5), numPaint);
    }

    private void drawHead(Canvas canvas) {
        float headLeft = (float) (batteryRight + width * 0.03);
        float headWidth = (float) (width * 0.04);
        RectF rectf = new RectF(headLeft, ((float) (height * 0.25)), headLeft + headWidth, ((float) (height * 0.75)));
        canvas.drawRoundRect(rectf, 3, 3, headPaint);
    }

    private void drawCharge(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, dst, chargePaint);
    }

    /**
     * 设置电量
     *
     * @param quantity 电量（0~100）
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        postInvalidate();
    }

    public void charge(boolean charge) {
        inCharging = charge;
        postInvalidate();
    }
}
