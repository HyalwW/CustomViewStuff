package com.example.customviewstuff.starwar;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.customviewstuff.R;
import com.example.customviewstuff.customs.BaseSurfaceView;
import com.example.customviewstuff.starwar.interfaces.IBlastEffect;
import com.example.customviewstuff.starwar.interfaces.IBullet;
import com.example.customviewstuff.starwar.interfaces.IDrawable;
import com.example.customviewstuff.starwar.interfaces.IEnemy;
import com.example.customviewstuff.starwar.interfaces.ISpaceShip;
import com.example.customviewstuff.starwar.model.AirFighter;
import com.example.customviewstuff.starwar.model.SimpleEnemy;

/**
 * @author wangwenhui on 3/28/21
 */
public class StarWarView extends BaseSurfaceView {
  private static final int mTouchSlop = 5;
  private ISpaceShip me;
  private final List<IEnemy> mEnemies = new CopyOnWriteArrayList<>();
  private final List<IBullet> mBullets = new CopyOnWriteArrayList<>();
  private final List<IBlastEffect> mBlastEffects = new CopyOnWriteArrayList<>();
  private Bitmap mEnemyBitmap;
  private int mScore;

  public StarWarView(Context context) {
    super(context);
  }

  public StarWarView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public StarWarView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onInit() {
    me = new AirFighter(getContext());
    mEnemyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_1);
  }

  @Override
  protected void onReady() {
    me.initContainer(mWidth, mHeight);
    begin();
    startGenerateEnemyTask();
  }

  private void startGenerateEnemyTask() {
    doInThread(() -> {
      while (running) {
        try {
          Thread.sleep(800);
          addEnemy();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void addEnemy() {
    IEnemy enemy = randomEnemy();
    enemy.initContainer(mWidth, mHeight);
    for (IEnemy e : mEnemies) {
      if (Utils.isOverlap(e.getDrawRect(), enemy.getDrawRect())) {
        addEnemy();
        return;
      }
    }
    mEnemies.add(enemy);
  }

  private IEnemy randomEnemy() {
    float randomX = (float) (mWidth * Math.random() + mWidth * 0.1f);
    if (randomX < mWidth * 0.1f) {
      randomX = mWidth * 0.1f;
    } else if (randomX > mWidth * 0.9f) {
      randomX = mWidth * 0.9f;
    }
    return new SimpleEnemy(getContext(), randomX, mEnemyBitmap);
  }

  @Override
  protected void onDataUpdate() {
    me.move(UPDATE_RATE);
    IBullet shootBullet = me.shootBullet();
    if (shootBullet != null) {
      mBullets.add(shootBullet);
    }
    handleEnemiesMove();
    handleBulletsMove();
    handleEffectsMove();
    Log.e("wwh", "onDataUpdate: 敌机数量：" + mEnemies.size() + " 子弹数量：" + mBullets.size() + " 爆炸数量：" +
        mBlastEffects.size());
  }

  private void handleEnemiesMove() {
    for (IEnemy enemy : mEnemies) {
      enemy.move(UPDATE_RATE);
      IBullet eb = enemy.shootBullet();
      if (eb != null) {
        mBullets.add(eb);
      }
      if (checkHitMe(enemy)) {
        mEnemies.remove(enemy);
        if (me.isDestroyed()) {
          handleGameOver();
        }
      } else {
        if (enemy.isFlyAway()) {
          mScore -= enemy.score() * 2;
          mEnemies.remove(enemy);
        }
      }
    }
  }

  private void handleBulletsMove() {
    for (IBullet bullet : mBullets) {
      bullet.move(UPDATE_RATE);
      if (checkHitMe(bullet)) {
        mBullets.remove(bullet);
        if (me.isDestroyed()) {
          handleGameOver();
        }
      } else if (!bullet.isFromEnemy()) {
        for (IEnemy enemy : mEnemies) {
          if (checkHitEnemy(enemy, bullet)) {
            enemy.beHit(bullet.damage());
            if (enemy.isDestroyed()) {
              mEnemies.remove(enemy);
              mBullets.remove(bullet);
              mScore += enemy.score();
              IBlastEffect blastEffect = enemy.destroyedEffect();
              if (blastEffect != null) {
                mBlastEffects.add(blastEffect);
              }
              return;
            }
          }
        }
      }
      if (bullet.isFlyAway()) {
        mBullets.remove(bullet);
      }
    }
  }

  private void handleGameOver() {
    IBlastEffect blastEffect = me.destroyedEffect();
    if (blastEffect != null) {
      mBlastEffects.add(blastEffect);
    }
    postDelayed(this::resetAll, 1000);
  }

  private void handleEffectsMove() {
    for (IBlastEffect blastEffect : mBlastEffects) {
      blastEffect.move(UPDATE_RATE);
      if (blastEffect.isBlastDone()) {
        mBlastEffects.remove(blastEffect);
      }
    }
  }

  @Override
  protected void onRefresh(Canvas canvas) {
    super.onRefresh(canvas);
    me.draw(canvas, mPaint);
    for (IEnemy enemy : mEnemies) {
      enemy.draw(canvas, mPaint);
    }
    for (IBullet bullet : mBullets) {
      bullet.draw(canvas, mPaint);
    }
    for (IBlastEffect blastEffect : mBlastEffects) {
      blastEffect.draw(canvas, mPaint);
    }
    drawScore(canvas);
  }

  private float dx, dy, mx, my;
  private boolean canDrag;
  private final RectF temp = new RectF();

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    //只能左右移动
//    int index = event.getActionIndex();
//    switch (event.getActionMasked()) {
//      case MotionEvent.ACTION_UP:
//      case MotionEvent.ACTION_CANCEL:
//        ((AirFighter) me).stopTurn();
//        break;
//      case MotionEvent.ACTION_DOWN:
//      case MotionEvent.ACTION_POINTER_DOWN:
//        float x = event.getX(index);
//        if (x < mWidth / 2f) {
//          ((AirFighter) me).turnLeft();
//        } else {
//          ((AirFighter) me).turnRight();
//        }
//        break;
//    }
    RectF rect = me.getDrawRect();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mx = dx = event.getX();
        my = dy = event.getY();
        if (rect.contains(dx, dy)) {
          canDrag = true;
          temp.set(rect);
        }
      case MotionEvent.ACTION_MOVE:
        if (canDrag) {
          float moveOffset = event.getX() - mx;
          if (Math.abs(moveOffset) <= mTouchSlop) {
            ((AirFighter) me).stopTurn();
          } else if (moveOffset > 0) {
            ((AirFighter) me).turnRight();
          } else if (moveOffset < 0) {
            ((AirFighter) me).turnLeft();
          }
          mx = event.getX();
          my = event.getY();
          float xx = mx - dx, yy = my - dy;
          rect.left = temp.left + xx;
          rect.right = temp.right + xx;
          rect.top = temp.top + yy;
          rect.bottom = temp.bottom + yy;
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        ((AirFighter) me).stopTurn();
        canDrag = false;
        dx = dy = mx = my = 0;
        temp.set(0, 0, 0, 0);
        break;
    }
    return true;
  }

  public void begin() {
    if (isAlive) {
      startAnim();
    }
  }

  private void drawScore(Canvas canvas) {
    float textSize = mWidth * 0.06f;
    mPaint.setTextSize(textSize);
    mPaint.setColor(Color.RED);
    canvas.drawText(String.format("分数：%d", mScore), 0, textSize, mPaint);
  }

  private boolean checkHitMe(IDrawable object) {
    if (Utils.isOverlap(me.getDrawRect(), object.getDrawRect()) && !me.isDestroyed()) {
      if (object instanceof IEnemy) {
        me.beHit(((IEnemy) object).bloodValue());
      } else if (object instanceof IBullet && ((IBullet) object).isFromEnemy()) {
        me.beHit(((IBullet) object).damage());
      }
      return true;
    }
    return false;
  }

  private boolean checkHitEnemy(IEnemy enemy, IBullet bullet) {
    return Utils.isOverlap(enemy.getDrawRect(), bullet.getDrawRect());
  }

  private void resetAll() {
    mScore = 0;
    me.reset();
    mEnemies.clear();
    mBullets.clear();
  }
}
