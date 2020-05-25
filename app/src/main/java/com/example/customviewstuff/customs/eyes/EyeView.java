package com.example.customviewstuff.customs.eyes;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import com.example.customviewstuff.customs.BaseSurfaceView;

import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/25
 * Description: blablabla
 */
public class EyeView extends BaseSurfaceView {
    private float bx, by;
    //0~360
    private int rotateAngle;
    private Eye showEye;
    private NormalEye normalEye;
    private Path drawPath, eyePath;
    private ValueAnimator animator;

    private boolean blink;
    private long blinkTime, blinkDuration = 960;
    private float maxHeight, width;
    private Random random;
    private Eye[] eyes;

    public EyeView(Context context) {
        super(context);
    }

    public EyeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EyeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        drawPath = new Path();
        eyePath = new Path();
        random = new Random();
        animator = new ValueAnimator();
        animator.setDuration(1200);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setFloatValues(30, 0);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            rotateAngle += value;
        });
    }

    @Override
    protected void onReady() {
        bx = getMeasuredWidth() >> 1;
        by = getMeasuredHeight() >> 1;
        width = getMeasuredWidth() * 0.8f;
        maxHeight = width * 0.6f;
        normalEye = new NormalEye(bx, by, getMeasuredWidth(), getMeasuredHeight());
        showEye = normalEye;
        Eye triEye = new TriEye(bx, by, getMeasuredWidth(), getMeasuredHeight());
        eyes = new Eye[]{normalEye, triEye};
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        if (blink) {
            if (blinkTime < blinkDuration) {
                blinkTime += UPDATE_RATE;
            } else {
                blinkTime = 0;
                blink = false;
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        mPaint.setXfermode(null);
        canvas.drawColor(Color.WHITE);
        drawEye(canvas);
        drawBorder(canvas);
    }

    private void drawEye(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotateAngle, bx, by);
        showEye.draw(canvas, mPaint);
        canvas.restore();
    }

    private void drawBorder(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.DKGRAY);
        drawPath.reset();
        drawPath.addRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), Path.Direction.CW);
        eyePath.reset();
        float hw = width / 2;
        eyePath.moveTo(bx - hw, by);
        float height = maxHeight;
        if (blink) {
            height = (float) (maxHeight - maxHeight * Math.sin(((float) blinkTime / blinkDuration) * Math.PI));
            if (height == 0 && eyes.length > 1) {
                showEye = eyes[random.nextInt(eyes.length)];
                rotateAngle = 0;
                animator.cancel();
                animator.start();
            }
        }
        eyePath.quadTo(bx, by - height, bx + hw, by);
        eyePath.quadTo(bx, by + height, bx - hw, by);
        drawPath.op(eyePath, Path.Op.DIFFERENCE);
        canvas.drawPath(drawPath, mPaint);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            blink();
        }
        return true;
    }

    void blink() {
        if (!blink) {
            blink = true;
        }
    }

}
