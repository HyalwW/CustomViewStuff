package com.example.customviewstuff.customs.fiveInOne;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.customviewstuff.ThreadPool;
import com.example.customviewstuff.customs.BaseSurfaceView;
import com.example.customviewstuff.socket.IMessageSender;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private Piece last;
    private RectF boardRect, resetRect;
    private PathEffect dash;
    private int[] near;
    private String showText;

    private boolean inTouch, canDrop, isConnect, isHost, win;
    //0:没有输赢 1：黑棋 2：白棋
    private int winner, nowPlayer, me;
    private String ipName;
    private IMessageSender sender;
    private Gson gson;
    private TransformBean bean;

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
        resetRect = new RectF();
        pieces = new CopyOnWriteArrayList<>();
        checkList = new LinkedList<>();
        dash = new DashPathEffect(new float[]{10, 10}, 5);
        near = new int[2];
        showText = "";
        gson = new Gson();
        bean = new TransformBean();
    }

    @Override
    protected void onReady() {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        gapWidth = (float) w / colSum;
        radius = gapWidth * 0.4f;
        boardBottom = gapWidth * rowSum;
        boardRect.set(0, 0, w, boardBottom);
        resetRect.set(w * 0.05f, boardBottom + h * 0.01f, w * 0.25f, boardBottom + h * 0.01f + w * 0.1f);
        mPaint.setTextAlign(Paint.Align.CENTER);
        startAnim();
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
            if (!isConnect) {
                drawIp(canvas);
            }
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
            mPaint.setColor(piece.isWin ? Color.GREEN : (piece.owner == 1 ? Color.BLACK : Color.WHITE));
            float cx = piece.col * gapWidth;
            float cy = piece.row * gapWidth;
            canvas.drawCircle(cx, cy, radius, mPaint);
            if (piece.equals(last)) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.BLUE);
                canvas.drawRect(cx - radius, cy - radius, cx + radius, cy + radius, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
            }
        }
    }

    private void drawShadow(Canvas canvas) {
        if (inTouch) {
            mPaint.setColor(canDrop ? (nowPlayer == 1 ? 0x88000000 : 0x88FFFFFF) : 0xBBFF0000);
            canvas.drawCircle(near[0] * gapWidth, near[1] * gapWidth, radius, mPaint);
        }
    }

    private void drawText(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(getMeasuredWidth() * 0.07f);
        canvas.drawText(showText, getMeasuredWidth() >> 1, (getMeasuredHeight() + boardBottom + mPaint.getTextSize()) / 2, mPaint);
    }

    private void drawReset(Canvas canvas) {
        mPaint.setColor(isReset ? 0xFF00FFFF : 0x5500FFFF);
        canvas.drawRect(resetRect, mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize((resetRect.right - resetRect.left) / 3.2f);
        canvas.drawText("下一局", (resetRect.left + resetRect.right) / 2, (resetRect.top + resetRect.bottom + mPaint.getTextSize() * 0.8f) / 2, mPaint);
    }

    private void drawIp(Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(getMeasuredWidth() * 0.05f);
        canvas.drawText(ipName, getMeasuredWidth() >> 1, getMeasuredHeight(), mPaint);
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
                if (resetRect.contains(eventX, eventY) && isHost) {
                    isReset = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (inTouch = eventY < boardBottom) {
                    calNearPos(eventX, eventY);
                }
                if (!resetRect.contains(eventX, eventY)) {
                    isReset = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (inTouch) {
                    inTouch = false;
                    if (canDrop) {
                        Piece piece = new Piece(near[0], near[1], nowPlayer);
                        if (isHost) {
                            addAndCheckWin(piece);
                        } else {
                            send(createClientBean(piece));
                        }
                    }
                }
                if (isReset) {
                    winner = -1;
                    isReset = false;
                    win = false;
                    pieces.clear();
                    send(createHostBean());
                }
                break;
        }
        return true;
    }

    private String createClientBean(Piece piece) {
        bean.setCheckList(null);
        bean.setPieces(null);
        bean.setFromHost(isHost);
        bean.setPiece(piece);
        bean.setPlayer(me);
        bean.setWin(false);
        return gson.toJson(bean);
    }

    private void addAndCheckWin(Piece piece) {
        pieces.add(piece);
        last = piece;
        win = checkWin(piece);
        if (win) {
            for (Piece p : checkList) {
                p.win();
            }
            winner = nowPlayer;
            showText = winner == me ? "你赢啦~" : "你输啦！";
            nowPlayer = nowPlayer == 1 ? 2 : 1;
        } else {
            nowPlayer = nowPlayer == 1 ? 2 : 1;
            showText = "轮到" + (nowPlayer == me ? "你" : "对面") + "下了" + (nowPlayer == 1 ? "(黑棋)" : "(白棋)");
        }
        send(createHostBean());
    }

    private String createHostBean() {
        bean.setPieces(pieces);
        bean.setCheckList(checkList);
        bean.setLast(last);
        bean.setWin(win);
        bean.setWinner(winner);
        bean.setPlayer(nowPlayer);
        bean.setFromHost(isHost);
        return gson.toJson(bean);
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
        if (win || me != nowPlayer || !isConnect) {
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
        if (isHost) {
            nowPlayer = Math.random() > 0.5 ? 1 : 2;
        }
        showText = isHost ? "等待玩家加入" : "正在加入" + ipName;
        callDraw("");
    }

    public void connect(boolean connect) {
        isConnect = connect;
        if (connect) {
            if (isHost) {
                showText = "轮到" + (nowPlayer == me ? "你" : "对面") + "下了" + (nowPlayer == 1 ? "(黑棋)" : "(白棋)");
                ThreadPool.cache().execute(() -> {
                    sleep(100);
                    send(createHostBean());
                });
            }
        } else {
            showText = isHost ? "等待玩家加入" : "正在加入" + ipName;
        }
    }

    public void receive(String msg) {
        TransformBean tb = gson.fromJson(msg, new TypeToken<TransformBean>() {
        }.getType());
        if (tb.isFromHost()) {
            nowPlayer = tb.getPlayer();
            pieces.clear();
            pieces.addAll(tb.getPieces());
            last = tb.getLast();
            win = tb.isWin();
            winner = tb.getWinner();
            if (win) {
                showText = winner == me ? "你赢啦~" : "你输啦！";
            } else {
                showText = "轮到" + (nowPlayer == me ? "你" : "对面") + "下了" + (nowPlayer == 1 ? "(黑棋)" : "(白棋)");
            }
        } else {
            addAndCheckWin(tb.getPiece());
        }
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

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof Piece)) {
                return false;
            }
            Piece other = (Piece) obj;
            return row == other.row && col == other.col && owner == other.owner;
        }
    }

    public void send(String msg) {
        if (sender != null) {
            sender.send(msg);
        }
    }

    public void setSender(IMessageSender sender) {
        this.sender = sender;
    }
}
