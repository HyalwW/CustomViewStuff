package com.example.customviewstuff.customs.soccerGame;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/7
 */
public class Ball {
    public float x, y;
    public float icmX, icmY;
    public float subX, subY;

    public Ball() {
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void hit(float speed, float sub, double direction) {
        icmX = (float) (speed * Math.sin(direction));
        icmY = (float) (speed * Math.cos(direction));
        subX = (float) Math.abs((sub * Math.sin(direction)));
        subY = (float) Math.abs((sub * Math.cos(direction)));
    }

    public void copy(Ball ball) {
        this.x = ball.x;
        this.y = ball.y;
        this.icmX = ball.icmX;
        this.icmY = ball.icmY;
        this.subX = ball.subX;
        this.subY = ball.subY;
    }

    public void copyResize(Ball ball, int width, int height) {
        this.x = ball.x * width;
        this.y = ball.y * height;
        this.icmX = ball.icmX * width;
        this.icmY = ball.icmY * height;
        this.subX = ball.subX * width;
        this.subY = ball.subY * height;
    }

    public void transform(int width, int height) {
        x = (width - x) / width;
        y = (height - y) / height;
    }

    public void reset(float x, float y) {
        this.x = x;
        this.y = y;
        icmX = 0;
        icmY = 0;
        subX = 0;
        subY = 0;
    }
}
