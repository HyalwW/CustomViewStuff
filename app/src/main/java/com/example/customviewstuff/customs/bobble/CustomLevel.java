package com.example.customviewstuff.customs.bobble;

import android.graphics.Path;
import android.graphics.PathEffect;

public class CustomLevel extends Level {
    private float[] center;
    private Path path;
    private boolean isReady;
    private int score;

    CustomLevel(int width, int height, float radius) {
        super(width, height, radius);
    }

    @Override
    protected int score() {
        return score;
    }

    @Override
    protected float[] shotPosition() {
        return center;
    }

    @Override
    protected void getPath(Path path) {
        path.reset();
        path.addPath(this.path);
    }

    @Override
    protected PathEffect getPathEffect() {
        return null;
    }

    @Override
    protected float moveIncrement() {
        return Math.min(width, height) * 0.001f;
    }

    public void setPath(Path p, float length) {
        score = (int) (length / 18);
        if (score < 50) {
            score = 50;
        }
        this.path = new Path();
        path.addPath(p);
        if (center != null) {
            isReady = true;
        }
    }

    public void setCenter(float x, float y) {
        center = new float[]{x, y};
        if (path != null) {
            isReady = true;
        }
    }

    public void reset() {
        center = null;
        path = null;
        isReady = false;
    }

    public boolean isReady() {
        return isReady;
    }
}
