package com.example.customviewstuff.socket;

import java.net.Socket;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/8
 */
public interface SocketListener {
    void onStartConnect();

    void onConnectSuccess(boolean isHost, String address);

    void onConnectFailed(String reason);

    void onDisconnect(String address);

    void onReceiveMsg(String address, String msg);

    void onError(Throwable e);
}
