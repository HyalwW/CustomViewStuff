package com.example.customviewstuff.customs.soccerGame;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/9
 */
public class SocketHostBean {
    private Player rival;
    private Ball ball;
    //0 自由 1 主机 2 陪玩
    private int soccerOwner;
    private boolean isGoal;
    private int countDownTime;

    public SocketHostBean(Player rival, Ball ball, int soccerOwner, boolean isGoal, int countDownTime) {
        this.rival = rival;
        this.ball = ball;
        this.soccerOwner = soccerOwner;
        this.isGoal = isGoal;
        this.countDownTime = countDownTime;
    }

    public Player getRival() {
        return rival;
    }

    public void setRival(Player rival) {
        this.rival = rival;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public int getSoccerOwner() {
        return soccerOwner;
    }

    public void setSoccerOwner(int soccerOwner) {
        this.soccerOwner = soccerOwner;
    }

    public boolean isGoal() {
        return isGoal;
    }

    public void setGoal(boolean goal) {
        isGoal = goal;
    }

    public int getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(int countDownTime) {
        this.countDownTime = countDownTime;
    }
}
