package com.example.customviewstuff.customs;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;

import com.example.customviewstuff.R;

/**
 * Created by Wang.Wenhui
 * Date: 2020/3/6
 * 待改进
 */
public class CartAnimView extends BaseSurfaceView {
    private Bitmap goods, cart;
    private Path mPath;
    private PathMeasure measure;
    private ValueAnimator animator;
    private Rect gDst, cDst;

    public CartAnimView(Context context) {
        super(context);
    }

    public CartAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CartAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        goods = BitmapFactory.decodeResource(getResources(), R.drawable.goods);
        cart = BitmapFactory.decodeResource(getResources(), R.drawable.cart_flow);
        mPath = new Path();
        measure = new PathMeasure();
        animator = new ValueAnimator();
        animator.setFloatValues(0, 1);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            if (fraction < 1) {
                float bpSize = goods.getWidth() * (1 - fraction) / 2;
                float[] pos = new float[2];
                measure.getPosTan(measure.getLength() * fraction, pos, null);
                gDst.set(((int) (pos[0] - bpSize)), ((int) (pos[1] - bpSize)), ((int) (pos[0] + bpSize)), (int) (pos[1] + bpSize));
                int lx = (getMeasuredWidth() - cart.getWidth()) / 2, ty = getMeasuredHeight() - cart.getHeight();
                cDst.set(lx, ty, lx + cart.getWidth(), ty + cart.getHeight());
                callDraw("draw");
            } else {
                callDrawDelay("clear", 200);
            }
        });
        gDst = new Rect();
        cDst = new Rect();
    }

    @Override
    protected void onReady() {

    }

    @Override
    protected void onDataUpdate() {

    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (data instanceof String) {
            switch (((String) data)) {
                case "draw":
                    canvas.drawBitmap(goods, null, gDst, mPaint);
                    canvas.drawBitmap(cart, null, cDst, mPaint);
                    break;
                case "clear":
                    break;
            }
        }
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    public void set(Pos start, Pos end) {
        mPath.reset();
        mPath.moveTo(start.x, start.y);
        mPath.quadTo(end.x, start.y, end.x, end.y);
        measure.setPath(mPath, false);
        animator.cancel();
        animator.start();
    }

    public static class Pos {
        float x, y;

        public Pos(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
