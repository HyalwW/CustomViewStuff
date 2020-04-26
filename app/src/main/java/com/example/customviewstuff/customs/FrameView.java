package com.example.customviewstuff.customs;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.customviewstuff.helpers.BitmapUtil;
import com.example.customviewstuff.helpers.GifDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/26
 */
public class FrameView extends BaseSurfaceView {
    private List<Bitmap> bitmaps;
    private int gap;
    private final String path = "timg.gif";
    private GifDecoder gifDecoder;
    private Rect src, dst;
    private float offset;

    public FrameView(Context context) {
        super(context);
    }

    public FrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        gifDecoder = new GifDecoder();
        src = new Rect();
        dst = new Rect();
        try {
            AssetManager assetManager = getResources().getAssets();
            InputStream stream = assetManager.open(path);
            gifDecoder.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("wwh", "FrameView --> onInit: decode gif failed, reason: " + e.getMessage());
        }
        bitmaps = new ArrayList<>();
        Log.e("wwh", "FrameView --> onInit: " + gifDecoder.getFrameCount());
        for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
            bitmaps.add(gifDecoder.getFrame(i));
        }
    }

    @Override
    protected void onReady() {
        if (bitmaps.get(0).getWidth() > getMeasuredWidth() || bitmaps.get(0).getHeight() > getMeasuredHeight()) {
            float sx = (float) getMeasuredWidth() / bitmaps.get(0).getWidth();
            float sy = (float) getMeasuredHeight() / bitmaps.get(0).getHeight();
            float s = Math.min(sx, sy);
            for (int i = 0; i < bitmaps.size(); i++) {
                Bitmap bitmap = bitmaps.get(i);
                Bitmap sb = BitmapUtil.scaleBitmap(bitmap, s);
                bitmaps.set(i, sb);
                bitmap.recycle();
            }
        }
        gap = bitmaps.get(0).getWidth() / 300;
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{gap * bitmaps.size(), gap}, 0));
        UPDATE_RATE = 100;
        startAnim();
    }


    @Override
    protected void onDataUpdate() {
        offset += gap;
        if (offset >= getMeasuredWidth()) {
            offset = 0;
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        int cx = getMeasuredWidth() >> 1, cy = getMeasuredHeight() >> 1;
        for (int i = 0; i < bitmaps.size(); i++) {
            Bitmap bitmap = bitmaps.get(i);
//            if (down) {
//                dst.set(cx - bitmap.getWidth() / 2, cy - bitmap.getHeight() / 2, cx + bitmap.getWidth() / 2, cy + bitmap.getHeight() / 2);
//                canvas.drawBitmap(bitmap, null, dst, mPaint);
//            }
            int l = cx - bitmap.getWidth() / 2 + i * gap, s = i * gap;
            while (s < bitmap.getWidth()) {
                src.set(s, 0, s + gap, bitmap.getHeight());
                dst.set(l, cy - bitmap.getHeight() / 2, l + gap, cy + bitmap.getHeight() / 2);
                canvas.drawBitmap(bitmap, src, dst, mPaint);
                l += gap * bitmaps.size();
                s += gap * bitmaps.size();
            }
        }
        canvas.translate(offset, 0);
        mPaint.setStrokeWidth(getMeasuredHeight());
        canvas.drawLine(-getMeasuredWidth(), getMeasuredHeight() >> 1, getMeasuredWidth(), getMeasuredHeight() >> 1, mPaint);
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

    private boolean down;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                down = false;
                break;
        }
        return true;
    }
}
