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

    Level(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected abstract void getPath(Path path);


    static Level level(int l, int width, int height) {
        switch (l) {
            case 1:
                return new LevelOne(width, height);
            case 2:
                return new LevelTwo(width, height);
            default:
                return new LevelOne(width, height);
        }
    }


    static class LevelOne extends Level {
        LevelOne(int width, int height) {
            super(width, height);
        }

        @Override
        protected void getPath(Path path) {
            RectF oval = new RectF();
            float bx, by;
            //todo adasdasd
            path.addArc(oval,0, 180);
        }
    }

    private static class LevelTwo extends Level {
        LevelTwo(int width, int height) {
            super(width, height);
        }

        @Override
        protected void getPath(Path path) {
        }
    }
}
