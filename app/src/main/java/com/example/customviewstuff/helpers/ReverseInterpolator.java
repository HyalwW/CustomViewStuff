package com.example.customviewstuff.helpers;

import android.animation.TimeInterpolator;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/13
 * Description:摆针回弹效果插值器
 */
public class ReverseInterpolator implements TimeInterpolator {
    private float reverseHeight, reverseIndex;
    private int reverseCount;

    public ReverseInterpolator() {
        this(0.7f, 0.3f, 2);
    }

    /**
     * 构造一个摆针回弹效果插值器
     *
     * @param reverseHeight 回弹的最大振幅
     * @param reverseIndex  开始回弹的位置，（0,1]之间,为1时等同于LinearInterpolator
     * @param reverseCount  回弹的次数
     */
    public ReverseInterpolator(float reverseHeight, float reverseIndex, int reverseCount) {
        if (reverseIndex <= 0 || reverseIndex > 1) {
            throw new IllegalStateException("reverseIndex must within (0 : exclusive,1 : inclusive)");
        }
        this.reverseHeight = reverseHeight;
        this.reverseIndex = reverseIndex;
        this.reverseCount = reverseCount;
    }

    @Override
    public float getInterpolation(float input) {
        float value, h;
        if (input <= reverseIndex) {
            value = input / reverseIndex;
        } else {
            h = reverseHeight / (1 - reverseIndex) * (1 - input);
            value = (float) (1 + Math.sin((input - reverseIndex) * reverseCount * Math.PI / (1 - reverseIndex)) * h);
        }
        return value;
    }
}
