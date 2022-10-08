package com.example.customviewstuff.starwar.interfaces;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author wangwenhui on 3/28/21
 */
public interface IDrawable {
  void draw(Canvas canvas, Paint paint);

  RectF getDrawRect();

  void initContainer(float width, float height);
}
