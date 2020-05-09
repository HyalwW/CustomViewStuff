package com.example.customviewstuff.customs.soccerGame;

import android.graphics.Color;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/7
 */
public class Player {
    public float x, y;
    public int score, color, angle;
    public boolean isLeasure;
    public double direction;
    private transient Thread runThread;

    public Player() {
        color = randomColor();
        reset(0, 0);
    }

    public void reset(float x, float y) {
        angle = 0;
        isLeasure = true;
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


    public void setPos(float x, float y) {
        direction = Math.PI / 2 - Math.atan2(y - this.y, x - this.x);
        this.y = y;
        this.x = x;
    }

    private int randomColor() {
        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }

    public void copy(Player player) {
        this.x = player.x;
        this.y = player.y;
        this.direction = player.direction;
        this.isLeasure = player.isLeasure;
        this.score = player.score;
        this.color = player.color;
        this.angle = player.angle;
    }

    public void copyResize(Player player, int width, int height) {
        this.x = player.x * width;
        this.y = player.y * height;
        this.direction = player.direction;
        this.score = player.score;
        this.color = player.color;
        this.angle = player.angle;
    }

    public void transform(int width, int height) {
        x = (width - x) / width;
        y = (height - y) / height;
        direction += Math.PI;
        angle = 360 - angle;
    }

    public void goal() {
        score++;
        stopRun();
    }
}
