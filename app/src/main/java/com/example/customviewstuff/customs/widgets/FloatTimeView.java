package com.example.customviewstuff.customs.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.customviewstuff.helpers.RotateHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/6
 */
public class FloatTimeView extends View {
    private Paint textPaint, datePaint;
    private float progress;
    private ValueAnimator animator;
    private String now, datee, week, last;
    private Calendar calendar;
    private Date date;
    private SimpleDateFormat format;
    private float mTextSize, showTextSize;
    private static final String FONT_DIGITAL_7 = "fonts" + File.separator + "digital-7-m.ttf";
    private final String TIME = "HH:mm:ss", DATE = "yyyy-MM-dd", WEEK = "EEEE";

    private float radius, cx, cy;
    private RotateHelper helper;

    public FloatTimeView(Context context) {
        this(context, null);
    }

    public FloatTimeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.TRANSPARENT);
        AssetManager assets = getResources().getAssets();
        Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        textPaint.setTypeface(font);

        datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        datePaint.setColor(Color.LTGRAY);
        datePaint.setTextAlign(Paint.Align.CENTER);

        animator = new ValueAnimator();
        animator.setFloatValues(0, 1);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });

        calendar = Calendar.getInstance();
        format = new SimpleDateFormat(TIME, Locale.CHINA);
        date = new Date();
        now = format.format(date);
        format.applyPattern(DATE);
        datee = format.format(date);
        format.applyPattern(WEEK);
        week = format.format(date);
        last = now;

        helper = new RotateHelper(this);
    }

    public void start() {
        post(tickRun);
    }

    public void stop() {
        removeCallbacks(tickRun);
    }

    public void destroy() {
        stop();
        animator.cancel();
    }

    private Runnable tickRun = new Runnable() {
        @Override
        public void run() {
            animator.cancel();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int millis = calendar.get(Calendar.MILLISECOND);
            postDelayed(this, 1020 - millis);
            date.setTime(System.currentTimeMillis());
            last = now;
            format.applyPattern(TIME);
            now = format.format(date);
            format.applyPattern(DATE);
            datee = format.format(date);
            format.applyPattern(WEEK);
            week = format.format(date);
            int duration = (1000 - millis) / 2;
            animator.setDuration(duration < 100 ? 100 : duration);
            animator.start();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
        radius = (float) ((getMeasuredWidth() >> 1) * 0.86);
        cx = cy = getMeasuredWidth() >> 1;
        mTextSize = (float) getMeasuredWidth() / 15;
        showTextSize = (float) getMeasuredWidth() / 5;
        textPaint.setTextSize(showTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        helper.cameraRotate(canvas);
        super.onDraw(canvas);
        drawRulers(canvas);
        drawToText(canvas);
        drawFormText(canvas);
        drawDate(canvas);
    }

    private void drawDate(Canvas canvas) {
        datePaint.setTextSize((float) getMeasuredWidth() / 14);
        canvas.drawText(datee, cx, (float) (getMeasuredWidth() * 0.38), datePaint);
        canvas.drawText(week, cx, (float) (getMeasuredWidth() * 0.72), datePaint);
    }

    private void drawToText(Canvas canvas) {
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(showTextSize);
        float baseX = cx - textPaint.measureText(last) / 2;
        float baseY = cy + showTextSize / 2;
        float fx, fy, tx, ty;
        for (int i = 0; i < last.length(); i++) {
            String temp = String.valueOf(last.charAt(i));
            if (i == 0) {
                fx = baseX;
            } else {
                textPaint.setTextSize(showTextSize);
                fx = baseX + textPaint.measureText(last.substring(0, i));
            }
            fy = baseY;
            if (isNumber(last.charAt(i)) && !temp.equals(String.valueOf(now.charAt(i)))) {
                int target = Integer.parseInt(temp);
                float[] to = rulerPosition(target);
                tx = to[0];
                ty = to[1];
                textPaint.setTextSize(showTextSize + (mTextSize - showTextSize) * progress);
                canvas.drawText(temp, fx + (tx - fx) * progress, fy + (ty - fy) * progress, textPaint);
            }
        }
    }

    private void drawFormText(Canvas canvas) {
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(showTextSize);
        float baseX = cx - textPaint.measureText(now) / 2;
        float baseY = cy + showTextSize / 2;
        float fx, fy, tx, ty;
        for (int i = 0; i < now.length(); i++) {
            String temp = String.valueOf(now.charAt(i));
            if (i == 0) {
                fx = baseX;
            } else {
                textPaint.setTextSize(showTextSize);
                fx = baseX + textPaint.measureText(now.substring(0, i));
            }
            fy = baseY;
            if (isNumber(now.charAt(i)) && !temp.equals(String.valueOf(last.charAt(i)))) {
                int target = Integer.parseInt(temp);
                float[] to = rulerPosition(target);
                tx = to[0];
                ty = to[1];
                textPaint.setTextSize(mTextSize + (showTextSize - mTextSize) * progress);
                canvas.drawText(temp, tx + (fx - tx) * progress, ty + (fy - ty) * progress, textPaint);
            } else {
                textPaint.setTextSize(showTextSize);
                canvas.drawText(temp, fx, fy, textPaint);
            }
        }
    }

    private void drawRulers(Canvas canvas) {
        textPaint.setTextSize(mTextSize);
        for (int i = 0; i < 10; i++) {
            textPaint.setColor(Color.WHITE);
            String text = String.valueOf(i);
            for (int j = 0; j < now.length(); j++) {
                if (String.valueOf(now.charAt(j)).equals(String.valueOf(i)) || String.valueOf(last.charAt(j)).equals(String.valueOf(i))) {
                    textPaint.setColor(0x66666666);
                }
            }
            float[] position = rulerPosition(i);
            canvas.drawText(text, position[0], position[1], textPaint);
            textPaint.setColor(Color.WHITE);
        }
    }

    private float[] rulerPosition(int num) {
        float[] positions = new float[2];
        float ruler = (float) (Math.PI / 5);
        double offset = Math.PI / 2;
        double rulerAngle = num * ruler - offset;
        positions[0] = (float) (cx + radius * Math.cos(rulerAngle));
        positions[1] = ((float) (cy + radius * Math.sin(rulerAngle))) + mTextSize / 2;
        return positions;
    }

    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

}
