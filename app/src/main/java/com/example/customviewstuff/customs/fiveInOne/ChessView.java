package com.example.customviewstuff.customs.fiveInOne;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.customviewstuff.customs.BaseSurfaceView;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wang.Wenhui
 * Date: 2020/7/29
 * Description: blablabla
 */
public class ChessView extends BaseSurfaceView {
    private int rowSum = 20, colSum = 15;
    private float gapWidth, radius, boardBottom;
    private List<Piece> pieces, checkList;
    private RectF boardRect, reserRect;
    private PathEffect dash;
    private float[] near;
    private boolean inTouch, blackTime, canDrop, win;
    private String text;

    public ChessView(Context context) {
        super(context);
    }

    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        boardRect = new RectF();
        reserRect = new RectF();
        pieces = new CopyOnWriteArrayList<>();
        checkList = new LinkedList<>();
        dash = new DashPathEffect(new float[]{10, 10}, 5);
        near = new float[2];
        blackTime = true;
        text = "黑棋";
    }

    @Override
    protected void onReady() {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        gapWidth = (float) w / colSum;
        radius = gapWidth * 0.4f;
        boardBottom = gapWidth * rowSum;
        boardRect.set(0, 0, w, boardBottom);
        reserRect.set(w * 0.05f, boardBottom + h * 0.01f, w * 0.25f, boardBottom + h * 0.01f + w * 0.1f);
        startAnim();
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDataUpdate() {

    }

    @Override
    protected void onRefresh(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawBg(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        drawShadow(canvas);
        drawText(canvas);
        drawReset(canvas);
    }

    private void drawBg(Canvas canvas) {
        mPaint.setColor(0xFFF5DEB3);
        canvas.drawRect(boardRect, mPaint);
    }

    private void drawBoard(Canvas canvas) {
        int x = 0;
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < colSum + 1; i++) {
            if (i == 0 || i == colSum) {
                mPaint.setPathEffect(null);
            } else {
                mPaint.setPathEffect(dash);
            }
            canvas.drawLine(x, 0, x, boardBottom, mPaint);
            x += gapWidth;
        }
        int y = 0;
        for (int i = 0; i < rowSum + 1; i++) {
            if (i == 0 || i == rowSum) {
                mPaint.setPathEffect(null);
            } else {
                mPaint.setPathEffect(dash);
            }
            canvas.drawLine(0, y, getMeasuredWidth(), y, mPaint);
            y += gapWidth;
        }
    }

    private void drawPieces(Canvas canvas) {
        for (Piece piece : pieces) {
            piece.draw(canvas);
        }
    }

    private void drawShadow(Canvas canvas) {
        if (inTouch) {
            mPaint.setColor(canDrop ? (blackTime ? 0x88000000 : 0x88FFFFFF) : 0xBBFF0000);
            canvas.drawCircle(near[0], near[1], radius, mPaint);
        }
    }

    private void drawText(Canvas canvas) {
        mPaint.setColor(win ? Color.RED : Color.BLACK);
        mPaint.setTextSize(getMeasuredWidth() * 0.1f);
        canvas.drawText(text, getMeasuredWidth() >> 1, (getMeasuredHeight() + boardBottom + mPaint.getTextSize()) / 2, mPaint);
    }

    private void drawReset(Canvas canvas) {
        mPaint.setColor(isReset ? 0xFF00FFFF : 0x5500FFFF);
        canvas.drawRect(reserRect, mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize((reserRect.right - reserRect.left) / 3.2f);
        canvas.drawText("重来", (reserRect.left + reserRect.right) / 2, (reserRect.top + reserRect.bottom + mPaint.getTextSize() * 0.8f) / 2, mPaint);
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

    private boolean isReset;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (inTouch = eventY < boardBottom) {
                    calNearPos(eventX, eventY);
                }
                if (reserRect.contains(eventX, eventY)) {
                    isReset = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (inTouch = eventY < boardBottom) {
                    calNearPos(eventX, eventY);
                }
                if (!reserRect.contains(eventX, eventY)) {
                    isReset = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (inTouch) {
                    inTouch = false;
                    if (canDrop) {
                        Piece piece = new Piece(near[0], near[1], blackTime);
                        pieces.add(piece);
                        win = checkWin(piece);

                        if (win) {
                            text = (blackTime ? "黑棋" : "白棋") + "胜利！";
                            for (Piece p : checkList) {
                                p.win();
                            }
                            blackTime = !blackTime;
                        } else {
                            blackTime = !blackTime;
                            text = blackTime ? "黑棋" : "白棋";
                        }
                    }
                }
                if (isReset) {
                    isReset = false;
                    win = false;
                    pieces.clear();
                    text = blackTime ? "黑棋" : "白棋";
                }
                break;
        }
        return true;
    }

    private boolean checkWin(Piece piece) {
        return ctb(piece) || clr(piece) || cltrb(piece) || crtlb(piece);
    }

    //检查竖直方向
    private Piece exist(float x, float y, boolean isBlack) {
        for (Piece piece : pieces) {
            if (piece.x == x && piece.y == y && piece.isBlack == isBlack)
                return piece;
        }
        return null;
    }

    private boolean ctb(Piece piece) {
        checkList.clear();
        checkList.add(piece);
        float y = piece.y;
        while (y > 0) {
            y -= gapWidth;
            Piece exist = exist(piece.x, y, piece.isBlack);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        y = piece.y;
        while (y < boardBottom) {
            y += gapWidth;
            Piece exist = exist(piece.x, y, piece.isBlack);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        return checkList.size() >= 5;
    }

    //检查水平方向
    private boolean clr(Piece piece) {
        float x = piece.x;
        checkList.clear();
        checkList.add(piece);
        while (x > 0) {
            x -= gapWidth;
            Piece exist = exist(x, piece.y, piece.isBlack);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        x = piece.x;
        while (x < getMeasuredWidth()) {
            x += gapWidth;
            Piece exist = exist(x, piece.y, piece.isBlack);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        return checkList.size() >= 5;
    }

    //左上-右下
    private boolean cltrb(Piece piece) {
        float x = piece.x;
        float y = piece.y;
        checkList.clear();
        checkList.add(piece);
        while (x > 0 && y > 0) {
            x -= gapWidth;
            y -= gapWidth;
            Piece exist = exist(x, y, piece.isBlack);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        x = piece.x;
        y = piece.y;
        while (x < getMeasuredWidth() && y < boardBottom) {
            x += gapWidth;
            y += gapWidth;
            Piece exist = exist(x, y, piece.isBlack);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        return checkList.size() >= 5;
    }

    //右上-左下
    private boolean crtlb(Piece piece) {
        float x = piece.x;
        float y = piece.y;
        checkList.clear();
        checkList.add(piece);
        while (x < getMeasuredWidth() && y > 0) {
            x += gapWidth;
            y -= gapWidth;
            Piece exist = exist(x, y, piece.isBlack);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        x = piece.x;
        y = piece.y;
        while (x > 0 && y < boardBottom) {
            x -= gapWidth;
            y += gapWidth;
            Piece exist = exist(x, y, piece.isBlack);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        return checkList.size() >= 5;
    }

    private void calNearPos(float eventX, float eventY) {
        int bi = colSum / 2;
        float bd = 99999;
        if (eventX > bi * gapWidth) {
            for (int i = bi; i < colSum; i++) {
                float abs = Math.abs(eventX - i * gapWidth);
                if (bd >= abs) {
                    bd = abs;
                    near[0] = i * gapWidth;
                } else {
                    break;
                }
            }
        } else {
            for (int i = bi; i > 0; i--) {
                float abs = Math.abs(eventX - i * gapWidth);
                if (bd >= abs) {
                    bd = abs;
                    near[0] = i * gapWidth;
                } else {
                    break;
                }
            }
        }
        bi = rowSum / 2;
        bd = 99999;
        if (eventY > bi * gapWidth) {
            for (int i = bi; i < rowSum; i++) {
                float abs = Math.abs(eventY - i * gapWidth);
                if (bd > abs) {
                    bd = abs;
                    near[1] = i * gapWidth;
                } else {
                    break;
                }
            }
        } else {
            for (int i = bi; i > 0; i--) {
                float abs = Math.abs(eventY - i * gapWidth);
                if (bd > abs) {
                    bd = abs;
                    near[1] = i * gapWidth;
                } else {
                    break;
                }
            }
        }
        if (win) {
            canDrop = false;
        } else {
            canDrop = true;
            for (Piece piece : pieces) {
                if (piece.x == near[0] && piece.y == near[1]) {
                    canDrop = false;
                }
            }
        }
    }

    private class Piece {
        float x, y;
        boolean isBlack;
        boolean isWin;

        public Piece(float x, float y, boolean isBlack) {
            this.x = x;
            this.y = y;
            this.isBlack = isBlack;
        }

        void win() {
            isWin = true;
        }

        void draw(Canvas canvas) {
            mPaint.setColor(isWin ? Color.GREEN : (isBlack ? Color.BLACK : Color.WHITE));
            canvas.drawCircle(x, y, radius, mPaint);
        }
    }
}
