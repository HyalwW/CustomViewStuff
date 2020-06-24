package com.example.customviewstuff.bitmapViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.customs.BaseSurfaceView;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/24
 * Description: blablabla
 */
public class ImageVisitView extends BaseSurfaceView {
    private Bitmap img;
    private RectF imgRect;
    private float baseScale, scale;
    private PointF center;

    public ImageVisitView(Context context) {
        super(context);
    }

    public ImageVisitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageVisitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        imgRect = new RectF();
        center = new PointF();
    }

    @Override
    protected void onReady() {
        if (img != null) {
            setBitmap(img, false);
        }
    }

    @Override
    protected void onDataUpdate() {

    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        if (img != null && !img.isRecycled()) {
            canvas.drawBitmap(img, null, imgRect, mPaint);
        }
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    public void setBitmap(final int id, final boolean recycle) {
        doInThread(() -> setBitmap(BitmapFactory.decodeResource(getResources(), id), recycle), true);
    }

    public void setBitmap(Bitmap bitmap, boolean recycle) {
        if (recycle) {
            if (img != null) {
                img.recycle();
            }
        }
        img = Bitmap.createBitmap(bitmap);
        if (isAlive) {
            if (img.getHeight() > img.getWidth()) {
                scale = baseScale = (float) getMeasuredHeight() / img.getHeight();
            } else {
                scale = baseScale = (float) getMeasuredWidth() / img.getWidth();
            }
            center.set(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
            setRect();
            callDraw("");
        }
    }

    public void recycle() {
        if (img != null && !img.isRecycled()) {
            img.recycle();
        }
        img = null;
    }

    private float d1x, d1y, d2x, d2y;
    private float blength;
    private boolean doubleTouch;
    private PointF temp = new PointF();
    private float tempScale;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (img == null) {
            return super.onTouchEvent(event);
        }
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (id == 0) {
                    d1x = event.getX(0);
                    d1y = event.getY(0);
                    temp.set(center);
                } else if (id == 1) {
                    d2x = event.getX(1);
                    d2y = event.getY(1);
                    blength = distance(d1x, d1y, d2x, d2y);
                    tempScale = scale;
                    doubleTouch = true;
                }
                parentMove(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!doubleTouch) {
                    if (scale == baseScale) {
                        parentMove(true);
                    } else {
                        center.set(temp.x + (event.getX(0) - d1x), temp.y + (event.getY(0) - d1y));
                        setRect();
                        callDraw("");
                    }
                } else if (event.getPointerCount() > 1) {
                    //两指距离
                    float nLength = distance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    //获取图片缩放增量
                    float scaleIncrement = (nLength - blength) / (Math.min(getMeasuredWidth(), getMeasuredHeight()));
                    scale = tempScale + scaleIncrement;
                    float vx = (event.getX(0) + event.getX(1)) / 2;
                    float vy = (event.getY(0) + event.getY(1)) / 2;
                    //两指中点到bitmap中点连线的角度
                    double angle = Math.atan2(temp.y - vy, temp.x - vx);
                    //两指中点到bitmap中点位置距离
                    float lLength = distance(temp.x, temp.y, vx, vy);
                    //重新获取新的bitmap中点位置
                    center.set(temp.x + ((float) (Math.cos(angle) * scaleIncrement * lLength)), temp.y + (float) (Math.sin(angle) * scaleIncrement * lLength));
                    setRect();
                    callDraw("");
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (scale < baseScale) {
                    scale = baseScale;
                }
                checkBounds();
                if (event.getPointerCount() == 1) {
                    d1x = d1y = d2x = d2y = 0;
                    doubleTouch = false;
                }
                break;
        }
        return true;
    }

    private void checkBounds() {
        if (imgRect.left < 0 && imgRect.right < getMeasuredWidth()) {
            center.x += Math.min(-imgRect.left, getMeasuredWidth() - imgRect.right);
        } else if (imgRect.left > 0 && imgRect.right > getMeasuredWidth()) {
            center.x -= Math.min(imgRect.left, imgRect.right - getMeasuredWidth());
        }
        if (imgRect.top < 0 && imgRect.bottom < getMeasuredHeight()) {
            center.y += Math.min(-imgRect.top, getMeasuredHeight() - imgRect.bottom);
        } else if (imgRect.top > 0 && imgRect.bottom > getMeasuredHeight()) {
            center.y -= Math.min(imgRect.top, imgRect.bottom - getMeasuredHeight());
        }
        setRect();
        callDraw("");
    }

    private void parentMove(boolean move) {
        getParent().requestDisallowInterceptTouchEvent(!move);
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private void setRect() {
        float fx = img.getWidth() * scale / 2;
        float fy = img.getHeight() * scale / 2;
        imgRect.set(center.x - fx, center.y - fy, center.x + fx, center.y + fy);
    }
}
