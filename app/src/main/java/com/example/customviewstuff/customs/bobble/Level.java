package com.example.customviewstuff.customs.bobble;

import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RectF;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/11
 * Description: 关卡
 */
public abstract class Level {
    protected int width, height;
    protected float radius;
    protected PathEffect defEffect;

    Level(int width, int height, float radius) {
        this.width = width;
        this.height = height;
        this.radius = radius;
        Path shape = new Path();
        float min = Math.min(width, height);
        shape.moveTo(0, -min * 0.01f);
        shape.lineTo(min * 0.01f,  0);
        shape.lineTo(0, min * 0.01f);
        shape.close();
        defEffect = new PathDashPathEffect(shape, min * 0.015f, 0, PathDashPathEffect.Style.MORPH);
    }

    //目标分数
    protected abstract int score();

    //射击球的初始位置
    protected abstract float[] shotPosition();

    //小球移动路径
    protected abstract void getPath(Path path);

    //小球路径纹理
    protected abstract PathEffect getPathEffect();

    //移动速度
    protected abstract float moveIncrement();


    static Level level(int l, int width, int height, float radius) {
        switch (l) {
            case 1:
                return new LevelOne(width, height, radius);
            case 2:
                return new LevelTwo(width, height, radius);
            case 3:
                return new LevelThree(width, height, radius);
            case 4:
                return new LevelFour(width, height, radius);
            case 5:
                return new CustomLevel(width, height, radius);
            default:
                return new LevelOne(width, height, radius);
        }
    }


    static class LevelOne extends Level {
        LevelOne(int width, int height, float radius) {
            super(width, height, radius);
        }

        @Override
        protected int score() {
            return 200;
        }

        @Override
        protected float[] shotPosition() {
            return new float[]{width >> 1, height >> 1};
        }

        @Override
        protected void getPath(Path path) {
            path.reset();
            path.moveTo(width * 0.1f, height * 0.1f);
            path.lineTo(width * 0.85f, height * 0.1f);
            path.quadTo(width * 0.9f, height * 0.1f, width * 0.9f, height * 0.1f + width * 0.05f);
            path.lineTo(width * 0.9f, height * 0.9f - width * 0.05f);
            path.quadTo(width * 0.9f, height * 0.9f, width * 0.85f, height * 0.9f);
            path.lineTo(width * 0.15f, height * 0.9f);
            path.quadTo(width * 0.1f, height * 0.9f, width * 0.1f, height * 0.9f - width * 0.05f);
            path.lineTo(width * 0.1f, height * 0.1f + width * 0.1f);
        }

        @Override
        protected PathEffect getPathEffect() {
            return defEffect;
        }

        @Override
        protected float moveIncrement() {
            return Math.min(width, height) * 0.001f;
        }
    }

    private static class LevelTwo extends Level {
        LevelTwo(int width, int height, float radius) {
            super(width, height, radius);
        }

        @Override
        protected int score() {
            return 250;
        }

        @Override
        protected float[] shotPosition() {
            return new float[]{width * 0.1f, height * 0.5f};
        }

        @Override
        protected void getPath(Path path) {
            path.reset();
            RectF oval = new RectF();
            float radius = width * 0.1f;
            path.moveTo(width * 0.9f, height * 0.2f);
            path.lineTo(width * 0.9f, height * 0.9f);
            oval.set(width * 0.7f, height * 0.9f - radius, width * 0.9f, height * 0.9f + radius);
            path.arcTo(oval, 0, 180);
            path.lineTo(width * 0.7f, height * 0.2f);
            oval.set(width * 0.5f, height * 0.2f - radius, width * 0.7f, height * 0.2f + radius);
            path.arcTo(oval, 0, -180);
            path.lineTo(width * 0.5f, height * 0.9f);
            oval.set(width * 0.3f, height * 0.9f - radius, width * 0.5f, height * 0.9f + radius);
            path.arcTo(oval, 0, 180);
            path.lineTo(width * 0.3f, height * 0.2f);
        }

        @Override
        protected PathEffect getPathEffect() {
            return defEffect;
        }

        @Override
        protected float moveIncrement() {
            return Math.min(width, height) * 0.001f;
        }
    }

    private static class LevelThree extends Level {

        LevelThree(int width, int height, float radius) {
            super(width, height, radius);
        }

        @Override
        protected int score() {
            return 500;
        }

        @Override
        protected float[] shotPosition() {
            return new float[]{width >> 1, height >> 1};
        }

        @Override
        protected void getPath(Path path) {
            path.reset();
            RectF oval = new RectF();
            float xi = (width - radius * 2) / 6f, yi = (height * 0.8f - radius * 2) / 6f, cx = width >> 1, cy = height >> 1;
            path.moveTo(cx + 3 * xi, cy);
            oval.set(cx - 3 * xi, cy - 3 * yi, cx + 3 * xi, cy + 3 * yi);
            path.arcTo(oval, 0, 180);

            oval.set(cx - 3 * xi, cy - 3 * yi, cx + 2 * xi, cy + 2 * yi);
            path.arcTo(oval, -180, 180);

            oval.set(cx - 2 * xi, cy - 2 * yi, cx + 2 * xi, cy + 2 * yi);
            path.arcTo(oval, 0, 180);

            oval.set(cx - 2 * xi, cy - 2 * yi, cx + xi, cy + yi);
            path.arcTo(oval, -180, 180);

            oval.set(cx - xi, cy - yi, cx + xi, cy + yi);
            path.arcTo(oval, 0, 270);
        }

        @Override
        protected PathEffect getPathEffect() {
            return defEffect;
        }

        @Override
        protected float moveIncrement() {
            return Math.min(width, height) * 0.0012f;
        }
    }

    private static class LevelFour extends Level {

        LevelFour(int width, int height, float radius) {
            super(width, height, radius);
        }

        @Override
        protected int score() {
            return 300;
        }

        @Override
        protected float[] shotPosition() {
            return new float[]{width >> 1, height * 0.9f};
        }

        @Override
        protected void getPath(Path path) {
            RectF oval = new RectF();
            float radius = width * 0.1f;
            path.reset();
            path.moveTo(width * 0.8f, radius * 2);
            path.lineTo(width * 0.2f, radius * 2);
            oval.set(width * 0.1f, radius * 2, width * 0.3f, radius * 4f);
            path.arcTo(oval, 270, -180);
            path.lineTo(width * 0.8f, radius * 4f);
            oval.set(width * 0.7f, radius * 4f, width * 0.9f, radius * 6f);
            path.arcTo(oval, -90, 180);
            path.lineTo(width * 0.2f, radius * 6f);
            oval.set(width * 0.1f, radius * 6f, width * 0.3f, radius * 8f);
            path.arcTo(oval, 270, -180);
            path.lineTo(width * 0.8f, radius * 8f);
            oval.set(width * 0.7f, radius * 8f, width * 0.9f, radius * 10f);
            path.arcTo(oval, -90, 180);
            path.lineTo(width * 0.1f, radius * 10f);
        }

        @Override
        protected PathEffect getPathEffect() {
            return defEffect;
        }

        @Override
        protected float moveIncrement() {
            return Math.min(width, height) * 0.0007f;
        }
    }
}
