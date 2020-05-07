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
}
