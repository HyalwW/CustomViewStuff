package com.example.customviewstuff.customs.fiveInOne;

import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/7/30
 * Description: blablabla
 */
public class TransformBean {
    private int player;
    private int winner;
    private boolean isWin;
    private boolean fromHost;
    private MultiChessView.Piece piece;
    private MultiChessView.Piece last;
    private List<MultiChessView.Piece> pieces;
    private List<MultiChessView.Piece> checkList;

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public boolean isFromHost() {
        return fromHost;
    }

    public void setFromHost(boolean fromHost) {
        this.fromHost = fromHost;
    }

    public MultiChessView.Piece getPiece() {
        return piece;
    }

    public void setPiece(MultiChessView.Piece piece) {
        this.piece = piece;
    }

    public MultiChessView.Piece getLast() {
        return last;
    }

    public void setLast(MultiChessView.Piece last) {
        this.last = last;
    }

    public List<MultiChessView.Piece> getPieces() {
        return pieces;
    }

    public void setPieces(List<MultiChessView.Piece> pieces) {
        this.pieces = pieces;
    }

    public List<MultiChessView.Piece> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<MultiChessView.Piece> checkList) {
        this.checkList = checkList;
    }
}
