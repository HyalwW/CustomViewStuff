package com.example.customviewstuff.customs.soccerGame;

import android.graphics.Color;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/7
 */
public class Player {
    public float x, y;
    public int score, color, angle;
    public boolean isLeasure, holdBall;
    public double direction;
    private Thread runThread;

    public Player() {
        color = randomColor();
    }

    public void reset(float x, float y) {
        angle = 0;
        isLeasure = true;
        score = 0;
        this.x = x;
        this.y = y;
        direction = 0;
    }

    public void running() {
        isLeasure = false;
        runThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isLeasure) {
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    angle += 5;
                    if (angle >= 360) {
                        angle = 0;
                    }
                }
            }
        };
        runThread.start();
    }

    public void stopRun() {
        isLeasure = true;
    }

    public void holdBall(boolean hold) {
        holdBall = hold;
    }

    public void setPos(float x, float y) {
        direction = Math.PI / 2 - Math.atan2(y - this.y, x - this.x);
        this.y = y;
        this.x = x;
    }

    private int randomColor() {
        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }
}
