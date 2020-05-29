package com.example.customviewstuff.customs.eyes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.customviewstuff.customs.BaseSurfaceView;
import com.example.customviewstuff.helpers.RotateHelper;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/25
 * Description: blablabla
 */
public class EyeView extends BaseSurfaceView implements View.OnClickListener {
    private float bx, by;
    //0~360
    private int rotateAngle, maxAngleIncrement;
    private Eye showEye;
    private Path drawPath, eyePath;

    private boolean blink;
    private long blinkTime, blinkDuration = 960;
    private long rotateTime = 2000, rotateDuration = 1600;
    private float maxHeight, width;
    private RotateHelper helper;

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
        maxAngleIncrement = 20;
        setOnClickListener(this);
    }

    @Override
    protected void onReady() {
        bx = getMeasuredWidth() >> 1;
        by = getMeasuredHeight() >> 1;
        width = getMeasuredWidth() * 0.8f;
        maxHeight = width * 0.6f;
        showEye = new NormalEye(bx, by, getMeasuredWidth(), getMeasuredHeight());
        helper = new RotateHelper(this, true, getMeasuredWidth() * 0.12f, true);
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
        if (rotateTime < rotateDuration) {
            rotateTime += UPDATE_RATE;
            int ai = (int) (Math.sin(((float) rotateTime / rotateDuration) * Math.PI) * maxAngleIncrement);
            rotateAngle += ai;
            if (rotateAngle % 360 == 0) {
                rotateAngle = 0;
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        mPaint.setXfermode(null);
        canvas.drawColor(Color.WHITE);
        canvas.save();
        helper.cameraRotate(canvas);
        drawEye(canvas);
        canvas.restore();
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
            if (height == 0) {
                showEye = showEye.next();
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

    void blink() {
        if (!blink) {
            blink = true;
            rotateTime = 0;
        }
    }

    @Override
    public void onClick(View v) {
        Log.e("wwh", "EyeView-->onClick(): " );
        blink();
    }
}
