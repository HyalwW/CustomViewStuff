package com.example.customviewstuff.customs.widgets;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.customviewstuff.helpers.TimeUtils;

import java.io.File;
import java.util.Calendar;

/**
 * Created by Wang.Wenhui
 * Date: 2019/12/30
 * Description:时钟View
 */
public class TimeView extends View {
    private Paint rulerPaint, textPaint;
    private String now, date;
    private float p, cx, cy;
    private float secCx, sexCy;
    private int hour, minute, second;
    private Paint hollowCirclePaint;
    private int secondMillis;
    private double offset = Math.PI / 2;
    private Calendar calendar;
    private float baseSize;
    private float secSize, minSize, houSize;
    private static final String FONT_DIGITAL_7 = "fonts" + File.separator + "digital-7-m.ttf";


    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        rulerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rulerPaint.setColor(Color.WHITE);
        rulerPaint.setTextAlign(Paint.Align.CENTER);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        AssetManager assets = getResources().getAssets();
        final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        textPaint.setTypeface(font);
//        textPaint.setFakeBoldText(true);

        hollowCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hollowCirclePaint.setStyle(Paint.Style.STROKE);
        hollowCirclePaint.setColor(Color.WHITE);

        calendar = Calendar.getInstance();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, size);
        baseSize = (float) size / 300;
        rulerPaint.setTextSize(dp2px(16));
        textPaint.setTextSize(dp2px(45));
        hollowCirclePaint.setStrokeWidth(dp2px(1));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        p = (float) ((getMeasuredWidth() >> 1) * 0.8);
        secCx = cx = cy = getMeasuredWidth() >> 1;
        sexCy = (float) (getMeasuredHeight() * 0.7);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRulers(canvas);
        drawSecondCircles(canvas);
        drawTicks(canvas);
        drawTime(canvas);
    }

    private void drawRulers(Canvas canvas) {
        rulerPaint.setColor(Color.WHITE);
        for (int i = 0; i < 60; i++) {
            float ruler = (float) (Math.PI / 30);
            double rulerAngle = i * ruler - offset;
            if (i % 15 == 0) {
                canvas.drawCircle(((float) (cx + p * Math.cos(rulerAngle))), ((float) (cy + p * Math.sin(rulerAngle))), dp2px(4), rulerPaint);
            } else if (i % 5 == 0) {
                canvas.drawCircle(((float) (cx + p * Math.cos(rulerAngle))), ((float) (cy + p * Math.sin(rulerAngle))), dp2px(2), rulerPaint);
            } else {
                canvas.drawCircle(((float) (cx + p * Math.cos(rulerAngle))), ((float) (cy + p * Math.sin(rulerAngle))), dp2px(1), rulerPaint);
            }

            if (i % 5 == 0) {
                String text = i == 0 ? "12" : String.valueOf(i / 5);
                canvas.drawText(text, ((float) (cx + p * 0.87 * Math.cos(rulerAngle))), ((float) (cy + p * 0.87 * Math.sin(rulerAngle))) + dp2px(6), rulerPaint);
            }
        }
    }

    private void drawSecondCircles(Canvas canvas) {
        canvas.drawCircle(secCx, sexCy, dp2px(20), hollowCirclePaint);
        canvas.drawCircle(secCx, sexCy, dp2px(2), hollowCirclePaint);
        double angle = (float) secondMillis / 500 * Math.PI - offset;
        canvas.drawLine((float) (secCx + dp2px(2) * Math.cos(angle)),
                (float) (sexCy + dp2px(2) * Math.sin(angle)),
                (float) (secCx + dp2px(16) * Math.cos(angle)),
                (float) (sexCy + dp2px(16) * Math.sin(angle)), hollowCirclePaint);
    }

    private void drawTicks(Canvas canvas) {
        double aHou = hour * Math.PI / 6 - offset;
        rulerPaint.setColor(Color.parseColor("#CD2626"));
        canvas.drawCircle(((float) (cx + p * Math.cos(aHou))), ((float) (cy + p * Math.sin(aHou))), houSize, rulerPaint);
        double aMin = minute * Math.PI / 30 - offset;
        rulerPaint.setColor(Color.parseColor("#3A5FCD"));
        canvas.drawCircle(((float) (cx + p * Math.cos(aMin))), ((float) (cy + p * Math.sin(aMin))), minSize, rulerPaint);
        double aSec = second * Math.PI / 30 - offset;
        rulerPaint.setColor(Color.GREEN);
        canvas.drawCircle(((float) (cx + p * Math.cos(aSec))), ((float) (cy + p * Math.sin(aSec))), secSize, rulerPaint);
    }

    private void drawTime(Canvas canvas) {
        rulerPaint.setColor(Color.LTGRAY);
        canvas.drawText(date, cx, cy - dp2px(50), rulerPaint);
        canvas.drawText(now, cx, cy, textPaint);
    }

    private Runnable millisRun = new Runnable() {
        @Override
        public void run() {
            postDelayed(this, 16);
            if (houSize < dp2px(6)) {
                houSize += 0.2;
            }
            if (minSize < dp2px(5)) {
                minSize += 0.2;
            }
            if (secSize < dp2px(4)) {
                secSize += 0.2;
            }
            setTime();
            postInvalidate();
        }
    };

    private void setTime() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        now = TimeUtils.getNowTimeString("HH:mm:ss");
        date = TimeUtils.getNowTimeString("yyyy-MM-dd");
        if (hour != calendar.get(Calendar.HOUR)) {
            hour = calendar.get(Calendar.HOUR);
            houSize = dp2px(1);
        }
        if (minute != calendar.get(Calendar.MINUTE)) {
            minute = calendar.get(Calendar.MINUTE);
            minSize = dp2px(1);
        }
        if (second != calendar.get(Calendar.SECOND)) {
            second = calendar.get(Calendar.SECOND);
            secSize = dp2px(1);
        }
        secondMillis = calendar.get(Calendar.MILLISECOND);
    }

    public void tick() {
        removeCallbacks(millisRun);
        setTime();
        postInvalidate();
        post(millisRun);
    }

    public void destroy() {
        removeCallbacks(millisRun);
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    private float dp2px(float dpValue) {
        return baseSize * dpValue;
//        final float scale = getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
    }
}
