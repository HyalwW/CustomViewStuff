package com.example.customviewstuff.helpers;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/10
 */
public class RotateHelper {
    /* Camera旋转的最大角度 */
    private float mMaxCameraRotate = 15;
    /* Camera绕X轴旋转的角度 */
    private float mCameraRotateX;
    /* Camera绕Y轴旋转的角度 */
    private float mCameraRotateY;
    private Matrix mMatrix;
    private Camera mCamera;
    private View target;
    private boolean isClick, isLongClick;
    private float downX, downY;
    private ValueAnimator mShakeAnim;

    public RotateHelper(View target) {
        this.target = target;
        mMatrix = new Matrix();
        mCamera = new Camera();
        target.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    target.getParent().requestDisallowInterceptTouchEvent(true);
                    if (mShakeAnim != null && mShakeAnim.isRunning()) {
                        mShakeAnim.cancel();
                    }
                    isClick = true;
                    isLongClick = true;
                    target.postDelayed(longClick, 600);
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((Math.abs(event.getX() - downX) > 5 || Math.abs(event.getY() - downY) > 5) && isClick) {
                        isClick = false;
                        isLongClick = false;
                    }
                    if (!isClick) {
                        target.removeCallbacks(longClick);
                        getCameraRotate(event);
                        target.invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (!isLongClick) {
                        if (isClick) {
                            target.performClick();
                        } else {
                            startShakeAnim();
                        }
                    }
                    target.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        });
    }

    private Runnable longClick = () -> target.performLongClick();

    //在onDraw方法开始位置中插入该方法
    public void cameraRotate(Canvas mCanvas) {
        mMatrix.reset();
        mCamera.save();
        mCamera.rotateX(mCameraRotateX);//绕x轴旋转
        mCamera.rotateY(mCameraRotateY);//绕y轴旋转
        mCamera.getMatrix(mMatrix);//计算对于当前变换的矩阵，并将其复制到传入的mMatrix中
        mCamera.restore();
        /*
          Camera默认位于视图的左上角，故生成的矩阵默认也是以其左上角为旋转中心，
          所以在动作之前调用preTranslate将mMatrix向左移动getWidth()/2个长度，
          向上移动getHeight()/2个长度，
          使旋转中心位于矩阵的中心位置，动作之后再post回到原位
         */
        mMatrix.preTranslate(-target.getWidth() >> 1, -target.getHeight() >> 1);
        mMatrix.postTranslate(target.getWidth() >> 1, target.getHeight() >> 1);
        mCanvas.concat(mMatrix);//将mMatrix与canvas中当前的Matrix相关联
    }

    private void getCameraRotate(MotionEvent event) {
        float rotateX = -(event.getY() - (target.getHeight() >> 1));
        float rotateY = (event.getX() - (target.getWidth() >> 1));
        /*
         为什么旋转角度要这样计算：
          当Camera.rotateX(x)的x为正时，图像围绕X轴，上半部分向里下半部分向外，进行旋转，
          也就是手指触摸点要往上移。这个x就会与event.getY()的值有关，x越大，绕X轴旋转角度越大，
          以圆心为基准，手指往上移动，event.getY() - getHeight() / 2的值为负，
          故 float rotateX = -(event.getY() - getHeight() / 2)
          同理，
          当Camera.rotateY(y)的y为正时，图像围绕Y轴，右半部分向里左半部分向外，进行旋转，
          也就是手指触摸点要往右移。这个y就会与event.getX()的值有关，y越大，绕Y轴旋转角度越大，
          以圆心为基准，手指往右移动，event.getX() - getWidth() / 2的值为正，
          故 float rotateY = event.getX() - getWidth() / 2
         */

        /*
          此时得到的rotateX、rotateY 其实是以圆心为基准，手指移动的距离，
          这个值很大，不能用来作为旋转的角度，
          所以还需要继续处理
         */

        //求出移动距离与半径之比。mMaxRadius为白色大圆的半径
        float mMaxRadius = target.getWidth() >> 1;
        float percentX = rotateX / mMaxRadius;
        float percentY = rotateY / mMaxRadius;

        if (percentX > 1) {
            percentX = 1;
        } else if (percentX < -1) {
            percentX = -1;
        }

        if (percentY > 1) {
            percentY = 1;
        } else if (percentY < -1) {
            percentY = -1;
        }

        //将最终的旋转角度控制在一定的范围内，这里mMaxCameraRotate的值为15，效果比较好
        mCameraRotateX = percentX * mMaxCameraRotate;
        mCameraRotateY = percentY * mMaxCameraRotate;
    }

    private void startShakeAnim() {
        final String cameraRotateXName = "cameraRotateX";
        final String cameraRotateYName = "cameraRotateY";
        PropertyValuesHolder cameraRotateXHolder =
                PropertyValuesHolder.ofFloat(cameraRotateXName, mCameraRotateX, 0);
        PropertyValuesHolder cameraRotateYHolder =
                PropertyValuesHolder.ofFloat(cameraRotateYName, mCameraRotateY, 0);
        this.mShakeAnim = ValueAnimator.ofPropertyValuesHolder(cameraRotateXHolder, cameraRotateYHolder);
        mShakeAnim.setInterpolator(new ReverseInterpolator());
        mShakeAnim.setDuration(800);
        mShakeAnim.addUpdateListener(animation -> {
            mCameraRotateX = (float) animation.getAnimatedValue(cameraRotateXName);
            mCameraRotateY = (float) animation.getAnimatedValue(cameraRotateYName);
            target.invalidate();
        });
        mShakeAnim.start();
    }
}
