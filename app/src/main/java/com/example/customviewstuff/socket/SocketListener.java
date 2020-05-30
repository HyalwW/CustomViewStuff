package com.example.customviewstuff.socket;

import java.net.Socket;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/8
 */
public interface SocketListener {
    void onStartConnect();

    void onConnectSuccess(boolean isHost, Socket socket);

    void onConnectFailed(String reason);

    void onDisconnect();

    void onReceiveMsg(String msg);

    void onError(Throwable e);
}
