package com.example.customviewstuff.customs;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.R;

import java.util.Locale;

/**
 * Created by Wang.Wenhui
 * Date: 2020/9/3
 * Description: 滑块验证控件
 */
public class ImageVerifyView extends BaseSurfaceView {
    private Animator animator;
    private Bitmap bitmap;
    private float offset, targetOffset, targetHeight;
    private RectF bgRect, scrollRect, imgRect, shadowRect, pieceRect;
    private float scrollWidth, scrollHeight, imgHeight;
    private float height, width;
    private boolean success;
    private PorterDuffXfermode mode;

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
        shadowRect = new RectF();
        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
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
        scrollHeight = height * 0.08f;
        scrollWidth = width * 0.2f;
        imgHeight = height * 0.8f;
        targetOffset = (float) (width * 0.3f + Math.random() * (width - scrollWidth - width * 0.3));
        targetHeight = (float) (Math.random() * (imgHeight - scrollWidth));
        float bgl = 0, bgb = height * 0.95f, bgr = width, bgt = bgb - (scrollHeight * 0.7f);
        bgRect.set(bgl, bgt, bgr, bgb);
        imgRect.set(0, 0, width, imgHeight);
        shadowRect.set(targetOffset, targetHeight, targetOffset + scrollWidth, targetHeight + scrollWidth);
        float scy = bgt + (bgb - bgt) / 2f;
        scrollRect.set(bgl, scy - scrollHeight / 2f, bgl + scrollWidth, scy + scrollHeight / 2f);
        mPaint.setStrokeWidth(height * 0.01f);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(height * 0.06f);
        callDraw("");
    }

    @Override
    protected void onDataUpdate() {
        float bgl = 0, bgb = height * 0.95f, bgr = width, bgt = bgb - (scrollHeight * 0.7f);
        float scy = bgt + (bgb - bgt) / 2f;
        scrollRect.set(bgl + offset, scy - scrollHeight / 2f, bgl + scrollWidth + offset, scy + scrollHeight / 2f);
    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        canvas.drawColor(Color.WHITE);
        drawImage(canvas);
        drawShadow(canvas);
        drawPiece(canvas);
        drawScroll(canvas);
        if (success) {
            drawSuccess(canvas);
        }
    }

    private void drawImage(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, imgRect, null);
    }

    private void drawShadow(Canvas canvas) {
        mPaint.setColor(0xCCCCCCCC);
        canvas.drawRect(shadowRect, mPaint);
    }

    private void drawPiece(Canvas canvas) {
        pieceRect.set(offset, targetHeight, offset + scrollWidth, targetHeight + scrollWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(pieceRect, mPaint);
        mPaint.setStyle(Paint.Style.FILL);

        int layer = canvas.saveLayer(imgRect.left, imgRect.top, imgRect.right, imgRect.bottom, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.translate(offset - targetOffset, 0);
        pieceRect.set(targetOffset, targetHeight, targetOffset + scrollWidth, targetHeight + scrollWidth);
        canvas.drawRect(pieceRect, mPaint);
        mPaint.setXfermode(mode);
        canvas.drawBitmap(bitmap, null, imgRect, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layer);
    }

    private void drawScroll(Canvas canvas) {
        float radius = height * 0.1f;
        mPaint.setColor(Color.GRAY);
        canvas.drawRoundRect(bgRect, radius, radius, mPaint);
        radius = height * 0.14f;
        mPaint.setColor(Color.GREEN);
        canvas.drawRoundRect(scrollRect, radius, radius, mPaint);
    }

    private void drawSuccess(Canvas canvas) {
        canvas.drawColor(0xDD888888);
        mPaint.setColor(Color.GREEN);
        canvas.drawText("验证通过，用时 " + String.format("%.1f", successTime / 1000f) + " S ~", width / 2, height / 2, mPaint);
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    private boolean canScroll;
    private float downX;
    private long startTime, successTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (success) return super.onTouchEvent(event);
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (scrollRect.contains(eventX, eventY)) {
                    canScroll = true;
                    startTime = System.currentTimeMillis();
                    downX = eventX;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (canScroll) {
                    offset = eventX - downX;
                    offset = Math.max(0, offset);
                    offset = Math.min(width - scrollWidth, offset);
                    callDraw("");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (canScroll) {
                    if (!verify()) {
                        canScroll = false;
                        offset = 0;
                        startTime = 0;
                    }
                    callDraw("");
                }
                break;
        }
        return true;
    }

    private boolean verify() {
        successTime = System.currentTimeMillis() - startTime;
        success = Math.abs(targetOffset - offset) < width * 0.02;
        if (success) offset = targetOffset;
        return success;
    }
}
