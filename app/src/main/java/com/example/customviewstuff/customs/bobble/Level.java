package com.example.customviewstuff.customs.bobble;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/11
 * Description: blablabla
 */
public abstract class Level {
    protected int width, height;
    protected float radius;

    Level(int width, int height, float radius) {
        this.width = width;
        this.height = height;
        this.radius = radius;
    }

    protected abstract void getPath(Path path);


    static Level level(int l, int width, int height, float radius) {
        switch (l) {
            case 1:
                return new LevelOne(width, height, radius);
            case 2:
                return new LevelTwo(width, height, radius);
            default:
                return new LevelOne(width, height, radius);
        }
    }


    static class LevelOne extends Level {
        LevelOne(int width, int height, float radius) {
            super(width, height, radius);
        }

        @Override
        protected void getPath(Path path) {
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
    }

    private static class LevelTwo extends Level {
        LevelTwo(int width, int height, float radius) {
            super(width, height, radius);
        }

        @Override
        protected void getPath(Path path) {
        }
    }
}
