package com.example.customviewstuff.starwar.interfaces;

/**
 * 子弹接口
 *
 * @author wangwenhui on 3/28/21
 */
public interface IBullet extends IDrawable {
  /**
   * 伤害，是否需要血条？
   */
  int damage();

  /**
   * 是否蓄力，蓄力时长
   */
  default long delay() {
    return 0;
  }

  /**
   * 蓄力效果
   */
  default IBlastEffect delayEffect() {
    return null;
  }

  /**
   * 是不是敌人反射的子弹
   */
  boolean isFromEnemy();

  void move(long time);

  boolean isFlyAway();

}
