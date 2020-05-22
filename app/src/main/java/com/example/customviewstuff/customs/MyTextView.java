package com.example.customviewstuff.customs;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputFilter;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/21
 * Description: blablabla
 */
public class MyTextView extends AppCompatTextView implements View.OnClickListener {
    private Paint paint;
    private ValueAnimator animator;
    private double angle;
    private boolean moving, showAll = true;
    private float currentHeight;
    private float maxHeight, minHeight, lineLength;
    private float cx, cy;

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        setOnClickListener(this);
        animator = new ValueAnimator();
        animator.setFloatValues(0, 1);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            currentHeight = (float) animation.getAnimatedValue();
            setHeight((int) (currentHeight + 5));
            float fraction = animation.getAnimatedFraction();
            if (showAll) {
                angle = Math.PI / 3 + Math.PI / 3 * fraction;
                cy = (float) (getMeasuredHeight() - 5 - lineLength * Math.cos(Math.PI / 3) * (1 - fraction));
            } else {
                angle = Math.PI * 2 / 3 - fraction * Math.PI / 3;
                cy = (float) (getMeasuredHeight() - 5 - lineLength * Math.cos(Math.PI / 3) * fraction);
            }
            invalidate();
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                moving = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showAll = !showAll;
                moving = false;
                setMaxLines(showAll ? Integer.MAX_VALUE : 2);
                if (!showAll) {
                    int end = getLayout().getLineEnd(0);
                    Log.e("wwh", "MyTextView --> onAnimationEnd: " + end);
                    setFilters(new InputFilter[]{new InputFilter.LengthFilter(end * 2 - 6)});

                } else {
                    setFilters(new InputFilter[0]);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        lineLength = 24;
        angle = Math.PI / 4;
        setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        maxHeight = minHeight = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        cx = getMeasuredWidth() - lineLength - 2;
        cy = getMeasuredHeight() - lineLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maxHeight == 0) {
            Layout layout = getLayout();
            int row = getLineCount();
            int bottomPadding = layout.getBottomPadding();
            minHeight = Math.min(layout.getLineBottom(1) + bottomPadding, layout.getLineBottom(row - 1) + bottomPadding);
            maxHeight = layout.getHeight();
            call();
        }
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        canvas.drawLine(cx, cy, cx + ((float) (Math.sin(angle) * lineLength)), ((float) (cy + Math.cos(angle) * lineLength)), paint);
        canvas.drawLine(cx, cy, cx + ((float) (Math.sin(-angle) * lineLength)), ((float) (cy + Math.cos(-angle) * lineLength)), paint);
    }

    @Override
    public void onClick(View v) {
        call();
    }

    private void call() {
        if (!moving) {
            if (showAll) {
                animator.setFloatValues(maxHeight, minHeight);
            } else {
                animator.setFloatValues(minHeight, maxHeight);
            }
            animator.start();
        }
    }
}
