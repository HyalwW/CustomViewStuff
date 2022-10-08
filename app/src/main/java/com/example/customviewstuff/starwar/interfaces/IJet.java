package com.example.customviewstuff.starwar.interfaces;

import android.graphics.RectF;

/**
 * @author wangwenhui on 3/28/21
 */
public interface IJet extends IDrawable {
  /**
   * 移动
   */
  void move(long time);

  /**
   * 发射的子弹
   */
  IBullet shootBullet();

  /**
   * 被击毁的效果
   */
  IBlastEffect destroyedEffect();

  /**
   * 现存血量
   */
  int bloodValue();

  /**
   * 被击中
   */
  void beHit(int damage);

  boolean isDestroyed();

  void release();
}
