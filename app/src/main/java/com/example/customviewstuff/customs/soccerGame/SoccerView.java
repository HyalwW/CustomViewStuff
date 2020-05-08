package com.example.customviewstuff.customs.soccerGame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.customviewstuff.customs.BaseSurfaceView;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/7
 */
public class SoccerView extends BaseSurfaceView {
    private Player player1, player2;
    private boolean isHost;
    private Ball soccer;
    private float ballRadius;
    private float playerRadius;
    private Path triPath;
    private boolean shooting;

    public SoccerView(Context context) {
        super(context);
    }

    public SoccerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SoccerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        player1 = new Player();
        player2 = new Player();
        soccer = new Ball();
        triPath = new Path();
    }

    @Override
    protected void onReady() {
        startAnim();
        ballRadius = getMeasuredWidth() * 0.02f;
        playerRadius = getMeasuredWidth() * 0.04f;
        player1.setPos(getMeasuredWidth() >> 1, getMeasuredHeight() * 0.8f);
        player1.running();
        player2.setPos(getMeasuredWidth() >> 1, getMeasuredHeight() * 0.2f);
        player2.running();
        soccer.setPos(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
    }

    @Override
    protected void onDataUpdate() {
        if ((soccer.icmX != 0 || soccer.icmY != 0) && !player1.holdBall && !player2.holdBall) {
            if (soccer.icmX != 0) {
                soccer.x += soccer.icmX;
                if (soccer.x - ballRadius < 0 || soccer.x + ballRadius > getMeasuredWidth()) {
                    if (soccer.x - ballRadius < 0) {
                        soccer.x = ballRadius;
                    } else {
                        soccer.x = getMeasuredWidth() - ballRadius;
                    }
                    soccer.icmX = -soccer.icmX;
                }
                if (soccer.icmX < 0) {
                    soccer.icmX += soccer.subX;
                    if (soccer.icmX > 0) {
                        soccer.icmX = 0;
                    }
                } else {
                    soccer.icmX -= soccer.subX;
                    if (soccer.icmX < 0) {
                        soccer.icmX = 0;
                    }
                }
            }
            if (soccer.icmY != 0) {
                soccer.y += soccer.icmY;
                if (soccer.y - ballRadius < 0 || soccer.y + ballRadius > getMeasuredHeight()) {
                    if (soccer.y - ballRadius < 0) {
                        soccer.y = ballRadius;
                    } else {
                        soccer.y = getMeasuredHeight() - ballRadius;
                    }
                    soccer.icmY = -soccer.icmY;
                }
                if (soccer.icmY < 0) {
                    soccer.icmY += soccer.subY;
                    if (soccer.icmY > 0) {
                        soccer.icmY = 0;
                    }
                } else {
                    soccer.icmY -= soccer.subY;
                    if (soccer.icmY < 0) {
                        soccer.icmY = 0;
                    }
                }
            }
            if (dis2Player1(soccer.x, soccer.y) <= ballRadius + playerRadius) {
                player1.holdBall(true);
            } else if (dis2Player2(soccer.x, soccer.y) <= ballRadius + playerRadius) {
                player2.holdBall(true);
            }
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawPlayGround(canvas);
        drawPlayer1(canvas);
        drawPlayer2(canvas);
        drawSoccer(canvas);
    }

    private void drawPlayGround(Canvas canvas) {

    }

    private void drawPlayer1(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(player1.x, player1.y, playerRadius, mPaint);
        canvas.save();
        canvas.rotate(player1.angle, player1.x, player1.y);
        triPath.reset();
        triPath.moveTo(player1.x, player1.y - playerRadius);
        triPath.lineTo((float) (playerRadius * Math.sin(Math.PI / 3)) + player1.x, (float) (playerRadius * Math.cos(Math.PI / 3)) + player1.y);
        triPath.lineTo((float) (playerRadius * Math.sin(Math.PI / 3 * 5)) + player1.x, (float) (playerRadius * Math.cos(Math.PI / 3 * 5)) + player1.y);
        triPath.close();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(player1.color);
        canvas.drawPath(triPath, mPaint);
        canvas.restore();
    }

    private void drawPlayer2(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(player2.x, player2.y, playerRadius, mPaint);
        canvas.save();
        canvas.rotate(player2.angle, player2.x, player2.y);
        triPath.reset();
        triPath.moveTo(player2.x, player2.y - playerRadius);
        triPath.lineTo((float) (playerRadius * Math.sin(Math.PI / 3)) + player2.x, (float) (playerRadius * Math.cos(Math.PI / 3)) + player2.y);
        triPath.lineTo((float) (playerRadius * Math.sin(Math.PI / 3 * 5)) + player2.x, (float) (playerRadius * Math.cos(Math.PI / 3 * 5)) + player2.y);
        triPath.close();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(player2.color);
        canvas.drawPath(triPath, mPaint);
        canvas.restore();
    }

    private void drawSoccer(Canvas canvas) {
        mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(soccer.x, soccer.y, ballRadius, mPaint);
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
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        player1.stopRun();
        player2.stopRun();
    }

    private float lastX, lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventY = event.getY();
        float eventX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                lastY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (dis(eventX, eventY, lastX, lastY) >= 5) {
                    if (dis2Player1(eventX, eventY) < playerRadius * 2) {
                        player1.setPos(eventX, eventY);
                    }
                    if (player1.holdBall) {
                        float sx = (float) (player1.x + Math.sin(player1.direction) * (ballRadius + playerRadius));
                        float sy = (float) (player1.y + Math.cos(player1.direction) * (ballRadius + playerRadius));
                        soccer.setPos(sx, sy);
                    } else {
                        if (dis2Ball(player1.x, player1.y) <= ballRadius + playerRadius) {
                            if (player2.holdBall) {
                                player2.holdBall(false);
                            }
                            player1.holdBall(true);
                        }
                    }
                    lastX = eventX;
                    lastY = eventY;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (player1.holdBall) {
                    soccer.hit(getMeasuredWidth() * 0.05f, getMeasuredWidth() * 0.0001f, player1.direction);
                    player1.holdBall(false);
                }
                break;
        }
        return true;
    }

    private float dis2Ball(float x, float y) {
        return (float) Math.sqrt((x - soccer.x) * (x - soccer.x) + (y - soccer.y) * (y - soccer.y));
    }

    private float dis2Player1(float x, float y) {
        return (float) Math.sqrt((x - player1.x) * (x - player1.x) + (y - player1.y) * (y - player1.y));
    }

    private float dis2Player2(float x, float y) {
        return (float) Math.sqrt((x - player2.x) * (x - player2.x) + (y - player2.y) * (y - player2.y));
    }

    private float dis(float sx, float sy, float ex, float ey) {
        return (float) Math.sqrt((sx - ex) * (sx - ex) + (sy - ey) * (sy - ey));
    }
}
