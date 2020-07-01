package com.example.customviewstuff.customs;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.R;

/**
 * Created by Wang.Wenhui
 * Date: 2020/7/1
 * Description: blablabla
 */
public class ScaleConstLayout extends ConstraintLayout {
    private ValueAnimator animator;
    private boolean isOut;
    private float scale;
    private static final float minScale = 0.95f;

    private float[] radiusArray;
    private Path path;
    private RectF rectF;
    private Paint mPaint;

    public ScaleConstLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ScaleConstLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScaleConstLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        animator = new ValueAnimator();
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            scale = (float) animation.getAnimatedValue();
            invalidate();
        });
        scale = 1;
        path = new Path();
        rectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        if (attrs != null) {
            radiusArray = new float[8];
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ScaleConstLayout);
            float r = arr.getDimension(R.styleable.ScaleConstLayout_layout_radius, 0);
            if (r != 0) {
                for (int i = 0; i < radiusArray.length; i++) {
                    radiusArray[i] = r;
                }
            }
            radiusArray[0] = arr.getDimension(R.styleable.ScaleConstLayout_radius_lt, r);
            radiusArray[1] = radiusArray[0];
            radiusArray[2] = arr.getDimension(R.styleable.ScaleConstLayout_radius_rt, r);
            radiusArray[3] = radiusArray[2];
            radiusArray[4] = arr.getDimension(R.styleable.ScaleConstLayout_radius_rb, r);
            radiusArray[5] = radiusArray[4];
            radiusArray[6] = arr.getDimension(R.styleable.ScaleConstLayout_radius_lb, r);
            radiusArray[7] = radiusArray[6];
            arr.recycle();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (scale != 0) {
            canvas.scale(scale, scale, getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
        }
        rectF.set(0, 0, getWidth(), getHeight());
        path.reset();
        path.addRoundRect(rectF, radiusArray, Path.Direction.CW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas.save();
            canvas.clipPath(path);
            super.draw(canvas);
            canvas.restore();
        } else {
            canvas.saveLayer(rectF, null, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            canvas.drawPath(path, mPaint);
            canvas.restore();
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        rectF.set(0, 0, getWidth(), getHeight());
        path.reset();
        path.addRoundRect(rectF, radiusArray, Path.Direction.CW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas.save();
            canvas.clipPath(path);
            super.dispatchDraw(canvas);
            canvas.restore();
        } else {
            canvas.saveLayer(rectF, null, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            canvas.drawPath(path, mPaint);
            canvas.restore();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float ex = event.getX(), ey = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOut = false;
                zoomOut();
                break;
            case MotionEvent.ACTION_MOVE:
                if (ex < 0 || ex > getMeasuredWidth() || ey < 0 || ey > getMeasuredHeight()) {
                    zoomIn();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                zoomIn();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void zoomIn() {
        if (scale != 1 && !isOut) {
            isOut = true;
            animator.cancel();
            animator.setFloatValues(scale, 1f);
            animator.start();
        }
    }

    private void zoomOut() {
        animator.cancel();
        animator.setFloatValues(scale, minScale);
        animator.start();
    }
}
