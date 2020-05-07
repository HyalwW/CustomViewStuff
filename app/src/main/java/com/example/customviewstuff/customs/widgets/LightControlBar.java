package com.example.customviewstuff.customs.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LightControlBar extends View {
    private int progress;
    private int width, height;
    private Paint sunPaint;
    private int yellow = 70, gray = 70;

    private Paint shinePaint;
    private float maxShineRadius, maxLength;
    private double shineAngel;

    private Paint bgPaint, rectPaint, sunBgPaint;
    private int px, py;
    private Rect bgr, sunBg;

    private float startY;
    private float[] radiusArray;
    private Path roundPath;
    private LightChangeListener listener;

    public LightControlBar(Context context) {
        super(context);
        init();
    }

    public LightControlBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LightControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        sunPaint = new Paint();
        sunPaint.setStyle(Paint.Style.FILL);
        sunPaint.setColor(getColor());
        sunPaint.setAntiAlias(true);

        shinePaint = new Paint();
        shinePaint.setAntiAlias(true);
        shinePaint.setColor(Color.WHITE);
        shinePaint.setStyle(Paint.Style.FILL);
        shinePaint.setStrokeCap(Paint.Cap.ROUND);
        shineAngel = Math.PI / 6;

        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        rectPaint = new Paint();
        sunBgPaint = new Paint();
        sunBgPaint.setColor(Color.LTGRAY);

        bgPaint.setColor(Color.DKGRAY);
        rectPaint.setColor(Color.GRAY);
        radiusArray = new float[8];
        for (int i = 0; i < radiusArray.length; i++) {
//            radiusArray[i] = getResources().getDimension(R.dimen.px_8);
            radiusArray[i] = 4f;
        }
        roundPath = new Path();
    }

    public void setListener(LightChangeListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (heightMeasureSpec < widthMeasureSpec * 3) {
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec * 3);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        maxLength = (float) width / 12;
        maxShineRadius = maxLength / 2;
        bgr = new Rect(0, 0, width, height - width);
        sunBg = new Rect(0, height - width, width, height);
        px = width >> 1;
        py = height - (width / 2);
        roundPath.addRoundRect(new RectF(0, 0, width, height), radiusArray, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(roundPath);
        super.onDraw(canvas);
        canvas.drawRect(bgr, bgPaint);
        int top = (height - width) - (int) ((height - width) * ((float) progress / 100));
        Rect pr = new Rect(0, top, width, height - width);
        canvas.drawRect(pr, rectPaint);
        canvas.drawRect(sunBg, sunBgPaint);
        RadialGradient gradient = new RadialGradient(px, py, (width >> 1) - ((float) width / 5), new int[]{getColor(), Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        sunPaint.setShader(gradient);
        canvas.drawCircle(px, py, (width >> 1) - ((float) width / 5), sunPaint);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            drawShines(canvas);
        }
    }

    private void drawShines(Canvas canvas) {
        shinePaint.setColor(getColor());
        float shinesRadius = (width >> 1) - ((float) width / 8);
        float curRadius, curLength;
        for (int i = 0; i < 12; i++) {
            if (progress <= 30) {
                shinePaint.setAlpha((int) ((float) progress / 30 * 255));
                curRadius = maxShineRadius * (float) progress / 30;
                canvas.drawCircle(shinesRadius * (float) Math.cos(shineAngel * i) + px,
                        shinesRadius * (float) Math.sin(shineAngel * i) + py,
                        curRadius, shinePaint);
            } else {
                shinePaint.setStrokeWidth(maxLength);
                curLength = maxLength * progress / 70 - 3 * maxLength / 7;
                canvas.drawLine(shinesRadius * (float) Math.cos(shineAngel * i) + px,
                        shinesRadius * (float) Math.sin(shineAngel * i) + py,
                        (shinesRadius + curLength) * (float) Math.cos(shineAngel * i) + px,
                        (shinesRadius + curLength) * (float) Math.sin(shineAngel * i) + py,
                        shinePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (startY > height - width && event.getY() > height - width) {
                    return true;
                }
                int top = (int) event.getY();
                if (top > height - width) {
                    top = height - width;
                }
                if (top < 0) {
                    top = 0;
                }
                int cHeight = height - width - top;
                int progress = (int) (100 * ((float) cHeight / (height - width)));
                setProgress(progress);
                if (listener != null) {
                    listener.onValueChanged(progress);
                }
                break;
        }
        return true;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (progress < 30) {
            yellow = (int) (70 + (double) progress / 30 * 50);
            gray = yellow;
        } else {
            yellow = (int) (480 / 7 + ((double) progress / 100) * 1200 / 7);
            if (210 - ((double) progress / 100) * 300 > 0) {
                gray = (int) (210 - ((double) progress / 100) * 300);
            } else {
                gray = 0;
            }
        }
        invalidate();
    }

    public void refreshSun() {
        RadialGradient gradient = new RadialGradient(px, py, (width >> 1) - ((float) width / 5), new int[]{getColor(), Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        sunPaint.setShader(gradient);
    }

    private int getColor() {
        return Color.rgb(yellow, yellow, gray);
    }

    public interface LightChangeListener {
        void onValueChanged(int progress);
    }

}
