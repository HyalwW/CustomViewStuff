package com.example.customviewstuff.bitmapViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.example.customviewstuff.customs.BaseSurfaceView;

public class PdfView extends BaseSurfaceView {
    private Bitmap img;
    private float baseScale, scale;
    private float cx, cy;

    private float xInc, yInc, sub;
    private VelocityTracker tracker;

    private Matrix matrix;
    private RectF dstRect;

    public PdfView(Context context) {
        super(context);
    }

    public PdfView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PdfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        dstRect = new RectF();
        tracker = VelocityTracker.obtain();
        matrix = new Matrix();
    }

    @Override
    protected void onReady() {
        sub = Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.001f;
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
                cx += xInc;
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
                cy += yInc;
            } else {
                yInc = 0;
            }
            setMatrix();
            if (xInc == 0 && yInc == 0) {
                checkBounds();
            }
        }
    }

    private void setMatrix() {
        matrix.reset();
        matrix.preScale(scale, scale);
        matrix.postTranslate(cx, cy);
    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        if (img != null && !img.isRecycled()) {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(img, matrix, null);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = img == null ? 1 :(int) (img.getHeight() * scale);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
        if (img != null) {
            callDraw("");
        }
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
            matrix.reset();
            callDraw("");
        }
        post(this::requestLayout);
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
                    temp.set(cx, cy);
                    xInc = yInc = 0;
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
                        tracker.addMovement(event);
                        cx = temp.x + (event.getX(0) - d1x);
                        cy = temp.y + (event.getY(0) - d1y);
                        callDraw("");
                    }
                } else if (event.getPointerCount() > 1) {
                    //两指距离
                    float nLength = distance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    //获取图片缩放增量
                    float scaleIncrement = (nLength - blength) / (Math.min(getMeasuredWidth(), getMeasuredHeight()));
                    scale = tempScale + scaleIncrement;
//                    sx = (event.getX(0) + event.getX(1)) / 2;
//                    sy = (event.getY(0) + event.getY(1)) / 2;
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
        dstRect.set(0, 0, img.getWidth(), img.getHeight());
        matrix.mapRect(dstRect);
        if (dstRect.left < 0 && dstRect.right < getMeasuredWidth()) {
            cx += Math.min(-dstRect.left, getMeasuredWidth() - dstRect.right);
        } else if (dstRect.left > 0 && dstRect.right > getMeasuredWidth()) {
            cx -= Math.min(dstRect.left, dstRect.right - getMeasuredWidth());
        }
        if (dstRect.top < 0 && dstRect.bottom < getMeasuredHeight()) {
            cy += Math.min(-dstRect.top, getMeasuredHeight() - dstRect.bottom);
        } else if (dstRect.top > 0 && dstRect.bottom > getMeasuredHeight()) {
            cy -= Math.min(dstRect.top, dstRect.bottom - getMeasuredHeight());
        }
        setMatrix();
    }

    private void parentMove(boolean move) {
        getParent().requestDisallowInterceptTouchEvent(!move);
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

}
