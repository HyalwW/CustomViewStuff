package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.example.customviewstuff.Pool;
import com.example.customviewstuff.Reusable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class HexaView extends BaseSurfaceView {
    private static final float pi = (float) Math.PI;
    private static final float baseAngle = (float) (Math.PI / 3);
    private Random random;
    private float borderLength;
    private PathMeasure measure;
    private Path drawPath;
    private List<Route> routes;
    private boolean draw;
    private Pool<Route> pool;

    public HexaView(Context context) {
        super(context);
    }

    public HexaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HexaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        drawPath = new Path();
        measure = new PathMeasure();
        random = new Random();
        routes = new CopyOnWriteArrayList<>();
        pool = new Pool<>(Route::new);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onReady() {
        borderLength = getMeasuredWidth() * 0.08f;
        draw = true;
        mPaint.setStrokeWidth(getMeasuredWidth() * 0.02f);
        startAnim();
        doInThread(() -> {
            while (draw) {
                routes.add(pool.get());
                sleep(200);
            }
        });
    }

    @Override
    protected void onDataUpdate() {
//        Collections.shuffle(routes, random);
        for (Route route : routes) {
            if (route.pos > route.total) {
                routes.remove(route);
            } else {
                route.move();
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (Route route : routes) {
            drawPath.reset();
            measure.setPath(route.mPath, false);
            float start = route.pos;
            measure.getSegment(start < 0 ? 0 : start, start + route.length, drawPath, true);
            mPaint.setColor(route.color);
            canvas.drawPath(drawPath, mPaint);
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

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        draw = false;
    }

    private class Route implements Reusable {
        Path mPath;
        float increment, pos, length, total;
        int color;

        Route() {
            reset();
        }

        @Override
        public void reset() {
            mPath = getPath();
            increment = randomIncrement();
            this.length = randomLength();
            this.pos = -length;
            color = randomColor();
        }

        @Override
        public boolean isLeisure() {
            return pos > total;
        }

        void move() {
            pos += increment;
        }

        private Path getPath() {
            total = 0;
            Path p = new Path();
            PointF point = new PointF(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
            float angle = -pi + baseAngle * 2 * random.nextInt(3);
            p.moveTo(point.x, point.y);
            while (point.x > 0 && point.x < getMeasuredWidth() && point.y > 0 && point.y < getMeasuredHeight()) {
                float x = (float) (borderLength * Math.cos(angle) + point.x);
                float y = (float) (borderLength * Math.sin(angle) + point.y);
                total += borderLength;
                p.lineTo(x, y);
                point.set(x, y);
                angle += baseAngle - (random.nextInt(2) * baseAngle * 2);
            }
            return p;
        }
    }

    private float randomIncrement() {
        return getMeasuredWidth() * 0.005f + random.nextFloat() * getMeasuredWidth() * 0.005f;
    }

    private float randomLength() {
        return getMeasuredWidth() * 0.2f + random.nextFloat() * getMeasuredWidth() * 0.8f;
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

}
