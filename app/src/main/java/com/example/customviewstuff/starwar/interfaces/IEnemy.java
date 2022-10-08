package com.example.customviewstuff.starwar.interfaces;

import android.graphics.Path;

/**
 * @author wangwenhui on 3/28/21
 */
public interface IEnemy extends IJet {
  /**
   * 敌机飞行路线
   */
  Path flyPath();

  /**
   * 顶部飞到底部时长
   */
  long duration();

  boolean isFlyAway();

  int score();
}
