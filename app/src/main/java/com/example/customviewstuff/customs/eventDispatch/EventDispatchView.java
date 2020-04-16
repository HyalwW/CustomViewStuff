package com.example.customviewstuff.customs.eventDispatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.customs.BaseSurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/16
 * Description:展示事件分发的view
 */
public class EventDispatchView extends BaseSurfaceView {
    private Path eventPath, drawPath;
    private PathMeasure measure;
    private List<Container> containers;
    public static final String AD = "activity Dispatch";
    public static final String AO = "activity OnTouch";
    public static final String VGD = "viewGroup Dispatch";
    public static final String VGI = "viewGroup Intercept";
    public static final String VGO = "viewGroup OnTouch";
    public static final String VD = "view Dispatch";
    public static final String VO = "view OnTouch";
    private Rect drawRect, moveRect;
    private String action;
    private long time, duration = 2000;
    private float hw, hh;

    public EventDispatchView(Context context) {
        super(context);
    }

    public EventDispatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventDispatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        eventPath = new Path();
        drawPath = new Path();
        measure = new PathMeasure();
        containers = new ArrayList<>();
        mPaint.setTextAlign(Paint.Align.CENTER);
        drawRect = new Rect();
        moveRect = new Rect();
        action = "";
    }

    @Override
    protected void onReady() {
        if (containers.size() == 0) {
            Container aDispatch = new Container(AD, getMeasuredWidth() * 0.2f, getMeasuredHeight() * 0.2f);
            containers.add(aDispatch);
            Container aOnTouch = new Container(AO, getMeasuredWidth() * 0.8f, getMeasuredHeight() * 0.2f);
            containers.add(aOnTouch);
            Container vgDispatch = new Container(VGD, getMeasuredWidth() * 0.2f, getMeasuredHeight() * 0.5f);
            containers.add(vgDispatch);
            Container vgIntercept = new Container(VGI, getMeasuredWidth() * 0.5f, getMeasuredHeight() * 0.65f);
            containers.add(vgIntercept);
            Container vgOnTouch = new Container(VGO, getMeasuredWidth() * 0.8f, getMeasuredHeight() * 0.5f);
            containers.add(vgOnTouch);
            Container vDispatch = new Container(VD, getMeasuredWidth() * 0.2f, getMeasuredHeight() * 0.8f);
            containers.add(vDispatch);
            Container vOnTouch = new Container(VO, getMeasuredWidth() * 0.8f, getMeasuredHeight() * 0.8f);
            containers.add(vOnTouch);
        }
        hw = getMeasuredWidth() * 0.14f;
        hh = getMeasuredWidth() * 0.05f;
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        if (time >= duration) {
            time = 0;
        } else {
            time += 16;
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        mPaint.setTextSize(getMeasuredWidth() * 0.05f);
        mPaint.setColor(Color.RED);
        canvas.drawText(action, getMeasuredWidth() >> 1, mPaint.getTextSize() + 10, mPaint);
        drawPath.reset();
        measure.setPath(eventPath, false);
        measure.getSegment(0, (float) time / duration * measure.getLength(), drawPath, true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getMeasuredWidth() * 0.01f);
        mPaint.setColor(Color.GREEN);
        canvas.drawPath(drawPath, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        for (Container container : containers) {
            boolean save = false;
            if (moveContainer && moveIndex == containers.indexOf(container)) {
                canvas.save();
                save = true;
                canvas.scale(1.2f, 1.2f, container.x, container.y);
            }
            drawRect.set(((int) (container.x - hw)), ((int) (container.y - hh)), ((int) (container.x + hw)), (int) (container.y + hh));
            mPaint.setColor(container.onEvent ? Color.YELLOW : Color.RED);
            canvas.drawRect(drawRect, mPaint);
            mPaint.setTextSize(getMeasuredWidth() * 0.03f);
            mPaint.setColor(Color.BLUE);
            canvas.drawText(container.name, drawRect.left + hw, drawRect.bottom - hh + mPaint.getTextSize() / 2, mPaint);
            mPaint.setTextSize(getMeasuredWidth() * 0.02f);
            mPaint.setColor(Color.BLACK);
            canvas.drawText(container.returnType, drawRect.left + hw, drawRect.bottom - hh - mPaint.getTextSize(), mPaint);
            if (save) {
                canvas.restore();
            }
        }
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

    public void resetType(String name, ReturnType type) {
        reset();
        for (Container container : containers) {
            if (container.name.equals(name)) {
                switch (type) {
                    case TRUE:
                        container.returnType = "true";
                        break;
                    case FALSE:
                        container.returnType = "false";
                        break;
                    case SUPER:
                        container.returnType = "super";
                        break;
                }
                return;
            }
        }
    }

    private class Container {
        String name, returnType;
        boolean onEvent;
        float x, y;

        Container(String name, float x, float y) {
            this.name = name;
            this.x = x;
            this.y = y;
            returnType = "super";
            reset();
        }

        void reset() {
            onEvent = false;
        }

        public Rect getRect() {
            moveRect.set((int) (x - hw), (int) (y - hh), (int) (x + hw), (int) (y + hh));
            return moveRect;
        }
    }

    public void reset() {
        for (Container container : containers) {
            container.reset();
        }
        eventPath.reset();
        action = "";
        time = 0;
    }

    public void action(String action) {
        reset();
        this.action = action;
    }

    public void active(String name) {
        for (Container container : containers) {
            if (container.name.equals(name)) {
                if (eventPath.isEmpty()) {
                    eventPath.moveTo(container.x, container.y);
                } else {
                    eventPath.lineTo(container.x, container.y);
                }
                container.onEvent = true;
                return;
            }
        }
    }

    private float sx, sy;
    private boolean moveContainer;
    private int moveIndex;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveContainer = false;
                sx = event.getX();
                sy = event.getY();
                for (Container container : containers) {
                    if (container.getRect().contains((int) sx, (int) sy)) {
                        moveContainer = true;
                        moveIndex = containers.indexOf(container);
                        reset();
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveContainer) {
                    Container container = containers.get(moveIndex);
                    container.x += event.getX() - sx;
                    container.y += event.getY() - sy;
                    sx = event.getX();
                    sy = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (moveContainer) {
                    Container container = containers.get(moveIndex);
                    if (container.x < hw) {
                        container.x = hw;
                    }
                    if (container.x > getMeasuredWidth() - hw) {
                        container.x = getMeasuredWidth() - hw;
                    }
                    if (container.y > getMeasuredHeight() - hh) {
                        container.y = getMeasuredHeight() - hh;
                    }
                    if (container.y < hh) {
                        container.y = hh;
                    }
                    moveContainer = false;
                }
                break;
        }
        return true;
    }
}
