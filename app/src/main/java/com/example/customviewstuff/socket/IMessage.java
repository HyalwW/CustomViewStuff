package com.example.customviewstuff.socket;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/8
 */
interface IMessage {
    void send(String msg);

    String read();

    void close();
}
