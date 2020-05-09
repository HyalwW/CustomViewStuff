package com.example.customviewstuff.customs.soccerGame;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/9
 */
public class SocketClientBean extends SocketBean {
    private Player rival;
    private boolean shooting;

    public SocketClientBean(Player rival) {
        this(rival, false);
    }

    public SocketClientBean(Player rival, boolean shooting) {
        this.rival = rival;
        this.shooting = shooting;
    }

    public Player getRival() {
        return rival;
    }

    public void setRival(Player rival) {
        this.rival = rival;
    }

    public boolean isShooting() {
        return shooting;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }
}
