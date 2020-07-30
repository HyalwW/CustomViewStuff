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
 * Date: 2020/7/30
 * Description: blablabla
 */
public class MultiChessView extends BaseSurfaceView {
    private int rowSum = 20, colSum = 15;
    private float gapWidth, radius, boardBottom;
    private List<Piece> pieces, checkList;
    private RectF boardRect, reserRect;
    private PathEffect dash;
    private int[] near;
    private String showText;

    private boolean inTouch, canDrop, isConnect, isHost, win;
    //0:没有输赢 1：黑棋 2：白棋
    private int winner, nowPlayer, me;
    private String ipName;

    public MultiChessView(Context context) {
        super(context);
    }

    public MultiChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiChessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        boardRect = new RectF();
        reserRect = new RectF();
        pieces = new CopyOnWriteArrayList<>();
        checkList = new LinkedList<>();
        dash = new DashPathEffect(new float[]{10, 10}, 5);
        near = new int[2];
        showText = "";
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
        if (isHost) {
            drawReset(canvas);
        }
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
            mPaint.setColor(canDrop ? (nowPlayer == 1 ? 0x88000000 : 0x88FFFFFF) : 0xBBFF0000);
            canvas.drawCircle(near[0], near[1], radius, mPaint);
        }
    }

    private void drawText(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(getMeasuredWidth() * 0.1f);
        canvas.drawText(showText, getMeasuredWidth() >> 1, (getMeasuredHeight() + boardBottom + mPaint.getTextSize()) / 2, mPaint);
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
        if (!isConnect) {
            return false;
        }
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (inTouch = eventY < boardBottom) {
                    calNearPos(eventX, eventY);
                }
                if (reserRect.contains(eventX, eventY) && isHost) {
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
                        if (isHost) {
                            addAndCheckWin(new Piece(near[0], near[1], nowPlayer));
                        } else {

                        }
                    }
                }
                if (isReset) {
                    winner = -1;
                    isReset = false;
                    win = false;
                    pieces.clear();
                }
                break;
        }
        return true;
    }

    private void addAndCheckWin(Piece piece) {
        pieces.add(piece);
        win = checkWin(piece);
        if (win) {
            for (Piece p : checkList) {
                p.win();
            }
            winner = nowPlayer;
            nowPlayer = nowPlayer == 1 ? 2 : 1;
        } else {
            nowPlayer = nowPlayer == 1 ? 2 : 1;
        }
    }

    private boolean checkWin(Piece piece) {
        return ctb(piece) || clr(piece) || cltrb(piece) || crtlb(piece);
    }

    //检查竖直方向
    private Piece exist(int col, int row, int owner) {
        for (Piece piece : pieces) {
            if (piece.col == col && piece.row == row && piece.owner == owner)
                return piece;
        }
        return null;
    }

    private boolean ctb(Piece piece) {
        checkList.clear();
        checkList.add(piece);
        int row = piece.row;
        while (row > 0) {
            row--;
            Piece exist = exist(piece.col, row, piece.owner);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        row = piece.row;
        while (row < rowSum) {
            row++;
            Piece exist = exist(piece.col, row, piece.owner);
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
        int col = piece.col;
        checkList.clear();
        checkList.add(piece);
        while (col > 0) {
            col--;
            Piece exist = exist(col, piece.row, piece.owner);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        col = piece.col;
        while (col < colSum) {
            col++;
            Piece exist = exist(col, piece.row, piece.owner);
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
        int col = piece.col;
        int row = piece.row;
        checkList.clear();
        checkList.add(piece);
        while (col > 0 && row > 0) {
            col--;
            row--;
            Piece exist = exist(col, row, piece.owner);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        col = piece.col;
        row = piece.row;
        while (col < colSum && row < rowSum) {
            col++;
            row++;
            Piece exist = exist(col, row, piece.owner);
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
        int col = piece.col;
        int row = piece.row;
        checkList.clear();
        checkList.add(piece);
        while (col < colSum && row > 0) {
            col++;
            row--;
            Piece exist = exist(col, row, piece.owner);
            if (exist != null) {
                checkList.add(exist);
            } else {
                break;
            }
        }
        col = piece.col;
        row = piece.row;
        while (col > 0 && row < boardBottom) {
            col--;
            row++;
            Piece exist = exist(col, row, piece.owner);
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
                    near[0] = i;
                } else {
                    break;
                }
            }
        } else {
            for (int i = bi; i > 0; i--) {
                float abs = Math.abs(eventX - i * gapWidth);
                if (bd >= abs) {
                    bd = abs;
                    near[0] = i;
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
                    near[1] = i;
                } else {
                    break;
                }
            }
        } else {
            for (int i = bi; i > 0; i--) {
                float abs = Math.abs(eventY - i * gapWidth);
                if (bd > abs) {
                    bd = abs;
                    near[1] = i;
                } else {
                    break;
                }
            }
        }
        if (win || me != nowPlayer) {
            canDrop = false;
        } else {
            canDrop = true;
            for (Piece piece : pieces) {
                if (piece.col == near[0] && piece.row == near[1]) {
                    canDrop = false;
                }
            }
        }
    }

    public void setIsHost(boolean isHost, String ipName) {
        this.isHost = isHost;
        this.ipName = ipName;
        me = isHost ? 1 : 2;
        nowPlayer = Math.random() > 0.5 ? 1 : 2;
        startAnim();
    }

    public class Piece {
        int row, col, owner;
        boolean isWin;

        Piece(int col, int row, int owner) {
            this.col = col;
            this.row = row;
            this.owner = owner;
        }

        void win() {
            isWin = true;
        }

        void draw(Canvas canvas) {
            mPaint.setColor(isWin ? Color.GREEN : (owner == 1 ? Color.BLACK : Color.WHITE));
            canvas.drawCircle(col * gapWidth, row * gapWidth, radius, mPaint);
        }
    }
}
