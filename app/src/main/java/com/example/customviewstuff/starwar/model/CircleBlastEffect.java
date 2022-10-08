package com.example.customviewstuff.starwar.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.SparseArray;

import com.example.customviewstuff.starwar.interfaces.IBlastEffect;
import com.example.customviewstuff.starwar.interfaces.IJet;

/**
 * 碎片爆炸效果
 *
 * @author wangwenhui on 3/29/21
 */
public class CircleBlastEffect implements IBlastEffect {

  private final IJet mJet;
  private float mWidth, mHeight;
  private final Bitmap mBitmap;
  //将图片分割出的颜色数量，将图片切割成多少行列，颜色跨度误差倍数，重复个数（总圆个数 = colorSize * reuseCount）
  private final int colorSize = 15, bitmapGap = 16, colorGap = 30, reuseCount = 2;
  private final int[] colors = new int[colorSize];
  private final SparseArray<Integer> queue = new SparseArray<>();
  private final List<Circle> list = new ArrayList<>();
  private long mMoveTime;
  private int mAlpha;

  public CircleBlastEffect(IJet jet, Bitmap bitmap) {
    mBitmap = bitmap;
    mJet = jet;
    analyze();
  }

  private void analyze() {
    int width = mBitmap.getWidth(), height = mBitmap.getHeight();
    int raiseW = width / bitmapGap, raiseH = height / bitmapGap;
    int colorG = (Color.TRANSPARENT - Color.BLACK) / colorGap;
    for (int i = 0; i < width; i += raiseW) {
      for (int j = 0; j < height; j += raiseH) {
        boolean use = false;
        int color = mBitmap.getPixel(i, j);
        for (int index = 0; index < queue.size(); index++) {
          if (color > queue.keyAt(index) - colorG && color < queue.keyAt(index) + colorG) {
            queue.setValueAt(index, queue.valueAt(index) + 1);
            use = true;
            break;
          }
        }
        if (!use) {
          queue.put(color, 1);
        }
      }
    }
    int index = 0;
    while (index < colorSize) {
      int color = 0, count = 0;
      for (int i = 0; i < queue.size(); i++) {
        if (queue.valueAt(i) > count) {
          count = queue.valueAt(i);
          color = queue.keyAt(i);
        }
      }
      queue.remove(color);
      colors[index++] = color;
    }
    for (int color : colors) {
      if (color == 0) {
        color = Color.WHITE;
      }
      for (int i = 0; i < reuseCount; i++) {
        RectF jetDrawRect = mJet.getDrawRect();
        list.add(new Circle(
            color,
            (jetDrawRect.left + jetDrawRect.right) * 0.5f,
            (jetDrawRect.top + jetDrawRect.bottom) * 0.5f));
      }
    }
  }

  @Override
  public long duration() {
    return 1000;
  }

  @Override
  public void move(long time) {
    mMoveTime += time;
    mAlpha = (int) (255 * (1 - (float) mMoveTime / duration()));
    for (Circle circle : list) {
      circle.move(((float) mMoveTime / duration()) * 2, 500);
    }
  }

  @Override
  public boolean isBlastDone() {
    return mMoveTime >= duration();
  }

  @Override
  public void draw(Canvas canvas, Paint paint) {
    for (Circle circle : list) {
      paint.setColor(circle.color);
      paint.setAlpha(mAlpha);
      canvas.drawCircle(circle.nx, circle.ny, circle.radius * mAlpha / 255, paint);
    }
  }

  @Override
  public RectF getDrawRect() {
    return null;
  }

  @Override
  public void initContainer(float width, float height) {
    mWidth = width;
    mHeight = height;
  }

  private class Circle {
    int color;
    float xSpeed, ySpeed;
    float x, y;
    float radius;
    float nx, ny;

    Circle(int color, float x, float y) {
      this.color = color;
      this.x = x;
      this.y = y;
      reset();
    }

    /**
     * @param time 时间：秒
     * @param g    重力加速度
     */
    void move(float time, float g) {
      nx = x + xSpeed * time;
      ny = y - (ySpeed * time - g * time * time / 2);
    }

    void reset() {
      radius = randomRadius();
      xSpeed = randomXSpeed();
      ySpeed = randomYSpeed();
    }

    private float randomRadius() {
      return (float) (Math.random() * 3 + 5);
    }

    private float randomXSpeed() {
      return (float) (-140 + Math.random() * 280);
    }

    private float randomYSpeed() {
      return (float) (40 + Math.random() * 200);
    }
  }
}
