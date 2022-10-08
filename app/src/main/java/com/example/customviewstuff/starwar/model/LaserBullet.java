package com.example.customviewstuff.starwar.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.customviewstuff.starwar.interfaces.IBullet;

/**
 * @author wangwenhui on 3/28/21
 */
public class LaserBullet implements IBullet {
  private final int mColor;
  private boolean mIsFromEnemy;
  private final float mShootX, mFromY;
  private float mToY;
  private float mBulletWidth, mBulletHeight;
  private float mSpeedY, mCurY;
  private float mWidth, mHeight;
  private RectF mRectF;

  public LaserBullet(boolean isFromEnemy, float shootX, float fromY, float toY, int color) {
    mIsFromEnemy = isFromEnemy;
    mShootX = shootX;
    mFromY = fromY;
    mToY = toY;
    mRectF = new RectF();
    mColor = color;
  }

  private void init() {
    mCurY = mFromY > mToY ? mFromY - mBulletHeight * 0.6f : mFromY + mBulletHeight * 0.6f;
    setRect();
  }

  @Override
  public int damage() {
    return 1000;
  }

  @Override
  public boolean isFromEnemy() {
    return mIsFromEnemy;
  }

  @Override
  public void move(long time) {
    mCurY += mSpeedY;
    setRect();
  }

  @Override
  public boolean isFlyAway() {
    if (mToY > mFromY) {
      return mCurY > mToY;
    } else if (mToY < mFromY) {
      return mCurY < mToY;
    }
    return false;
  }

  @Override
  public void draw(Canvas canvas, Paint paint) {
    paint.setColor(mColor);
    float radius = mBulletWidth * 0.5f;
    canvas.drawRoundRect(mRectF, radius, radius, paint);
  }

  @Override
  public RectF getDrawRect() {
    return mRectF;
  }

  @Override
  public void initContainer(float width, float height) {
    mWidth = width;
    mHeight = height;
    mSpeedY = height * (mIsFromEnemy ? 0.005f : 0.01f) * (mToY > mFromY ? 1 : -1);
    mToY += ((mToY > mFromY) ? mBulletHeight * 0.5f : -mBulletHeight * 0.5f);
    mBulletWidth = mWidth * 0.01f;
    mBulletHeight = mHeight * 0.03f;
    init();
  }

  private void setRect() {
    float left = mShootX - mBulletWidth * 0.5f;
    float top = mCurY - mBulletHeight * 0.5f;
    mRectF.set(left, top, left + mBulletWidth, top + mBulletHeight);
  }
}
