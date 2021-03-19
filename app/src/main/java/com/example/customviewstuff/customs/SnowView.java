package com.example.customviewstuff.customs;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.Pool;
import com.example.customviewstuff.Reusable;

/**
 * @author wangwenhui on 3/19/21
 */
public class SnowView extends BaseSurfaceView {
  private PathMeasure pathMeasure;
  private Pool<Snow> snowPool;
  private final List<Snow> snows = new CopyOnWriteArrayList<>();
  private Matrix matrix = new Matrix();
  private float wind, maxWind;
  private boolean isTouch;

  public SnowView(Context context) {
    super(context);
  }

  public SnowView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onInit() {
    pathMeasure = new PathMeasure();
    snowPool = new Pool<>(Snow::new);
  }

  @Override
  protected void onReady() {
    float min = Math.min(mWidth, mHeight) * 0.008f;
    R1 = min;
    R2 = min * 2f;
    R3 = min * 3f;
    R4 = min * 4f;
    R5 = min * 5f;
    gap = Math.min(mWidth, mHeight) * 0.15f;
    startAnim();
    doInThread(() -> {
      while (running) {
        try {
          snows.add(snowPool.get());
          Thread.sleep(16);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override
  protected void onDataUpdate() {
    float offset = mWidth * 0.001f;
    if (wind < maxWind) {
      wind += offset;
      if (wind > maxWind) {
        wind = maxWind;
      }
    } else if (wind > maxWind) {
      wind -= offset;
      if (wind < maxWind) {
        wind = maxWind;
      }
    }
    for (Snow snow : snows) {
      snow.move();
    }
  }

  @Override
  protected void onRefresh(Canvas canvas) {
    for (Snow snow : snows) {
      snow.draw(canvas);
    }
  }

  @Override
  protected void draw(Canvas canvas, Object data) {

  }

  @Override
  protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

  }

  @Override
  protected boolean preventClear() {
    return false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        isTouch = true;
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        isTouch = false;
        maxWind = 0;
        break;
    }
    if (isTouch) {
      maxWind = ((mWidth >> 1) - event.getX());
    }
    return true;
  }

  private float R1, R2, R3, R4, R5, gap;

  private class Snow implements Reusable {
    public Path path;
    public float[] pos;
    public long duration, time;
    public float radius;
    public Shader gradient;
    private float x, y;

    public Snow() {
      path = new Path();
      pos = new float[2];
      reset();
    }

    @Override
    public void reset() {
      time = 0;
      generateRadiusAndDuration();
      path.reset();
      x = (float) (mWidth * Math.random() + wind * 4 * Math.random() - wind * 4 * Math.random()) - wind * 4;
      y = -R5;
      path.moveTo(x, y);
      float x1, x2, x3, y1, y2, y3;
      x1 = x + offsetX();
      y1 = mHeight / 3f;
      x2 = x1 + offsetX();
      y2 = mHeight * 2 / 3f;
      x3 = x2 + offsetX();
      y3 = mHeight + R5;
      path.cubicTo(x1, y1, x2, y2, x3, y3);
      gradient =
          new RadialGradient(x, y, radius, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP);
    }

    private float offsetX() {
      return (float) (2 * Math.random() * gap - gap);
    }

    private void generateRadiusAndDuration() {
      double seed = Math.random();
      if (seed < 0.2) {
        radius = R5;
        duration = (long) (3200 + Math.random() * 500);
      } else if (seed < 0.4) {
        radius = R4;
        duration = (long) (3600 + Math.random() * 600);
      } else if (seed < 0.6) {
        radius = R3;
        duration = (long) (4200 + Math.random() * 500);
      } else if (seed < 0.8) {
        radius = R2;
        duration = 5000;
      } else {
        radius = R1;
        duration = 6500;
      }
    }

    public void move() {
      if (time >= duration) {
        snows.remove(this);
        return;
      }
      time += UPDATE_RATE;
      pathMeasure.setPath(path, false);
      float tt = (float) time / duration;
      float distance = tt * pathMeasure.getLength();
      pathMeasure.getPosTan(distance, pos, null);
      pos[0] += wind * 10 * tt;
    }

    public void draw(Canvas canvas) {
      matrix.setTranslate(pos[0] - x, pos[1] - y);
      gradient.setLocalMatrix(matrix);
      mPaint.setShader(gradient);
      canvas.drawCircle(pos[0], pos[1], radius, mPaint);
    }

    @Override
    public boolean isLeisure() {
      return time >= duration;
    }
  }
}
