package com.example.customviewstuff.customs.love;


import java.util.Random;

import android.graphics.Rect;

/**
 * @author wangwenhui on 2022/11/15
 */
public class Heart {
  public static float touchX, touchY;

  private static final Random random = new Random();
  //变化速率，30~50
//  private final float inc = (float) (Math.PI / (50 + random.nextInt(20)));
  private final float inc = (float) (Math.PI / 50);
  //创建心的偏移量打散，默认20px
  private static final float rr = 20;
  //爱心旋转角度
  public final int rotate = random.nextInt(360);
  //爱心初始属性
  public float x, y, nowAngel, halfWidth = 12 + random.nextInt(6);
  //位移属性
  public float cx, cy, targetX, targetY;
  //移动速度rms恢复速度
  private final float ms, rms;
  //爱心绘制的rect
  public Rect rect = new Rect();

  public boolean disableScale = false;
  public int heartType;

  public Heart(float x, float y) {
    this.x = x;
    this.y = y;
    cx = targetX = x;
    cy = targetY = y;
    ms = 6 + 3 * random.nextFloat();
    rms = 10 + 8 * random.nextFloat();
  }

  public void go() {
    nowAngel += inc;
    if (nowAngel >= Math.PI * 2) {
      nowAngel = 0;
    }
    float scale = 0.1f + (float) Math.abs(Math.sin(nowAngel)) * 0.9f;
    if (disableScale) {
      scale = 1;
    }
    targetX = touchX == 0 ? x : touchX;
    targetY = touchY == 0 ? y : touchY;
    move();
    rect.set(
        (int) (cx - halfWidth * scale),
        (int) (cy - halfWidth * scale),
        (int) (cx + halfWidth * scale),
        (int) (cy + halfWidth * scale));
  }

  private void move() {
    float xx = targetX - cx;
    float yy = targetY - cy;
    if (Math.sqrt(xx * xx + yy * yy) < 18) {
      cx = targetX;
      cy = targetY;
      return;
    }
    double aa = Math.atan2(yy, xx);
    float speed = touchX == 0 ? rms : ms;
    float mx = (float) (speed * Math.cos(aa));
    float my = (float) (speed * Math.sin(aa));
    cx += mx;
    cy += my;
  }

  public static Heart create(float x, float y) {
    float hx = random.nextBoolean() ? x + random.nextFloat() * rr : x - random.nextFloat() * rr;
    float hy = random.nextBoolean() ? y + random.nextFloat() * rr : y - random.nextFloat() * rr;
    return new Heart(hx, hy);
  }

}
