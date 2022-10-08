package com.example.customviewstuff.starwar.interfaces;

/**
 * 爆炸效果
 *
 * @author wangwenhui on 3/28/21
 */
public interface IBlastEffect extends IDrawable {
  /**
   * 爆炸动画时长
   */
  long duration();

  void move(long time);

  boolean isBlastDone();
}
