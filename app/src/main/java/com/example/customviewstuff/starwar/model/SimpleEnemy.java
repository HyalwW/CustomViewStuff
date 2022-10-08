package com.example.customviewstuff.starwar.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

import com.example.customviewstuff.starwar.interfaces.IBlastEffect;
import com.example.customviewstuff.starwar.interfaces.IBullet;
import com.example.customviewstuff.starwar.interfaces.IEnemy;

/**
 * @author wangwenhui on 3/28/21
 */
public class SimpleEnemy implements IEnemy {
  private static final long bulletDelay = 1200;
  private final Context mContext;
  private final float mMoveX;
  private Bitmap mEnemyBitmap;
  private int mBloodValue = 100;
  private RectF mEnemyRect;
  private final Path mFlyPath = new Path();
  private final PathMeasure mPathMeasure = new PathMeasure();
  private float mWidth, mHeight, mEnemyWidth, mEnemyHeight;
  private final float[] center = new float[2];
  private long shotDelay, mMoveTime;

  public SimpleEnemy(Context context, float moveX, Bitmap enemyBitmap) {
    mContext = context;
    mMoveX = moveX;
    init(enemyBitmap);
  }

  private void init(Bitmap enemyBitmap) {
    shotDelay = bulletDelay;
    mBloodValue = 100;
    mEnemyBitmap = enemyBitmap;
    mEnemyRect = new RectF();
  }

  @Override
  public Path flyPath() {
    return mFlyPath;
  }

  @Override
  public long duration() {
    return 10000;
  }

  @Override
  public boolean isFlyAway() {
    return mMoveTime >= duration();
  }

  @Override
  public int score() {
    return 100;
  }

  @Override
  public void move(long time) {
    if (mBloodValue <= 0) {
      return;
    }
    mMoveTime += time;
    shotDelay -= time;
    setRect();
  }

  @Override
  public IBullet shootBullet() {
    if (shotDelay <= 0) {
      shotDelay = bulletDelay;
      LaserBullet laserBullet = new LaserBullet(true, center[0], mEnemyRect.bottom, mHeight, Color.RED);
      laserBullet.initContainer(mWidth, mHeight);
      return laserBullet;
    }
    return null;
  }

  @Override
  public IBlastEffect destroyedEffect() {
    CircleBlastEffect blastEffect = new CircleBlastEffect(this, mEnemyBitmap);
    blastEffect.initContainer(mWidth, mHeight);
    return blastEffect;
  }

  @Override
  public int bloodValue() {
    return mBloodValue;
  }

  @Override
  public void beHit(int damage) {
    mBloodValue -= damage;
  }

  @Override
  public boolean isDestroyed() {
    return mBloodValue <= 0;
  }

  @Override
  public void release() {
//    if (isBitmapAvailable()) {
//      mEnemyBitmap.recycle();
//    }
  }

  @Override
  public void draw(Canvas canvas, Paint paint) {
    if (!isBitmapAvailable()) {
      return;
    }
    canvas.drawBitmap(mEnemyBitmap, null, mEnemyRect, null);
  }

  @Override
  public RectF getDrawRect() {
    return mEnemyRect;
  }

  @Override
  public void initContainer(float width, float height) {
    mWidth = width;
    mHeight = height;
    initJet();
  }

  private void initJet() {
    if (!isBitmapAvailable()) {
      return;
    }
    float ratio = (float) mEnemyBitmap.getWidth() / mEnemyBitmap.getHeight();
    mEnemyWidth = mWidth * 0.15f;
    mEnemyHeight = mEnemyWidth / ratio;
    mFlyPath.reset();
    mFlyPath.moveTo(mMoveX, -mEnemyHeight);
    mFlyPath.lineTo(mMoveX, mHeight + mEnemyHeight);
    mPathMeasure.setPath(mFlyPath, false);
    setRect();
  }

  private void setRect() {
    mPathMeasure.getPosTan((float) mMoveTime / duration() * mPathMeasure.getLength(), center, null);
    float left = center[0] - mEnemyWidth * 0.5f;
    float top = center[1] - mEnemyHeight * 0.5f;
    mEnemyRect.set(left, top, left + mEnemyWidth, top + mEnemyHeight);
  }

  private boolean isBitmapAvailable() {
    return mEnemyBitmap != null && !mEnemyBitmap.isRecycled();
  }
}
