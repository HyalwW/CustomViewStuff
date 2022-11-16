package com.example.customviewstuff.customs.love;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.R;
import com.example.customviewstuff.customs.BaseSurfaceView;

/**
 * @author wangwenhui on 2022/11/14
 */
public class LoveView extends BaseSurfaceView {
  private final int maxCount = 500, jumpCount = 360;
  private final Path mLovePath = new Path();
  private float mAngel = 0, ua;
  private Bitmap bitmap, bitmapD;
  private final Random random = new Random();
  private final List<Heart> mHearts = new ArrayList<>();
  private final List<Heart> extraHearts = new ArrayList<>();

  private final Path path = new Path();
  private float jumpAngel = 0;
  private float jinc = (float) (Math.PI / 12);
  private boolean isTouch, isClick;

  public LoveView(Context context) {
    super(context);
  }

  public LoveView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public LoveView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onInit() {
    holder.setFormat(PixelFormat.TRANSLUCENT);
    mPaint.setColor(Color.RED);
    mPaint.setStrokeWidth(6);
    UPDATE_RATE = 16;
    ua = (float) (UPDATE_RATE / 5000f * 2 * Math.PI);
    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.heart_c);
    bitmapD = BitmapFactory.decodeResource(getResources(), R.drawable.heart_d);
  }

  @Override
  protected void onReady() {
    mLovePath.reset();
    mPaint.setTextAlign(Paint.Align.CENTER);
    mPaint.setTextSize(mWidth * 0.05f);
    startAnim();
  }

  @Override
  protected void onDataUpdate() {
    if (!extraHearts.isEmpty() && mHearts.size() < maxCount) {
      mHearts.addAll(extraHearts);
    }
    extraHearts.clear();
    if (mAngel < Math.PI * 2) {
      float x = (float) (16 * Math.pow(Math.sin(mAngel), 3));
      float y = -(float) (13 * Math.cos(mAngel) - 5 * Math.cos(mAngel * 2) - 2 * Math.cos(3 * mAngel) - Math.cos(4 * mAngel));
      float cx = x * mWidth / 40f + mWidth / 2f;
      float cy = y * mWidth / 40f + mHeight / 2f;
      mHearts.add(Heart.create(cx, cy));
      if (mLovePath.isEmpty()) {
        mLovePath.moveTo(cx, cy);
      } else {
        mLovePath.lineTo(cx, cy);
      }
      mAngel += ua;
    } else if (mHearts.size() >= jumpCount){
      jumpAngel += jinc;
      if (jumpAngel >= Math.PI * 6) {
        jumpAngel = 0;
      }
      if (mHearts.size() >= maxCount && jumpAngel >= Math.PI * 4) {
        jumpAngel = 0;
      }
    }
    for (Heart heart : mHearts) {
      heart.go();
    }
  }

  @Override
  protected void onRefresh(Canvas canvas) {
    super.onRefresh(canvas);
    canvas.drawColor(Color.WHITE);
    canvas.save();
    float scale = 1;
    if (jumpAngel < Math.PI * 2 && mHearts.size() >= jumpCount && !isTouch) {
      scale = (float) (1 + Math.abs(Math.sin(jumpAngel)) * 0.04f);
    }
    canvas.scale(scale, scale, mWidth / 2f, mHeight / 2f);
    //如果爱心不完整，画红色爱心线条
//    if (mAngel < Math.PI * 2) {
//      mPaint.setStyle(Paint.Style.STROKE);
//      canvas.drawPath(mLovePath, mPaint);
//    }
    //在爱心线条上随机绘制起伏的小爱心
    for (Heart heart : mHearts) {
      if (jumpAngel < Math.PI * 2 && mHearts.size() >= jumpCount && !isTouch) {
        scale = (float) (1 + Math.abs(Math.sin(jumpAngel)) * 0.2f);
      }
      canvas.save();
      canvas.rotate(heart.rotate, heart.cx, heart.cy);
      if (heart.disableScale && scale > 1) {
        canvas.scale(scale, scale, heart.cx, heart.cy);
      }
      canvas.drawBitmap(heart.heartType == 1 ? bitmapD : bitmap, null, heart.rect, mPaint);
      canvas.restore();
    }
    canvas.restore();
    mPaint.setStyle(Paint.Style.FILL);
    String text = "";
    if (isTouch) {
      text = "芳心捕获中...";
    } else if (mHearts.size() >= maxCount) {
      text = "心动❤️❤️满啦~~";
    }
    //绘制出文案：总共的爱心个数
    canvas.drawText(text, mWidth / 2f, mWidth * 0.2f, mPaint);
    if (isTouch) {
      mPaint.setStyle(Paint.Style.STROKE);
      canvas.drawCircle(Heart.touchX, Heart.touchY, 20, mPaint);
    }
  }

  private float downX, downY;
  private final Runnable tr = () -> {
    isClick = false;
    isTouch = true;
    Heart.touchX = downX;
    Heart.touchY = downY;
  };

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        downX = event.getX();
        downY = event.getY();
        isTouch = false;
        isClick = true;
        postDelayed(tr, 500);
        break;
      case MotionEvent.ACTION_MOVE:
        if (Math.abs(event.getX() - downX) > 10 || Math.abs(event.getX() - downX) > 10) {
          removeCallbacks(tr);
          isTouch = true;
          isClick = false;
          Heart.touchX = event.getX();
          Heart.touchY = event.getY();
        }
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        removeCallbacks(tr);
        if (isClick) {
          addValidHeart(downX, downY);
          addValidHeart(downX, downY);
        }
        Heart.touchX = 0;
        Heart.touchY = 0;
        downX = 0;
        downY = 0;
        isTouch = false;
        isClick = false;
        break;
    }
    return true;
  }

  private void addValidHeart(float ex, float ey) {
    float x = 0, y = 0;
    while (path.isEmpty()) {
      x = (int) (random.nextFloat() * mWidth);
      y = (int) (random.nextFloat() * mHeight);
      path.reset();
      path.addRect(x - 1, y - 1, x + 1, y + 1, Path.Direction.CW);
      path.op(mLovePath, Path.Op.INTERSECT);
    }
    path.reset();
    Heart e = Heart.create(x, y);
    e.cx = ex;
    e.cy = ey;
    e.disableScale = true;
    e.heartType = 1;
    extraHearts.add(e);
  }
}
