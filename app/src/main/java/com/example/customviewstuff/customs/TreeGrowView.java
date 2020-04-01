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
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/1
 */
public class TreeGrowView extends BaseSurfaceView {
    private PathMeasure measure;
    private long duration = 30;
    private static final int leaveDepth = 10;
    private double pi = Math.PI;
    private Random random;
    private List<TreePath> paths;
    private boolean isDestroy;
    private Path path, src, drawPath;

    public TreeGrowView(Context context) {
        super(context);
    }

    public TreeGrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TreeGrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        measure = new PathMeasure();
        random = new Random();

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        paths = new CopyOnWriteArrayList<>();

        path = new Path();
        src = new Path();
        drawPath = new Path();
    }

    @Override
    protected void onReady() {
        float maxLen = getMeasuredHeight() / 7f;
        float maxWid = getMeasuredWidth() / 50f;
        if (paths.size() > 0) {
            paths.clear();
        }
        isDestroy = false;
        TreeNode root = new TreeNode(1, null);
        root.set(new PointF(getMeasuredWidth() >> 1, getMeasuredHeight()), (float) (-pi / 2), maxLen, maxWid);
        doInThread(() -> draw(root));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        isDestroy = true;
        paths.clear();
    }

    private void draw(TreeNode node) {
        if (node.time == 0) {
            while (node.time < duration && !isDestroy) {
                node.time += 16;
                src.reset();
                node.getPath(src);
                mPaint.setStrokeWidth(node.width);
                callDraw(src);
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            paths.add(new TreePath(node, node.width));
            if (node.depth == leaveDepth) {
                int color = randomColor();
                TreeNode left = new TreeNode(node.depth + 1, node);
                left.set(node.end, randomAngle(node.angle, true), node.length * 0.8f, node.width * 0.8f);
                paths.add(new TreePath(left, left.width * 3, color));
                TreeNode right = new TreeNode(node.depth + 1, node);
                right.set(node.end, randomAngle(node.angle, false), node.length * 0.8f, node.width * 0.8f);
                paths.add(new TreePath(right, right.width * 3, color));
                draw(node.parent);
                return;
            }
        }
//        float childLength = node.length * 0.8f + node.length * 0.2f * random.nextFloat();
        float childLength = node.length * 0.9f;
        if (node.left == null) {
            TreeNode left = new TreeNode(node.depth + 1, node);
            left.set(node.end, randomAngle(node.angle, true), childLength, node.width * 0.8f);
            node.left = left;
            draw(node.left);
        } else if (node.right == null) {
            TreeNode right = new TreeNode(node.depth + 1, node);
            right.set(node.end, randomAngle(node.angle, false), childLength, node.width * 0.8f);
            node.right = right;
            draw(node.right);
        } else if (node.parent != null) {
            draw(node.parent);
        } else {
            Log.e("wwh", "TreeGrowView --> draw: done");
        }
    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private float randomAngle(float parentAngle, boolean isLeft) {
        return isLeft ? (float) (parentAngle - (pi / 16 + random.nextFloat() * pi / 8))
                : (float) (parentAngle + (pi / 16 + random.nextFloat() * pi / 8));
    }


    @Override
    protected void onDataUpdate() {

    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        if (data instanceof String) {
            canvas.drawColor(Color.WHITE);
        } else if (data instanceof Path) {
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath((Path) data, mPaint);
            for (TreePath path : paths) {
                drawPath.reset();
                drawPath.moveTo(path.sx, path.sy);
                drawPath.lineTo(path.ex, path.ey);
                if (path.color != 0) {
                    mPaint.setColor(path.color);
                } else {
                    mPaint.setColor(Color.WHITE);
                }
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(path.width);
                canvas.drawPath(drawPath, mPaint);
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

    private class TreeNode {
        int depth;
        long time;
        TreeNode parent, left, right;
        PointF start, end;
        float angle;
        private float length, width;

        TreeNode(int depth, TreeNode parent) {
            this.depth = depth;
            this.parent = parent;
            time = 0;
        }

        void set(PointF start, float angle, float length, float width) {
            this.start = start;
            this.angle = angle;
            this.length = length;
            this.width = width;
            this.end = new PointF(((float) (length * Math.cos(angle) + start.x)), (float) (length * Math.sin(angle) + start.y));
        }

        void getPath(Path src) {
            path.reset();
            path.moveTo(start.x, start.y);
            path.lineTo(end.x, end.y);
            measure.setPath(path, false);
            float stopD = ((float) time / duration) * measure.getLength();
            measure.getSegment(0, stopD, src, true);
        }
    }

    private class TreePath {
        float sx, sy, ex, ey;
        float width;
        int color;

        TreePath(TreeNode node, float width) {
            sx = node.start.x;
            sy = node.start.y;
            ex = node.end.x;
            ey = node.end.y;
            this.width = width;
        }

        TreePath(TreeNode node, float width, int color) {
            this(node, width);
            this.color = color;
        }
    }

}
