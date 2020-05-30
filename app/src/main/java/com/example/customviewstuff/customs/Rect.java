package com.example.customviewstuff.customs;

import android.util.Log;

/**
 * 创建者：wyz
 * 创建时间：2020-05-19
 * 功能描述：
 * 更新者：
 * 更新时间：
 * 更新描述：
 */
public class Rect {

    private float PointX, pointY, radius;
    private int clolor;
    private double step;
    private int num = 1;
    private float radiusStart;
    private float startDelt = 30;

    public Rect(float pointX, float pointY, float radius, int clolor) {
        PointX = pointX;
        this.pointY = pointY;
        this.radius = radius;
        this.clolor = clolor;
        step = Math.PI / 30;
        radiusStart = radius;

    }

    public Rect() {
    }

    ;

    public float getPointX() {
        return PointX;
    }

    public void setPointX(float pointX) {
        PointX = pointX;
    }

    public float getPointY() {
        return pointY;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getClolor() {
        return clolor;
    }

    public void setClolor(int clolor) {
        this.clolor = clolor;
    }

    public float getStartDelt() {
        return startDelt;
    }

    public void setStartDelt(float startDelt) {
        this.startDelt += startDelt;
    }

    //todo 更新数据。
    public void updata() {
        num = num - 1;
        if (num < 0) {
            num = 30;
        }
        radius = (float) (radiusStart * (Math.sin((startDelt + num * step) % Math.PI)));
        if (radius < 2) {
            radius = 2;
        }
    }
}
