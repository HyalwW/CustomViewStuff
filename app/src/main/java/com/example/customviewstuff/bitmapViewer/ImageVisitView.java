package com.example.customviewstuff.bitmapViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.example.customviewstuff.customs.BaseSurfaceView;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/24
 * Description: blablabla
 */
public class ImageVisitView extends BaseSurfaceView {
    private Bitmap img;
    private RectF dstRect;
    private float baseScale, scale;
    private PointF center;

    private float xInc, yInc, sub;
    private VelocityTracker tracker;

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
        dstRect = new RectF();
        center = new PointF();
        tracker = VelocityTracker.obtain();
    }

    @Override
    protected void onReady() {
        if (img != null) {
            setBitmap(img, false);
        }
    }

    @Override
    protected void onDataUpdate() {
        if (img != null) {
            if (xInc != 0) {
                if (xInc > 0) {
                    xInc -= sub;
                    if (xInc < 0) {
                        xInc = 0;
                    }
                } else {
                    xInc += sub;
                    if (xInc > 0) {
                        xInc = 0;
                    }
                }
                center.x += xInc;
            }
            if (yInc != 0) {
                if (yInc > 0) {
                    yInc -= sub;
                    if (yInc < 0) {
                        yInc = 0;
                    }
                } else {
                    yInc += sub;
                    if (yInc > 0) {
                        yInc = 0;
                    }
                }
                center.y += yInc;
            } else {
                yInc = 0;
            }
            setRect();
            if (xInc == 0 && yInc == 0) {
                checkBounds();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = img == null ? 1 : (int) (img.getHeight() * scale);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (img != null) {
            center.set(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
            sub = Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.001f;
            callDraw("");
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        if (img != null && !img.isRecycled()) {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(img, null, dstRect, null);
            if (xInc != 0 || yInc != 0) {
                callDraw("");
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
            scale = baseScale = (float) getMeasuredWidth() / img.getWidth();
            center.set(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
            callDraw("");
            post(this::requestLayout);
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
    private boolean doubleTouch, onTouch;
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
                onTouch = true;
                if (id == 0) {
                    d1x = event.getX(0);
                    d1y = event.getY(0);
                    temp.set(center);
                    xInc = yInc = 0;
                } else if (id == 1 && event.getPointerCount() > 1) {
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
                        tracker.addMovement(event);
                        center.set(temp.x + (event.getX(0) - d1x), temp.y + (event.getY(0) - d1y));
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
                    callDraw("");
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (!doubleTouch) {
                    tracker.computeCurrentVelocity((int) UPDATE_RATE, getMeasuredWidth() * 0.04f);
                    xInc = tracker.getXVelocity();
                    yInc = tracker.getYVelocity();
                }
                if (scale < baseScale) {
                    scale = baseScale;
                }
                callDraw("");
                if (event.getPointerCount() == 1) {
                    d1x = d1y = d2x = d2y = 0;
                    doubleTouch = false;
                    onTouch = false;
                    tracker.clear();
                }
                break;
        }
        return true;
    }

    private void checkBounds() {
        if (dstRect.left < 0 && dstRect.right < getMeasuredWidth()) {
            center.x += Math.min(-dstRect.left, getMeasuredWidth() - dstRect.right);
        } else if (dstRect.left > 0 && dstRect.right > getMeasuredWidth()) {
            center.x -= Math.min(dstRect.left, dstRect.right - getMeasuredWidth());
        }
        if (dstRect.top < 0 && dstRect.bottom < getMeasuredHeight()) {
            center.y += Math.min(-dstRect.top, getMeasuredHeight() - dstRect.bottom);
        } else if (dstRect.top > 0 && dstRect.bottom > getMeasuredHeight()) {
            center.y -= Math.min(dstRect.top, dstRect.bottom - getMeasuredHeight());
        }
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
        dstRect.set(center.x - fx, center.y - fy, center.x + fx, center.y + fy);
    }
}
