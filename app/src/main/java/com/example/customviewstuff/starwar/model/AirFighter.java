package com.example.customviewstuff.starwar.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.customviewstuff.R;
import com.example.customviewstuff.starwar.interfaces.IBlastEffect;
import com.example.customviewstuff.starwar.interfaces.IBullet;
import com.example.customviewstuff.starwar.interfaces.ISpaceShip;

/**
 * 我的飞机
 *
 * @author wangwenhui on 3/28/21
 */
public class AirFighter implements ISpaceShip {
  private final int[] fights = new int[]{R.drawable.fighter_1, R.drawable.fighter_2, R.drawable.fighter_3};
  private static final float R_LEFT = -45, R_RIGHT = 45, radiusIC = 3;
  private static final float speedR = 0.018f, cyR = 0.9f, jwR = 0.2f;
  private static final long bulletShootPried = 200;

  private final Context mContext;
  public int level, mBloodValue;
  public Bitmap mFighterBitmap;
  private float moveSpeed, leftSpeed, rightSpeed;
  private float mWidth, mHeight;
  private float currentRadius, targetRadius;
  private Camera mRotateCamera;
  private Matrix mMatrix;
  private RectF mJetRect;
  private float jetCenter[] = new float[2];
  private float jetWidth, jetHeight;
  private long shotDelay;

  public AirFighter(Context context) {
    mContext = context;
    init(context);
  }

  private void init(Context context) {
    level = 0;
    shotDelay = bulletShootPried;
    mBloodValue = 100;
    mFighterBitmap = BitmapFactory.decodeResource(context.getResources(), fights[level]);
    mRotateCamera = new Camera();
    mMatrix = new Matrix();
    mJetRect = new RectF();
  }

  @Override
  public IBullet shootBullet() {
    if (shotDelay > 0) {
      return null;
    }
    shotDelay = bulletShootPried;
    LaserBullet laserBullet = new LaserBullet(false, jetCenter[0], mJetRect.top, 0, Color.CYAN);
    laserBullet.initContainer(mWidth, mHeight);
    return laserBullet;
  }

  @Override
  public IBlastEffect destroyedEffect() {
    return new CircleBlastEffect(this, mFighterBitmap);
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
    if (isBitmapAvailable()) {
      mFighterBitmap.recycle();
    }
  }

  @Override
  public int totalLife() {
    return 1;
  }

  @Override
  public void move(long time) {
    if (mBloodValue <= 0) {
      return;
    }
    shotDelay -= time;
//    mJetRect.left += moveSpeed;
//    mJetRect.right = mJetRect.left + jetWidth;
//    if (mJetRect.left < 0) {
//      mJetRect.left = 0;
//      mJetRect.right = jetWidth;
//    } else if (mJetRect.right >= mWidth) {
//      mJetRect.right = mWidth;
//      mJetRect.left = mWidth - jetWidth;
//    }
    jetCenter[0] = (mJetRect.left + mJetRect.right) / 2f;
    jetCenter[1] = (mJetRect.top + mJetRect.bottom) / 2f;
    if (currentRadius < targetRadius) {
      currentRadius += radiusIC;
      if (currentRadius > targetRadius) {
        currentRadius = targetRadius;
      }
    } else if (currentRadius > targetRadius) {
      currentRadius -= radiusIC;
      if (currentRadius < targetRadius) {
        currentRadius = targetRadius;
      }
    }
  }

  @Override
  public void draw(Canvas canvas, Paint paint) {
    if (!isBitmapAvailable() || isDestroyed()) {
      return;
    }
    int layer = canvas.saveLayer(mJetRect, null);
    mRotateCamera.save();
    mMatrix.reset();
    mRotateCamera.rotateY(currentRadius);
    mRotateCamera.getMatrix(mMatrix);
    mRotateCamera.restore();
    mMatrix.preTranslate(-jetCenter[0], -jetCenter[1]);
    mMatrix.postTranslate(jetCenter[0], jetCenter[1]);
    canvas.concat(mMatrix);
    canvas.drawBitmap(mFighterBitmap, null, mJetRect, null);
    canvas.restoreToCount(layer);
  }

  @Override
  public RectF getDrawRect() {
    return mJetRect;
  }

  @Override
  public void initContainer(float width, float height) {
    mWidth = width;
    mHeight = height;
    leftSpeed = -width * speedR;
    rightSpeed = -leftSpeed;
    initJetRect(mWidth * 0.5f, mHeight * cyR);
  }

  @Override
  public void reset() {
    stopTurn();
    level = 0;
    mBloodValue = 100;
    if (isBitmapAvailable()) {
      mFighterBitmap.recycle();
    }
    mFighterBitmap = BitmapFactory.decodeResource(mContext.getResources(), fights[level]);
    initJetRect(mWidth * 0.5f, mHeight * cyR);
  }

  private void initJetRect(float cx, float cy) {
    if (!isBitmapAvailable()) {
      return;
    }
    float ratio = (float) mFighterBitmap.getWidth() / mFighterBitmap.getHeight();
    jetWidth = mWidth * jwR;
    jetHeight = jetWidth / ratio;
    float left = cx - jetWidth * 0.5f;
    float top = cy - jetHeight * 0.5f;
    mJetRect.set(left, top, left + jetWidth, top + jetHeight);
    jetCenter[0] = (mJetRect.left + mJetRect.right) / 2f;
    jetCenter[1] = (mJetRect.top + mJetRect.bottom) / 2f;
  }

  public void turnLeft() {
    moveSpeed = leftSpeed;
    targetRadius = R_LEFT;
  }

  public void turnRight() {
    moveSpeed = rightSpeed;
    targetRadius = R_RIGHT;
  }

  public void stopTurn() {
    moveSpeed = 0;
    targetRadius = 0;
  }

  public void upgrade() {
    mBloodValue = 100;
    if (level >= 2) {
      return;
    }
    level++;
    if (isBitmapAvailable()) {
      mFighterBitmap.recycle();
    }
    mFighterBitmap = BitmapFactory.decodeResource(mContext.getResources(), fights[level]);
    initJetRect(jetCenter[0], jetCenter[1]);
  }

  private boolean isBitmapAvailable() {
    return mFighterBitmap != null && !mFighterBitmap.isRecycled();
  }
}
