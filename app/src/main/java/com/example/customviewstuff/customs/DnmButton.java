package com.example.customviewstuff.customs;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import com.example.customviewstuff.R;


/**
 * DynamicButton
 * Created by Wang.Wenhui
 * Date: 2020/2/21
 */
public class DnmButton extends AppCompatTextView {
    private Paint mPaint;
    private Path path;
    private RectF rectF, rect;
    private float[] radiusArray;
    private int lcDuration;
    private int clickColor, longClickColor;
    private ValueAnimator animator, fadeAnim;
    private Runnable lcRun = () -> {
        isLongClick = true;
        animator.cancel();
        animator.setFloatValues(0, 1);
        animator.setDuration(lcDuration);
        animator.start();
    };
    private float value, fraction;
    private float radius, shadowWidth;
    private int shadowColor;
    private PorterDuffXfermode xfermode;
    private LinearGradient shader;

    public DnmButton(@NonNull Context context) {
        this(context, null);
    }

    public DnmButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DnmButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        radiusArray = new float[8];
        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.DnmButton);
            radius = arr.getDimension(R.styleable.DnmButton_radius, 10);
            lcDuration = arr.getInt(R.styleable.DnmButton_longClickDuration, 700);
            clickColor = arr.getColor(R.styleable.DnmButton_clickColor, Color.LTGRAY);
            longClickColor = arr.getColor(R.styleable.DnmButton_longClickColor, Color.DKGRAY);
            shadowWidth = arr.getDimension(R.styleable.DnmButton_shadowWidth, 10);
            shadowColor = arr.getColor(R.styleable.DnmButton_shadowColor, Color.GRAY);
            arr.recycle();
        } else {
            lcDuration = 700;
            clickColor = Color.LTGRAY;
            longClickColor = Color.DKGRAY;
            shadowWidth = 10;
            shadowColor = Color.GRAY;
            radius = 10;
        }
        for (int i = 0; i < radiusArray.length; i++) {
            radiusArray[i] = radius;
        }
        path = new Path();
        rectF = new RectF();
        rect = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(animation -> {
            value = ((float) animation.getAnimatedValue());
            if (value == 1) {
                if (isLongClick) {
                    performLongClick();
                }
                fadeAnim.cancel();
                fadeAnim.start();
                value = 0;
            }
            invalidate();
        });
        fadeAnim = ValueAnimator.ofFloat(1, 0);
        fadeAnim.setDuration(500);
        fadeAnim.addUpdateListener(animation -> {
            fraction = (float) animation.getAnimatedValue();
            invalidate();
        });
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        shader = new LinearGradient(getMeasuredWidth(), getMeasuredHeight(), getMeasuredWidth() * 0.8f, 0, new int[]{shadowColor, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
    }

    private boolean isClick, isLongClick, inTouch;
    private float startX, startY;
    private long inTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                inTouch = true;
                isClick = true;
                startX = event.getX();
                startY = event.getY();
                inTime = System.currentTimeMillis();
                postDelayed(lcRun, 80);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - startX) > 10 || Math.abs(event.getY() - startY) > 10) {
                    isClick = false;
                    isLongClick = false;
                    removeCallbacks(lcRun);
                    animator.cancel();
                    if (value != 0) {
                        value = 0;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                inTouch = false;
                if (isClick && System.currentTimeMillis() - inTime < lcDuration) {
                    performClick();
                    isLongClick = false;
                    removeCallbacks(lcRun);
                    animator.setFloatValues(value, 1);
                    animator.cancel();
                    animator.setDuration((long) (360 - value * 360));
                    animator.start();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                inTouch = false;
                isLongClick = false;
                removeCallbacks(lcRun);
                animator.cancel();
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getBackground() == null) {
            canvas.drawColor(Color.GRAY);
        }
        drawAnim(canvas);
        super.onDraw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        rectF.set(shadowWidth, shadowWidth, getMeasuredWidth() - shadowWidth, getMeasuredHeight() - shadowWidth);
        drawShadow(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (radius > 0) {
                canvas.clipPath(path);
                super.draw(canvas);
            }
        } else {
            canvas.saveLayer(rectF, null, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            if (radius > 0) {
                drawBorder(canvas);
            }
        }

    }

    private void drawShadow(Canvas canvas) {
        path.reset();
        if (radius > 0) {
            path.addRoundRect(rectF, radiusArray, Path.Direction.CW);
        } else {
            path.addRect(rectF, Path.Direction.CW);
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(inTouch ? shadowWidth * 1.5f : shadowWidth);
        mPaint.setShader(shader);
        canvas.drawPath(path, mPaint);
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void drawAnim(Canvas canvas) {
        if (animator.isRunning()) {
            if (startX <= getMeasuredWidth() / 2) {
                rect.set(shadowWidth, shadowWidth, (float) getMeasuredWidth() * value - shadowWidth, getMeasuredHeight() - shadowWidth);
            } else {
                rect.set(getMeasuredWidth() - (float) getMeasuredWidth() * value + shadowWidth, shadowWidth, getMeasuredWidth() - shadowWidth, getMeasuredHeight() - shadowWidth);
            }
            if (isLongClick) {
                mPaint.setColor(longClickColor);
            } else {
                mPaint.setColor(clickColor);
            }
            canvas.drawRect(rect, mPaint);
        }
        if (fadeAnim.isRunning()) {
            rect.set(shadowWidth, shadowWidth, (float) getMeasuredWidth() - shadowWidth, getMeasuredHeight() - shadowWidth);
            if (isLongClick) {
                mPaint.setColor(longClickColor);
            } else {
                mPaint.setColor(clickColor);
            }
            mPaint.setAlpha((int) (255 * fraction));
            canvas.drawRect(rect, mPaint);
            mPaint.setAlpha(255);
        }
    }

    private void drawBorder(Canvas canvas) {
        mPaint.setXfermode(xfermode);
        path.reset();
        path.addRoundRect(rectF, radiusArray, Path.Direction.CCW);
        canvas.drawPath(path, mPaint);
        mPaint.setXfermode(null);
        canvas.restore();
    }

}
