package com.example.customviewstuff.customs;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.R;

/**
 * Created by Wang.Wenhui
 * Date: 2020/9/3
 * Description: blablabla
 */
public class ImageVerifyView extends BaseSurfaceView {
    private Animator animator;
    private Bitmap bitmap;
    private float offset;
    private RectF bgRect, scrollRect, imgRect, pieceRect;
    private float scrollWidth, scrollHeight;
    private float height, width;

    public ImageVerifyView(Context context) {
        super(context);
    }

    public ImageVerifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageVerifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gm);
        bgRect = new RectF();
        scrollRect = new RectF();
        imgRect = new RectF();
        pieceRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int ws = MeasureSpec.getSize(widthMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(ws, hm));
    }

    @Override
    protected void onReady() {
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        bgRect.set(width * 0.05f, height * 0.85f, width * 0.95f, height * 0.95f);
        imgRect.set(0, 0, width, height * 0.8f);
        scrollHeight = height * 0.08f;
        scrollWidth = width * 0.2f;
        scrollRect.set(width * 0.05f, height * 0.83f, width * 0.05f + scrollWidth, height * 0.97f);
        callDraw("");
    }

    @Override
    protected void onDataUpdate() {

    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        canvas.drawColor(Color.WHITE);
        drawImage(canvas);
        drawScroll(canvas);
    }

    private void drawImage(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, imgRect, null);
    }

    private void drawScroll(Canvas canvas) {
        float radius = height * 0.1f;
        mPaint.setColor(Color.GRAY);
        canvas.drawRoundRect(bgRect, radius, radius, mPaint);
        radius = height * 0.14f;
        mPaint.setColor(Color.GREEN);
        canvas.drawRoundRect(scrollRect, radius, radius, mPaint);
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
        return true;
    }
}
