package com.example.customviewstuff.helpers;

import android.view.animation.Interpolator;

/**
 * Created by Wang.Wenhui
 * Date: 2020/2/20
 */
public class ShakeInterpolator implements Interpolator {
    private float maxAngle;

    /**
     * 构造一个摆针回弹效果插值器
     *
     * @param maxAngle 最大角度
     */
    public ShakeInterpolator(float maxAngle) {
        this.maxAngle = maxAngle;
    }

    @Override
    public float getInterpolation(float input) {
        float value;
        float angle = maxAngle * (1 - input);
        value = (float) (Math.sin((1 - input) * 16 * Math.PI)) * angle;
        return value;
    }
}
