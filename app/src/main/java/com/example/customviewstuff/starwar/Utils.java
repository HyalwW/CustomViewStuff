package com.example.customviewstuff.starwar;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @author wangwenhui on 3/28/21
 */
public class Utils {
  public static boolean isOverlap(Rect r1, Rect r2) {
    return r1.contains(r2.left, r2.top) || r1.contains(r2.left, r2.bottom) || r1.contains(r2.right, r2.top) || r1.contains(r2.right, r2.bottom) ||
        r2.contains(r1.left, r1.top) || r2.contains(r1.left, r1.bottom) || r2.contains(r1.right, r1.top) || r2.contains(r1.right, r1.bottom);
  }

  public static boolean isOverlap(RectF r1, RectF r2) {
    return r1.contains(r2.left, r2.top) || r1.contains(r2.left, r2.bottom) || r1.contains(r2.right, r2.top) || r1.contains(r2.right, r2.bottom) ||
        r2.contains(r1.left, r1.top) || r2.contains(r1.left, r1.bottom) || r2.contains(r1.right, r1.top) || r2.contains(r1.right, r1.bottom);
  }
}
