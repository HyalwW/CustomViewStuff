package com.example.customviewstuff.customs.soccerGame;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/8
 */
public interface SocketListener {
    void onReceiveMsg(String msg);

    void onClose();
}
