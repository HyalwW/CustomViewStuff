package com.example.customviewstuff.customs.widgets;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.example.customviewstuff.R;


/**
 * Created by Wang.Wenhui
 * Date: 2020/1/9
 */
public class RegulateView extends View {
    private Paint mPaint;
    private Bitmap regulator;
    private RectF bgRect;
    private Path bgPath;
    private float[] radiusArray;
    //0~100
    private int progress;
    private int minVal = 0, maxVal = 100, value;
    private ValueAnimator animator;
    private String title = "";
    private float downY, offsetY;
    private boolean isChanging, isShowing;
    private RectF dst, changingDst;
    private Rect src;
    private ValueChangeListener listener;
    private int cr = 96;
    private int cb = 164;
    private int cg = 244;

    public RegulateView(Context context) {
        this(context, null);
    }

    public RegulateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegulateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RegulateView);
            minVal = array.getInteger(R.styleable.RegulateView_minValue, 0);
            maxVal = array.getInteger(R.styleable.RegulateView_maxValue, 100);
            title = array.getString(R.styleable.RegulateView_title);
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
            array.recycle();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setTextAlign(Paint.Align.CENTER);

        regulator = BitmapFactory.decodeResource(getResources(), R.drawable.up_down);

        bgRect = new RectF();
        bgPath = new Path();
        radiusArray = new float[8];
        for (int i = 0; i < radiusArray.length; i++) {
//            radiusArray[i] = getResources().getDimension(R.dimen.qb_px_8);
            radiusArray[i] = 4f;
        }

        dst = new RectF();
        changingDst = new RectF();
        src = new Rect();

        animator = new ValueAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        animator.setFloatValues(0.0f, 1.0f);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            float cx = (float) (getMeasuredWidth() * 0.25), cy = getMeasuredHeight() >> 1;
            float w = ((float) (getMeasuredWidth() * 0.15 * value)), h = (float) (getMeasuredWidth() * 0.3 * value);
            dst.set(cx - w, cy - h, cx + w, cy + h);
            postInvalidate();
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isShowing = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isShowing = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height < 3.5 * width ? (int) (width * 3.5) : height);
    }

    private boolean isClick;
    private int proTemp;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                downY = event.getY();
                isClick = true;
                offsetY = 0;
                animator.cancel();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                if (Math.abs(moveY - downY) > 5 && isClick) {
                    downY = event.getY();
                    isClick = false;
                    isChanging = true;
                    animator.start();
                    proTemp = progress;
                }
                if (!isClick) {
                    offsetY = moveY - downY;
                    int offset = (int) (offsetY * 1.5 / getMeasuredHeight() * 100);
                    progress = proTemp - offset;
                    if (progress > 100) {
                        progress = 100;
                    }
                    if (progress < 0) {
                        progress = 0;
                    }
                    postInvalidate();
                    int newVal = genValue();
                    if (value != newVal) {
                        value = newVal;
                        if (listener != null) {
                            listener.onValueChanged(value);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isClick) {
                    performClick();
                } else {
                    isChanging = false;
                    isShowing = false;
                    if (animator.isRunning()) {
                        animator.cancel();
                    }
                    postInvalidate();
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        super.onDraw(canvas);
        drawText(canvas);
        if (isChanging) {
            drawBitmap(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        bgRect.set(0, 0, getWidth(), getHeight());
        bgPath.reset();
        bgPath.addRoundRect(bgRect, radiusArray, Path.Direction.CW);
        this.cr = (244 - 96) * progress / 100 + 96;
        this.cg = 164;
        this.cb = 244 - (244 - 96) * progress / 100;
        mPaint.setColor(Color.rgb(cr, cg, cb));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas.save();
            canvas.clipPath(bgPath);
            canvas.restore();
        } else {
            canvas.saveLayer(bgRect, null, Canvas.ALL_SAVE_FLAG);
            canvas.drawPath(bgPath, mPaint);
            canvas.restore();
        }
    }

    private void drawText(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
//        mPaint.setTextSize(getResources().getDimension(R.dimen.qb_px_18));
        mPaint.setTextSize(9f);
        mPaint.setFakeBoldText(false);
        canvas.drawText(title, getMeasuredWidth() >> 1, (float) (getMeasuredHeight() * 0.2), mPaint);
        mPaint.setColor(Color.rgb(255 - cr, 255 - cg, 255 - cb));
//        mPaint.setTextSize(getResources().getDimension(R.dimen.qb_px_30));
        mPaint.setTextSize(15f);
        mPaint.setFakeBoldText(true);
        canvas.drawText(String.valueOf(genValue()), getMeasuredWidth() >> 1, (float) (getMeasuredHeight() * 0.88), mPaint);
    }

    private void drawBitmap(Canvas canvas) {
        if (isShowing || offsetY == 0) {
            canvas.drawBitmap(regulator, null, dst, mPaint);
        } else {
            float offset = offsetY / 10;
            if (offsetY > 0) {
                src.set(0, regulator.getHeight() >> 1, regulator.getWidth(), regulator.getHeight());
                changingDst.set(dst.left, dst.top + (dst.bottom - dst.top) / 2 + offset, dst.right, dst.bottom + offset);
                canvas.drawBitmap(regulator, src, changingDst, mPaint);
            } else {
                src.set(0, 0, regulator.getWidth(), regulator.getHeight() >> 1);
                changingDst.set(dst.left, dst.top + offset, dst.right, dst.bottom - (dst.bottom - dst.top) / 2 + offset);
                canvas.drawBitmap(regulator, src, changingDst, mPaint);
            }
        }
    }

    private int genValue() {
        return Math.round((float) progress / 100 * (maxVal - minVal) + minVal);
    }

    /**
     * @param progress value from minVal~maxVal
     */
    public void setProgress(int progress) {
        if (isChanging) {
            return;
        }
        this.progress = Math.round(((float) progress - minVal) / (maxVal - minVal) * 100);
        this.value = progress;
        postInvalidate();
    }

    public void setRange(int minVal, int maxVal) {
        this.minVal = minVal;
        this.maxVal = maxVal;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setListener(ValueChangeListener listener) {
        this.listener = listener;
    }

    public int getValue() {
        return value;
    }

    public interface ValueChangeListener {
        void onValueChanged(int value);
    }

}
