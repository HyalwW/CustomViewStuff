package com.example.customviewstuff.customs.soccerGame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.customviewstuff.customs.BaseSurfaceView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/7
 */
public class SoccerView extends BaseSurfaceView {
    private Player player1, player2;
    private boolean isHost, isConnect, isGoal;
    private Ball soccer;
    private float ballRadius, playerRadius, goalWidth;
    private Path triPath;
    private OnMsgSendListener listener;
    private Gson mGson;
    private int soccerOwner;
    private long coolDown;
    private int countDownTime;
    private RectF bgRect;

    //单人模式
    private boolean isPractice, goRight;
    private float xIncrement;

    private float speed, maxSpeed, controlRadius;
    private double moveDirection;

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
        mGson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        soccerOwner = 0;
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);
        bgRect = new RectF();
    }

    @Override
    protected void onReady() {
        ballRadius = getMeasuredWidth() * 0.02f;
        playerRadius = getMeasuredWidth() * 0.04f;
        goalWidth = getMeasuredWidth() >> 1;
        maxSpeed = getMeasuredWidth() * 0.016f;
        controlRadius = getMeasuredWidth() * 0.1f;
        bgRect.set(0, playerRadius * 2, getMeasuredWidth(), getMeasuredHeight() - playerRadius * 2);
        if (player1.x == 0) {
            player1.setPos(getMeasuredWidth() >> 1, getMeasuredHeight() * 0.7f);
            player2.setPos(getMeasuredWidth() >> 1, getMeasuredHeight() * 0.2f);
            soccer.setPos(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
        }
        player1.running();
        if (isPractice) {
            player2.running();
        }
        startAnim();
    }

    @Override
    protected void onDataUpdate() {
        if (isHost) {
            if ((soccer.icmX != 0 || soccer.icmY != 0) && soccerOwner == 0 && !isGoal) {
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
                    changeSoccerOwner(1);
                } else if (dis2Player2(soccer.x, soccer.y) <= ballRadius + playerRadius) {
                    if (isPractice) {
                        soccer.hit(getMeasuredWidth() * 0.04f, getMeasuredWidth() * 0.0001f, randomDirection());
                        changeSoccerOwner(0);
                    } else {
                        changeSoccerOwner(2);
                    }
                }
            }
            if (!isGoal) {
                if (soccerOwner != 2 && dis2Ball(player2.x, player2.y) <= ballRadius + playerRadius) {
                    if (isPractice) {
                        soccer.hit(getMeasuredWidth() * 0.04f, getMeasuredWidth() * 0.0001f, randomDirection());
                        changeSoccerOwner(0);
                    } else {
                        changeSoccerOwner(2);
                    }
                }

                if (soccer.y <= ballRadius) {
                    if (soccer.x > (getMeasuredWidth() >> 1) - (goalWidth / 2) && soccer.x < (getMeasuredWidth() >> 1) + (goalWidth / 2)) {
                        goal();
                        player1.goal();
                        player2.stopRun();
                    }
                } else if (soccer.y >= getMeasuredHeight() - ballRadius) {
                    if (soccer.x > (getMeasuredWidth() >> 1) - (goalWidth / 2) && soccer.x < (getMeasuredWidth() >> 1) + (goalWidth / 2)) {
                        goal();
                        player2.goal();
                        player1.stopRun();
                    }
                }
            } else {
                if (countDownTime > 0) {
                    countDownTime -= UPDATE_RATE;
                } else {
                    countDownTime = 0;
                    isGoal = false;
                    soccer.reset(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
                    player1.reset(getMeasuredWidth() >> 1, getMeasuredHeight() * 0.7f);
                    player1.running();
                    if (isPractice) {
                        player2.running();
                    }
                }
            }
        }
        handleMove();
        if (isPractice && !isGoal) {
            if (goRight) {
                if (player2.x < getMeasuredWidth() / 2f + goalWidth / 2) {
                    player2.x += xIncrement;
                } else {
                    goRight = false;
                }
            } else {
                if (player2.x > getMeasuredWidth() / 2f - goalWidth / 2) {
                    player2.x -= xIncrement;
                } else {
                    goRight = true;
                }
            }
        }
        if (isConnect) {
            handleAndSendMsg();
        }
    }

    private double randomDirection() {
        return Math.PI / 4 - Math.random() * Math.PI / 2;
    }

    private void handleMove() {
        if (controlX != 0) {
            float nx = (float) (speed * Math.sin(moveDirection) + player1.x);
            float ny = (float) (speed * Math.cos(moveDirection) + player1.y);
            if (ny < bgRect.top + playerRadius) {
                ny = bgRect.top + playerRadius;
            } else if (ny > bgRect.bottom - playerRadius) {
                ny = bgRect.bottom - playerRadius;
            }
            if (nx < playerRadius) {
                nx = playerRadius;
            } else if (nx > getMeasuredWidth() - playerRadius) {
                nx = getMeasuredWidth() - playerRadius;
            }
            player1.setPos(nx, ny);

            if (isHost) {
                if (soccerOwner == 1) {
                    float sx = (float) (player1.x + Math.sin(player1.direction) * (ballRadius + playerRadius));
                    float sy = (float) (player1.y + Math.cos(player1.direction) * (ballRadius + playerRadius));
                    soccer.setPos(sx, sy);
                } else {
                    if (dis2Ball(player1.x, player1.y) <= ballRadius + playerRadius) {
                        changeSoccerOwner(1);
                    }
                }
            }
        }
    }

    private void goal() {
        isGoal = true;
        controlX = controlY = 0;
        countDownTime = 3000;
        if (listener != null) {
            listener.onGoal();
        }
    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawPlayGround(canvas);
        drawPlayer1(canvas);
        drawPlayer2(canvas);
        drawSoccer(canvas);
        drawCountDownIfNeed(canvas);
        drawControlPanelIfNeed(canvas);
    }

    private void drawCountDownIfNeed(Canvas canvas) {
        if (countDownTime == 0) return;
        canvas.drawColor(0xDD555555);
        String text = "1";
        if (countDownTime > 2000) {
            text = "3";
        } else if (countDownTime > 1000) {
            text = "2";
        }
        mPaint.setTextSize(getMeasuredWidth() >> 2);
        mPaint.setColor(Color.RED);
        canvas.drawText(text, getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, mPaint);
    }

    private void drawPlayGround(Canvas canvas) {
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(Color.parseColor("#5090EE90"));
//        canvas.drawRect(bgRect, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getMeasuredHeight() * 0.02f);
        mPaint.setColor(player1.color);
        canvas.drawLine((getMeasuredWidth() >> 1) - (goalWidth / 2), getMeasuredHeight(), (getMeasuredWidth() >> 1) + (goalWidth / 2), getMeasuredHeight(), mPaint);
        mPaint.setColor(player2.color);
        canvas.drawLine((getMeasuredWidth() >> 1) - (goalWidth / 2), 0, (getMeasuredWidth() >> 1) + (goalWidth / 2), 0, mPaint);
        mPaint.setStrokeWidth(5f);
    }

    private void drawPlayer1(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(getMeasuredWidth() >> 4);
        mPaint.setColor(Color.BLACK);
        canvas.drawText(String.valueOf(player1.score), getMeasuredWidth() >> 1, getMeasuredHeight() - playerRadius, mPaint);
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
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(getMeasuredWidth() >> 4);
        mPaint.setColor(Color.BLACK);
        canvas.drawText(String.valueOf(player2.score), getMeasuredWidth() >> 1, playerRadius + mPaint.getTextSize(), mPaint);
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

    private void drawControlPanelIfNeed(Canvas canvas) {
        if (controlX != 0) {
            mPaint.setColor(0xDDCCCCCC);
            canvas.drawCircle(controlX, controlY, controlRadius, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(moveX, moveY, controlRadius * 0.2f, mPaint);
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
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        player1.stopRun();
        player2.stopRun();
    }

    private float controlX, controlY, moveX, moveY;
    private boolean shoot;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGoal) {
            return true;
        }
        float eventY = event.getY();
        float eventX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                controlX = eventX;
                controlY = eventY;
                speed = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                moveDirection = Math.PI / 2 - Math.atan2(moveY - controlY, moveX - controlX);
                float moveDis = dis(moveX, moveY, controlX, controlY);
                if (moveDis > controlRadius - controlRadius * 0.2f) {
                    moveX = (float) (controlX + (controlRadius - controlRadius * 0.2f) * Math.sin(moveDirection));
                    moveY = (float) (controlY + (controlRadius - controlRadius * 0.2f) * Math.cos(moveDirection));
                    moveDis = controlRadius - controlRadius * 0.2f;
                }
                speed = maxSpeed * moveDis / (controlRadius - controlRadius * 0.2f);
                break;
            case MotionEvent.ACTION_UP:
                controlX = 0;
                controlY = 0;
                if (isHost) {
                    if (soccerOwner == 1) {
                        soccer.hit(getMeasuredWidth() * 0.04f, getMeasuredWidth() * 0.0001f, player1.direction);
                        soccerOwner = 0;
                    }
                } else {
                    if (soccerOwner == 2) {
                        shoot = true;
                    }
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

    private void handleAndSendMsg() {
        String msg;
        if (isHost) {
            Player rival = new Player();
            rival.copy(player1);
            rival.transform(getMeasuredWidth(), getMeasuredHeight());
            Ball ball = new Ball();
            ball.copy(soccer);
            ball.transform(getMeasuredWidth(), getMeasuredHeight());
            SocketHostBean bean = new SocketHostBean(rival, ball, soccerOwner, isGoal, countDownTime);
            msg = mGson.toJson(bean);
        } else {
            boolean isShooting = shoot;
            shoot = false;
            Player rival = new Player();
            rival.copy(player1);
            rival.transform(getMeasuredWidth(), getMeasuredHeight());
            SocketClientBean bean = new SocketClientBean(rival, isShooting);
            msg = mGson.toJson(bean);
        }
        if (listener != null) {
            listener.onSend(msg);
        }
    }

    public void receiveMsg(String msg) {
        if (isHost) {
            SocketClientBean bean = mGson.fromJson(msg, new TypeToken<SocketClientBean>() {
            }.getType());
            player2.copyResize(bean.getRival(), getMeasuredWidth(), getMeasuredHeight());
            if (soccerOwner == 2) {
                float sx = (float) (player2.x + Math.sin(player2.direction) * (ballRadius + playerRadius));
                float sy = (float) (player2.y + Math.cos(player2.direction) * (ballRadius + playerRadius));
                soccer.setPos(sx, sy);
            }
            if (bean.isShooting()) {
                soccer.hit(getMeasuredWidth() * 0.04f, getMeasuredWidth() * 0.0001f, player2.direction);
                soccerOwner = 0;
            }
        } else {
            SocketHostBean bean = mGson.fromJson(msg, new TypeToken<SocketHostBean>() {
            }.getType());
            player2.copyResize(bean.getRival(), getMeasuredWidth(), getMeasuredHeight());
            soccer.copyResize(bean.getBall(), getMeasuredWidth(), getMeasuredHeight());
            soccerOwner = bean.getSoccerOwner();
            if (isGoal) {
                if (!bean.isGoal()) {
                    player1.reset(getMeasuredWidth() >> 1, getMeasuredHeight() * 0.7f);
                    player1.running();
                }
            } else {
                if (bean.isGoal()) {
                    if (soccer.y < getMeasuredHeight() >> 1) {
                        player1.goal();
                    }
                    if (listener != null) {
                        listener.onGoal();
                    }
                    player1.stopRun();
                    controlX = controlY = 0;
                }
            }
            isGoal = bean.isGoal();
            countDownTime = bean.getCountDownTime();
        }
    }

    private void changeSoccerOwner(int owner) {
        if (canChangeBall()) {
            soccerOwner = owner;
            coolDown = System.currentTimeMillis();
        }
    }

    private boolean canChangeBall() {
        return System.currentTimeMillis() - coolDown >= 500;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
        if (connect) {
            isPractice = false;
        }
    }

    public void setListener(OnMsgSendListener listener) {
        this.listener = listener;
    }

    public void practice() {
        isPractice = true;
        player2.setPos(getMeasuredWidth() / 2f - goalWidth / 2, playerRadius * 3);
        player2.running();
        xIncrement = goalWidth * 0.03f;
    }

    public interface OnMsgSendListener {
        void onSend(String msg);

        void onGoal();
    }
}
