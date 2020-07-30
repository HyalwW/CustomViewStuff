package com.example.customviewstuff.customs.fiveInOne;

import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/7/30
 * Description: blablabla
 */
public class TransformBean {
    private int player;
    private boolean isWin;
    private List<MultiChessView.Piece> pieces;
    private List<MultiChessView.Piece> checkList;

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
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
